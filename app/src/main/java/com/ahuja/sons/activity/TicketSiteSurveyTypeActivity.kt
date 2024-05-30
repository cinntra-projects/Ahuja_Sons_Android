package com.ahuja.sons.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ticketItemAdapter.*
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityTicketSiteSurveyTypeBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.IncapableCause
import com.zhihu.matisse.internal.entity.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class TicketSiteSurveyTypeActivity : AppCompatActivity() {

    lateinit var binding : ActivityTicketSiteSurveyTypeBinding
    lateinit var viewModel: MainViewModel
    var dataModel = ItemAllListResponseModel.DataXXX()
    var mArrayUriList: ArrayList<Uri> = ArrayList()
    var mSelectedList: ArrayList<Uri> = ArrayList()
    var path: ArrayList<String> = ArrayList()
    var index = 0;
    var locationContent = ""
    var contentLength = ""
    var contentWidth = ""
    var contentHeight = ""
    var itemName = ""
    var itemQty = ""
    var itemDistance = ""
    var localList = mutableListOf<SpareCustomModel>()
    lateinit var availabilityNoAdapter: AddAvailabilityNoAdapter
    lateinit var addAreaItemsAdapter: AddAreaItemsAdapter
    private val REQUEST_CODE_CHOOSE = 1000
    var ticketData = TicketData()

    private val REQUEST_CODE = 1

    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    private  val REQUEST_CODE_PERMISSIONS = 10
    lateinit var fileUri: Uri

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketSiteSurveyTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

//        dataModel = intent.getSerializableExtra("SiteSurvey")!! as ItemAllListResponseModel.DataXXX

        binding.loadingback.visibility = View.GONE

        ticketData = intent.getSerializableExtra("ticketData")!! as TicketData


        if (ticketData != null){
            if (ticketData.AssignToDetails.size > 0){
                binding.edtName.setText(ticketData.AssignToDetails[0].firstName + " " + ticketData.AssignToDetails[0].lastName)
                binding.edtCustomerName.setText(ticketData.BusinessPartner[0].CardName)
                binding.edtCustomerNumber.setText(ticketData.BusinessPartner[0].Phone1)
            }
        }

        //todo HERE code for Add Availability items--
        availabilityNoAdapter = AddAvailabilityNoAdapter(this, mutableListOf())

        bindAvailabilityItemAdapter()

        binding.ivAddAvailabilityItems.setOnClickListener {
            val newItem = AvailabilityCustomModel.Availability(
                ItemName = itemName,
                ItemQty = itemQty,
                ItemDistance = itemDistance
            )
            availabilityNoAdapter.addItem(newItem)
            itemQty = ""
            itemDistance = ""
        }


        //todo HERE code for Add Area Item---
        addAreaItemsAdapter = AddAreaItemsAdapter(this, mutableListOf())

        bindAreaItemAdapter()

        binding.ivAddAreaItems.setOnClickListener {
            val newItem = AreaCustomModel.Area(
                Location =  locationContent,
                Length = contentLength,
                Width = contentWidth,
                Height = contentHeight)
            addAreaItemsAdapter.addItem(newItem)
            locationContent = ""
            contentLength = ""
        }


        //todo submit data---
        binding.submitBtn.setOnClickListener {

            if (validation( binding.edtTotalFloorBuilding.text.toString(),binding.edtSourceWater.text.toString(), binding.edtOverheadTankVolume.text.toString(),
                    binding.edtTankCleaningCapacity.text.toString(),binding.edtTankFillingFrequency.text.toString(),binding.edtTotalFloor.text.toString(), binding.edtNoShifts.text.toString(),
                    binding.edtWorkTimeShifts.text.toString(), binding.edtTankHeight.text.toString(), binding.edtNoOfEmployee.text.toString(), binding.edtNoOfVisitors.text.toString(),
                    binding.edtNoOfPantriesFloor.text.toString(),binding.edtElectricityPoint.text.toString(),binding.edtPowerBackUpCapacity.text.toString(),
                    binding.edtTDSRawWater.text.toString(),binding.edtChlorinatedWater.text.toString(), binding.edtwaterPressure.text.toString(), binding.edtLevelPHValue.text.toString(),
                    binding.edtDispensersFloor.text.toString(),binding.edtExistingWater.text.toString(), binding.edtDailyConsumption.text.toString(),
                    binding.edtCapacityWaterBottle.text.toString(), binding.edtExistingWaterBottleValue.text.toString(), binding.edtName.text.toString(),
                    binding.edtCustomerName.text.toString(), binding.edtCustomerNumber.text.toString())) {


                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()

                var availabilityMutableList = mutableListOf<AvailabilityCustomModel.Availability>()
                availabilityMutableList.addAll(availabilityNoAdapter.getAttachList())
                Log.e("LIST>>>>>>", "onCreate:${availabilityNoAdapter.getAttachList()}")

                var areaMutableList = mutableListOf<AreaCustomModel.Area>()
                areaMutableList.addAll(addAreaItemsAdapter.getAttachList())
                Log.e("LIST>>>>>>", "onCreate:${" Areadata===> "} ${addAreaItemsAdapter.getAttachList()}")


                val listJsonArray = availabilityMutableList.map {
                    JSONObject().apply {
                        put("ItemName", it.ItemName)
                        put("ItemQty", it.ItemQty)
                        put("ItemDistance", it.ItemDistance)
                    }
                }

                val jsonArray = areaMutableList.map {
                    JSONObject().apply {
                        put("Location", it.Location)
                        put("Length", it.Length)
                        put("Width", it.Width)
                        put("Height", it.Height)
                    }
                }

                val jsonObject = JSONObject().apply {
                    // Add properties to the JSON object
                    put("TicketId", ticketData.id)
                    put("ReportType", "Site Survey")
                    put("ItemSerialNo", "")
                    put("ItemCode", "")
                    put("BuildingFloorCount", binding.edtTotalFloorBuilding.text.toString().trim())
                    put("WaterSource", binding.edtSourceWater.text.toString().trim())
                    put("TankVolume", binding.edtOverheadTankVolume.text.toString().trim())
                    put("TankCleaning", binding.edtTankCleaningCapacity.text.toString().trim())
                    put("TankFillingFrequency", binding.edtTankFillingFrequency.text.toString().trim())
                    put("Floor", binding.edtTotalFloor.text.toString().trim())
                    put("ShiftNo", binding.edtNoShifts.text.toString().trim())
                    put("ShiftTiming", binding.edtWorkTimeShifts.text.toString().trim())
                    put("TankHeight", binding.edtTankHeight.text.toString())
                    put("TotalEmployee", binding.edtNoOfEmployee.text.toString().trim())
                    put("TotalVisitors", binding.edtNoOfVisitors.text.toString().trim())
                    put("PantriesOnFloor", binding.edtNoOfPantriesFloor.text.toString().trim())
                    put("ElectricityType", binding.edtElectricityPoint.text.toString().trim())
                    put("PowerBackupCapacity", binding.edtPowerBackUpCapacity.text.toString().trim())
                    put("TDSRawWater", binding.edtTDSRawWater.text.toString().trim())
                    put("ChlorinatedWater", binding.edtChlorinatedWater.text.toString().trim())
                    put("WaterPressure", binding.edtwaterPressure.text.toString())
                    put("PHLevel", binding.edtLevelPHValue.text.toString().trim())
                    put("InstalledDispensers", binding.edtDispensersFloor.text.toString().trim())
                    put("ExistingDispensersMake", binding.edtExistingWater.text.toString().trim())
                    put("WaterBottelsUses", binding.edtDailyConsumption.text.toString().trim())
                    put("WaterBottlesCapacity", binding.edtCapacityWaterBottle.text.toString().trim())
                    put("ExistingBottlesMake", binding.edtExistingWaterBottleValue.text.toString().trim())
                    put("EngineerName", binding.edtName.text.toString().trim())
                    put("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())
                    put("CustomerName", binding.edtCustomerName.text.toString().trim())
                    put("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    put("CustomerRemark", binding.edtCustomerRemark.text.toString().trim())
                    put("Availability", listJsonArray)
                    put("Area", jsonArray)
                }
                //todo Convert the JSONObject to a JSON string
                val jsonString = jsonObject.toString()
                Log.e("REQUEST>>>>>", "onCreate: $jsonString")


                try {
                    val builder = MultipartBody.Builder()
                    builder.setType(MultipartBody.FORM)

                    builder.addFormDataPart("TicketId", ticketData.id.toString())
                    builder.addFormDataPart("ReportType", "Site Survey")
                    builder.addFormDataPart("ItemSerialNo", "")
                    builder.addFormDataPart("ItemCode", "")
                    builder.addFormDataPart("BuildingFloorCount", binding.edtTotalFloorBuilding.text.toString().trim())
                    builder.addFormDataPart("WaterSource", binding.edtSourceWater.text.toString().trim())
                    builder.addFormDataPart("TankVolume", binding.edtOverheadTankVolume.text.toString().trim())
                    builder.addFormDataPart("TankCleaning", binding.edtTankCleaningCapacity.text.toString().trim())
                    builder.addFormDataPart("TankFillingFrequency", binding.edtTankFillingFrequency.text.toString().trim())
                    builder.addFormDataPart("Floor", binding.edtTotalFloor.text.toString().trim())
                    builder.addFormDataPart("ShiftNo", binding.edtNoShifts.text.toString().trim())
                    builder.addFormDataPart("ShiftTiming", binding.edtWorkTimeShifts.text.toString().trim())
                    builder.addFormDataPart("TankHeight", binding.edtTankHeight.text.toString())
                    builder.addFormDataPart("TotalEmployee", binding.edtNoOfEmployee.text.toString().trim())
                    builder.addFormDataPart("TotalVisitors", binding.edtNoOfVisitors.text.toString().trim())
                    builder.addFormDataPart("PantriesOnFloor", binding.edtNoOfPantriesFloor.text.toString().trim())
                    builder.addFormDataPart("ElectricityType", binding.edtElectricityPoint.text.toString().trim())
                    builder.addFormDataPart("PowerBackupCapacity", binding.edtPowerBackUpCapacity.text.toString().trim())
                    builder.addFormDataPart("TDSRawWater", binding.edtTDSRawWater.text.toString().trim())
                    builder.addFormDataPart("ChlorinatedWater", binding.edtChlorinatedWater.text.toString().trim())
                    builder.addFormDataPart("WaterPressure", binding.edtwaterPressure.text.toString())
                    builder.addFormDataPart("PHLevel", binding.edtLevelPHValue.text.toString().trim())
                    builder.addFormDataPart("InstalledDispensers", binding.edtDispensersFloor.text.toString().trim())
                    builder.addFormDataPart("ExistingDispensersMake", binding.edtExistingWater.text.toString().trim())
                    builder.addFormDataPart("WaterBottelsUses", binding.edtDailyConsumption.text.toString().trim())
                    builder.addFormDataPart("WaterBottlesCapacity", binding.edtCapacityWaterBottle.text.toString().trim())
                    builder.addFormDataPart("ExistingBottlesMake", binding.edtExistingWaterBottleValue.text.toString().trim())
                    builder.addFormDataPart("EngineerName", binding.edtName.text.toString().trim())
                    builder.addFormDataPart("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())
                    builder.addFormDataPart("CustomerName", binding.edtCustomerName.text.toString().trim())
                    builder.addFormDataPart("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    builder.addFormDataPart("CustomerRemark", binding.edtCustomerRemark.text.toString().trim())

                    if (availabilityMutableList.isNotEmpty()) {
                        val sparePartsArray = JSONArray()

                        for (sparePart in availabilityMutableList) {
                            val sparePartObject = JSONObject()
                            sparePartObject.put("ItemName", sparePart.ItemName)
                            sparePartObject.put("ItemQty", sparePart.ItemQty)
                            sparePartObject.put("ItemDistance", sparePart.ItemDistance)
                            // Add the individual spare part objects to the array
                            sparePartsArray.put(sparePartObject)
                        }

                        val discountPart: MultipartBody.Part = MultipartBody.Part.createFormData("Availability", sparePartsArray.toString())
                        builder.addPart(discountPart)
                    } else {
                        // If the sparePartsList is empty, add an empty value for "SpartPart"
                        builder.addFormDataPart("Availability", "")
                    }

                    if (areaMutableList.isNotEmpty()) {
                        val sparePartsArray = JSONArray()

                        for (sparePart in areaMutableList) {
                            val sparePartObject = JSONObject()
                            sparePartObject.put("Location", sparePart.Location)
                            sparePartObject.put("Length", sparePart.Length)
                            sparePartObject.put("Width", sparePart.Width)
                            sparePartObject.put("Height", sparePart.Height)
                            // Add the individual spare part objects to the array
                            sparePartsArray.put(sparePartObject)
                        }

                        val discountPart: MultipartBody.Part = MultipartBody.Part.createFormData("Area", sparePartsArray.toString())
                        builder.addPart(discountPart)
                    } else {
                        // If the sparePartsList is empty, add an empty value for "SpartPart"
                        builder.addFormDataPart("Area", "")
                    }


                    Log.e(TAG, "onCreate: ${mArrayUriList.size}" )

                    if (mArrayUriList.size > 0) {
                        for (i in 0 until mArrayUriList.size) {
                            if (path.isNotEmpty()) {
                                val file: File
                                try {
                                    file = File(path[i])
//                                    file = File(path[i].replace(":", "_"))
                                    val attach = MultipartBody.Part.createFormData("File", file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
                                    builder.addPart(attach)
                                } catch (e: Exception) {
                                    builder.addFormDataPart("File", "", RequestBody.create("multipart/form-data".toMediaTypeOrNull(), ""))
                                    e.printStackTrace()
                                }
                            } else {
                                builder.addFormDataPart("File", "")
                            }
                        }
                    } else {
                        builder.addFormDataPart("File", "")
                    }


                    val requestBody = builder.build()
                    Log.e("payload", requestBody.toString())


                    viewModel.createTicketTypeItems(requestBody)

                    bindRemarkObserver()

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }

        }


        //todo open gallery on click---
        binding.ivAttachmentFiles.setOnClickListener {
//            openImageUploader()
//            chooseImageFromGallery()

            if (allPermissionsGranted()) {
                captureImageFromCamera()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_PERMISSIONS
                )
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }
        }



    }


    //todo create item observer bind--

    fun bindRemarkObserver() {

        viewModel.customerUpload.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "bindRemarkObserver: $it")
            },
            onLoading = {
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.successmessagetoast(this, "Successful")
                    Prefs.putString(Global.ITEM_FLAG, "Site_Survey_Created")
                    onBackPressed()
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this, response.message)
                }
            }
        ))

    }


    //todo bind spare parts adapter--
    private fun bindAvailabilityItemAdapter() = binding.rvAddAvailabilityItems.apply {
        adapter = availabilityNoAdapter
        layoutManager = LinearLayoutManager(this@TicketSiteSurveyTypeActivity)

        if (availabilityNoAdapter != null){
            availabilityNoAdapter.setOnItemMinusClickListener { s, i ->
                if (availabilityNoAdapter.itemCount > 0) {
                    availabilityNoAdapter.removeItem(i)
                }
            }
        }

    }


    //todo bind spare parts adapter--
    private fun bindAreaItemAdapter() = binding.rvAddAreaItems.apply {
        adapter = addAreaItemsAdapter
        layoutManager = LinearLayoutManager(this@TicketSiteSurveyTypeActivity)

        if (addAreaItemsAdapter != null){
            addAreaItemsAdapter.setOnItemMinusClickListener { s, i ->
                if (addAreaItemsAdapter.itemCount > 0) {
                    addAreaItemsAdapter.removeItem(i)
                }
            }
        }

    }


    private val PICK_IMAGES_REQUEST_CODE = 1

    private fun captureImageFromCamera() {
        var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, PICK_IMAGES_REQUEST_CODE)
    }


    //todo camera capture--

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
                    mSelectedList.clear()
                    mSelectedList.add(fileUri)

                    mArrayUriList.addAll(mSelectedList)
