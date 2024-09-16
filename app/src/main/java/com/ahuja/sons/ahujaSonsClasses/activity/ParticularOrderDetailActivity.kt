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
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.Interface.OrderStageItemClick
import com.ahuja.sons.ahujaSonsClasses.adapter.*
import com.ahuja.sons.ahujaSonsClasses.fragments.order.SurgeryPersonDetailsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.UploadProofImagesFragment
import com.ahuja.sons.ahujaSonsClasses.model.AllDependencyAndErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.AllErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryItemListModel
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonModelData
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.OrderOneResponseModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityParticularOrderDetailBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class ParticularOrderDetailActivity : AppCompatActivity(), OrderStageItemClick {

    lateinit var binding : ActivityParticularOrderDetailBinding
    lateinit var viewModel: MainViewModel
    var id = ""
    var dependencyOrderAdapter = DependencyOrderAdapter()
    var earrandsOrderAdapter = EarrandsOrderAdapter()
    var deliveryDetailAdapter = DeliveryDetailsItemAdapter()

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
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    var orderID = ""
    var FLAG = ""

    lateinit var pagerAdapter : ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticularOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)

        orderID = intent.getStringExtra("id").toString()
        FLAG = intent.getStringExtra("flag")!!
        Log.e(TAG, "onCreate: $orderID")

        //todo inspection tabs
//        pagerAdapter.add(DeliveryItemsFragment(), "Delivery Items")
//        pagerAdapter.add(PendingItemsFragment(), "Pending Items")
        pagerAdapter.add(UploadProofImagesFragment(), "Proof")

        binding.viewpagerInspect.adapter = pagerAdapter

        binding.tabLayout.setupWithViewPager(binding.viewpagerInspect)


        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
        }



        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        binding.loadingBackFrame.visibility = View.GONE
        binding.loadingView.stop()


        if (Global.checkForInternet(this@ParticularOrderDetailActivity)) {
            binding.loadingBackFrame.visibility = View.VISIBLE
            binding.loadingView.start()
            var jsonObject = JsonObject()
            jsonObject.addProperty("id", orderID)
            viewModel.callOrderRequestOneApi(jsonObject)
            bindOneObserver()

        }



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
            binding.tvOrderInfo.visibility = View.GONE
            binding.tvDoctorName.visibility = View.GONE
            binding.headerUpArrow.visibility = View.GONE
            binding.headerDownArrow.visibility = View.VISIBLE
        }

        binding.headerDownArrow.setOnClickListener {
            binding.tvOrderID.visibility = View.VISIBLE
            binding.tvOrderInfo.visibility = View.VISIBLE
            binding.tvDoctorName.visibility = View.VISIBLE
            binding.headerDownArrow.visibility = View.GONE
            binding.headerUpArrow.visibility = View.VISIBLE
        }



        //todo order details arrow--
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

        binding.apply {
            errandsUpArrow.setOnClickListener {
                if (rvEarrands.visibility == View.VISIBLE) {
                    rvEarrands.visibility = View.GONE
                    errandsUpArrow.setImageResource(R.drawable.arrow_up_icon)

                } else {
                    rvEarrands.visibility = View.VISIBLE
                    errandsUpArrow.setImageResource(R.drawable.down_arrow_icon)
                }
            }


            dependencyUpArrow.setOnClickListener {
                if (rvDependency.visibility == View.VISIBLE) {
                    rvDependency.visibility = View.GONE
                    dependencyUpArrow.setImageResource(R.drawable.arrow_up_icon)

                } else {
                    rvDependency.visibility = View.VISIBLE
                    dependencyUpArrow.setImageResource(R.drawable.down_arrow_icon)
                }
            }


            binding.deliveryUpArrow.setOnClickListener {
                binding.rvDeliveryDetail.visibility = View.GONE
                binding.deliveryUpArrow.visibility = View.GONE
                binding.deliveryDownArrow.visibility = View.VISIBLE

            }

            binding.deliveryDownArrow.setOnClickListener {
                binding.rvDeliveryDetail.visibility = View.VISIBLE
                binding.deliveryDownArrow.visibility = View.GONE
                binding.deliveryUpArrow.visibility = View.VISIBLE
            }

        }

