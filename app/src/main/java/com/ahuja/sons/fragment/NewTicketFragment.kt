package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.ahujaSonsClasses.adapter.OrderListAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.OrderRequestModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.globals.Global
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewTicketFragment : Fragment() {

    private lateinit var ticketbiding: CategoryseeAllFragmentBinding
    lateinit var adapter: OrderListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var pageno = 1
    var recallApi = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)

        if (Global.checkForInternet(requireContext()) && recallApi) {
            pageno = 1
            recallApi = true
            ticketbiding.loadingView.start()
            AllitemsList.clear()
            callPastTicketApiList()

        }


        ticketbiding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(requireContext()) && recallApi) {
                    pageno++
                    ticketbiding.idPBLoading.visibility = View.VISIBLE
                    callPastTicketApiList()
                }

            }
        })//todo

        return ticketbiding.root
    }

    var AllitemsList = ArrayList<AllOrderListResponseModel.Data>()

    private fun callPastTicketApiList() {
        ticketbiding.idPBLoading.visibility = View.VISIBLE
        ticketbiding.loadingback.visibility = View.VISIBLE
        ticketbiding.loadingView.start()

        var field = OrderRequestModel.Field(FromDate = "", ToDate = "", FinalStatus = "", CardCode = "", CardName = "",
            ShipToCode = "", FromAmount = "", ToAmount = "", U_MR_NO = "")
        var requestModel = OrderRequestModel(
            PageNo = pageno,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = "",
            field = field,
            maxItem = "10",
        )

        val call: Call<AllOrderListResponseModel> = ApiClient().service.callOrderListApi(requestModel)
        call.enqueue(object : Callback<AllOrderListResponseModel> {
            override fun onResponse(
                call: Call<AllOrderListResponseModel>,
                response: Response<AllOrderListResponseModel>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.data != null) {
                        recallApi = response.body()!!.data.isNotEmpty()
                        AllitemsList.addAll(response.body()!!.data)
                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = OrderListAdapter(AllitemsList,RoleClass.ticket)
                        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                        ticketbiding.productRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()

                        if (adapter.itemCount == 0) {
                            ticketbiding.nodatafound.isVisible = true
                        }
                        Log.e("data", response.body()?.data.toString())
                    }


                }
                else {
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                }
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.loadingView.stop()

            }

            override fun onFailure(call: Call<AllOrderListResponseModel>, t: Throwable) {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.ssPullRefresh.setRefreshing(false)

            }
        })
    }


}
