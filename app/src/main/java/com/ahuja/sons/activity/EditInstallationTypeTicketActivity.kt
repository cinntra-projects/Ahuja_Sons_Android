package com.ahuja.sons.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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
import com.ahuja.sons.databinding.ActivityEditInstallationTypeTicketBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.*
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditInstallationTypeTicketActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditInstallationTypeTicketBinding
    lateinit var viewModel: MainViewModel
    var dataModel = ItemAllListResponseModel.DataXXX()
    var mArrayUriList: ArrayList<Uri> = ArrayList()
    var path: ArrayList<String> = ArrayList()
    var isVantillationChecked = "false"
    var isRawWaterChecked = "false"
    var isPowerAMPChecked = "false"
    var Flag = ""
    var isPartMissingChecked = "false"
    var isDamagedPartChecked = "false"
    var content = ""
    var contentQuantity = ""
    var focSerialNo = ""
    var focItemAdapter: EditFOCItemsAdapter? = null
    var billableItemsAdapter: EditBillableItemsAdapter? = null
    var ticketData = TicketData()

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInstallationTypeTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        dataModel = intent.getSerializableExtra("Installation")!! as ItemAllListResponseModel.DataXXX
        ticketData = intent.getSerializableExtra("ticketData")!! as TicketData
        Flag = intent.getStringExtra("ticketType")!!

