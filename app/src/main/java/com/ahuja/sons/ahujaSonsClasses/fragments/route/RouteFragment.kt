package com.ahuja.sons.ahujaSonsClasses.fragments.route

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.RouteItemListAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.autoCompleteAdapter.DeliveryPersonAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryPersonEmployeeModel
import com.ahuja.sons.ahujaSonsClasses.model.RouteListModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderListModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.DialogAssignDeliveryPersonBinding
import com.ahuja.sons.databinding.FragmentRouteBinding
import com.ahuja.sons.globals.Global
import com.github.loadingview.LoadingView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteFragment : Fragment() {
    lateinit var binding: FragmentRouteBinding
    var routeItemListAdapter = RouteItemListAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private const val TAG = "RouteFragment"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRouteListApi()

        val sampleData = listOf(
            LocalRouteData(
                id = "1",
                orderName = "Route 1",
                orderList = mutableListOf(
                    AllOrderListModel.Data(id = 101, CardName = "Order details 101"),
                    AllOrderListModel.Data(id = 102, CardName = "Order details 102")
                )
            ),
            LocalRouteData(
                id = "2",
                orderName = "Route 2",
                orderList = mutableListOf(
                    AllOrderListModel.Data(id = 201, CardName = "Order details 201"),
                    AllOrderListModel.Data(id = 202, CardName = "Order details 202")
                )
            ),
            LocalRouteData(
                id = "3",
                orderName = "Route 3",
                orderList = mutableListOf(
                    AllOrderListModel.Data(id = 301, CardName = "Order details 301"),
                    AllOrderListModel.Data(id = 302, CardName = "Order details 302")
                )
            ),
            LocalRouteData(
                id = "4",
                orderName = "Route 4",
                orderList = mutableListOf(
                    AllOrderListModel.Data(id = 401, CardName = "Order details 401"),
                    AllOrderListModel.Data(id = 402, CardName = "Order details 402")
                )
            ),
            LocalRouteData(
                id = "5",
                orderName = "Route 5",
                orderList = mutableListOf(
                    AllOrderListModel.Data(id = 501, CardName = "Order details 501"),
                    AllOrderListModel.Data(id = 502, CardName = "Order details 502")
                )
            )
        )

        routeItemListAdapter.submitList(sampleData)



        binding.rvRoute.apply {
            adapter = routeItemListAdapter
            layoutManager = LinearLayoutManager(requireActivity())

            routeItemListAdapter.setOnItemClickListener { data, i ->

                openDeliveryPersonDialog(requireActivity())

            }
            routeItemListAdapter.notifyDataSetChanged()
        }

    }


    //todo caling Create Assign api here---
    private fun getRouteListApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("SalesPersonCode", Prefs.getString(Global.Employee_Code, ""))

        val call: Call<RouteListModel> = ApiClient().service.getRouteList(jsonObject1)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(call: Call<RouteListModel?>, response: Response<RouteListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    Log.e("data", response.body()!!.data.toString())
//                    Global.successmessagetoast(requireContext(), "Assign SuccessFully")

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(requireContext(), response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<RouteListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
//                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }



    lateinit var dialogBinding: DialogAssignDeliveryPersonBinding

    private fun openDeliveryPersonDialog(context: Context) {

        val dialog = Dialog(context, R.style.Theme_Dialog)

        val layoutInflater = LayoutInflater.from(context)
        dialogBinding = DialogAssignDeliveryPersonBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = 400
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        callDeliveryPersonApi(dialogBinding.acDeliveryPersonOne, dialogBinding.acDeliveryPersonTwo, dialogBinding.acDeliveryPersonThree)

        dialogBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }


        dialogBinding.btnSave.setOnClickListener {
            dialogBinding.loadingback.visibility = View.VISIBLE
            dialogBinding.loadingView.start()

            val idArrayList = ArrayList<Int>()
            for (order in GlobalClasses.deliveryIDsList) {
                idArrayList.add(order.id.toInt())
            }

            val commaSeparatedIds = idArrayList.joinToString(separator = ",")

            val idStringList = commaSeparatedIds.split(",") as ArrayList

            val idArray = idStringList.map { it.toInt() } as ArrayList

            val vehicleNumber = dialogBinding.edtVehicleNo.text.toString()

            if (Global.isValidVehicleNumber(vehicleNumber)) {
                createAssignApi(dialog, dialogBinding.loadingback, dialogBinding.loadingView, idArray, vehicleNumber)

            } else {
                Global.warningmessagetoast(requireContext(), "Invalid Vehicle Number")
            }


        }


        dialogBinding.tvTitle.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()


    }


    //todo caling Create Assign api here---
    private fun createAssignApi(dialog: Dialog, loadingback: FrameLayout, loadingView: LoadingView, idArray: ArrayList<Int>, vehicleNumber: String) {
        loadingback.visibility = View.VISIBLE
        loadingView.start()

        val jsonArray = JsonArray()
        idArray.forEach { id ->
            jsonArray.add(id)
        }

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("DeliveryNote", jsonArray.toString())
        jsonObject1.addProperty("DeliveryPerson1", deliveryPersonOne)
        jsonObject1.addProperty("DeliveryPerson2", deliveryPersonTwo)
        jsonObject1.addProperty("DeliveryPerson3", deliveryPersonThree)
        jsonObject1.addProperty("VechicleNo", vehicleNumber)
        jsonObject1.addProperty("CreatedBy", Prefs.getString(Global.Employee_Code, ""))

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.updateAssign(jsonObject1)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {
                    loadingback.visibility = View.GONE
                    loadingView.stop()

                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(requireContext(), "Assign SuccessFully")

                    var listData = response.body()!!.data

                    dialog.dismiss()

                } else {
                    loadingback.visibility = View.GONE
                    loadingView.stop()
                    Global.warningmessagetoast(requireContext(), response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                loadingback.visibility = View.GONE
                loadingView.stop()
                Log.e("RouteFragment", "onFailure: "+t.message )
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }


    var deliveryPersonOne = ""
    var deliveryPersonTwo = ""
    var deliveryPersonThree = ""

    //todo calling delivery person api here---

    private fun callDeliveryPersonApi(acDeliveryPersonOne: AutoCompleteTextView, acDeliveryPersonTwo: AutoCompleteTextView, acDeliveryPersonThree: AutoCompleteTextView) {

        val call: Call<DeliveryPersonEmployeeModel> = ApiClient().service.getDeliveryPerson()
        call.enqueue(object : Callback<DeliveryPersonEmployeeModel?> {
            override fun onResponse(call: Call<DeliveryPersonEmployeeModel?>, response: Response<DeliveryPersonEmployeeModel?>) {
                if (response.body()!!.status == 200) {

                    var adapter = DeliveryPersonAdapter(requireContext(), R.layout.drop_down_item_textview ,response.body()!!.data )
                    acDeliveryPersonOne.setAdapter(adapter)

                    acDeliveryPersonOne.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acDeliveryPersonOne.setText(response.body()!!.data[i].SalesEmployeeName)
                            deliveryPersonOne = response.body()!!.data[i].SalesEmployeeCode
                        }
                    }

                    var personAdapter2 = DeliveryPersonAdapter(requireContext(), R.layout.drop_down_item_textview ,response.body()!!.data )
                    acDeliveryPersonTwo.setAdapter(personAdapter2)

                    acDeliveryPersonTwo.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acDeliveryPersonTwo.setText(response.body()!!.data[i].SalesEmployeeName)
                            deliveryPersonTwo = response.body()!!.data[i].SalesEmployeeCode
                        }
                    }

                    var personAdapter3 = DeliveryPersonAdapter(requireContext(), R.layout.drop_down_item_textview ,response.body()!!.data )
                    acDeliveryPersonThree.setAdapter(personAdapter3)

                    acDeliveryPersonThree.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acDeliveryPersonThree.setText(response.body()!!.data[i].SalesEmployeeName)
                            deliveryPersonThree = response.body()!!.data[i].SalesEmployeeCode
                        }
                    }


                } else {

                    Global.warningmessagetoast(requireContext(), response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<DeliveryPersonEmployeeModel?>, t: Throwable) {

                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }




}