//                    mArrayUriList.add(fileUri)

                    Log.e(TAG, "onActivityResult: "+mArrayUriList.size )
                    path.clear()

                    for (i in mArrayUriList.indices) {
                        path.add(FileUtils.getPath(this@TicketSiteSurveyTypeActivity, mArrayUriList.get(i)).toString())
                    }

                    val adapter = ImageViewAdapter(this, mArrayUriList)
                    binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
                    binding.rvAttachment.setAdapter(adapter)

                }catch (e:NullPointerException){
                    e.printStackTrace()
                }

            }else {
                // Failed to take picture
                Global.warningmessagetoast(this, "Failed to take camera picture")
            }
        }
    }


    //todo to convert bitmap to file--
    private fun savebitmap(bmp: Bitmap): File {
//        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        var extStorageDirectory = this.cacheDir //todo this is use for temporairy file path storage //"/storage/emulated/0/Download"
        var outStream: OutputStream? = null
        val num: Int = random.nextInt(90) + 10
//        var file = File(extStorageDirectory, "temp$num.png")

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
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    } //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera()
            } else {
                Global.warningmessagetoast(this, "denied permissions case")
            }
        }
    }


    //todo bind attachment from gallery--
    private fun openImageUploader() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        Matisse.from(this@TicketSiteSurveyTypeActivity)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .maxSelectable(5)
                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideEngine())
                            .showPreview(false) // Default is `true`
                            .originalEnable(true)
                            .maxOriginalSize(10)
                            .autoHideToolbarOnSingleTap(true)
                            .addFilter(UriFilter(mArrayUriList)) // Add a UriFilter for preselected images
                            .showSingleMediaType(true)
