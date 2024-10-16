package com.ahuja.sons.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.BranchFilterDropDownAdapter
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.AccountDetailsViewBinding
import com.ahuja.sons.fragment.*
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.collections.ArrayList

class AccountDetailActivity : MainBaseActivity(), View.OnClickListener {

    private lateinit var ticketbiding: AccountDetailsViewBinding
    var accountdata = AccountBpData()
    lateinit var viewModel: MainViewModel
    var BranchCode = ""
    var BranchName = ""
    private val TAG = "AccountDetailActivity"

    interface MyFragmentCustomerListener {
        fun onDataPassedCustomer(startDate: String?, endDate: String?, pos : Int?)
//        fun onDataPassedCustomer(data : BranchAllListResponseModel.DataXXX)
    }

    private var listenerCustomer: MyFragmentCustomerListener? = null

    override fun onAttachFragment(@NonNull fragment: Fragment) {

        if (fragment is MyFragmentCustomerListener) {
            listenerCustomer = fragment
        } else {
            throw RuntimeException((fragment.toString() + " must implement OnDataReceivedListener"))
        }
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }
    var serviceID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ticketbiding = AccountDetailsViewBinding.inflate(layoutInflater)
        setContentView(ticketbiding.root)
        setUpViewModel()
        setSupportActionBar(ticketbiding.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        Prefs.putString(Global.SpinnerAddressType, "")
        Prefs.putString(Global.SpinnerBranchId, "")
        accountdata = intent.getParcelableExtra<AccountBpData>(Global.AccountData)!!

        serviceID = accountdata.CardCode

        setData()
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        if (Global.checkForInternet(this)) {

            val tdd = ContactEmployee(CardCode = accountdata.CardCode)
            viewModel.getbpwiseTicket(tdd)
            bpWiseObserver()


            //todo calling branch api list here---

            var jsonObject1 = JsonObject()
            jsonObject1.addProperty("BPCode", accountdata.CardCode)
            viewModel.getBranchAllList(jsonObject1)
            bindBranchListObserver()


            ticketbiding.ticketLayout.setOnClickListener {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val mFragmentTransaction = supportFragmentManager.beginTransaction()
                var mFragment = TicketFragment(0)

                val mBundle = Bundle()
                mBundle.putString("serviceID", serviceID)
                mBundle.putString("Flag", "CustomerDetail")
                mFragment.arguments = mBundle
                mFragmentTransaction.replace(R.id.frame_ticket, mFragment).addToBackStack(null).commit()
            }
        }


        eventmanager()

        /*pagerAdapter.add(CustomerDetailFragment(accountdata), "Details")
        pagerAdapter.add(BranchListFragment(accountdata), "Branch")
//        pagerAdapter.add(CustomerOrderFragment(accountdata), "Orders")
        pagerAdapter.add(CustomerContactFragment(accountdata), "Contacts")
        pagerAdapter.add(CustomerEquipmentFragment(accountdata), "Equipments")
        ticketbiding.viewpager.adapter = pagerAdapter
        ticketbiding.tabLayout.setupWithViewPager(ticketbiding.viewpager)
*/

        //todo load fragment on fragment container..
        if (savedInstanceState == null) {
            // Load the initial fragment when the activity is created for the first time
            loadFragment(CustomerDetailFragment(accountdata))
        }

        // Listen for radio button changes
        ticketbiding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_details -> loadFragment(CustomerDetailFragment(accountdata))
                R.id.radioBranch -> loadFragment(BranchListFragment(accountdata))
                R.id.radio_contact -> loadFragment(CustomerContactFragment(accountdata))
                R.id.radio_equipment -> loadFragment(CustomerEquipmentFragment(accountdata))

               /* R.id.radio_order -> loadFragment(CustomerOrderFragment(accountdata))
                R.id.radio_all_items -> loadFragment(CustomerAllItemsFragment("customer",accountdata))
                R.id.radio_system -> loadFragment(CustomerSystemFragment("customer",accountdata))
                R.id.radio_removed -> loadFragment(CustomerRemovedFragment("customer"))*/
            }
        }

        ticketbiding.chatView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // Specifies the "mailto" scheme
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(accountdata.EmailAddress)
            ) // Sets the recipient email address
            // it.putExtra(Intent.EXTRA_SUBJECT, subject) // Sets the email subject
            // it.putExtra(Intent.EXTRA_TEXT, body) // Sets the email body
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Global.infomessagetoast(this, "No App Found")
                // Handle case when Gmail app is not installed
                // For example, you can open a web-based email service
            }
        }


        ticketbiding.callView.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + accountdata.Phone1)
            startActivity(intent)
        }

    }

    // todo Function to load a fragment into the container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    var branchAllList = ArrayList<BranchAllListResponseModel.DataXXX>()
    //todo branch observer---
    private fun bindBranchListObserver() {
        viewModel.branchAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e("fail==>", it)
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    branchAllList.clear()
                    branchAllList.addAll(it.data)

                    var shipToFilterList : ArrayList<BranchAllListResponseModel.DataXXX> = ArrayList()
                    for (item in branchAllList){
                        if (item.AddressType == "bo_ShipTo"){
                            shipToFilterList.add(item)
                        }
                    }

                    var adapter = BranchFilterDropDownAdapter(this@AccountDetailActivity, R.layout.drop_down_item_textview, shipToFilterList)
                    ticketbiding.acBranch.setAdapter(adapter)

                }else {
                    Toast.makeText(this@AccountDetailActivity, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


    //todo bp wise observer..
    private fun bpWiseObserver() {
        viewModel.getBPWiseTicket.observe(this, Event.EventObserver(

            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200) {
                    Log.e("bpwisedata", it.data.toString())
                    ticketbiding.allcount.text = it.data.All.toString()
                    ticketbiding.pendcount.text = it.data.Pending.toString()
                    ticketbiding.acceptcount.text = it.data.Accepted.toString()
                    ticketbiding.rejectcount.text = it.data.Rejected.toString()

                }else
                    Toast.makeText(this@AccountDetailActivity, it.message, Toast.LENGTH_SHORT).show()


            }

        ))
    }


    private fun eventmanager() {
        ticketbiding.onholdview.setOnClickListener(this)
        ticketbiding.allView.setOnClickListener(this)
        ticketbiding.openView.setOnClickListener(this)
        ticketbiding.overdueView.setOnClickListener(this)



        //todo item click of branch ..
        ticketbiding.acBranch.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (branchAllList.size > 0) {
                    var data = branchAllList[position]
                    BranchCode = data.id!!

                    BranchName = data.AddressName
                    ticketbiding.acBranch.setText(data.AddressName)

                    Log.e(TAG, "onItemSelected: ")

                    Prefs.putString(Global.SpinnerAddressType,data.BPCode)
                    Prefs.putString(Global.SpinnerBranchId,position.toString())
                    listenerCustomer!!.onDataPassedCustomer(data.id,data.BPCode, position)
//                    listenerCustomer!!.onDataPassedCustomer(data)

                } else {
                    BranchCode = ""
                    ticketbiding.acBranch.setText("")
                    Prefs.putString(Global.SpinnerAddressType,"")
                    Prefs.putInt(Global.SpinnerBranchId,0)
//                    listenerCustomer!!.onDataPassedCustomer()
                }
            }

        }
    }

    private fun setData() {
//        ticketbiding.companyName.text = accountdata.CardName
        ticketbiding.heading.text = accountdata.CardName

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (accountdata.CardName.isNotEmpty()) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    accountdata.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            ticketbiding.nameIcon.setImageDrawable(drawable)
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.account_detail_menu, menu)
        val item = menu.findItem(R.id.edit)
        //    item.isVisible = true
        return super.onCreateOptionsMenu(menu)


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.onholdview, R.id.allView, R.id.open_view, R.id.overdueView -> {

/*
                val bundle = Bundle()
                bundle.putParcelableArrayList(Global.BPWiseticketlist, bpwiseticketlist)
                val intent = Intent(this, AccountActivity()::class.java)
                intent.putExtras(bundle)
                startActivity(intent)*/
            }
        }
    }


}
