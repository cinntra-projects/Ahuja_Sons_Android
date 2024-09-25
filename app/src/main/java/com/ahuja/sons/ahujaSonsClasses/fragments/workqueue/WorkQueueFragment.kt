package com.ahuja.sons.ahujaSonsClasses.fragments.workqueue

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.Interface.OnDialogClickListener
import com.ahuja.sons.ahujaSonsClasses.activity.AddSalesOrderActivity
import com.ahuja.sons.ahujaSonsClasses.activity.AhujaSonsMainActivity
import com.ahuja.sons.ahujaSonsClasses.adapter.WorkQueueAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.fragments.route.OrderForDeliveryCoordinatorFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.route.RouteFragment
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.WorkQueueRequestModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.FragmentWorkQueueBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WorkQueueFragment : Fragment() , OnDialogClickListener{
    lateinit var binding: FragmentWorkQueueBinding
    lateinit var workQueueAdapter : WorkQueueAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var pageno = 1
    var isScrollingpage: Boolean = false
    var maxItem = 10
    var recallApi = true
    var AllitemsList = ArrayList<AllWorkQueueResponseModel.Data>()
    var StatusType = ""
    var SearchText = ""
    lateinit var viewModel: MainViewModel


    lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWorkQueueBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "WorkQueueFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as AhujaSonsMainActivity).viewModel

        if (Prefs.getString(Global.Employee_role,"").equals("Delivery Coordinator")){
            showViewForDeliveryCoordinatorRole()
        }else{
            //todo hide and show view
            showViewForOtherRole()

        }


        //todo viewpager for delivery coordinator

        pagerAdapter = ViewPagerAdapter(childFragmentManager)
        pagerAdapter.add(OrderForDeliveryCoordinatorFragment(binding.tvCreateRoute, binding.ivCollapseCart,binding.searchBtn), "Order")
//        pagerAdapter.add(SelectAllCheckBoxFragment(binding.tvCreateRoute), "Order")
        pagerAdapter.add(RouteFragment(), "Route")
        binding.viewpager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)


        binding.ivCollapseCart.visibility = View.VISIBLE


        //todo add order visiility--

        if (Prefs.getString(Global.Employee_role, "").equals("Sales Person")) {
            binding.fabWorkQueue.visibility = View.VISIBLE
        }else{
            binding.fabWorkQueue.visibility = View.GONE
        }


        binding.ssPullRefresh.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                binding.searchView.clearFocus()
                binding.searchView.visibility = View.GONE

                if(Prefs.getString(Global.Employee_role, "").equals("Sales Person")) {
                    binding.fabWorkQueue.visibility = View.VISIBLE
                }else{
                    binding.fabWorkQueue.visibility = View.GONE
                }


                if (Global.checkForInternet(requireContext())) {
                    pageno = 1
                    recallApi = true
//                    AllitemsList.clear()
                    SearchText = ""

                    callWorkQueueList(pageno, SearchText, fromDate, toDate)
                    binding.loadingView.start()
                }
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
           /* binding.apply {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.INVISIBLE
                rvWorkQue.visibility = View.VISIBLE
            }*///todo shimmer work here


        }

        binding.fabWorkQueue!!.setOnClickListener {
            Intent(requireActivity(), AddSalesOrderActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.searchBtn.setOnClickListener {
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
//                    AllitemsList.clear()
                    SearchText = query
                    binding.loadingView.start()
                    binding.loadingback.visibility = View.VISIBLE
                    callWorkQueueList(pageno, SearchText, fromDate, toDate)

                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty() && newText != "") {
                    pageno = 1
                    recallApi = true
                    SearchText = newText
                    binding.loadingView.start()
                    binding.loadingback.visibility = View.VISIBLE
                    callWorkQueueList(pageno, SearchText, fromDate, toDate)

                } else {

                }
                return false
            }

        })


        binding.ivCollapseCart.setOnClickListener {
            showFilterPopup()
        }


        //todo recycler view scrollListener for add more items in list...
        binding.rvWorkQue.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var lastCompletelyVisibleItemPosition = (linearLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (isScrollingpage && lastCompletelyVisibleItemPosition == AllitemsList.size - 2 && recallApi) {
                    pageno++
                    Log.e("page--->", pageno.toString())
                    callWorkQueueList(pageno, SearchText, fromDate, toDate)
                    isScrollingpage = false
                } else {
                    recyclerView.setPadding(0, 0, 0, 0);
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //it means we are scrolling
                    isScrollingpage = true

                }
            }
        })



    }

    var fromDate = ""
    var toDate = ""
    private fun showFilterPopup() {

        val dialog = Dialog(requireContext())
        val layoutInflater = LayoutInflater.from(requireActivity())
        val customDialog = layoutInflater.inflate(R.layout.show_filter_layout, null)
        dialog.setContentView(customDialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val resetBtn = dialog.findViewById<MaterialButton>(R.id.resetBtn)
        val applyBtn = dialog.findViewById<MaterialButton>(R.id.applyBtn)
        val ivCrossIcon = dialog.findViewById<ImageView>(R.id.ivCrossIcon)
        val edtFromDate = dialog.findViewById<TextInputEditText>(R.id.edtFromDate)
        val edtToDate = dialog.findViewById<TextInputEditText>(R.id.edtToDate)


        ivCrossIcon.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        edtFromDate.setOnClickListener {
            Global.disableFutureDates(requireContext(), edtFromDate)
        }

        edtToDate.setOnClickListener {
            Global.enableAllCalenderDateSelect(requireContext(), edtToDate)
        }


        resetBtn.setOnClickListener {
            edtFromDate.setText("")
            edtToDate.setText("")

        }

        applyBtn.setOnClickListener {
            fromDate = edtFromDate.text.toString()
            toDate = edtToDate.text.toString()

            if (fromDate.isNotEmpty()){
                fromDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(fromDate)
            }else{
                fromDate = ""
            }

            if (toDate.isNotEmpty()){
                toDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(toDate)
            }else{
                toDate = ""
            }

            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE
            callWorkQueueList(pageno, SearchText, fromDate, toDate)

            dialog.dismiss()
        }


        dialog.show()

    }


    override fun onButtonClick() {
        pagerAdapter = ViewPagerAdapter(childFragmentManager)
        pagerAdapter.add(OrderForDeliveryCoordinatorFragment(binding.tvCreateRoute, binding.ivCollapseCart,binding.searchBtn), "Order")
        pagerAdapter.add(RouteFragment(), "Route")
        binding.viewpager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }


    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        if (Global.checkForInternet(requireContext())) {
            pageno = 1
            recallApi = true
//            AllitemsList.clear()
            SearchText = ""
            binding.searchView.clearFocus()
            binding.searchView.visibility = View.GONE
            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE
            callWorkQueueList(pageno, SearchText, fromDate, toDate)

        }

        //todo add order visiility--

        if(Prefs.getString(Global.Employee_role, "").equals("Sales Person")) {
            binding.fabWorkQueue.visibility = View.VISIBLE
        }else{
            binding.fabWorkQueue.visibility = View.GONE
        }

        if(Prefs.getString(Global.Employee_role, "").equals("Operation Manger")) {
            binding.ivCollapseCart.visibility = View.GONE
        }else{
            binding.ivCollapseCart.visibility = View.VISIBLE
        }

        pagerAdapter = ViewPagerAdapter(childFragmentManager)
        pagerAdapter.add(OrderForDeliveryCoordinatorFragment(binding.tvCreateRoute, binding.ivCollapseCart,binding.searchBtn), "Order")
        pagerAdapter.add(RouteFragment(), "Route")
        binding.viewpager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)

    }


    //todo calling list api here---
    private fun callWorkQueueList(pageno: Int, searchText: String, fromDate: String, toDate: String) {

        var field = WorkQueueRequestModel.Field(CardCode = "", CardName = "", FromDate = fromDate, FinalStatus = "", ToDate = toDate)
        var requestModel = WorkQueueRequestModel(
            PageNo = pageno,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = searchText,
            field = field,
            maxItem = maxItem,
            role_id = Prefs.getString(Global.MyID, "")
        )

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.callAllWorkQueueApi(requestModel)
        call.enqueue(object : Callback<AllWorkQueueResponseModel> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel>, response: Response<AllWorkQueueResponseModel>) {
                if (response.body()?.status == 200) {

                    val responseData = response.body()?.data ?: emptyList()

                   /* if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
//                        AllitemsList.clear()
                        var tempList = response.body()!!.data

                        AllitemsList.addAll(response.body()!!.data)
                        setAdapter()
                        binding.nodatafound.visibility = View.VISIBLE

                    }
                    else {

                        var valueList = response.body()!!.data

                        if (pageno == 1) {
                            AllitemsList.clear()
                            AllitemsList.addAll(valueList)
                        } else {
                            AllitemsList.addAll(valueList)
                        }

                        setAdapter()
                        workQueueAdapter.notifyDataSetChanged()
                        binding.nodatafound.visibility = View.GONE
                        binding.ssPullRefresh.setRefreshing(false)

                        if (valueList.size < 10)
                            recallApi = false
                    }
*/


                    if (responseData.isEmpty()) {
                        if (pageno == 1) {
                            // Handle the case where the first page has no data
                            AllitemsList.clear()
                            setAdapter()
                            binding.nodatafound.visibility = View.VISIBLE
                        } else {
                            // Handle the case where subsequent pages have no data
                            recallApi = false
                        }
                    } else {
                        // There is data to process
                        if (pageno == 1) {
                            // If it's the first page, clear the list and add new data
                            AllitemsList.clear()
                        }
                        AllitemsList.addAll(responseData)

                        // Update UI elements
                        setAdapter()
                        workQueueAdapter.notifyDataSetChanged()
                        binding.nodatafound.visibility = View.GONE
                        binding.ssPullRefresh.setRefreshing(false)

                        // Determine if we should recall the API
                        if (responseData.size < 10) {
                            recallApi = false
                        }

                    }

                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()

                } else if (response.body()!!.status == 201) {
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.nodatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                }

            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel>, t: Throwable) {
//                Global.errormessagetoast(requireContext(),t.message.toString())
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                binding.ssPullRefresh.setRefreshing(false)
                Log.e(TAG, "onFailure: "+t.message.toString() )

            }
        })
    }


    private fun setAdapter() {

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        workQueueAdapter = WorkQueueAdapter(AllitemsList, RoleClass.deliveryPerson)
        binding.rvWorkQue.layoutManager = linearLayoutManager
        binding.rvWorkQue.adapter = workQueueAdapter
        workQueueAdapter.notifyDataSetChanged()

    }


    private fun showViewForOtherRole() {
        binding.apply {
//            shimmerLayout.visibility = View.VISIBLE
            rvWorkQue.visibility = View.VISIBLE
            tvCreateRoute.visibility = View.GONE
            tabLayout.visibility = View.GONE
            viewpager.visibility = View.GONE
        }
    }

    private fun showViewForDeliveryCoordinatorRole() {
        binding.apply {
//            shimmerLayout.visibility = View.GONE
            rvWorkQue.visibility = View.GONE
            tvCreateRoute.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
            viewpager.visibility = View.VISIBLE
        }
    }



}