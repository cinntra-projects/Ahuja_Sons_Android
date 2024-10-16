package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.R
import com.ahuja.sons.`interface`.SelectBusinessPartneer
import com.ahuja.sons.adapter.DepartMentAdapter
import com.ahuja.sons.adapter.RoleAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.AddContactBinding
import com.ahuja.sons.fragment.SelectBusinessPartnerFragement
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.DataCustomerListForContact
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class AddContactPerson : MainBaseActivity(), View.OnClickListener, SelectBusinessPartneer {

    private lateinit var addContactBinding: AddContactBinding
    var departMentList = ArrayList<DepartmentData>()
    var rolelist = ArrayList<DepartmentData>()
    var rolename = ""
    var departmentName = ""

    // lateinit var accountdata: AccountBpData
    var cardCode = ""
    var cardName = ""
    var id = ""
    private lateinit var suggestionsAdapter: ArrayAdapter<String>

    lateinit var viewModel: MainViewModel

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
        addContactBinding = AddContactBinding.inflate(layoutInflater)
        setUpViewModel()
        setContentView(addContactBinding.root)

        addContactBinding.loadingback.visibility = View.GONE
        addContactBinding.loadingView.stop()
        //   setSupportActionBar(addContactBinding.toolbar)
        //setSupportActionBar(addContactBinding.toolbar.toolbar)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//        supportActionBar!!.setDisplayUseLogoEnabled(true)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

//        // Create the adapter for suggestions
//        suggestionsAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line)
//
//        // Set the adapter on the AutoCompleteTextView
//        addContactBinding.businessPartnerValue.setAdapter(suggestionsAdapter)

        // Set abusiness_partner_value threshold for the minimum number of characters to trigger suggestions
        addContactBinding.businessPartnerValue.threshold = 2
        addContactBinding.toolbarContact.setOnClickListener {
            finish()
        }


//
//        if(intent.extras!=null){
//            accountdata = intent.getParcelableExtra<AccountBpData>(Global.AccountData)!!
//            addContactBinding.businessPartnerValue.setText(accountdata.CardName)
//        }


        //  addContactBinding.toolbar.heading.text = "Add Contact"
        viewModel.getCustomerListForContact()


        viewModel.getDepartMent()
        loadDepartmentObserver()

        eventmanager()
        subsCribeTObserver()

        addContactBinding.btnCreateContact.setOnClickListener {
            if (validation(
                    addContactBinding.firstNameValue.text.toString(),
                    addContactBinding.businessPartnerValue.text.toString(),
                    addContactBinding.mobileValue.text.toString(),
                    addContactBinding.emailValue.text.toString(),
                    addContactBinding.addressValue.text.toString()
                )
            ) {
                val createContact = CreateContactData(
                    CardCode = cardCode,
                    Title = "",
                    Position = rolename,
                    Address = addContactBinding.addressValue.text.toString(),
                    MobilePhone = addContactBinding.mobileValue.text.toString(),
                    E_Mail = addContactBinding.emailValue.text.toString(),
                    Profession = departmentName,
                    FirstName = addContactBinding.firstNameValue.text.toString(),
                    LastName = addContactBinding.firstNameValue.text.toString(),
                    U_BPID = id,
                    U_BRANCHID = "1",
                    U_NATIONALTY = "Indian",
                    CreateDate = Global.getTodayDate(),
                    CreateTime = Global.getTCurrentTime(),
                    UpdateDate = Global.getTodayDate(),
                    UpdateTime = Global.getTCurrentTime(),
                    Remarks1 = "",
                    DateOfBirth = "",
                    Gender = "",
                    Fax = "",
                    MiddleName = "",
                    CountryCode = addContactBinding.countryPickerPhone.selectedCountryCodeWithPlus

                )
                if (Global.checkForInternet(this)) {
                    addContactBinding.loadingView.start()

                    viewModel.createcontact(createContact)

                    bindCreateContactObserver()
//                    createContactApi(createContact)
                }

            }
        }


    }


    //todo load department..
    private fun loadDepartmentObserver() {
        viewModel.getDepartment.observe(this, Event.EventObserver(

            onError = {
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200) {
                    if (it.data.isNotEmpty()) {
                        departMentList.clear()
                        departMentList.addAll(it.data)
                        addContactBinding.departmentSpinner.adapter =
                            DepartMentAdapter(applicationContext, departMentList)
                        if (it.data.isNotEmpty()) {
                            departmentName = it.data[0].Name
                        }
                        addContactBinding.loadingback.visibility = View.GONE
                        addContactBinding.loadingView.stop()
                    }
                }

                viewModel.getRole()

                bindRoleObserver()
            }

        ))
    }


    //todo bind role observer..
    private fun bindRoleObserver() {
        viewModel.getDepartment.observe(this, Event.EventObserver(

            onError = {
                addContactBinding.loadingback.visibility = View.GONE
                addContactBinding.loadingView.stop()
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                addContactBinding.loadingback.visibility = View.VISIBLE
                addContactBinding.loadingView.start()
            },
            onSuccess = {
                if (it.status == 200) {
                    addContactBinding.loadingback.visibility = View.GONE
                    addContactBinding.loadingView.stop()
                    if (it.data.isNotEmpty()) {
                        rolelist.clear()
                        rolelist.addAll(it.data)
                        addContactBinding.roleSpinner.adapter =
                            RoleAdapter(applicationContext, rolelist)
                        if (it.data.isNotEmpty()) {
                            rolename = it.data[0].Name
                        }

                    }
                }
            }

        ))
    }

    companion object {
        private const val TAG = "AddContactPerson"
    }

    private fun subsCribeTObserver() {
        viewModel.customerListContact.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "subsCribeTObserverERROR:$it ")