//        binding.loadingback.visibility = View.GONE

        if ((Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
           supportActionBar!!.title = "View Report"
            binding.submitBtn.visibility = View.GONE
        }

        binding.ivAddFocItem.setOnClickListener {
            val newItem = SparePart(
                SellType = "FOC",
                SparePartId = "",
                SparePartName = content,
                PartQty = contentQuantity,
                ServiceReportId = "",
                SpareSerialNo = focSerialNo,
                ItemCode = "",
                ItemSerialNo = "",
                TicketId = "",
                id = ""
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
                ItemCode = "",
                ItemSerialNo = "",
                TicketId = "",
                id = ""
            )
            billableItemsAdapter!!.addItem(newItem)
            content = ""
            contentQuantity = ""

        }


        binding.edtDateOfInstallation.setOnClickListener {
            Global.selectDate(this, binding.edtDateOfInstallation)
        }

        binding.ventillationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isVantillationChecked = isChecked.toString()
            Log.e(TAG, "isVantillationChecked: ${isChecked}" )
        }

        binding.rawWaterCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isRawWaterChecked = isChecked.toString()
            Log.e(TAG, "isRawWaterChecked: ${isChecked}" )
        }

        binding.powerAMPCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the TextView when the checkbox state changes
            isPowerAMPChecked = isChecked.toString()
            Log.e(TAG, "isPowerAMPChecked: ${isChecked}" )
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


        binding.ivAttachmentFiles.visibility = View.INVISIBLE

        if (Flag == "De-Installation" || Flag == "Shifting" || Flag == "Packaging"){
            binding.partMissingLayout.visibility = View.VISIBLE
            binding.damagedPartLayout.visibility = View.VISIBLE
        }else{
            binding.partMissingLayout.visibility = View.GONE
            binding.damagedPartLayout.visibility = View.GONE
            isPartMissingChecked = ""
            isDamagedPartChecked = ""
        }


        //todo set model no. adapter--
        val modelAdapter = ArrayAdapter(this@EditInstallationTypeTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
        binding.acMachineModelType.setAdapter(modelAdapter)

        //todo mode communication item selected
        binding.acMachineModelType.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.modelList_gl.isNotEmpty()) {
                    modelNoValue = Global.modelList_gl[position]
                    binding.acMachineModelType.setText(Global.modelList_gl[position])

                    val adapter = ArrayAdapter(this@EditInstallationTypeTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
                    binding.acMachineModelType.setAdapter(adapter)
                } else {
                    modelNoValue = ""
                    binding.acMachineModelType.setText("")
                }
            }

        }


        if (dataModel != null) {
            //todo set default data--

            var jsonObject = JsonObject()
            jsonObject.addProperty("TicketId", dataModel.TicketId)
            jsonObject.addProperty("ReportType", Flag)
            jsonObject.addProperty("ItemSerialNo", dataModel.SerialNo)
            jsonObject.addProperty("ItemCode", dataModel.ItemCode)

            viewModel.ticketInstallationOneApi(jsonObject)
            bindDefaultObserver()
        }


        binding.submitBtn.setOnClickListener {
            callRequestPayload()
        }

    }

    private fun callRequestPayload() {

        if (validation( binding.edtDateOfInstallation.text.toString(), binding.edtMachineLocationFloar.text.toString(), binding.edtMachineArea.text.toString(), binding.edtNoEmployeeOfArea.text.toString(),
                binding.edtMembrane.text.toString(), binding.edtRejected.text.toString(), binding.edtROPump.text.toString(),binding.edtTDSInput.text.toString(), binding.edtTDSOutput.text.toString(),binding.edtHotWater.text.toString(), binding.edtColdWater.text.toString()
                , isVantillationChecked, binding.edtVentillationRemark.text.toString(), isRawWaterChecked, binding.edtRawWaterRemark.text.toString(), isPowerAMPChecked, binding.edtPowerAMPRemark.text.toString(),
                binding.edtCustomerName.text.toString(), binding.edtCustomerNumber.text.toString(), binding.edtName.text.toString(), isPartMissingChecked,
                binding.edtPartMissingRemark.text.toString(),
                isDamagedPartChecked,
                binding.edtDamagedPartRemark.text.toString(), modelNoValue)) {
            try {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()

                var sparePartList = mutableListOf<SparePart>()
                sparePartList.addAll(focItemAdapter!!.getAttachList())

                var billableList = mutableListOf<SparePart>()
                billableList.addAll(billableItemsAdapter!!.getAttachList())

                Log.e("FOC_LIST>>>>>>", "onCreate:${sparePartList} ")
                Log.e("Billable_LIST>>>>>>", "onCreate:${billableList} ")

                val jsonObject = JsonObject().apply {
                    addProperty("id", defaultDataModel.id)
                    addProperty("TicketId", dataModel.TicketId)
                    addProperty("ReportType", Flag)
                    addProperty("ItemSerialNo", dataModel.SerialNo)
                    addProperty("ItemCode", dataModel.ItemCode)
                    addProperty("InstallDate", Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtDateOfInstallation.text.toString()))
                    addProperty("MachineLocFloor", binding.edtMachineLocationFloar.text.toString().trim())
                    addProperty("MachineLocArea", binding.edtMachineArea.text.toString().trim())
                    addProperty("EmployeeInArea", binding.edtNoEmployeeOfArea.text.toString().trim())
                    addProperty("Remark", binding.edtRemarks.text.toString().trim())
                    addProperty("Membrane", binding.edtMembrane.text.toString().trim())
                    addProperty("Rejected", binding.edtRejected.text.toString().trim())
                    addProperty("ROPump", binding.edtROPump.text.toString().trim())
                    addProperty("modelType", modelNoValue)
                    addProperty("TDSInput", binding.edtTDSInput.text.toString().trim())
                    addProperty("TDSOutput", binding.edtTDSOutput.text.toString())
                    addProperty("HotWater", binding.edtHotWater.text.toString().trim())
                    addProperty("ColdWater", binding.edtColdWater.text.toString().trim())
                    addProperty("is_Ventillation", isVantillationChecked)
                    addProperty("VentillationRemark", binding.edtVentillationRemark.text.toString().trim())
                    addProperty("is_WaterPressure", isRawWaterChecked)
                    addProperty("WaterPressureRemark", binding.edtRawWaterRemark.text.toString().trim())
                    addProperty("is_PowerAvailable", isPowerAMPChecked)
                    addProperty("PowerAvailableRemark", binding.edtPowerAMPRemark.text.toString())
                    addProperty("is_PartMissing", isPartMissingChecked)
                    addProperty("PartMissingRemark", binding.edtPartMissingRemark.text.toString())
                    addProperty("is_DamagedPart", isDamagedPartChecked)
                    addProperty("DamagedPartRemark", binding.edtDamagedPartRemark.text.toString())
                    addProperty("CustomerName", binding.edtCustomerName.text.toString().trim())
                    addProperty("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    addProperty("CustomerDesignation", binding.edtCustomerDesignation.text.toString().trim())
                    addProperty("EngineerName", binding.edtName.text.toString().trim())
                    addProperty("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())
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

                val jsonString = jsonObject.toString()
                Log.e("JSON Payload", jsonString)

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

    var defaultDataModel = InstallationTicketOneResponse.DataXXX()

    //todo calling default api here---
    private fun bindDefaultObserver() {
        viewModel.installationOneData.observe(this, Event.EventObserver(
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


    var modelNoValue = ""

    //todo set default data here---
    private fun setDefaultData(dataModel: InstallationTicketOneResponse.DataXXX) {
        if (dataModel.InstallDate.isNotEmpty()) {
            binding.edtDateOfInstallation.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.InstallDate))
        } else {
            binding.edtDateOfInstallation.setText(Global.getTodayDate())
        }

        binding.edtMachineLocationFloar.setText(dataModel.MachineLocFloor)
        binding.edtMachineArea.setText(dataModel.MachineLocArea)
        binding.edtNoEmployeeOfArea.setText(dataModel.EmployeeInArea)
        binding.edtRemarks.setText(dataModel.Remark)


        binding.edtMembrane.setText(dataModel.Membrane)
        binding.edtRejected.setText(dataModel.Rejected)
        binding.edtROPump.setText(dataModel.ROPump)
        binding.acMachineModelType.setText(dataModel.modelType)

        val adapter = ArrayAdapter(this@EditInstallationTypeTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
        binding.acMachineModelType.setAdapter(adapter)

        binding.edtTDSInput.setText(dataModel.TDSInput)
        binding.edtTDSOutput.setText(dataModel.TDSOutput)

        binding.edtHotWater.setText(dataModel.HotWater)
        binding.edtColdWater.setText(dataModel.ColdWater)

        if (dataModel.is_Ventillation){
            binding.ventillationCheckBox.isChecked = true
            isVantillationChecked = "true"
        }else{
            binding.ventillationCheckBox.isChecked = false
            isVantillationChecked = "false"
        }

        if (dataModel.is_WaterPressure){
            binding.rawWaterCheckBox.isChecked = true
            isRawWaterChecked = "true"
        }else{
            binding.rawWaterCheckBox.isChecked = false
            isRawWaterChecked = "false"
        }

        if (dataModel.is_PowerAvailable){
            binding.powerAMPCheckBox.isChecked = true
            isPowerAMPChecked = "true"
        }else{
            binding.powerAMPCheckBox.isChecked = false
            isPowerAMPChecked = "false"
        }


        if (Flag == "De-Installation" || Flag == "Shifting" || Flag == "Packaging"){
            binding.partMissingLayout.visibility = View.VISIBLE
            binding.damagedPartLayout.visibility = View.VISIBLE


            if (dataModel.is_PartMissing == "true"){
                binding.partMissingCheckBox.isChecked = true
                isPartMissingChecked = "true"
            }else{
                binding.partMissingCheckBox.isChecked = false
                isPartMissingChecked = "false"
            }

            if (dataModel.is_DamagedPart == "true"){
                binding.damagedPartCheckBox.isChecked = true
                isDamagedPartChecked = "true"
            }else{
                binding.damagedPartCheckBox.isChecked = false
                isDamagedPartChecked = "false"
            }

            
        }else{
            binding.partMissingLayout.visibility = View.GONE
            binding.damagedPartLayout.visibility = View.GONE
            isPartMissingChecked = ""
            isDamagedPartChecked = ""
        }



        binding.edtVentillationRemark.setText(dataModel.VentillationRemark)
        binding.edtRawWaterRemark.setText(dataModel.WaterPressureRemark)
        binding.edtPowerAMPRemark.setText(dataModel.PowerAvailableRemark)
        binding.edtPartMissingRemark.setText(dataModel.PartMissingRemark)
        binding.edtDamagedPartRemark.setText(dataModel.DamagedPartRemark)

        binding.edtCustomerName.setText(dataModel.CustomerName)
        binding.edtCustomerNumber.setText(dataModel.CustomerNumber)
        binding.edtCustomerDesignation.setText(dataModel.CustomerDesignation)

        binding.edtName.setText(dataModel.EngineerName)
        binding.edtServiceEngineerRemark.setText(dataModel.EngineerRemark)

        /*if (dataModel.Files.isNotEmpty()){
            for (i in dataModel.Files) {
                path.addAll(i)
            }
        }*/
        if (dataModel.Files.isNotEmpty()){
            val adapter = PreviousImageViewAdapter(this, dataModel.Files)
            binding.rvAttachment.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            binding.rvAttachment.adapter = adapter
            adapter.notifyDataSetChanged()
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
                            focItemAdapter = EditFOCItemsAdapter(this@EditInstallationTypeTicketActivity, mutableListOf(), customerList_gl)
                            bindFocItemAdapter()
                            //todo calling for loop for bins spare items ---

                            if (sparePart.isNotEmpty()) {
                                for (item in sparePart) {
                                    if (item.SellType == "FOC") {
                                        focItemAdapter!!.addItem(item)
                                    }
                                }
                            }


                            billableItemsAdapter = EditBillableItemsAdapter(this@EditInstallationTypeTicketActivity, mutableListOf(), customerList_gl)
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
                Global.errormessagetoast(this@EditInstallationTypeTicketActivity, t.message.toString())
            }
        })
    }




    //todo bind foc items parts adapter--
    private fun bindFocItemAdapter() = binding.rvSpareParts.apply {
        adapter = focItemAdapter!!
        layoutManager = LinearLayoutManager(this@EditInstallationTypeTicketActivity, LinearLayoutManager.VERTICAL, false)

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
        layoutManager = LinearLayoutManager(this@EditInstallationTypeTicketActivity)

        //todo remove billable items---
        if (billableItemsAdapter != null){
            billableItemsAdapter!!.setOnItemMinusClickListener { s, i ->
                if (billableItemsAdapter!!.itemCount > 0) {
                    billableItemsAdapter!!.removeItem(i)
                }
            }
        }
    }

    companion object{
        private const val TAG = "EditInstallationTypeTic"
    }


    private fun validation(dateInstall: String, machineLocFloor: String, machineArea: String, noEmployeeArea: String,
        membrane: String, rejected: String, roPump: String, tdsInput: String, tdsOutput: String, hotWater: String, coldWater: String,
        isVantillationChecked: String, ventillationRemarks: String, isRawWaterChecked: String, rawWaterRemark: String, isPowerAMPChecked: String, powerAMPRemark: String, customerName: String,
        customerNumber: String,  engineerName: String,  isPartMissingChecked: String, partMissingRemark: String, isDamagedPartChecked: String, damagedPartRemark: String, modelNoValue : String): Boolean {

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
        }
        else if (modelNoValue.isEmpty()) {
            Global.warningmessagetoast(this, "Select Machine Model Type")
            return false
        }
        else if (tdsInput.isEmpty()) {
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
        else if (isVantillationChecked == "true" && ventillationRemarks.isEmpty()) {
            Global.warningmessagetoast(this, "Ventilation Remark can't be Empty")
            return false
        }
        else if (isVantillationChecked == "false" && ventillationRemarks.isNotEmpty()) {
            Global.warningmessagetoast(this, "Ventilation Check Box can't be Empty")
            return false
        }
        else if ( isRawWaterChecked == "true" && rawWaterRemark.isEmpty()) {
            Global.warningmessagetoast(this, "Raw Water Remark can't be Empty")
            return false
        }
        else if (isRawWaterChecked == "false" && rawWaterRemark.isNotEmpty()) {
            Global.warningmessagetoast(this, "Raw Water Check Box can't be Empty")
            return false
        }
        else if (isPowerAMPChecked == "true" && powerAMPRemark.isEmpty()) {
            Global.warningmessagetoast(this, "Power AMP Remark can't be Empty")
            return false
        }
        else if (isPowerAMPChecked == "false" && powerAMPRemark.isNotEmpty()) {
            Global.warningmessagetoast(this, "Power Check Box can't be Empty")
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

}