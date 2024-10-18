package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.autoCompleteAdapter.NatureErrandsAdapter
import com.ahuja.sons.ahujaSonsClasses.model.AllErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllItemListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.NatureErrandsResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityUpdateErrandsBinding
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

class UpdateErrandsActivity : AppCompatActivity() {

    lateinit var binding : ActivityUpdateErrandsBinding
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


    companion object{
        private const val TAG = "UpdateErrandsActivity"
    }

    var errandPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateErrandsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var orderID = intent.getIntExtra("OrderID", 0)
        errandPos = intent.getIntExtra("pos", 0)

        binding.toolbar.toolbar.setOnClickListener {
            finish()
        }

        binding.toolbar.heading.setText("Update Errands")
        setUpViewModel()

        var listContactP = arrayListOf<String>("Rahul", "Deepanshu", "Arif")
        var adapter = ArrayAdapter<String>(this@UpdateErrandsActivity, R.layout.drop_down_item_textview, listContactP)
//        binding.acContactPerson.setAdapter(adapter)

        viewModel.getBPList()
        bindPickUpObserver()


        callNatureErrandsAPi()

        callErrandsAllList(orderID, errandPos)

        binding.submitChip.setOnClickListener {

            callUpdateErrandsApi(orderID)

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



    var errandsListModels = ArrayList<AllErrandsListModel.Data>()

    private fun callErrandsAllList(orderID: Int, pos: Int) {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", orderID)

        binding.loadingView.start()
        binding.loadingback.visibility = View.VISIBLE
        val call: Call<AllErrandsListModel> = ApiClient().service.getErrandsList(jsonObject)
        call.enqueue(object : Callback<AllErrandsListModel?> {
            override fun onResponse(call: Call<AllErrandsListModel?>, response: Response<AllErrandsListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){

                        errandsListModels.clear()
                        errandsListModels.addAll(response.body()!!.data)

                        setDefaultData(errandsListModels[pos])

                    }

                } else {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(this@UpdateErrandsActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<AllErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingback.visibility = View.GONE
                Toast.makeText(this@UpdateErrandsActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setDefaultData(data: AllErrandsListModel.Data) {

        PickUpName = data.PickupLocation
        binding.acPickUpLocation.setText(data.PickupLocation)
        DropName = data.DropLocation
        binding.acDropLocation.setText(data.DropLocation)
        binding.acNatureErrand.setText(data.NatureOfErrands.Name)
        binding.acContactPerson.setText(data.ContactPerson)
        binding.edRemarks.setText(data.Remark)

        errandID = data.NatureOfErrands.id

        var listContactP = arrayListOf<String>("Rahul", "Deepanshu", "Arif")
        var adapter = ArrayAdapter<String>(this@UpdateErrandsActivity, R.layout.drop_down_item_textview, listContactP)
//        binding.acContactPerson.setAdapter(adapter)


    }


    var errandID = 0


    //todo nature errands api here---
    private fun callNatureErrandsAPi(){

        val call: Call<NatureErrandsResponseModel> = ApiClient().service.getNatureErrands()
        call.enqueue(object : Callback<NatureErrandsResponseModel?> {
            override fun onResponse(call: Call<NatureErrandsResponseModel?>, response: Response<NatureErrandsResponseModel?>) {
                if (response.body()!!.status == 200) {

                    var adapter = NatureErrandsAdapter(this@UpdateErrandsActivity, R.layout.drop_down_item_textview ,response.body()!!.data )
                    binding.acNatureErrand.setAdapter(adapter)

                    binding.acNatureErrand.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            binding.acNatureErrand.setText(response.body()!!.data[i].Name)
                            errandID = response.body()!!.data[i].id
                        }
                    }

                } else {

                    Global.warningmessagetoast(this@UpdateErrandsActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<NatureErrandsResponseModel?>, t: Throwable) {

                Toast.makeText(this@UpdateErrandsActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //todo calling create errands api here--
    private fun callUpdateErrandsApi(orderID: Int) {
        if (validation(binding.acPickUpLocation.text.toString().trim(), binding.acDropLocation.text.toString().trim(), binding.acNatureErrand.text.toString(), binding.acContactPerson.text.toString())){

            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", errandsListModels[errandPos].id)
            jsonObject.addProperty("OrderRequestID", orderID)
            jsonObject.addProperty("PickupLocation", binding.acPickUpLocation.text.toString())
            jsonObject.addProperty("DropLocation", binding.acDropLocation.text.toString().trim())
            jsonObject.addProperty("NatureOfErrands", errandID)
            jsonObject.addProperty("CreatedBy", Prefs.getString(Global.Employee_Code, ""))
            jsonObject.addProperty("ContactPerson", binding.acContactPerson.text.toString().trim())
            jsonObject.addProperty("Remark ", binding.edRemarks.text.toString())
            jsonObject.addProperty("CreateDate ", Global.getTodayDateDashFormatReverse())
            jsonObject.addProperty("CreateTime ", Global.getfullformatCurrentTime())
            jsonObject.addProperty("UpdateDate ", Global.getTodayDateDashFormatReverse())
            jsonObject.addProperty("UpdateTime ", Global.getfullformatCurrentTime())

            val call: Call<AllItemListResponseModel> = ApiClient().service.updateErrands(jsonObject)
            call.enqueue(object : Callback<AllItemListResponseModel?> {
                override fun onResponse(call: Call<AllItemListResponseModel?>, response: Response<AllItemListResponseModel?>) {
                    if (response.body()!!.status == 200) {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        Log.e("data", response.body()!!.data.toString())
                        Global.successmessagetoast(this@UpdateErrandsActivity, response.message().toString());

                        onBackPressed()
                        finish()

                    } else {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        Global.warningmessagetoast(this@UpdateErrandsActivity, response.body()!!.message.toString());

                    }
                }

                override fun onFailure(call: Call<AllItemListResponseModel?>, t: Throwable) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Toast.makeText(this@UpdateErrandsActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        }

    }


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