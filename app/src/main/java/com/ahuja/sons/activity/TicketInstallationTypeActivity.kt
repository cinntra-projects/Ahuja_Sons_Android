package com.ahuja.sons.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ticketItemAdapter.AddBillableItemsAdapter
import com.ahuja.sons.adapter.ticketItemAdapter.AddFOCPartsItemsAdapter
import com.ahuja.sons.adapter.ticketItemAdapter.ImageViewAdapter
import com.ahuja.sons.apibody.BodySparePart
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityTicketInstallationTypeBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.newapimodel.SpareItemListApiModel
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.SelectionCreator
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*


class TicketInstallationTypeActivity : AppCompatActivity() {

    lateinit var binding: ActivityTicketInstallationTypeBinding
    lateinit var viewModel: MainViewModel
    var dataModel = ItemAllListResponseModel.DataXXX()
    var mArrayUriList: ArrayList<Uri> = ArrayList()
    var mSelectedList: ArrayList<Uri> = ArrayList()
    var path: ArrayList<String> = ArrayList()
    var isVantillationChecked = "false"
    var isRawWaterChecked = "false"
    var isPowerAMPChecked = "false"
    var isPartMissingChecked = "false"
    var isDamagedPartChecked = "false"
    var Flag = ""
    var focItemsAdapter: AddFOCPartsItemsAdapter? = null
    var billableItemsAdapter: AddBillableItemsAdapter? = null
    var content = ""
    var contentQuantity = ""
    var focSerialNo = ""

    var ticketData = TicketData()
    var modelNoValue = ""


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
        binding = ActivityTicketInstallationTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        dataModel = intent.getSerializableExtra("Installation")!! as ItemAllListResponseModel.DataXXX
        ticketData = intent.getSerializableExtra("ticketData")!! as TicketData
        Flag = intent.getStringExtra("ticketType")!!

        binding.loadingback.visibility = View.GONE

        if (ticketData != null){
            if (ticketData.AssignToDetails.size > 0){
                binding.edtName.setText(ticketData.AssignToDetails[0].firstName + " " + ticketData.AssignToDetails[0].lastName)
                binding.edtCustomerName.setText(ticketData.BusinessPartner[0].CardName)
                binding.edtCustomerNumber.setText(ticketData.BusinessPartner[0].Phone1)
                binding.edtCustomerDesignation.setText(ticketData.BusinessPartner[0].BPEmployee[0].Position)
            }
        }


