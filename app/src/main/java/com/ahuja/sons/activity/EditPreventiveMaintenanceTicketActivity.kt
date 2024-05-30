package com.ahuja.sons.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ticketItemAdapter.*
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityEditPreventiveMaintenanceTicketBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.ComplainDetailResponseModel
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.newapimodel.SpareItemListApiModel
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.newapimodel.TicketPreventiveMaintainanceResponse
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPreventiveMaintenanceTicketActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditPreventiveMaintenanceTicketBinding
    lateinit var viewModel: MainViewModel
    var dataModel = ItemAllListResponseModel.DataXXX()
    var mArrayUriList: kotlin.collections.ArrayList<Uri> = ArrayList()
    var path: ArrayList<String> = ArrayList()
    var index = 0;
    var content = ""
    var contentQuantity = ""
    var focSerialNo = ""
    var focItemPrice = ""
    var localList = mutableListOf<SpareCustomModel>()
    var focItemAdapter: EditFOCItemsAdapter? = null
    var billableItemsAdapter: EditBillableItemsAdapter? = null
    private val REQUEST_CODE_CHOOSE = 1000
    var isHygieneSanitizeCheckbox = "false"
    var isFilterWashCheckBox = "false"
    var isDrainTubeCheckBoxItem = "false"
    var isLeakagePointCheckBoxItem = "false"
    var isHarnessCheckBoxItem = "false"
    var Flag = ""
    var radioText = ""
    var ticketData = TicketData()

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPreventiveMaintenanceTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

//        binding.loadingback.visibility = View.GONE

        dataModel = intent.getSerializableExtra("data")!! as ItemAllListResponseModel.DataXXX
