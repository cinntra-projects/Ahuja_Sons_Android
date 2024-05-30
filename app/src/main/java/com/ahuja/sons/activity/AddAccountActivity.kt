package com.ahuja.sons.activity

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.ahuja.sons.adapter.*
import com.ahuja.sons.apiservice.ApiClient

import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.R
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.AddAccountsBinding
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AddAccountActivity : MainBaseActivity(), View.OnClickListener {

    private lateinit var binding: AddAccountsBinding
    var salesEmployeeCode = 0

    var payment_term = ""
    var parenT_account = ""

    lateinit var shippinngType: Array<String>
    var billshipType: String = ""
    var ship_shiptype: String = ""
    var TYPE = ""
    var industryCode: String = ""
    var zoneval: String = ""

    var billtoState: String = ""
    var billtoStateCode: String = ""
    var billtoCountrycode: String = ""
    var billtoCountryName: String = ""
    var shiptoState: String = ""
    var shiptoCountrycode: String = ""
    var shiptoCountryName: String = ""
    var shiptoStateCode: String = ""
    lateinit var countryAdapter: CountryApapter
    lateinit var stateAdapter: StateAdapter
    lateinit var shipStateAdapter: StateAdapter
    var stateList: ArrayList<BPLID> = ArrayList<BPLID>()
    var shipstateList: ArrayList<BPLID> = ArrayList<BPLID>()
    var districtList = ArrayList<BPLID>()
    var zoneList = ArrayList<BPLID>()
    var countrylist = ArrayList<BPLID>()
    var SalesEmployeeList = ArrayList<EmployeeData>()
    lateinit var braanchAdapter: BranchMultiAdapter
    lateinit var zoneAdapter: ZoneAdapter
    var getPaymenterm = ArrayList<BPLID>()
    var IndustryItemItemList = ArrayList<BPLID>()


    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setUpViewModel()

        viewModel.getBPList()


        binding.toolbar.heading.text = "Add Account"
        frameManager(binding.generalFrame, binding.contactFrame, binding.general, binding.contact)

        binding.contactview.createButton.setOnClickListener(this)
        binding.tab1.setOnClickListener(this)
        binding.general.setOnClickListener(this)
        binding.tab2.setOnClickListener(this)
        binding.contact.setOnClickListener(this)
        if (Global.checkForInternet(this)) {
            viewModel.getCountryList()
            countryBindObserver()
        }
        eventmanager()

        bindObserver()
    }


    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    var AllitemsList = ArrayList<AccountBpData>()

    //todo bind observer...
    private fun bindObserver() {
        viewModel.businessPartnerList.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
            },
            onLoading = {

            },
            onSuccess = { response ->
                if (response.status == 200) {
                    AllitemsList.clear()
                    val bpd = AccountBpData()
                    bpd.CardName = ("No Parent Account")
                    AllitemsList.add(bpd)
                    AllitemsList.addAll(response.data)

                    parenT_account = filter(AllitemsList)[0]
                    binding.generalview.parentAccountValue.adapter = ArrayAdapter<String>(
                        this@AddAccountActivity,
                        android.R.layout.simple_list_item_1, filter(AllitemsList)
                    )
                }

                viewModel.getAllBranchList()
                allBranchObserver()

            }

        ))


    }


    //todo payment term ..
    private fun allBranchObserver() {
        viewModel.getAllData.observe(this, Event.EventObserver(
            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.getStatus() == 200) {
                    if (it.getData().isNotEmpty()) {
                        districtList.clear()
                        districtList.addAll(it.getData())
                    } else {
                        Toast.makeText(
                            this@AddAccountActivity,
                            it.getMessage(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                viewModel.getAllZoneList()
                zoneObserver()

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
                if (it.getStatus() == 200) {
                    if (it.getData().isNotEmpty()) {
                        zoneList.clear()
                        zoneList.addAll(it.getData())
                        zoneAdapter = ZoneAdapter(this@AddAccountActivity, zoneList)
                        binding.contactview.zoneValue.adapter = zoneAdapter
                        zoneval = zoneList[0].getZone()
                    } else
                        Toast.makeText(
                            this@AddAccountActivity,
                            it.getMessage(),
                            Toast.LENGTH_SHORT
                        ).show()

                }

                viewModel.getPaymentTerm()
                paymentBindObserver()
            }

        ))
    }


    //todo payment term ..
    private fun paymentBindObserver() {
        viewModel.getAllData.observe(this, Event.EventObserver(

            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.getStatus() == 200) {
                    if (it.getData().isNotEmpty()) {
                        getPaymenterm.clear()
                        getPaymenterm.addAll(it.getData())
                        binding.generalview.paymentTermValue.adapter =
                            PaymentAdapter(this@AddAccountActivity, getPaymenterm)
                        payment_term = getPaymenterm[0].getGroupNumber()
                    }
                }

                viewModel.getIndustryList()
                callIndustryObserver()
            }

        ))
    }


    //todo industry observer..

    private fun callIndustryObserver() {
        viewModel.getAllData.observe(this, Event.EventObserver(

            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.getStatus() == 200) {
                    if (it.getData().isNotEmpty()) {
                        IndustryItemItemList.addAll(it.getData())
                        binding.generalview.industrySpinner.adapter = IndustrySpinnerAdapter(
                            this@AddAccountActivity,
                            IndustryItemItemList
                        )
                        industryCode = IndustryItemItemList[0].getIndustryCode().toString()
                    }
                }
            }

        ))
    }


    //todo country bind observer..

    private fun countryBindObserver() {
        viewModel.getAllData.observe(this, Event.EventObserver(
            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.getStatus() == 200) {
                    if (it.getData().isNotEmpty()) {
                        countrylist.clear()
                        countrylist.addAll(it.getData())
                    }

                }

                val employeeValue = NewLoginData()
                employeeValue.setSalesEmployeeCode(Global.Employee_Code)
                viewModel.getSalesEmplyeeList(employeeValue)

                salesEmployeeObserver()
            }

        ))
    }


    //todo sales empoyee list
    private fun salesEmployeeObserver() {
        viewModel.salesEmployeeResponse.observe(this, Event.EventObserver(
            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.getStatus() == 200) {
                    SalesEmployeeList.clear()
                    SalesEmployeeList.addAll(it.getData())
                    salesEmployeeCode = SalesEmployeeList[0].getSalesEmployeeCode()!!.toInt()
                }

                viewModel.getBPList()
            }

        ))
    }


    private fun filter(allitemsList: ArrayList<AccountBpData>): List<String> {
        val bplist = ArrayList<String>()
        bplist.clear()
        bplist.add("No Parent Account")
        for (bpdata in allitemsList) {
            bplist.add(bpdata.CardName)
        }
        return bplist
    }


    private fun multiDistic(distList: java.util.ArrayList<BPLID>) {
        val dialog = Dialog(this@AddAccountActivity)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        //dialog.setTitle("Select Districts");
        dialog.setContentView(R.layout.district_dialog)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val districList = dialog.findViewById<RecyclerView>(R.id.dist)
        val done = dialog.findViewById<Button>(R.id.done)
        if (distList.size > 0) {
            braanchAdapter = BranchMultiAdapter(this@AddAccountActivity, distList)
            districList.layoutManager =
                LinearLayoutManager(this@AddAccountActivity, RecyclerView.VERTICAL, false)
            districList.setHasFixedSize(true)
            districList.adapter = braanchAdapter
        }
        done.setOnClickListener {
            if (distList.size > 0) binding.contactview.branchName.text = getName(distList)
            dialog.dismiss()
        }
        dialog.show()
    }

    var districtCode: String = ""
    var districtName: String = ""
    private fun getName(districtList: ArrayList<BPLID>): String? {
        districtCode = ""
        districtName = ""

        // use for loop
        for (j in districtList.indices) {
            // concat array value
            if (districtList[j].isSelected() && districtName!!.isEmpty()) {
                districtName = districtList[j].getBPLName()
                districtCode = "" + districtList[j].getBPLId()
            } else if (districtList[j].isSelected()) {
                districtName = districtName + ", " + districtList[j].getBPLName()
                districtCode = districtCode + "," + districtList[j].getBPLId()
            }
        }
        return districtName
    }


    private fun eventmanager() {
        countryAdapter = CountryApapter(this, countrylist)
        binding.contactview.addressView.countryValue.adapter = countryAdapter
        binding.contactview.addressView.countryValue.setSelection(
            Global.getCountrypos(
                countrylist,
                "India"
            )
        )
        binding.contactview.branchName.setOnClickListener(View.OnClickListener {
            multiDistic(
                districtList
            )
        })

        binding.contactview.addressView.countryValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    billtoCountrycode = countrylist[position].getCode()
                    billtoCountryName = countrylist[position].getName()
                    billtoState = ""
                    billtoStateCode = ""

                    //todo request
                    val stateData = BPLID()
                    stateData.setCountry(billtoCountrycode)

                    viewModel.getStateList(stateData)

                    bindStateObserver("billto")

//                    callStateApi(billtoCountrycode, "billto")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    billtoCountrycode = countrylist[0].getCode()
                    billtoCountryName = countrylist[0].getName()
                }
            }

        binding.contactview.addressView.stateValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    billtoState = stateList[position].getName()
                    billtoStateCode = stateList[position].getCode()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    billtoState = stateList[0].getName()
                    billtoStateCode = stateList[0].getCode()
                }
            }

        binding.contactview.addressView.shipCountryValue.adapter = countryAdapter
        binding.contactview.addressView.shipCountryValue.setSelection(
            Global.getCountrypos(
                countrylist,
                "India"
            )
        )

        binding.contactview.addressView.shipCountryValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    shiptoCountrycode = countrylist[position].getCode()
                    shiptoCountryName = countrylist[position].getName()
                    shiptoState = ""
                    shiptoStateCode = ""

                    //todo request
                    val stateData = BPLID()
                    stateData.setCountry(billtoCountrycode)

                    viewModel.getStateList(stateData)

                    bindStateObserver("shipto")

