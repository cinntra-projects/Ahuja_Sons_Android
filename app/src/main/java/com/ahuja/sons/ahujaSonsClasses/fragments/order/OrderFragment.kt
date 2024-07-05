package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.OrderListAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.OrderRequestModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.DialogAssignDeliveryPersonBinding
import com.ahuja.sons.databinding.FragmentOrderBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderFragment : Fragment() {

    lateinit var binding: FragmentOrderBinding
    lateinit var adapter: OrderListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var pageno = 1
    var isScrollingpage: Boolean = false
    var maxItem = "10"
    var recallApi = true
    var deleteicon = R.drawable.ic_baseline_delete_24
    var isOtherType = false
    lateinit var viewModel: MainViewModel
    var servciceID = ""
    var AllitemsList = ArrayList<AllOrderListResponseModel.Data>()
    var StatusType = ""
    var SearchText = ""


    companion object {
        private const val TAG = "OrderFragment"
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
        binding.toolbar.visibility = View.GONE






        /*  val callback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
              override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

                  if (AllitemsList.size > 0) {
                      var isAdmin = Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true)
                      Log.e(TAG, "getSwipeDirs: $isAdmin")
                      Log.e(TAG, "EMPLOYEECODE: ${Prefs.getString(Global.Employee_Code).toInt()}")
                      Log.e(TAG, "ASSIGNODE: ${AllitemsList[viewHolder.bindingAdapterPosition].AssignTo.toInt()}")

                      var cond = Prefs.getString(Global.Employee_Code).toInt() != AllitemsList[viewHolder.bindingAdapterPosition].AssignTo.toInt() || !isAdmin
                      Log.e(TAG, "COND====>: $cond")

                      var assignCond = Prefs.getString(Global.Employee_Code).toInt() != AllitemsList[viewHolder.bindingAdapterPosition].AssignTo.toInt()
                      Log.e(TAG, "Assign===>: $assignCond")

                      return if (Prefs.getString(Global.Employee_Code).toInt() != AllitemsList[viewHolder.bindingAdapterPosition].AssignTo.toInt() && !isAdmin) {
                          0
                      } else {
                          if (AllitemsList[viewHolder.bindingAdapterPosition].TicketStatus != "Pending") {
                              0
                          } else {
                              super.getSwipeDirs(recyclerView, viewHolder)
                          }
                      }
                  } else {
                      return 0
                  }

              }

              override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                  return false
              }

              override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                  // Take action for the swiped item

                  if (AllitemsList.size > 0) {
                      if (direction == ItemTouchHelper.LEFT) {
                          //   viewHolder.bindingAdapterPosition
                          openconfiremationdialog("Reject", AllitemsList[viewHolder.bindingAdapterPosition].id)
                          // adapter.notifyItemRemoved(viewHolder.adapterPosition)
                      } else {
                          openconfiremationdialog("Accept", AllitemsList[viewHolder.bindingAdapterPosition].id)
                      }
                  }


              }

              override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                  val deletecolor = context?.let { ContextCompat.getColor(it, R.color.red) }
                  val aceptcolor = context?.let { ContextCompat.getColor(it, R.color.green) }


                  if (deletecolor != null) {
                      if (aceptcolor != null) {
                          RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                              .addSwipeLeftBackgroundColor(deletecolor)
                              .addSwipeRightBackgroundColor(aceptcolor)
                              .addSwipeRightLabel("Accept")
                              .addSwipeRightActionIcon(R.drawable.ic_baseline_done_24)
                              .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                              .addSwipeLeftLabel("Reject")
                              .setSwipeRightLabelColor(resources.getColor(R.color.white))
                              .setSwipeLeftLabelColor(resources.getColor(R.color.white))
                              .setSwipeLeftActionIconTint(resources.getColor(R.color.white))
                              .setSwipeRightActionIconTint(resources.getColor(R.color.white))
                              .create()
                              .decorate()
                      }
                  }


                  super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
              }

          }
          val itemTouchHelper = ItemTouchHelper(callback)

          itemTouchHelper.attachToRecyclerView(binding.productRecyclerView)
  */

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

                    callAllOrderListApi(pageno, SearchText)
                    binding.loadingView.start()
                }
            }
        })



        binding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(requireContext()) && recallApi) {
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

            if (scrollY > oldScrollY + 12 && binding.addCustomer.isExtended) {
                binding.addCustomer.shrink()
            }

            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !binding.addCustomer.isExtended) {
                binding.addCustomer.extend()
            }

            // if the nestedScrollView is at the first item of the list then the
            // extended floating action should be in extended state
            if (scrollY == 0) {
                binding.addCustomer.extend();
            }
        })


        //todo set add button ui shrink and expendable according to list scroll
        binding.productRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // if the recycler view is scrolled
                // above shrink the FAB
                if (dy > 10 && binding.addCustomer.isExtended) {
                    binding.addCustomer.shrink()
                }

                // if the recycler view is scrolled
                // above extend the FAB
                if (dy < -10 && !binding.addCustomer.isExtended) {
                    binding.addCustomer.extend()
                }

                // of the recycler view is at the first
                // item always extend the FAB
                if (!recyclerView.canScrollVertically(-1)) {
                    binding.addCustomer.extend()
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


    }


    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        if (Global.checkForInternet(requireContext())) {
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
        binding.all.text = "All"
        binding.all.visibility = View.GONE
    }


    private fun callAllOrderListApi(pageno: Int, SearchText: String) {


        var field = OrderRequestModel.Field(
            FromDate = "", ToDate = "", FinalStatus = "", CardCode = "", CardName = "",
            ShipToCode = "", FromAmount = "", ToAmount = "", U_MR_NO = ""
        )
        var requestModel = OrderRequestModel(
            PageNo = pageno,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = SearchText,
            field = field,
            maxItem = maxItem,
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
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.nodatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
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
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = OrderListAdapter(AllitemsList,RoleClass.deliveryPerson)
        binding.productRecyclerView.layoutManager = linearLayoutManager
        binding.productRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickListener { data, i ->
            openDeliveryPersonDialog(requireActivity())


        }
    }



    lateinit var dialogBinding: DialogAssignDeliveryPersonBinding
    private fun openDeliveryPersonDialog(context: Context) {

        val dialog = Dialog(context,R.style.Theme_Dialog)

        val layoutInflater = LayoutInflater.from(context)
        dialogBinding = DialogAssignDeliveryPersonBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = 400
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }


        dialogBinding.tvTitle.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()


    }


}