        //todo set model no. adapter--
        val modelAdapter = ArrayAdapter(this@TicketInstallationTypeActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
        binding.acMachineModelType.setAdapter(modelAdapter)

        //todo mode communication item selected
        binding.acMachineModelType.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.modelList_gl.isNotEmpty()) {
                    modelNoValue = Global.modelList_gl[position]
                    binding.acMachineModelType.setText(Global.modelList_gl[position])

                    val adapter = ArrayAdapter(this@TicketInstallationTypeActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
                    binding.acMachineModelType.setAdapter(adapter)
                } else {
                    modelNoValue = ""
                    binding.acMachineModelType.setText("")
                }
            }

        }


        subscribeToCustomerFilterObserver()


        //todo add foc items---
        binding.ivAddFocItem.setOnClickListener {
            if (customerList_gl.isNotEmpty()) {
                /*  if (radioText.isEmpty()) {
                      Toast.makeText(this, "Please Select Spare Part First", Toast.LENGTH_SHORT).show()
                  } else {
                      val newItem = BodySparePart.SparePart(
                          SellType = radioText,
                          SparePartId = "",
                          SparePartName = content,
                          PartQty = contentQuantity
                      )
                      focItemsAdapter!!.addItem(newItem)
                      content = ""
                      contentQuantity = ""
                  }*/
                val newItem = BodySparePart.SparePart(
                    SellType = "FOC",
                    SparePartId = "",
                    SparePartName = content,
                    PartQty = contentQuantity,
                    SpareSerialNo = focSerialNo
                )
                focItemsAdapter!!.addItem(newItem)
                content = ""
                contentQuantity = ""
                focSerialNo = ""

            } else {
                Toast.makeText(this, "NOt Connected the observer", Toast.LENGTH_SHORT).show()
            }


        }


        //todo add billable items---
        binding.ivAddBillableItems.setOnClickListener {
            if (customerList_gl.isNotEmpty()) {
                val newItem = BodySparePart.SparePart(
                    SellType = "Billable",
                    SparePartId = "",
                    SparePartName = content,
                    PartQty = contentQuantity,
                    SpareSerialNo = focSerialNo
                )
                billableItemsAdapter!!.addItem(newItem)
                content = ""
                contentQuantity = ""
                focSerialNo = ""
            } else {
                Toast.makeText(this, "NOt Connected the observer", Toast.LENGTH_SHORT).show()
            }


        }


        binding.ivAttachmentFiles.setOnClickListener {
//            chooseImageFromGallery()

            if (allPermissionsGranted()) {
                captureImageFromCamera()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }

        }

        binding.edtDateOfInstallation.setOnClickListener {
            Global.selectDate(this, binding.edtDateOfInstallation)
        }

        binding.ventillationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isVantillationChecked = isChecked.toString()
            Log.e(TAG, "isVantillationChecked: ${isChecked}")
        }

        binding.rawWaterCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isRawWaterChecked = isChecked.toString()
            Log.e(TAG, "isRawWaterChecked: ${isChecked}")
        }

        binding.powerAMPCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isPowerAMPChecked = isChecked.toString()
            Log.e(TAG, "isPowerAMPChecked: ${isChecked}")
        }

        binding.partMissingCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isPartMissingChecked = isChecked.toString()
            Log.e(TAG, "isPartMissingChecked: ${isChecked}")
        }

        binding.damagedPartCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isDamagedPartChecked = isChecked.toString()
            Log.e(TAG, "isDamagedPartChecked: ${isChecked}")
        }

        if (Flag == "De-Installation" || Flag == "Shifting" || Flag == "Packaging"){
            binding.partMissingLayout.visibility = View.VISIBLE
            binding.damagedPartLayout.visibility = View.VISIBLE
        }else{
            binding.partMissingLayout.visibility = View.GONE
            binding.damagedPartLayout.visibility = View.GONE
            isPartMissingChecked = ""
            isDamagedPartChecked = ""
        }


        binding.submitBtn.setOnClickListener {
            //todo calling remark api here---

            if (validation(binding.edtDateOfInstallation.text.toString(), binding.edtMachineLocationFloar.text.toString(), binding.edtMachineArea.text.toString(),
                    binding.edtNoEmployeeOfArea.text.toString(), binding.edtMembrane.text.toString(), binding.edtRejected.text.toString(), binding.edtROPump.text.toString(),
                    binding.edtTDSInput.text.toString(), binding.edtTDSOutput.text.toString(), binding.edtHotWater.text.toString(), binding.edtColdWater.text.toString(),
                    isVantillationChecked, binding.edtVentillationRemark.text.toString(), isRawWaterChecked, binding.edtRawWaterRemark.text.toString(), isPowerAMPChecked, binding.edtPowerAMPRemark.text.toString(),
                    binding.edtCustomerName.text.toString(), binding.edtCustomerNumber.text.toString(), binding.edtName.text.toString(), isPartMissingChecked, binding.edtPartMissingRemark.text.toString(), isDamagedPartChecked, binding.edtDamagedPartRemark.text.toString(), modelNoValue)) {

                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()

                var sparePartList = mutableListOf<BodySparePart.SparePart>()
                sparePartList.addAll(focItemsAdapter!!.getAttachList())

                var billableList = mutableListOf<BodySparePart.SparePart>()
                billableList.addAll(billableItemsAdapter!!.getAttachList())

                Log.e("LIST>>>>>>", "onCreate:${sparePartList} ")

                Log.e(TAG, "isPartMissingChecked: "+ isPartMissingChecked )


                try {
                    val builder = MultipartBody.Builder()
                    builder.setType(MultipartBody.FORM)

                    builder.addFormDataPart("TicketId", dataModel.TicketId)
                    builder.addFormDataPart("ReportType", Flag)
                    builder.addFormDataPart("ItemSerialNo", dataModel.SerialNo)
                    builder.addFormDataPart("ItemCode", dataModel.ItemCode)
                    builder.addFormDataPart("InstallDate", Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtDateOfInstallation.text.toString()))
                    builder.addFormDataPart("MachineLocFloor", binding.edtMachineLocationFloar.text.toString().trim())
                    builder.addFormDataPart("MachineLocArea", binding.edtMachineArea.text.toString().trim())
                    builder.addFormDataPart("EmployeeInArea", binding.edtNoEmployeeOfArea.text.toString().trim())
                    builder.addFormDataPart("Remark", binding.edtRemarks.text.toString().trim())
                    builder.addFormDataPart("Membrane", binding.edtMembrane.text.toString().trim())
                    builder.addFormDataPart("Rejected", binding.edtRejected.text.toString().trim())
                    builder.addFormDataPart("ROPump", binding.edtROPump.text.toString().trim())
                    builder.addFormDataPart("modelType", modelNoValue)
                    builder.addFormDataPart("TDSInput", binding.edtTDSInput.text.toString().trim())
                    builder.addFormDataPart("TDSOutput", binding.edtTDSOutput.text.toString())
                    builder.addFormDataPart("HotWater", binding.edtHotWater.text.toString().trim())
                    builder.addFormDataPart("ColdWater", binding.edtColdWater.text.toString().trim())
                    builder.addFormDataPart("is_Ventillation", isVantillationChecked)
                    builder.addFormDataPart("VentillationRemark", binding.edtVentillationRemark.text.toString().trim())
                    builder.addFormDataPart("is_WaterPressure", isRawWaterChecked)
                    builder.addFormDataPart("WaterPressureRemark", binding.edtRawWaterRemark.text.toString().trim())
                    builder.addFormDataPart("is_PowerAvailable", isPowerAMPChecked)
                    builder.addFormDataPart("PowerAvailableRemark", binding.edtPowerAMPRemark.text.toString())
                    builder.addFormDataPart("is_PartMissing", isPartMissingChecked)
                    builder.addFormDataPart("PartMissingRemark", binding.edtPartMissingRemark.text.toString())
                    builder.addFormDataPart("is_DamagedPart", isDamagedPartChecked)
                    builder.addFormDataPart("DamagedPartRemark", binding.edtDamagedPartRemark.text.toString())
                    builder.addFormDataPart("CustomerName", binding.edtCustomerName.text.toString().trim())
                    builder.addFormDataPart("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    builder.addFormDataPart("CustomerDesignation", binding.edtCustomerDesignation.text.toString().trim())
                    builder.addFormDataPart("EngineerName", binding.edtName.text.toString().trim())
                    builder.addFormDataPart("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())

                    val sparePartsArray = JSONArray()

                    if (sparePartList.isNotEmpty() || billableList.isNotEmpty()) {

                        if (sparePartList.isNotEmpty()){

                            for (sparePart in sparePartList) {
                                val sparePartObject = JSONObject()
                                sparePartObject.put("SellType", sparePart.SellType)
                                sparePartObject.put("SparePartId", sparePart.SparePartId)
                                sparePartObject.put("SparePartName", sparePart.SparePartName)
                                sparePartObject.put("PartQty", sparePart.PartQty)
                                sparePartObject.put("SpareSerialNo", sparePart.SpareSerialNo)
                                // Add the individual spare part objects to the array
                                sparePartsArray.put(sparePartObject)
                            }
                        }
                        if (billableList.isNotEmpty()){
                            for (sparePart in billableList) {
                                val billablePartObject = JSONObject()
                                billablePartObject.put("SellType", sparePart.SellType)
                                billablePartObject.put("SparePartId", sparePart.SparePartId)
                                billablePartObject.put("SparePartName", sparePart.SparePartName)
                                billablePartObject.put("PartQty", sparePart.PartQty)
                                billablePartObject.put("SpareSerialNo", sparePart.SpareSerialNo)

                                // Add the individual spare part objects to the array
                                sparePartsArray.put(billablePartObject)
                            }
                        }


                        val discountPart: MultipartBody.Part = MultipartBody.Part.createFormData("SpartPart", sparePartsArray.toString())
                        builder.addPart(discountPart)
                    } else {
                        // If the sparePartsList is empty, add an empty value for "SpartPart"
                        builder.addFormDataPart("SpartPart", "")
                    }

                    Log.e(TAG, "onCreate: ${mArrayUriList.size}")

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
    }



    var customerList_gl = ArrayList<SpareItemListApiModel.DataXXX>()

    fun subscribeToCustomerFilterObserver() {
        val call: Call<SpareItemListApiModel> = ApiClient().service.allSparePartApiList()
        call.enqueue(object : Callback<SpareItemListApiModel?> {
            override fun onResponse(
                call: Call<SpareItemListApiModel?>,
                response: Response<SpareItemListApiModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        if (response.body()?.data!!.isNotEmpty()) {
                            customerList_gl.clear()
                            customerList_gl.addAll(response.body()!!.data)
                            focItemsAdapter = AddFOCPartsItemsAdapter(this@TicketInstallationTypeActivity, mutableListOf(), customerList_gl)
                            bindFocItemAdapter()

                            billableItemsAdapter = AddBillableItemsAdapter(this@TicketInstallationTypeActivity, mutableListOf(), customerList_gl)
                            bindBillableItemAdapter()

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SpareItemListApiModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@TicketInstallationTypeActivity,
                    t.message.toString()
                )
            }
        })
    }


    //todo bind foc items parts adapter--
    private fun bindFocItemAdapter() = binding.rvSpareParts.apply {
        adapter = focItemsAdapter
        layoutManager = LinearLayoutManager(this@TicketInstallationTypeActivity)

        //todo remove foc items--
        if (focItemsAdapter != null){
            focItemsAdapter!!.setOnItemMinusClickListener { s, i ->
                if (focItemsAdapter!!.itemCount > 0) {
                    focItemsAdapter!!.removeItem(i)
                }
            }
        }

    }


    //todo bind billable items parts adapter--
    private fun bindBillableItemAdapter() = binding.rvBillableItemParts.apply {
        adapter = billableItemsAdapter
        layoutManager = LinearLayoutManager(this@TicketInstallationTypeActivity)

        //todo remove billable items---
        if (billableItemsAdapter != null){
            billableItemsAdapter!!.setOnItemMinusClickListener { s, i ->
                if (billableItemsAdapter!!.itemCount > 0) {
                    billableItemsAdapter!!.removeItem(i)
                }
            }
        }
    }



    //todo remark observer bind--

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
                    onBackPressed()
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this, response.message)
                }
            }
        ))

    }


    private val REQUEST_CODE_CHOOSE = 1000

    private fun openImageUploader() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {

                        Log.e("","Size=>${mArrayUriList.size}")

                        Matisse.from(this@TicketInstallationTypeActivity)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(9) // Set the maximum number of images that can be selected
                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideEngine())
                            .originalEnable(true)
                            .maxOriginalSize(10)
                            .autoHideToolbarOnSingleTap(true)
                            .addFilter(UriFilter(mArrayUriList)) // Add a UriFilter for preselected images
                            .showSingleMediaType(true)
