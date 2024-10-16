package com.ahuja.sons.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.AccountDetailActivity
import com.ahuja.sons.activity.ParticularorderDetailsActivity
import com.ahuja.sons.adapter.CustomerOrderAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.FragmentCustomerOrderBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.newapimodel.DataParticularCustomerOrder
import com.ahuja.sons.viewmodel.MainViewModel

class CustomerOrderFragment(val accountdata: AccountBpData) : Fragment() ,  AccountDetailActivity.MyFragmentCustomerListener{

    private lateinit var ticketbiding: FragmentCustomerOrderBinding
    lateinit var adapter: CustomerOrderAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ticketbiding = FragmentCustomerOrderBinding.inflate(layoutInflater)
        viewModel = (activity as AccountDetailActivity).viewModel

        ticketbiding.loadingView.start()
        if (Global.checkForInternet(requireContext())) {
            var data = HashMap<String, String>()
            data["CardCode"] = accountdata.CardCode
            viewModel.getCustomerMvvmOrderAllOrderList(data)
            subscribeToObserver()
        }




        return ticketbiding.root
    }


    var AllitemsList = ArrayList<DataParticularCustomerOrder>()
//    private fun getOrderlist() {
//        val data = AccountBpData(
//            CardCode = accountdata.CardCode
//        )
//
//
//        val call: Call<OrderDataResponse> =
//            ApiClient().service.getCustomerOrderAllOrderList(data)
//        call.enqueue(object : Callback<OrderDataResponse> {
//            override fun onResponse(
//                call: Call<OrderDataResponse>,
//                response: Response<OrderDataResponse>
//            ) {
//                if (response.code() == 200) {
//
//                    if (response.body()?.data != null) {
//                        AllitemsList.clear()
//                        AllitemsList.addAll(response.body()!!.data)
//                        linearLayoutManager =
//                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//                        adapter = CustomerOrderAdapter(AllitemsList)
//                        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
//                        ticketbiding.productRecyclerView.adapter = adapter
//                        adapter.notifyDataSetChanged()
//
//                        if (adapter.itemCount == 0) {
//                            ticketbiding.nodatafound.visibility = View.VISIBLE
//                        } else {
//                            ticketbiding.nodatafound.visibility = View.GONE
//
//                        }
//
//                        Log.e("data", response.body()?.data.toString())
//                    }
//
//
//                } else {
//                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
//
//                }
//                ticketbiding.loadingView.stop()
//                ticketbiding.loadingback.visibility = View.GONE
//            }
//
//            override fun onFailure(call: Call<OrderDataResponse>, t: Throwable) {
//                ticketbiding.loadingView.stop()
//                ticketbiding.loadingback.visibility = View.GONE
//
//                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
//
//            }
//        })
//    }


    private fun subscribeToObserver() {
        viewModel.getCustomerParticularOrder.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {

                ticketbiding.loadingView.visibility = View.GONE
//                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {

                ticketbiding.loadingView.visibility = View.VISIBLE
            }, {

                ticketbiding.loadingView.visibility = View.GONE
                AllitemsList.clear()
                AllitemsList.addAll(it.data)
                linearLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                adapter = CustomerOrderAdapter(AllitemsList)
                adapter.setOnItemClickListener {order->
                   // Toast.makeText(requireContext(), "${order.id}", Toast.LENGTH_SHORT).show()
                  Log.e("TAG", "onBindViewHolder: ")
                    val intent = Intent(context, ParticularorderDetailsActivity::class.java)
                    //  intent.putExtra("order",AllitemsList[position])

                    intent.putExtra("OrderId",order.id.toString())
                    activity?.startActivity(intent)
                }

                ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
                ticketbiding.productRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()

                if (adapter.itemCount == 0) {
                    ticketbiding.nodatafound.visibility = View.VISIBLE
                } else {
                    ticketbiding.nodatafound.visibility = View.GONE

                }

                Log.e("data", it.data.toString())

            }


        ))

    }


    override fun onDataPassedCustomer(startDate: String?, endDate: String?, pos: Int?) {
        Log.e("TAG", "CustomerOrderFragment_: ")
    }


}


