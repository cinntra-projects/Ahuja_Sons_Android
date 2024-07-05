package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.ItemInOrderForDeliveryCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.OrderListForDeliveryCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.SelectOrderForCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.WorkQueueAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.fragments.route.OrderForDeliveryCoordinatorFragment
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.OrderRequestModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.ActivitySelectOrderForCreateDependencyBinding
import com.ahuja.sons.databinding.BottomSheetItemListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectOrderForCreateDependencyActivity : AppCompatActivity() {
    lateinit var binding: ActivitySelectOrderForCreateDependencyBinding
    lateinit var adapter: SelectOrderForCoordinatorAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var pageno = 1
    var isScrollingpage: Boolean = false
    var maxItem = 10
    var recallApi = true
    var deleteicon = R.drawable.ic_baseline_delete_24
    var isOtherType = false
    lateinit var viewModel: MainViewModel
    var servciceID = ""
    var AllitemsList = ArrayList<AllOrderListResponseModel.Data>()
    var StatusType = ""
    var SearchText = ""


    var isMultiOrderCardSelectEnabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectOrderForCreateDependencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cardAssignButton!!!!.visibility = View.VISIBLE

      /*  binding.tvSelect.setOnClickListener {
            //   Log.e(OrderForDeliveryCoordinatorFragment.TAG, "onViewCreated: text")

            if (binding.cardAssignButton!!.visibility == View.GONE) {
                binding.cardAssignButton!!!!.visibility = View.VISIBLE
                isMultiOrderCardSelectEnabled = true
                adapter.isUpdated(isMultiOrderCardSelectEnabled)
            } else {
                binding.cardAssignButton!!!!.visibility = View.GONE
                isMultiOrderCardSelectEnabled = false
                adapter.isUpdated(isMultiOrderCardSelectEnabled)
            }
            adapter.notifyDataSetChanged()


        }*/

        binding.ssPullRefresh.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                binding.searchView.clearFocus()
                binding.searchView.visibility = View.GONE

                if (Global.checkForInternet(this@SelectOrderForCreateDependencyActivity)) {
                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = ""

                    callAllOrderListApi(pageno, SearchText)
                    binding.loadingView.start()
                }
            }
        })



        binding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(this) && recallApi) {
                    pageno++

                    if (binding.searchView.isVisible) {
                        SearchText = binding.searchView.query.toString()
                        callAllOrderListApi(pageno, SearchText)
                    } else {
                        SearchText = ""
                        callAllOrderListApi(pageno, SearchText)
                    }

                }
            }


        })





        binding.search.setOnClickListener {
            if (binding.searchView.isVisible) {
                binding.searchView.visibility = View.GONE
            } else {
                binding.searchView.visibility = View.VISIBLE
            }
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {


                if (query != null && query.toString().isNotEmpty()) {

                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = query
                    binding.loadingView.start()

                    callAllOrderListApi(pageno, SearchText)

                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty() && newText != "") {
                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = newText
                    binding.loadingView.start()

                    callAllOrderListApi(pageno, SearchText)

                } else {

                }
                return false
            }

        })


        binding.chipAssign.setOnClickListener {

            if (GlobalClasses.cartListForOrderRequest.isNotEmpty()){
                showItemListDialogBottomSheetDialog()
            }else{
                Global.warningmessagetoast(this,"Please Select One")
            }

        }


    }

    private fun showItemListDialogBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetItemListBinding =
            BottomSheetItemListBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())

        bottomSheetDialog.show()

        bindingBottomSheet.headingMore.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        var itemInOrderForDeliveryCoordinatorAdapter = ItemInOrderForDeliveryCoordinatorAdapter()

        // Sample data
        val orders = List(5) { index ->
            LocalWorkQueueData(
                id = index.toString(),
                date = "2024-05-30",
                time = "12:00 PM",
                orderName = "Order #$index",
                doctor = "Doctor #$index",
                status = "Pending",
                omsID = "OMSID#$index"
            )
        }

        itemInOrderForDeliveryCoordinatorAdapter.submitList(orders)

        bindingBottomSheet.btnConfirm.setOnClickListener {
            binding.apply {
                /*    linearSelectOrder.visibility = View.GONE
                    linearCreateDependencyEarrands.visibility = View.VISIBLE*/
                finish()
            }
            bottomSheetDialog.dismiss()
        }
        bindingBottomSheet.rvItemList.apply {
            adapter = itemInOrderForDeliveryCoordinatorAdapter
            layoutManager = LinearLayoutManager(this@SelectOrderForCreateDependencyActivity)
            itemInOrderForDeliveryCoordinatorAdapter.notifyDataSetChanged()
        }


    }


    override fun onResume() {
        super.onResume()
        Log.e("OrderForDeliveryCoordinatorFragment.TAG", "onResume: ")
        if (Global.checkForInternet(this)) {
            pageno = 1
            recallApi = true
            AllitemsList.clear()
            binding.loadingView.start()
            SearchText = ""
            binding.loadingback.visibility = View.VISIBLE
            callAllOrderListApi(pageno, SearchText)

        }

        binding.searchView.clearFocus()
        binding.searchView.visibility = View.GONE
        // binding.all.text = "All"
        // binding.all.visibility = View.GONE
    }


    private fun callAllOrderListApi(pageno: Int, SearchText: String) {
        binding.loadingView.start()
        binding.loadingback.visibility = View.VISIBLE

        var field = OrderRequestModel.Field(
            FromDate = "", ToDate = "", FinalStatus = "", CardCode = "", CardName = "",
            ShipToCode = "", FromAmount = "", ToAmount = "", U_MR_NO = ""
        )

        var requestModel = OrderRequestModel(
            PageNo = pageno,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = SearchText,
            field = field,
            maxItem = "10",//maxItem
        )

        val call: Call<AllOrderListResponseModel> =
            ApiClient().service.callOrderListApi(requestModel)
        call.enqueue(object : Callback<AllOrderListResponseModel> {
            override fun onResponse(
                call: Call<AllOrderListResponseModel>,
                response: Response<AllOrderListResponseModel>
            ) {
                if (response.body()?.status == 200) {

                    if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                        AllitemsList.clear()
                        AllitemsList.addAll(response.body()!!.data)
                        setAdapter()
                        binding.nodatafound.visibility = View.VISIBLE

                    } else {

                        var valueList = response.body()!!.data

                        if (pageno == 1) {
                            AllitemsList.clear()
                            AllitemsList.addAll(valueList)
                        } else {
                            AllitemsList.addAll(valueList)
                        }

                        setAdapter()
                        adapter.notifyDataSetChanged()
                        binding.nodatafound.visibility = View.GONE
                        binding.ssPullRefresh.setRefreshing(false)

                        if (valueList.size < 10)
                            recallApi = false
                    }


                    binding.idPBLoading.visibility = View.GONE
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()


                } else if (response.body()!!.status == 201) {
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(
                        this@SelectOrderForCreateDependencyActivity,
                        response.body()!!.message
                    )
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.nodatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(
                        this@SelectOrderForCreateDependencyActivity,
                        response.body()!!.message
                    )
                }

            }

            override fun onFailure(call: Call<AllOrderListResponseModel>, t: Throwable) {
//                Global.errormessagetoast(requireContext(),t.message.toString())
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                binding.idPBLoading.visibility = View.GONE
                binding.ssPullRefresh.setRefreshing(false)

            }
        })
    }

    private fun setAdapter() {
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = SelectOrderForCoordinatorAdapter(
            AllitemsList,
            RoleClass.orderCoordinator,
            isMultiOrderCardSelectEnabled
        )
        binding.productRecyclerView.layoutManager = linearLayoutManager
        binding.productRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()


    }
}