//                Global.warningmessagetoast(this, it)
            }, onLoading = {

            }, { customer ->
                if (customer.status == 200) {
                    val itemNames: MutableList<String> = java.util.ArrayList()
                    //  List<String> itemCode = new ArrayList<>();
                    //  List<String> itemCode = new ArrayList<>();
                    for (item in customer.data) {
                        itemNames.add(item.CardName)
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        itemNames
                    )

                    //   participant_value.setAdapter<ArrayAdapter<String>>(adapter)

                    addContactBinding.businessPartnerValue.setAdapter<ArrayAdapter<String>>(
                        adapter
                    )

                    addContactBinding.businessPartnerValue.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            cardName = parent.getItemAtPosition(position) as String
                            cardCode = customer.data[position].CardCode
                            this@AddContactPerson.id = customer.data[position].id.toString()

                            addContactBinding.businessPartnerValue.setText(cardName)

                            // Clear the AutoCompleteTextView after selecting the item

                        }

                } else {
                    Global.warningmessagetoast(this, customer.message)
                }

            }
        ))
    }

    private fun eventmanager() {

        //  addContactBinding.toolbar.search.setOnClickListener(this)
        //  addContactBinding.createButton.setOnClickListener(this)
        addContactBinding.bussinessPartner.setOnClickListener(this)


        addContactBinding.departmentSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    departmentName = departMentList[position].Name
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    departmentName = departMentList[0].Name
                }
            }


        addContactBinding.roleSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (rolelist.size > 0 && position > 0) {
                        rolename = rolelist[position].Name
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    rolename = rolelist[0].Name
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

            R.id.bussinessPartner -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.main_edit_qt_frame, SelectBusinessPartnerFragement(AddTicketActivity(),EditTicketActivity(), this,"AddContact", AddServiceContractActivty()))
                    .addToBackStack(null)
                transaction.commit()
            }
        }

    }


    private fun bindCreateContactObserver() {
        viewModel.createContact.observe(this, Event.EventObserver(

            onError = {
                addContactBinding.loadingView.stop()
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                addContactBinding.loadingView.start()
            },
            onSuccess = {
                if (it.status == 200) {
                    addContactBinding.loadingback.visibility = View.GONE
                    addContactBinding.loadingView.stop()
                    Global.successmessagetoast(this@AddContactPerson, "Added Succesfully")
                    finish()
                } else if (it.status == 201) {
                    addContactBinding.loadingView.stop()
                    Global.warningmessagetoast(this@AddContactPerson, it.message!!)
                } else {
                    addContactBinding.loadingView.stop()
                    Global.warningmessagetoast(this@AddContactPerson, it.toString())
                }

            }

        ))
    }


    private fun validation(
        firstname: String,
        businesspartner: String,
        mobile: String,
        email: String,
        address: String
    ): Boolean {


        if (firstname.isEmpty()) {
            addContactBinding.firstNameValue.error = "Enter First Name"
            return false
        } else if (mobile.isEmpty()) {
            addContactBinding.mobileValue.error = "Enter Mobile no."
            return false
        } else if (email.isEmpty()) {
            addContactBinding.emailValue.error = "Enter email"
            return false
        } else if (address.isEmpty()) {
            addContactBinding.addressValue.error = "Enter Address"
            return false
        }
        return true
    }

    override fun selectpartner(bpdata: DataCustomerListForContact) {
        // accountdata = bpdata
        //  addContactBinding.businessPartnerValue.setText(accountdata.CardName)
    }


}
