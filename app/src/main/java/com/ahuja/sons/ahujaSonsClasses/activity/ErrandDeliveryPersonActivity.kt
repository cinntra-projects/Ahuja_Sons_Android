package com.ahuja.sons.ahujaSonsClasses.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.BuildConfig
import com.ahuja.sons.ahujaSonsClasses.Interface.LocationPermissionHelper
import com.ahuja.sons.ahujaSonsClasses.adapter.DeliveryDetailsItemAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.PreviousImageViewAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.SurgeryNameListAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.UploadImageListAdapter
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryItemListModel
import com.ahuja.sons.ahujaSonsClasses.model.RouteListModel
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonNameListModel
import com.ahuja.sons.ahujaSonsClasses.model.TripDetailModel
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityOrderScreenForDeliveryPersonBinding
import com.ahuja.sons.databinding.UploadInspectionImageProofLayoutBottomSheetBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.github.loadingview.LoadingView
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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

class ErrandDeliveryPersonActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderScreenForDeliveryPersonBinding
    var isErrandTripStarted = false
    var isErrandTripEnd = false
    var isErrandUploadProof = false

    lateinit var viewModel: MainViewModel

    private val PICK_IMAGES_REQUEST_CODE = 1111
    private val REQUEST_CODE_PERMISSIONS = 10
    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    val mArrayUriList = ArrayList<Uri>()
    val pdfurilist = ArrayList<String>()
    lateinit var fileUri: Uri
    var deliveryDetailAdapter = DeliveryDetailsItemAdapter()

    companion object {
        private const val TAG = "OrderScreenForDeliveryP"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1001

    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    var orderID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderScreenForDeliveryPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        checkAndRequestPermissions()

        orderID = intent.getStringExtra("id").toString()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
        }

        client = LocationServices.getFusedLocationProviderClient(this)

        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("")

        hideAndShowViews()

        binding.btnTrip.visibility = View.VISIBLE

        binding.btnENdTrip.setOnClickListener {

            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else {
                getMyCurrentLocation("EndTrip")
            }
        }



        binding.btnTrip.setOnClickListener {

            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else {
                getMyCurrentLocation("StartTrip")
            }

        }


        binding.btnSubmit.setOnClickListener {
            binding.loadingView.start()
            binding.loadingBackFrame.visibility = View.VISIBLE
            var jsonObject = JsonObject()
            jsonObject.addProperty("DepositedBy", Prefs.getString(Global.Employee_Code,""))
            jsonObject.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
            jsonObject.addProperty("is_return", globalDataWorkQueueList.is_return)
            if (globalDataWorkQueueList.is_errands == true){
                jsonObject.addProperty("errand_id", globalDataWorkQueueList.DeliveryId)
            }else{
                jsonObject.addProperty("errand_id", "")
            }

            viewModel.getDeliveryPersonComplete(jsonObject)
            bindObserverDeliveryComplete()

        }


        binding.btnUploadProof.setOnClickListener {

            showItemListDialogBottomSheetDialog()

        }

    }

    private fun bindObserverDeliveryComplete() {
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
                    Global.successmessagetoast(this, response.message)
                    onBackPressed()
                    finish()
                }

            }

        ))
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

        binding.dispatchUpArrow.setOnClickListener {
            binding.statusLayout.visibility = View.GONE
            binding.dispatchDetail.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.GONE
            binding.dispatchDownArrow.visibility = View.VISIBLE
        }

        binding.dispatchDownArrow.setOnClickListener {
            binding.statusLayout.visibility = View.VISIBLE
            binding.dispatchDetail.visibility = View.VISIBLE
            binding.dispatchDownArrow.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.VISIBLE
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


    var globalDataWorkQueueList = AllWorkQueueResponseModel.Data()

    //todo work queue detail api --
    private fun bindWorkQueueDetail(fromWhere: String) {
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

                        //todo set deafult data---
                        setDefaultData(modelData, fromWhere)

                        bindGetInspectionImages()

                        bindGetDeliveryDispatchImages()

                        callTripDetailsApi("")

                        if (globalDataWorkQueueList.is_return == true){
                            callSurgeryPersonDetailApi()

                            callPickUpTripDetailsApi("")
                        }


                    }


                }


            }

        ))
    }


    //todo set default data here---
    private fun setDefaultData(modelData: AllWorkQueueResponseModel.Data, fromWhere: String) {

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


        binding.companyName.setText(modelData.OrderRequest!!.CardName)
        binding.tvOrderID.setText("Order ID : " + modelData.OrderRequest!!.id)
        binding.tvDoctorName.setText(modelData.OrderRequest!!.Doctor[0].DoctorFirstName + " " + modelData.OrderRequest!!.Doctor[0].DoctorLastName)
        binding.tvOrderInfo.setText("Order Information :  " + modelData.OrderRequest!!.OrderInformation)
        binding.tvSTatus.setText("Status  :  " + modelData.OrderRequest!!.Status)
        if (!modelData.OrderRequest.SurgeryDate.isNullOrEmpty()){
            binding.tvSurgeryDateAndTime.setText("Surgery Date & Time  :  "+ Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.OrderRequest!!.SurgeryDate) +" "+ modelData.OrderRequest!!.SurgeryTime)
        }
        else{
            binding.tvSurgeryDateAndTime.setText("NA")
        }

        if (modelData.OrderRequest!!.SapOrderId.isNotEmpty()) {
            binding.apply {

                itemViewDetailCardView.visibility = View.VISIBLE
                itemDetailView.setOnClickListener {
                    var intent = Intent(this@ErrandDeliveryPersonActivity, ItemDetailActivity::class.java)
                    intent.putExtra("SapOrderId", modelData.OrderRequest!!.SapOrderId)
                    intent.putExtra("deliveryID", orderID)
                    intent.putExtra("flag", "Delivery Person")
                    startActivity(intent)
                }

            }

        } else {
            binding.apply {

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


        //todo invoke when proof upload--
        if (fromWhere.equals("uploadProof")){

            callDeliveryDetailItemList()

        }


    }


    //todo calling surgery person detail api here---

    var surgeryDetailData_gl:  ArrayList<SurgeryPersonNameListModel.Data> = ArrayList<SurgeryPersonNameListModel.Data>()
    private fun callSurgeryPersonDetailApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject = JsonObject()
        jsonObject.addProperty("OrderID", globalDataWorkQueueList.OrderRequest?.id)
        jsonObject.addProperty("is_return", globalDataWorkQueueList.is_return)

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
                            rvSurgeryPersonListName.layoutManager = LinearLayoutManager(this@ErrandDeliveryPersonActivity, LinearLayoutManager.VERTICAL, false)
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

                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<SurgeryPersonNameListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //todo get surgery image proof list images--

    var surgeryProofList = ArrayList<UploadedPictureModel.Data>()
    private fun getSurgeryUploadProofAPi() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)

        val call: Call<UploadedPictureModel> = ApiClient().service.getSurgeryProof(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    surgeryProofList = response.body()!!.data
                    bindSurgeryPersonUploadedProofAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
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
    private fun callTripDetailsApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)
        if (globalDataWorkQueueList.is_errands == true){
            jsonObject1.addProperty("errand_id", globalDataWorkQueueList.DeliveryId)
        }else{
            jsonObject1.addProperty("errand_id", "")
        }

        val call: Call<TripDetailModel> = ApiClient().service.getTripDetailsApi(jsonObject1)
        call.enqueue(object : Callback<TripDetailModel?> {
            override fun onResponse(call: Call<TripDetailModel?>, response: Response<TripDetailModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data


                    isErrandTripStarted = response.body()!!.is_trip_started
                    isErrandTripEnd = response.body()!!.is_trip_ended
                    isErrandUploadProof = response.body()!!.is_proff_uploded

                    if (!flag.equals("uploadProof")) {

                        if (isErrandTripStarted == false && isErrandTripEnd == false && isErrandUploadProof == false) {
                            binding.apply {
                                tvCountText.visibility = View.GONE
                                btnENdTrip.visibility = View.GONE
                                btnTrip.visibility = View.VISIBLE
                                tvClickStartText.visibility = View.VISIBLE
                                btnSubmit.visibility = View.GONE

                                Log.e(TAG, "onCreate: " + binding.tvCountText.text.toString())

                                tvTripStatus.setText("Status : Pending")
                            }

                        }

                        else if (isErrandTripStarted == true && isErrandTripEnd == false && isErrandUploadProof == false) {

                            binding.apply {
                                btnTrip.visibility = View.GONE
                                btnENdTrip.visibility = View.GONE
                                linearTripDetails.visibility = View.VISIBLE
                                tvCountText.visibility = View.VISIBLE
                                tvClickStartText.visibility = View.GONE
                                linearTripEndDetails.visibility = View.GONE
                                btnUploadProof.visibility = View.VISIBLE


                            }

                            binding.apply {
                                dispatchedDetailLayout.visibility = View.VISIBLE
                                dispatchTripDetail.visibility = View.VISIBLE
                                dispatchDetailAfterEndTrip.visibility = View.GONE
                            }


                        }


                        else if (isErrandTripStarted == true && isErrandUploadProof == true && isErrandTripEnd == false) {
                            binding.apply {
                                tvCountText.visibility = View.GONE
                                btnENdTrip.visibility = View.VISIBLE
                                btnTrip.visibility = View.GONE
                                tvClickStartText.visibility = View.GONE
                                btnSubmit.visibility = View.GONE
                                btnUploadProof.visibility = View.GONE

                                Log.e(TAG, "onCreate: " + binding.tvCountText.text.toString())

                                tvTripStatus.setText("Status : Started")

                            }

                            binding.apply {

                                dispatchedDetailLayout.visibility = View.VISIBLE
                                dispatchTripDetail.visibility = View.GONE
                                dispatchDetailAfterEndTrip.visibility = View.VISIBLE
                                EndTripFieldslayout.visibility = View.GONE

                            }


                        }


                        else if (isErrandTripStarted == true && isErrandTripEnd == true && isErrandUploadProof == false) {
                            binding.apply {
                                tvCountText.visibility = View.GONE
                                btnENdTrip.visibility = View.GONE
                                btnTrip.visibility = View.GONE
                                tvClickStartText.visibility = View.GONE
                                btnSubmit.visibility = View.GONE
                                btnUploadProof.visibility = View.VISIBLE

                                Log.e(TAG, "onCreate: " + binding.tvCountText.text.toString())

                                tvTripStatus.setText("Status : End Trip")

                            }

                            binding.apply {
                                dispatchedDetailLayout.visibility = View.VISIBLE
                                dispatchTripDetail.visibility = View.GONE
                                dispatchDetailAfterEndTrip.visibility = View.VISIBLE
                            }


                        }


                        else if (isErrandTripStarted == true && isErrandTripEnd == true && isErrandUploadProof == true) {
                            binding.apply {
                                tvCountText.visibility = View.GONE
                                btnENdTrip.visibility = View.GONE
                                btnTrip.visibility = View.GONE
                                tvClickStartText.visibility = View.GONE
                                btnSubmit.visibility = View.VISIBLE
                                btnUploadProof.visibility = View.GONE

                                Log.e(TAG, "onCreate: " + binding.tvCountText.text.toString())

                                tvTripStatus.setText("Status : End Trip")

                            }

                            binding.apply {
                                dispatchedDetailLayout.visibility = View.VISIBLE
                                dispatchTripDetail.visibility = View.GONE
                                dispatchDetailAfterEndTrip.visibility = View.VISIBLE
                                EndTripFieldslayout.visibility = View.VISIBLE
                            }

                        }


                        else {
                            binding.apply {
                                tvCountText.visibility = View.GONE
                                btnENdTrip.visibility = View.GONE
                                btnTrip.visibility = View.GONE
                                tvClickStartText.visibility = View.GONE
                                btnUploadProof.visibility = View.GONE
                                btnSubmit.visibility = View.VISIBLE

                                Log.e(TAG, "onCreate: " + binding.tvCountText.text.toString())

                            }

                            binding.apply {
                                dispatchedDetailLayout.visibility = View.VISIBLE
                                dispatchTripDetail.visibility = View.GONE
                                dispatchDetailAfterEndTrip.visibility = View.VISIBLE
                            }
                        }

                    }

                    else {
                        binding.btnENdTrip.visibility = View.VISIBLE
                        binding.btnUploadProof.visibility = View.GONE
                    }


                    if (listData.size > 0){

                        var data = listData[0]


                        if (data.StartAt.isNotEmpty()){

                            val dateStr = data.StartAt
                            val secondsTimeer = Global.secondsBetween(dateStr)
                            println("Seconds between $dateStr and now: $secondsTimeer")

                            try {
                                if (data.EndAt.toString().isBlank() && data.StartAt.toString().isNotBlank()) { //2024_09_24 11:20:00
                                    if (data.EndAt.toString() != "foo" && data.StartAt.toString() != "foo") {
                                        running = true
                                        seconds = secondsTimeer.toLong()
                                    }
                                    Log.e("sec", seconds.toString())
                                } else if (data.EndAt.toString().isNotBlank() && data.StartAt.toString().isBlank()) {
                                    if (data.EndAt.toString() != "foo" && data.StartAt.toString() != "foo") {
                                        seconds = secondsTimeer.toLong()
                                        Log.e("sec", seconds.toString())
                                    }
                                }
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                            }


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

                                if (data.StartAt.isNotEmpty() && data.EndAt.isEmpty()){
                                    EndTripFieldslayout.visibility = View.GONE
                                    tvStartLocation12.setText(data.StartLocation)
                                    tvStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))

                                }

                                if (data.StartAt.isNotEmpty() && data.EndAt.isNotEmpty()){

                                    EndTripFieldslayout.visibility = View.VISIBLE

                                    tvStartLocation12.setText(data.StartLocation)
                                    tvStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))
                                    tvEndLocation.setText(data.EndLocation)
                                    tvEndTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }else{
                                    /*   tvStartTime.setText("NA")
                                       tvStartLocation12.setText("NA")
                                       tvEndLocation.setText("NA")
                                       tvEndTime.setText("NA")
   */
                                }


                            }

                            runTimer()

                        }
                    }

                }

                else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo call pickup trip detail--

    private fun callPickUpTripDetailsApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)


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

                                    val linearLayoutManager = LinearLayoutManager(this@ErrandDeliveryPersonActivity, LinearLayoutManager.HORIZONTAL, false)
                                    val adapter = PreviousImageViewAdapter(this@ErrandDeliveryPersonActivity, mArrayUriList, arrayOf(), arrayListOf())
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
                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



    //todo order detail images--
    private fun bindGetInspectionImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getInspectionImages(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    bindGETCameraImagesAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //todo bind image adapter data----
    private fun bindGETCameraImagesAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.inspectionViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.imageRecyclerView.layoutManager = linearLayoutManager
            binding.imageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.inspectionViewLayout.visibility = View.GONE

        }


    }


    //todo order detail images--

    var dispatchList = ArrayList<UploadedPictureModel.Data>()
    private fun bindGetDeliveryDispatchImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)
        if (globalDataWorkQueueList.is_errands == true){
            jsonObject1.addProperty("errand_id", globalDataWorkQueueList.DeliveryId)
        }else{
            jsonObject1.addProperty("errand_id", "")
        }

        val call: Call<UploadedPictureModel> = ApiClient().service.getDeliveryDispatchProofImage(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    dispatchList = response.body()!!.data


                    bindGETDispatchCameraImagesAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



    //todo bind image data----
    private fun bindGETDispatchCameraImagesAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.deliveryProofViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.rvDeliveryImage.layoutManager = linearLayoutManager
            binding.rvDeliveryImage.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {

            binding.deliveryProofViewLayout.visibility = View.GONE

        }


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

                        binding.apply {
                            tvDeliveryDetail.setText("Delivery Details  " + "( "+response.body()!!.data.size.toString()+" )")
                            deliveryDetailLayout.visibility = View.VISIBLE
                            tvDeliveryDetailNoDataFound.visibility = View.GONE
                            rvDeliveryDetail.visibility = View.VISIBLE
                        }

                        deliveryItemList_gl.clear()
                        deliveryItemList_gl.addAll(response.body()!!.data)

                        deliveryDetailAdapter.submitList(deliveryItemList_gl)

                        setupDeliveryDetailRecyclerview()


                    }else{
                        setupDeliveryDetailRecyclerview()
                        binding.apply {
                            deliveryDetailLayout.visibility = View.GONE
                            tvDeliveryDetailNoDataFound.visibility = View.VISIBLE
                            rvDeliveryDetail.visibility = View.GONE
                        }

                    }

                } else {

                    binding.apply {
                        deliveryDetailLayout.visibility = View.GONE
                        loadingView.stop()
                        loadingBackFrame.visibility = View.GONE
                    }

                }
            }

            override fun onFailure(call: Call<DeliveryItemListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupDeliveryDetailRecyclerview() = binding.rvDeliveryDetail.apply {
        adapter = deliveryDetailAdapter
        layoutManager = LinearLayoutManager(this@ErrandDeliveryPersonActivity)
        deliveryDetailAdapter.notifyDataSetChanged()
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

        bottomSheetDialog.setCancelable(false) // Prevents the dialog from being canceled
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        bottomSheetDialog.show()

        recyclerViewMoreImageLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.recyclerViewMoreImageLayout)!!
        proofImageRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.proofImageRecyclerView)!!
        clickNewImageLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.clickNewImageLayout)!!
        statusLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.statusLayout)!!
        var tv_employee_name = bottomSheetDialog.findViewById<TextView>(R.id.tv_employee_name)!!

        tv_employee_name.setText("Proof")

        bindingBottomSheet.acStatus.visibility = View.GONE
        bindingBottomSheet.ivCloseDialog.visibility = View.VISIBLE

        var statusList = arrayListOf<String>("Not Good", "Good")
        var adapter = ArrayAdapter<String>(this, R.layout.drop_down_item_textview, statusList)
        bindingBottomSheet.acStatus.setAdapter(adapter)

        bindingBottomSheet.acStatus.setOnItemClickListener { adapterView, view, i, l ->
            bindingBottomSheet.acStatus.setText(statusList[i])

//            status = statusList[i]
        }

        bindingBottomSheet.ivCloseDialog.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.ivRvCloseDialog.setOnClickListener {
            mArrayUriList.clear()
            pdfurilist.clear()
            bottomSheetDialog.dismiss()
        }


        bindingBottomSheet.linearAddImage.setOnClickListener {

            /*  if (allPermissionsGranted()) {

              } else {
                  ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
              }*/
            dispatchTakePictureIntent()
        }


        bindingBottomSheet.tvAddMoreImages.setOnClickListener {
            dispatchTakePictureIntent()
            /*if (allPermissionsGranted()) {

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }*/
        }


        bindingBottomSheet.btnConfirm.setOnClickListener {
            if (pdfurilist.size > 0) {
                callUploadProofApi(bottomSheetDialog, bindingBottomSheet.loadingback, bindingBottomSheet.loadingView)
            }else{
                Global.errormessagetoast(this, "Upload Image!")
            }
        }

    }


    //todo start trip for delivery person--
    private fun callUploadProofApi(bottomSheetDialog: BottomSheetDialog, loadingback: FrameLayout, loadingView: LoadingView) {
        loadingback.visibility = View.VISIBLE
        loadingView.start()

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        builder.addFormDataPart("OrderID", globalDataWorkQueueList.OrderRequest!!.id.toString())
        builder.addFormDataPart("DeliveryNote", "")//globalDataWorkQueueList.id //todo send blank right now but not blank always
        builder.addFormDataPart("Status", "")
        builder.addFormDataPart("UploadBy", Prefs.getString(Global.Employee_Code))
        builder.addFormDataPart("is_return", globalDataWorkQueueList.is_return.toString())

        if (globalDataWorkQueueList.is_errands == true){
            builder.addFormDataPart("errand_id", globalDataWorkQueueList.DeliveryId)
        }else{
            builder.addFormDataPart("errand_id", "")
        }

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
        jsonObject.put("DeliveryNote", "")
        jsonObject.put("Status", "")
        jsonObject.put("is_return", globalDataWorkQueueList.is_return)
        if (globalDataWorkQueueList.is_errands == true){
            jsonObject.put("errand_id", globalDataWorkQueueList.DeliveryId)
        }else{
            jsonObject.put("errand_id", "")
        }

        val filesArray = JSONArray()
        if (pdfurilist.size > 0) {
            for (i in pdfurilist.indices) {
                val file: File = File(pdfurilist[i])
                filesArray.put( file.name)
                jsonObject.put("Attachment", filesArray)
            }
        }

        Log.e("payload", jsonObject.toString())

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.uploadDeliveryPersonProof(requestBody)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {

                    loadingback.visibility = View.GONE
                    loadingView.stop()

                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@ErrandDeliveryPersonActivity, "Picture Uploaded Success")
                    bottomSheetDialog.dismiss()

//                    binding.btnSubmit.visibility = View.VISIBLE

                    //todo new flow
                    binding.apply{
                        linearTripDetails.visibility = View.VISIBLE
                        btnENdTrip.visibility = View.VISIBLE
                        tvCountText.visibility = View.VISIBLE
                        tvClickStartText.visibility = View.GONE
                        linearTripEndDetails.visibility = View.GONE
                        uploadProofLayout.visibility = View.GONE
                        btnUploadProof.visibility = View.GONE
                        btnSubmit.visibility = View.GONE

                        tvTripStatus.setText("Status : Started" )

                    }

                    var jsonObject = JsonObject()
                    jsonObject.addProperty("id", orderID)
                    viewModel.callWorkQueueDetailApi(jsonObject)
                    bindWorkQueueDetail("uploadProof")

                    callTripDetailsApi("uploadProof")

                    binding.apply {
                        dispatchTripDetail.visibility = View.GONE
                        dispatchDetailAfterEndTrip.visibility = View.VISIBLE
                        EndTripFieldslayout.visibility = View.GONE
                    }


                } else {
                    binding.btnSubmit.visibility = View.GONE
                    loadingback.visibility = View.GONE
                    loadingView.stop()
                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                loadingback.visibility = View.GONE
                loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@ErrandDeliveryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


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
    }


    //todo
    private fun bindCameraImagesAdapter(mArrayUriList: ArrayList<Uri>) {

        if (mArrayUriList.size > 0) {
            recyclerViewMoreImageLayout.visibility = View.VISIBLE
            clickNewImageLayout.visibility = View.GONE
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = UploadImageListAdapter(this, mArrayUriList, arrayOf(), pdfurilist,"DeliveryPerson")
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


    //todo for get device location--
    private fun givePermission(type: String) {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION) //, Manifest.permission.CALL_PHONE
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // do you work now
                        Log.e(TAG, "onComplete: " + "givepermission")
                        getMyCurrentLocation(type)
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // In Android 14 and above, guide user to settings
                            goToAppSettings()
                        }
                        // permission is denied permenantly, navigate user to app settings
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }


    private fun goToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    lateinit var client: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    private fun getMyCurrentLocation(type: String) {
        // Initialize Location manager
        val locationManager = this@ErrandDeliveryPersonActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.lastLocation?.addOnCompleteListener { task ->
                // Initialize location
                val location: Location? = task.result
                // Check condition
                if (location != null) {
                    Log.e(TAG, "onComplete: locationNotNull")
                    // When location result is not null, set latitude
                    val geocoder = Geocoder(this, Locale.getDefault())
                    var addresses: List<Address>? = null

                    try {
                        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1) // Here 1 represents max location result to return, as per documentation recommended 1 to 5
                        val address = addresses?.get(0)?.getAddressLine(0) // If any additional address line present, check with max available address lines by getMaxAddressLineIndex()
                        val city = addresses?.get(0)?.locality
                        val state = addresses?.get(0)?.adminArea
                        val country = addresses?.get(0)?.countryName
                        val postalCode = addresses?.get(0)?.postalCode
                        val knownName = addresses?.get(0)?.featureName

                        Log.e(TAG, "onComplete: Call Api" + address)
                        if (type == "StartTrip") {
                            startTripApiCall(location?.latitude!!, location?.longitude!!, address)
                        }
                        else{
                            endTripApiCall(location?.latitude!!, location?.longitude!!, address)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e(TAG, "onComplete: locationNull")
                    // When location result is null, initialize location request
                    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                        .setMinUpdateIntervalMillis(1000L)
                        .setMaxUpdates(1)
                        .build()

                    // Initialize location callback
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            // Initialize location
                            val location1 = locationResult.lastLocation
                            val geocoder = Geocoder(this@ErrandDeliveryPersonActivity, Locale.getDefault())
                            var addresses: List<Address>? = null

                            try {
                                addresses = geocoder.getFromLocation(location1!!.latitude, location1.longitude, 1) // Here 1 represent max location result to return, as per documentation recommended 1 to 5
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            val address = addresses?.get(0)?.getAddressLine(0) // If any additional address line present, check with max available address lines by getMaxAddressLineIndex()
                            val city = addresses?.get(0)?.locality
                            val state = addresses?.get(0)?.adminArea
                            val country = addresses?.get(0)?.countryName
                            val postalCode = addresses?.get(0)?.postalCode
                            val knownName = addresses?.get(0)?.featureName
                            Log.e(TAG, "onComplete: Call Api123" + address)

                            if (type == "StartTrip") {
                                startTripApiCall(location?.latitude!!, location?.longitude!!, address)
                            }else{
                                endTripApiCall(location?.latitude!!, location?.longitude!!, address)
                            }

                        }
                    }

                    // Request location updates
                    client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                }
            }
        } else {
            // When location service is not enabled, open location setting
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }



    //todo start trip for delivery person--
    private fun startTripApiCall(latitude: Double, longitude: Double, address: String?) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("WorkQueueId",globalDataWorkQueueList.id)
        jsonObject1.addProperty("DeliveryAssigned",globalDataWorkQueueList.DeliveryAssigned)
        jsonObject1.addProperty("DeliveryNote","")
        jsonObject1.addProperty("OrderID",globalDataWorkQueueList.OrderRequest?.id)
        jsonObject1.addProperty("StartAt",Global.getTodayDateDashFormatReverse() + " "+ Global.getfullformatCurrentTime())
        jsonObject1.addProperty("StartLocation",address)//address
        jsonObject1.addProperty("EndAt","")
        jsonObject1.addProperty("EndLocation","")
        jsonObject1.addProperty("DepositedBy", Prefs.getString(Global.Employee_Code, ""))
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)

        if (globalDataWorkQueueList.is_errands == true){
            jsonObject1.addProperty("errand_id", globalDataWorkQueueList.DeliveryId)
        }else{
            jsonObject1.addProperty("errand_id", "")
        }

        val call: Call<RouteListModel> = ApiClient().service.startTripForDeliveryPerson(jsonObject1)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(call: Call<RouteListModel?>, response: Response<RouteListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    binding.apply {
                        linearTripDetails.visibility = View.VISIBLE
                        btnENdTrip.visibility = View.GONE
                        tvCountText.visibility = View.VISIBLE
                        tvClickStartText.visibility = View.GONE
                        linearTripEndDetails.visibility = View.GONE
                        btnUploadProof.visibility = View.VISIBLE

                        tvTripStatus.setText("Status : Started" )

                    }

                    running = true

//                    startTimer()

                    Log.e("data", response.body()!!.data.toString())

                    binding.apply {
                        dispatchedDetailLayout.visibility = View.VISIBLE
                        dispatchTripDetail.visibility = View.VISIBLE
                        dispatchDetailAfterEndTrip.visibility = View.GONE
                    }

//                    showItemListDialogBottomSheetDialog()

                    callTripDetailsApi("StartTrip")


                } else {

                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<RouteListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
            }
        })


    }


    //todo end trip
    private fun endTripApiCall(latitude: Double, longitude: Double, address: String?) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("id",globalDataWorkQueueList.id)
        jsonObject1.addProperty("WorkQueueId",globalDataWorkQueueList.id)
        jsonObject1.addProperty("DeliveryAssigned",globalDataWorkQueueList.DeliveryAssigned)
        jsonObject1.addProperty("DeliveryNote","")
        jsonObject1.addProperty("OrderID",globalDataWorkQueueList.OrderRequest?.id)
        jsonObject1.addProperty("EndAt",Global.getTodayDateDashFormatReverse() + " "+ Global.getfullformatCurrentTime())//binding.tvCountText.text.toString()
        jsonObject1.addProperty("EndLocation",address)//
        jsonObject1.addProperty("DepositedBy", Prefs.getString(Global.Employee_Code, ""))
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)

        if (globalDataWorkQueueList.is_errands == true){
            jsonObject1.addProperty("errand_id", globalDataWorkQueueList.DeliveryId)
        }else{
            jsonObject1.addProperty("errand_id", "")
        }

        val call: Call<RouteListModel> = ApiClient().service.finishTripForDeliveryPerson(jsonObject1)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(call: Call<RouteListModel?>, response: Response<RouteListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()


//                    stopTimer()
                    running = false

                    binding.apply {
                        tvCountText.visibility = View.GONE
                        btnTrip.visibility = View.INVISIBLE
                        btnENdTrip.visibility = View.GONE
                        btnTrip.visibility = View.GONE
                        linearTripEndDetails.visibility = View.VISIBLE
                        tvClickStartText.visibility = View.GONE
                        btnUploadProof.visibility = View.GONE

                        binding.btnSubmit.visibility = View.VISIBLE

                        Log.e(TAG, "onCreate: "+binding.tvCountText.text.toString() )

//                        showItemListDialogBottomSheetDialog()
                        tvTripStatus.setText("Status : Ended" )

                        callTripDetailsApi("EndTrip")

                    }


                    Log.e("data", response.body()!!.data.toString())

                    binding.apply {
                        dispatchTripDetail.visibility = View.GONE
                        dispatchDetailAfterEndTrip.visibility = View.VISIBLE
                        EndTripFieldslayout.visibility = View.VISIBLE
                    }


                } else {

                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@ErrandDeliveryPersonActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<RouteListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
            }
        })


    }


    //todo for attchment--

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



    private var isRunning = false
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    private fun startTimer() {
        isRunning = true
        binding.btnTrip.text = "End trip"
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        isRunning = false
        binding.btnTrip.text = "Start trip"
        handler.removeCallbacks(timerRunnable)
    }


    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime += 1000
                val hours = (elapsedTime / (1000 * 60 * 60)).toInt()
                val minutes = ((elapsedTime / (1000 * 60)) % 60).toInt()
                val seconds = ((elapsedTime / 1000) % 60).toInt()
                binding.tvCountText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private var seconds: Long = 0

    // Is the stopwatch running?
    private var running = false
    private fun runTimer() {

        // Get the text view.

        // Creates a new Handler
        val handler = Handler()

        handler.post(object : Runnable {
            override fun run() {
                val hours: Long = seconds / 3600
                val minutes: Long = seconds % 3600 / 60
                val secs: Long = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                // Set the text view text.
                binding.tvCountText.text = time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }
}