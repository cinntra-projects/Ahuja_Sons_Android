package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.ItemInOrderForDeliveryCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.SelectOrderForCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllItemListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderListModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderRequestModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.CreateDependencyRequestModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.ActivitySelectOrderForCreateDependencyBinding
import com.ahuja.sons.databinding.BottomSheetItemListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
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
    var AllitemsList = ArrayList<AllOrderListModel.Data>()
    var StatusType = ""
    var SearchText = ""
    var orderID = ""

    var isMultiOrderCardSelectEnabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectOrderForCreateDependencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderID = intent.getStringExtra("orderID")!!


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


        //todo confirm chip call--

        binding.chipAssign.setOnClickListener {

            val idArrayList = ArrayList<Int>()

            for (order in GlobalClasses.cartListForOrderRequest.values) {
                idArrayList.add(order.id.toInt())
            }

            val commaSeparatedIds = idArrayList.joinToString(separator = ",")

            val idStringList = commaSeparatedIds.split(",")

            val idArray = idStringList.map { it.toInt() }

            if (GlobalClasses.cartListForOrderRequest.isNotEmpty()) {

                callCreateDependencyApi(idArray)

            }else{

                Global.warningmessagetoast(this,"Please Select One")

            }

        }


    }


    //todo calling create dependency api here---
    private fun callCreateDependencyApi(ids : List<Int>) {
        binding.loadingback.visibility = View.VISIBLE
        binding.loadingView.start()

        var data = CreateDependencyRequestModel(Prefs.getString(Global.Employee_Code, ""), ids,  orderID)

        val call: Call<AllItemListResponseModel> = ApiClient().service.createDependency(data)
        call.enqueue(object : Callback<AllItemListResponseModel> {
            override fun onResponse(call: Call<AllItemListResponseModel>, response: Response<AllItemListResponseModel>) {

                if (response.body()?.status == 200) {

                    onBackPressed()
                    GlobalClasses.cartListForOrderRequest.clear()
                    Global.successmessagetoast(this@SelectOrderForCreateDependencyActivity, response.body()!!.message)

                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()


                } else if (response.body()!!.status == 201) {
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(this@SelectOrderForCreateDependencyActivity, response.body()!!.message)
                } else {
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(this@SelectOrderForCreateDependencyActivity, response.body()!!.message)
                }

            }

            override fun onFailure(call: Call<AllItemListResponseModel>, t: Throwable) {

                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()

            }
        })

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


        var field = AllOrderRequestModel.Field(CardCode = "", FromAmount = "", FromDate = "", MrNo = "", PoDateFrom = "", PoDateTo = "",
            ShipToCode = "", ToAmount = "", ToDate = "")
        var requestModel = AllOrderRequestModel(
            PageNo = pageno,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = SearchText,
            field = field,
            maxItem = maxItem,
        )

        val call: Call<AllOrderListModel> = ApiClient().service.callOrderListForDependency(requestModel)
        call.enqueue(object : Callback<AllOrderListModel> {
            override fun onResponse(
                call: Call<AllOrderListModel>,
                response: Response<AllOrderListModel>
            ) {
                if (response.body()?.status == 200) {

                    if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                        AllitemsList.clear()
                        AllitemsList.addAll(response.body()!!.data)
                        setAdapter()
                        binding.nodatafound.visibility = View.VISIBLE
                        binding.cardAssignButton.visibility = View.GONE

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

                    binding.cardAssignButton.visibility = View.VISIBLE


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

            override fun onFailure(call: Call<AllOrderListModel>, t: Throwable) {
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
        adapter = SelectOrderForCoordinatorAdapter(AllitemsList, RoleClass.orderCoordinator, isMultiOrderCardSelectEnabled)
        binding.productRecyclerView.layoutManager = linearLayoutManager
        binding.productRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener { data, pos ->

            showItemListDialogBottomSheetDialog(data, pos)

          /*  if (GlobalClasses.cartListForOrderRequest.isNotEmpty()){

            }else{
                Global.warningmessagetoast(this,"Please Select One")
            }*/

        }


    }


    //todo calling item all api here---
    private fun callAllItemListAPi(bindingBottomSheet: BottomSheetItemListBinding, data: AllOrderListModel.Data, pos: Int) {
        bindingBottomSheet.loadingView.start()
        bindingBottomSheet.loadingback.visibility = View.VISIBLE

        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", data.id)


        val call: Call<AllItemListResponseModel> = ApiClient().service.getAllItemListApi(jsonObject)
        call.enqueue(object : Callback<AllItemListResponseModel> {
            override fun onResponse(call: Call<AllItemListResponseModel>, response: Response<AllItemListResponseModel>) {
                if (response.body()?.status == 200) {

                    if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                        bindingBottomSheet.noDataFound.visibility = View.VISIBLE
                    } else {
                        var data = response.body()!!.data
                        var itemInOrderForDeliveryCoordinatorAdapter = ItemInOrderForDeliveryCoordinatorAdapter()
                        itemInOrderForDeliveryCoordinatorAdapter.submitList(data)
                        bindingBottomSheet.rvItemList.apply {
                            adapter = itemInOrderForDeliveryCoordinatorAdapter
                            layoutManager = LinearLayoutManager(this@SelectOrderForCreateDependencyActivity)
                            itemInOrderForDeliveryCoordinatorAdapter.notifyDataSetChanged()
                        }

                        bindingBottomSheet.noDataFound.visibility = View.GONE

                    }


                    bindingBottomSheet.loadingback.visibility = View.GONE
                    bindingBottomSheet.loadingView.stop()


                } else if (response.body()!!.status == 201) {
                    bindingBottomSheet.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(this@SelectOrderForCreateDependencyActivity, response.body()!!.message)
                } else {
                    bindingBottomSheet.loadingback.visibility = View.GONE
                    bindingBottomSheet.noDataFound.visibility = View.VISIBLE
                    Global.warningmessagetoast(this@SelectOrderForCreateDependencyActivity, response.body()!!.message)
                }

            }

            override fun onFailure(call: Call<AllItemListResponseModel>, t: Throwable) {
                bindingBottomSheet.loadingback.visibility = View.GONE
                bindingBottomSheet.loadingView.stop()

            }
        })


    }


    //todo bottom dialog open for item list--
    private fun showItemListDialogBottomSheetDialog(data: AllOrderListModel.Data, pos: Int) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetItemListBinding = BottomSheetItemListBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())

        bottomSheetDialog.show()


        bindingBottomSheet.tvOrderName.setText(data.CardName)
        bindingBottomSheet.tvOrderDoctorName.setText(data.Doctor[0].DoctorFirstName + " " + data.Doctor[0].DoctorLastName)
        bindingBottomSheet.tvOrderInfo.setText(data.OrderInformation)

        bindingBottomSheet.headingMore.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        callAllItemListAPi(bindingBottomSheet, data, pos)

//        itemInOrderForDeliveryCoordinatorAdapter.submitList(orders)

        bindingBottomSheet.btnConfirm.setOnClickListener {

            bottomSheetDialog.dismiss()
        }



    }



}