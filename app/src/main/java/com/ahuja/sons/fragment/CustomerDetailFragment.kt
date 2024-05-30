package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.activity.AccountDetailActivity
import com.ahuja.sons.adapter.TicketAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.CustomerDetailsBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBPResponse
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import com.ahuja.sons.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CustomerDetailFragment(val accountdata: AccountBpData) : Fragment() ,  AccountDetailActivity.MyFragmentCustomerListener {

    private lateinit var ticketbiding : CustomerDetailsBinding
    lateinit var adapter: TicketAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = CustomerDetailsBinding.inflate(layoutInflater)

        viewModel = (activity as AccountDetailActivity).viewModel
        setData()
        return ticketbiding.root
    }



    private fun setData() {
        ticketbiding.contacnameValue.text = accountdata.ContactPerson
        ticketbiding.tvAccountName.text=accountdata.CardName
        if(accountdata.EmailAddress.isNotEmpty())
        ticketbiding.emailValue.text = accountdata.EmailAddress
        else
            ticketbiding.emailValue.text = "N/A"

        if(accountdata.Website.isNotEmpty())
            ticketbiding.websiteVal.text = accountdata.Website
        else
            ticketbiding.websiteVal.text ="N/A"

        if(accountdata.Phone1.isNotEmpty())
            ticketbiding.phoneNumber.text = accountdata.Phone1
        else
            ticketbiding.phoneNumber.text = "N/A"

        if(accountdata.zone.isNotEmpty())
            ticketbiding.zone.text = accountdata.zone
        else
            ticketbiding.zone.text = "N/A"

        if (accountdata.Industry.isNotEmpty())
            ticketbiding.tvIndustry.text = accountdata.Industry
        else
            ticketbiding.tvIndustry.text = "NA"

        if (accountdata.U_TYPE.isNotEmpty())
            ticketbiding.tvType.text = accountdata.U_TYPE
        else
            ticketbiding.tvType.text = "NA"

        if (accountdata.U_Landline.isNotEmpty())
            ticketbiding.tvLandlineNo.text = accountdata.U_Landline
        else
            ticketbiding.tvLandlineNo.text = "NA"

        if (accountdata.PayTermsGrpDetails.isNotEmpty()){
            ticketbiding.tvPaymentTerm.text = accountdata.PayTermsGrpDetails[0].PaymentTermsGroupName
        }else{
            ticketbiding.tvPaymentTerm.text = "NA"
        }

        if (accountdata.U_PARENTACC != ""){
            ticketbiding.tvParentAccount.text = accountdata.U_PARENTACC
        }else{
            ticketbiding.tvParentAccount.text = "NA"
        }

        if (accountdata.U_INVNO != ""){
            ticketbiding.gstnumber.text = accountdata.U_INVNO
        }else{
            ticketbiding.gstnumber.text = "NA"
        }

        if (accountdata.SalesPersonDetails.isNotEmpty()){
            ticketbiding.tvServiceEmployee.text = accountdata.SalesPersonDetails[0].SalesEmployeeName
        }
        else{
            ticketbiding.tvServiceEmployee.text = "NA"
        }

        if (accountdata.Notes.isNotEmpty()){
            ticketbiding.tvRemarks.text = accountdata.Notes
        }
        else{
            ticketbiding.tvRemarks.text = "NA"
        }

        if (accountdata.ContactEmployees.isNotEmpty()){
            ticketbiding.tvContactEmail.text = accountdata.ContactEmployees[0].E_Mail
        }
        else{
            ticketbiding.tvContactEmail.text = "NA"
        }

        if (accountdata.ContactEmployees.isNotEmpty()){
            ticketbiding.tvContactNumber.text = accountdata.ContactEmployees[0].MobilePhone
        }
        else{
            ticketbiding.tvContactNumber.text = "NA"
        }

        if (accountdata.ContactEmployees.isNotEmpty()){
            ticketbiding.tvContactDesignation.text = accountdata.ContactEmployees[0].Position
        }
        else{
            ticketbiding.tvContactDesignation.text = "NA"
        }

    }


    override fun onResume() {
        super.onResume()

        Log.e(TAG, "CustomerDetailFragment_onResume: ")
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val startDate = Prefs.getString(Global.SpinnerAddressType, "")
            var pos = Prefs.getString(Global.SpinnerBranchId, "")
            var jsonObject1 = JsonObject()
            jsonObject1.addProperty("BPCode", startDate)
            viewModel.getBranchAllList(jsonObject1)

            if (pos.isNotEmpty()){
                bindObserverForOneResumeFunctionBranch(pos)
            }

        }


    }

    var branchAllList1 = ArrayList<BranchAllListResponseModel.DataXXX>()


    private fun bindObserverForOneResumeFunctionBranch(pos: String) {
        viewModel.branchAllList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    branchAllList1.clear()
                    branchAllList1.addAll(it.data)

                    var position = pos.toInt()
                    setAddressDefaultData(branchAllList1[position])

                }else {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


    //todo set address data as per branch name--
    private fun setAddressDefaultData(modelData: BranchAllListResponseModel.DataXXX) {
        ticketbiding.billingAddressLayout.visibility = View.VISIBLE
        if(modelData.AddressName.isNotEmpty())
            ticketbiding.tvBillingName.text = modelData.AddressName
        else
            ticketbiding.tvBillingName.text = "N/A"

        if(modelData.Street.isNotEmpty())
            ticketbiding.tvBillingAddress.text = modelData.Street
        else
            ticketbiding.tvBillingAddress.text = "N/A"

        if(modelData.City.isNotEmpty())
            ticketbiding.tvCity.text = modelData.City
        else
            ticketbiding.tvCity.text = "N/A"

        if(modelData.ZipCode.isNotEmpty())
            ticketbiding.tvZipcode.text = modelData.ZipCode
        else
            ticketbiding.tvZipcode.text = "N/A"

        if(modelData.U_COUNTRY.isNotEmpty())
            ticketbiding.tvCountry.text = modelData.U_COUNTRY
        else
            ticketbiding.tvCountry.text = "N/A"

        if(modelData.U_STATE.isNotEmpty())
            ticketbiding.tvState.text = modelData.U_STATE
        else
            ticketbiding.tvState.text = "N/A"

        if(modelData.U_SHPTYP.isNotEmpty())
            ticketbiding.tvShippingTyper.text = modelData.U_SHPTYP
        else
            ticketbiding.tvShippingTyper.text = "N/A"

        if(modelData.Block.isNotEmpty())
            ticketbiding.tvRemarks.text = modelData.Block
        else
            ticketbiding.tvRemarks.text = "N/A"


    }


    companion object{
        private const val TAG = "CustomerDetailFragment"
    }

    override fun onDataPassedCustomer(startDate: String?, endDate: String?, pos: Int?) {
        Log.e(TAG, "onDataPassedCustomer: CustomerDetailFragment", )
//        callCustomerOneApi()

        //todo calling branch api list here---
        var position = pos!!.toInt()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("BPCode", endDate)
        viewModel.getBranchAllList(jsonObject1)
        bindBranchListObserver(position)
    }


    var branchAllList = ArrayList<BranchAllListResponseModel.DataXXX>()
    //todo branch observer---
    private fun bindBranchListObserver(pos: Int?) {
        viewModel.branchAllList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    branchAllList.clear()
                    branchAllList.addAll(it.data)

                    setAddressDefaultData(branchAllList[pos!!])

                }else {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }




    //todo calling customer one api here--
    private fun callCustomerOneApi(){
        ticketbiding.progressBarLoader.visibility = View.VISIBLE
        var jsonObject : JsonObject = JsonObject()
        jsonObject.addProperty("CardCode", accountdata.CardCode)
        val call: Call<AccountBPResponse> = ApiClient().service.getCustomerOneApi(jsonObject)
        call.enqueue(object : Callback<AccountBPResponse> {
            override fun onResponse(call: Call<AccountBPResponse>, response: Response<AccountBPResponse>) {
                if (response.body()?.status == 200) {
                    try {
                        ticketbiding.progressBarLoader.visibility = View.GONE
                        if (response.body()!!.data.size > 0 && response.body()!!.data.isNotEmpty()) {
                            ticketbiding.billingAddressLayout.visibility = View.VISIBLE
                            if (response.body()!!.data[0].BPAddresses.isNotEmpty()) {
                                var modelData = response.body()!!.data[0].BPAddresses[0]
//                                setAddressDefaultData(modelData)
                            }
                        }else{
                            ticketbiding.billingAddressLayout.visibility = View.GONE
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                        Log.e("TAG===>", "onResponse: "+e.message )
                    }

                } else {
                    ticketbiding.progressBarLoader.visibility = View.GONE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<AccountBPResponse>, t: Throwable) {
                ticketbiding.progressBarLoader.visibility = View.GONE
                context?.let { t.message?.let { it1 -> Global.errormessagetoast(it, it1) } }
            }
        })
    }


}
