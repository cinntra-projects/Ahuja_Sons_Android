package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.ahujaSonsClasses.adapter.PendingItemAfterGroupingAdapter
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.FragmentPendingItemsBinding
import com.ahuja.sons.globals.Global
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PendingItemsFragment(val SapOrderID : String) : Fragment() {

    lateinit var binding : FragmentPendingItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingItemsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var dataList = arrayListOf<String>("Attune Femur123", "Attune Femur1", "Attune Femur2")

        callPendingListApi()

    }


    var pendingItemArrayList = ArrayList<AllItemsForOrderModel.PendingItem>()

    private fun callPendingListApi() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("SapOrderId", SapOrderID)

        binding.loadingview.start()
        binding.loadingback.visibility = View.VISIBLE
        val call: Call<AllItemsForOrderModel> = ApiClient().service.callAllOrderItemList(jsonObject)
        call.enqueue(object : Callback<AllItemsForOrderModel?> {
            override fun onResponse(
                call: Call<AllItemsForOrderModel?>,
                response: Response<AllItemsForOrderModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingview.stop()
                    binding.loadingback.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){
                        if (response.body()!!.data[0].pending_item.size > 0){

                            pendingItemArrayList.clear()
                            pendingItemArrayList.addAll(response.body()!!.data[0].pending_item)

                            val resultList = ArrayList<AllItemsForOrderModel.PendingItem>()

                            resultList.addAll(pendingItemArrayList
                                    .groupBy { it.ItemDescription }
                                    .map { (description, itemsList) ->
                                        val totalQuantity = itemsList.sumOf { it.Quantity.toInt() }
                                        val uniqueMeasureQuantities = itemsList.map { it.U_Size }
                                                .toSet()
                                                .joinToString(", ")

                                        AllItemsForOrderModel.PendingItem(
                                            ItemDescription = description,
                                            Quantity = totalQuantity.toDouble(), // Ensure this is a String
                                            U_Size = uniqueMeasureQuantities
                                        )
                                    }
                            )

                            var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            binding.pendingRecyclerView.layoutManager = linearLayoutManager
//                            val adapter = PendingItemsListAdapter(pendingItemArrayList)
                            val adapter = PendingItemAfterGroupingAdapter(resultList)
                            binding.pendingRecyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()

                        }else{
                            binding.pendingRecyclerView.visibility = View.GONE
                            binding.noDatafound.visibility = View.VISIBLE
                        }
                    }
                    else{
                        binding.pendingRecyclerView.visibility = View.GONE
                        binding.noDatafound.visibility = View.VISIBLE
                    }


                } else if (response.body()!!.status == 404){
                    binding.loadingview.stop()
                    binding.loadingback.visibility = View.GONE
                    binding.pendingRecyclerView.visibility = View.GONE
                    binding.noDatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(requireContext(), response.message().toString());
                }else {
                    binding.pendingRecyclerView.visibility = View.GONE
                    binding.noDatafound.visibility = View.VISIBLE
                    binding.loadingview.stop()
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(requireContext(), response.message().toString());

                }
            }

            override fun onFailure(call: Call<AllItemsForOrderModel?>, t: Throwable) {
                binding.loadingview.stop()
                binding.loadingback.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}