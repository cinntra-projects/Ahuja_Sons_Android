package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ItemNameAdapter
import com.ahuja.sons.adapter.ScopeOfWorkAdapter
import com.ahuja.sons.adapter.SelectedItemAdapter
import com.ahuja.sons.adapter.TypeAdapter
import com.ahuja.sons.adapter.ticketItemAdapter.RequestTypeAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.EditTicketDetailsBinding
import com.ahuja.sons.fragment.*
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.`interface`.ContactItemSelect
import com.ahuja.sons.`interface`.SelectBranchItem
import com.ahuja.sons.`interface`.SelectBusinessPartneer
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.model.ComplainDetailResponseModel
import com.ahuja.sons.model.ScopOfWorkResponseModel
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

class EditTicketActivity : MainBaseActivity(),  ContactItemSelect, SelectBranchItem, SelectBusinessPartneer {

    private lateinit var binding: EditTicketDetailsBinding
    lateinit var viewModel: MainViewModel
    var parcelTicketData = TicketData()

    var ticketdata = TicketData()
    var ticketOneData_gl = TicketData()

    var statusval = "--None--"
    var priorityval = ""
    var typeval = ""
    var subTypeSPinner = ""
    var zoneval = "South"
    var CatID = ""
    var zoneList = ArrayList<BPLID>()
    var typelist = ArrayList<BPLID>()
    var priorityList = ArrayList<BPLID>()
    var nameTypeTicket = mutableListOf<String>()
    val nameSubTypeTicket = mutableListOf<String>()

    lateinit var spinnerArrayAdapter: TypeAdapter
    lateinit var zoneArrayAdapter: ArrayAdapter<String>
    lateinit var statusArrayAdapter: ArrayAdapter<String>
    lateinit var priorityArrayAdapter: ArrayAdapter<String>

    lateinit var contactfragement: SelectDepartmentFragement
    lateinit var branchFragment: SelectBranchFragment
    lateinit var employeeFragement: SelectEmployeeFragement
    var contactlist = ArrayList<DataXX>()
    var branchAllList = ArrayList<BranchAllListResponseModel.DataXXX>()

    var selectedDataList = ArrayList<ItemAllListResponseModel.DataXXX>()
    var scopeWorkVal = ""
    var requestTypeVal = ""
    var caseOriginVal = ""

    companion object {
        private const val TAG = "EditTicketActivity"
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditTicketDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        binding.addTicket.countryPickerPhone.setCountryForPhoneCode(91)
//        binding.addTicket.countryPickerPhone.setDefaultCountryUsingNameCode("IN")
        binding.addTicket.countryPickerAlternate.setCountryForPhoneCode(91)
//        binding.addTicket.countryPickerAlternate.setDefaultCountryUsingNameCode("IN")



        parcelTicketData = intent.getSerializableExtra(Global.TicketData)!! as TicketData

        binding.addTicket.duedateValue.setOnClickListener {
            Global.selectDate(this, binding.addTicket.duedateValue)
        }


        binding.addTicket.createAndUpdateTicketBtn.visibility = View.GONE

        binding.toolbar.heading.text = "Edit Tickets"

        binding.toolbar.backPress.setOnClickListener {
            onBackPressed()
        }
        if (Global.checkForInternet(this)) {
            var hashMap = HashMap<String, String>()
            hashMap.put("id", parcelTicketData.id.toString())
            viewModel.getTicketOne(hashMap)
            subscribeToObserverr()

            viewModel.getScopeWorkList()
            binsScopeOfWorkObserver()

            callRequestTypeApi()
            //callZoneApi()
        }

        eventmanager()

        //  setData()
        disablekeys() //todo comment by me disable customer name ui---

        binding.update.setOnClickListener {
            updateTicketsData()
        }


        //todo give visibility to zonal manager--
        if (Prefs.getString(Global.Employee_role, "") == "Zonal Manager"){
            binding.addTicket.zonalManagerRemarkLayout.visibility = View.VISIBLE
        }else{
            binding.addTicket.zonalManagerRemarkLayout.visibility = View.GONE
        }


    }



