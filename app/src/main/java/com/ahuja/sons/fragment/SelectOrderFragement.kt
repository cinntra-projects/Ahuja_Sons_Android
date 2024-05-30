package com.ahuja.sons.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.adapter.SelectOrderAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.SelectOrderFragmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.newapimodel.DataParticularCustomerOrder
import com.ahuja.sons.viewmodel.MainViewModel

class SelectOrderFragement(
    val orderValue1: AddTicketActivity,
    val code: String,

) : Fragment() {

    lateinit var viewModel: MainViewModel
    lateinit var adapter: SelectOrderAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var selectOrderData : AddTicketActivity


    private  lateinit var ticketFragment: SelectOrderFragmentBinding



    companion object{
        private const val TAG = "SelectOrderFragement"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ticketFragment = SelectOrderFragmentBinding.inflate(layoutInflater)
        viewModel=(activity as AddTicketActivity).viewModel
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayUseLogoEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        selectOrderData = orderValue1
        ticketFragment.toolbarview.heading.text  = "Select Order"

        val data = AccountBpData(
            CardCode = code
        )

        if(Global.checkForInternet(requireContext())) {
            var data = HashMap<String, String>()
            data["CardCode"] = code
            viewModel.getCustomerMvvmOrderAllOrderList(data)
            subscribeToObserver()
           // callorderlistapi(data)
          //  ticketFragment.loadingView.start()
        }



        ticketFragment.toolbarview.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        return ticketFragment.root
    }


    var AllitemsList= ArrayList<DataParticularCustomerOrder>()


//    private fun callorderlistapi(data: AccountBpData) {
//        val call: Call<OrderDataResponse> =
//            ApiClient().service.getAllorderList(data)
//        call.enqueue(object : Callback<OrderDataResponse> {
//            override fun onResponse(
//                call: Call<OrderDataResponse>,
//                response: Response<OrderDataResponse>
//            ) {
//                if (response.code() == 200) {
//                    Log.e(TAG, "onResponse: order" )
//
//                    if (response.body()?.data != null) {
//                        AllitemsList.clear()
//                        AllitemsList.addAll(response.body()!!.data)
//                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                        adapter = SelectOrderAdapter(orderValue1,requireContext(),AllitemsList)
//                        ticketFragment.recyclerview.layoutManager = linearLayoutManager
//                        ticketFragment.recyclerview.adapter = adapter
//                        adapter.notifyDataSetChanged()
//                        ticketFragment.nodatafound.isVisible = adapter.itemCount==0
//                        Log.e("data",response.body()?.data.toString())
//                    }
//
//
//                }else{
//                    Toast.makeText(context,response.body()?.message, Toast.LENGTH_SHORT).show()
//
//                }
//                ticketFragment.loadingView.stop()
//            }
//
//            override fun onFailure(call: Call<OrderDataResponse>, t: Throwable) {
//                ticketFragment.loadingback.visibility = View.GONE
//
//                ticketFragment.loadingView.stop()
//                Log.e("fail",t.message.toString())
//                Toast.makeText(context,t.message, Toast.LENGTH_SHORT).show()
//
//            }
//        })
//    }


    private fun subscribeToObserver() {
        viewModel.getCustomerParticularOrder.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {

                ticketFragment.loadingView.stop()
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                ticketFragment.loadingView.start()
            }, {

                ticketFragment.loadingView.stop()
                AllitemsList.clear()
                AllitemsList.addAll(it.data)
                linearLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                adapter = SelectOrderAdapter(orderValue1,requireContext(),AllitemsList)
                ticketFragment.recyclerview.layoutManager = linearLayoutManager
                ticketFragment.recyclerview.adapter = adapter
                adapter.notifyDataSetChanged()
                ticketFragment.nodatafound.isVisible = adapter.itemCount==0

            }


        ))

    }

}






