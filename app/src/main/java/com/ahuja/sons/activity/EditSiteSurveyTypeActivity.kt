package com.ahuja.sons.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ticketItemAdapter.*
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityEditSiteSurveyTypeBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.newapimodel.SiteSurveyTicketResponse
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class EditSiteSurveyTypeActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditSiteSurveyTypeBinding
    lateinit var viewModel: MainViewModel
    var dataModel = ItemAllListResponseModel.DataXXX()
    var mArrayUriList: ArrayList<Uri> = ArrayList()
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
    lateinit var editAvailabilityAdapter: EditAvailabilityAdapter
    lateinit var editAreaAdapter: EditAreaAdapter
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
        binding = ActivityEditSiteSurveyTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

//        dataModel = intent.getSerializableExtra("SiteSurvey")!! as ItemAllListResponseModel.DataXXX
        ticketData = intent.getSerializableExtra("ticketData")!! as TicketData

//        binding.loadingback.visibility = View.GONE

        if ((Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
            supportActionBar!!.title = "View Report"
            binding.submitBtn.visibility = View.GONE
        }

        //todo HERE code for Add Availability items--
        editAvailabilityAdapter = EditAvailabilityAdapter(this, mutableListOf())

        bindAvailabilityItemAdapter()

        binding.ivAddAvailabilityItems.setOnClickListener {
            val newItem = SiteSurveyTicketResponse.Availability(
                ItemName = itemName,
                ItemQty = itemQty,
                ItemDistance = itemDistance,
                ItemCode = "",
                ItemSerialNo = "",
                SiteSurveyId = "",
                TicketId = "",
                id = ""
            )
            editAvailabilityAdapter.addItem(newItem)
            itemQty = ""
            itemDistance = ""
        }

        editAvailabilityAdapter.setOnItemMinusClickListener { s, i ->
            if (editAvailabilityAdapter.itemCount > 0) {
                editAvailabilityAdapter.removeItem(i)
            }

        }


        //todo HERE code for Add Area Item---
        editAreaAdapter = EditAreaAdapter(this, mutableListOf())

        bindAreaItemAdapter()

        binding.ivAddAreaItems.setOnClickListener {
            val newItem = SiteSurveyTicketResponse.Area(
                Location = locationContent,
                Length = contentLength,
                Width = contentWidth,
                Height = contentHeight,
                ItemCode = "",
                ItemSerialNo = "",
                SiteSurveyId = "",
                TicketId = "",
                id = ""
            )
            editAreaAdapter.addItem(newItem)
            locationContent = ""
            contentLength = ""
        }

        editAreaAdapter.setOnItemMinusClickListener { s, i ->
            if (editAreaAdapter.itemCount > 0) {
                editAreaAdapter.removeItem(i)
            }

        }

        binding.ivAttachmentFiles.visibility = View.INVISIBLE


        if (ticketData != null) {
            //todo set default data--

            var jsonObject = JsonObject()
            jsonObject.addProperty("TicketId", ticketData.id)
            jsonObject.addProperty("ReportType", "Site Survey")
            jsonObject.addProperty("ItemSerialNo", "")
            jsonObject.addProperty("ItemCode", "")

            viewModel.ticketSiteSurveyOneApi(jsonObject)
            bindDefaultObserver()
        }


        //todo update maintenance---
        binding.submitBtn.setOnClickListener {
            callRequestPayload()
        }


    }

    private fun callRequestPayload() {
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

            var availabilityMutableList = mutableListOf<SiteSurveyTicketResponse.Availability>()
            availabilityMutableList.addAll(editAvailabilityAdapter.getAttachList())
            Log.e("LIST>>>>>>", "onCreate:${editAvailabilityAdapter.getAttachList()}")

            var areaMutableList = mutableListOf<SiteSurveyTicketResponse.Area>()
            areaMutableList.addAll(editAreaAdapter.getAttachList())
            Log.e("LIST>>>>>>", "onCreate:${" Areadata===> "} ${editAreaAdapter.getAttachList()}")

            try {

                val jsonObject = JsonObject().apply {
                    // Add properties to the JSON object
                    addProperty("id", defaultDataModel.id)
                    addProperty("TicketId", ticketData.id)
                    addProperty("ReportType", "Site Survey")
                    addProperty("ItemSerialNo", "")
                    addProperty("ItemCode", "")
                    addProperty("BuildingFloorCount", binding.edtTotalFloorBuilding.text.toString().trim())
                    addProperty("WaterSource", binding.edtSourceWater.text.toString().trim())
                    addProperty("TankVolume", binding.edtOverheadTankVolume.text.toString().trim())
                    addProperty("TankCleaning", binding.edtTankCleaningCapacity.text.toString().trim())
                    addProperty("TankFillingFrequency", binding.edtTankFillingFrequency.text.toString().trim())
                    addProperty("Floor", binding.edtTotalFloor.text.toString().trim())
                    addProperty("ShiftNo", binding.edtNoShifts.text.toString().trim())
                    addProperty("ShiftTiming", binding.edtWorkTimeShifts.text.toString().trim())
                    addProperty("TankHeight", binding.edtTankHeight.text.toString())
                    addProperty("TotalEmployee", binding.edtNoOfEmployee.text.toString().trim())
                    addProperty("TotalVisitors", binding.edtNoOfVisitors.text.toString().trim())
                    addProperty("PantriesOnFloor", binding.edtNoOfPantriesFloor.text.toString().trim())
                    addProperty("ElectricityType", binding.edtElectricityPoint.text.toString().trim())
                    addProperty("PowerBackupCapacity", binding.edtPowerBackUpCapacity.text.toString().trim())
                    addProperty("TDSRawWater", binding.edtTDSRawWater.text.toString().trim())
                    addProperty("ChlorinatedWater", binding.edtChlorinatedWater.text.toString().trim())
                    addProperty("WaterPressure", binding.edtwaterPressure.text.toString())
                    addProperty("PHLevel", binding.edtLevelPHValue.text.toString().trim())
                    addProperty("InstalledDispensers", binding.edtDispensersFloor.text.toString().trim())
                    addProperty("ExistingDispensersMake", binding.edtExistingWater.text.toString().trim())
                    addProperty("WaterBottelsUses", binding.edtDailyConsumption.text.toString().trim())
                    addProperty("WaterBottlesCapacity", binding.edtCapacityWaterBottle.text.toString().trim())
                    addProperty("ExistingBottlesMake", binding.edtExistingWaterBottleValue.text.toString().trim())
                    addProperty("EngineerName", binding.edtName.text.toString().trim())
                    addProperty("EngineerRemark", binding.edtServiceEngineerRemark.text.toString().trim())
                    addProperty("CustomerName", binding.edtCustomerName.text.toString().trim())
                    addProperty("CustomerNumber", binding.edtCustomerNumber.text.toString().trim())
                    addProperty("CustomerRemark", binding.edtCustomerRemark.text.toString().trim())

                    val listJsonArray = availabilityMutableList.map {
                        JsonObject().apply {
                            addProperty("ItemName", it.ItemName)
                            addProperty("ItemQty", it.ItemQty)
                            addProperty("ItemDistance", it.ItemDistance)
                            addProperty("ItemCode", it.ItemCode)
                            addProperty("ItemSerialNo", it.ItemSerialNo)
                            addProperty("SiteSurveyId", it.SiteSurveyId)
                            addProperty("TicketId", it.TicketId)
                            addProperty("id", it.id)
                        }
                    }

                    val jsonArray = areaMutableList.map {
                        JsonObject().apply {
                            addProperty("Location", it.Location)
                            addProperty("Length", it.Length)
                            addProperty("Width", it.Width)
                            addProperty("Height", it.Height)
                            addProperty("ItemCode", it.ItemCode)
                            addProperty("ItemSerialNo", it.ItemSerialNo)
                            addProperty("SiteSurveyId", it.SiteSurveyId)
                            addProperty("TicketId", it.TicketId)
                            addProperty("id", it.id)
                        }
                    }

                    addProperty("Availability", listJsonArray.toString())
                    addProperty("Area", jsonArray.toString())
                    addProperty("File", "")

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

    //todo bind spare parts adapter--
    private fun bindAvailabilityItemAdapter() = binding.rvAddAvailabilityItems.apply {
        adapter = editAvailabilityAdapter
        layoutManager = LinearLayoutManager(this@EditSiteSurveyTypeActivity)
    }


    //todo bind spare parts adapter--
    private fun bindAreaItemAdapter() = binding.rvAddAreaItems.apply {
        adapter = editAreaAdapter
        layoutManager = LinearLayoutManager(this@EditSiteSurveyTypeActivity)
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


    //todo bind default observer---
    var defaultDataModel = SiteSurveyTicketResponse.DataXXX()

    private fun bindDefaultObserver() {
        viewModel.siteSurveyOneData.observe(this, Event.EventObserver(
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
    private fun setDefaultData(data: SiteSurveyTicketResponse.DataXXX) {

        binding.edtTotalFloorBuilding.setText(data.BuildingFloorCount)
        binding.edtSourceWater.setText(data.WaterSource)
        binding.edtOverheadTankVolume.setText(data.TankVolume)
        binding.edtTankCleaningCapacity.setText(data.TankCleaning)
        binding.edtTankFillingFrequency.setText(data.TankFillingFrequency)

        binding.edtTotalFloor.setText(data.Floor)
        binding.edtNoShifts.setText(data.ShiftNo)
        binding.edtWorkTimeShifts.setText(data.ShiftTiming)
        binding.edtTankHeight.setText(data.TankHeight)
        binding.edtNoOfEmployee.setText(data.TotalEmployee)
        binding.edtNoOfVisitors.setText(data.TotalVisitors)
        binding.edtNoOfPantriesFloor.setText(data.PantriesOnFloor)
        binding.edtElectricityPoint.setText(data.ElectricityType)
        binding.edtPowerBackUpCapacity.setText(data.PowerBackupCapacity)

        binding.edtTDSRawWater.setText(data.TDSRawWater)
        binding.edtChlorinatedWater.setText(data.ChlorinatedWater)
        binding.edtwaterPressure.setText(data.WaterPressure)
        binding.edtLevelPHValue.setText(data.PHLevel)

        binding.edtDispensersFloor.setText(data.InstalledDispensers)
        binding.edtExistingWater.setText(data.ExistingDispensersMake)
        binding.edtDailyConsumption.setText(data.WaterBottelsUses)
        binding.edtCapacityWaterBottle.setText(data.WaterBottlesCapacity)
        binding.edtExistingWaterBottleValue.setText(data.ExistingBottlesMake)


        binding.edtName.setText(data.EngineerName)
        binding.edtServiceEngineerRemark.setText(data.EngineerRemark)

        binding.edtCustomerName.setText(data.CustomerName)
        binding.edtCustomerNumber.setText(data.CustomerNumber)
        binding.edtCustomerRemark.setText(data.CustomerRemark)


        //todo calling for loop for bins spare items ---

        if (data.Availability.isNotEmpty()){
            for (item in data.Availability){
                editAvailabilityAdapter.addItem(item)
            }
        }


        if (data.Area.isNotEmpty()){
            for (item in data.Area){
                editAreaAdapter.addItem(item)
            }
        }


        //todo attached images---

        if (data.Files.isNotEmpty()){
            val adapter = PreviousImageViewAdapter(this, data.Files)
            binding.rvAttachment.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
            binding.rvAttachment.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }



    private fun validation(floorBuilding: String, sourceWater: String, overheadTankVol: String, tankCleaningCap: String, tankFillingFreq: String,
                           floor: String, noOfShifts: String, workTimeShift: String, tankHeightMach: String, NoOfEmployee: String, NoOFVisitor: String,
                           NoOfPanitries: String, electricityPoints: String, powerBankUp: String, tdsRawWater: String, chlorinatedWater: String, waterPressurePSI: String,
                           levelPHValue: String, noOfDispensersInstall: String, existWaterDispense: String, dailyConsumptionBottles: String, capacityWaterBottle: String,
                           existingWaterBottle: String, name: String, customerName: String, customerNumber: String ): Boolean {

        if (editAvailabilityAdapter.getAttachList().isEmpty()){
            Global.warningmessagetoast(this, "Select Atleast One Availability Item")
            return false
        }
        else if (editAreaAdapter.getAttachList().isEmpty()){
            Global.warningmessagetoast(this, "Select Atleast One Area Item")
            return false
        }
        else if (floorBuilding.isEmpty()) {
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


    companion object {
        private const val TAG = "EditSiteSurveyTypeActiv"
    }
}