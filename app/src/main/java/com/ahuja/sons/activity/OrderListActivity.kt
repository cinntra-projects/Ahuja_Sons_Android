package com.ahuja.sons.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.OrderListAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.ActivityOrderListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.OrderListRequestModel
import com.ahuja.sons.newapimodel.OrderListResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class OrderListActivity : AppCompatActivity() {
    lateinit var binding : ActivityOrderListBinding

    var apicall = true
    var pageno = 1
    var maxItem = 10
    var searchTextValue = ""
    var finalStatus = ""
    lateinit var layoutManager: LinearLayoutManager
    var isScrollingpage = false
    lateinit var orderListAdapter : OrderListAdapter
    var AllitemsList : ArrayList<OrderListResponseModel.DataXXX> = ArrayList()
    
    companion object{
        private const val TAG = "OrderListActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        if (Global.checkForInternet(this)){
            callOrderList(pageno, searchTextValue, finalStatus)
        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            if (Global.checkForInternet(this@OrderListActivity)) {
                pageno = 1
                apicall = true
                searchTextValue = ""
                finalStatus = ""

                callOrderList(pageno, searchTextValue, finalStatus)
            } else {
                binding.swipeRefreshLayout.setRefreshing(false)
            }
        }
        

        binding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(this@OrderListActivity) && apicall) {
                    pageno++
                    Log.e("page--->", pageno.toString())

                    callOrderList(pageno, searchTextValue, finalStatus)

                    isScrollingpage = false
                }
            }

        })


    }


    private fun callOrderList(pageno: Int, searchTextValue: String, finalStatus: String) {
        binding.loadingback.visibility = View.VISIBLE
        binding.loadingview.start()

        var field = OrderListRequestModel.Field(FinalStatus = finalStatus)
        var data = OrderListRequestModel(PageNo = pageno, SalesPersonCode = Prefs.getString(Global.Employee_Code), SearchText = searchTextValue, field, maxItem = maxItem)

        val call: Call<OrderListResponseModel> = ApiClient().service.getOrderList(data)
        call.enqueue(object : Callback<OrderListResponseModel?> {
            override fun onResponse(call: Call<OrderListResponseModel?>, response: Response<OrderListResponseModel?>) {
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
                                orderListAdapter.notifyDataSetChanged()
                                binding.noDatafound.visibility = View.GONE
                                binding.swipeRefreshLayout.setRefreshing(false)

                                if (valueList.size < 10)
                                    apicall = false
                            }

                        }else{
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Log.e(TAG, "subscribeToObserverApiError: ${response.message()}")
                            Global.warningmessagetoast(this@OrderListActivity, response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<OrderListResponseModel?>, t: Throwable) {
                Global.errormessagetoast(this@OrderListActivity, t.message.toString())
            }
        })
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(this@OrderListActivity, LinearLayoutManager.VERTICAL, false)
        binding.orderRecyclerView.layoutManager = layoutManager
        orderListAdapter = OrderListAdapter(AllitemsList)
        binding.orderRecyclerView.adapter = orderListAdapter

        orderListAdapter.setOnItemClickListener { data, i ->
            var intent : Intent = Intent(this@OrderListActivity, OrderDetailActivity::class.java)
            intent.putExtra("id", data.id)
            startActivity(intent)

        }

    }
    
    

    override fun onResume() {
        super.onResume()

        if (Global.checkForInternet(this@OrderListActivity)){
            pageno = 1
            apicall = true

            callOrderList(pageno, searchTextValue, finalStatus)

        }else{
            Global.warningmessagetoast(this@OrderListActivity, "Please Check Internet Connection")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {

            }
            R.id.order_all ->{
                pageno = 1
                finalStatus = ""
                callOrderList(pageno, searchTextValue, finalStatus)
            }
            R.id.pending ->{
                pageno = 1
                finalStatus = "Pending"
                callOrderList(pageno, searchTextValue, finalStatus)
            }
            R.id.approved ->{
                pageno = 1
                finalStatus = "Approved"
                callOrderList(pageno, searchTextValue, finalStatus)
            }
            R.id.reject ->{
                pageno = 1
                finalStatus = "Rejected"
                callOrderList(pageno, searchTextValue, finalStatus)
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_menu, menu)

        val item = menu!!.findItem(R.id.search)
        val searchView = SearchView((this@OrderListActivity).supportActionBar!!.themedContext)

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        item.actionView = searchView
        searchView.queryHint = "Search Here"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                pageno = 1
                searchTextValue = query
                if (query.isNotEmpty()){
                    callOrderList(pageno, searchTextValue, finalStatus)
                }

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (orderListAdapter != null) {
                    orderListAdapter.filter(newText)
                }
                return false
            }
        })

        return true
    }
    
    

}