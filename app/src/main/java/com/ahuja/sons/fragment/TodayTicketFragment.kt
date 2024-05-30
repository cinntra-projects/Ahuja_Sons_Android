package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.ahujaSonsClasses.adapter.OrderListAdapter
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.OrderRequestModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class TodayTicketFragment(val getstartDate: Calendar) : Fragment() {

    private lateinit var ticketbiding: CategoryseeAllFragmentBinding

    //  lateinit var adapter: TicketAdapter
    lateinit var ticketNewAdapter: OrderListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var pageno = 1
    var recallApi = true

    private lateinit var viewModel: MainViewModel
//    var AllitemsList = ArrayList<TicketDataModel>()
    var AllitemsListNew = ArrayList<AllOrderListResponseModel.Data>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)
        viewModel = (activity as MainActivity).viewModel

        if (Global.checkForInternet(requireContext()) && recallApi) {
            pageno = 1
            recallApi = true
            AllitemsListNew.clear()
            callTodayTicketApiList()

        }


        ticketbiding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(requireContext()) && recallApi) {

                    pageno++
                    callTodayTicketApiList()

                }

            }
        })

        return ticketbiding.root
    }


    companion object {
        private const val TAG = "TodayTicketFragment"
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: called")

        if (Global.checkForInternet(requireContext())) {
            AllitemsListNew.clear()
            recallApi = true
            pageno = 1

            callTodayTicketApiList()

        }
    }


    private fun callTodayTicketApiList() {
        ticketbiding.idPBLoading.visibility = View.VISIBLE
        ticketbiding.loadingback.visibility = View.VISIBLE
        ticketbiding.loadingView.start()

        var field = OrderRequestModel.Field(FromDate = Global.datetoSimpleString(getstartDate.time), ToDate = Global.datetoSimpleString(getstartDate.time), FinalStatus = "", CardCode = "", CardName = "",
            ShipToCode = "", FromAmount = "", ToAmount = "", U_MR_NO = "")
        var requestModel = OrderRequestModel(
            PageNo = pageno,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = "",
            field = field,
            maxItem = 10,
        )

        val call: Call<AllOrderListResponseModel> = ApiClient().service.callOrderListApi(requestModel)
        call.enqueue(object : Callback<AllOrderListResponseModel> {
            override fun onResponse(
                call: Call<AllOrderListResponseModel>,
                response: Response<AllOrderListResponseModel>
            ) {
                if (response.body()?.status == 200) {

                    if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                        AllitemsListNew.clear()
                        AllitemsListNew.addAll(response.body()!!.data)
                        setAdapter()
                        ticketbiding.nodatafound.visibility = View.VISIBLE

                    } else {

                        var valueList = response.body()!!.data

                        if (pageno == 1) {
                            AllitemsListNew.clear()
                            AllitemsListNew.addAll(valueList)
                        } else {
                            AllitemsListNew.addAll(valueList)
                        }

                        setAdapter()
                        ticketNewAdapter.notifyDataSetChanged()
                        ticketbiding.nodatafound.visibility = View.GONE
                        ticketbiding.ssPullRefresh.setRefreshing(false)

                        if (valueList.size < 10)
                            recallApi = false
                    }


                    ticketbiding.idPBLoading.visibility = View.GONE
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.loadingView.stop()


                }
                else if (response.body()!!.status == 201) {
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.loadingView.stop()
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                } else {
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.nodatafound.visibility = View.VISIBLE
                    ticketbiding.loadingView.stop()
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                }

            }

            override fun onFailure(call: Call<AllOrderListResponseModel>, t: Throwable) {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.ssPullRefresh.setRefreshing(false)

            }
        })
    }


    private fun setAdapter() {
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        ticketNewAdapter = OrderListAdapter(AllitemsListNew)
        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
        ticketbiding.productRecyclerView.adapter = ticketNewAdapter
        ticketNewAdapter.notifyDataSetChanged()
    }

}
