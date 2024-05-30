package com.ahuja.sons.ahujaSonsClasses.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.Interface.OrderStageItemClick
import com.ahuja.sons.ahujaSonsClasses.adapter.AddSurgeryPersonAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.OrderStageLineAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.UploadImageListAdapter
import com.ahuja.sons.ahujaSonsClasses.fragments.order.DeliveryItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.PendingItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.SurgeryPersonDetailsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.UploadProofImagesFragment
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonModelData
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityParticularOrderDetailBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.OrderOneResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class ParticularOrderDetailActivity : AppCompatActivity(), OrderStageItemClick {

    lateinit var binding : ActivityParticularOrderDetailBinding
    lateinit var viewModel: MainViewModel
    var id = ""

    private val PICK_IMAGES_REQUEST_CODE = 1
    private  val REQUEST_CODE_PERMISSIONS = 10
    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    val mArrayUriList = ArrayList<Uri>()
    lateinit var fileUri: Uri

    var content = ""

    var addSurgeryPersonAdapter: AddSurgeryPersonAdapter? = null

    interface UploadItemClickListener {
        fun onUploadItemClick(mArrayUriList: ArrayList<Uri>)
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    lateinit var pagerAdapter : ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticularOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        id = intent.getStringExtra("id").toString()
        Log.e(TAG, "onCreate: $id")

        val toolbar: Toolbar = binding.toolbar

        pagerAdapter = ViewPagerAdapter(supportFragmentManager)

        //todo inspection tabs
        pagerAdapter.add(DeliveryItemsFragment(), "Delivery Items")
        pagerAdapter.add(PendingItemsFragment(), "Pending Items")
        pagerAdapter.add(UploadProofImagesFragment(), "Proof")

        binding.viewpagerInspect.adapter = pagerAdapter

        binding.tabLayout.setupWithViewPager(binding.viewpagerInspect)


        setSupportActionBar(toolbar)

        if (Global.checkForInternet(this@ParticularOrderDetailActivity)) {
            binding.loadingBackFrame.visibility = View.VISIBLE
            binding.loadingView.start()
            var jsonObject = JsonObject()
            jsonObject.addProperty("id", id)
            viewModel.getOrderOneDetail(jsonObject)
            bindOneObserver()
        }


        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //todo stage order lines
        var dataList = arrayListOf<String>("Sales Order Request", "Order","Counter" , "Inspect Deliver", "Delivery Coordinator", "Delivery",
        "Surgery Coordinator", "Surgery Person")

        val recyclerView: RecyclerView = findViewById(R.id.rvOrderStageStatus)
        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager
        val adapter = OrderStageLineAdapter(dataList, this)
        recyclerView.adapter = adapter


        /*var statusList = arrayListOf<String>("Inspected", "Part Inspected", "Inspected – Missing items")

        var statusAdapter = StageInspectionStatusAutoAdapter(this, R.layout.drop_down_item_textview, statusList)
        binding.acStatus.setAdapter(statusAdapter)


        //todo branch item click--
        binding.acStatus.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (statusList.size > 0){
                    binding.acStatus.setText(statusList[pos])
                }else{
                    binding.acStatus.setText("")
                }
            }

        }*/


        //todo order drop down's---
      /*  val order = arrayOf("Order 1", "Order 2", "Order 3")
        val orderInfoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, order)
        binding.acOrderInfo.setAdapter(orderInfoAdapter)*/


        initView()


        //todo start trip--

        binding.startTripChip.setOnClickListener {
            binding.tvClickStartText.visibility = View.GONE
            binding.tripStartDetailCardView.visibility = View.VISIBLE
            binding.uploadProofLayout.visibility = View.VISIBLE
            binding.startTripChipGroup.visibility = View.GONE
            binding.endTripChipGroup.visibility = View.VISIBLE
            binding.tvCountText.visibility = View.VISIBLE
        }


        binding.endTripChip.setOnClickListener {
            binding.tvClickStartText.visibility = View.GONE
            binding.tripStartDetailCardView.visibility = View.VISIBLE
            binding.clickNewImageLayout.visibility = View.GONE
            binding.uploadProofLayout.visibility = View.VISIBLE
            binding.recyclerViewMoreImageLayout.visibility = View.VISIBLE
            binding.tvAddMoreImages.visibility = View.GONE
            binding.tripEndDetailCardView.visibility = View.VISIBLE
            binding.endTripChipGroup.visibility = View.GONE
        }


        binding.ivAddNewImage.setOnClickListener {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            }else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }
        }


        binding.tvAddMoreImages.setOnClickListener {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            }else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }
        }


        //todo data about surgery persons stages--

        val order = arrayOf("Surgery Person 1", "Surgery Person 2", "Surgery Person 3")
        val orderInfoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, order)
        binding.acSurgeryPerson1.setAdapter(orderInfoAdapter)

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, order)
        binding.acSurgeryPerson2.setAdapter(adapter2)


        addSurgeryPersonAdapter = AddSurgeryPersonAdapter(this@ParticularOrderDetailActivity, mutableListOf())
        bindFocItemAdapter()

        binding.addSurgeryPerson.setOnClickListener {
            val newItem = SurgeryPersonModelData(
                str = "FOC"
            )
            addSurgeryPersonAdapter!!.addItem(newItem)
            content = ""
        }


        binding.submitSurgeryChip.setOnClickListener {
            binding.chipCardViewBtton.visibility = View.GONE
            binding.surgeryDetailLinearLayout.visibility = View.VISIBLE
            binding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.GONE

        }



    }


    //todo bind foc items parts adapter--
    private fun bindFocItemAdapter() = binding.rvSurgeryPersons.apply {
        adapter = addSurgeryPersonAdapter
        layoutManager = LinearLayoutManager(this@ParticularOrderDetailActivity)

        //todo remove foc items--
        if (addSurgeryPersonAdapter != null){
            addSurgeryPersonAdapter!!.setOnItemMinusClickListener { s, i ->
                if (addSurgeryPersonAdapter!!.itemCount > 0) {
                    addSurgeryPersonAdapter!!.removeItem(i)
                }
            }
        }

    }


    //todo all drop down and Up listeners--
    private fun initView() {

        //todo header arrow-
        binding.headerUpArrow.setOnClickListener {
            binding.tvOrderID.visibility = View.GONE
            binding.tvExpectedData.visibility = View.GONE
            binding.headerUpArrow.visibility = View.GONE
            binding.headerDownArrow.visibility = View.VISIBLE
        }

        binding.headerDownArrow.setOnClickListener {
            binding.tvOrderID.visibility = View.VISIBLE
            binding.tvExpectedData.visibility = View.VISIBLE
            binding.headerDownArrow.visibility = View.GONE
            binding.headerUpArrow.visibility = View.VISIBLE
        }

        //todo order arrow-

        binding.orderUpArrow.setOnClickListener {
            binding.orderDetailsLayout.visibility = View.GONE
            binding.orderUpArrow.visibility = View.GONE
            binding.orderDownArrow.visibility = View.VISIBLE
        }

        binding.orderDownArrow.setOnClickListener {
            binding.orderDetailsLayout.visibility = View.VISIBLE
            binding.orderDownArrow.visibility = View.GONE
            binding.orderUpArrow.visibility = View.VISIBLE
        }

        //todo dependency arrow-

        binding.dependencyUpArrow.setOnClickListener {
            binding.dependencyLayout.visibility = View.GONE
            binding.dependency1Layout.visibility = View.GONE
            binding.view1.visibility = View.GONE
            binding.dependencyUpArrow.visibility = View.GONE
            binding.dependencyDownArrow.visibility = View.VISIBLE
        }

        binding.dependencyDownArrow.setOnClickListener {
            binding.dependencyLayout.visibility = View.VISIBLE
            binding.dependency1Layout.visibility = View.VISIBLE
            binding.view1.visibility = View.VISIBLE
            binding.dependencyDownArrow.visibility = View.GONE
            binding.dependencyUpArrow.visibility = View.VISIBLE
        }

        //todo errands arrow-

        binding.errandsUpArrow.setOnClickListener {
            binding.errandsLayout.visibility = View.GONE
            binding.errandsUpArrow.visibility = View.GONE
            binding.errandsDownArrow.visibility = View.VISIBLE
        }

        binding.errandsDownArrow.setOnClickListener {
            binding.errandsLayout.visibility = View.VISIBLE
            binding.errandsDownArrow.visibility = View.GONE
            binding.errandsUpArrow.visibility = View.VISIBLE
        }

        //todo startTrip arrow-

        binding.tripStartUpArrow.setOnClickListener {
            binding.startTripLayout.visibility = View.GONE
            binding.tripStartUpArrow.visibility = View.GONE
            binding.tripStartDownArrow.visibility = View.VISIBLE
        }

        binding.tripStartDownArrow.setOnClickListener {
            binding.startTripLayout.visibility = View.VISIBLE
            binding.tripStartDownArrow.visibility = View.GONE
            binding.tripStartUpArrow.visibility = View.VISIBLE
        }

        //todo end trip arrow-

        binding.tripEndUpArrow.setOnClickListener {
            binding.endTripLayout.visibility = View.GONE
            binding.tripEndUpArrow.visibility = View.GONE
            binding.tripEndDownArrow.visibility = View.VISIBLE
        }

        binding.tripEndDownArrow.setOnClickListener {
            binding.endTripLayout.visibility = View.VISIBLE
            binding.tripEndDownArrow.visibility = View.GONE
            binding.tripEndUpArrow.visibility = View.VISIBLE
        }


        //todo surgery Person detail drop downand up arrow--

        binding.surgeryPUpArrow.setOnClickListener {
            binding.surgeryPDetailsLayout.visibility = View.GONE
            binding.surgeryPUpArrow.visibility = View.GONE
            binding.surgeryPDownArrow.visibility = View.VISIBLE
        }

        binding.surgeryPDownArrow.setOnClickListener {
            binding.surgeryPDetailsLayout.visibility = View.VISIBLE
            binding.surgeryPDownArrow.visibility = View.GONE
            binding.surgeryPUpArrow.visibility = View.VISIBLE
        }


    }


    override fun stagesOnClick(id: Int, obj: String) {

        if (obj.equals("Sales Order Request") || obj.equals("Order")){
            binding.orderDetailLayoutCardView.visibility = View.VISIBLE
            binding.saleOrderLayouts.visibility = View.VISIBLE
            binding.dependencyCardViewLayout.visibility = View.GONE
            binding.errandsCardViewLayout.visibility = View.GONE
            binding.chipCardViewBtton.visibility = View.GONE
            binding.inspectTabLayouts.visibility = View.GONE
            binding.deliveryLayout.visibility = View.GONE
            binding.srugeryPersonLinearLayout.visibility = View.GONE
        }

        else if (obj.equals("Counter")){
            binding.orderDetailLayoutCardView.visibility = View.GONE
            binding.dependencyCardViewLayout.visibility = View.VISIBLE
            binding.errandsCardViewLayout.visibility = View.VISIBLE
            binding.chipCardViewBtton.visibility = View.VISIBLE
            binding.saleOrderLayouts.visibility = View.VISIBLE
            binding.inspectTabLayouts.visibility = View.GONE
            binding.deliveryLayout.visibility = View.GONE
            binding.surgeryPersonBtnLayout.visibility = View.GONE
            binding.srugeryPersonLinearLayout.visibility = View.GONE

        }
        else if (obj.equals("Inspect Deliver")){
            binding.saleOrderLayouts.visibility = View.GONE
            binding.chipCardViewBtton.visibility = View.GONE
            binding.inspectTabLayouts.visibility = View.VISIBLE
            binding.deliveryLayout.visibility = View.GONE
            binding.srugeryPersonLinearLayout.visibility = View.GONE
        }
        else if (obj.equals("Delivery")){
            binding.saleOrderLayouts.visibility = View.GONE
            binding.inspectTabLayouts.visibility = View.GONE
            binding.deliveryLayout.visibility = View.VISIBLE
            binding.chipCardViewBtton.visibility = View.VISIBLE
            binding.counterBtnLayout.visibility = View.GONE
            binding.deliveryBtnLayout.visibility = View.VISIBLE
            binding.surgeryPersonBtnLayout.visibility = View.GONE
            binding.srugeryPersonLinearLayout.visibility = View.GONE
            if (Global.mArrayUriList.size > 0){
                val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                val adapter = UploadImageListAdapter(this, Global.mArrayUriList, arrayOf())
                binding.proofImageRecyclerView.layoutManager = linearLayoutManager
                binding.proofImageRecyclerView.adapter = adapter
            }

        }

        else if (obj.equals("Surgery Coordinator")){
            binding.saleOrderLayouts.visibility = View.GONE
            binding.inspectTabLayouts.visibility = View.GONE
            binding.deliveryLayout.visibility = View.GONE
            binding.chipCardViewBtton.visibility = View.VISIBLE
            binding.counterBtnLayout.visibility = View.GONE
            binding.deliveryBtnLayout.visibility = View.GONE
            binding.surgeryPersonBtnLayout.visibility = View.VISIBLE
            binding.surgeryCoordinatorLinearLayout.visibility = View.VISIBLE
            binding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.VISIBLE
            binding.surgeryDetailLinearLayout.visibility = View.GONE
            binding.srugeryPersonLinearLayout.visibility = View.GONE

        }

        else if (obj.equals("Surgery Person")){
            //todo surgery persons tabs--
            var pagerAdapter = ViewPagerAdapter(supportFragmentManager)

            pagerAdapter.add(SurgeryPersonDetailsFragment(), "Details")
            pagerAdapter.add(DeliveryItemsFragment(), "Delivery Items")
            pagerAdapter.add(PendingItemsFragment(), "Pending Items")

            binding.viewpagerSurgeryPerson.adapter = pagerAdapter

            binding.tabLayoutSurgery.setupWithViewPager(binding.viewpagerSurgeryPerson)

            binding.saleOrderLayouts.visibility = View.GONE
            binding.inspectTabLayouts.visibility = View.GONE
            binding.deliveryLayout.visibility = View.GONE
            binding.surgeryCoordinatorLinearLayout.visibility = View.GONE
            binding.srugeryPersonLinearLayout.visibility = View.VISIBLE
            binding.chipCardViewBtton.visibility = View.GONE


        }



    }



    private fun dispatchTakePictureIntent() {
        var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, PICK_IMAGES_REQUEST_CODE)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGES_REQUEST_CODE-> if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && null != data){
                try {
                    var photo = data?.extras?.get("data")
                    file = savebitmap(photo as Bitmap)
                    picturePath = file.path
                    fileUri = Uri.fromFile(file)
                    Log.e("fileUri---", fileUri.toString())

                    //todo add camera bitmap file uri photo in arraylist...
                    Global.mArrayUriList.add(fileUri)
                    bindCameraImagesAdapter(Global.mArrayUriList)

                }catch (e:NullPointerException){
                    e.printStackTrace()
                }

            }else {
                // Failed to take picture
                Toast.makeText(this, "Failed to take camera picture", Toast.LENGTH_SHORT).show()

            }
        }

    }


    private fun bindCameraImagesAdapter(mArrayUriList: ArrayList<Uri>) {

        if (Global.mArrayUriList.size > 0){
            binding.recyclerViewMoreImageLayout.visibility = View.VISIBLE
            binding.clickNewImageLayout.visibility = View.GONE
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = UploadImageListAdapter(this, Global.mArrayUriList, arrayOf())
            binding.proofImageRecyclerView.layoutManager = linearLayoutManager
            binding.proofImageRecyclerView.adapter = adapter

            adapter.setOnItemClickListener { list, position ->

                if (binding.endTripChipGroup.visibility == View.VISIBLE){
                    if (position >= 0 && position < Global.mArrayUriList.size) {
                        Global.mArrayUriList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyDataSetChanged()

                    }
                    if (Global.mArrayUriList.size > 0) {
                        binding.recyclerViewMoreImageLayout.visibility = View.VISIBLE
                        binding.clickNewImageLayout.visibility = View.GONE

                    }else{
                        binding.recyclerViewMoreImageLayout.visibility = View.GONE
                        binding.clickNewImageLayout.visibility = View.VISIBLE
                    }
                }

            }
            adapter.notifyDataSetChanged()


        }else{
            binding.recyclerViewMoreImageLayout.visibility = View.GONE
            binding.clickNewImageLayout.visibility = View.VISIBLE
        }


    }

    //todo to convert bitmap to file--
    private fun savebitmap(bmp: Bitmap): File {
//        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        var extStorageDirectory = this.cacheDir //todo this is use for temporairy file path storage //"/storage/emulated/0/Download"
        var outStream: OutputStream? = null
        val num: Int = random.nextInt(90) + 10

        Log.e("extStorageDirectory---", extStorageDirectory.toString())
        var file = File.createTempFile("prefix", ".extension", extStorageDirectory)
        if (file.exists()) {
            file.delete()
            file = File(extStorageDirectory, "temp$num.png")
        }
        try {
            outStream = FileOutputStream(file)
            if (outStream!= null) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null!!
        }
        Log.e("file---", file.toString())
        return file
    }




    private fun allPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "denied permissions case", Toast.LENGTH_SHORT).show()
                // Show an error message or handle denied permissions case
            }
        }
    }





    //todo bind default data--
    private fun bindOneObserver() {
        viewModel.orderOneDetail.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e(FileUtil.TAG, "errorInApi: $it")
                    Global.warningmessagetoast(this@ParticularOrderDetailActivity, it)
                }, onLoading = {
                    binding.loadingBackFrame.visibility = View.VISIBLE
                    binding.loadingView.start()
                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            binding.loadingBackFrame.visibility = View.GONE
                            binding.loadingView.stop()
                            if (it.data.isNotEmpty() && it.data != null) {
                                setDefaultData(it.data[0])
                            }

                        } else {
                            binding.loadingBackFrame.visibility = View.GONE
                            binding.loadingView.stop()
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@ParticularOrderDetailActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        binding.loadingBackFrame.visibility = View.GONE
                        binding.loadingView.stop()
                        e.printStackTrace()
                    }

                }
            ))
    }


    //todo set deafult data
    private fun setDefaultData(modelData: OrderOneResponseModel.DataXXX) {

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (modelData!!.CardName?.isNotEmpty()!!) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    modelData.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            binding.nameIcon.setImageDrawable(drawable)
        }


        binding.tvOrderID.text = "Dr. Vijay Chauhan"
        //todo set contact details--

        binding.tvOrderStatus.text = "Status : Order Request"


        binding.tvExpectedData.text = "Order Information : Borem ipsum dolor sit hue , kdnm"


        if (modelData.CardName.isNotEmpty()) {
            binding.companyName.text = modelData.CardName
        } else {
            binding.companyName.text = "NA"
        }

        if (modelData.TaxDate.isNotEmpty()) {
            binding.tvPostingDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.TaxDate)
        } else {
            binding.tvPostingDate.text = "NA"
        }
        if (modelData.DocDueDate.isNotEmpty()) {
//            binding.tvValidDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.DocDueDate)
            binding.tvValidDate.text = "12:00 PM"
        } else {
            binding.tvValidDate.text = "NA"
        }

        if (modelData.U_MR_NO.isNotEmpty()) {
            binding.tvMrNo.text = modelData.U_MR_NO
        } else {
            binding.tvMrNo.text = "NA"
        }

        binding.tvRemarks.text = "NA"

    }


    companion object{
        private const val TAG = "ParticularOrderDetailAc"
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }

        }
        return true
    }


    override fun onBackPressed() {
//
        if (supportFragmentManager.backStackEntryCount >= 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
        super.onBackPressed()
    }




}