//                    callStateApi(shiptoCountrycode, "shipto")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    shiptoCountrycode = countrylist[0].getCode()
                    shiptoCountryName = countrylist[0].getName()
                }
            }

        binding.contactview.addressView.shipStateValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    shiptoState = stateList[position].getName()
                    shiptoStateCode = stateList[position].getCode()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    shiptoState = stateList[0].getName()
                    shiptoStateCode = stateList[0].getCode()
                }
            }

        if (stateList.isEmpty()) {
            val sta = BPLID()
            sta.setName("Select State")
            stateList.add(sta)
        }

        binding.generalview.salesEmployeeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (SalesEmployeeList.size > 0 && position > 0) salesEmployeeCode =
                    SalesEmployeeList[position].getSalesEmployeeCode()!!.toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                salesEmployeeCode = SalesEmployeeList[0].getSalesEmployeeCode()!!.toInt()
            }
        }

        binding.generalview.parentAccountValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (AllitemsList.size > 0 && position > 0) parenT_account =
                        AllitemsList[position].CardName
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parenT_account = AllitemsList[0].CardName
                }
            }

        binding.generalview.typeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    TYPE = binding.generalview.typeSpinner.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TYPE = binding.generalview.typeSpinner.selectedItem.toString()
                }
            }

        binding.contactview.addressView.shippingSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    billshipType = shippinngType[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    billshipType = shippinngType[0]
                }
            }

        binding.contactview.addressView.shippingSpinner2.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    ship_shiptype = shippinngType[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    ship_shiptype = shippinngType[0]
                }
            }

        binding.contactview.zoneValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    zoneval = zoneList[position].getZone()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    zoneval = zoneList[0].getZone()
                }
            }

        binding.generalview.industrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (IndustryItemItemList.size > 0) industryCode =
                        IndustryItemItemList[position].getIndustryCode()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    if (IndustryItemItemList.size > 0) industryCode =
                        IndustryItemItemList[0].getIndustryCode()
                }
            }
        binding.contactview.addressView.checkbox1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.contactview.addressView.shipBlock.visibility = View.VISIBLE
            } else {
                binding.contactview.addressView.shipBlock.visibility = View.GONE
            }
        })
        binding.generalview.paymentTermValue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (getPaymenterm.size > 0) payment_term =
                        getPaymenterm.get(position).getGroupNumber()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    payment_term = getPaymenterm[0].getGroupNumber()
                }
            }

    }

    private fun bindStateObserver(s: String) {
        viewModel.getAllData.observe(this, Event.EventObserver(
            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.getStatus() == 200) {
                    if (s.equals("billto", ignoreCase = true)) {
                        stateList.clear()
                        if (it.getData().isNotEmpty()) {
                            stateList.addAll(it.getData())
                        } else {
                            val sta = BPLID()
                            sta.setName("Select State")
                            stateList.add(sta)
                        }
                        stateAdapter = StateAdapter(this@AddAccountActivity, stateList)
                        binding.contactview.addressView.stateValue.adapter = stateAdapter
                        stateAdapter.notifyDataSetChanged()
                        billtoState = stateList[0].getName()
                        billtoStateCode = stateList[0].getCode()
                    } else {
                        shipstateList.clear()
                        if (it.getData().isNotEmpty()) {
                            shipstateList.addAll(it.getData())
                        } else {
                            val sta = BPLID()
                            sta.setName("Select State")
                            shipstateList.add(sta)
                        }
                        shipStateAdapter = StateAdapter(this@AddAccountActivity, shipstateList)
                        binding.contactview.addressView.shipStateValue.adapter = shipStateAdapter
                        shipStateAdapter.notifyDataSetChanged()
                        shiptoState = stateList[0].getName()
                        shiptoStateCode = stateList[0].getCode()
                    }

                }

            }

        ))
    }