/*
        binding.dispatchUpArrow.setOnClickListener {
            binding.dispatchDetailsLayout.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.GONE
            binding.dispatchDownArrow.visibility = View.VISIBLE
        }

        binding.dispatchDownArrow.setOnClickListener {
            binding.dispatchDetailsLayout.visibility = View.VISIBLE
            binding.dispatchDownArrow.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.VISIBLE
        }


        binding.surgeryUpArrow.setOnClickListener {
            binding.surgeryDetailsLayout.visibility = View.GONE
            binding.surgeryUpArrow.visibility = View.GONE
            binding.surgeryDownArrow.visibility = View.VISIBLE
        }

        binding.surgeryDownArrow.setOnClickListener {
            binding.surgeryDetailsLayout.visibility = View.VISIBLE
            binding.surgeryDownArrow.visibility = View.GONE
            binding.surgeryUpArrow.visibility = View.VISIBLE
        }*/
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
                val adapter = UploadImageListAdapter(
                    this,
                    Global.mArrayUriList,
                    arrayOf(),
                    ArrayList()
                )
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
//            pagerAdapter.add(DeliveryItemsFragment(), "Delivery Items")
//            pagerAdapter.add(PendingItemsFragment(), "Pending Items")

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
            val adapter = UploadImageListAdapter(this, Global.mArrayUriList, arrayOf(), ArrayList())
            binding.proofImageRecyclerView.layoutManager = linearLayoutManager
            binding.proofImageRecyclerView.adapter = adapter

            adapter.setOnItemClickListener { list, position,pdfList ->

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




    var globalDataWorkQueueList = OrderOneResponseModel.Data()
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

                            if (it.data.size > 0) {
                                var modelData = it.data[0]

                                globalDataWorkQueueList = it.data[0]

                                //todo calling dependency and errand list

                                callDependencyAllList()

                                callErrandsAllList()

                                callDeliveryDetailItemList()

                                //todo set deafult data---
                                setDefaultData(modelData)



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
    private fun setDefaultData(modelData: OrderOneResponseModel.Data){

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (modelData.CardName.isNotEmpty()!!) {
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


        binding.companyName.setText(modelData.CardName)
        binding.tvOrderID.setText("Order ID : "+modelData.id)
        binding.tvDoctorName.setText(modelData.Doctor[0].DoctorFirstName + " "+modelData.Doctor[0].DoctorLastName)
        binding.tvOrderInfo.setText("Order Information :  "+ modelData.OrderInformation)
        binding.tvOrderStatus.setText("Status  :  "+ modelData.Status)


        if (modelData.SapOrderId.isNotEmpty()) {
            binding.apply {
                SapOrderIdEdt.setText(modelData.SapOrderId)
                SapOrderIdEdt.isClickable = false
                SapOrderIdEdt.isFocusable = false
                SapOrderIdEdt.isFocusableInTouchMode = false
              
                itemViewDetailCardView.visibility = View.VISIBLE
                itemDetailView.setOnClickListener {
                    var intent = Intent(this@ParticularOrderDetailActivity, ItemDetailActivity::class.java)
                    intent.putExtra("SapOrderId", modelData.SapOrderId)
                    startActivity(intent)
                }

            }

        }else{
            binding.apply {
                SapOrderIdEdt.isClickable = false
                SapOrderIdEdt.isFocusable = false
                SapOrderIdEdt.isFocusableInTouchMode = false
                itemViewDetailCardView.visibility = View.GONE

            }

        }


        //todo bind order detail--

        if (modelData.id.toString().isNotEmpty()){
            binding.tvOMSID.setText(modelData.id.toString())
        }else{
            binding.tvOMSID.setText("NA")
        }
        if (modelData.Employee.isNotEmpty()){
            binding.tvSalesPerson.setText(modelData.Employee[0].SalesEmployeeName)
        }else{
            binding.tvSalesPerson.setText("NA")
        }
        if (modelData.SurgeryName.isNotEmpty()){
            binding.tvPreparedBy.setText(modelData.SurgeryName)
        }else{
            binding.tvPreparedBy.setText("NA")
        }
        if (modelData.SurgeryDate.isNotEmpty()){
            binding.tvInspectedBy.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.SurgeryDate))
        }else{
            binding.tvInspectedBy.setText("NA")
        }

        if (modelData.Remarks.isNotEmpty()){
            binding.tvRemarks.setText(modelData.Remarks)
        }else{
            binding.tvRemarks.setText("NA")
        }



    }




    var dependencyListModels = ArrayList<AllDependencyAndErrandsListModel.Data>()

    private fun callDependencyAllList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.id)

        binding.loadingView.start()
        binding.loadingBackFrame.visibility = View.VISIBLE
        val call: Call<AllDependencyAndErrandsListModel> = ApiClient().service.getDependencyList(jsonObject)
        call.enqueue(object : Callback<AllDependencyAndErrandsListModel?> {
            override fun onResponse(
                call: Call<AllDependencyAndErrandsListModel?>,
                response: Response<AllDependencyAndErrandsListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())

                    if (response.body()!!.data.size > 0){

                        binding.tvDependency.setText("Dependency  " + "( "+response.body()!!.data.size.toString()+" )")
                        binding.dependencyCardViewLayout.visibility = View.VISIBLE
                        binding.tvDependencyNoDataFound.visibility = View.GONE
                        binding.rvDependency.visibility = View.VISIBLE

                        dependencyListModels.clear()
                        dependencyListModels.addAll(response.body()!!.data)

                        dependencyOrderAdapter.submitList(dependencyListModels)

                        setupDependencyRecyclerview()

                    }
                    else{
                        binding.dependencyCardViewLayout.visibility = View.GONE
                        setupDependencyRecyclerview()

                        binding.tvDependencyNoDataFound.visibility = View.VISIBLE
                        binding.rvDependency.visibility = View.GONE

                    }

                } else {

                    binding.tvDependencyNoDataFound.visibility = View.VISIBLE
                    binding.rvDependency.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Global.warningmessagetoast(this@ParticularOrderDetailActivity, response.message().toString());

                }
            }

            override fun onFailure(call: Call<AllDependencyAndErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@ParticularOrderDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    var errandsListModels = ArrayList<AllErrandsListModel.Data>()

    private fun callErrandsAllList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.id)

        binding.loadingView.start()
        binding.loadingBackFrame.visibility = View.VISIBLE
        val call: Call<AllErrandsListModel> = ApiClient().service.getErrandsList(jsonObject)
        call.enqueue(object : Callback<AllErrandsListModel?> {
            override fun onResponse(
                call: Call<AllErrandsListModel?>,
                response: Response<AllErrandsListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){
                        binding.tvErrands.setText("Errands  " + "( "+response.body()!!.data.size.toString()+" )")
                        binding.errandsCardViewLayout.visibility = View.VISIBLE
                        binding.tvErrandsNoDataFound.visibility = View.GONE
                        binding.rvEarrands.visibility = View.VISIBLE

                        errandsListModels.clear()
                        errandsListModels.addAll(response.body()!!.data)

                        earrandsOrderAdapter.submitList(errandsListModels)

                        setupEarrandRecyclerview()


                    }else{

                        setupDependencyRecyclerview()
                        binding.errandsCardViewLayout.visibility = View.GONE
                        binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                        binding.rvEarrands.visibility = View.GONE

                    }

                } else {
                    binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                    binding.rvEarrands.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Global.warningmessagetoast(this@ParticularOrderDetailActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<AllErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@ParticularOrderDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    var deliveryItemList_gl = ArrayList<DeliveryItemListModel.Data>()

    private fun callDeliveryDetailItemList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.id)

        binding.loadingView.start()
        binding.loadingBackFrame.visibility = View.VISIBLE
        val call: Call<DeliveryItemListModel> = ApiClient().service.getOrderDeliveryItems(jsonObject)
        call.enqueue(object : Callback<DeliveryItemListModel?> {
            override fun onResponse(
                call: Call<DeliveryItemListModel?>,
                response: Response<DeliveryItemListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){
                        binding.tvDeliveryDetail.setText("Delivery Details  " + "( "+response.body()!!.data.size.toString()+" )")
                        binding.deliveryDetailLayout.visibility = View.VISIBLE
                        binding.tvDeliveryDetailNoDataFound.visibility = View.GONE
                        binding.rvDeliveryDetail.visibility = View.VISIBLE

                        deliveryItemList_gl.clear()
                        deliveryItemList_gl.addAll(response.body()!!.data)

                        deliveryDetailAdapter.submitList(deliveryItemList_gl)

                        setupDeliveryDetailRecyclerview()


                    }else{
                        setupDeliveryDetailRecyclerview()
                        binding.deliveryDetailLayout.visibility = View.GONE
                        binding.tvDeliveryDetailNoDataFound.visibility = View.VISIBLE
                        binding.rvDeliveryDetail.visibility = View.GONE

                    }

                } else {
                    binding.deliveryDetailLayout.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
//                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<DeliveryItemListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@ParticularOrderDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupDependencyRecyclerview() = binding.rvDependency.apply {
        adapter = dependencyOrderAdapter
        layoutManager = LinearLayoutManager(this@ParticularOrderDetailActivity)
        dependencyOrderAdapter.notifyDataSetChanged()
    }


    private fun setupEarrandRecyclerview() = binding.rvEarrands.apply {
        adapter = earrandsOrderAdapter
        layoutManager = LinearLayoutManager(this@ParticularOrderDetailActivity)
        earrandsOrderAdapter.notifyDataSetChanged()
    }


    private fun setupDeliveryDetailRecyclerview() = binding.rvDeliveryDetail.apply {
        adapter = deliveryDetailAdapter
        layoutManager = LinearLayoutManager(this@ParticularOrderDetailActivity)
        deliveryDetailAdapter.notifyDataSetChanged()
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