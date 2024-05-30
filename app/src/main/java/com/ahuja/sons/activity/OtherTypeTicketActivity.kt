package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.ActivityOtherTypeTicketBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ResponseTicket
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.recyclerviewadapter.TicketNewAdapter
import com.pixplicity.easyprefs.library.Prefs
import com.simform.refresh.SSPullToRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherTypeTicketActivity : AppCompatActivity() {
    lateinit var binding: ActivityOtherTypeTicketBinding
    var where = ""
    var StatusType = ""
    var pageno = 1
    var recallApi = true
    lateinit var adapter: TicketNewAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var AllitemsList = ArrayList<TicketData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherTypeTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        where = intent.getStringExtra(Global.INTENT_WHERE_STATUS).toString()
        setupToolbar()
        linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        callticketlistOtherapi(pageno)


        binding.ssPullRefresh.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                // This is demo code to perform
                /*GlobalScope.launch {
                    delay(3000)
                    ssPullRefresh.setRefreshing(false) // This line stops layout refreshing
                    MainScope().launch {
                        Toast.makeText(this@MainActivity,"Refresh Complete",Toast.LENGTH_SHORT).show()
                    }
                }*/
               // ticketactbinding.searchView.clearFocus()
               // ticketactbinding.searchView.visibility = View.GONE

                if (Global.checkForInternet(this@OtherTypeTicketActivity)) {
                    pageno = 1
                    recallApi = true
                    AllitemsList.clear()

                        callticketlistOtherapi(pageno)


                    binding.loadingView.start()
                }
            }
        })


        binding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.


                if (Global.checkForInternet(this) && recallApi) {

                    pageno++
                    //   ticketbiding.idPBLoading.visibility = View.VISIBLE
                  //  binding.loadingback.visibility = View.VISIBLE
//                    if (ticketactbinding.searchView.isVisible) {
//
//                        callSearchApi(ticketactbinding.searchView.query.toString())
//                    } else {
//                        if (isOtherType){
//                            callticketlistOtherapi(pageno)
//                        }else{
//                            callticketlistapi(pageno)
//                        }
//                        //  callticketlistapi(pageno)
//                    }

                }

            }


//            if (scrollY > oldScrollY + 12 && binding.addCustomer.isExtended) {
//             //   ticketactbinding.addCustomer.shrink()
//            }
//
//            // the delay of the extension of the FAB is set for 12 items
//            if (scrollY < oldScrollY - 12 && !ticketactbinding.addCustomer.isExtended) {
//              //  ticketactbinding.addCustomer.extend()
//            }

            // if the nestedScrollView is at the first item of the list then the
            // extended floating action should be in extended state
//            if (scrollY == 0) {
//                ticketactbinding.addCustomer.extend();
//            }
        })


    }


    private fun setupToolbar() {
        if (where.equals("repair", ignoreCase = true)) {
            binding.toolbarOtherTicket.toolbarAnnouncement.title = "Repair"
            StatusType = "Repair"
        } else {
            binding.toolbarOtherTicket.toolbarAnnouncement.title = "Man Trap"
            StatusType = "Man-Trap"
        }

        binding.toolbarOtherTicket.toolbarAnnouncement.setOnClickListener {
            finish()
        }
    }


    private fun callticketlistOtherapi(pageno: Int) {
        val data = HashMap<String, Any>()

        data["PageNo"] = pageno
        data["EmployeeId"] = Prefs.getString(Global.Employee_Code).toInt()
        data["Type"] = StatusType
        Log.e("PayLoad==>BHUPI==>", data.toString())
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
                            LinearLayoutManager(
                                this@OtherTypeTicketActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        adapter = TicketNewAdapter(AllitemsList)
                        binding.rvTicket.layoutManager = linearLayoutManager
                        binding.rvTicket.adapter = adapter
                        adapter.notifyDataSetChanged()
                        Log.e("data", response.body()?.data.toString())

                        checknodata()
                    }


                } else {
                    Global.warningmessagetoast(
                        this@OtherTypeTicketActivity,
                        response.body()?.message.toString()
                    )

                }
                //    binding.idPBLoading.visibility = View.GONE
                binding.loadingback.visibility = View.GONE

                binding.loadingView.stop()
                binding.ssPullRefresh.setRefreshing(false)
            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
//                Global.errormessagetoast(requireContext(),t.message.toString())
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                //ticketbiding.idPBLoading.visibility = View.GONE
                binding.ssPullRefresh.setRefreshing(false)

            }
        })
    }

    private fun checknodata() {
        binding.nodatafound.isVisible = adapter.itemCount == 0
    }
}