//        ticketData = intent.getSerializableExtra("ticketData")!! as TicketData
        Flag = intent.getStringExtra("ticketType")!!

        if ((Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
            supportActionBar!!.title = "View Report"
            binding.submitBtn.visibility = View.GONE
        }

//        spareAdapter = EditSparePartsAdapter(this, mutableListOf())

        binding.ivAddFocItem.setOnClickListener {
            val newItem = SparePart(
                SellType = "FOC",
                SparePartId = "",
                SparePartName = content,
                PartQty = contentQuantity,
                ServiceReportId = "",
                SpareSerialNo = focSerialNo,
                SparePartPrice = focItemPrice,
                ItemCode = "",
                ItemSerialNo = "",
                TicketId = "",
                id = "",
            )
            focItemAdapter!!.addItem(newItem)
            content = ""
            contentQuantity = ""

        }

        //todo add billable items---
        binding.ivAddBillableItems.setOnClickListener {
            val newItem = SparePart(
                SellType = "Billable",
                SparePartId = "",
                SparePartName = content,
                PartQty = contentQuantity,
                ServiceReportId = "",
                SpareSerialNo = focSerialNo,
                SparePartPrice = focItemPrice,
                ItemCode = "",
                ItemSerialNo = "",
                TicketId = "",
                id = ""
            )
            billableItemsAdapter!!.addItem(newItem)
            content = ""
            contentQuantity = ""

        }


        binding.ivAttachmentFiles.visibility = View.INVISIBLE


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

        }


        if (dataModel != null) {
            //todo set default data--

            var jsonObject = JsonObject()
            jsonObject.addProperty("TicketId", dataModel.TicketId)
            jsonObject.addProperty("ReportType", Flag)
            jsonObject.addProperty("ItemSerialNo", dataModel.SerialNo)
            jsonObject.addProperty("ItemCode", dataModel.ItemCode)

            viewModel.ticketMaintenanceOneApi(jsonObject)
            bindDefaultObserver()
        }


        //todo update maintenance---
        binding.submitBtn.setOnClickListener {
            callRequestPayload()
        }

    }


    var customerList_gl = ArrayList<SpareItemListApiModel.DataXXX>()

    fun subscribeToCustomerFilterObserver(sparePart: List<SparePart>) {
        binding.loadingback.visibility = View.VISIBLE
        binding.loadingView.start()
        val call: Call<SpareItemListApiModel> = ApiClient().service.allSparePartApiList()
        call.enqueue(object : Callback<SpareItemListApiModel?> {
            override fun onResponse(
                call: Call<SpareItemListApiModel?>,
                response: Response<SpareItemListApiModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        binding.loadingback.visibility = View.GONE
                        binding.loadingView.stop()
                        if (response.body()?.data!!.isNotEmpty()) {
                            customerList_gl.clear()
                            customerList_gl.addAll(response.body()!!.data)
                            focItemAdapter = EditFOCItemsAdapter(this@EditPreventiveMaintenanceTicketActivity, mutableListOf(), customerList_gl)
                            bindFocItemAdapter()
                            //todo calling for loop for bins spare items ---

                            if (sparePart.isNotEmpty()) {
                                for (item in sparePart) {
                                    if (item.SellType == "FOC") {
                                        focItemAdapter!!.addItem(item)
                                    }
                                }
                            }


                            billableItemsAdapter = EditBillableItemsAdapter(this@EditPreventiveMaintenanceTicketActivity, mutableListOf(), customerList_gl)
                            bindBillableItemAdapter()

                            if (sparePart.isNotEmpty()) {
                                for (item in sparePart) {
                                    if (item.SellType == "Billable") {
                                        billableItemsAdapter!!.addItem(item)
                                    }
                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SpareItemListApiModel?>, t: Throwable) {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                Global.errormessagetoast(
                    this@EditPreventiveMaintenanceTicketActivity,
                    t.message.toString()
                )
            }
        })
    }


    //todo set request type api hre--
    var complainDetailVal = ""

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

//                            var requestTypeAdapter = RequestTypeAdapter(this@EditPreventiveMaintenanceTicketActivity, complainDetailList)
//                            binding.complainDetailSpinSearch.adapter = requestTypeAdapter

                            binding.complainDetailSpinSearch.setSelection(Global.getRequestType(complainDetailList, defaultDataModel.ClientComplainDetail))

                            complainDetailVal = defaultDataModel.ClientComplainDetail

                            if (defaultDataModel.ClientComplainDetail == "Other"){
                                binding.complainDetailLayout.visibility = View.VISIBLE
                                binding.edtComplainDetail.setText(defaultDataModel.OtherClientComplain)
                            }else{
                                binding.complainDetailLayout.visibility = View.GONE
                                binding.edtComplainDetail.setText("")
                            }


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

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    complainDetailVal = defaultDataModel.ClientComplainDetail
                                    binding.complainDetailSpinSearch.setSelection(Global.getRequestType(complainDetailList, defaultDataModel.ClientComplainDetail))
                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@EditPreventiveMaintenanceTicketActivity,
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
                            defectFoundList.addAll(response.body()!!.data)
                            defectFoundList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))


                            var requestTypeAdapter = DefectFoundAdapter(this@EditPreventiveMaintenanceTicketActivity, defectFoundList)
                            binding.defectFoundSpinSearch.adapter = requestTypeAdapter

                            binding.defectFoundSpinSearch.setSelection(Global.getRequestType(defectFoundList, defaultDataModel.DefectFound))

                            defectFoundVal = defaultDataModel.DefectFound

                            if (defaultDataModel.DefectFound == "Other"){
                                binding.defectFoundLayout.visibility = View.VISIBLE
                                binding.edtDefectFound.setText(defaultDataModel.OtherDefectFound)
                            }else{
                                binding.defectFoundLayout.visibility = View.GONE
                                binding.edtDefectFound.setText("")
                            }

                            binding.defectFoundSpinSearch.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                                    val selectedItem: ComplainDetailResponseModel.DataX = defectFoundList[position]

                                    defectFoundVal = selectedItem.Name

                                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $defectFoundVal--$position")

                                    if (defectFoundVal == "Other"){
                                        binding.defectFoundLayout.visibility = View.VISIBLE
                                    }else{
                                        binding.defectFoundLayout.visibility = View.GONE
                                        binding.edtDefectFound.setText("")
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    defectFoundVal = defaultDataModel.DefectFound
                                    binding.defectFoundSpinSearch.setSelection(Global.getRequestType(defectFoundList, defaultDataModel.DefectFound))
                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@EditPreventiveMaintenanceTicketActivity,
                    t.message.toString()
                )
            }
        })
    }


    //todo set reson of defect Found type api hre--
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
                            reasonDefectFoundList.addAll(response.body()!!.data)
                            reasonDefectFoundList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))


                            var requestTypeAdapter = ReasonDefectFoundAdapter(this@EditPreventiveMaintenanceTicketActivity, reasonDefectFoundList)
                            binding.reasonDefectFoundSpinSearch.adapter = requestTypeAdapter

                            binding.reasonDefectFoundSpinSearch.setSelection(Global.getRequestType(reasonDefectFoundList, defaultDataModel.DefectReason))

                            reasonDefectFoundVal = defaultDataModel.DefectReason

                            if (defaultDataModel.DefectReason == "Other"){
                                binding.reasonDefectLayout.visibility = View.VISIBLE
                                binding.edtReasonDefect.setText(defaultDataModel.OtherDefectReason)
                            }else{
                                binding.reasonDefectLayout.visibility = View.GONE
                                binding.edtReasonDefect.setText("")
                            }

                            binding.reasonDefectFoundSpinSearch.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                                    val selectedItem: ComplainDetailResponseModel.DataX = reasonDefectFoundList[position]

                                    reasonDefectFoundVal = selectedItem.Name

                                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $reasonDefectFoundVal--$position")

                                    if (reasonDefectFoundVal == "Other"){
                                        binding.reasonDefectLayout.visibility = View.VISIBLE
                                    }else{
                                        binding.reasonDefectLayout.visibility = View.GONE
                                        binding.edtReasonDefect.setText("")
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    reasonDefectFoundVal = defaultDataModel.DefectReason
                                    binding.reasonDefectFoundSpinSearch.setSelection(Global.getRequestType(reasonDefectFoundList, defaultDataModel.DefectReason))

                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@EditPreventiveMaintenanceTicketActivity,
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
                            remedialActionList.addAll(response.body()!!.data)
                            remedialActionList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))


                            var requestTypeAdapter = RemedialActionAdapter(this@EditPreventiveMaintenanceTicketActivity, remedialActionList)
                            binding.remedialActionSpinSearch.adapter = requestTypeAdapter

                            binding.remedialActionSpinSearch.setSelection(Global.getRequestType(remedialActionList, defaultDataModel.RemedialAction))

                            remedialActionVal = defaultDataModel.RemedialAction

                            if (defaultDataModel.RemedialAction == "Other"){
                                binding.remedialActionLayout.visibility = View.VISIBLE
                                binding.edtRemedialAction.setText(defaultDataModel.OtherRemedialAction)
                            }else{
                                binding.remedialActionLayout.visibility = View.GONE
                                binding.edtRemedialAction.setText("")
                            }

                            binding.remedialActionSpinSearch.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                                    val selectedItem: ComplainDetailResponseModel.DataX = remedialActionList[position]

                                    remedialActionVal = selectedItem.Name

                                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $remedialActionVal--$position")

                                    if (remedialActionVal == "Other"){
                                        binding.remedialActionLayout.visibility = View.VISIBLE
                                    }else{
                                        binding.remedialActionLayout.visibility = View.GONE
                                        binding.edtRemedialAction.setText("")
                                    }

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    binding.remedialActionSpinSearch.setSelection(Global.getRequestType(remedialActionList, defaultDataModel.RemedialAction))

                                    remedialActionVal = defaultDataModel.RemedialAction
                                }
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(this@EditPreventiveMaintenanceTicketActivity, t.message.toString())
            }
        })
    }



    private fun callRequestPayload() {

        if (validation(
                binding.edtComplainDetail.text.toString(),
                defectFoundVal,
                reasonDefectFoundVal,
                remedialActionVal,
                binding.edtMembrane.text.toString(),
                binding.edtRejected.text.toString(),
                binding.edtROPump.text.toString(),
                binding.edtRawPPM.text.toString(),
                binding.edtTreatedPPM.text.toString(),
                binding.edtHotWater.text.toString(),
                binding.edtColdWater.text.toString(),
                binding.edtName.text.toString(),
                binding.edtNumber.text.toString(),
                binding.edtCustomerName.text.toString(),
                binding.edtCustomerNumber.text.toString(),
            )
        ) {

            binding.loadingback.visibility = View.VISIBLE
            binding.loadingView.start()

            var sparePartList = mutableListOf<SparePart>()
            sparePartList.addAll(focItemAdapter!!.getAttachList())

            var billableList = mutableListOf<SparePart>()
            billableList.addAll(billableItemsAdapter!!.getAttachList())

            Log.e("FOC_LIST>>>>>>", "onCreate:${sparePartList} ")
            Log.e("Billable_LIST>>>>>>", "onCreate:${billableList} ")

            try {
                val jsonObject = JsonObject().apply {
                    addProperty("id", defaultDataModel.id)
                    addProperty("TicketId", dataModel.TicketId)
                    addProperty("ReportType", Flag)
                    addProperty("ItemSerialNo", dataModel.SerialNo)
                    addProperty("ItemCode", dataModel.ItemCode)
                    addProperty("ClientComplainDetail",  binding.edtComplainDetail.text.toString())
                    addProperty("DefectFound", defectFoundVal)
                    addProperty("DefectReason", reasonDefectFoundVal)
                    addProperty("RemedialAction", remedialActionVal)
                    addProperty("OtherClientComplain", binding.edtComplainDetail.text.toString())
                    addProperty("OtherDefectFound", binding.edtDefectFound.text.toString())
                    addProperty("OtherDefectReason",binding.edtReasonDefect.text.toString())
                    addProperty("OtherRemedialAction", binding.edtRemedialAction.text.toString())
                    addProperty("Membrane", binding.edtMembrane.text.toString().trim())
                    addProperty("Rejected", binding.edtRejected.text.toString().trim())
                    addProperty("ROPump", binding.edtROPump.text.toString().trim())
                    addProperty("Raw", binding.edtRawPPM.text.toString().trim())
                    addProperty("Treated", binding.edtTreatedPPM.text.toString())
                    addProperty("HotWater", binding.edtHotWater.text.toString().trim())
                    addProperty("ColdWater", binding.edtColdWater.text.toString().trim())
                    addProperty("is_HygieneCleansing", isHygieneSanitizeCheckbox)
                    addProperty("is_FilterWash", isFilterWashCheckBox)
                    addProperty("is_DrainCheck", isDrainTubeCheckBoxItem)
                    addProperty("is_LeakageCheck", isLeakagePointCheckBoxItem)
                    addProperty("is_HarnessCheck", isHarnessCheckBoxItem)
                    addProperty("EngineerName", binding.edtName.text.toString())
                    addProperty("EngineerNumber", binding.edtNumber.text.toString().trim())
                    addProperty("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())
                    addProperty("CustomerName", binding.edtCustomerName.text.toString().trim())
                    addProperty("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    addProperty("CustomerRemark", binding.edtCustomerRemark.text.toString().trim())
                    addProperty("File", "")

                    val sparePartsArray = JsonArray()

                    if (sparePartList.isNotEmpty() || billableList.isNotEmpty()) {

                        if (sparePartList.isNotEmpty()){

                            for (sparePart in sparePartList) {

                                val sparePartObject = JsonObject()
                                sparePartObject.addProperty("SellType", sparePart.SellType)
                                sparePartObject.addProperty("SparePartId", sparePart.SparePartId)
                                sparePartObject.addProperty("SparePartName", sparePart.SparePartName)
                                sparePartObject.addProperty("SpareSerialNo", sparePart.SpareSerialNo)
                                sparePartObject.addProperty("SparePartPrice,", sparePart.SparePartPrice)
                                sparePartObject.addProperty("PartQty", sparePart.PartQty)
                                sparePartObject.addProperty("ServiceReportId", sparePart.ServiceReportId)
                                sparePartObject.addProperty("ItemCode", sparePart.ItemCode)
                                sparePartObject.addProperty("ItemSerialNo", sparePart.ItemSerialNo)
                                sparePartObject.addProperty("TicketId", sparePart.TicketId)
                                sparePartObject.addProperty("id", sparePart.id)
                                // Add the individual spare part objects to the array
                                sparePartsArray.add(sparePartObject)
                            }
                        }

                        if (billableList.isNotEmpty()){

                            for (sparePart in billableList) {

                                val billablePartObject = JsonObject()
                                billablePartObject.addProperty("SellType", sparePart.SellType)
                                billablePartObject.addProperty("SparePartId", sparePart.SparePartId)
                                billablePartObject.addProperty("SparePartName", sparePart.SparePartName)
                                billablePartObject.addProperty("PartQty", sparePart.PartQty)
                                billablePartObject.addProperty("SpareSerialNo", sparePart.SpareSerialNo)
                                billablePartObject.addProperty("SparePartPrice,", sparePart.SparePartPrice)
                                billablePartObject.addProperty("ServiceReportId", sparePart.ServiceReportId)
                                billablePartObject.addProperty("ItemCode", sparePart.ItemCode)
                                billablePartObject.addProperty("ItemSerialNo", sparePart.ItemSerialNo)
                                billablePartObject.addProperty("TicketId", sparePart.TicketId)
                                billablePartObject.addProperty("id", sparePart.id)
                                // Add the individual spare part objects to the array
                                sparePartsArray.add(billablePartObject)
                            }
                        }


                        addProperty("SpartPart", sparePartsArray.toString())
                    } else {
                        addProperty("SpartPart", "")
                    }


                }

                //todo Convert the JSONObject to a JSON string
                val jsonString = jsonObject.toString()
                Log.e("REQUEST>>>>>", "onCreate: $jsonString")

                viewModel.updateTicketTypeItems(jsonObject)
                bindUpdateObserver()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }


    }


    //todo calling update observer---
    private fun bindUpdateObserver() {
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


    //todo bind foc items parts adapter--
    private fun bindFocItemAdapter() = binding.rvSpareParts.apply {
        adapter = focItemAdapter!!
        layoutManager = LinearLayoutManager(this@EditPreventiveMaintenanceTicketActivity, LinearLayoutManager.VERTICAL, false)

        //todo remove foc items--
        if (focItemAdapter != null){
            focItemAdapter!!.setOnItemMinusClickListener { s, i ->
                if (focItemAdapter!!.itemCount > 0) {
                    focItemAdapter!!.removeItem(i)
                }
            }
        }

    }


    //todo bind billable items parts adapter--
    private fun bindBillableItemAdapter() = binding.rvBillableItemParts.apply {
        adapter = billableItemsAdapter
        layoutManager = LinearLayoutManager(this@EditPreventiveMaintenanceTicketActivity)

        //todo remove billable items---
        if (billableItemsAdapter != null){
            billableItemsAdapter!!.setOnItemMinusClickListener { s, i ->
                if (billableItemsAdapter!!.itemCount > 0) {
                    billableItemsAdapter!!.removeItem(i)
                }
            }
        }
    }



    //todo bind default observer---
    var defaultDataModel = TicketPreventiveMaintainanceResponse.DataXXX()

    private fun bindDefaultObserver() {
        viewModel.preventiveMaintainanceOneData.observe(this, Event.EventObserver(
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
                    if (response.data.size > 0 && response.data != null) {
                        setDefaultData(response.data[0])
                        defaultDataModel = response.data[0]
                        subscribeToCustomerFilterObserver(response.data[0].SparePart)

//                        callComplainDetailApi() //todo comment by chanchal (not need right now due to keep edit text box with disable)

                        callDefectFoundAPi()

                        callReasonDefectFoundApi()

                        callRemedialActionApi()

                    }
//                    Global.successmessagetoast(this, "Successful")
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this, response.message)
                }
            }
        ))
    }


    //todo set default data---
    private fun setDefaultData(data: TicketPreventiveMaintainanceResponse.DataXXX) {

        binding.edtComplainDetail.setText(data.ClientComplainDetail)
        binding.edtComplainDetail.isEnabled = false
        binding.edtComplainDetail.isClickable = false
        binding.edtComplainDetail.isFocusableInTouchMode = false

        binding.edtMembrane.setText(data.Membrane)
        binding.edtRejected.setText(data.Rejected)
        binding.edtROPump.setText(data.ROPump)

        binding.edtRawPPM.setText(data.Raw)
        binding.edtTreatedPPM.setText(data.Treated)

        binding.edtHotWater.setText(data.HotWater)
        binding.edtColdWater.setText(data.ColdWater)

        binding.edtName.setText(data.EngineerName)
        binding.edtNumber.setText(data.EngineerNumber)
        binding.edtServiceEngineerRemark.setText(data.EngineerRemark)

        binding.edtCustomerName.setText(data.CustomerName)
        binding.edtCustomerNumber.setText(data.CustomerNumber)
        binding.edtCustomerRemark.setText(data.CustomerRemark)


        //todo checked check box---
        if (data.is_HygieneCleansing) {
            binding.hygieneSanitizeCheckbox.isChecked = true
            isHygieneSanitizeCheckbox = "true"
        } else {
            binding.hygieneSanitizeCheckbox.isChecked = false
            isHygieneSanitizeCheckbox = "false"
        }

        if (data.is_FilterWash) {
            binding.filterWashCheckBox.isChecked = true
            isFilterWashCheckBox = "true"
        } else {
            binding.filterWashCheckBox.isChecked = false
            isFilterWashCheckBox = "false"
        }

        if (data.is_DrainCheck) {
            binding.drainTubeCheckBoxItem.isChecked = true
            isDrainTubeCheckBoxItem = "true"
        } else {
            binding.drainTubeCheckBoxItem.isChecked = false
            isDrainTubeCheckBoxItem = "false"
        }

        if (data.is_LeakageCheck) {
            binding.leakagepointCheckBoxItem.isChecked = true
            isLeakagePointCheckBoxItem = "true"
        } else {
            binding.leakagepointCheckBoxItem.isChecked = false
            isLeakagePointCheckBoxItem = "false"
        }

        if (data.is_HarnessCheck) {
            binding.harnessCheckBoxItem.isChecked = true
            isHarnessCheckBoxItem = "true"
        } else {
            binding.harnessCheckBoxItem.isChecked = false
            isHarnessCheckBoxItem = "false"
        }

        //todo checked radio button ---

        if (data.SparePart.isNotEmpty()) {
            if (data.SparePart[0].SellType == "FOC") {
                binding.radioFOC.isChecked = true
                radioText = "FOC"
            } else {
                binding.radioBillable.isChecked = true
                radioText = "Billable"
            }
        }


        /*  var sparePartList = mutableListOf<TicketPreventiveMaintainanceResponse.SparePart>()
          sparePartList.addAll(spareAdapter!!.getAttachList())*/


        //todo attached images---

        if (data.Files.isNotEmpty()) {
            val adapter = PreviousImageViewAdapter(this, data.Files)
            binding.rvAttachment.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            binding.rvAttachment.adapter = adapter
            adapter.notifyDataSetChanged()
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

        /*if (spareAdapter!!.getAttachList().isEmpty()) {
            Global.warningmessagetoast(this, "Select Atleast One Spare Item")
            return false
        } else*/

        if (complaintClient.isEmpty()) {
            Global.warningmessagetoast(this, "Complain Detail can't be Empty")
            return false
        } else if (defetctFound.isEmpty()) {
            Global.warningmessagetoast(this, "Defect Found can't be Empty")
            return false
        } else if (reasonFound.isEmpty()) {
            Global.warningmessagetoast(this, "Reason Defect can't be Empty")
            return false
        } else if (remedialAction.isEmpty()) {
            Global.warningmessagetoast(this, "Remedial Action can't be Empty")
            return false
        } else if (membrane.isEmpty()) {
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
        private const val TAG = "EditPreventiveMaintenan"
    }

}