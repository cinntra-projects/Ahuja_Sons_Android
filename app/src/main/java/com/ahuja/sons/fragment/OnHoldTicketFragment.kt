package com.ahuja.sons.fragment

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.databinding.TicektDetailsBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.NewLoginData
import com.ahuja.sons.newapimodel.ResponseTicket
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.recyclerviewadapter.TicketNewAdapter
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.activity.ServiceContractDetailActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.viewmodel.MainViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import taimoor.sultani.sweetalert2.Sweetalert
import java.util.HashMap

class OnHoldTicketFragment(val ticketactbinding: TicektDetailsBinding, var flag: String?) : Fragment() {

    private lateinit var ticketbiding : CategoryseeAllFragmentBinding
    var pageno = 1
    var recallApi = true
    lateinit var adapter: TicketNewAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)

        if (flag == "ServiceContract"){
            viewModel = (activity as ServiceContractDetailActivity).viewModel
        }else {
            viewModel = (activity as MainActivity).viewModel
        }

        /*ticketbiding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.


                if(Global.checkForInternet(requireContext())&&recallApi){

                    pageno++
                    ticketbiding.idPBLoading.visibility = View.VISIBLE
                    callticketlistapi()
                }

            }
        })*///todo


        val callback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

            override fun getSwipeDirs (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if(AllitemsList.size>0){
                    if (AllitemsList[viewHolder.bindingAdapterPosition].TicketStatus != "Pending")
                        return 0
                }
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Take action for the swiped item
                if(AllitemsList.size>0){
                    if (direction == ItemTouchHelper.LEFT) {
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




        ticketbiding.ssPullRefresh.setOnRefreshListener(object : SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                // This is demo code to perform
                /*GlobalScope.launch {
                    delay(3000)
                    ssPullRefresh.setRefreshing(false) // This line stops layout refreshing
                    MainScope().launch {
                        Toast.makeText(this@MainActivity,"Refresh Complete",Toast.LENGTH_SHORT).show()
                    }
                }*/


                if(Global.checkForInternet(requireContext())) {
                    AllitemsList.clear()
                    pageno=1
                    ticketbiding.loadingView.start()
                    callticketlistapi()

                }
            }
        })

        ticketbiding.productRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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


        ticketactbinding.search.setOnClickListener{
            if(ticketactbinding.searchView.isVisible){
                ticketactbinding.viewpager.currentItem = 0
                ticketactbinding.searchView.visibility= View.GONE
            }else{
                ticketactbinding.viewpager.currentItem = 0
                ticketactbinding.searchView.visibility= View.VISIBLE
            }
        }

/*
        ticketactbinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // if query exist within list we
                // are filtering our list adapter.
                if (query != null) {
                    adapter.filter(query)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filter(newText)
                }
                return true
            }

        })
*/


        return ticketbiding.root
    }
    private fun openconfiremationdialog(s: String, id: Int) {
        val msz = if(s == "Accept") {
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
        pDialog.setCancelClickListener {
                sDialog -> sDialog.cancel()
            adapter.notifyDataSetChanged()
        }
        pDialog.setConfirmClickListener {
            val ticketdata = NewLoginData()
            if(s=="Accept"){

                ticketdata.setTicketid(id.toString())
                ticketdata.setEmployeeId(Prefs.getString(Global.Employee_Code))
                ticketdata.setTicketStatus("Accepted")
            }else{
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

                Global.warningmessagetoast(requireContext(), it)
                Log.e("ticketAcceptReject", it)
            }, onLoading = {

            },
            onSuccess = { response ->
                Log.e("response",response.getLogInDetail().toString())

                if (response.getStatus() == 200) {
                    if (response.getStatus()==200) {
                        it?.changeAlertType(Sweetalert.SUCCESS_TYPE)
                        Handler().postDelayed({
                            it?.dismissWithAnimation()
                        }, 2000)
                        AllitemsList.clear()
                        pageno=1
                        recallApi=true
                        ticketbiding.loadingView.start()
                        callticketlistapi()

                    }
                } else {
                    Global.warningmessagetoast(requireContext(), response.getMessage().toString());
                }
            }
        ))
    }


    var AllitemsList= ArrayList<TicketData>()
    private fun callticketlistapi() {


        val data = HashMap<String,Any>()

        data["PageNo"] = pageno
        data["EmployeeId"] = Prefs.getString(Global.Employee_Code).toInt()
        data["Type"] = "Breakdown"
//        data["Type"] = "Servicing"



        val call: Call<ResponseTicket> = ApiClient().service.getfilterbyTickethashmap(data)
        call.enqueue(object : Callback<ResponseTicket> {
            override fun onResponse(
                call: Call<ResponseTicket>,
                response: Response<ResponseTicket>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.data != null) {
                        recallApi = response.body()!!.data.isNotEmpty()
                        AllitemsList.addAll(response.body()!!.data)
                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = TicketNewAdapter(AllitemsList)
                        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                        ticketbiding.productRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()

                        if(adapter.itemCount==0){
                            ticketbiding.nodatafound.isVisible = true
                        }
                        Log.e("data",response.body()?.data.toString())
                    }


                }else{
                    Toast.makeText(context,response.body()?.message, Toast.LENGTH_SHORT).show()

                }
                ticketbiding.loadingback.visibility = View.GONE

                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.ssPullRefresh.setRefreshing(false)
            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
                Toast.makeText(context,t.message, Toast.LENGTH_SHORT).show()
                ticketbiding.loadingback.visibility = View.GONE

                ticketbiding.loadingView.stop()
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.ssPullRefresh.setRefreshing(false)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if(Global.checkForInternet(requireContext())) {
            AllitemsList.clear()
            pageno=1
            recallApi=true
            callticketlistapi()
            ticketbiding.loadingView.start()
        }
        ticketactbinding.all.visibility = View.GONE
        ticketactbinding.searchView.clearFocus()
        ticketactbinding.searchView.visibility = View.GONE
    }
}
