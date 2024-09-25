package com.ahuja.sons.ahujaSonsClasses.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.Interface.OnDialogClickListener
import com.ahuja.sons.ahujaSonsClasses.adapter.autoCompleteAdapter.DeliveryPersonAdapter
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.fragments.route.OrderForDeliveryCoordinatorFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.workqueue.WorkQueueFragment
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryPersonEmployeeModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityAhujaSonsMainBinding
import com.ahuja.sons.databinding.DialogAssignDeliveryPersonBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.Global.isValidVehicleNumber
import com.ahuja.sons.globals.Global.validateVehicleNumber
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.github.loadingview.LoadingView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AhujaSonsMainActivity : AppCompatActivity() {

    lateinit var binding: ActivityAhujaSonsMainBinding
    lateinit var navController: NavController
    lateinit var viewModel: MainViewModel

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAhujaSonsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavController()


        setUpViewModel()

        binding.chipAssign.setOnClickListener {
            /*if (GlobalClasses.cartListForOrderRequest.isNotEmpty()) {
                openDeliveryPersonDialog(this)
            } else {
                Global.infomessagetoast(this, "No Order Selected")
            }*/

            if (GlobalClasses.deliveryIDsList.isNotEmpty()) {
                openDeliveryPersonDialog(this)
            } else {
                Global.infomessagetoast(this, "No Delivery Selected")
            }

        }

        val conditionString = "hide_dashboard"

        if (Prefs.getString(Global.Employee_role, "").equals("Delivery Person") || Prefs.getString(Global.Employee_role, "").equals("Surgery Person")) {//            || Prefs.getString(Global.Employee_role, "").equals("Sales Person")
            hideMenuItem(binding.navigationView, R.id.orderFragment)
        }//todo hide bottom nav item as per to role


    }


    private fun hideMenuItem(bottomNavigationView: BottomNavigationView, itemId: Int) {
        bottomNavigationView.menu.findItem(itemId).isVisible = false
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_ahuja_sons) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.navigationView, navController)

    }


    //todo open delivery dialog----
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

            val idArrayList = ArrayList<Int>()
            for (order in GlobalClasses.deliveryIDsList) {
                idArrayList.add(order.id.toInt())
            }
            val commaSeparatedIds = idArrayList.joinToString(separator = ",")


            val orderIDList = ArrayList<String>()
            for (order in GlobalClasses.cartListForOrderRequest) {
                orderIDList.add(order.value.orderId)
            }

            val orderCommaSeparatedIds = orderIDList.joinToString(separator = ",")

         /*   val idStringList = commaSeparatedIds.split(",") as ArrayList

            val idArray = idStringList.map { it.toInt() } as ArrayList*/

            val vehicleNumber = dialogBinding.edtVehicleNo.text.toString()

            if (!vehicleNumber.equals("") && validateVehicleNumber(vehicleNumber)) {

                createAssignApi(dialog, dialogBinding.loadingback, dialogBinding.loadingView, commaSeparatedIds, vehicleNumber, orderCommaSeparatedIds)

            } else {
                Global.warningmessagetoast(this@AhujaSonsMainActivity, "Invalid Vehicle Number or Empty")
            }


        }

        dialogBinding.tvTitle.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()

    }


    //todo calling Create Assign api here---
    private fun createAssignApi(dialog: Dialog, loadingback: FrameLayout, loadingView: LoadingView, idArray: String, vehicleNumber: String, orderCommaSeparatedIds : String) {

        loadingback.visibility = View.VISIBLE
        loadingView.start()

        val jsonArray = JsonArray()
        idArray.forEach { id ->
            jsonArray.add(id)
        }

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("DeliveryNote", idArray)
        jsonObject1.addProperty("DeliveryPerson1", deliveryPersonOne)
        jsonObject1.addProperty("DeliveryPerson2", deliveryPersonTwo)
        jsonObject1.addProperty("DeliveryPerson3", deliveryPersonThree)
        jsonObject1.addProperty("VechicleNo", vehicleNumber)
        jsonObject1.addProperty("OrderID", orderCommaSeparatedIds)
        jsonObject1.addProperty("CreatedBy", Prefs.getString(Global.Employee_Code, ""))

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.createAssign(jsonObject1)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {

                    loadingback.visibility = View.GONE
                    loadingView.stop()

                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@AhujaSonsMainActivity, "Assign SuccessFully")

                    GlobalClasses.deliveryIDsList.clear()

                    dialog.dismiss()

                    val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_ahuja_sons)
                        ?.childFragmentManager?.fragments?.find { it is WorkQueueFragment } as? OnDialogClickListener

                    fragment?.onButtonClick()

                    binding.cardAssignButton.visibility = View.GONE


                } else {
                    loadingback.visibility = View.GONE
                    loadingView.stop()
                    Global.warningmessagetoast(this@AhujaSonsMainActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                loadingback.visibility = View.GONE
                loadingView.stop()
                Log.e("AhujaMainActivity", "onFailure: "+t.message )
                Toast.makeText(this@AhujaSonsMainActivity, t.message, Toast.LENGTH_SHORT).show()
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

                    var adapter = DeliveryPersonAdapter(this@AhujaSonsMainActivity, R.layout.drop_down_item_textview ,response.body()!!.data )
                    acDeliveryPersonOne.setAdapter(adapter)

                    acDeliveryPersonOne.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acDeliveryPersonOne.setText(response.body()!!.data[i].SalesEmployeeName)
                            deliveryPersonOne = response.body()!!.data[i].SalesEmployeeCode
                        }
                    }

                    var personAdapter2 = DeliveryPersonAdapter(this@AhujaSonsMainActivity, R.layout.drop_down_item_textview ,response.body()!!.data )
                    acDeliveryPersonTwo.setAdapter(personAdapter2)

                    acDeliveryPersonTwo.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acDeliveryPersonTwo.setText(response.body()!!.data[i].SalesEmployeeName)
                            deliveryPersonTwo = response.body()!!.data[i].SalesEmployeeCode
                        }
                    }

                    var personAdapter3 = DeliveryPersonAdapter(this@AhujaSonsMainActivity, R.layout.drop_down_item_textview ,response.body()!!.data )
                    acDeliveryPersonThree.setAdapter(personAdapter3)

                    acDeliveryPersonThree.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acDeliveryPersonThree.setText(response.body()!!.data[i].SalesEmployeeName)
                            deliveryPersonThree = response.body()!!.data[i].SalesEmployeeCode
                        }
                    }


                } else {
                    Global.warningmessagetoast(this@AhujaSonsMainActivity, response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<DeliveryPersonEmployeeModel?>, t: Throwable) {

                Toast.makeText(this@AhujaSonsMainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }



}