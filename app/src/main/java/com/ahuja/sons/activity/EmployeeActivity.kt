package com.ahuja.sons.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.EmployeeListAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityEmployeeBinding
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
import java.util.ArrayList

class EmployeeActivity : AppCompatActivity() {
    lateinit var binding : ActivityEmployeeBinding
    var apicall = true
    var pageno = 1
    var maxItem = 10
    var searchTextValue = ""
    lateinit var layoutManager: LinearLayoutManager
    var isScrollingpage = false
    lateinit var employeeListAdapter : EmployeeListAdapter
    var AllitemsList : ArrayList<EmployeeResponseModel.DataXXX> = ArrayList()
    lateinit var viewModel: MainViewModel

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    companion object{
        private const val TAG = "EmployeeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        if (Global.checkForInternet(this)){
            callEmployeeListApi(pageno, searchTextValue)
        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Global.checkForInternet(this@EmployeeActivity)) {
                pageno = 1
                apicall = true
                searchTextValue = ""

                callEmployeeListApi(pageno, searchTextValue)
            } else {
                binding.swipeRefreshLayout.setRefreshing(false)
            }
        }


        binding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(this@EmployeeActivity) && apicall) {
                    pageno++
                    Log.e("page--->", pageno.toString())

                    callEmployeeListApi(pageno, searchTextValue)

                    isScrollingpage = false
                }
            }

        })


    /*    binding.employeeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (Global.checkForInternet(this@EmployeeActivity) && apicall) {
                    if (isScrollingpage && lastCompletelyVisibleItemPosition == AllitemsList.size - 2 && apicall) {
                        pageno++
                        Log.e("page--->", pageno.toString())

                        callEmployeeListApi(pageno, searchTextValue)

                        isScrollingpage = false
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //it means we are scrolling
                    isScrollingpage = true
                }
            }
        })*/

        if (Prefs.getString(Global.Employee_role, "") == "admin"){
            binding.addEmployeeBtn.visibility = View.VISIBLE
        }else{
            binding.addEmployeeBtn.visibility = View.GONE
        }

        binding.addEmployeeBtn.setOnClickListener {
            var intent = Intent(this@EmployeeActivity, AddEmployeeActivity::class.java)
            startActivity(intent)
        }

    }


    private fun callEmployeeListApi(pageno: Int, searchTextValue: String) {
        binding.loadingback.visibility = View.VISIBLE
        binding.loadingview.start()

        var field = EmployeeRequestModel.Field(FinalStatus = "")
        var data = EmployeeRequestModel(PageNo = pageno, SalesPersonCode = Prefs.getString(Global.Employee_Code), SearchText = searchTextValue, field, maxItem = maxItem)

        val call: Call<EmployeeResponseModel> = ApiClient().service.getEmployeeList(data)
        call.enqueue(object : Callback<EmployeeResponseModel?> {
            override fun onResponse(call: Call<EmployeeResponseModel?>, response: Response<EmployeeResponseModel?>) {
                try {
                    if (response.isSuccessful){

                        if (response.body()!!.status == 200) {

                            Log.e(TAG, "subscribeToObserver: ")
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                                AllitemsList.clear()
                                AllitemsList.addAll(response.body()!!.data)
                                setAdapter()
                                binding.noDatafound.visibility = View.VISIBLE

                            } else {
                                var valueList = response.body()!!.data

                                if (pageno == 1){
                                    AllitemsList.clear()
                                    AllitemsList.addAll(valueList)
                                }else{
                                    AllitemsList.addAll(valueList)
                                }

                                setAdapter()
                                employeeListAdapter.notifyDataSetChanged()
                                binding.noDatafound.visibility = View.GONE
                                binding.swipeRefreshLayout.setRefreshing(false)

                                if (valueList.size < 10)
                                    apicall = false
                            }

                        }else{
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Log.e(TAG, "subscribeToObserverApiError: ${response.message()}")
                            Global.warningmessagetoast(this@EmployeeActivity, response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<EmployeeResponseModel?>, t: Throwable) {
                Global.errormessagetoast(this@EmployeeActivity, t.message.toString())
            }
        })
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(this@EmployeeActivity, LinearLayoutManager.VERTICAL, false)
        binding.employeeRecyclerView.layoutManager = layoutManager
        employeeListAdapter = EmployeeListAdapter(AllitemsList)
        binding.employeeRecyclerView.adapter = employeeListAdapter

        employeeListAdapter.setOnItemClickListener { data, i ->
            var intent : Intent = Intent(this@EmployeeActivity, EmployeeDetailActivity::class.java)
            intent.putExtra("id", data.id)
            startActivity(intent)

        }

        employeeListAdapter.setOnResetBtnClickListener {dataModel, password , dialog->

            var activeValue = ""
            if (dataModel.Active == "tYES"){
                activeValue = "tYES"
            }
            else if (dataModel.Active == "tNO"){
                activeValue = "tNO"
            }

            var modelData = EmployeeCreateRequestModel(
                Active = activeValue,
                Email = dataModel.Email,
                EmployeeID = dataModel.EmployeeID,
                Mobile = dataModel.Mobile,
                SalesEmployeeCode = dataModel.SalesEmployeeCode,
                SalesEmployeeName = dataModel.SalesEmployeeName,
                branch = dataModel.branch,
                companyID = dataModel.companyID,
                div = dataModel.div,
                firstName = dataModel.firstName,
                id = dataModel.id,
                lastLoginOn = dataModel.lastLoginOn,
                lastName = dataModel.lastName,
                middleName = dataModel.middleName,
                password = password,
                passwordUpdatedOn = dataModel.passwordUpdatedOn,
                position = dataModel.position,
                reportingTo = dataModel.reportingTo,
                role = dataModel.role,
                salesUnit = dataModel.salesUnit,
                subdep = dataModel.subdep.toString(),
                timestamp = dataModel.timestamp,
                userName = dataModel.userName,
                zone = dataModel.zone
            )

            val gson = Gson()
            val jsonTut: String = gson.toJson(modelData)
            Log.e("data", jsonTut)
            if (Global.checkForInternet(this)) {
                viewModel.updateEmployee(modelData)
                bindCreateEmployeeObserver(dialog)
            }
        }
        

    }

    //todo reset password and override fucn--
    private fun bindCreateEmployeeObserver(dialog: AlertDialog) {
        viewModel.productOneDetailData.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@EmployeeActivity, it)
                }, onLoading = {
                    binding.loadingback.visibility = View.VISIBLE
                    binding.loadingview.start()
                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Global.successmessagetoast(this@EmployeeActivity, "Successfully Password Change")
                            dialog.dismiss()

                        } else {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@EmployeeActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        binding.loadingback.visibility = View.GONE
                        binding.loadingview.stop()
                        e.printStackTrace()
                    }

                }
            ))
    }


    override fun onResume() {
        super.onResume()

        if (Global.checkForInternet(this@EmployeeActivity)){
            pageno = 1
            apicall = true

            callEmployeeListApi(pageno, searchTextValue)

        }else{
            Global.warningmessagetoast(this@EmployeeActivity, "Please Check Internet Connection")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {

            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.service_contract_menu, menu)

        val item = menu!!.findItem(R.id.search)
        val searchView = SearchView((this@EmployeeActivity).supportActionBar!!.themedContext)

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        item.actionView = searchView
        searchView.queryHint = "Search Here"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                pageno = 1
                searchTextValue = query
                if (query.isNotEmpty()){
                    callEmployeeListApi(pageno, searchTextValue)
                }

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (employeeListAdapter != null) {
                    employeeListAdapter.filter(newText)
                }
                return false
            }
        })

        return true
    }


    
}