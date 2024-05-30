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
import android.widget.RadioButton
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
import com.ahuja.sons.adapter.ticketItemAdapter.*
import com.ahuja.sons.apibody.BodySparePart
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityTicketItemPreventiveMaintenanceBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.ComplainDetailResponseModel
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.newapimodel.SpareItemListApiModel
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
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class TicketItemPreventiveMaintenanceActivity : AppCompatActivity() {

    lateinit var binding: ActivityTicketItemPreventiveMaintenanceBinding
//    lateinit var binding: DemoLayoutBinding
    lateinit var viewModel: MainViewModel
    var dataModel = ItemAllListResponseModel.DataXXX()
    var mArrayUriList: kotlin.collections.ArrayList<Uri> = ArrayList()
    var mSelectedList: ArrayList<Uri> = ArrayList()
    var path: ArrayList<String> = ArrayList()
    var index = 0;
    var billableSerialNo = ""
    var localList = mutableListOf<SpareCustomModel>()
    var focItemsAdapter: AddFOCPartsItemsAdapter? = null
    var billableItemsAdapter: AddBillableItemsAdapter? = null
    var content = ""
    var contentQuantity = ""
    var focSerialNo = ""
    var focItemPrice = ""

    //    lateinit var spareAdapter: demoAdapter
    private val REQUEST_CODE_CHOOSE = 1000

    var isHygieneSanitizeCheckbox = "false"
    var isFilterWashCheckBox = "false"
    var isDrainTubeCheckBoxItem = "false"
    var isLeakagePointCheckBoxItem = "false"
    var isHarnessCheckBoxItem = "false"
    var Flag = ""
    var radioText = ""
    var ticketData = TicketData()

    private val REQUEST_CODE = 1

    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    private  val REQUEST_CODE_PERMISSIONS = 10
    lateinit var fileUri: Uri

    var complainDetailVal = ""

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketItemPreventiveMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()
        reqPermission();
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding.loadingback.visibility = View.GONE

        dataModel = intent.getSerializableExtra("data")!! as ItemAllListResponseModel.DataXXX
        Flag = intent.getStringExtra("ticketType")!!

        ticketData = intent.getSerializableExtra("ticketData")!! as TicketData


        if (ticketData != null){
            if (ticketData.AssignToDetails.size > 0){
                binding.edtName.setText(ticketData.AssignToDetails[0].firstName + " " + ticketData.AssignToDetails[0].lastName)
                binding.edtNumber.setText(ticketData.AssignToDetails[0].Mobile)
                if (ticketData.BusinessPartner.size > 0){
                    binding.edtCustomerName.setText(ticketData.BusinessPartner[0].CardName)
                    binding.edtCustomerNumber.setText(ticketData.BusinessPartner[0].Phone1)
                }

                binding.edtComplainDetail.setText(ticketData.SubType)
                binding.edtComplainDetail.isEnabled = false
                binding.edtComplainDetail.isClickable = false
                binding.edtComplainDetail.isFocusableInTouchMode = false
            }
        }
//        spareAdapter = AddSparePartsItemsAdapter(this@TicketItemPreventiveMaintenanceActivity, ArrayList(), ArrayList())

        if (Global.checkForInternet(this)){

//            callComplainDetailApi()//todo comment by chanchal (not need right now due to keep edit text box with disable)

            callDefectFoundAPi()

            callReasonDefectFoundApi()

            callRemedialActionApi()

            //todo calling spare parts item list api here---
            subscribeToCustomerFilterObserver()

        }else{
            Global.warningmessagetoast(this, "Please Check Internet")
        }


        //todo add foc items---
        binding.ivAddFocItem.setOnClickListener {
            if (customerList_gl.isNotEmpty()) {

                val newItem = BodySparePart.SparePart(
                    SellType = "FOC",
                    SparePartId = "",
                    SparePartName = content,
                    PartQty = contentQuantity,
                    SpareSerialNo = focSerialNo,
                    SparePartPrice = focItemPrice
                )
                focItemsAdapter!!.addItem(newItem)
                content = ""
                contentQuantity = ""
                focSerialNo = ""
                focItemPrice = ""

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
                    SpareSerialNo = focSerialNo,
                    SparePartPrice = focItemPrice
                )
                billableItemsAdapter!!.addItem(newItem)
                content = ""
                contentQuantity = ""
                focSerialNo = ""
                focItemPrice = ""
            } else {
                Toast.makeText(this, "NOt Connected the observer", Toast.LENGTH_SHORT).show()
            }


        }



        //todo add attachment files---
        binding.ivAttachmentFiles.setOnClickListener {
//            openImageUploader()

//            chooseImageFromGallery()
            if (allPermissionsGranted()) {
                captureImageFromCamera()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }

        }


        //todo click on check box action ---

        binding.hygieneSanitizeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isHygieneSanitizeCheckbox = isChecked.toString()
            Log.e(TAG, "isHygieneSanitizeCheckbox: ${isChecked}")
        }

        binding.filterWashCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isFilterWashCheckBox = isChecked.toString()
            Log.e(TAG, "isFilterWashCheckBox: ${isChecked}")
        }

        binding.drainTubeCheckBoxItem.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isDrainTubeCheckBoxItem = isChecked.toString()
            Log.e(TAG, "isDrainTubeCheckBoxItem: ${isChecked}")
        }

        binding.leakagepointCheckBoxItem.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isLeakagePointCheckBoxItem = isChecked.toString()
            Log.e(TAG, "isLeakagePointCheckBoxItem: ${isChecked}")
        }

        binding.harnessCheckBoxItem.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isHarnessCheckBoxItem = isChecked.toString()
            Log.e(TAG, "isHarnessCheckBoxItem: ${isChecked}")
        }


        //todo click on radio button --
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            radioText = radio.text.toString()
//            Toast.makeText(applicationContext, "On checked change :" + " ${radio.text}", Toast.LENGTH_SHORT).show()

        }

        //todo submit report--

        binding.submitBtn.setOnClickListener {

            if (validation(binding.edtComplainDetail.text.toString(), defectFoundVal, reasonDefectFoundVal, remedialActionVal,binding.edtMembrane.text.toString(),binding.edtRejected.text.toString(), binding.edtROPump.text.toString(),
                    binding.edtRawPPM.text.toString(), binding.edtTreatedPPM.text.toString(), binding.edtHotWater.text.toString(), binding.edtColdWater.text.toString(),
                    binding.edtName.text.toString(), binding.edtNumber.text.toString(), binding.edtCustomerName.text.toString(), binding.edtCustomerNumber.text.toString())) {


                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()

                var sparePartList = mutableListOf<BodySparePart.SparePart>()
                sparePartList.addAll(focItemsAdapter!!.getAttachList())

                var billableList = mutableListOf<BodySparePart.SparePart>()
                billableList.addAll(billableItemsAdapter!!.getAttachList())

                Log.e("LIST>>>>>>", "onCreate:${sparePartList} ")

                // Assuming sparePartList is a list of SparePart objects
                val sparePartListJsonArray = sparePartList.map {
                    JSONObject().apply {
                        put("SellType", it.SellType)
                        put("SparePartId", it.SparePartId)
                        put("SparePartName", it.SparePartName)
                        put("PartQty", it.PartQty)
                    }
                }

                val jsonObject = JSONObject().apply {
                    put("TicketId", dataModel.TicketId)
                    put("ReportType", Flag)
                    put("ItemSerialNo", dataModel.SerialNo)
                    put("ItemCode", dataModel.ItemCode)
                    put("ClientComplainDetail", binding.edtComplainDetail.text.toString())
                    put("DefectFound", defectFoundVal)
                    put("DefectReason",reasonDefectFoundVal)
                    put("RemedialAction", remedialActionVal)
                    put("OtherClientComplain", binding.edtComplainDetail.text.toString())
                    put("OtherDefectFound", binding.edtDefectFound.text.toString())
                    put("OtherDefectReason",binding.edtReasonDefect.text.toString())
                    put("OtherRemedialAction", binding.edtRemedialAction.text.toString())
                    put("Membrane", binding.edtMembrane.text.toString().trim())
                    put("Rejected", binding.edtRejected.text.toString().trim())
                    put("ROPump", binding.edtROPump.text.toString().trim())
                    put("Raw", binding.edtRawPPM.text.toString().trim())
                    put("Treated", binding.edtTreatedPPM.text.toString())
                    put("HotWater", binding.edtHotWater.text.toString().trim())
                    put("ColdWater", binding.edtColdWater.text.toString().trim())
                    put("is_HygieneCleansing", isHygieneSanitizeCheckbox)
                    put("is_FilterWash", isFilterWashCheckBox)
                    put("is_DrainCheck", isDrainTubeCheckBoxItem)
                    put("is_LeakageCheck", isLeakagePointCheckBoxItem)
                    put("is_HarnessCheck", isHarnessCheckBoxItem)
                    put("EngineerName", binding.edtName.text.toString())
                    put("EngineerNumber", binding.edtNumber.text.toString().trim())
                    put("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())
                    put("CustomerName", binding.edtCustomerName.text.toString().trim())
                    put("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    put("CustomerRemark", binding.edtCustomerRemark.text.toString().trim())
                    put("SpartPart", sparePartListJsonArray)
                }

                //todo Convert the JSONObject to a JSON string
                val jsonString = jsonObject.toString()
                Log.e("REQUEST>>>>>", "onCreate: $jsonString")

                try {
                    val builder = MultipartBody.Builder()
                    builder.setType(MultipartBody.FORM)


                    builder.addFormDataPart("TicketId", dataModel.TicketId)
                    builder.addFormDataPart("ReportType", Flag)
                    builder.addFormDataPart("ItemSerialNo", dataModel.SerialNo)
                    builder.addFormDataPart("ItemCode", dataModel.ItemCode)
                    builder.addFormDataPart("ClientComplainDetail", binding.edtComplainDetail.text.toString())//complainDetailVal
                    builder.addFormDataPart("DefectFound", defectFoundVal)
                    builder.addFormDataPart("DefectReason", reasonDefectFoundVal)
                    builder.addFormDataPart("RemedialAction", remedialActionVal)
                    builder.addFormDataPart("OtherClientComplain", binding.edtComplainDetail.text.toString())
                    builder.addFormDataPart("OtherDefectFound", binding.edtDefectFound.text.toString())
                    builder.addFormDataPart("OtherDefectReason",binding.edtReasonDefect.text.toString())
                    builder.addFormDataPart("OtherRemedialAction", binding.edtRemedialAction.text.toString())
                    builder.addFormDataPart("Membrane", binding.edtMembrane.text.toString().trim())
                    builder.addFormDataPart("Rejected", binding.edtRejected.text.toString().trim())
                    builder.addFormDataPart("ROPump", binding.edtROPump.text.toString().trim())
                    builder.addFormDataPart("Raw", binding.edtRawPPM.text.toString().trim())
                    builder.addFormDataPart("Treated", binding.edtTreatedPPM.text.toString())
                    builder.addFormDataPart("HotWater", binding.edtHotWater.text.toString().trim())
                    builder.addFormDataPart("ColdWater", binding.edtColdWater.text.toString().trim())
                    builder.addFormDataPart("is_HygieneCleansing", isHygieneSanitizeCheckbox)
                    builder.addFormDataPart("is_FilterWash", isFilterWashCheckBox)
                    builder.addFormDataPart("is_DrainCheck", isDrainTubeCheckBoxItem)
                    builder.addFormDataPart("is_LeakageCheck", isLeakagePointCheckBoxItem)
                    builder.addFormDataPart("is_HarnessCheck", isHarnessCheckBoxItem)
                    builder.addFormDataPart("EngineerName", binding.edtName.text.toString())
                    builder.addFormDataPart("EngineerNumber", binding.edtNumber.text.toString().trim())
                    builder.addFormDataPart("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())
                    builder.addFormDataPart("CustomerName", binding.edtCustomerName.text.toString().trim())
                    builder.addFormDataPart("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    builder.addFormDataPart("CustomerRemark", binding.edtCustomerRemark.text.toString().trim())

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
                                sparePartObject.put("SparePartPrice", sparePart.SparePartPrice)
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
                                billablePartObject.put("SparePartPrice", sparePart.SparePartPrice)

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

                }

                catch (e: java.lang.Exception) {
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
                            focItemsAdapter = AddFOCPartsItemsAdapter(this@TicketItemPreventiveMaintenanceActivity, mutableListOf(), customerList_gl)
                            bindFocItemAdapter()

                            billableItemsAdapter = AddBillableItemsAdapter(this@TicketItemPreventiveMaintenanceActivity, mutableListOf(), customerList_gl)
                            bindBillableItemAdapter()

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SpareItemListApiModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@TicketItemPreventiveMaintenanceActivity,
                    t.message.toString()
                )
            }
        })
    }


    //todo set request type api hre--
    var complainDetailList = java.util.ArrayList<ComplainDetailResponseModel.DataX>()

    fun callComplainDetailApi(){
        val call: Call<ComplainDetailResponseModel> = ApiClient().service.getAllComplainDetail()
        call.enqueue(object : Callback<ComplainDetailResponseModel?> {
            override fun onResponse(
                call: Call<ComplainDetailResponseModel?>,
                response: Response<ComplainDetailResponseModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        if (response.body()?.data!!.isNotEmpty()) {
                            complainDetailList.clear()
                            complainDetailList.addAll(response.body()!!.data)
                            complainDetailList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))

                            binding.complainDetailSpinSearch.setSelection(Global.getRequestType(complainDetailList, ticketData.SubType))


//                            var requestTypeAdapter = RequestTypeAdapter(this@TicketItemPreventiveMaintenanceActivity, complainDetailList)
//                            binding.complainDetailSpinSearch.adapter = requestTypeAdapter

                            binding.complainDetailSpinSearch.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                                    val selectedItem: ComplainDetailResponseModel.DataX = complainDetailList[position]

                                    complainDetailVal = selectedItem.Name

                                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $complainDetailVal--$position")

                                    if (complainDetailVal == "Other"){
                                        binding.complainDetailLayout.visibility = View.VISIBLE
                                    }else{
                                        binding.complainDetailLayout.visibility = View.GONE
                                        binding.edtComplainDetail.setText("")
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}

                            }


                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@TicketItemPreventiveMaintenanceActivity,
                    t.message.toString()
                )
            }
        })
    }


    //todo set defect Found type api hre--
    var defectFoundList = java.util.ArrayList<ComplainDetailResponseModel.DataX>()

    var defectFoundVal = ""

    fun callDefectFoundAPi(){
        val call: Call<ComplainDetailResponseModel> = ApiClient().service.getAllDefectFoundList()
        call.enqueue(object : Callback<ComplainDetailResponseModel?> {
            override fun onResponse(
                call: Call<ComplainDetailResponseModel?>,
                response: Response<ComplainDetailResponseModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        if (response.body()?.data!!.isNotEmpty()) {
                            defectFoundList.clear()
                            defectFoundList.add(ComplainDetailResponseModel.DataX("", "Select an Option", "", "", 0))
                            defectFoundList.addAll(response.body()!!.data)
                            defectFoundList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))


                            var requestTypeAdapter = DefectFoundAdapter(this@TicketItemPreventiveMaintenanceActivity, defectFoundList)
                            binding.defectFoundSpinSearch.adapter = requestTypeAdapter


//                            binding.defectFoundSpinSearch.adapter = NothingSelectedSpinnerAdapter(requestTypeAdapter, R.layout.drop_down_item_textview, R.layout.drop_down_item_textview, this@TicketItemPreventiveMaintenanceActivity)

                            binding.defectFoundSpinSearch.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                                    val selectedItem: ComplainDetailResponseModel.DataX = defectFoundList[position]

                                    if (selectedItem.Name == "Select an Option"){
                                        Global.warningmessagetoast(this@TicketItemPreventiveMaintenanceActivity, "Choose an Other Option")
                                    }else{
                                        defectFoundVal = selectedItem.Name
                                    }

                                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $defectFoundVal--$position")

                                    if (defectFoundVal == "Other"){
                                        binding.defectFoundLayout.visibility = View.VISIBLE
                                    }else{
                                        binding.defectFoundLayout.visibility = View.GONE
                                        binding.edtDefectFound.setText("")
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@TicketItemPreventiveMaintenanceActivity,
                    t.message.toString()
                )
            }
        })
    }


    //todo set reason defect Found type api hre--
    var reasonDefectFoundList = java.util.ArrayList<ComplainDetailResponseModel.DataX>()

    var reasonDefectFoundVal = ""

    fun callReasonDefectFoundApi(){
        val call: Call<ComplainDetailResponseModel> = ApiClient().service.getAllReasonDefectFoundList()
        call.enqueue(object : Callback<ComplainDetailResponseModel?> {
            override fun onResponse(
                call: Call<ComplainDetailResponseModel?>,
                response: Response<ComplainDetailResponseModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        if (response.body()?.data!!.isNotEmpty()) {
                            reasonDefectFoundList.clear()
                            reasonDefectFoundList.add(ComplainDetailResponseModel.DataX("", "Select an Option", "", "", 0))
                            reasonDefectFoundList.addAll(response.body()!!.data)
                            reasonDefectFoundList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))

                            var requestTypeAdapter = ReasonDefectFoundAdapter(this@TicketItemPreventiveMaintenanceActivity, reasonDefectFoundList)
                            binding.reasonDefectFoundSpinSearch.adapter = requestTypeAdapter

                            binding.reasonDefectFoundSpinSearch.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                                    val selectedItem: ComplainDetailResponseModel.DataX = reasonDefectFoundList[position]

                                    if (selectedItem.Name == "Select an Option"){
                                        Global.warningmessagetoast(this@TicketItemPreventiveMaintenanceActivity, "Choose an Other Option")
                                    }else{
                                        reasonDefectFoundVal = selectedItem.Name
                                    }


                                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $reasonDefectFoundVal--$position")

                                    if (reasonDefectFoundVal == "Other"){
                                        binding.reasonDefectLayout.visibility = View.VISIBLE
                                    }else{
                                        binding.reasonDefectLayout.visibility = View.GONE
                                        binding.edtReasonDefect.setText("")
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@TicketItemPreventiveMaintenanceActivity,
                    t.message.toString()
                )
            }
        })
    }


    //todo set remedial action type api hre--
    var remedialActionList = java.util.ArrayList<ComplainDetailResponseModel.DataX>()

    var remedialActionVal = ""

    fun callRemedialActionApi(){
        val call: Call<ComplainDetailResponseModel> = ApiClient().service.getAllRemedialActionList()
        call.enqueue(object : Callback<ComplainDetailResponseModel?> {
            override fun onResponse(
                call: Call<ComplainDetailResponseModel?>,
                response: Response<ComplainDetailResponseModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        if (response.body()?.data!!.isNotEmpty()) {
                            remedialActionList.clear()
                            remedialActionList.add(ComplainDetailResponseModel.DataX("", "Select an Option", "", "", 0))
                            remedialActionList.addAll(response.body()!!.data)
                            remedialActionList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))


                            var requestTypeAdapter = RemedialActionAdapter(this@TicketItemPreventiveMaintenanceActivity, remedialActionList)
                            binding.remedialActionSpinSearch.adapter = requestTypeAdapter

                            binding.remedialActionSpinSearch.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                                    val selectedItem: ComplainDetailResponseModel.DataX = remedialActionList[position]

                                    if (selectedItem.Name == "Select an Option"){
                                        Global.warningmessagetoast(this@TicketItemPreventiveMaintenanceActivity, "Choose an Other Option")
                                    }else{
                                        remedialActionVal = selectedItem.Name
                                    }

                                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $remedialActionVal--$position")

                                    if (remedialActionVal == "Other"){
                                        binding.remedialActionLayout.visibility = View.VISIBLE
                                    }else{
                                        binding.remedialActionLayout.visibility = View.GONE
                                        binding.edtRemedialAction.setText("")
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@TicketItemPreventiveMaintenanceActivity,
                    t.message.toString()
                )
            }
        })
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
                binding.loadingback.visibility = View.VISIBLE
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


    //todo bind foc items parts adapter--
    private fun bindFocItemAdapter() = binding.rvSpareParts.apply {
        adapter = focItemsAdapter
        layoutManager = LinearLayoutManager(this@TicketItemPreventiveMaintenanceActivity)

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
        layoutManager = LinearLayoutManager(this@TicketItemPreventiveMaintenanceActivity)

        //todo remove billable items---
        if (billableItemsAdapter != null){
            billableItemsAdapter!!.setOnItemMinusClickListener { s, i ->
                if (billableItemsAdapter!!.itemCount > 0) {
                    billableItemsAdapter!!.removeItem(i)
                }
            }
        }
    }


    private fun reqPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {

                    Log.e("Permission","Success")
                    // Handle the result of permission checking if needed
                    // For example, you can check if all permissions were granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Log.e("Permission","Success-1")
                        // Permissions are granted, you can proceed with your logic
                    } else {
                        // Handle the case when some or all permissions are denied
                        Log.e("Permission","Success-2")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                    Log.e("Permission","Not")
                }


            })
            .check()
    }


    private fun openImageUploader() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        Matisse.from(this@TicketItemPreventiveMaintenanceActivity)
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
                    } else {
                        Toast.makeText(this@TicketItemPreventiveMaintenanceActivity, "Please enable permission", Toast.LENGTH_SHORT).show()
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

                    Log.e(TAG, "onActivityResult: "+mArrayUriList.size )
                    path.clear()

                    for (i in mArrayUriList.indices) {
                        path.add(FileUtils.getPath(this@TicketItemPreventiveMaintenanceActivity, mArrayUriList.get(i)).toString())
                    }
//                    mArrayUriList.add(fileUri)

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



  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && null != data) {
            //mSelected.add(data.getData());
            mSelectedList = Matisse.obtainResult(data) as ArrayList<Uri>
            mArrayUriList.addAll(mSelectedList)
            for (i in mArrayUriList.indices) {
                path.add(FileUtils.getPath(this@TicketItemPreventiveMaintenanceActivity, mArrayUriList.get(i)).toString())
            }
            val adapter = ImageViewAdapter(this, mArrayUriList)
            binding.rvAttachment.setLayoutManager(
                GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            )
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
                    path.add(FileUtils.getPath(this@TicketItemPreventiveMaintenanceActivity, mArrayUriList.get(i)).toString())
                }
                val adapter = ImageViewAdapter(this, mArrayUriList)
                binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
                binding.rvAttachment.setAdapter(adapter)

            } else {
                val imageurl: Uri = data.data!!
                mArrayUriList.add(imageurl)
                Log.e(TAG, "onActivityResult: "+mArrayUriList.size )
                for (i in mArrayUriList.indices) {
                    path.add(FileUtils.getPath(this@TicketItemPreventiveMaintenanceActivity, mArrayUriList.get(i)).toString())
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


    private fun validation(
        complaintClient: String,
        defetctFound: String,
        reasonFound: String,
        remedialAction: String,
        membrane: String,
        rejected: String,
        roPump: String,
        rawPPM: String,
        treatedPPM: String,
        hotWater: String,
        coldWater: String,
        serviceNAme: String,
        serviceNumber: String,
        customerName: String,
        customerNUmber: String,
    ): Boolean {

       /* if (spareAdapter!!.getAttachList().isEmpty()) {
            Global.warningmessagetoast(this, "Select Atleast One Spare Item")
            return false
        } else if (mArrayUriList.size == 0) {
            Global.warningmessagetoast(this, "Select Atleast One Attachment")
            return false
        } else*/

        /*if (radioText.isEmpty()) {
            Global.warningmessagetoast(this, "Select One Spare Type")
            return false
        } else*/
        if (complaintClient.isEmpty()) {
            Global.warningmessagetoast(this, "Complain Detail can't be Empty")
            return false
        } else if (defetctFound.isEmpty()) {
            Global.warningmessagetoast(this, "Defect Found can't be Empty")
            return false
        }
        else if (defetctFound == "Select an Option") {
            Global.warningmessagetoast(this, "Choose an Other Option")
            return false
        } else if (reasonFound.isEmpty()) {
            Global.warningmessagetoast(this, "Reason Defect can't be Empty")
            return false
        }else if (reasonFound == "Select an Option"){
            Global.warningmessagetoast(this, "Choose an Other Option")
            return false
        }else if (remedialAction.isEmpty()) {
            Global.warningmessagetoast(this, "Remedial Action can't be Empty")
            return false
        }else if (remedialAction == "Select an Option"){
            Global.warningmessagetoast(this, "Choose an Other Option")
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
        } else if (rawPPM.isEmpty()) {
            Global.warningmessagetoast(this, "Raw PPM can't be Empty")
            return false
        } else if (treatedPPM.isEmpty()) {
            Global.warningmessagetoast(this, "Treated PPM can't be Empty")
            return false
        } else if (hotWater.isEmpty()) {
            Global.warningmessagetoast(this, "Hot Water can't be Empty")
            return false
        } else if (coldWater.isEmpty()) {
            Global.warningmessagetoast(this, "Cold Water can't be Empty")
            return false
        } else if (serviceNAme.isEmpty()) {
            Global.warningmessagetoast(this, "Service Name can't be Empty")
            return false
        } else if (serviceNumber.isEmpty()) {
            Global.warningmessagetoast(this, "Service Number can't be Empty")
            return false
        } else if (customerName.isEmpty()) {
            Global.warningmessagetoast(this, "Customer Name can't be Empty")
            return false
        } else if (customerNUmber.isEmpty()) {
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


    companion object {
        private const val TAG = "TicketItemPreventiveMai"
    }

}