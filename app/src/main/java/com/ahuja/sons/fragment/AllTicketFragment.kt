package com.ahuja.sons.fragment

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.ahuja.sons.R
import com.ahuja.sons.activity.AccountDetailActivity
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.activity.ServiceContractDetailActivity
import com.ahuja.sons.adapter.ScopeOfWorkAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.databinding.TicektDetailsBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.NewLoginData
import com.ahuja.sons.model.ScopOfWorkResponseModel
import com.ahuja.sons.newapimodel.AllTicketRequestModel
import com.ahuja.sons.newapimodel.ResponseTicket
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.receiver.DataEmployeeAllData
import com.ahuja.sons.recyclerviewadapter.TicketNewAdapter
import com.ahuja.sons.viewmodel.MainViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import taimoor.sultani.sweetalert2.Sweetalert


class AllTicketFragment(
    var ticketactbinding: TicektDetailsBinding,
    var flag: String,
    var servcieID: String
) : Fragment() {

    private lateinit var ticketbiding: CategoryseeAllFragmentBinding
    lateinit var adapter: TicketNewAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var powerMenu: PowerMenu
    lateinit var powerMenuOther: PowerMenu
    var pageno = 1
    var isScrollingpage: Boolean = false
    var maxItem = 10
    var recallApi = true
    var deleteicon = R.drawable.ic_baseline_delete_24
    var isOtherType = false
    lateinit var viewModel: MainViewModel
    var servciceID = ""
    var AllitemsList = ArrayList<TicketData>()
    var StatusType = ""
    var SearchText = ""

    companion object {
        private const val TAG = "AllTicketFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)

        if (flag == "ServiceContract") {
            viewModel = (activity as ServiceContractDetailActivity).viewModel
        } else if (flag == "CustomerDetail") {
            viewModel = (activity as AccountDetailActivity).viewModel
        } else {
            viewModel = (activity as MainActivity).viewModel
        }


        val callback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or LEFT) {
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
                        if (direction == LEFT) {
                            //   viewHolder.bindingAdapterPosition
                            openconfiremationdialog(
                                "Reject",
                                AllitemsList[viewHolder.bindingAdapterPosition].id
                            )
                            // adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        } else {
                            openconfiremationdialog(
                                "Accept",
                                AllitemsList[viewHolder.bindingAdapterPosition].id
                            )
                        }
                    }


                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val deletecolor = context?.let { ContextCompat.getColor(it, R.color.red) }
                    val aceptcolor = context?.let { ContextCompat.getColor(it, R.color.green) }


                    if (deletecolor != null) {
                        if (aceptcolor != null) {
                            RecyclerViewSwipeDecorator.Builder(
                                c,
                                recyclerView,
                                viewHolder,
                                dX,
                                dY,
                                actionState,
                                isCurrentlyActive
                            )
                                /* .addSwipeLeftBackgroundColor(deletecolor)
                                            .addSwipeLeftActionIcon(deleteicon)*/
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


                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }
        val itemTouchHelper = ItemTouchHelper(callback)

        itemTouchHelper.attachToRecyclerView(ticketbiding.productRecyclerView)


        powerMenuOther = PowerMenu.Builder(requireContext()) // list has "Novel", "Poerty", "Art"
            .addItem(PowerMenuItem("All", true))
            .addItem(PowerMenuItem("Servicing", false)) // add an item.
//            .addItem(PowerMenuItem("Break Down", false)) // add an item.
            // add an item.
            // aad an item list.
//                .addItem(PowerMenuItem("Man-Trap", false)) // aad an item list.
//            .addItem(PowerMenuItem("Extra Work", false)) // aad an item list.
//                .addItem(PowerMenuItem("Resolved", false)) // aad an item list.
//                .addItem(PowerMenuItem("New", false)) // aad an item list.
            .setAnimation(MenuAnimation.ELASTIC_TOP_RIGHT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            .build()

        val onMenuOtherItemClickListener: OnMenuItemClickListener<PowerMenuItem?> =
            OnMenuItemClickListener<PowerMenuItem?> { position, item ->
                powerMenuOther.selectedPosition = position //
                item.setIsSelected(true)
                powerMenuOther.dismiss()
                // ticketactbinding.all.text=item.title.toString()
                when (item.title) {

                    "Servicing" -> {
                        isOtherType = true
                        StatusType = "Servicing"
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
//                        callticketlistOtherapi(pageno)
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", StatusType)

                        ticketbiding.loadingView.start()
                        ticketactbinding.tabLayout.getTabAt(0)?.text = "Servicing"
                        ticketactbinding.tabLayout.getTabAt(0)?.select()

                    }

                    "All" -> {
                        //  isOtherType=true
                        StatusType = ""
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
//                        callticketlistOtherapi(pageno)
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", StatusType)
                        ticketbiding.loadingView.start()
                        ticketactbinding.tabLayout.getTabAt(0)?.text = "All"
                        ticketactbinding.tabLayout.getTabAt(0)?.select()
                    }
                    "Repair" -> {
                        isOtherType = true
                        StatusType = "Extra Work"
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
//                        callticketlistOtherapi(pageno)
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", StatusType)
                        ticketbiding.loadingView.start()
                        ticketactbinding.tabLayout.getTabAt(0)?.text = "Repair"

                    }

                }
                true
            }
        powerMenuOther.onMenuItemClickListener = onMenuOtherItemClickListener


        ticketactbinding.kebabMoreFilter.setOnClickListener {
            Log.e("TAG", "onCreateView=====>: ")

            powerMenuOther.showAsDropDown(ticketactbinding.all)

        }

        if (Global.checkForInternet(requireContext())) {
            callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
//            setAdapter()
        }


        /*  //todo recycler view scrollListener for add more items in list...
          ticketbiding.productRecyclerView.addOnScrollListener(object :
              RecyclerView.OnScrollListener() {
              override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                  super.onScrolled(recyclerView, dx, dy)

                  var lastCompletelyVisibleItemPosition = (linearLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()



                  if (isScrollingpage && lastCompletelyVisibleItemPosition == AllitemsList.size - 2 && recallApi) {

                      if (Global.checkForInternet(requireContext()) && recallApi) {
                          pageno++
  //                    ticketbiding.idPBLoading.visibility = View.VISIBLE

                          if (ticketactbinding.searchView.isVisible) {
                              callSearchApi(ticketactbinding.searchView.query.toString())
                          } else {
                              Log.e("page--->", pageno.toString())
                              callticketlistapi(pageno)
                              isScrollingpage = false
                          }

                      }
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
          })*/


        ticketbiding.ssPullRefresh.setOnRefreshListener(object : SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                ticketactbinding.searchView.clearFocus()
                ticketactbinding.searchView.visibility = View.GONE

                if (Global.checkForInternet(requireContext())) {
                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = ""
                    AssignedName = ""
                    AssignedCode = ""
                    Priority = ""
                    Status = ""
                    fromDate = ""
                    toDate = ""

                    callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                    ticketbiding.loadingView.start()
                }
            }
        })




        ticketbiding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(requireContext()) && recallApi) {
                    pageno++

                    if (ticketactbinding.searchView.isVisible) {
//                        callSearchApi(ticketactbinding.searchView.query.toString()) //todo
                        SearchText = ticketactbinding.searchView.query.toString()
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                    } else {
                        SearchText = ""
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                        /* if (isOtherType) {
                             callticketlistOtherapi(pageno)
                         } else {
                             callticketlistapi(pageno)
                         }*///todo comment by me
                    }

                }
            }

            if (scrollY > oldScrollY + 12 && ticketactbinding.addCustomer.isExtended) {
                ticketactbinding.addCustomer.shrink()
            }

            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !ticketactbinding.addCustomer.isExtended) {
                ticketactbinding.addCustomer.extend()
            }

            // if the nestedScrollView is at the first item of the list then the
            // extended floating action should be in extended state
            if (scrollY == 0) {
                ticketactbinding.addCustomer.extend();
            }
        })


        //todo set add button ui shrink and expendable according to list scroll
        ticketbiding.productRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // if the recycler view is scrolled
                // above shrink the FAB
                if (dy > 10 && ticketactbinding.addCustomer.isExtended) {
                    ticketactbinding.addCustomer.shrink()
                }

                // if the recycler view is scrolled
                // above extend the FAB
                if (dy < -10 && !ticketactbinding.addCustomer.isExtended) {
                    ticketactbinding.addCustomer.extend()
                }

                // of the recycler view is at the first
                // item always extend the FAB
                if (!recyclerView.canScrollVertically(-1)) {
                    ticketactbinding.addCustomer.extend()
                }
            }
        })


        powerMenu = PowerMenu.Builder(requireContext()) // list has "Novel", "Poerty", "Art"
            .addItem(PowerMenuItem("All", true)) // add an item.
            // add an item.
            .addItem(PowerMenuItem("Assigned", false)) // aad an item list.
            .addItem(PowerMenuItem("In Progress", false)) // aad an item list.
            .addItem(PowerMenuItem("Pending", false)) // aad an item list.
            .addItem(PowerMenuItem("Resolved", false)) // aad an item list.
            .addItem(PowerMenuItem("New", false)) // aad an item list.
            .setAnimation(MenuAnimation.ELASTIC_TOP_RIGHT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .build()

        val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem?> =
            OnMenuItemClickListener<PowerMenuItem?> { position, item ->
                powerMenu.selectedPosition = position //
                item.setIsSelected(true)
                powerMenu.dismiss()
                ticketactbinding.all.text = item.title.toString()
                when (item.title) {

                    "All" -> {
                        StatusType = ""
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                        ticketbiding.loadingView.start()
                    }
                    "Assigned" -> {
                        StatusType = "Assigned"
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                        ticketbiding.loadingView.start()
                    }
                    "In Progress" -> {
                        StatusType = "In Progress"
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                        ticketbiding.loadingView.start()
                    }
                    "Pending" -> {
                        StatusType = "Pending"
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                        ticketbiding.loadingView.start()
                    }
                    "Resolved" -> {
                        StatusType = "Resolved"
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                        ticketbiding.loadingView.start()
                    }


                    "New" -> {
                        StatusType = "New"
                        SearchText = ""
                        pageno = 1
                        recallApi = true
                        AllitemsList.clear()
                        callticketlistapi(pageno, SearchText, "", "", "", "", "", "")
                        ticketbiding.loadingView.start()
                    }

                }
            }
        powerMenu.onMenuItemClickListener = onMenuItemClickListener



        ticketactbinding.all.setOnClickListener {
            powerMenu.showAsDropDown(ticketactbinding.all)
        }

        ticketactbinding.search.setOnClickListener {
            if (ticketactbinding.searchView.isVisible) {
                ticketactbinding.searchView.visibility = View.GONE
            } else {
                ticketactbinding.searchView.visibility = View.VISIBLE
            }
        }

        ticketactbinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // if query exist within list we
                // are filtering our list adapter.
                if (query != null && query.toString().isNotEmpty()) {
                    /* pageno = 1
                     recallApi = true
                     AllitemsList.clear()
                     ticketbiding.loadingView.start()

                     callSearchApi(query)*/

                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = query
                    ticketbiding.loadingView.start()

                    callticketlistapi(pageno, SearchText, "", "", "", "", "", "")

                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty() && newText != "") {
                    /*pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    ticketbiding.loadingView.start()

                    callSearchApi(newText)
*/
                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = newText
                    ticketbiding.loadingView.start()

                    callticketlistapi(pageno, SearchText, "", "", "", "", "", "")

                } else {

                }
                return false
            }

        })


        ticketactbinding.filter.setOnClickListener {
            showAllFilterDialog()
        }


        return ticketbiding.root


    }

    var Status = ""
    var AssignedCode = ""
    var AssignedName = ""
    var fromDate = ""
    var toDate = ""
    var Priority = ""
    var scopeWorkVal = ""


    private fun showAllFilterDialog() {
        val dialog = Dialog(requireContext())
        val layoutInflater = LayoutInflater.from(requireContext())
        val customDialog = layoutInflater.inflate(R.layout.all_filter_layout, null)
        dialog.setContentView(customDialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val acAssignedTo: AutoCompleteTextView = dialog.findViewById(R.id.acCustomer)
        val acStatus: AutoCompleteTextView = dialog.findViewById(R.id.acStatus)
        val acScopeWork: AutoCompleteTextView = dialog.findViewById(R.id.acScopeWork)
        val acPriority: AutoCompleteTextView = dialog.findViewById(R.id.acPriority)
        val resetBtn: MaterialButton = dialog.findViewById(R.id.resetBtn)
        val applyBtn: MaterialButton = dialog.findViewById(R.id.applyBtn)
        val ivCrossIcon: ImageView = dialog.findViewById(R.id.ivCrossIcon)
        val rlRecyclerViewLayout: LinearLayout = dialog.findViewById(R.id.rlRecyclerViewLayout)
        val edtFromDate = dialog.findViewById<TextInputEditText>(R.id.edtFromDate)
        val edtToDate = dialog.findViewById<TextInputEditText>(R.id.edtToDate)
        val rvCustomerSearchList: RecyclerView = dialog.findViewById(R.id.rvCustomerSearchList)

        ivCrossIcon.setOnClickListener { dialog.dismiss() }


        acAssignedTo.setText(AssignedName)
        acStatus.setText(Status)
        edtFromDate.setText(fromDate)
        edtToDate.setText(toDate)
        acPriority.setText(Priority)

        if (Global.checkForInternet(requireContext())) {
            viewModel.getAllEmployeeList()
            subscribeAssignedToObserver(acAssignedTo, rlRecyclerViewLayout,rvCustomerSearchList)

            viewModel.getTypeTicket()
            binsScopeOfWorkObserver(acScopeWork)
        }


        //todo priority adapter bind--
        val priorityAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, Global.priorityList_gl)
        acPriority.setAdapter(priorityAdapter)

        acPriority.setOnItemClickListener { adapterView, view, pos, l ->
            if (Global.priorityList_gl.size > 0){
                Priority = Global.priorityList_gl[pos]
                acPriority.setText(Global.priorityList_gl[pos])
                val priorityAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, Global.priorityList_gl)
                acPriority.setAdapter(priorityAdapter)
            }else{
                acPriority.setText("")
            }
        }


        //todo status adapter bind--
        val statusAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, Global.ticketStatusList_gl)
        acStatus.setAdapter(statusAdapter)

        acStatus.setOnItemClickListener { adapterView, view, pos, l ->
            if (Global.ticketStatusList_gl.size > 0){
                Status = Global.ticketStatusList_gl[pos]
                acStatus.setText(Global.ticketStatusList_gl[pos])
                val statusAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, Global.ticketStatusList_gl)
                acStatus.setAdapter(statusAdapter)
            }else{
                acStatus.setText("")
            }
        }


        //todo scope work item selected
        acScopeWork.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (scopeWorkList.isNotEmpty()) {
                    scopeWorkVal = scopeWorkList[position].Type
                    acScopeWork.setText(scopeWorkList[position].Type)

                } else {
                    scopeWorkVal = ""
                    acScopeWork.setText("")
                }
            }

        }

        edtFromDate.setOnClickListener {
            Global.enableAllCalenderDateSelect(requireActivity(), edtFromDate)
        }


        edtToDate.setOnClickListener {
            Global.enableAllCalenderDateSelect(requireActivity(), edtToDate)
        }

        resetBtn.setOnClickListener {
            acAssignedTo.setText("")
            acStatus.setText("")
            acPriority.setText("")
            edtFromDate.setText("")
            edtToDate.setText("")
            AssignedCode = ""
            AssignedName = ""
            Priority = ""
            Status = ""
            fromDate = ""
            toDate = ""
        }

        applyBtn.setOnClickListener {
            pageno = 1
            SearchText = ""
            fromDate = edtFromDate.text.toString()
            toDate = edtToDate.text.toString()
            callticketlistapi(pageno, SearchText, AssignedCode, Priority, Status, fromDate, toDate, scopeWorkVal)
            dialog.dismiss()
        }

        dialog.show()
    }

