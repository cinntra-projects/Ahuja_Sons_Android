package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.AddTicketsBinding
import com.ahuja.sons.fragment.*
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.adapter.*
import com.ahuja.sons.adapter.ticketItemAdapter.RequestTypeAdapter
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.`interface`.*
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.newapimodel.*
import com.ahuja.sons.newapimodel.DocumentLine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTicketActivity : MainBaseActivity(), View.OnClickListener, SelectBusinessPartneer,
    SelectedItemData, SelectedOrderFragment, SelectBranchItem, ContactItemSelect {
    lateinit var viewModel: MainViewModel

    private lateinit var binding: AddTicketsBinding
    lateinit var contactfragement: SelectDepartmentFragement
    lateinit var branchFragment: SelectBranchFragment
    lateinit var orderfragement: SelectOrderFragement
    lateinit var employeeFragement: SelectEmployeeFragement
    var zoneList = ArrayList<BPLID>()
    var typelist = ArrayList<BPLID>()
    var priorityList = ArrayList<BPLID>()
    lateinit var tdm: TicketDetailsData
    lateinit var zoneAdapter: ZoneAdapter
    lateinit var typeAdapter: TypeAdapter
    lateinit var priorityAdapter: PriorityAdapter
    var CardCode = ""
    var statusval = "New"
    var priorityval = "Low"

    var languageval = "English"
    var typeval = "Installation"
    var subTypeSPinner = ""
    var classificationval = "--None--"
    var zoneval = ""

    var scopeWorkVal = ""
    var requestTypeVal = ""
    var caseOriginVal = ""

    companion object {
        private const val TAG = "AddTicketActivity"
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

//    val arr= arrayOf(1,"2",3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTicketsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.addTicket.countryPickerPhone.setCountryForPhoneCode(91)
//        binding.addTicket.countryPickerPhone.setDefaultCountryUsingNameCode("IN")
        binding.addTicket.countryPickerAlternate.setCountryForPhoneCode(91)
//        binding.addTicket.countryPickerAlternate.setDefaultCountryUsingNameCode("IN")


        viewModel.getTypeTicket()
        subscribeToObserver()

        if (Prefs.getString(Global.TicketFlowFrom).equals("Scanner")) {
            binding.loadingView.start()
            tdm = intent.getParcelableExtra<TicketDetailsData>(Global.TicketData)!!
            setData(tdm)
        }
       /* if (Global.checkForInternet(this)) {
            viewModel.getAllZoneList()
            zoneObserver()
        }*///todo comment


        if (Global.checkForInternet(this)) {
            viewModel.getScopeWorkList()
            binsScopeOfWorkObserver()

            callRequestTypeApi();
        }

        eventmanager()


        binding.loadingback.visibility = View.GONE
        binding.loadingView.stop()
        binding.toolbar.heading.text = "Add Tickets"


    }


    private fun subscribeToObserver() {
        viewModel.typeTicket.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            }, { type ->
                if (type.status == 200) {
                    var typeListAnd = type.data
                    var nameTypeTicket = mutableListOf<String>()
                    for (typical in typeListAnd) {
                        nameTypeTicket.add(typical.Type)
                    }

                    val adapter = ArrayAdapter(
                        this@AddTicketActivity,
                        R.layout.spinner_row, nameTypeTicket
                    )
                    binding.addTicket.channelDropdown.adapter = adapter

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
                if (subType.status == 200) {
                    val typeListAnd = subType.data
                    val nameTypeTicket = mutableListOf<String>()
                    for (typical in typeListAnd) {
                        nameTypeTicket.add(typical.SubType)
                    }

                    val adapter = ArrayAdapter(
                        this@AddTicketActivity,
                        R.layout.spinner_row, nameTypeTicket
                    )
                    binding.addTicket.spinnerSubType.adapter = adapter

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
                            subTypeSPinner = nameTypeTicket[position]
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

        viewModel.createTicket.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                Global.warningmessagetoast(this, it)
                Log.e(TAG, "errorCreateTicket: "+it )
            }, onLoading = {
                binding.loadingback.visibility = View.VISIBLE
            }, {
                binding.loadingback.visibility = View.GONE
                if (it.status == 200) {
                    Global.TicketAssigntoID = ""
                    Global.successmessagetoast(this,"Added Successfully")
                    finish()
                 //   onBackPressed()
                } else {
                    Log.e(TAG, "errorinApiTicket: ${it.message}")
                    Global.warningmessagetoast(this, it.message)
                }
            }
        ))


    }


    //todo zone observer..

    private fun zoneObserver() {
        viewModel.getAllData.observe(this, Event.EventObserver(

            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.getData().isNotEmpty()) {
                    zoneList.clear()
                    zoneList.addAll(it.getData())
                    zoneAdapter = ZoneAdapter(this@AddTicketActivity, zoneList)
//                    binding.addTicket.zonedropdown.adapter = zoneAdapter//todo comment by me
                    zoneval = zoneList[0].getZone()
                }else
                    Toast.makeText(this@AddTicketActivity, it.getMessage(), Toast.LENGTH_SHORT).show()

            }

        ))
    }


    private fun callTypeApi() {
        val call: Call<BPBranchResponse> = ApiClient().service.getAllTypeList()
        call.enqueue(object : Callback<BPBranchResponse> {
            override fun onResponse(
                call: Call<BPBranchResponse>,
                response: Response<BPBranchResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()!!.getStatus() == 200) {
                        typelist.clear()
                        typelist.addAll(response.body()!!.getData())
                        typeAdapter = TypeAdapter(this@AddTicketActivity, typelist)
                        binding.addTicket.channelDropdown.adapter = typeAdapter
                        typeval = typelist[0].getType()
                    } else
                        Toast.makeText(
                            this@AddTicketActivity,
                            response.body()!!.getMessage(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                } else {
                    Toast.makeText(
                        this@AddTicketActivity,
                        response.body()!!.getMessage(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<BPBranchResponse>, t: Throwable) {

                Toast.makeText(this@AddTicketActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

//        callPriorityApi()
    }

    private fun callPriorityApi() {
        val call: Call<BPBranchResponse> = ApiClient().service.getAllPriorityList()
        call.enqueue(object : Callback<BPBranchResponse> {
            override fun onResponse(
                call: Call<BPBranchResponse>,
                response: Response<BPBranchResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()!!.getStatus() == 200) {
                        priorityList.clear()
                        priorityList.addAll(response.body()!!.getData())
                        priorityAdapter = PriorityAdapter(this@AddTicketActivity, priorityList)
                        binding.addTicket.prioritySpinner.adapter = priorityAdapter
                        if (priorityList.size > 0) {
                            priorityval = priorityList[0].getPriority()
                        }
                    } else
                        Toast.makeText(
                            this@AddTicketActivity,
                            response.body()!!.getMessage(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                } else {
                    Toast.makeText(
                        this@AddTicketActivity,
                        response.body()!!.getMessage(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onFailure(call: Call<BPBranchResponse>, t: Throwable) {

                Toast.makeText(this@AddTicketActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setData(ticketdata: TicketDetailsData?) {

        offClickableproperty()

        CardCode = ticketdata!!.BusinessPartner.CardCode
        CategoryCode = ticketdata.ItemCategory
        binding.serialNumVal.setText(ticketdata.SerialNo)
        binding.addTicket.businesspartnerValue.setText(ticketdata.BusinessPartner.CardName)
        binding.addTicket.contacnameValue.setText(ticketdata.BusinessPartner.ContactPerson)
        if (ticketdata.BusinessPartner.BPAddresses.isNotEmpty()) {
            binding.addTicket.edBranchList.setText(ticketdata.BusinessPartner.BPAddresses[0].AddressName)
        }else{
            binding.addTicket.edBranchList.setText("NA")
        }
        binding.addTicket.email.setText(ticketdata.BusinessPartner.EmailAddress)
        binding.addTicket.phoneNumber.setText(ticketdata.BusinessPartner.Phone1)
        binding.addTicket.accountName.setText(ticketdata.BusinessPartner.U_ACCNT)
        binding.addTicket.itemCodeValue.setText(ticketdata.ItemDescription)
        binding.addTicket.category.setText(ticketdata.ItemCategoryName)
        binding.addTicket.orderValue.setText(ticketdata.DeliveryId.id.toString())


    }

    private fun offClickableproperty() {
        binding.addTicket.businesspartnerValue.setOnClickListener(null)
        binding.addTicket.contacnameValue.setOnClickListener(null)
        binding.addTicket.edBranchList.setOnClickListener(null)
        binding.addTicket.orderValue.setOnClickListener(null)
        binding.addTicket.itemCodeValue.setOnClickListener(null)
        binding.addTicket.createAndUpdateTicketBtn.setOnClickListener(null)
        binding.loadingback.visibility = View.GONE
        binding.loadingView.stop()
    }


    private fun eventmanager() {
        binding.addTicket.assignedValue.setOnClickListener(this)
        binding.addTicket.businesspartnerValue.setOnClickListener(this)
        binding.addTicket.contacnameValue.setOnClickListener(this)
        binding.addTicket.edBranchList.setOnClickListener(this)
        binding.addTicket.orderValue.setOnClickListener(this)
        binding.addTicket.duedateValue.setOnClickListener(this)
        binding.addTicket.itemCodeValue.setOnClickListener(this)
        binding.addTicket.ticketowner.setOnClickListener(this)
        binding.addTicket.createAndUpdateTicketBtn.setOnClickListener(this)
        binding.toolbar.search.setOnClickListener(this)
        binding.confirmButton.setOnClickListener(this)

        binding.addTicket.statusdropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                statusval = p0?.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                statusval = p0?.selectedItem.toString()
            }

        }


        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.priorityList_gl)
        binding.addTicket.prioritySpinner.setAdapter(priorityAdapter)


        binding.addTicket.prioritySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (Global.priorityList_gl.isNotEmpty()) {
                    priorityval = Global.priorityList_gl[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                if (Global.priorityList_gl.isNotEmpty()) {
                    priorityval = Global.priorityList_gl[0]
                }
            }

        }

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

        binding.addTicket.classificationDropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                classificationval = p0?.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                classificationval = p0?.selectedItem.toString()
            }

        }


        //todo scope of work adapter bind--
        val zoneAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.zoneList_gl)
        binding.addTicket.zonedropdown.setAdapter(zoneAdapter)

        binding.addTicket.zonedropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (Global.zoneList_gl.isNotEmpty()) {
                    zoneval = Global.zoneList_gl[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                if (Global.zoneList_gl.isNotEmpty()) {
                    zoneval = Global.zoneList_gl[0]
                }
            }

        }


      /*  //todo scope of work adapter bind--
        val scopAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.scopeWorkList)
        binding.addTicket.acScopeWork.setAdapter(scopAdapter)*/


        //todo scope work item selected
       /* binding.addTicket.acScopeWork.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.scopeWorkList.isNotEmpty()) {
                    scopeWorkVal = Global.scopeWorkList[position]
                    binding.addTicket.acScopeWork.setText(Global.scopeWorkList[position])

                    val adapter = ArrayAdapter(this@AddTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.scopeWorkList)
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
    /*    val requestAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.reqestTypeList)
        binding.addTicket.acRequestType.setAdapter(requestAdapter)*/


        //todo scope work item selected
        binding.addTicket.acRequestType.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (requestTypeList.isNotEmpty()) {
                    requestTypeVal = requestTypeList[position].Name
                    binding.addTicket.acRequestType.setText(requestTypeList[position].Name)

                    /*val requestAdapter = ArrayAdapter(this@AddTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.reqestTypeList)
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

                    val requestAdapter = ArrayAdapter(this@AddTicketActivity, android.R.layout.simple_dropdown_item_1line, Global.caseOrginList)
                    binding.addTicket.acCaseOrigin.setAdapter(requestAdapter)

                } else {
                    caseOriginVal = ""
                    binding.addTicket.acCaseOrigin.setText("")
                }
            }

        }




    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.businesspartner_value -> {

                //  args.putStringArrayList("data", position)
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.container, SelectBusinessPartnerFragement(this,EditTicketActivity() ,AddContactPerson(), "AddTicket", AddServiceContractActivty())).addToBackStack(null)
                transaction.commit()
            }
            R.id.contacname_value -> {
                if (binding.addTicket.businesspartnerValue.length() == 0) {
                    Global.warningmessagetoast(this, "Select Business Partner")
                } else {
                    contactfragement = SelectDepartmentFragement(binding.addTicket.contacnameValue, contactlist, this, EditTicketActivity(), "AddTicketContext")
                    // args.putStringArrayList("data", position)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, contactfragement).addToBackStack(null)
                    transaction.commit()
                }

            }

            R.id.edBranchList -> {
                if (binding.addTicket.businesspartnerValue.length() == 0) {
                    Global.warningmessagetoast(this, "Select Business Partner")
                } else {
                    branchFragment = SelectBranchFragment(binding.addTicket.edBranchList, branchAllList, this, EditTicketActivity(), "AddTicketContext")
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, branchFragment).addToBackStack(null)
                    transaction.commit()
                }

            }

            R.id.item_code_value -> {

                if (binding.addTicket.orderValue.length() != 0) {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, SelectItemFragement(this, ItemList)).addToBackStack(null)
                    transaction.commit()
                } else {
                    Global.warningmessagetoast(this, "Select Order")

                }
            }
            R.id.order_value -> {

                if (binding.addTicket.businesspartnerValue.length() == 0) {
                    Global.warningmessagetoast(this, "Select Business Partner")
                } else {

                    orderfragement = SelectOrderFragement(this, CardCode)
                    //  args.putStringArrayList("data", position)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, orderfragement).addToBackStack(null)
                    transaction.commit()
                }
            }
            R.id.assigned_value -> {
                employeeFragement = SelectEmployeeFragement(binding.addTicket.assignedValue, "AddTicketFlag")
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.container, employeeFragement).addToBackStack(null)
                transaction.commit()
            }


            R.id.createAndUpdateTicketBtn -> {
                if (validation(binding.addTicket.contacnameValue.text.toString(), binding.addTicket.edBranchList.text.toString(),
                        binding.addTicket.businesspartnerValue.text.toString(), binding.addTicket.email.text.toString(),
                    binding.addTicket.phoneNumber.text.toString() , binding.addTicket.subject.text.toString(),
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
                            ToDate = selectedDataList[i].ToDate
                        )

                        ticketItemList.add(ticketItem)
                    }

                    val addTicketData = AddTicketRequestModel(
                        AlternatePhone = binding.addTicket.alternatephoneNumber.text.toString().trim(),
                        AssignTo = Global.TicketAssigntoID,
                        BranchId = branchID,
                        CardCode = CardCode,
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
                        Status = "Pending",
                        SubType = requestTypeVal,
                        TicketEndDate = "",
                        TicketItems = ticketItemList,
                        TicketStartDate = "",
                        TicketStatus = "Pending",
                        Title = binding.addTicket.subject.text.toString().trim(),
                        Type = scopeWorkVal,
                        Zone = zoneval,
                        TandC = binding.addTicket.edTandC.text.toString().trim(),
                        id = 0
                    )


                    val gson = Gson()
                    val jsonTut: String = gson.toJson(addTicketData)
                    Log.e("data", jsonTut)

                    if (Global.checkForInternet(this)) {
                        binding.loadingView.start()
                        viewModel.createTicket(addTicketData)
                    }
                }

            }
            R.id.duedate_value -> {
                Global.selectDate(this, binding.addTicket.duedateValue)

            }
            R.id.confirm_button -> {
                if (binding.serialNumVal.length() == 0) {
                    Toast.makeText(this, "Enter Serial Num", Toast.LENGTH_SHORT).show()
                } else {
                    val tdd = TicketDetailsData(
                        SerialNo = binding.serialNumVal.text.toString()
                    )

                    viewModel.getTicketDetails(tdd)
                    ticketDetailObserver()

                }
            }
        }
    }


    //todo request type api here---

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

                            var requestTypeAdapter = RequestTypeAdapter(this@AddTicketActivity,R.layout.drop_down_item_textview ,requestTypeList)
                            binding.addTicket.acRequestType.setAdapter(requestTypeAdapter)

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {

                Global.errormessagetoast(
                    this@AddTicketActivity,
                    t.message.toString()
                )
            }
        })
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


    //todo ticket detail
    private fun ticketDetailObserver() {
        viewModel.getTicketDetails.observe(this, Event.EventObserver(

            onError = {
                Log.e("fail==>", it.toString())
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    tdm = it.data[0]
                    setData(it.data[0])
                }else
                    Toast.makeText(this@AddTicketActivity, it.message, Toast.LENGTH_SHORT).show()

            }

        ))
    }


    var contactlist = ArrayList<DataXX>()
    var branchAllList = ArrayList<BranchAllListResponseModel.DataXXX>()

    //todo BP item selected override function---

    override fun selectpartner(bpdata: DataCustomerListForContact) { //AccountBpData
        binding.addTicket.businesspartnerValue.setText(bpdata.CardName)
        CardCode = bpdata.CardCode
//        binding.addTicket.address.setText(bpdata.BPAddresses[0].AddressName + ", " + bpdata.BPAddresses[0].State + ", " + bpdata.BPAddresses[0].Country) //todo comment by me--
        binding.addTicket.orderValue.setText("")
        binding.addTicket.contacnameValue.setText("")
        binding.addTicket.itemCodeValue.setText("")
        binding.addTicket.category.setText("")
        ItemList.clear()
        contactlist.clear()

        orderfragement = SelectOrderFragement(this, bpdata.CardCode)


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
                    Toast.makeText(this@AddTicketActivity, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
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

                }else {
                    Toast.makeText(this@AddTicketActivity, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }

    var branchID = ""

    //todo branch item slected override function--
    override fun selectBranch(bpdata: BranchAllListResponseModel.DataXXX) {

        branchID = bpdata.id.toString()
        selectedDataList.clear()


        var jsonObject = JsonObject()
        jsonObject.addProperty("BranchId", bpdata.id)
        jsonObject.addProperty("CardCode", bpdata.BPCode)

        viewModel.getItemAllList(jsonObject)
        bindItemListObserver()


        zoneval = bpdata.zone
        val zoneSelectedValue = bpdata.zone
        Log.e("TAg", "setData: $zoneSelectedValue")

        val position = resources.getStringArray(R.array.zone_list).indexOf(zoneSelectedValue)
        if (position != -1) {
            binding.addTicket.zonedropdown.setSelection(position)
        } else {
            // String value not found, handle accordingly
        }

        binding.addTicket.address.setText(bpdata.Street)

    }

    var insallItemList: ArrayList<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
    var selectedDataList = ArrayList<ItemAllListResponseModel.DataXXX>()

    private fun bindItemListObserver() {
        viewModel.itemAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        if (it.data.size > 0 && it.data != null) {
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
                                        val gridLayoutManager = GridLayoutManager(this@AddTicketActivity, 2)
                                        val adapterEmp = SelectedItemAdapter(this@AddTicketActivity, selectedDataList, "")
                                        binding.addTicket.rvItemNames.layoutManager = gridLayoutManager
                                        binding.addTicket.rvItemNames.adapter = adapterEmp
                                        adapterEmp.notifyDataSetChanged()
                                        adapter.notifyDataSetChanged()
                                    }

                                    binding.addTicket.acItemName.text.clear()
                                }

                            }

                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        Global.warningmessagetoast(this@AddTicketActivity, it.message!!)
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

      /*  else if (assignedTo.text.isEmpty()) {
            assignedTo.requestFocus()
            assignedTo.error = "Can't Be Empty"
            Toast.makeText(this, "Choose Assigned To ", Toast.LENGTH_SHORT).show()
            return false
        }*/
        return true
    }


    var ItemList = ArrayList<DocumentLine>()


    var CategoryCode = ""

    override fun selecteditemdata(itemdata: DocumentLine) {
        tdm = TicketDetailsData()
        binding.addTicket.itemCodeValue.setText(itemdata.ItemCode)
        binding.addTicket.category.setText(itemdata.CategoryName)
        binding.addTicket.tvProductSerialNumber.setText(itemdata.ItemSerialNo)
        CategoryCode = itemdata.ProjectCode
        binding.serialNumVal.setText(itemdata.ItemSerialNo)
        tdm.ItemCategoryName = itemdata.CategoryName
        tdm.WarrantyStartDate = "itemdata.WarrantyDueDate"
        tdm.WarrantyDueDate = "itemdata.WarrantyDueDate"
        tdm.ExtWarrantyStartDate = "itemdata.ExtWarrantyStartDate"
        tdm.ExtWarrantyDueDate = "itemdata.ExtWarrantyDueDate"
        tdm.AMCStartDate = "itemdata.AMCStartDate"
        tdm.AMCDueDate = "itemdata.AMCDueDate"
        tdm.CMCStartDate = "itemdata.CMCStartDate"


    }


    override fun selectedorderdata(orderdata: DataParticularCustomerOrder) {
        binding.addTicket.orderValue.setText(orderdata.id.toString())
        ItemList.clear()
        ItemList.addAll(orderdata.DocumentLines)
        contactlist.clear()
//        contactlist.addAll(orderdata.ContactPersonCode)//todo comment by chanch
    }


}