/*
    private fun callStateApi(Countrycode: String, s: String) {
        val stateData = BPLID()
        stateData.setCountry(Countrycode)
        val call: Call<BPBranchResponse> = ApiClient().service.getStateList(stateData)
        call.enqueue(object : Callback<BPBranchResponse> {
            override fun onResponse(
                call: Call<BPBranchResponse>,
                response: Response<BPBranchResponse>
            ) {
                if (response.code() == 200) {
                    if (s.equals("billto", ignoreCase = true)) {
                        stateList.clear()
                        if (response.body()!!.getData().isNotEmpty()) {
                            stateList.addAll(response.body()!!.getData())
                        } else {
                            val sta = BPLID()
                            sta.setName("Select State")
                            stateList.add(sta)
                        }
                        stateAdapter = StateAdapter(this@AddAccountActivity, stateList)
                        binding.contactview.addressView.stateValue.adapter = stateAdapter
                        stateAdapter.notifyDataSetChanged()
                        billtoState = stateList[0].getName()
                        billtoStateCode = stateList[0].getCode()
                    } else {
                        shipstateList.clear()
                        if (response.body()!!.getData().isNotEmpty()) {
                            shipstateList.addAll(response.body()!!.getData())
                        } else {
                            val sta = BPLID()
                            sta.setName("Select State")
                            shipstateList.add(sta)
                        }
                        shipStateAdapter = StateAdapter(this@AddAccountActivity, shipstateList)
                        binding.contactview.addressView.shipStateValue.adapter = shipStateAdapter
                        shipStateAdapter.notifyDataSetChanged()
                        shiptoState = stateList[0].getName()
                        shiptoStateCode = stateList[0].getCode()
                    }
                }
            }

            override fun onFailure(call: Call<BPBranchResponse>, t: Throwable) {
                Toast.makeText(this@AddAccountActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
*/


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }


    lateinit var bp1: BPAddresse
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.back_press -> {
                onBackPressed()
            }
            R.id.tab_1, R.id.general -> {
                frameManager(
                    binding.generalFrame,
                    binding.contactFrame,
                    binding.general,
                    binding.contact
                )
            }
            R.id.tab_2, R.id.contact -> {
                frameManager(
                    binding.contactFrame,
                    binding.generalFrame,
                    binding.contact,
                    binding.general
                )
            }
            R.id.create_button -> {
                val name: String = binding.generalview.nameValue.text.toString().trim()
                val balance: String = binding.generalview.accountBalanceValue.text.toString().trim()
                val anlrvnue: String = binding.generalview.annualRevenueValue.text.toString().trim()
                val credit_limit: String =
                    binding.generalview.creditLimitValue.text.toString().trim()
                val invoice: String = binding.generalview.invoiceNoValue.text.toString().trim()
                val rating: String = binding.generalview.ratingBar.rating.toString()
                val cardCode: String = binding.generalview.cardCodeValue.text.toString().trim()
                val mobile: String = binding.contactview.mobileValue.text.toString().trim()
                val email: String = binding.contactview.emailValue.text.toString().trim()
                val website: String = binding.contactview.websiteValue.text.toString().trim()
                val comp_email: String =
                    binding.generalview.companyEmailValue.text.toString().trim()
                val comp_no: String = binding.generalview.companyNoValue.text.toString().trim()
                if (validation(
                        name,
                        comp_email,
                        comp_no,
                        mobile,
                        email,
                        industryCode,
                        billtoStateCode
                    )
                ) {
                    /************************ BP Address  */
                    val postbpAddresses = ArrayList<BPAddresse>()
                    val bp = BPAddresse(
                        BPCode = cardCode,
                        AddressName = binding.contactview.addressView.billingNameValue.text.toString(),
                        Street = binding.contactview.addressView.billingAddressValue.text.toString(),
                        Block = "",
                        ZipCode = binding.contactview.addressView.zipCodeValue.text.toString(),
                        City = "",
                        Country = billtoCountrycode,
                        State = billtoStateCode,
                        U_COUNTRY = billtoCountryName,
                        U_STATE = billtoState,
                        U_SHPTYP = billshipType,
                        RowNum = "0",
                        AddressType = "bo_BillTo"
                    )
                    postbpAddresses.add(bp)

                    if (binding.contactview.addressView.checkbox1.isChecked) {
                        bp1 = BPAddresse(
                            ZipCode = binding.contactview.addressView.zipcodeValue2.text.toString(),
                            AddressName = binding.contactview.addressView.shippingNameValue.text.toString(),
                            U_STATE = shiptoState,
                            Street = binding.contactview.addressView.shippingAddressValue.text.toString(),
                            U_COUNTRY = shiptoCountryName,
                            U_SHPTYP = ship_shiptype,
                            State = shiptoStateCode,
                            Country = shiptoCountrycode,
                            RowNum = "1",
                            BPCode = cardCode,
                            Block = "",
                            City = "",
                            AddressType = "bo_ShipTo"
                        )


                    } else {
                        bp1 = BPAddresse(
                            ZipCode = binding.contactview.addressView.zipCodeValue.text.toString(),
                            AddressName = binding.contactview.addressView.billingNameValue.text.toString(),
                            U_STATE = billtoState,
                            Street = binding.contactview.addressView.billingAddressValue.text.toString(),
                            U_COUNTRY = billtoCountryName,
                            U_SHPTYP = billshipType,
                            State = billtoStateCode,
                            Country = billtoCountrycode,
                            RowNum = "1",
                            BPCode = cardCode,
                            Block = "",
                            City = "",
                            AddressType = "bo_ShipTo"
                        )

                    }


                    postbpAddresses.add(bp1)
                    /********************* Con Employee  */
                    val postcontactEmployees = ArrayList<ContactEmployee>()
                    val postemp = ContactEmployee(
                        Name = binding.contactview.contactOwnerValue.text.toString(),
                        E_Mail = email,
                        MobilePhone = mobile
                    )

                    postcontactEmployees.add(postemp)
                    val contactExtension = BusinessPartnerDataNew()
                    contactExtension.setU_LEADID("")
                    contactExtension.setU_LEADNM("")
                    contactExtension.setUCurbal(balance)
                    contactExtension.setPayTermsGrpCode(payment_term)
                    contactExtension.setCreditLimit(credit_limit)
                    contactExtension.setURating(rating)
                    contactExtension.setUInvno(invoice)
                    contactExtension.setUParentacc(parenT_account)
                    contactExtension.setCardCode(cardCode)
                    contactExtension.setCardName(name)
                    contactExtension.setCardType("cCustomer") //select value from spinner
                    contactExtension.setSalesPersonCode(salesEmployeeCode.toString())
                    contactExtension.setEmailAddress(comp_email)
                    contactExtension.setPhone1(comp_no)
                    contactExtension.setUType(TYPE)
                    contactExtension.setNotes(binding.contactview.remarksValue.text.toString())
                    contactExtension.setUAnlrvn(anlrvnue)
                    contactExtension.setIndustry(industryCode)
                    contactExtension.setUAccnt("")
                    contactExtension.setWebsite(website)
                    contactExtension.setDiscountPercent("")
                    contactExtension.setCurrency("INR")
                    contactExtension.setIntrestRatePercent("")
                    contactExtension.setCommissionPercent("")
                    contactExtension.setAttachmentEntry("")
                    contactExtension.setUBpgrp("")
                    contactExtension.setUContownr(binding.contactview.contactOwnerValue.text.toString())
                    // contactExtension.setContactPerson(contact_owner_value.getText().toString());
                    contactExtension.setCreateDate(Global.getTodayDate())
                    contactExtension.setCreateTime(Global.getTCurrentTime())
                    contactExtension.setUpdateDate("")
                    contactExtension.setuLat("28.622827380895615")
                    contactExtension.setuLong("77.36626848578453")
                    contactExtension.setUpdateTime("")
                    contactExtension.setZone(zoneval)
                    val result = districtCode.replace("^\"|\"$".toRegex(), "")
                    val aa = result.split(",".toRegex()).toTypedArray()
                    contactExtension.setBPL_IDAssignedToInvoice(aa)
                    contactExtension.setBPAddresses(postbpAddresses)
                    contactExtension.setContactEmployees(postcontactEmployees)
                    if (Global.checkForInternet(this)) {
                        binding.loadingView.start()

                        viewModel.addnewCustomer(contactExtension)

//                        createBP(contactExtension)

                        addCustomerBindObserver()
                    }
                }
            }
        }
    }

    //todo add customer bind observer...
    private fun addCustomerBindObserver() {
        viewModel.addNewCustomer.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
            },
            onLoading = {
                binding.loadingView.start()
                binding.loadingback.visibility = View.GONE
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE

                    Global.successmessagetoast(this@AddAccountActivity, "Success")
                    onBackPressed()

                }
            }

        ))


    }


    //todo comment by chanchal

    /* private fun createBP(`in`: BusinessPartnerDataNew) {
         val call: Call<AccountBPResponse> = ApiClient().service.addnewCustomer(`in`)
         call.enqueue(object : Callback<AccountBPResponse> {
             override fun onResponse(
                 call: Call<AccountBPResponse>,
                 response: Response<AccountBPResponse>
             ) {

                 if (response.code() == 200) {

                     Global.successmessagetoast(this@AddAccountActivity, "Sucess")
                     onBackPressed()

                 } else
                     Global.warningmessagetoast(
                         applicationContext,
                         response.body()!!.message
                     )

                 binding.loadingView.stop()
                 binding.loadingback.visibility = View.GONE

             }

             override fun onFailure(call: Call<AccountBPResponse>, t: Throwable) {

                 Global.errormessagetoast(applicationContext, t.message.toString())
                 binding.loadingView.stop()
                 binding.loadingback.visibility = View.GONE

             }
         })
     }*/


    private fun validation(
        cowner: String,
        comp_name: String,
        comp_no: String,
        mobile: String,
        email: String,
        industryCode: String,
        billtoStateCode: String
    ): Boolean {
        if (cowner.isEmpty()) {
            Global.warningmessagetoast(this, "Enter name")
            return false
        } else if (comp_name.isEmpty() || !isvalidateemail(binding.generalview.companyEmailValue)) {
            Global.warningmessagetoast(this, "Enter Company Email")
            return false
        } else if (!Patterns.WEB_URL.matcher(
                binding.contactview.websiteValue.text.toString().trim()
            )
                .matches()
        ) {
            binding.contactview.websiteValue.error = "Enter Valid Url"
            return false
        } else if (comp_no.isEmpty()) {
            Global.warningmessagetoast(this, "Please enter Company No.")
            return false
        } else if (mobile.length != 10) {
            Global.warningmessagetoast(this, "Please enter mobile number")
            return false
        } else if (industryCode.trim().equals("-1", ignoreCase = true)) {
            Global.warningmessagetoast(this, "Select Industry.")
            return false
        } else if (email.isEmpty() || !isvalidateemail(binding.contactview.emailValue)) {
            Global.warningmessagetoast(this, "Enter email address")
            return false
        } else if (billtoStateCode.isEmpty()) {
            Global.warningmessagetoast(this, "Select Billing State")
            return false
        } else if (districtCode.isEmpty()) {
            Global.warningmessagetoast(this, "Select at least 1 Branch")
            return false
        } else if (binding.contactview.contactOwnerValue.length() == 0) {
            binding.contactview.contactOwnerValue.error = "Enter Contact Owner"
            Global.warningmessagetoast(this, "Enter Contact Owner")
            return false
        } else if (binding.contactview.addressView.billingNameValue.length() == 0) {
            binding.contactview.addressView.billingNameValue.error = "Enter Billing Name"
            Global.warningmessagetoast(this, "Enter Billing Name")
            return false
        } else if (binding.contactview.addressView.zipCodeValue.length() == 0) {
            binding.contactview.addressView.zipCodeValue.error = "Enter zipcode"
            Global.warningmessagetoast(this, "Enter zipcode")
            return false
        }
        return true
    }

    private fun isvalidateemail(email_value: EditText): Boolean {
        val checkEmail = email_value.text.toString()
        val hasSpecialEmail = Patterns.EMAIL_ADDRESS.matcher(checkEmail).matches()
        if (!hasSpecialEmail) {
            email_value.error = "This E-Mail address is not valid"
            Toast.makeText(this, "Enter valid E-Mail.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun frameManager(
        visiblle_frame: FrameLayout,
        f1: FrameLayout,
        selected: TextView,
        t1: TextView
    ) {
        selected.setTextColor(resources.getColor(R.color.colorPrimary))
        t1.setTextColor(resources.getColor(R.color.black))
        visiblle_frame.visibility = View.VISIBLE
        f1.visibility = View.GONE
    }

}