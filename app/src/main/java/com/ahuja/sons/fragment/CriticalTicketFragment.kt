package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ResponseTicket
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.recyclerviewadapter.TicketNewAdapter
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class CriticalTicketFragment : Fragment() {
    var pageno = 1
    var recallApi = true
    private lateinit var ticketbiding : CategoryseeAllFragmentBinding
    lateinit var adapter: TicketNewAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)

        if(Global.checkForInternet(requireContext())) {
             pageno = 1
             recallApi = true
            AllitemsList.clear()
            ticketbiding.loadingView.start()
            callticketlistapi()

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
        })*/


        return ticketbiding.root
    }

    var AllitemsList= ArrayList<TicketData>()
    private fun callticketlistapi() {

        val data = HashMap<String,Any>()

        data["PageNo"] = pageno
        data["EmployeeId"] = Prefs.getString(Global.Employee_Code).toInt()
        data["Priority"] = "High"

        Log.e("payload",data.toString())
        val call: Call<ResponseTicket> =
            ApiClient().service.getfilterbyTickethashmap(data)
        call.enqueue(object : Callback<ResponseTicket> {
            override fun onResponse(
                call: Call<ResponseTicket>,
                response: Response<ResponseTicket>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.data != null) {
                        recallApi = response.body()!!.data.isEmpty()
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
            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
                Toast.makeText(context,t.message, Toast.LENGTH_SHORT).show()
                ticketbiding.loadingView.stop()
                ticketbiding.loadingback.visibility = View.GONE

                ticketbiding.idPBLoading.visibility = View.GONE
            }
        })


      /*  val model: TicketViewModel = ViewModelProviders.of(requireActivity())[TicketViewModel::class.java]
        model.getAllTicketlist(pageno)?.observe(requireActivity()) { itemsList ->
            if (itemsList!!.size >= 0) {
                AllitemsList.clear()
                AllitemsList.addAll(itemsList)
                linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = TicketAdapter(AllitemsList.filter { it.Priority=="High" } as ArrayList<TicketDataModel>)
                ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                ticketbiding.productRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                if(adapter.itemCount==0){
                    ticketbiding.nodatafound.isVisible = true
                }

            }
            ticketbiding.loadingView.stop()
        }*/

    }
}
