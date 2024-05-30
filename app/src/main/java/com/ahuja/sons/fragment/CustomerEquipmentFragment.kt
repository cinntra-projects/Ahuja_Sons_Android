package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simform.refresh.SSPullToRefreshLayout
import com.ahuja.sons.activity.AccountDetailActivity
import com.ahuja.sons.adapter.CustomerEquipmentAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.ProductResponseModel
import com.ahuja.sons.newapimodel.ServiceContractListRequest
import com.ahuja.sons.viewmodel.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerEquipmentFragment(val accountdata: AccountBpData) : Fragment(), AccountDetailActivity.MyFragmentCustomerListener {

    private lateinit var ticketbiding : CategoryseeAllFragmentBinding
    lateinit var adapter: CustomerEquipmentAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel: MainViewModel
    var apicall = true
    var pageno = 1
    var maxItem = 10
    var searchTextValue = ""
    var isScrollingpage = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)

        viewModel = (activity as AccountDetailActivity).viewModel

        ticketbiding.loadingView.start()

        if (Global.checkForInternet(requireContext())) {
           /* val data = AccountBpData(CardCode = accountdata.CardCode)
            viewModel.getAccountitemList(data)
            bindObserver()*/
            callEquipmentList(pageno, searchTextValue)
            ticketbiding.loadingView.start()
        }

        ticketbiding.ssPullRefresh.setOnRefreshListener(object : SSPullToRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                if (Global.checkForInternet(requireContext())) {
                    pageno = 1
                    apicall = true
                    searchTextValue = ""

                    callEquipmentList(pageno, searchTextValue)
                } else {
                    ticketbiding.ssPullRefresh.setRefreshing(false)
                }
            }

        })


        //todo pagination for list..
        ticketbiding.productRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastCompletelyVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()

                if (Global.checkForInternet(requireContext()) && apicall) {
                    if (isScrollingpage && lastCompletelyVisibleItemPosition == AllitemsList.size - 2 && apicall) {
                        pageno++
                        Log.e("page--->", pageno.toString())

                        callEquipmentList(pageno, searchTextValue)

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


        return ticketbiding.root
    }


    private fun callEquipmentList(pageno: Int, searchTextValue: String) {
        ticketbiding.loadingback.visibility = View.VISIBLE
        ticketbiding.loadingView.start()

        var field = ServiceContractListRequest.Field(WS_FromDate = "", WS_ToDate = "", WE_FromDate = "", WE_ToDate = "", finalstatus = "", searchpriority = "", searchAssignTo = "")
        var data = ServiceContractListRequest(BranchId = "", CardCode = accountdata.CardCode, PageNo = pageno, SearchText = searchTextValue, field, maxItem = maxItem)

        val call: Call<ProductResponseModel> = ApiClient().service.getProductAllList(data)
        call.enqueue(object : Callback<ProductResponseModel?> {
            override fun onResponse(call: Call<ProductResponseModel?>, response: Response<ProductResponseModel?>) {
                try {
                    if (response.isSuccessful){

                        if (response.body()!!.status == 200) {

                            Log.e(TAG, "subscribeToObserver: ")
                            ticketbiding.loadingback.visibility = View.GONE
                            ticketbiding.loadingView.stop()
                            if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                                AllitemsList.clear()
                                AllitemsList.addAll(response.body()!!.data)
                             //   setAdapter()
                                linearLayoutManager = LinearLayoutManager(requireContext())
                                adapter = CustomerEquipmentAdapter(AllitemsList)
                                ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                                ticketbiding.productRecyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()
                                ticketbiding.nodatafound.visibility = View.VISIBLE

                            } else {
                                var valueList = response.body()!!.data

                                if (pageno == 1){
                                    AllitemsList.clear()
                                    AllitemsList.addAll(valueList)
                                }else{
                                    AllitemsList.addAll(valueList)
                                }
                                linearLayoutManager = LinearLayoutManager(requireContext())
                                adapter = CustomerEquipmentAdapter(AllitemsList)
                                ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                                ticketbiding.productRecyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()

                                //setAdapter()
                                adapter.notifyDataSetChanged()
                                ticketbiding.nodatafound.visibility = View.GONE
                                ticketbiding.ssPullRefresh.setRefreshing(false)

                                if (valueList.size < 10)
                                    apicall = false
                            }

                        }else{
                            ticketbiding.loadingback.visibility = View.GONE
                            ticketbiding.loadingView.stop()
                            Log.e("TAG===>", "subscribeToObserverApiError: ${response.message()}")
                            Global.warningmessagetoast(requireContext(), response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.loadingView.stop()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ProductResponseModel?>, t: Throwable) {
                Global.errormessagetoast(requireContext(), t.message.toString())
            }
        })
    }


    fun setAdapter(){
        linearLayoutManager = LinearLayoutManager(requireContext())
        adapter = CustomerEquipmentAdapter(AllitemsList)
        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
        ticketbiding.productRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    var AllitemsList= ArrayList<ProductResponseModel.DataXXX>()
    //todo bind observer...
   /* private fun bindObserver(){
        viewModel.equipmentList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(requireContext(), it)
                ticketbiding.loadingView.stop()
            },
            onLoading = {
                ticketbiding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200){
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.loadingView.stop()

                    if (response.data != null){
                        AllitemsList.clear()
                        AllitemsList.addAll(response.data)
                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                        adapter = CustomerEquipmentAdapter(AllitemsList)
                        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                        ticketbiding.productRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()

                        if(adapter.itemCount==0){
                            ticketbiding.nodatafound.visibility = View.VISIBLE
                        }else{
                            ticketbiding.nodatafound.visibility = View.GONE

                        }

                        Log.e("data",response.data.toString())
                    }
                }else {
                    Global.warningmessagetoast(requireContext(), response.message)
                }
            }

        ))


    }*/


  /*  override fun onDataPassedCustomer(data: BranchAllListResponseModel.DataXXX) {
        Log.e("TAG", "onDataPassedCustomer: ", )
    }*/

    override fun onDataPassedCustomer(startDate: String?, endDate: String?, pos: Int?) {
        Log.e("TAG", "onDataPassedCustomer: ", )
    }


    companion object{
        private const val TAG = "CustomerEquipmentFragme"
    }

}
