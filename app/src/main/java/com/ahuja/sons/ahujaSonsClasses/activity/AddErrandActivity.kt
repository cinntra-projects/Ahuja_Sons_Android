package com.ahuja.sons.ahujaSonsClasses.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.autoCompleteAdapter.NatureErrandsAdapter
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllItemListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.NatureErrandsResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityAddErrandAcitivtyBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddErrandActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddErrandAcitivtyBinding
    var PickUpName = ""
    var PickUpCode = ""
    var DropName = ""
    var DropCode = ""
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
        binding = ActivityAddErrandAcitivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        var orderID = intent.getStringExtra("orderID")

        binding.toolbar.toolbar.setOnClickListener {
            finish()
        }

        binding.toolbar.heading.setText("Errand")


        var listContactP = arrayListOf<String>("Rahul", "Deepanshu", "Arif")
        var adapter = ArrayAdapter<String>(this@AddErrandActivity, R.layout.drop_down_item_textview, listContactP)
//        binding.acContactPerson.setAdapter(adapter)


        viewModel.getBPList()
        bindPickUpObserver()


        callNatureErrandsAPi()


        binding.submitChip.setOnClickListener {

            callCreateErrandsApi(orderID)

        }


    }


    var AllitemsList = ArrayList<AccountBpData>()

    //todo bind observer...
    private fun bindPickUpObserver() {
        viewModel.businessPartnerList.observe(this, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(this, it)
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
            },
            onLoading = {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()

                    AllitemsList.clear()
                    var itemsList = filterList(response.data)
                    AllitemsList.addAll(itemsList)

                    var itemNames = itemsList.map { it.CardName }
                    val cardCodeName = itemsList.map { it.CardCode }

                    //TODO set Pick Up Location Data---
                    val adapter = ArrayAdapter(this, com.ahuja.sons.R.layout.drop_down_item_textview, itemNames)
                    binding.acPickUpLocation.setAdapter(adapter)

                    // Handle bill to address dropdown item selection
                    binding.acPickUpLocation.setOnItemClickListener { parent, _, position, _ ->
                        try {
                            val hospitalName = parent.getItemAtPosition(position) as String
                            PickUpName = hospitalName

                            val pos = Global.getHospitalPos(AllitemsList, hospitalName)
                            PickUpCode = AllitemsList[pos].CardCode

                            if (hospitalName.isEmpty()) {
                                binding.pickUpRecyclerViewLayout.visibility = View.GONE
                                binding.rvPickUpList.visibility = View.GONE
                            } else {
                                binding.pickUpRecyclerViewLayout.visibility = View.VISIBLE
                                binding.rvPickUpList.visibility = View.VISIBLE

                            }

                            if (hospitalName.isNotEmpty()) {
                                adapter.notifyDataSetChanged()
                                binding.acPickUpLocation.setText(hospitalName, false)
                                binding.acPickUpLocation.setSelection(hospitalName.length)

                            } else {
                                PickUpName = ""
                                PickUpCode = ""
                                binding.acPickUpLocation.setText("")
                            }

                        } catch (e: Exception) {
                            Log.e("catch", "onItemClick: ${e.message}")
                            e.printStackTrace()
                        }
                    }


                    //TODO set Drop Location Data---

                    val dropAdapter = ArrayAdapter(this, com.ahuja.sons.R.layout.drop_down_item_textview, itemNames)
                    binding.acDropLocation.setAdapter(dropAdapter)

                    // Handle bill to address dropdown item selection
                    binding.acDropLocation.setOnItemClickListener { parent, _, position, _ ->
                        try {
                            val hospitalName = parent.getItemAtPosition(position) as String
                            DropName = hospitalName

                            val pos = Global.getHospitalPos(AllitemsList, hospitalName)
                            DropCode = AllitemsList[pos].CardCode

                            if (hospitalName.isEmpty()) {
                                binding.dropLocRecyclerViewLayout.visibility = View.GONE
                                binding.rvDropLocationList.visibility = View.GONE
                            } else {
                                binding.dropLocRecyclerViewLayout.visibility = View.VISIBLE
                                binding.rvDropLocationList.visibility = View.VISIBLE

                            }

                            if (hospitalName.isNotEmpty()) {
                                adapter.notifyDataSetChanged()
                                binding.acDropLocation.setText(hospitalName, false)
                                binding.acDropLocation.setSelection(hospitalName.length)

                            } else {
                                DropName = ""
                                DropCode = ""
                                binding.acDropLocation.setText("")
                            }

                        } catch (e: Exception) {
                            Log.e("catch", "onItemClick: ${e.message}")
                            e.printStackTrace()
                        }

                    }

                }


            }

        ))


    }


    private fun filterList(value: List<AccountBpData>): List<AccountBpData> {
        val tempList = mutableListOf<AccountBpData>()
        for (customer in value) {
            if (customer.CardName != "foo") {
                tempList.add(customer)
            }
        }
        return tempList
    }


    var errandID = 0


    //todo nature errands api here---
    private fun callNatureErrandsAPi(){

        val call: Call<NatureErrandsResponseModel> = ApiClient().service.getNatureErrands()
        call.enqueue(object : Callback<NatureErrandsResponseModel?> {
            override fun onResponse(call: Call<NatureErrandsResponseModel?>, response: Response<NatureErrandsResponseModel?>) {
                if (response.body()!!.status == 200) {

                    var adapter = NatureErrandsAdapter(this@AddErrandActivity, R.layout.drop_down_item_textview ,response.body()!!.data )
                    binding.acNatureErrand.setAdapter(adapter)

                    binding.acNatureErrand.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            binding.acNatureErrand.setText(response.body()!!.data[i].Name)
                            errandID = response.body()!!.data[i].id
                        }
                    }

                } else {

                    Global.warningmessagetoast(this@AddErrandActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<NatureErrandsResponseModel?>, t: Throwable) {

                Toast.makeText(this@AddErrandActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //todo calling create errands api here--
    private fun callCreateErrandsApi(orderID: String?) {
        if (validation(binding.acPickUpLocation.text.toString().trim(), binding.acDropLocation.text.toString().trim(), binding.acNatureErrand.text.toString(), binding.acContactPerson.text.toString())){

            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE

            var jsonObject = JsonObject()
            jsonObject.addProperty("OrderRequestID", orderID)
            jsonObject.addProperty("PickupLocation", PickUpName)
            jsonObject.addProperty("DropLocation", DropName)
            jsonObject.addProperty("NatureOfErrands", errandID)
            jsonObject.addProperty("CreatedBy", Prefs.getString(Global.Employee_Code, ""))
            jsonObject.addProperty("ContactPerson", binding.acContactPerson.text.toString().trim())
            jsonObject.addProperty("Remark ", binding.edRemarks.text.toString())

            val call: Call<AllItemListResponseModel> = ApiClient().service.createErrands(jsonObject)
            call.enqueue(object : Callback<AllItemListResponseModel?> {
                override fun onResponse(call: Call<AllItemListResponseModel?>, response: Response<AllItemListResponseModel?>) {
                    if (response.body()!!.status == 200) {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        Log.e("data", response.body()!!.data.toString())
                        Global.successmessagetoast(this@AddErrandActivity, response.message().toString());

                        onBackPressed()
                        finish()

                    } else {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        Global.warningmessagetoast(this@AddErrandActivity, response.body()!!.message.toString());

                    }
                }

                override fun onFailure(call: Call<AllItemListResponseModel?>, t: Throwable) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Toast.makeText(this@AddErrandActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        }

    }



/*
    override fun onBackPressed() {
        val intent = Intent()
        // Send the tag back to the previous activity
        intent.putExtra("TAG", "AddErrands") // Put the tag in the intent
        setResult(Activity.RESULT_OK, intent) // Set the result with OK status
        finish() // Finish this activity
    }
*/

    fun validation(edtPickUplocation : String, edtDroplocation : String, acNatureErrand : String, acContactPerson : String) : Boolean{
        if (edtPickUplocation.isEmpty()) {
            binding.acPickUpLocation.requestFocus()
            binding.acPickUpLocation.setError(" Pick Up Location is Required")
            return false
        }

        else if (edtDroplocation.isEmpty()) {
            binding.acDropLocation.requestFocus()
            binding.acDropLocation.setError(" Drop Location is Required")
            return false
        }

        else if (acNatureErrand.isEmpty()) {
            binding.acNatureErrand.requestFocus()
            binding.acNatureErrand.setError("Nature Errands is Required")
            return false
        }

        else if (acContactPerson.isEmpty()) {
            binding.acContactPerson.requestFocus()
            binding.acContactPerson.setError("Contact Person is Required")
            return false
        }

        return true
    }

}