//    var scopeWorkList : ArrayList<DataTicketType> = ArrayList()
    var scopeWorkList : ArrayList<ScopOfWorkResponseModel.Daum> = ArrayList()

    private fun binsScopeOfWorkObserver(acScopeWork: AutoCompleteTextView) {
        viewModel.scopeOfWorkData.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserver: "+it )
            }, onLoading = {
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    try {
                        response.data.let { dataList ->

                            scopeWorkList.clear()
                            scopeWorkList.addAll(response.data)
                            var adapter = ScopeOfWorkAdapter(requireActivity(), R.layout.drop_down_item_textview, scopeWorkList)
                            acScopeWork.setAdapter(adapter)

                        }
                    }catch (e : java.lang.Exception){
                        e.printStackTrace()
                    }
                    Log.e(TAG, "subscribeToObserveAPir: $response")

                } else {
                    Global.warningmessagetoast(requireContext(), response.message)
                }

            }
        ))
    }

    //todo call assigned to api here...
    private val assignedTo_gl = ArrayList<DataEmployeeAllData>()

    private fun subscribeAssignedToObserver(acAssignedTo: AutoCompleteTextView, rlRecyclerViewLayout: LinearLayout, rvCustomerSearchList: RecyclerView) {
        viewModel.employeesAll.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserver: "+it )
            }, onLoading = {
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    try {
                        response.data.let { dataList ->
                            val itemsList = filterList(dataList)
                            assignedTo_gl.addAll(itemsList)

                            val itemNames = ArrayList<String>()
                            val cardCodeName = ArrayList<String>()
                            for (item in assignedTo_gl) {
                                itemNames.add(item.SalesEmployeeName + " ( " + item.role + " ) ")
                                cardCodeName.add(item.SalesEmployeeName)
                            }

                            val adapter = ArrayAdapter(requireContext(), R.layout.drop_down_item_textview, itemNames)
                            acAssignedTo.setAdapter(adapter)

                            //todo bill to and ship to address drop down item select..
                            acAssignedTo.setOnItemClickListener { parent, view, position, id ->
                                val cardName = parent.getItemAtPosition(position) as String
                                AssignedName = cardName

                                val pos = Global.getCustomerPos(assignedTo_gl, AssignedName)
                                AssignedCode = assignedTo_gl[pos].SalesEmployeeCode

                                if (AssignedName.isEmpty()) {
                                    rlRecyclerViewLayout.visibility = View.GONE
                                    rvCustomerSearchList.visibility = View.GONE
                                } else {
                                    rlRecyclerViewLayout.visibility = View.VISIBLE
                                    rvCustomerSearchList.visibility = View.VISIBLE
                                }

                                if (!AssignedName.isEmpty()) {
                                    adapter.notifyDataSetChanged()
                                    acAssignedTo.setText(AssignedName)
                                    acAssignedTo.setSelection(acAssignedTo.length())

                                } else {
                                    acAssignedTo.setText("")
                                }
                            }
                        }
                    }catch (e : java.lang.Exception){
                        e.printStackTrace()
                    }
                    Log.e(SelectEmployeeDialogFragment.TAG, "subscribeToObserveAPir: $response")

                } else {
                    Global.warningmessagetoast(requireContext(), response.message)
                }

            }
        ))
    }


    //todo filter for customer search ..
    private fun filterList(value: ArrayList<DataEmployeeAllData>): List<DataEmployeeAllData> {
        val tempList = ArrayList<DataEmployeeAllData>()
        for (customer in value) {
            if (customer.SalesEmployeeName != "admin") {
                tempList.add(customer)
            }
        }
        return tempList
    }


    //todo search api by chanchal..
    private fun callSearchApi(query: String) {
        val data = HashMap<String, Any>()
        data["EmployeeId"] = Prefs.getString(Global.Employee_Code)
        data["PageNo"] = pageno
        data["SearchText"] = query

        Log.e("payload", data.toString())

        val call: Call<ResponseTicket> = ApiClient().service.searchApi(data)
        call.enqueue(object : Callback<ResponseTicket> {
            override fun onResponse(
                call: Call<ResponseTicket>,
                response: Response<ResponseTicket>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.status == 200) {

                        recallApi = response.body()!!.data.isNotEmpty()
                        AllitemsList.addAll(response.body()!!.data)
                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = TicketNewAdapter(AllitemsList)
                        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                        ticketbiding.productRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                        checknodata()
                    }


                } else {

                    Global.warningmessagetoast(
                        requireContext(),
                        response.body()?.message.toString()
                    )

                }
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.loadingback.visibility = View.GONE

            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
                Global.errormessagetoast(requireContext(), t.message.toString())
                ticketbiding.loadingView.stop()
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.idPBLoading.visibility = View.GONE
            }
        })
    }

    private fun openconfiremationdialog(s: String, id: Int) {
        val msz = if (s == "Accept") {
            "You want to accept the ticket"
        } else {
            "You want to reject the ticket"
        }
        val pDialog = Sweetalert(context, Sweetalert.WARNING_TYPE)
        pDialog.titleText = "Are you sure?"
        pDialog.contentText = msz
        pDialog.setCanceledOnTouchOutside(false)
        pDialog.cancelText = "No,cancel it!"
        pDialog.confirmText = "Yes,$s it!"
        pDialog.showCancelButton(true)
        pDialog.showConfirmButton(true)
        pDialog.setCancelClickListener { sDialog ->
            sDialog.cancel()
            adapter.notifyDataSetChanged()
        }
        pDialog.setConfirmClickListener {

            val ticketdata = NewLoginData()
            if (s == "Accept") {

                ticketdata.setTicketid(id.toString())
                ticketdata.setEmployeeId(Prefs.getString(Global.Employee_Code))
                ticketdata.setTicketStatus("Accepted")
            } else {
                ticketdata.setTicketid(id.toString())
                ticketdata.setEmployeeId(Prefs.getString(Global.Employee_Code))
                ticketdata.setTicketStatus("Rejected")
            }

            viewModel.acceptRejectTicket(ticketdata)
            bindTicketStatusObserver(it)

        }
        pDialog.show()

    }

    //todo observer for ticket accept and reject..

    private fun bindTicketStatusObserver(it: Sweetalert?) {
        viewModel.ticketAcceptRejectResponse.observe(this, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(requireContext(), it)
//                Log.e("ticketAcceptReject", it)
            }, onLoading = {

            },
            onSuccess = { response ->
                Log.e("response", response.getLogInDetail().toString())

                if (response.getStatus() == 200) {
                    it?.changeAlertType(Sweetalert.SUCCESS_TYPE)
                    Handler().postDelayed({ it?.dismissWithAnimation() }, 2000)
                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()
                    SearchText = ""
                    ticketbiding.loadingView.start()
                    if (isOtherType) {
                        callticketlistOtherapi(pageno)
                    } else {
                        callticketlistapi(
                            pageno,
                            SearchText,
                            AssignedCode,
                            Priority,
                            Status,
                            fromDate,
                            toDate, scopeWorkVal
                        )
                    }
                } else {
                    Global.warningmessagetoast(
                        requireContext(),
                        response.getMessage().toString()
                    );
                }
            }
        ))
    }


    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        // StatusType = ""
        if (Global.checkForInternet(requireContext())) {
            pageno = 1
            recallApi = true
            AllitemsList.clear()
            ticketbiding.loadingView.start()
            SearchText = ""
            ticketbiding.loadingback.visibility = View.VISIBLE
            if (isOtherType) {
                callticketlistOtherapi(pageno)
            } else {
                callticketlistapi(
                    pageno,
                    SearchText,
                    AssignedCode,
                    Priority,
                    Status,
                    fromDate,
                    toDate,
 scopeWorkVal
                )
            }


        }

        ticketactbinding.searchView.clearFocus()
        ticketactbinding.searchView.visibility = View.GONE
        ticketactbinding.all.text = "All"
        ticketactbinding.all.visibility = View.GONE
        powerMenu.selectedPosition = 0
    }

    var CardCode = ""

    private fun callticketlistapi(pageno: Int, SearchText: String, AssignedCode: String, Priority: String,
        Status: String, fromDate: String, toDate: String, scopeWorkVal: String) {

        if (flag == "ServiceContract") {
            servciceID = Prefs.getString(Global.servcieID)
        } else if (flag == "CustomerDetail") {
            CardCode = Prefs.getString(Global.servcieID)
        } else {
            servciceID = ""
            CardCode = ""
        }

        var field = AllTicketRequestModel.Field(finalstatus = Status, fromdate = fromDate, todate = toDate, searchAssignTo = AssignedCode, searchpriority = Priority,
            scopeWork = scopeWorkVal)
        var requestModel = AllTicketRequestModel(
            BranchId = "",
            CardCode = CardCode,
            PageNo = pageno,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = SearchText,
            field = field,
            maxItem = maxItem,
            ServiceContractId = servciceID,
        )

        val call: Call<ResponseTicket> = ApiClient().service.getAllTicketList(requestModel)
        call.enqueue(object : Callback<ResponseTicket> {
            override fun onResponse(
                call: Call<ResponseTicket>,
                response: Response<ResponseTicket>
            ) {
                if (response.body()?.status == 200) {

                    if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                        AllitemsList.clear()
                        AllitemsList.addAll(response.body()!!.data)
                        setAdapter()
                        ticketbiding.nodatafound.visibility = View.VISIBLE

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
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                } else {
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.nodatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                }

            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
//                Global.errormessagetoast(requireContext(),t.message.toString())
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.ssPullRefresh.setRefreshing(false)

            }
        })
    }

    private fun setAdapter() {
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = TicketNewAdapter(AllitemsList)
        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
        ticketbiding.productRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun callticketlistOtherapi(pageno: Int) {
        val data = HashMap<String, Any>()
        data["PageNo"] = pageno
        data["EmployeeId"] = Prefs.getString(Global.Employee_Code).toInt()
        data["Type"] = StatusType
        Log.e("PayLoad==>BHUPI==>", data.toString())

        val call: Call<ResponseTicket> = ApiClient().service.getfilterbyTickethashmap(data)
        call.enqueue(object : Callback<ResponseTicket> {
            override fun onResponse(
                call: Call<ResponseTicket>,
                response: Response<ResponseTicket>
            ) {
                if (response.body()?.status == 200) {

                    if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                        AllitemsList.clear()
                        AllitemsList.addAll(response.body()!!.data)
                        setAdapter()
                        ticketbiding.nodatafound.visibility = View.VISIBLE

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
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                } else {
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.nodatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                }

            /*    if (response.code() == 200) {

                    if (response.body()?.data != null) {
                        recallApi = response.body()!!.data.isNotEmpty()
                        AllitemsList.addAll(response.body()!!.data)
                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = TicketNewAdapter(AllitemsList)
                        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                        ticketbiding.productRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                        Log.e("data", response.body()?.data.toString())

                        checknodata()
                    }


                } else {
                    Global.warningmessagetoast(
                        requireContext(),
                        response.body()?.message.toString()
                    )

                }
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.loadingback.visibility = View.GONE

                ticketbiding.loadingView.stop()
                ticketbiding.ssPullRefresh.setRefreshing(false)*/
            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
//                Global.errormessagetoast(requireContext(),t.message.toString())
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.ssPullRefresh.setRefreshing(false)

            }
        })
    }

    private fun checknodata() {
        ticketbiding.nodatafound.isVisible = adapter.itemCount == 0
    }

}
