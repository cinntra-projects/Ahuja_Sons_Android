package com.ahuja.sons.ahujaSonsClasses.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.autoCompleteAdapter.NatureErrandsAdapter
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllItemListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.NatureErrandsResponseModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.ActivityAddErrandAcitivtyBinding
import com.ahuja.sons.globals.Global
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddErrandActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddErrandAcitivtyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddErrandAcitivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var orderID = intent.getStringExtra("orderID")

        binding.toolbar.toolbar.setOnClickListener {
            finish()
        }

        binding.toolbar.heading.setText("Errand")


        var listContactP = arrayListOf<String>("Rahul", "Deepanshu", "Arif")
        var adapter = ArrayAdapter<String>(this@AddErrandActivity, R.layout.drop_down_item_textview, listContactP)
//        binding.acContactPerson.setAdapter(adapter)


        callNatureErrandsAPi()


        binding.submitChip.setOnClickListener {

            callCreateErrandsApi(orderID)

        }


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
        if (validation(binding.edtPickUplocation.text.toString().trim(), binding.edtDroplocation.text.toString().trim(), binding.acNatureErrand.text.toString(), binding.acContactPerson.text.toString())){

            binding.loadingView.start()
            binding.loadingback.visibility = View.VISIBLE

            var jsonObject = JsonObject()
            jsonObject.addProperty("OrderRequestID", orderID)
            jsonObject.addProperty("PickupLocation", binding.edtPickUplocation.text.toString())
            jsonObject.addProperty("DropLocation", binding.edtDroplocation.text.toString().trim())
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