//                            .theme(R.style.Matisse_Zhihu1)
                            .forResult(REQUEST_CODE_CHOOSE)

                        /*Matisse.from(this@TicketInstallationTypeActivity)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .maxSelectable(5)
                            .gridExpectedSize(resources.getDimensionPixelSize(com.wae.servicesupportportal.R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideEngine())
                            .showPreview(false) // Default is `true`
                            .addFilter(UriFilter(mArrayUriList)) // Add a UriFilter for preselected images
                            .showSingleMediaType(true)
                            .forResult(REQUEST_CODE_CHOOSE)*/


                    } else {
                        Toast.makeText(
                            this@TicketInstallationTypeActivity,
                            "Please enable permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()


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
                        path.add(FileUtils.getPath(this@TicketInstallationTypeActivity, mArrayUriList.get(i)).toString())
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
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    } //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera()
            } else {
                Global.warningmessagetoast(this, "denied permissions case")
            }
        }
    }


    private fun validation(
        dateInstall: String,
        machineLocFloor: String,
        machineArea: String,
        noEmployeeArea: String,
        membrane: String,
        rejected: String,
        roPump: String,
        tdsInput: String,
        tdsOutput: String,
        hotWater: String,
        coldWater: String,
        isVantillationChecked: String,
        ventillationRemarks: String,
        isRawWaterChecked: String,
        rawWaterRemark: String,
        isPowerAMPChecked: String,
        powerAMPRemark: String,
        customerName: String,
        customerNumber: String,
        engineerName: String,
        isPartMissingChecked: String,
        partMissingRemark: String,
        isDamagedPartChecked: String,
        damagedPartRemark: String,
        modelNoValue : String
    ): Boolean {
       /* if (mArrayUriList.size == 0) {
            Global.warningmessagetoast(this, "Select Atleast One Attachment")
            return false
        } else */
        if (dateInstall.isEmpty()) {
            Global.warningmessagetoast(this, "Enter Date Installation")
            return false
        } else if (machineLocFloor.isEmpty()) {
            Global.warningmessagetoast(this, "Enter Machine Loc Floor")
            return false
        } else if (machineArea.isEmpty()) {
            Global.warningmessagetoast(this, "Enter Machine Loc Area")
            return false
        } else if (noEmployeeArea.isEmpty()) {
            Global.warningmessagetoast(this, "No. Of Employee can't be Empty")
            return false
        }else if (membrane.isEmpty()) {
            Global.warningmessagetoast(this, "Membrane can't be Empty")
            return false
        } else if (rejected.isEmpty()) {
            Global.warningmessagetoast(this, "Rejected can't be Empty")
            return false
        } else if (roPump.isEmpty()) {
            Global.warningmessagetoast(this, "Ro Pump can't be Empty")
            return false
        } else if (tdsInput.isEmpty()) {
            Global.warningmessagetoast(this, "Enter TDS Input")
            return false
        } else if (tdsOutput.isEmpty()) {
            Global.warningmessagetoast(this, "Enter TDS Output")
            return false
        } else if (hotWater.isEmpty()) {
            Global.warningmessagetoast(this, "Hot Water can't be Empty")
            return false
        } else if (coldWater.isEmpty()) {
            Global.warningmessagetoast(this, "Cold Water can't be Empty")
            return false
        } else if (customerName.isEmpty()) {
            Global.warningmessagetoast(this, "Customer Name can't be Empty")
            return false
        } else if (customerNumber.isEmpty()) {
            Global.warningmessagetoast(this, "Customer Number can't be Empty")
            return false
        } else if (engineerName.isEmpty()) {
            Global.warningmessagetoast(this, "Engineer Name can't be Empty")
            return false
        }
        else if (modelNoValue.isEmpty()) {
            Global.warningmessagetoast(this, "Select Machine Model Type")
            return false
        }
        else if (isVantillationChecked == "true" && ventillationRemarks.isEmpty()) {
            Global.warningmessagetoast(this, "Ventilation Remark can't be Empty")
            return false
        }
        else if (isVantillationChecked == "false" && ventillationRemarks.isNotEmpty()) {
            Global.warningmessagetoast(this, "Ventilation Check Box can't be Empty")
            return false
        }
        else if ( isRawWaterChecked == "true" && rawWaterRemark.isEmpty()) {
            Global.warningmessagetoast(this, "Raw Water Pressure Remark can't be Empty")
            return false
        }
        else if (isRawWaterChecked == "false" && rawWaterRemark.isNotEmpty()) {
            Global.warningmessagetoast(this, "Raw Water Pressure Check Box can't be Empty")
            return false
        }
        else if (isPowerAMPChecked == "true" && powerAMPRemark.isEmpty()) {
            Global.warningmessagetoast(this, "Power Availability in AMP Remark can't be Empty")
            return false
        }
        else if (isPowerAMPChecked == "false" && powerAMPRemark.isNotEmpty()) {
            Global.warningmessagetoast(this, "Power Availability in AMP Check Box can't be Empty")
            return false
        }

        else if (isPartMissingChecked == "true" && partMissingRemark.isEmpty()) {
            Global.warningmessagetoast(this, "Part Missing Remark can't be Empty")
            return false
        }

        else if (isPartMissingChecked == "false" && partMissingRemark.isNotEmpty()) {
            Global.warningmessagetoast(this, "Part Missing Check Box can't be Empty")
            return false
        }

        else if (isDamagedPartChecked == "true" && damagedPartRemark.isEmpty()) {
            Global.warningmessagetoast(this, "Damaged Part Remark can't be Empty")
            return false
        }

        else if (isDamagedPartChecked == "false" && damagedPartRemark.isNotEmpty()) {
            Global.warningmessagetoast(this, "Damaged Part Check Box can't be Empty")
            return false
        }

        return true
    }


    companion object {
        private const val TAG = "TicketInstallationTypeA"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {}
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
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


    //todo choose images from gallery --
    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
     super.onActivityResult(requestCode, resultCode, data)
     if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && null != data) {
         //mSelected.add(data.getData());
         mSelectedList = Matisse.obtainResult(data) as ArrayList<Uri>

         mArrayUriList.addAll(mSelectedList)
         for (i in mArrayUriList.indices) {
             path.add(FileUtils.getPath(this@TicketInstallationTypeActivity, mArrayUriList.get(i)).toString())
         }
         val adapter = ImageViewAdapter(this, mArrayUriList)
         binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
         binding.rvAttachment.setAdapter(adapter)
//            adapter.notifyDataSetChanged()
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
                 path.add(FileUtils.getPath(this@TicketInstallationTypeActivity, mArrayUriList.get(i)).toString())
             }
             val adapter = ImageViewAdapter(this, mArrayUriList)
             binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
             binding.rvAttachment.setAdapter(adapter)

         } else {
             val imageurl: Uri = data.data!!
             mArrayUriList.add(imageurl)
             Log.e(TAG, "onActivityResult: "+mArrayUriList.size )
             for (i in mArrayUriList.indices) {
                 path.add(FileUtils.getPath(this@TicketInstallationTypeActivity, mArrayUriList.get(i)).toString())
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



    private fun openGalleryWithPreselectedImages() {
        // Replace the following with actual Uri objects of your preselected images
        val preselectedUris: MutableList<Uri> = ArrayList()
        preselectedUris.add(Uri.parse("content://media/external/images/media/123"))
        preselectedUris.add(Uri.parse("content://media/external/images/media/456"))
        Matisse.from(this@TicketInstallationTypeActivity)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(9) // Set the maximum number of images that can be selected

            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .originalEnable(true)
            .maxOriginalSize(10)
            .autoHideToolbarOnSingleTap(true)
            .imageEngine(GlideEngine())
            .showSingleMediaType(true)

            .forResult(REQUEST_CODE_CHOOSE)

        // To preselect images, you can use the following code
        val selectionCreator: SelectionCreator = Matisse.from(this@TicketInstallationTypeActivity)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(9) // Set the maximum number of images that can be selected
            .gridExpectedSize(resources.getDimensionPixelSize(com.ahuja.sons.R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .originalEnable(true)
            .maxOriginalSize(10)
            .autoHideToolbarOnSingleTap(true)
            .imageEngine(GlideEngine())
            .showSingleMediaType(true)


        // Preselect images
            .addFilter(UriFilter(mArrayUriList)) // Add a UriFilter for preselected images
            .showSingleMediaType(true)
        // Start the image picker
        selectionCreator.forResult(REQUEST_CODE_CHOOSE)
    }


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


}

