package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.autoCompleteAdapter.NatureErrandsAdapter
import com.ahuja.sons.ahujaSonsClasses.model.AllErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllItemListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.NatureErrandsResponseModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.ActivityUpdateErrandsBinding
import com.ahuja.sons.globals.Global
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateErrandsActivity : AppCompatActivity() {

    lateinit var binding : ActivityUpdateErrandsBinding

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


        var listContactP = arrayListOf<String>("Rahul", "Deepanshu", "Arif")
        var adapter = ArrayAdapter<String>(this@UpdateErrandsActivity, R.layout.drop_down_item_textview, listContactP)
//        binding.acContactPerson.setAdapter(adapter)


        callNatureErrandsAPi()

        callErrandsAllList(orderID, errandPos)

        binding.submitChip.setOnClickListener {

            callUpdateErrandsApi(orderID)

        }


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

        binding.edtPickUplocation.setText(data.PickupLocation)
        binding.edtDroplocation.setText(data.DropLocation)
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
        if (validation(binding.edtPickUplocation.text.toString().trim(), binding.edtDroplocation.text.toString().trim(), binding.acNatureErrand.text.toString(), binding.acContactPerson.text.toString())){

            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", errandsListModels[errandPos].id)
            jsonObject.addProperty("OrderRequestID", orderID)
            jsonObject.addProperty("PickupLocation", binding.edtPickUplocation.text.toString())
            jsonObject.addProperty("DropLocation", binding.edtDroplocation.text.toString().trim())
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
            binding.edtPickUplocation.requestFocus()
            binding.edtPickUplocation.setError(" Pick Up Location is Required")
            return false
        }

        else if (edtDroplocation.isEmpty()) {
            binding.edtDroplocation.requestFocus()
            binding.edtDroplocation.setError(" Drop Location is Required")
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