    //todo calling ticket one api observer--
    private fun subscribeToObserverr() {
        viewModel.particularTicket.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverrError:$it ")
                Global.warningmessagetoast(this, it)
            }, onLoading = {
                binding.loadingback.visibility = View.VISIBLE
            }, { ticketResponse ->
                binding.loadingback.visibility = View.GONE
                if (ticketResponse.status == 200) {
                    ticketOneData_gl = ticketResponse.data[0]
                    setData(ticketResponse.data[0])
                    viewModel.getTypeTicket()
                } else {
                    Global.warningdialogbox(this, ticketResponse.message)
                    Log.e(TAG, "subscribeToObserverrAPIERROR===>: ${ticketResponse.message}")
                }

            }

        ))



        viewModel.typeTicket.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            }, { type ->
                Log.e(TAG, "TYPE>>>>>>: ")
                if (type.status == 200) {
                    var typeListAnd = type.data

                    for (typical in typeListAnd) {
                        nameTypeTicket.add(typical.Type)
                    }

                    val adapter = ArrayAdapter(
                        this,
                        R.layout.spinner_row, nameTypeTicket
                    )
                    binding.addTicket.channelDropdown.adapter = adapter
                    binding.addTicket.channelDropdown.setSelection(
                        getTicketTypePos(
                            nameTypeTicket,
                            ticketOneData_gl.Type
                        )
                    )



                    binding.addTicket.channelDropdown.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View, position: Int, id: Long
                        ) {
//                            Toast.makeText(
//                                this@AddTicketActivity,
//                                nameTypeTicket[position], Toast.LENGTH_SHORT
//                            ).show()
                            typeval = nameTypeTicket[position]
                            nameSubTypeTicket.clear()
                            var hash = HashMap<String, String>()
                            hash["Type"] = typeval
                            viewModel.getSubType(hash)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }

                } else {
                    Global.warningmessagetoast(this, type.message)
                }
            }

        ))


        viewModel.subTypeTicket.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            }, { subType ->
                Log.e(TAG, "SUBTYPETYPE>>>>>>: ")
                if (subType.status == 200) {
                    if (subType.data.isNotEmpty()) {
                        val typeListAnd = subType.data

                        for (typical in typeListAnd) {
                            nameSubTypeTicket.add(typical.SubType)
                        }
                    }

                    val adapter = ArrayAdapter(
                        this,
                        R.layout.spinner_row, nameSubTypeTicket
                    )
                    binding.addTicket.spinnerSubType.adapter = adapter
                    binding.addTicket.spinnerSubType.setSelection(
                        getSubTicketTypePos(
                            nameSubTypeTicket,
                            ticketOneData_gl.SubType
                        )
                    )

                    binding.addTicket.spinnerSubType.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View, position: Int, id: Long
                        ) {
//                            Toast.makeText(
//                                this@AddTicketActivity,
//                                nameTypeTicket[position], Toast.LENGTH_SHORT
//                            ).show()
                            if (nameSubTypeTicket.isNotEmpty()) {
                                subTypeSPinner = nameSubTypeTicket[position]
                            }

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }
                } else {
                    Global.warningmessagetoast(this, subType.message)
                }
            }

        ))


    }

    //todo event listener---
    private fun eventmanager() {

     /*   //todo scope of work adapter bind--
        val scopAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.scopeWorkList)
        binding.addTicket.acScopeWork.setAdapter(scopAdapter)*/

   /*     //todo request type adapter bind--
        val requestAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.reqestTypeList)
        binding.addTicket.acRequestType.setAdapter(requestAdapter)*/


        //todo case Origin type adapter bind--
        val originAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.caseOrginList)
        binding.addTicket.acCaseOrigin.setAdapter(originAdapter)

        val priorityList = resources.getStringArray(R.array.priority_list)
        statusArrayAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.ticket_status)
        )

        statusArrayAdapter.setDropDownViewResource(R.layout.dropdownview)


        binding.addTicket.statusdropdown.adapter = statusArrayAdapter


        binding.addTicket.channelDropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (typelist.isNotEmpty()) {
                    typeval = typelist[p2].getType()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                if (typelist.isNotEmpty()) {
                    typeval = typelist[0].getType()
                }
            }

        }


        binding.addTicket.statusdropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                statusval = statusArrayAdapter.getItem(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                statusval = statusArrayAdapter.getItem(0).toString()
            }

        }


        //TODO select priority from static list..
        binding.addTicket.prioritySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    priorityval = parent!!.selectedItem.toString()
                    Log.e(TAG, "onItemSelectedproio====?: $priorityval")

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    priorityval = parent!!.selectedItem.toString()
                }

            }


        //TODO select priority from static list..
        binding.addTicket.zonedropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    zoneval = parent!!.selectedItem.toString()
                    Log.e(TAG, "onItemSelectedzone====?: $zoneval")

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    zoneval = parent!!.selectedItem.toString()
                }

            }


    }


    //todo update api request payload--
    private fun updateTicketsData() {
        if (validation(binding.addTicket.contacnameValue.text.toString(), binding.addTicket.edBranchList.text.toString(),
                binding.addTicket.businesspartnerValue.text.toString(),  binding.addTicket.email.text.toString(),
                binding.addTicket.phoneNumber.text.toString(), binding.addTicket.subject.text.toString(),
                priorityval, scopeWorkVal, requestTypeVal, caseOriginVal, binding.addTicket.address.text.toString(), zoneval)) {

            var ticketItemList : ArrayList<TicketItem> = ArrayList()

            for (i in 0 until selectedDataList.size){
                var ticketItem = TicketItem(
                    BranchId = selectedDataList[i].BranchId,
                    ItemCode = selectedDataList[i].ItemCode,
                    ItemName = selectedDataList[i].ItemName,
                    ItemsGroupCode = selectedDataList[i].ItemsGroupCode,
                    ItemsGroupName = selectedDataList[i].ItemsGroupName,
                    ModelNo = selectedDataList[i].ModelNo,
                    Quantity = selectedDataList[i].Quantity.toString(),
                    SerialNo = selectedDataList[i].SerialNo,
                    UnitPrice = selectedDataList[i].UnitPrice,
                    WarrantyEndDate = selectedDataList[i].WarrantyEndDate,
                    WarrantyStartDate = selectedDataList[i].WarrantyStartDate,
                    ExtendedWarrantyEndDate = selectedDataList[i].ExtendedWarrantyEndDate,
                    ExtendedWarrantyStartDate = selectedDataList[i].ExtendedWarrantyStartDate,
                    ToDate = selectedDataList[i].ToDate,
                    id = selectedDataList[i].id
                )

                ticketItemList.add(ticketItem)
            }

            val addTicketData = AddTicketRequestModel(
                AlternatePhone = binding.addTicket.alternatephoneNumber.text.toString().trim(),
                AssignTo = Global.TicketAssigntoID,
                BranchId = branchID,
                CardCode = ticketOneData_gl.CardCode,
                CaseOrigin = caseOriginVal,
                ContactAddress =  binding.addTicket.address.text.toString().trim(),
                ContactEmail = binding.addTicket.email.text.toString().trim(),
                ContactName = contactName,
                ContactPhone = binding.addTicket.phoneNumber.text.toString().trim(),
                ContractType = "",
                CountryCode = "+91",
                CountryCode1 = "+91",
                CreatedBy = Prefs.getString(Global.Employee_Code, ""),
                DeliveryID = "",
                Description = binding.addTicket.description.text.toString().trim(),
                DueDate = "",
                Observation = "",
                Priority = priorityval,
                Status = ticketOneData_gl.Status,
                SubType = requestTypeVal,
                TicketEndDate = ticketOneData_gl.TicketEndDate,
                TicketItems = ticketItemList,
                TicketStartDate = ticketOneData_gl.TicketStartDate,
                TicketStatus = ticketOneData_gl.TicketStatus,
                Title = binding.addTicket.subject.text.toString().trim(),
                Type = scopeWorkVal,
                Zone = zoneval,
                ZonalManagerRemark = binding.addTicket.edZonalManagerRemarks.text.toString().trim(),
                TandC = binding.addTicket.edTandC.text.toString().trim(),
                id = ticketOneData_gl.id
            )


            val gson = Gson()
            val jsonTut: String = gson.toJson(addTicketData)
            Log.e("data", jsonTut)

            if (Global.checkForInternet(this)) {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()
                viewModel.updateParticularTicket(addTicketData)
                subcribeToUpdateTicket()
            }

        }


    }


    var scopeWorkList: ArrayList<ScopOfWorkResponseModel.Daum> = ArrayList()

    private fun binsScopeOfWorkObserver() {
        viewModel.scopeOfWorkData.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserver: " + it)
            }, onLoading = {
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    try {
                        response.data.let { dataList ->

                            scopeWorkList.clear()
                            scopeWorkList.addAll(response.data!!)
                            var adapter = ScopeOfWorkAdapter(this, R.layout.drop_down_item_textview, scopeWorkList)
                            binding.addTicket.acScopeWork.setAdapter(adapter)

                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    Log.e(TAG, "subscribeToObserveAPir: $response")

                } else {
                    Global.warningmessagetoast(this, response.message)
                }

            }
        ))
    }

    //todo observer of update api--
    private fun subcribeToUpdateTicket() {
        viewModel.createTicket.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverrError:$it ")
                Global.warningmessagetoast(this, it)
            }, onLoading = {
                binding.loadingback.visibility = View.VISIBLE
            }, { ticketResponse ->
                binding.loadingback.visibility = View.GONE
                if (ticketResponse.status == 200) {
                    Global.successmessagetoast(this, "Updated Successfully")
                    Global.TicketAssigntoID = ""
                    onBackPressed()

                } else {
                    Global.warningdialogbox(this, ticketResponse.message)
                    Log.e(TAG, "subscribeToObserverrAPIERROR===>: ${ticketResponse.message}")
                }

            }


        ))
    }


    private fun validation(
        contactname: String,
        edBranchList: String,
        serialnum: String,
        email: String,
        phoneNumber: String,
        subject: String,
        priorityval: String,
        scopeWorkVal: String,
        requestTypeVal: String,
        caseOriginVal: String,
        address: String,
        zoneval: String
    ): Boolean {
        if (contactname.isEmpty()) {
            Toast.makeText(this, "Enter Contact Details", Toast.LENGTH_SHORT).show()
            return false
        } else if (edBranchList.isEmpty()) {
            Toast.makeText(this, "Branch is Required", Toast.LENGTH_SHORT).show()
            return false
        } else if (serialnum.isEmpty()) {
            Toast.makeText(this, "Enter Business Partner", Toast.LENGTH_SHORT).show()
            return false
        }else if (email.isEmpty()) {
            Toast.makeText(this, "Email is Required", Toast.LENGTH_SHORT).show()
            return false
        }else if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone No. is Required", Toast.LENGTH_SHORT).show()
            return false
        }else if (subject.isEmpty()) {
            Toast.makeText(this, "Subject is Required", Toast.LENGTH_SHORT).show()
            return false
        }else if (priorityval.isEmpty()) {
            Toast.makeText(this, "Priority is Required", Toast.LENGTH_SHORT).show()
            return false
        }else if (scopeWorkVal.isEmpty()) {
            Toast.makeText(this, "Scope Of Work is Required", Toast.LENGTH_SHORT).show()
            return false
        }else if (requestTypeVal.isEmpty()) {
            Toast.makeText(this, "Request Type is Required", Toast.LENGTH_SHORT).show()
            return false
        }else if (caseOriginVal.isEmpty()) {
            Toast.makeText(this, "Case Origin is Required", Toast.LENGTH_SHORT).show()
            return false
        }else if (address.isEmpty()) {
            Toast.makeText(this, "Address is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (zoneval.isEmpty()) {
            Toast.makeText(this, "Zone is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (scopeWorkVal != "Site Survey"){
            if (selectedDataList.size == 0) {
                Toast.makeText(this, "Select Atleast one Items Please !", Toast.LENGTH_SHORT).show()
            }
        }
        /* else if (assignedTo.text.isEmpty()) {
             assignedTo.requestFocus()
             assignedTo.error = "Can't Be Empty"
             Toast.makeText(this, "Choose Assigned To ", Toast.LENGTH_SHORT).show()
             return false
         }*/
        return true
    }




    private fun setData(ticketData: TicketData) {
        Log.e(TAG, "setData>>>>>: ")
        statusval = ticketData.Status
        priorityval = ticketData.Priority
        Global.TicketAssigntoID = ticketData.AssignTo

        contactName = ticketData.ContactName
        scopeWorkVal = ticketData.Type
        requestTypeVal = ticketData.SubType
        caseOriginVal = ticketData.CaseOrigin
        CardCode = ticketData.CardCode

        binding.addTicket.edBranchList.isEnabled = false
        binding.addTicket.edBranchList.isClickable = false


        //todo item list bind as per ticket one api--
        var SalesEmployeeList: List<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
        SalesEmployeeList = filterlist(ticketData.TicketItems)

        binding.addTicket.acItemName.isEnabled = false
        binding.addTicket.acItemName.isClickable = false


        selectedDataList.clear()
        selectedDataList.addAll(SalesEmployeeList)
        if (selectedDataList != null && selectedDataList.size > 0) {
            binding.addTicket.rvItemNames.visibility = View.VISIBLE
        } else {
            binding.addTicket.rvItemNames.visibility = View.GONE
        }
        Log.e("selected", "onItemClick: " + selectedDataList.size)
        val gridLayoutManager = GridLayoutManager(this, 2)
        val adapterEmp = SelectedItemAdapter(this, selectedDataList, "DefaultSet")
        binding.addTicket.rvItemNames.layoutManager = gridLayoutManager
        binding.addTicket.rvItemNames.adapter = adapterEmp
        adapterEmp.notifyDataSetChanged()



        //todo contact name selectable--

        contactlist.clear()


        //todo calling contact name api list here---


        var jsonObject = JsonObject()
        jsonObject.addProperty("CardCode", CardCode)
        viewModel.getContactNameList(jsonObject)
        bindContactNameObserver()


        //todo set click on customer item list--
      /*  binding.addTicket.businesspartnerValue.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, SelectBusinessPartnerFragement(AddTicketActivity(), this,AddContactPerson(), "EditTicket", AddServiceContractActivty())).addToBackStack(null)
            transaction.commit()
        }*/ //todo comment by me---

        //todo set on item click of contact list--
        binding.addTicket.contacnameValue.setOnClickListener {
            if (binding.addTicket.businesspartnerValue.length() == 0) {
                Global.warningmessagetoast(this, "Select Business Partner")
            } else {
                contactfragement = SelectDepartmentFragement(binding.addTicket.contacnameValue, contactlist, AddTicketActivity(), this, "EditTicketConext")
                // args.putStringArrayList("data", position)
                this.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val transaction = this.supportFragmentManager.beginTransaction()
                transaction.add(R.id.container, contactfragement).addToBackStack(null)
                transaction.commit()
            }

        }


        //todo calling branch api list here---

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("BPCode", CardCode)
        viewModel.getBranchAllList(jsonObject1)
        bindBranchListObserver()



        //todo set on branch item click here ---
        binding.addTicket.edBranchList.setOnClickListener {
            if (binding.addTicket.businesspartnerValue.length() == 0) {
                Global.warningmessagetoast(this, "Select Business Partner")
            } else {
                branchFragment = SelectBranchFragment(binding.addTicket.edBranchList, branchAllList, AddTicketActivity(), this, "EditTicketConext")
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.container, branchFragment).addToBackStack(null)
                transaction.commit()
            }
        }



        //todo set item click of assigned api ---
        binding.addTicket.assignedValue.setOnClickListener {
            employeeFragement = SelectEmployeeFragement(binding.addTicket.assignedValue, "EditTicketFlag")
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, employeeFragement).addToBackStack(null)
            transaction.commit()
        }



        /*//todo scope of work adapter bind--
        val scopAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.scopeWorkList)
        binding.addTicket.acScopeWork.setAdapter(scopAdapter)


        //todo scope work item selected
        binding.addTicket.acScopeWork.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.scopeWorkList.isNotEmpty()) {
                    scopeWorkVal = Global.scopeWorkList[position]
                    binding.addTicket.acScopeWork.setText(Global.scopeWorkList[position])

                    val adapter = ArrayAdapter(this@EditTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.scopeWorkList)
                    binding.addTicket.acScopeWork.setAdapter(adapter)
                    if (scopeWorkVal == "Site Survey" && scopeWorkVal.isNotEmpty()){
                        binding.addTicket.itemNameLayout.visibility = View.GONE
                    }else{
                        binding.addTicket.itemNameLayout.visibility = View.VISIBLE
                    }

                } else {
                    scopeWorkVal = ""
                    binding.addTicket.acScopeWork.setText("")
                }
            }

        }*/

        //todo scope work item selected
        binding.addTicket.acScopeWork.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (scopeWorkList.isNotEmpty()) {
                    scopeWorkVal = scopeWorkList[position].Type
                    binding.addTicket.acScopeWork.setText(scopeWorkList[position].Type)

                    if (scopeWorkVal == "Site Survey" && scopeWorkVal.isNotEmpty()){
                        binding.addTicket.itemNameLayout.visibility = View.GONE
                    }else{
                        binding.addTicket.itemNameLayout.visibility = View.VISIBLE
                    }
                } else {
                    scopeWorkVal = ""
                    binding.addTicket.acScopeWork.setText("")
                }
            }

        }


        //todo request type adapter bind--
       /* val requestAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.reqestTypeList)
        binding.addTicket.acRequestType.setAdapter(requestAdapter)*/


        //todo scope work item selected
        binding.addTicket.acRequestType.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (requestTypeList.isNotEmpty()) {
                    requestTypeVal = requestTypeList[position].Name
                    binding.addTicket.acRequestType.setText(requestTypeList[position].Name)

                    /*val requestAdapter = ArrayAdapter(this@EditTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.reqestTypeList)
                    binding.addTicket.acRequestType.setAdapter(requestAdapter)*/

                } else {
                    requestTypeVal = ""
                    binding.addTicket.acRequestType.setText("")
                }
            }

        }


        //todo case Origin type adapter bind--
        val originAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.caseOrginList)
        binding.addTicket.acCaseOrigin.setAdapter(originAdapter)


        //todo scope work item selected
        binding.addTicket.acCaseOrigin.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.caseOrginList.isNotEmpty()) {
                    caseOriginVal = Global.caseOrginList[position]
                    binding.addTicket.acCaseOrigin.setText(Global.caseOrginList[position])

                    val requestAdapter = ArrayAdapter(this@EditTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.caseOrginList)
                    binding.addTicket.acCaseOrigin.setAdapter(requestAdapter)

                } else {
                    caseOriginVal = ""
                    binding.addTicket.acCaseOrigin.setText("")
                }
            }

        }


        typeval = ticketData.Type
        CatID = ticketData.ProductCategory

        binding.addTicket.statusdropdown.setSelection(statusArrayAdapter.getPosition(statusval))
        //  binding.addTicket.zonedropdown.setSelection(getCurrentZonePos(zoneList, ticketData.Zone))

        Log.e(TAG, "setData: 1==> ${ticketData.CountryCode} 2====>${ticketData.CountryCode1}")

        if (ticketData.CountryCode.isNotEmpty()) {
            binding.addTicket.countryPickerPhone.setCountryForPhoneCode(ticketData.CountryCode.toInt())
        } else {
            binding.addTicket.countryPickerPhone.setCountryForPhoneCode(91)
        }

        if (ticketData.CountryCode1.isNotEmpty()) {
            binding.addTicket.countryPickerAlternate.setCountryForPhoneCode(ticketData.CountryCode1.toInt())
        } else {
            binding.addTicket.countryPickerAlternate.setCountryForPhoneCode(91)
        }


        zoneval = ticketData.Zone
        val zoneSelectedValue = ticketData.Zone
        val prioritySelectedValue = ticketData.Priority// Replace with your actual selected value
        Log.e(TAG, "setData: $prioritySelectedValue")

        val position = resources.getStringArray(R.array.zone_list).indexOf(zoneSelectedValue)
        if (position != -1) {
            binding.addTicket.zonedropdown.setSelection(position)
        } else {
            // String value not found, handle accordingly
        }

        val positionPrior = resources.getStringArray(R.array.priority_list).indexOf(prioritySelectedValue)
        if (position != -1) {
            Log.e(TAG, "setDataPos==>: $positionPrior")
            binding.addTicket.prioritySpinner.setSelection(positionPrior)
        } else {
            // String value not found, handle accordingly
        }

        //   binding.addTicket.prioritySpinner.setSelection(getPriorityPos(priorityval))


        binding.addTicket.alternatephoneNumber.setText(ticketData.AlternatePhone)
        binding.addTicket.email.setText(ticketData.ContactEmail)
        binding.addTicket.phoneNumber.setText(ticketData.ContactPhone)
        binding.addTicket.itemCodeValue.setText(ticketData.ProductName)
        binding.addTicket.category.setText(ticketData.ProductCategoryName)
        binding.addTicket.tvProductSerialNumber.setText(ticketData.ProductSerialNo)
        binding.addTicket.duedateValue.setText(ticketData.DueDate)
        binding.addTicket.contacnameValue.setText(ticketData.ContactName)
        binding.addTicket.businesspartnerValue.setText(ticketData.BusinessPartner[0].CardName)
        //  binding.addTicket.phoneNumber.setText(ticketdata.BusinessPartner.Phone1)
        binding.addTicket.accountName.setText(ticketData.BusinessPartner[0].U_ACCNT)
        binding.addTicket.orderValue.setText(ticketData.DeliveryID)
        binding.addTicket.description.setText(ticketData.Description)
        binding.addTicket.address.setText(ticketData.ContactAddress)
        binding.addTicket.assignedValue.setText(ticketData.AssignToDetails[0].SalesEmployeeName)

        binding.addTicket.subject.setText(ticketData.Title)
