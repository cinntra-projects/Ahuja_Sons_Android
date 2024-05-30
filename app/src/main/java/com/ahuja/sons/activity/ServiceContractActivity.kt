package com.ahuja.sons.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ServiceContractListAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.ActivityServiceContractBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ServiceContractListRequest
import com.ahuja.sons.newapimodel.ServiceContractListResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ServiceContractActivity : AppCompatActivity() {

    lateinit var binding : ActivityServiceContractBinding
    var apicall = true
    var pageno = 1
    var maxItem = 10
    var searchTextValue = ""
    lateinit var layoutManager: LinearLayoutManager
    var isScrollingpage = false
    lateinit var serviceAdapter : ServiceContractListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceContractBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        if (Global.checkForInternet(this)){
            callServiceProductListApi(pageno, searchTextValue)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Global.checkForInternet(this@ServiceContractActivity)) {
                pageno = 1
                apicall = true
                searchTextValue = ""

                callServiceProductListApi(pageno, searchTextValue)
            } else {
                binding.swipeRefreshLayout.setRefreshing(false)
            }
        }


        //todo pagination for list..

        //todo pagination for list..
        binding.serviceContractRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (Global.checkForInternet(this@ServiceContractActivity) && apicall) {
                    if (isScrollingpage && lastCompletelyVisibleItemPosition == AllitemsList.size - 2 && apicall) {
                        pageno++
                        Log.e("page--->", pageno.toString())

                        callServiceProductListApi(pageno, searchTextValue)

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
        })


        binding.addServiceContractBtn.setOnClickListener {
            var intent = Intent(this@ServiceContractActivity, AddServiceContractActivty::class.java)
            startActivity(intent)
        }
    }

    private fun callServiceProductListApi(pageno: Int, searchTextValue: String) {
        binding.loadingback.visibility = View.VISIBLE
        binding.loadingview.start()

        var field = ServiceContractListRequest.Field(WS_FromDate = "", WS_ToDate = "", WE_FromDate = "", WE_ToDate = "", finalstatus = "", searchpriority = "", searchAssignTo = "")
        var data = ServiceContractListRequest(BranchId = "", CardCode = "", PageNo = pageno, SearchText = searchTextValue, field, maxItem = maxItem)

        val call: Call<ServiceContractListResponseModel> = ApiClient().service.getServiceProductList(data)
        call.enqueue(object : Callback<ServiceContractListResponseModel?> {
            override fun onResponse(call: Call<ServiceContractListResponseModel?>, response: Response<ServiceContractListResponseModel?>) {
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
                                serviceAdapter.notifyDataSetChanged()
                                binding.noDatafound.visibility = View.GONE
                                binding.swipeRefreshLayout.setRefreshing(false)

                                if (valueList.size < 10)
                                    apicall = false
                            }

                        }else{
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Log.e(TAG, "subscribeToObserverApiError: ${response.message()}")
                            Global.warningmessagetoast(this@ServiceContractActivity, response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ServiceContractListResponseModel?>, t: Throwable) {
                Global.errormessagetoast(this@ServiceContractActivity, t.message.toString())
            }
        })
    }

    companion object{
        private const val TAG = "ServiceContractActivity"
    }

    var AllitemsList : ArrayList<ServiceContractListResponseModel.DataXXX> = ArrayList()


    private fun setAdapter() {
        layoutManager = LinearLayoutManager(this@ServiceContractActivity, LinearLayoutManager.VERTICAL, false)
        binding.serviceContractRecyclerView.layoutManager = layoutManager
        serviceAdapter = ServiceContractListAdapter(AllitemsList)
        binding.serviceContractRecyclerView.adapter = serviceAdapter
        serviceAdapter.setOnItemClickListener { data, i ->

            var intent : Intent = Intent(this@ServiceContractActivity, ServiceContractDetailActivity::class.java)
            intent.putExtra("id", data.id)
            startActivity(intent)

        }

    }


    override fun onResume() {
        super.onResume()

        if (Global.checkForInternet(this@ServiceContractActivity)){
            pageno = 1
            apicall = true

            callServiceProductListApi(pageno, searchTextValue)

        }else{
            Global.warningmessagetoast(this@ServiceContractActivity, "Please Check Internet Connection")
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
        val searchView = SearchView((this@ServiceContractActivity).supportActionBar!!.themedContext)

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        item.actionView = searchView
        searchView.queryHint = "Search Here"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                pageno = 1
                searchTextValue = query
                if (query.isNotEmpty()){
                    callServiceProductListApi(pageno, searchTextValue)
                }

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (serviceAdapter != null) {
                    serviceAdapter.filter(newText)
                }
                return false
            }
        })

        return true
    }




}