package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.OrderOneResponseModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityOrderDetailBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class OrderDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrderDetailBinding
    lateinit var viewModel: MainViewModel

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }

        var id = intent.getIntExtra("id", 0)

        if (Global.checkForInternet(this@OrderDetailActivity)) {

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", id)
            viewModel.getOrderOneDetail(jsonObject)
            bindOneObserver()
        }

        
    }

    
    //todo bind default data--
    private fun bindOneObserver() {
        viewModel.orderOneDetail.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@OrderDetailActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            if (it.data.isNotEmpty() && it.data != null) {
                                setDefaultData(it.data[0])
                            }

                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@OrderDetailActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            ))
    }


    //todo set deafult data
    private fun setDefaultData(modelData: OrderOneResponseModel.Data) {

        //todo set contact details--
        if (modelData.CardName.isNotEmpty()) {
            binding.tvCustomerName.text = modelData.CardName
        } else {
            binding.tvCustomerName.text = "NA"
        }
       /* if (modelData.ContactPersonCode.isNotEmpty()) {
            binding.tvContactPerson.text = modelData.ContactPersonCode[0].FirstName
        } else {
            binding.tvContactPerson.text = "NA"
        }
        if (modelData.SalesPersonCode.isNotEmpty()) {
            binding.tvServiceEmployee.text = modelData.SalesPersonCode[0].SalesEmployeeName
        } else {
            binding.tvServiceEmployee.text = "NA"
        }
        if (modelData.TaxDate.isNotEmpty()) {
            binding.tvPostingDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.TaxDate)
        } else {
            binding.tvPostingDate.text = "NA"
        }
        if (modelData.DocDueDate.isNotEmpty()) {
            binding.tvValidDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.DocDueDate)
        } else {
            binding.tvValidDate.text = "NA"
        }
        if (modelData.DocDate.isNotEmpty()) {
            binding.tvDocumentDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.DocDate)
        } else {
            binding.tvDocumentDate.text = "NA"
        }
        if (modelData.U_TermInterestRate.isNotEmpty()) {
            binding.tvInterestRate.text = modelData.U_TermInterestRate
        } else {
            binding.tvInterestRate.text = "NA"
        }
        if (modelData.U_TermDueDate.isNotEmpty()) {
            binding.tvDueDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.U_TermDueDate)
        } else {
            binding.tvDueDate.text = "NA"
        }
        if (modelData.U_TermDueDate.isNotEmpty()) {
            binding.tvDescription.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.U_TermDueDate)
        } else {
            binding.tvDescription.text = "NA"
        }
        if (modelData.PoNo.isNotEmpty()) {
            binding.tvPurchaseNumber.text = modelData.PoNo
        } else {
            binding.tvPurchaseNumber.text = "NA"
        }
        binding.tvPurchaseDate.text = "NA"
        if (modelData.PoAmt.isNotEmpty()) {
            binding.tvPurchaseAmount.text = modelData.PoAmt
        } else {
            binding.tvPurchaseAmount.text = "NA"
        }


        //todo billing address data set---
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.BillToBuilding.isNotEmpty()){
                binding.tvBillingName.text = modelData.AddressExtension.BillToBuilding
            }else {
                binding.tvBillingName.text = "NA"
            }
        } else {
            binding.tvBillingName.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.BillToZipCode.isNotEmpty()){
                binding.tvBillZipCode.text = modelData.AddressExtension.BillToZipCode
            }else {
                binding.tvBillZipCode.text = "NA"
            }

        } else {
            binding.tvBillZipCode.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.BillToCountry.isNotEmpty()){
                binding.tvBillCountry.text = modelData.AddressExtension.BillToCountry
            }else {
                binding.tvBillCountry.text = "NA"
            }
        } else {
            binding.tvBillCountry.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.BillToState.isNotEmpty()){
                binding.tvBillState.text = modelData.AddressExtension.BillToState
            }else {
                binding.tvBillState.text = "NA"
            }
        } else {
            binding.tvBillState.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.U_SHPTYPB.isNotEmpty()){
                binding.tvBillToShippingType.text = modelData.AddressExtension.U_SHPTYPB
            }else {
                binding.tvBillToShippingType.text = "NA"
            }
        } else {
            binding.tvBillToShippingType.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.BillToStreet.isNotEmpty()){
                binding.tvBillingAddress.text = modelData.AddressExtension.BillToStreet
            }else {
                binding.tvBillingAddress.text = "NA"
            }
        } else {
            binding.tvBillingAddress.text = "NA"
        }


        //todo shipping address data set---
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.ShipToBuilding.isNotEmpty()){
                binding.tvShippingName.text = modelData.AddressExtension.ShipToBuilding
            }else {
                binding.tvShippingName.text = "NA"
            }
        } else {
            binding.tvShippingName.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.ShipToZipCode.isNotEmpty()){
                binding.tvShipZipCode.text = modelData.AddressExtension.ShipToZipCode
            }else {
                binding.tvShipZipCode.text = "NA"
            }

        } else {
            binding.tvShipZipCode.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.ShipToCountry.isNotEmpty()){
                binding.tvShipCountry.text = modelData.AddressExtension.ShipToCountry
            }else {
                binding.tvShipCountry.text = "NA"
            }
        } else {
            binding.tvShipCountry.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.ShipToState.isNotEmpty()){
                binding.tvShipState.text = modelData.AddressExtension.ShipToState
            }else {
                binding.tvShipState.text = "NA"
            }
        } else {
            binding.tvShipState.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.U_SHPTYPS.isNotEmpty()){
                binding.tvShipToShippingType.text = modelData.AddressExtension.U_SHPTYPS
            }else {
                binding.tvShipToShippingType.text = "NA"
            }
        } else {
            binding.tvShipToShippingType.text = "NA"
        }
        if (modelData.AddressExtension != null) {
            if (modelData.AddressExtension.ShipToStreet.isNotEmpty()){
                binding.tvShippingAddress.text = modelData.AddressExtension.ShipToStreet
            }else {
                binding.tvShippingAddress.text = "NA"
            }
        } else {
            binding.tvShippingAddress.text = "NA"
        }
*/


    }


}