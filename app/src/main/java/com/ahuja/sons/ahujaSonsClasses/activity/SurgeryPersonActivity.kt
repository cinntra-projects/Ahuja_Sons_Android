package com.ahuja.sons.ahujaSonsClasses.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
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
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.Interface.LocationPermissionHelper
import com.ahuja.sons.ahujaSonsClasses.adapter.PreviousImageViewAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.SurgeryNameListAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.UploadImageListAdapter
import com.ahuja.sons.ahujaSonsClasses.model.RouteListModel
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonNameListModel
import com.ahuja.sons.ahujaSonsClasses.model.TripDetailModel
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivitySurgeryPersonBinding
import com.ahuja.sons.databinding.BottomSheetSelectDateTimeBinding
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

class SurgeryPersonActivity : AppCompatActivity() {
    lateinit var binding: ActivitySurgeryPersonBinding
    lateinit var pagerAdapter: ViewPagerAdapter

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

    var orderID = ""
    
    companion object{
        private const val TAG = "SurgeryPersonActivity"
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurgeryPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpViewModel()

        checkAndRequestPermissions()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        client = LocationServices.getFusedLocationProviderClient(this)

        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
        }

        orderID = intent.getStringExtra("id")!!

        binding.loadingBackFrame.visibility = View.GONE
        binding.loadingView.stop()

        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("")


        hideAndShowViews()


        binding.chipReschedule.setOnClickListener {
            showRescheduleDialogBottomSheetDialog()

        }


        binding.endTripChipSurgery.setOnClickListener {
//            givePermission("EndTrip")
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



        binding.chipStart.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else {
                getMyCurrentLocation("StartTrip")
            }
//            givePermission("StartTrip")
        }


        binding.UploadProofChipSurgery.setOnClickListener {
            showItemListDialogBottomSheetDialog()
        }