//        binding.addTicket.acScopeWork.setText(ticketData.Type)
//        var scopePos = getScopeWorkPos(scopeWorkList, ticketData.Type)
        binding.addTicket.acScopeWork.setText(ticketData.Type)
        binding.addTicket.acRequestType.setText(ticketData.SubType)
        binding.addTicket.acCaseOrigin.setText(ticketData.CaseOrigin)
        binding.addTicket.edZonalManagerRemarks.setText(ticketData.ZonalManagerRemark)


        binding.loadingback.visibility = View.GONE


    }

    private fun getScopeWorkPos(zoneList: MutableList<ScopOfWorkResponseModel.Daum>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.id == code) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos
    }


    var requestTypeList = java.util.ArrayList<ComplainDetailResponseModel.DataX>()

    fun callRequestTypeApi(){
        val call: Call<ComplainDetailResponseModel> = ApiClient().service.getAllComplainDetail()
        call.enqueue(object : Callback<ComplainDetailResponseModel?> {
            override fun onResponse(
                call: Call<ComplainDetailResponseModel?>,
                response: Response<ComplainDetailResponseModel?>
            ) {
                try {
                    if (response.body()?.status!! == 200) {
                        if (response.body()?.data!!.isNotEmpty()) {
                            requestTypeList.clear()
                            requestTypeList.addAll(response.body()!!.data)
                            requestTypeList.add(ComplainDetailResponseModel.DataX("", "Other", "", "", 0))

                            var requestTypeAdapter = RequestTypeAdapter(this@EditTicketActivity,R.layout.drop_down_item_textview ,requestTypeList)
                            binding.addTicket.acRequestType.setAdapter(requestTypeAdapter)

                        }

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(this@EditTicketActivity, t.message.toString())
            }
        })
    }



    //todo contact name observer--
    private fun bindContactNameObserver() {
        viewModel.contactNameList.observe(this, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    contactlist.clear()
                    contactlist.addAll(it.data)

                }else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


    //todo select new customer name ---
    override fun selectpartner(bpdata: DataCustomerListForContact) {
        if (ticketOneData_gl.BusinessPartner.isNotEmpty()){

            if (ticketOneData_gl.BusinessPartner[0].CardName != bpdata.CardName){

                binding.addTicket.businesspartnerValue.setText(bpdata.CardName)
                CardCode = bpdata.CardCode
                binding.addTicket.orderValue.setText("")
                binding.addTicket.contacnameValue.setText("")
                binding.addTicket.edBranchList.setText("")
                binding.addTicket.itemCodeValue.setText("")
                binding.addTicket.category.setText("")
                contactlist.clear()
                selectedDataList.clear()

                val gridLayoutManager = GridLayoutManager(this, 2)
                val adapterEmp = SelectedItemAdapter(this, selectedDataList, "")
                binding.addTicket.rvItemNames.layoutManager = gridLayoutManager
                binding.addTicket.rvItemNames.adapter = adapterEmp
                adapterEmp.notifyDataSetChanged()

                //todo calling contact name api list here---

                var jsonObject = JsonObject()
                jsonObject.addProperty("CardCode", CardCode)
                viewModel.getContactNameList(jsonObject)
                bindContactNameObserver()


                //todo calling branch api list here---

                var jsonObject1 = JsonObject()
                jsonObject1.addProperty("BPCode", CardCode)
                viewModel.getBranchAllList(jsonObject1)
                bindBranchListObserver()
            }

        }

    }


    var contactName = ""
    //todo set text while select contact item--call override function
    override fun selectContactItem(bpdata: DataXX) {
        binding.addTicket.email.setText(bpdata.E_Mail)
        binding.addTicket.phoneNumber.setText(bpdata.MobilePhone)
        contactName = bpdata.FirstName.toString()
    }


    //todo branch observer---
    private fun bindBranchListObserver() {
        viewModel.branchAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    branchAllList.clear()
                    branchAllList.addAll(it.data)

                    if (branchAllList.size > 0) {
                        for (i in branchAllList.indices) {
                            if (ticketOneData_gl.BranchId.isNotEmpty()) {
                                if (ticketOneData_gl.BranchId == branchAllList[i].id) {
                                    branchID = ticketOneData_gl.BranchId
                                    binding.addTicket.edBranchList.setText(branchAllList[i].AddressName)
                                }
                            } else {
                                binding.addTicket.edBranchList.setText("")
                            }
                        }
                    }

                }else {
                    Toast.makeText(this@EditTicketActivity, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }

    var branchID = ""
    var CardCode = ""

    //todo branch item slected override function--
    override fun selectBranch(bpdata: BranchAllListResponseModel.DataXXX) {

        branchID = bpdata.id
//        CardCode = bpdata.BPCode


        var jsonObject = JsonObject()
        jsonObject.addProperty("BranchId", branchID)
        jsonObject.addProperty("CardCode", CardCode)

        viewModel.getItemAllList(jsonObject)
        bindItemListObserver()

    }

    var insallItemList: ArrayList<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()

    private fun bindItemListObserver() {
        viewModel.itemAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200 && it.message == "Success") {
                        if (it.data.size > 0 && it.data != null) {
                            selectedDataList.clear()

                            var SalesEmployeeList: List<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
                            SalesEmployeeList = filterlist(it.data)
                            insallItemList.clear()
                            insallItemList.addAll(SalesEmployeeList)

                            val itemNames: MutableList<String> = ArrayList()
                            for (item in insallItemList) {
                                itemNames.add("Item - " + item.ItemCode + " ( " + item.ItemName + " )")
                            }

                            var adapter = ItemNameAdapter(this, R.layout.drop_down_item_textview, insallItemList)
                            binding.addTicket.acItemName.setAdapter(adapter)


                            binding.addTicket.acItemName.onItemClickListener = object : AdapterView.OnItemClickListener {
                                override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                                    val selectedData = insallItemList[position]

                                    if (selectedData != null) {
                                        binding.addTicket.rvItemNames.visibility = View.VISIBLE
                                    } else {
                                        binding.addTicket.rvItemNames.visibility = View.GONE
                                    }
                                    if (selectedData != null && !selectedDataList.contains(selectedData)) {
                                        selectedDataList.add(selectedData)
                                        adapter.notifyDataSetChanged()
                                        Log.e("selected", "onItemClick: " + selectedDataList.size)
                                        val gridLayoutManager = GridLayoutManager(this@EditTicketActivity, 2)
                                        val adapterEmp = SelectedItemAdapter(this@EditTicketActivity, selectedDataList, "")
                                        binding.addTicket.rvItemNames.layoutManager = gridLayoutManager
                                        binding.addTicket.rvItemNames.adapter = adapterEmp
                                        adapterEmp.notifyDataSetChanged()
                                        adapter.notifyDataSetChanged()
                                    }

                                    binding.addTicket.acItemName.text.clear()
                                }

                            }

                        }else{
                            insallItemList.clear()
                            selectedDataList.clear()

                            //todo clear drop down list
                            var adapter = ItemNameAdapter(this, R.layout.drop_down_item_textview, insallItemList)
                            binding.addTicket.acItemName.setAdapter(adapter)

                            //todo clear grid layout recycler list---
                            val gridLayoutManager = GridLayoutManager(this, 2)
                            val adapterEmp = SelectedItemAdapter(this, selectedDataList, "")
                            binding.addTicket.rvItemNames.layoutManager = gridLayoutManager
                            binding.addTicket.rvItemNames.adapter = adapterEmp
                            adapterEmp.notifyDataSetChanged()
                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        Global.warningmessagetoast(this@EditTicketActivity, it.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }


    private fun filterlist(value: ArrayList<ItemAllListResponseModel.DataXXX>): List<ItemAllListResponseModel.DataXXX> {
        val tempList: MutableList<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
        for (installedItemModel in value) {
            if (!installedItemModel.ItemName.equals("admin")) {
                tempList.add(installedItemModel)
            }
        }
        return tempList
    }



    private fun disablekeys() {

        /**********Clickable false ****************/
        binding.addTicket.businesspartnerValue.isClickable = false

        /**********Focusable false ****************/
        binding.addTicket.businesspartnerValue.isFocusable = false

    }



    private fun getPriorityPos(value: String): Int {
        var pos = -1
        for (sd in priorityList) {
            if (sd.equals(value)) {
                pos = priorityList.indexOf(sd)
                break
            }
        }
        return pos
    }


    private fun getZonepos(zoneList: ArrayList<BPLID>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.getZone() == code) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getCurrentZonePos(zoneList: ArrayList<String>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.equals(code, ignoreCase = true)) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }


    private fun getTicketTypePos(zoneList: MutableList<String>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.equals(code, ignoreCase = true)) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getSubTicketTypePos(zoneList: MutableList<String>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.equals(code, ignoreCase = true)) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getTypepos(zoneList: ArrayList<BPLID>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.getType() == code) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getprioritypos(zoneList: ArrayList<BPLID>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.getPriority() == code) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }



}