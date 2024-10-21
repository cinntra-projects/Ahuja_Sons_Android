package com.ahuja.sons.ahujaSonsClasses.fragments.route

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.Interface.OnDialogClickListener
import com.ahuja.sons.ahujaSonsClasses.adapter.DeliveryCoordinatorIDsAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.OrderListForDeliveryCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalSelectedOrder
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.WorkQueueRequestModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.FragmentOrderBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderForDeliveryCoordinatorFragment(var tvCreateRoute: TextView, var ivCollapseCart: ImageButton, var searchBtn: ImageButton) : Fragment(), OnDialogClickListener {

    lateinit var binding: FragmentOrderBinding
    var adapter: OrderListForDeliveryCoordinatorAdapter? = null
    lateinit var deliveryIDAdapter: DeliveryCoordinatorIDsAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var pageno = 1
    var isScrollingpage: Boolean = false
    var maxItem = 10
    var recallApi = true
    var deleteicon = R.drawable.ic_baseline_delete_24
    var isOtherType = false
    lateinit var viewModel: MainViewModel
    var servciceID = ""
    var AllitemsList = ArrayList<AllWorkQueueResponseModel.Data>()
    var StatusType = ""
    var SearchText = ""

    var assignCard: CardView? = null
    var tvCancelRoute: TextView? = null
    var isMultiOrderCardSelectEnabled = false


    companion object {
        private const val TAG = "OrderForDeliveryCoordinato"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assignCard = requireActivity().findViewById(R.id.cardAssignButton)
        tvCancelRoute = requireActivity().findViewById(R.id.tvCancelRoute)

        binding.toolbar.visibility = View.GONE

        deliveryIDAdapter = DeliveryCoordinatorIDsAdapter(
            ArrayList(),
            isMultiOrderCardSelectEnabled,
            OrderListForDeliveryCoordinatorAdapter.checkBOxOuter
        )

        binding.chipGroup.visibility = View.GONE//todo hide filter
        tvCreateRoute.visibility = View.VISIBLE
        ivCollapseCart.visibility = View.VISIBLE

        tvCreateRoute.setOnClickListener {
            Log.e(TAG, "onViewCreated: text")

            if (assignCard!!.visibility == View.GONE) {
                assignCard!!.visibility = View.VISIBLE
                isMultiOrderCardSelectEnabled = true
                adapter?.let {
                    it.isUpdated(isMultiOrderCardSelectEnabled)
                }
                deliveryIDAdapter.isUpdated(isMultiOrderCardSelectEnabled)
            } else {
                assignCard!!.visibility = View.GONE
                isMultiOrderCardSelectEnabled = false
                adapter?.let {
                    it.isUpdated(isMultiOrderCardSelectEnabled)
                }

                deliveryIDAdapter.isUpdated(isMultiOrderCardSelectEnabled)
            }
            adapter?.let {
                it.notifyDataSetChanged()
            }

        }


        tvCancelRoute!!.setOnClickListener {

            GlobalClasses.deliveryIDsList.clear()
            GlobalClasses.allOrderIDCoordinatorCheck.clear()

            if (assignCard!!.visibility == View.GONE) {
                assignCard!!.visibility = View.VISIBLE
                isMultiOrderCardSelectEnabled = true
                adapter?.let {
                    it.isUpdated(isMultiOrderCardSelectEnabled)
                }
                deliveryIDAdapter.isUpdated(isMultiOrderCardSelectEnabled)
            } else {
                assignCard!!.visibility = View.GONE
                isMultiOrderCardSelectEnabled = false
                adapter?.let {
                    it.isUpdated(isMultiOrderCardSelectEnabled)
                }

                deliveryIDAdapter.isUpdated(isMultiOrderCardSelectEnabled)

            }

            pageno = 1
            recallApi = true
            AllitemsList.clear()
            SearchText = ""
            binding.searchView.clearFocus()
            binding.searchView.visibility = View.GONE
            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE
            callWorkQueueList(pageno, SearchText, fromDate, toDate)


            adapter?.let {
                it.notifyDataSetChanged()
            }

            deliveryIDAdapter?.let {
                it.notifyDataSetChanged()
            }

        }


        binding.ssPullRefresh.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                binding.searchView.clearFocus()
                binding.searchView.visibility = View.GONE

                if (Global.checkForInternet(requireContext())) {
                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = ""

                    callWorkQueueList(pageno, SearchText, fromDate, toDate)
                    binding.loadingView.start()
                }
            }
        })


        searchBtn.setOnClickListener {
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
                    binding.loadingback.visibility = View.VISIBLE
                    callWorkQueueList(pageno, SearchText, fromDate, toDate)

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
                    binding.loadingback.visibility = View.VISIBLE
                    callWorkQueueList(pageno, SearchText, fromDate, toDate)

                } else {

                }
                return false
            }

        })


        ivCollapseCart.setOnClickListener {
            showFilterPopup()
        }

    }


    fun onChildCheckboxSelected(selectedChildItem: AllWorkQueueResponseModel.InspectedDelivery) {
        // Find the parent data corresponding to this child
        Log.e(TAG, "onChildCheckboxSelected: parent selected" )
        val parentData = this.findParentDataForChild(selectedChildItem)
        if (parentData != null) {
            // Check the parent checkbox
            parentData.isSelected = true
            // Add parent order to the cartListForDeliveryCoordinatorCheck
            val localSelectedOrder = LocalSelectedOrder().apply {
                orderId = parentData.OrderRequest!!.id.toString()//id
                orderName = parentData.CardName
                orderName = parentData.CardName
                errandId = parentData.DeliveryId
                isErrand = parentData.is_errands
                isReturn = parentData.is_return
                id = parentData.id
            }
            GlobalClasses.allOrderIDCoordinatorCheck.add(localSelectedOrder)
//            GlobalClasses.cartListForDeliveryCoordinatorCheck[parentData.OrderRequest?.id.toString()] = localSelectedOrder
        }
    }

    private fun findParentDataForChild(childItem: AllWorkQueueResponseModel.InspectedDelivery): AllWorkQueueResponseModel.Data? {
        // Logic to find the parent data corresponding to the child item
        return AllitemsList.find { parent -> parent.InspectedDeliverys.contains(childItem) }
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

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        if (Global.checkForInternet(requireContext())) {
            pageno = 1
            recallApi = true
            AllitemsList.clear()
            SearchText = ""
            binding.searchView.clearFocus()
            binding.searchView.visibility = View.GONE
            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE
            callWorkQueueList(pageno, SearchText, fromDate, toDate)

        }

        binding.all.text = "All"
        binding.all.visibility = View.GONE
    }


    override fun onButtonClick() {
        if (Global.checkForInternet(requireContext())) {
            pageno = 1
            recallApi = true
            AllitemsList.clear()
            SearchText = ""
            binding.searchView.clearFocus()
            binding.searchView.visibility = View.GONE
            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE
            callWorkQueueList(pageno, SearchText, fromDate, toDate)

        }
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
            override fun onResponse(
                call: Call<AllWorkQueueResponseModel>,
                response: Response<AllWorkQueueResponseModel>
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
                        adapter?.let {
                            it.notifyDataSetChanged()
                        }

//                        adapter.notifyDataSetChanged()
                        binding.nodatafound.visibility = View.GONE
                        binding.ssPullRefresh.setRefreshing(false)

                        if (valueList.size < 10)
                            recallApi = false
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
        adapter = OrderListForDeliveryCoordinatorAdapter(AllitemsList, RoleClass.deliveryPerson, isMultiOrderCardSelectEnabled, binding.checkBoxSelectAll)
        binding.productRecyclerView.layoutManager = linearLayoutManager
        binding.productRecyclerView.adapter = adapter
        adapter?.let {
            it.notifyDataSetChanged()
        }



    }


}