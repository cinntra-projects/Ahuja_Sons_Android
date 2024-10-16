package com.ahuja.sons.ahujaSonsClasses.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.BuildConfig
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.*
import com.ahuja.sons.ahujaSonsClasses.model.*
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityBillingCoordinatorDetailBinding
import com.ahuja.sons.databinding.UploadInspectionImageProofLayoutBottomSheetBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.github.loadingview.LoadingView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BillingCoordinatorDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityBillingCoordinatorDetailBinding
    var dependencyOrderAdapter = DependencyOrderAdapter()
    var earrandsOrderAdapter = EarrandsOrderAdapter()
    var deliveryDetailAdapter = DeliveryDetailsItemAdapter()
    lateinit var viewModel: MainViewModel
    private val PICK_IMAGES_REQUEST_CODE = 1111
    private val REQUEST_CODE_PERMISSIONS = 10
    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    val mArrayUriList = ArrayList<Uri>()
    val pdfurilist = ArrayList<String>()
    lateinit var fileUri: Uri

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    companion object{
        private const val TAG = "BillingCoordinatorDetai"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1001
    }

    var orderID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillingCoordinatorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        orderID = intent.getStringExtra("id")!!


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
        }

        checkAndRequestPermissions()

        binding.loadingBackFrame.visibility = View.GONE
        binding.loadingView.stop()


        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("")


        hideAndShowViews()

        binding.addConsumptionChip.setOnClickListener {
            showItemListDialogBottomSheetDialog()
        }


        binding.apply {
            tvCreateOrder.setOnClickListener {

                Intent(this@BillingCoordinatorDetailActivity, AddErrandActivity::class.java).also {
                    it.putExtra("orderID", globalDataWorkQueueList.OrderRequest!!.id.toString())
                    startActivity(it)
                }

            }
        }


        binding.submitChipBtn.setOnClickListener {
            callSubmitBillingApi()
        }

        binding.billingDoneChip.setOnClickListener {
            callSubmitBillingApi()
        }


    }


    override fun onResume() {
        super.onResume()
        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("onResume")

        tag?.let {
            // Do something with the tag, e.g., log it
            Log.d("MainActivity", "Received Tag: $it")

            // Reset the tag if needed
            tag = null



        }

    }

    //todo hide and show arrows--

    private fun hideAndShowViews() {

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


            deliveryUpArrow.setOnClickListener {
                rvDeliveryDetail.visibility = View.GONE
                deliveryUpArrow.visibility = View.GONE
                deliveryDownArrow.visibility = View.VISIBLE

            }

            deliveryDownArrow.setOnClickListener {
                rvDeliveryDetail.visibility = View.VISIBLE
                deliveryDownArrow.visibility = View.GONE
                deliveryUpArrow.visibility = View.VISIBLE
            }

        }


        binding.apply {
            surgeryDownArrow.setOnClickListener {
                tvSurgeryStatus.visibility = View.VISIBLE
                surgeryDetailsLayout.visibility = View.VISIBLE
                surgeryDownArrow.visibility = View.GONE
                surgeryUpArrow.visibility = View.VISIBLE

            }

            surgeryUpArrow.setOnClickListener {
                surgeryDownArrow.visibility = View.VISIBLE
                surgeryUpArrow.visibility = View.GONE
                tvSurgeryStatus.visibility = View.GONE
                surgeryDetailsLayout.visibility = View.GONE
            }

        }

        binding.apply {
            consumpDownArrow.setOnClickListener {
                ConsumptionLayout.visibility = View.VISIBLE
                consumpDownArrow.visibility = View.GONE
                consumpUpArrow.visibility = View.VISIBLE

            }

            consumpUpArrow.setOnClickListener {
                consumpDownArrow.visibility = View.VISIBLE
                consumpUpArrow.visibility = View.GONE
                ConsumptionLayout.visibility = View.GONE
            }

        }

        binding.dispatchUpArrow.setOnClickListener {
            binding.dispatchTripDetail.visibility = View.GONE
            binding.statusLayout.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.GONE
            binding.dispatchDownArrow.visibility = View.VISIBLE
        }

        binding.dispatchDownArrow.setOnClickListener {
            binding.dispatchTripDetail.visibility = View.VISIBLE
            binding.statusLayout.visibility = View.VISIBLE
            binding.dispatchDownArrow.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.VISIBLE
        }



        binding.apply {
            pickupDownArrow.setOnClickListener {
                pickUpDetailsLayout.visibility = View.VISIBLE
                pickupDownArrow.visibility = View.GONE
                pickupUpArrow.visibility = View.VISIBLE

            }

            pickupUpArrow.setOnClickListener {
                pickupDownArrow.visibility = View.VISIBLE
                pickupUpArrow.visibility = View.GONE
                pickUpDetailsLayout.visibility = View.GONE
            }

        }

    }



    private fun callSubmitBillingApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("BillingBy", Prefs.getString(Global.Employee_Code,""))

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.submitBilling(jsonObject1)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message)
                    onBackPressed()
                    finish()

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



    var globalDataWorkQueueList = AllWorkQueueResponseModel.Data()

    //todo work queue detail api --
    private fun bindWorkQueueDetail(flag : String) {
        viewModel.workQueueOne.observe(this, Event.EventObserver(
            onError = {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Global.warningmessagetoast(this, it)
            },
            onLoading = {
                binding.loadingBackFrame.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    if (response.data.size > 0) {
                        var modelData = response.data[0]

                        globalDataWorkQueueList = response.data[0]

                        //todo calling dependency and errand list

                        callErrandsAllList()

                        callDeliveryDetailItemList()

                        //todo set deafult data---
                        setDefaultData(modelData, flag)

                        bindGetInspectionImages()

                        //todo surgery details api call-
                        callSurgeryPersonDetailApi()

                        //todo consumption proof api--
                        bindGetConsumptionImagesProof()

                        callDispatchDetailsApi("")
                        if (globalDataWorkQueueList.is_return == true){
                            callPickUpTripDetailsApi("")
                        }

                    }

                }

            }

        ))
    }


    //todo set deafult data here---
    private fun setDefaultData(modelData: AllWorkQueueResponseModel.Data, flag : String) {

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (modelData.OrderRequest!!.CardName.isNotEmpty()!!) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    modelData.OrderRequest!!.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            binding.nameIcon.setImageDrawable(drawable)
        }

        binding.tvSurgeryName.setText(modelData.OrderRequest!!.SurgeryName)
        binding.companyName.setText(modelData.OrderRequest!!.CardName)
        binding.tvOrderID.setText("Order ID : "+modelData.OrderRequest!!.id)
        binding.tvDoctorName.setText(modelData.OrderRequest!!.Doctor[0].DoctorFirstName + " "+modelData.OrderRequest!!.Doctor[0].DoctorLastName)
        binding.tvOrderInfo.setText("Order Information :  "+ modelData.OrderRequest!!.OrderInformation)
        binding.tvSTatus.setText("Status  :  "+ modelData.OrderRequest!!.Status)
        if (!modelData.OrderRequest.SurgeryDate.isNullOrEmpty()){
            binding.tvSurgeryDateAndTime.setText("Surgery Date & Time  :  "+ Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.OrderRequest!!.SurgeryDate) +" "+ modelData.OrderRequest!!.SurgeryTime)
        }
        else{
            binding.tvSurgeryDateAndTime.setText("NA")
        }
        if (modelData.OrderRequest!!.SapOrderId.isNotEmpty()) {
            binding.apply {
                Log.e(TAG, "setDefaultData: SAP ID Not Blank" )

                //todo make dependency / errands editable after sap id linked-

              /*  tvCreateOrder.isClickable = false
                tvCreateOrder.isFocusable = false
                tvCreateOrder.isFocusableInTouchMode = false*/


                itemViewDetailCardView.visibility = View.VISIBLE
                itemDetailView.setOnClickListener {
                    var intent = Intent(this@BillingCoordinatorDetailActivity, ItemDetailActivity::class.java)
                    intent.putExtra("SapOrderId", modelData.OrderRequest!!.SapOrderId)
                    startActivity(intent)
                }

            }

        }else{
            binding.apply {
                Log.e(TAG, "setDefaultData: SAP ID  Blank" )

                itemViewDetailCardView.visibility = View.GONE

            }

        }

        //todo bind order detail--

        if (modelData.OrderRequest?.id.toString().isNotEmpty()){
            binding.tvOMSID.setText(modelData.OrderRequest.id.toString())
        }else{
            binding.tvOMSID.setText("NA")
        }
        if (modelData.OrderRequest!!.Employee.isNotEmpty()){
            binding.tvSalesPerson.setText(modelData.OrderRequest!!.Employee[0].SalesEmployeeName)
        }else{
            binding.tvSalesPerson.setText("NA")
        }

        if (!modelData.OrderRequest!!.PreparedBy.isNullOrEmpty()){
            binding.preparedByLayout.visibility = View.VISIBLE
            binding.tvPreparedBy.setText(modelData.OrderRequest!!.PreparedBy)
        }else{
            binding.preparedByLayout.visibility = View.GONE
            binding.tvPreparedBy.setText("NA")
        }
        if (!modelData.OrderRequest!!.InspectedBy.isNullOrEmpty()){
            binding.inspectedByLayout.visibility = View.VISIBLE
            binding.tvInspectedBy.setText(modelData.OrderRequest!!.InspectedBy)
        }else{
            binding.inspectedByLayout.visibility = View.GONE
            binding.tvInspectedBy.setText("NA")
        }

        if (modelData.OrderRequest!!.Remarks.isNotEmpty()){
            binding.tvRemarks.setText(modelData.OrderRequest!!.Remarks)
        }else{
            binding.tvRemarks.setText("NA")
        }


    }


    lateinit var recyclerViewMoreImageLayout: LinearLayout
    lateinit var proofImageRecyclerView: RecyclerView
    lateinit var clickNewImageLayout: LinearLayout
    lateinit var statusLayout: LinearLayout

    //todo upload proof--
    private fun showItemListDialogBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: UploadInspectionImageProofLayoutBottomSheetBinding = UploadInspectionImageProofLayoutBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())


        recyclerViewMoreImageLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.recyclerViewMoreImageLayout)!!
        proofImageRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.proofImageRecyclerView)!!
        clickNewImageLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.clickNewImageLayout)!!
        statusLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.statusLayout)!!
        var tv_employee_name = bottomSheetDialog.findViewById<TextView>(R.id.tv_employee_name)!!

        tv_employee_name.setText("Proof")

        bindingBottomSheet.acStatus.visibility = View.GONE


        bindingBottomSheet.ivCloseDialog.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.btnConfirm.setOnClickListener {

            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.linearAddImage.setOnClickListener {
            dispatchTakePictureIntent()

        }


        bindingBottomSheet.tvAddMoreImages.setOnClickListener {
            dispatchTakePictureIntent()

        }


        bindingBottomSheet.btnConfirm.setOnClickListener {
            callUploadProofApi(bottomSheetDialog, bindingBottomSheet.loadingback, bindingBottomSheet.loadingView)
        }

        bottomSheetDialog.show()


    }


    //todo start trip for delivery person--
    private fun callUploadProofApi(bottomSheetDialog: BottomSheetDialog, loadingback: FrameLayout, loadingView: LoadingView) {
        loadingback.visibility = View.VISIBLE
        loadingView.start()

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        builder.addFormDataPart("OrderID", globalDataWorkQueueList.OrderRequest!!.id.toString())
        builder.addFormDataPart("UploadBy", Prefs.getString(Global.Employee_Code))

        if (pdfurilist.size > 0) {
            for (i in pdfurilist.indices) {
                val file: File = File(pdfurilist[i])
                builder.addFormDataPart("Attachment", file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
            }
        } else {
            builder.addFormDataPart("Attachment", "", RequestBody.create("multipart/form-data".toMediaTypeOrNull(), ""))
        }


        val requestBody = builder.build()
        Log.e("payload", requestBody.toString())

        val jsonObject = JSONObject()
        jsonObject.put("UploadBy", Prefs.getString(Global.Employee_Code))
        jsonObject.put("OrderID", globalDataWorkQueueList.OrderRequest!!.id.toString())

        val filesArray = JSONArray()
        if (pdfurilist.size > 0) {
            for (i in pdfurilist.indices) {
                val file: File = File(pdfurilist[i])
                filesArray.put( file.name)
                jsonObject.put("Attachment", filesArray)
            }
        }

        Log.e("payload", jsonObject.toString())

        val call: Call<RouteListModel> = ApiClient().service.uploadBillingProof(requestBody)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(call: Call<RouteListModel?>, response: Response<RouteListModel?>) {
                if (response.body()!!.status == 200) {

                    loadingback.visibility = View.GONE
                    loadingView.stop()

                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@BillingCoordinatorDetailActivity, "Picture Uploaded Success")
                    bottomSheetDialog.dismiss()

                    var jsonObject = JsonObject()
                    jsonObject.addProperty("id", orderID)
                    viewModel.callWorkQueueDetailApi(jsonObject)
                    bindWorkQueueDetail("uploadProof")


                } else {
                    loadingback.visibility = View.GONE
                    loadingView.stop()
                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<RouteListModel?>, t: Throwable) {
                loadingback.visibility = View.GONE
                loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
    private var tag: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imgFile = File(currentPhotoPath!!)
            if (imgFile.exists()) {
                fileUri = Uri.fromFile(imgFile)
                mArrayUriList.add(fileUri)
                pdfurilist.add(currentPhotoPath!!)
                bindCameraImagesAdapter(mArrayUriList)

                Log.e(TAG, "onActivityResult: $currentPhotoPath")

            }
        }
        /*else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Retrieve the tag from the Intent
            tag = data?.getStringExtra("TAG")
        }*/
    }


    //todo bind dialog recycler view--
    private fun bindCameraImagesAdapter(mArrayUriList: ArrayList<Uri>) {

        if (mArrayUriList.size > 0) {
            recyclerViewMoreImageLayout.visibility = View.VISIBLE
            clickNewImageLayout.visibility = View.GONE
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = UploadImageListAdapter(this, mArrayUriList, arrayOf(), pdfurilist,"SurgeryPerson")
            proofImageRecyclerView.layoutManager = linearLayoutManager
            proofImageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            adapter.setOnItemDeliveryClick { list, position ->

                if (position >= 0 && position < list.size) {
                    list.removeAt(position)
                    pdfurilist.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()

                }
                if (list.size > 0 && pdfurilist.size > 0) {
                    recyclerViewMoreImageLayout.visibility = View.VISIBLE
                    clickNewImageLayout.visibility = View.GONE

                } else {
                    recyclerViewMoreImageLayout.visibility = View.GONE
                    clickNewImageLayout.visibility = View.VISIBLE
                }

                Log.e(TAG, "bindCameraImagesAdapter: "+list )
            }
            adapter.notifyDataSetChanged()
        } else {
            recyclerViewMoreImageLayout.visibility = View.GONE
            clickNewImageLayout.visibility = View.VISIBLE
        }


    }


    //todo get consumpton images--
    private fun bindGetConsumptionImagesProof() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getBillingProof(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    bindConsumptionShowProofAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind inspection image adapter data----
    private fun bindConsumptionShowProofAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.AddConsumedChipBtn.visibility = View.GONE
            binding.ViewConsumedChipBtn.visibility = View.GONE
            binding.finalSubmit.visibility = View.VISIBLE

            binding.consumptionProofCardView.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.rvConsumptionProof.layoutManager = linearLayoutManager
            binding.rvConsumptionProof.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.consumptionProofCardView.visibility = View.GONE
            binding.AddConsumedChipBtn.visibility = View.VISIBLE
            binding.ViewConsumedChipBtn.visibility = View.VISIBLE
            binding.finalSubmit.visibility = View.GONE
        }


    }


    //todo get inspection images--
    private fun bindGetInspectionImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getInspectionImages(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    bindInspectionUploadedProofAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind inspection image adapter data----
    private fun bindInspectionUploadedProofAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.inspectionViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.inspectionProofImageRecyclerView.layoutManager = linearLayoutManager
            binding.inspectionProofImageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.inspectionViewLayout.visibility = View.GONE

        }


    }


    var errandsListModels = ArrayList<AllErrandsListModel.Data>()

    private fun callErrandsAllList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

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
                        setupEarrandRecyclerview()
                        binding.errandsCardViewLayout.visibility = View.GONE
                        binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                        binding.rvEarrands.visibility = View.GONE

                    }

                } else {
                    binding.errandsCardViewLayout.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE

                }
            }

            override fun onFailure(call: Call<AllErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //todo delivery detail list items--

    var deliveryItemList_gl = ArrayList<DeliveryItemListModel.Data>()

    private fun callDeliveryDetailItemList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

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

                }
            }

            override fun onFailure(call: Call<DeliveryItemListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupEarrandRecyclerview() = binding.rvEarrands.apply {
        adapter = earrandsOrderAdapter
        layoutManager = LinearLayoutManager(this@BillingCoordinatorDetailActivity)
        earrandsOrderAdapter.notifyDataSetChanged()
    }


    private fun setupDeliveryDetailRecyclerview() = binding.rvDeliveryDetail.apply {
        adapter = deliveryDetailAdapter
        layoutManager = LinearLayoutManager(this@BillingCoordinatorDetailActivity)
        deliveryDetailAdapter.notifyDataSetChanged()
    }



    //todo calling surgery person detail api here---

    var surgeryDetailData_gl:  ArrayList<SurgeryPersonNameListModel.Data> = ArrayList<SurgeryPersonNameListModel.Data>()

    private fun callSurgeryPersonDetailApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject = JsonObject()
        jsonObject.addProperty("OrderID", globalDataWorkQueueList.OrderRequest?.id)

        val call: Call<SurgeryPersonNameListModel> = ApiClient().service.getSurgeryPersonDetail(jsonObject)
        call.enqueue(object : Callback<SurgeryPersonNameListModel?> {
            override fun onResponse(call: Call<SurgeryPersonNameListModel?>, response: Response<SurgeryPersonNameListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    surgeryDetailData_gl = response.body()!!.data
                    var data = response.body()!!.data
                    if (data.size > 0){

                        binding.apply {
                            surgeryDetailCardView.visibility = View.VISIBLE

                            tvSurgeryStatus.setText("Status : Ended")

                            tvSurgeryVehicleNo.setText("NA")
                            if (data[0].StartAt.isNotEmpty()){
                                tvSurgeryStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data[0].StartAt))
                            }else{
                                tvSurgeryStartTime.setText("NA")
                            }
                            if (data[0].EndAt.isNotEmpty()){
                                tvSurgeryEndTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data[0].EndAt))
                            }else{
                                tvSurgeryEndTime.setText("NA")
                            }
                            if (data[0].StartAt.isNotEmpty() && data[0].EndAt.isNotEmpty()){
                                tvSurgeryDuration.setText(Global.durationGet(data[0].StartAt, data[0].EndAt))
                            }
                            else{
                                tvSurgeryDuration.setText("00:00:00")
                            }
                            tvCRSNo.setText(data[0].NoOfCSRRequired)

                            val innerAdapter = SurgeryNameListAdapter(data)
                            rvSurgeryPersonListName.layoutManager = LinearLayoutManager(this@BillingCoordinatorDetailActivity, LinearLayoutManager.VERTICAL, false)
                            rvSurgeryPersonListName.adapter = innerAdapter
                            innerAdapter.notifyDataSetChanged()

                        }

                        getSurgeryUploadProofAPi()

                    }
                    else{

                        binding.loadingBackFrame.visibility = View.GONE
                        binding.loadingView.stop()
                        binding.surgeryDetailCardView.visibility = View.GONE
                        val innerAdapter = SurgeryNameListAdapter(ArrayList())
                        innerAdapter.notifyDataSetChanged()

                    }
                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<SurgeryPersonNameListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //todo get surgery image proof list images--

    var surgeryProofList = ArrayList<UploadedPictureModel.Data>()
    private fun getSurgeryUploadProofAPi() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getSurgeryProof(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    surgeryProofList = response.body()!!.data
                    bindSurgeryPersonUploadedProofAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind surgery image adapter data----
    private fun bindSurgeryPersonUploadedProofAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.surgeryProofViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.rvSurgeryList.layoutManager = linearLayoutManager
            binding.rvSurgeryList.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.surgeryProofViewLayout.visibility = View.GONE

        }


    }

    //todo call trip detail--
    private fun callDispatchDetailsApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.id)

        val call: Call<TripDetailModel> = ApiClient().service.getTripDetailsApi(jsonObject1)
        call.enqueue(object : Callback<TripDetailModel?> {
            override fun onResponse(call: Call<TripDetailModel?>, response: Response<TripDetailModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data

                    if (listData.size > 0){

                        var data = listData[0]
                        binding.dispatchedDetailLayout.visibility = View.VISIBLE
                        if (data.StartAt.isNotEmpty()){

                            binding.apply {
                                tvStartLocation.setText(data.StartLocation)
                                tvStartTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))

                                if (!data.Deliveryassigned.isNullOrEmpty()){
                                    Log.e(TAG, "Deliveryassigned: "+data.Deliveryassigned )
                                    tvDeliveryPersonOne.setText(data.Deliveryassigned[0].DeliveryPerson1)
                                    tvDeliveryPersonTwo.setText(data.Deliveryassigned[0].DeliveryPerson2)
                                    tvDeliveryPersonThree.setText(data.Deliveryassigned[0].DeliveryPerson3)
                                    tvVehicleNum.setText(data.Deliveryassigned[0].VechicleNo)
                                }

                                if (data.EndAt.isNotEmpty() && data.EndLocation.isNotEmpty()){
                                    linearTripEndDetails.visibility = View.VISIBLE
                                    tvTripStatus.setText("Status : Ended")
                                    tvDispatchEndLocation.setText(data.EndLocation)
                                    tvEndTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }else{
                                    tvTripStatus.setText("Status : Started")
                                    linearTripEndDetails.visibility = View.GONE
                                    tvDispatchEndLocation.setText("NA")
                                    tvEndTripTime.setText("NA")

                                }

                                bindGetDeliveryDispatchImages()
                            }
                        }
                    }else{
                        binding.dispatchedDetailLayout.visibility = View.GONE
                    }

                }
                else if (response.body()!!.status == 401){
                    binding.dispatchedDetailLayout.visibility = View.GONE
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                }else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //todo call pickup trip detail--
    private fun callPickUpTripDetailsApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.id)

        val call: Call<TripDetailModel> = ApiClient().service.getPickUpTripDetailsApi(jsonObject1)
        call.enqueue(object : Callback<TripDetailModel?> {
            override fun onResponse(call: Call<TripDetailModel?>, response: Response<TripDetailModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    var proofData = response.body()!!.proof_data

                    if (listData.size > 0){

                        var data = listData[0]
                        binding.pickupDetailCardView.visibility = View.VISIBLE
                        binding.pickUpAllDetail.visibility = View.VISIBLE
                        if (data.StartAt.isNotEmpty()){

                            binding.apply {

                                if (data.StartAt.isNotEmpty() && data.EndAt.isEmpty()){
                                    tvPickUpStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))
                                    pickUpEndTimeLayout.visibility = View.GONE
                                }

                                if (data.StartAt.isNotEmpty() && data.EndAt.isNotEmpty()){
                                    pickUpEndTimeLayout.visibility = View.VISIBLE
                                    tvPickUpStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))
                                    tvPickUpEndTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }

                                if (data.Deliveryassigned.isNotEmpty()){
                                    Log.e(TAG, "Deliveryassigned: "+data.Deliveryassigned )
                                    tvPickUpDeliveryPersonOne.setText(data.Deliveryassigned[0].DeliveryPerson1)
                                    tvPickUpDeliveryPersonTwo.setText(data.Deliveryassigned[0].DeliveryPerson2)
                                    tvPickUpDeliveryPersonThree.setText(data.Deliveryassigned[0].DeliveryPerson3)
                                    tvVehicleNo.setText(data.Deliveryassigned[0].VechicleNo)

                                }


                            }


                            if (!proofData.isNullOrEmpty()){

                                var mArrayUriList : java.util.ArrayList<UploadedPictureModel.Data> = java.util.ArrayList()

                                mArrayUriList.clear()
                                mArrayUriList.addAll(proofData)

                                if (mArrayUriList.size > 0) {

                                    binding.pickUpViewLayout.visibility = View.VISIBLE

                                    val linearLayoutManager = LinearLayoutManager(this@BillingCoordinatorDetailActivity, LinearLayoutManager.HORIZONTAL, false)
                                    val adapter = PreviousImageViewAdapter(this@BillingCoordinatorDetailActivity, mArrayUriList, arrayOf(), arrayListOf())
                                    binding.pickUpProofRecyclerView.layoutManager = linearLayoutManager
                                    binding.pickUpProofRecyclerView.adapter = adapter
                                    adapter.notifyDataSetChanged()

                                } else {

                                    binding.pickUpViewLayout.visibility = View.GONE

                                }
                            }

                        }
                    }else{
                        binding.pickupDetailCardView.visibility = View.GONE
                    }

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo delivery dispatch upload proof api here---

    var dispatchList = java.util.ArrayList<UploadedPictureModel.Data>()
    private fun bindGetDeliveryDispatchImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getDeliveryDispatchProofImage(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    dispatchList = response.body()!!.data

                    bindGETDispatchCameraImagesAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@BillingCoordinatorDetailActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@BillingCoordinatorDetailActivity, t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }


    //todo bind dispatch image adater data----
    private fun bindGETDispatchCameraImagesAdapter(mArrayUriList: java.util.ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.dispatchViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.dispatchDeliveryProofRecyclerView.layoutManager = linearLayoutManager
            binding.dispatchDeliveryProofRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.dispatchViewLayout.visibility = View.GONE

        }


    }



    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val write =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = mutableListOf<String>()

        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            false
        } else {
            true
        }
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", it)
//                    val photoURI: Uri = Uri.fromFile(it) //todo ==> using Uri.fromFile to create the URI for the photo file, which leads to a FileUriExposedException on Android 7.0 and above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                }
            }
        }
    }

    private var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/Ahuja"
        )
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        val image = File.createTempFile(
            imageFileName,
            ".png",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }


}