package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.TicketDataModel
import com.ahuja.sons.newapimodel.ResponseTicket
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.recyclerviewadapter.TicketNewAdapter
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AccToDateTicketFragment(val startdate: Calendar, val enddate: Calendar) : Fragment() {

    private lateinit var ticketbiding: CategoryseeAllFragmentBinding
    lateinit var adapter: TicketNewAdapter

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel: MainViewModel
    var pageno = 1
    var recallApi = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)
        viewModel = (activity as MainActivity).viewModel
        Log.e(TAG, "onCreateView: ", )



       

        return ticketbiding.root
    }

    var AllitemsList = ArrayList<TicketData>()


    companion object{
        private const val TAG = "AccToDateTicketFragment"
    }

    val isLoading = false
    val islastPage = false
    var isScrollingpage = false


    val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                val firstVisibleitempositon: Int =
                    linearLayoutManager.findFirstVisibleItemPosition() //first item
                val visibleItemCount: Int =
                    linearLayoutManager.getChildCount() //total number of visible item
                val totalItemCount: Int = linearLayoutManager.getItemCount() //total number of item
                val isNotLoadingAndNotLastPage = !isLoading && !islastPage
                val isAtLastItem = firstVisibleitempositon + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleitempositon >= 0
                val isTotaolMoreThanVisible: Boolean =
                    totalItemCount >= Global.PAGE_SIZE
                val shouldPaginate =
                    (isNotLoadingAndNotLastPage && isNotAtBeginning && isAtLastItem && isTotaolMoreThanVisible
                            && isScrollingpage)

                if (isScrollingpage && (visibleItemCount + firstVisibleitempositon == totalItemCount)) {
                    pageno++
                    Log.e(TAG, "onScrolled: ", )
                    loadTickets()
                    isScrollingpage = false
                } else {
                    // Log.d(TAG, "onScrolled:not paginate");
                    recyclerView.setPadding(0, 0, 0, 0)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //it means we are scrolling
                    isScrollingpage = true
                }
            }
        }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ", )
        if (Global.checkForInternet(requireContext())) {
            pageno = 1
            recallApi = true
            AllitemsList.clear()
//            val data = HashMap<String,Any>()
//            data["PageNo"] = pageno
//            data["EmployeeId"] = Prefs.getString(Global.Employee_Code).toInt()
//            data["FromDate"] = Global.datetoSimpleString(startdate.time)
//            data["ToDate"] = Global.datetoSimpleString(enddate.time)
//            Log.e("payload=>B",data.toString())
//            viewModel.getTodayTicket(data)
//
//
//            subscribeToTicketObserver()
            loadTickets()
        }
    }

    private fun loadTickets() {
        val data = HashMap<String, Any>()

        data["PageNo"] = pageno
        data["EmployeeId"] = Prefs.getString(Global.Employee_Code).toInt()
        data["FromDate"] = Global.datetoSimpleString(startdate.time)
        data["ToDate"] = Global.datetoSimpleString(enddate.time)
        Log.e("payload==>BB", data.toString())
        /*loader.setVisibility(View.VISIBLE);
        String url = Globals.GetCustomers+" &$skip="+Globals.SkipItem(pageNo);*/
        val call: Call<ResponseTicket> =
            ApiClient().service.getfilterbyTickethashmap(data)
        call.enqueue(object : Callback<ResponseTicket> {
            override fun onResponse(
                call: Call<ResponseTicket>,
                response: Response<ResponseTicket>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.data != null) {
                        recallApi = response.body()!!.data.isNotEmpty()
                        AllitemsList.addAll(response.body()!!.data)
                        linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = TicketNewAdapter(AllitemsList)
                        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                        ticketbiding.productRecyclerView.adapter = adapter
                        ticketbiding.productRecyclerView.addOnScrollListener(scrollListener)
                        adapter.notifyDataSetChanged()
                        if (adapter.itemCount == 0 && pageno == 1) {
                            ticketbiding.nodatafound.isVisible = true
                        }
                        Log.e("data", response.body()?.data.toString())
                    }
                    ticketbiding.loadingback.visibility = View.GONE


                } else {
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                }
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.idPBLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                ticketbiding.loadingView.stop()
                ticketbiding.loadingback.visibility = View.GONE

                ticketbiding.idPBLoading.visibility = View.GONE
            }
        })
    }

    private fun filterbydate(itemsList: ArrayList<TicketDataModel>): ArrayList<TicketDataModel> {
        val templist = ArrayList<TicketDataModel>()
        val startdatedata: Date = startdate.time
        val enddatedata: Date = enddate.time
        templist.clear()
        for (td in itemsList) {
            val date1: Date = Global.formatDateFromDateString(td.CreateDate)
                ?.let { SimpleDateFormat("dd/MM/yyyy").parse(it) } as Date
            if (date1.after(startdatedata) && date1.before(enddatedata))
                templist.add(td)

        }

        return templist
    }


    private fun subscribeToTicketObserver() {
        viewModel.todaysTicket.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                // binding.loader.visibility = View.VISIBLE
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                Global.errormessagetoast(requireContext(), it)
                //  Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }, onLoading = {
                ticketbiding.loadingback.visibility = View.VISIBLE
                ticketbiding.loadingView.start()
                //  binding.loader.visibility = View.VISIBLE
            }, {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                //  binding.loader.visibility = View.GONE
                if (it.status == 200) {
                    ticketbiding.loadingView.stop()
                    recallApi = it.data.isNotEmpty()
                    AllitemsList.clear()
                    AllitemsList.addAll(it.data)
                    linearLayoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = TicketNewAdapter(AllitemsList)
                    ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                    ticketbiding.productRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()

                    if (adapter.itemCount == 0) {
                        ticketbiding.nodatafound.isVisible = true
                    }
                    Log.e("data", it.data.toString())
                } else {
                    Global.errormessagetoast(requireContext(), it.message)
                }

            }

        ))


    }


}
