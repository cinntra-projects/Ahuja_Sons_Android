package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.DeliveryDetailItemAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.DeliveryItemsListAdapter
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.DeliveryDetailItemListModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.FragmentDeliveryItemsBinding
import com.ahuja.sons.globals.Global
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryItemsFragment(val SapOrderId : String, var DeliveryID : Int, var flag : String) : Fragment() {

    lateinit var binding : FragmentDeliveryItemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeliveryItemsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (flag.equals("FromDeliveryIdSelect")){
            callAllItemListApi()
        }else {
            callDeliveryListApi()
        }

    }


    //todo for all roles-
    var documentline_gl = ArrayList<AllItemsForOrderModel.DeliveryItem>()

    private fun callDeliveryListApi() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("SapOrderId", SapOrderId)

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

                        if (response.body()!!.data[0].delivery_item.size > 0){
                            documentline_gl.clear()
                            documentline_gl.addAll(response.body()!!.data[0].delivery_item)

                            //todo
                            val resultList = ArrayList<AllItemsForOrderModel.DeliveryItem>()

                            resultList.addAll(documentline_gl
                                .groupBy { it.ItemDescription }
                                .map { (description, itemsList) ->
                                    val totalQuantity = itemsList.sumOf { it.Quantity.toInt() }
                                    val uniqueMeasureQuantities = itemsList.map { it.U_Size }
                                        .toSet()
                                        .joinToString(", ")

                                    AllItemsForOrderModel.DeliveryItem(
                                        ItemDescription = description,
                                        Quantity = totalQuantity, // Ensure this is a String
                                        U_Size = uniqueMeasureQuantities
                                    )
                                }
                            )

                            var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            binding.deliveryRecyclerView.layoutManager = linearLayoutManager
                            val adapter = DeliveryItemsListAdapter(resultList)
                            binding.deliveryRecyclerView.adapter = adapter

                        }else{
                            binding.deliveryRecyclerView.visibility = View.GONE
                            binding.noDatafound.visibility = View.VISIBLE
                        }


                    }else{
                        binding.deliveryRecyclerView.visibility = View.GONE
                        binding.noDatafound.visibility = View.VISIBLE
                    }

                }  else if (response.body()!!.status == 404){
                    binding.loadingview.stop()
                    binding.loadingback.visibility = View.GONE
                    binding.deliveryRecyclerView.visibility = View.GONE
                    binding.noDatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(requireContext(), response.message().toString());
                }else {
                    binding.deliveryRecyclerView.visibility = View.GONE
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


    var deliveryDetailline_gl = ArrayList<DeliveryDetailItemListModel.Data>()

    //todo calling when inspection delivery id selcted
    private fun callAllItemListApi() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("delivery_id", DeliveryID)

        val call: Call<DeliveryDetailItemListModel> = ApiClient().service.callDeliveryItem(jsonObject)
        call.enqueue(object : Callback<DeliveryDetailItemListModel?> {
            override fun onResponse(
                call: Call<DeliveryDetailItemListModel?>,
                response: Response<DeliveryDetailItemListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingview.stop()
                    binding.loadingback.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){

                        if (response.body()!!.data.size > 0){
                            deliveryDetailline_gl.clear()
                            deliveryDetailline_gl.addAll(response.body()!!.data)

                            var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            binding.deliveryRecyclerView.layoutManager = linearLayoutManager
                            val adapter = DeliveryDetailItemAdapter(deliveryDetailline_gl)
                            binding.deliveryRecyclerView.adapter = adapter

                        }else{
                            binding.deliveryRecyclerView.visibility = View.GONE
                            binding.noDatafound.visibility = View.VISIBLE
                        }


                    }else{
                        binding.deliveryRecyclerView.visibility = View.GONE
                        binding.noDatafound.visibility = View.VISIBLE
                    }

                }  else if (response.body()!!.status == 404){
                    binding.loadingview.stop()
                    binding.loadingback.visibility = View.GONE
                    binding.deliveryRecyclerView.visibility = View.GONE
                    binding.noDatafound.visibility = View.VISIBLE
                    Global.warningmessagetoast(requireContext(), response.message().toString());
                }else {
                    binding.deliveryRecyclerView.visibility = View.GONE
                    binding.noDatafound.visibility = View.VISIBLE
                    binding.loadingview.stop()
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(requireContext(), response.message().toString());

                }
            }

            override fun onFailure(call: Call<DeliveryDetailItemListModel?>, t: Throwable) {
                binding.loadingview.stop()
                binding.loadingback.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


}