//                            .theme(R.style.Matisse_Zhihu1)
                            .forResult(REQUEST_CODE_CHOOSE)
                    }else {
                        Toast.makeText(this@TicketSiteSurveyTypeActivity, "Please enable permission", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                    token!!.continuePermissionRequest()
                }

            }).check()


    }


    //todo bind attachment from gallery--
    private fun chooseImageFromGallery() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE)
        }
        else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE);
        }
    }



/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && null != data) {
            //mSelected.add(data.getData());
            mSelectedList = Matisse.obtainResult(data) as ArrayList<Uri>
            mArrayUriList.addAll(mSelectedList)
            for (i in mArrayUriList.indices) {
                path.add(FileUtils.getPath(this@TicketSiteSurveyTypeActivity, mArrayUriList.get(i)).toString())
            }
            val adapter = ImageViewAdapter(this, mArrayUriList)
            binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
            binding.rvAttachment.setAdapter(adapter)
//            adapter.notifyDataSetChanged()
            // Get the Image from data
        }

        else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the Image from data
            if (data.clipData != null) {
                val mClipData: ClipData = data.clipData!!
                var cout = data.clipData!!.itemCount
                for (i in 0 until cout) {
                    // adding imageuri in array
                    val imageurl: Uri = data.clipData!!.getItemAt(i).uri
//                    mSelectedList.add(imageurl)
                    mArrayUriList.add(imageurl)
                }

//                mArrayUriList.addAll(mSelectedList)
                Log.e(TAG, "onActivityResult: "+mArrayUriList.size )
                for (i in mArrayUriList.indices) {
                    path.add(FileUtils.getPath(this@TicketSiteSurveyTypeActivity, mArrayUriList.get(i)).toString())
                }
                val adapter = ImageViewAdapter(this, mArrayUriList)
                binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
                binding.rvAttachment.setAdapter(adapter)

            } else {
                val imageurl: Uri = data.data!!
                mArrayUriList.add(imageurl)
                Log.e(TAG, "onActivityResult: " + mArrayUriList.size )
                for (i in mArrayUriList.indices) {
                    path.add(FileUtils.getPath(this@TicketSiteSurveyTypeActivity, mArrayUriList.get(i)).toString())
                }
                val adapter = ImageViewAdapter(this, mArrayUriList)
                binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
                binding.rvAttachment.setAdapter(adapter)

            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }

    }*/



    class UriFilter(private val preselectedUris: List<Uri>) : Filter() {

        override fun constraintTypes(): Set<MimeType> {
            Log.e(TAG, "filter0: "+preselectedUris.size )

            return MimeType.ofImage()
        }

        override fun filter(context: Context, item: Item): IncapableCause? {
            // Return an IncapableCause if the item should be filtered
            return if (!preselectedUris.contains(item.contentUri)) {
                Log.e(TAG, "filter: "+preselectedUris.size )

                null // No incapability, the item is allowed
            } else {
                Log.e(TAG, "filter2: "+preselectedUris.size )

                IncapableCause(IncapableCause.TOAST, "This image is Already Selected.")
            }
        }
    }


    private fun validation(floorBuilding: String, sourceWater: String, overheadTankVol: String, tankCleaningCap: String, tankFillingFreq: String,
                           floor: String, noOfShifts: String, workTimeShift: String, tankHeightMach: String, NoOfEmployee: String, NoOFVisitor: String,
        NoOfPanitries: String, electricityPoints: String, powerBankUp: String, tdsRawWater: String, chlorinatedWater: String, waterPressurePSI: String,
        levelPHValue: String, noOfDispensersInstall: String, existWaterDispense: String, dailyConsumptionBottles: String, capacityWaterBottle: String,
        existingWaterBottle: String, name: String, customerName: String, customerNumber: String, ): Boolean {

        if (availabilityNoAdapter.getAttachList().isEmpty()){
            Global.warningmessagetoast(this, "Select Atleast One Availability Item")
            return false
        }
        else if (addAreaItemsAdapter.getAttachList().isEmpty()){
            Global.warningmessagetoast(this, "Select Atleast One Area Item")
            return false
        }
       /* else if (mArrayUriList.size == 0){
            Global.warningmessagetoast(this, "Select Atleast One Attachment")
            return false
        }*/ else if (floorBuilding.isEmpty()) {
            Global.warningmessagetoast(this, "Total Floor Building can't be Empty")
            return false
        } else if (sourceWater.isEmpty()) {
            Global.warningmessagetoast(this, "Source of Water can't be Empty")
            return false
        } else if (overheadTankVol.isEmpty()) {
            Global.warningmessagetoast(this, "Overhead Tank Volume can't be Empty")
            return false
        } else if (tankCleaningCap.isEmpty()) {
            Global.warningmessagetoast(this, "Tank Cleaning Capacity can't be Empty")
            return false
        } else if (tankFillingFreq.isEmpty()) {
            Global.warningmessagetoast(this, "Tank Filling Frequency can't be Empty")
            return false
        }  else if (floor.isEmpty()) {
            Global.warningmessagetoast(this, "Floor can't be Empty")
            return false
        }else if (noOfShifts.isEmpty()) {
            Global.warningmessagetoast(this, "Shifts can't be Empty")
            return false
        }else if (workTimeShift.isEmpty()) {
            Global.warningmessagetoast(this, "Work Timing Shifts can't be Empty")
            return false
        }else if (tankHeightMach.isEmpty()) {
            Global.warningmessagetoast(this, "Tank Height Level can't be Empty")
            return false
        }else if (NoOfEmployee.isEmpty()) {
            Global.warningmessagetoast(this, "No of Employee can't be Empty")
            return false
        }else if (NoOFVisitor.isEmpty()) {
            Global.warningmessagetoast(this, "Visitor can't be Empty")
            return false
        }else if (NoOfPanitries.isEmpty()) {
            Global.warningmessagetoast(this, "Pantries can't be Empty")
            return false
        }else if (electricityPoints.isEmpty()) {
            Global.warningmessagetoast(this, "Electricity Point can't be Empty")
            return false
        }else if (powerBankUp.isEmpty()) {
            Global.warningmessagetoast(this, "Power BackUp can't be Empty")
            return false
        } else if (tdsRawWater.isEmpty()) {
            Global.warningmessagetoast(this, "TDS Raw Water can't be Empty")
            return false
        } else if (chlorinatedWater.isEmpty()) {
            Global.warningmessagetoast(this, "Chlorinated Water can't be Empty")
            return false
        } else if (waterPressurePSI.isEmpty()) {
            Global.warningmessagetoast(this, "Watter Pressure can't be Empty")
            return false
        } else if (levelPHValue.isEmpty()) {
            Global.warningmessagetoast(this, "Level PH Value can't be Empty")
            return false
        } else if (noOfDispensersInstall.isEmpty()) {
            Global.warningmessagetoast(this, "Dispensers Installed Floor can't be Empty")
            return false
        }  else if (existWaterDispense.isEmpty()) {
            Global.warningmessagetoast(this, "Existing Water Dispensers can't be Empty")
            return false
        }  else if (dailyConsumptionBottles.isEmpty()) {
            Global.warningmessagetoast(this, "Daily Consumption Water Bottle can't be Empty")
            return false
        }  else if (capacityWaterBottle.isEmpty()) {
            Global.warningmessagetoast(this, "Water Bottle Capacity can't be Empty")
            return false
        }  else if (existingWaterBottle.isEmpty()) {
            Global.warningmessagetoast(this, "Level PH Value can't be Empty")
            return false
        }  else if (name.isEmpty()) {
            Global.warningmessagetoast(this, "Name can't be Empty")
            return false
        }else if (customerName.isEmpty()) {
            Global.warningmessagetoast(this, "Customer Name can't be Empty")
            return false
        }else if (customerNumber.isEmpty()) {
            Global.warningmessagetoast(this, "Customer Number can't be Empty")
            return false
        }

        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {

            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }


   companion object{
       private const val TAG = "TicketSiteSurveyTypeAct"
   }


}