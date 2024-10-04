package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.AllItemListAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.DeliveryItemsListAdapter
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.FragmentAllItemListBinding
import com.ahuja.sons.databinding.FragmentDeliveryItemsBinding
import com.ahuja.sons.globals.Global
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllItemListFragment(val SapOrderId : String) : Fragment() {

    lateinit var binding : FragmentAllItemListBinding

    companion object{
        private const val TAG = "AllItemListFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllItemListBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        callAllItemListApi()

    }


    var documentline_gl = ArrayList<AllItemsForOrderModel.AllItem>()

    private fun callAllItemListApi() {
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
                if (response.code() == 200){
                    if (response.body()!!.status == 200) {
                        binding.loadingview.stop()
                        binding.loadingback.visibility = View.GONE
                        Log.e("data", response.body()!!.data.toString())
                        if (response.body()!!.data.size > 0){
                            if (response.body()!!.data[0].all_item.size > 0){
                                documentline_gl.clear()
                                documentline_gl.addAll(response.body()!!.data[0].all_item)

                                try {
                                    var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    binding.allItemListRecyclerView.layoutManager = linearLayoutManager
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                val adapter = AllItemListAdapter(documentline_gl)
                                binding.allItemListRecyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()
                            }else{
                                binding.allItemListRecyclerView.visibility = View.GONE
                                binding.noDatafound.visibility = View.VISIBLE
                            }

                        }else{
                            binding.allItemListRecyclerView.visibility = View.GONE
                            binding.noDatafound.visibility = View.VISIBLE
                        }

                    }

                    else if (response.body()!!.status == 404){
                        binding.loadingview.stop()
                        binding.loadingback.visibility = View.GONE
                        binding.allItemListRecyclerView.visibility = View.GONE
                        binding.noDatafound.visibility = View.VISIBLE
                        Global.warningmessagetoast(requireContext(), response.message().toString());
                    }else {

                        binding.allItemListRecyclerView.visibility = View.GONE
                        binding.noDatafound.visibility = View.VISIBLE
                        binding.loadingview.stop()
                        binding.loadingback.visibility = View.GONE
                        Global.warningmessagetoast(requireContext(), response.message().toString());

                    }
                }else if (response.code() == 500){
                    Log.e(TAG, "onResponse: "+response.message() )
                }else{
                    Log.e(TAG, "onResponse: "+response.message() )
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