        binding.submitSurgeryChip.setOnClickListener {
            callSurgeryPersonSubmitApi()
        }


    }

    //todo calling reschedule api--
    private fun callSurgeryPersonSubmitApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("SurgeryBy", Prefs.getString(Global.Employee_Code,""))

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.surgeryPersonSubmitApi(jsonObject1)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@SurgeryPersonActivity, response.body()!!.message)
                    onBackPressed()
                    finish()

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
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


        binding.dispatchUpArrow.setOnClickListener {
            binding.statusLayout.visibility = View.GONE
            binding.dispatchTripDetail.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.GONE
            binding.dispatchDownArrow.visibility = View.VISIBLE
        }

        binding.dispatchDownArrow.setOnClickListener {
            binding.statusLayout.visibility = View.VISIBLE
            binding.dispatchTripDetail.visibility = View.VISIBLE
            binding.dispatchDownArrow.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.VISIBLE
        }


        binding.apply {
            surgeryDownArrow.setOnClickListener {
                tvSurgeryStatus.visibility = View.GONE
                surgeryAllDetailsLayout.visibility = View.GONE
                surgeryDownArrow.visibility = View.GONE
                surgeryUpArrow.visibility = View.VISIBLE

                startBtnClickTextView.visibility = View.GONE


            }

            surgeryUpArrow.setOnClickListener {
                surgeryDownArrow.visibility = View.VISIBLE
                surgeryUpArrow.visibility = View.GONE

                if (surgeryAllDetailsLayout.isVisible){
                    surgeryAllDetailsLayout.visibility = View.VISIBLE
                }else{
                    surgeryAllDetailsLayout.visibility = View.GONE
                }

                if (startBtnClickTextView.isVisible){
                    startBtnClickTextView.visibility = View.VISIBLE
                }else{
                    startBtnClickTextView.visibility = View.GONE
                }

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

                        //todo calling order trip api fr dispatch details

                        callDispatchDetailsApi("")

                        callSurgeryPersonDetailApi("")

                        bindGetDeliveryDispatchImages()

                        getSurgeryUploadProofAPi()


                    }


                }


            }

        ))
    }


    //todo set default data here---
    private fun setDefaultData(modelData: AllWorkQueueResponseModel.Data, fromWhere: String) {

    /*    if (Prefs.getString(Global.SurgeryPersonService) == "StartSurgery"){

            binding.tvSurgeryStatus.setText("Status : Started")
            binding.counterBtnLayoutSurgery.visibility = View.GONE
            binding.surgeryPersonSubmitBtnLayout.visibility = View.GONE
            binding.uploadChipGroupSurgery.visibility = View.GONE
            binding.endBtnLayoutSurgery.visibility = View.VISIBLE

        }
        else if (Prefs.getString(Global.SurgeryPersonService) == "EndSurgery"){
            binding.tvSurgeryStatus.setText("Status : Ended")
            binding.counterBtnLayoutSurgery.visibility = View.GONE
            binding.surgeryPersonSubmitBtnLayout.visibility = View.GONE
            binding.endBtnLayoutSurgery.visibility = View.VISIBLE
            binding.uploadChipGroupSurgery.visibility = View.VISIBLE
            binding.endTripChipGroupSurgery.visibility = View.GONE
        }
        else if (Prefs.getString(Global.SurgeryPersonService) == "UploadProof"){
            binding.counterBtnLayoutSurgery.visibility = View.GONE
            binding.surgeryPersonSubmitBtnLayout.visibility = View.VISIBLE
            binding.uploadChipGroupSurgery.visibility = View.GONE
            binding.endBtnLayoutSurgery.visibility = View.GONE
        }else{
            binding.counterBtnLayoutSurgery.visibility = View.VISIBLE
            binding.surgeryPersonSubmitBtnLayout.visibility = View.GONE
            binding.uploadChipGroupSurgery.visibility = View.GONE
            binding.endBtnLayoutSurgery.visibility = View.GONE
        }
        */

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
                    var intent = Intent(this@SurgeryPersonActivity, ItemDetailActivity::class.java)
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

                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
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


    //todo delovery dispatch upload proof api here---

    var dispatchList = ArrayList<UploadedPictureModel.Data>()
    private fun bindGetDeliveryDispatchImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getDeliveryDispatchProofImage(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    dispatchList = response.body()!!.data


                    bindGETDispatchCameraImagesAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }


    //todo bind dispatch image adater data----
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

                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
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


    //todo reschedule dialog bottom sheet----
    private fun showRescheduleDialogBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetSelectDateTimeBinding = BottomSheetSelectDateTimeBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())


        bottomSheetDialog.show()

        bindingBottomSheet.ivCross.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        bindingBottomSheet.edtDate.setOnClickListener {
            Global.selectDate(this, bindingBottomSheet.edtDate)
        }


        bindingBottomSheet.edtTime.setOnClickListener {

            Global.selectTime(this, bindingBottomSheet.edtTime)
        }

        bindingBottomSheet.btnConfirm.setOnClickListener {
            callRescheduleApi(bottomSheetDialog, bindingBottomSheet.edtDate.text.toString(), bindingBottomSheet.edtTime.text.toString())

        }


    }


    //todo calling reschedule api--
    private fun callRescheduleApi(bottomSheetDialog: BottomSheetDialog, edtDate: String, edtTime: String) {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("previousSuegeryId", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("StartDate", Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(edtDate))
        jsonObject1.addProperty("StartTime", edtTime)
        jsonObject1.addProperty("CreatedBy", Prefs.getString(Global.Employee_SalesEmpCode))

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.rescheduleSurgeryApi(jsonObject1)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())
                    bottomSheetDialog.dismiss()
                    finish()
                    onBackPressed()

                } else {

                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
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


    lateinit var client: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    private fun getMyCurrentLocation(type: String) {
        // Initialize Location manager
        val locationManager = this@SurgeryPersonActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
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
                            val geocoder = Geocoder(this@SurgeryPersonActivity, Locale.getDefault())
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
        jsonObject1.addProperty("OrderID",globalDataWorkQueueList.OrderRequest?.id)
        jsonObject1.addProperty("StartAt",Global.getTodayDateDashFormatReverse() + " "+ Global.getfullformatCurrentTime())
        jsonObject1.addProperty("StartLocation",address)//"Ghaziabad, Uttar Pradesh 201009, India"
        jsonObject1.addProperty("EndAt","")
        jsonObject1.addProperty("EndLocation","")
        jsonObject1.addProperty("SurgeryBy", Prefs.getString(Global.Employee_Code, ""))


        val call: Call<RouteListModel> = ApiClient().service.startSurgery(jsonObject1)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(call: Call<RouteListModel?>, response: Response<RouteListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    Prefs.putString(Global.SurgeryPersonService, "StartSurgery")

                    /*binding.apply {
                        counterBtnLayoutSurgery.visibility = View.GONE
                        uploadChipGroupSurgery.visibility = View.GONE
                        endBtnLayoutSurgery.visibility = View.VISIBLE
                        endTripChipGroupSurgery.visibility = View.VISIBLE
                        surgeryDetailCardView.visibility = View.VISIBLE
                        tvSurgeryStatus.setText("Status : Started")
                        startBtnClickTextView.visibility = View.VISIBLE

                        tvSurgeryStartTimer.setText(Global.getTodayDate() +" at " + Global.getfullformatCurrentTime())

                    }*/


                    Log.e("data", response.body()!!.data.toString())

//                    showItemListDialogBottomSheetDialog()

                    callSurgeryPersonDetailApi("StartSurgery")

                } else {

                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.errors);

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
        jsonObject1.addProperty("OrderID",globalDataWorkQueueList.OrderRequest?.id)
        jsonObject1.addProperty("EndAt",Global.getTodayDateDashFormatReverse() + " "+ Global.getfullformatCurrentTime())//binding.tvCountText.text.toString()
        jsonObject1.addProperty("EndLocation",address)//"Ghaziabad, Uttar Pradesh 201009, India"
        jsonObject1.addProperty("SurgeryBy", Prefs.getString(Global.Employee_Code, ""))

        val call: Call<RouteListModel> = ApiClient().service.finishSurgery(jsonObject1)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(call: Call<RouteListModel?>, response: Response<RouteListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    Prefs.putString(Global.SurgeryPersonService, "EndSurgery")

                    binding.apply {

                      /*  endTripChipGroupSurgery.visibility = View.GONE
                        uploadChipGroupSurgery.visibility = View.VISIBLE

                        tvSurgeryStatus.setText("Status : Ended")*/


                    }

                    callSurgeryPersonDetailApi("EndSurgery")

                    Log.e("data", response.body()!!.data.toString())


                } else {

                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.errors)

                }
            }

            override fun onFailure(call: Call<RouteListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
            }
        })


    }


    //todo calling surgery person detail api here---

    var surgeryDetailData_gl:  ArrayList<SurgeryPersonNameListModel.Data> = ArrayList<SurgeryPersonNameListModel.Data>()
    private fun callSurgeryPersonDetailApi(flag: String) {
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

                            if (data[0].isSurgeryStarted == true && data[0].isSurgeryEnd == false && data[0].isSurgeryProofUp == false){
                                tvSurgeryStatus.setText("Status : Started")
                                surgeryDetailCardView.visibility = View.VISIBLE
                                surgeryAllDetailsLayout.visibility = View.GONE

                                counterBtnLayoutSurgery.visibility = View.GONE
                                surgeryPersonSubmitBtnLayout.visibility = View.GONE
                                endBtnLayoutSurgery.visibility = View.VISIBLE
                                uploadChipGroupSurgery.visibility = View.VISIBLE
                                endTripChipGroupSurgery.visibility = View.GONE
                                startBtnClickTextView.visibility = View.VISIBLE

//                                tvSurgeryStartTimer.setText(Global.getTodayDate() +" at " + Global.getfullformatCurrentTime())
                                tvSurgeryStartTimer.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data[0].StartAt))
                            }

                            else if (data[0].isSurgeryStarted == true && data[0].isSurgeryEnd == false && data[0].isSurgeryProofUp == true){
                                surgeryDetailCardView.visibility = View.VISIBLE
                                surgeryAllDetailsLayout.visibility = View.VISIBLE
                                surgeryTimeDetails.visibility = View.VISIBLE
                                startBtnClickTextView.visibility = View.GONE

                                tvSurgeryStatus.setText("Status : Started")
                                counterBtnLayoutSurgery.visibility = View.GONE
                                surgeryPersonSubmitBtnLayout.visibility = View.GONE
                                endBtnLayoutSurgery.visibility = View.VISIBLE
                                uploadChipGroupSurgery.visibility = View.GONE
                                endTripChipGroupSurgery.visibility = View.VISIBLE


                                if (data[0].StartAt.isNotEmpty()){
                                    tvSurgeryStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data[0].StartAt))
                                }else{
                                    tvSurgeryStartTime.setText("NA")
                                }

                                if (data[0].EndAt.isNotEmpty()){
                                    tvSurgeryEndTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data[0].EndAt))
                                }else{
                                    endSurgeryTimeLayout.visibility = View.GONE
                                    tvSurgeryEndTime.setText("NA")

                                }
                                if (data[0].StartAt.isNotEmpty() && data[0].EndAt.isNotEmpty()){
                                    tvSurgeryDuration.setText(Global.durationGet(data[0].StartAt, data[0].EndAt))
                                }
                                else{
                                    surgeryDurationLayout.visibility = View.GONE
                                    tvSurgeryDuration.setText("00:00:00")
                                }
                                tvCRSNo.setText(data[0].NoOfCSRRequired)
                            }


                            else if (data[0].isSurgeryStarted == true && data[0].isSurgeryEnd == true && data[0].isSurgeryProofUp == false){
                                surgeryDetailCardView.visibility = View.VISIBLE
                                surgeryAllDetailsLayout.visibility = View.VISIBLE
                                surgeryTimeDetails.visibility = View.VISIBLE
                                startBtnClickTextView.visibility = View.GONE

                                tvSurgeryStatus.setText("Status : Ended")
                                counterBtnLayoutSurgery.visibility = View.GONE
                                surgeryPersonSubmitBtnLayout.visibility = View.GONE
                                endBtnLayoutSurgery.visibility = View.VISIBLE
                                uploadChipGroupSurgery.visibility = View.VISIBLE
                                endTripChipGroupSurgery.visibility = View.GONE

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
                            }


                            else if (data[0].isSurgeryStarted == true && data[0].isSurgeryEnd == true && data[0].isSurgeryProofUp == true){
                                surgeryDetailCardView.visibility = View.VISIBLE
                                surgeryAllDetailsLayout.visibility = View.VISIBLE
                                startBtnClickTextView.visibility = View.GONE
                                surgeryTimeDetails.visibility = View.VISIBLE

                                tvSurgeryStatus.setText("Status : Ended")

                                counterBtnLayoutSurgery.visibility = View.GONE
                                surgeryPersonSubmitBtnLayout.visibility = View.VISIBLE
                                uploadChipGroupSurgery.visibility = View.GONE
                                endBtnLayoutSurgery.visibility = View.GONE
                                endSurgeryTimeLayout.visibility = View.VISIBLE


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

                            }

                            else if (data[0].isSurgeryStarted == false && data[0].isSurgeryEnd == false && data[0].isSurgeryProofUp == false){
                                surgeryDetailCardView.visibility = View.GONE
                                surgeryAllDetailsLayout.visibility = View.GONE
                                startBtnClickTextView.visibility = View.GONE

                                counterBtnLayoutSurgery.visibility = View.VISIBLE
                                surgeryPersonSubmitBtnLayout.visibility = View.GONE
                                uploadChipGroupSurgery.visibility = View.GONE
                                endBtnLayoutSurgery.visibility = View.GONE

                                tvSurgeryStatus.setText("Status : Yet to Start")
                            }


                            val innerAdapter = SurgeryNameListAdapter(data)
                            rvSurgeryPersonListName.layoutManager = LinearLayoutManager(this@SurgeryPersonActivity, LinearLayoutManager.VERTICAL, false)
                            rvSurgeryPersonListName.adapter = innerAdapter
                            innerAdapter.notifyDataSetChanged()

                        }

                    }
                    else{

                        binding.loadingBackFrame.visibility = View.GONE
                        binding.loadingView.stop()
                        binding.surgeryDetailCardView.visibility = View.GONE
                        val innerAdapter = SurgeryNameListAdapter(ArrayList())
                        innerAdapter.notifyDataSetChanged()

                    }
                }

                else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<SurgeryPersonNameListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    //todo call trip detail--
    private fun callDispatchDetailsApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

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
                                    dispatchEndTimeLayout.visibility = View.VISIBLE
                                    tvTripStatus.setText("Status : Ended")
                                    tvDispatchEndLocation.setText(data.EndLocation)
                                    tvEndTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }else{
                                    tvTripStatus.setText("Status : Started")
                                    dispatchEndTimeLayout.visibility = View.GONE
                                    tvDispatchEndLocation.setText("NA")
                                    tvEndTripTime.setText("NA")

                                }


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
                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
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

        recyclerViewMoreImageLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.recyclerViewMoreImageLayout)!!
        proofImageRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.proofImageRecyclerView)!!
        clickNewImageLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.clickNewImageLayout)!!
        statusLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.statusLayout)!!
        var tv_employee_name = bottomSheetDialog.findViewById<TextView>(R.id.tv_employee_name)!!


        bindingBottomSheet.acStatus.visibility = View.GONE

        bindingBottomSheet.ivCloseDialog.visibility = View.VISIBLE

        bindingBottomSheet.ivRvCloseDialog.setOnClickListener {
            mArrayUriList.clear()
            pdfurilist.clear()
            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.ivCloseDialog.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.btnConfirm.setOnClickListener {

            bottomSheetDialog.dismiss()
        }

        bindingBottomSheet.linearAddImage.setOnClickListener {
            dispatchTakePictureIntent()
            /*if (allPermissionsGranted()) {

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }*/

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

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.uploadSurgeryProof(requestBody)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {

                    loadingback.visibility = View.GONE
                    loadingView.stop()

                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@SurgeryPersonActivity, "Picture Uploaded Success")
                    bottomSheetDialog.dismiss()

                   /* binding.surgeryPersonSubmitBtnLayout.visibility = View.VISIBLE
                    binding.endBtnLayoutSurgery.visibility = View.GONE*/

                    var jsonObject = JsonObject()
                    jsonObject.addProperty("id", orderID)
                    viewModel.callWorkQueueDetailApi(jsonObject)
                    bindWorkQueueDetail("uploadProof")

                    Prefs.putString(Global.SurgeryPersonService, "UploadProof")

                    callSurgeryPersonDetailApi("EndSurgery")

                } else {
                    binding.surgeryPersonSubmitBtnLayout.visibility = View.GONE
                    loadingback.visibility = View.GONE
                    loadingView.stop()
                    Global.warningmessagetoast(this@SurgeryPersonActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                loadingback.visibility = View.GONE
                loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryPersonActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

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
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            false
        } else {
            true
        }
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


    //todo for attchment--
    private fun allPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
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