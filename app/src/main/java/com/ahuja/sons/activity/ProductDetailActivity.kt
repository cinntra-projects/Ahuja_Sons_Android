package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityProductDetailBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ProductResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ProductDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityProductDetailBinding
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
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }

        var intent = intent.getStringExtra("id")

        if (Global.checkForInternet(this@ProductDetailActivity)) {

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", intent)
            viewModel.getProductOneDetailApi(jsonObject)
            bindObserver()
        }
        
        
    }


    var serviceID = ""
    private fun bindObserver() {
        viewModel.productOneDetailData.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    Log.e(FileUtil.TAG, "errorInApi: $it")
                    Global.warningmessagetoast(this@ProductDetailActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            if (it.data.isNotEmpty() && it.data != null) {
                                setDefaultData(it.data[0])
                                serviceID = it.data[0].id
                            }

                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(
                                this@ProductDetailActivity,
                                it.message!!
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            ))
    }

    private fun setDefaultData(dataModel: ProductResponseModel.DataXXX) {


        if (dataModel.CardName.isNotEmpty()) {
            binding.tvCustomer.text = dataModel.CardName
        } else {
            binding.tvCustomer.text = "NA"
        }
        if (dataModel.BPBranch.isNotEmpty()) {
            binding.tvBranch.text = dataModel.BPBranch[0].AddressName
        } else {
            binding.tvBranch.text = "NA"
        }

        if (dataModel.BPBranch.isNotEmpty()) {
            binding.tvAddress.text = dataModel.BPBranch[0].Street
        } else {
            binding.tvAddress.text = "NA"
        }

        if (dataModel.BPBranch.isNotEmpty()) {
            binding.tvCity.text = dataModel.BPBranch[0].City
        } else {
            binding.tvCity.text = "NA"
        }

        if (dataModel.zone.isNotEmpty()) {
            binding.tvZone.text =dataModel.zone
        } else {
            binding.tvZone.text = "NA"
        }

        if (dataModel.ItemsGroupCode.isNotEmpty()) {
            binding.tvItemGroupCode.text = dataModel.ItemsGroupCode
        } else {
            binding.tvItemGroupCode.text = "NA"
        }
        if (dataModel.ItemsGroupName.isNotEmpty()) {
            binding.tvItemsGroupName.text = dataModel.ItemsGroupName
        } else {
            binding.tvItemsGroupName.text = "NA"
        }

        if (dataModel.ItemCode.isNotEmpty()) {
            binding.tvItemCode.text = dataModel.ItemCode
        } else {
            binding.tvItemCode.text = "NA"
        }

        if (dataModel.ItemName.isNotEmpty()) {
            binding.tvItemName.text = dataModel.ItemName
        } else {
            binding.tvItemName.text = "NA"
        }

        if (dataModel.ContractorName.isNotEmpty()) {
            binding.tvContractName.text = dataModel.ContractorName
        } else {
            binding.tvContractName.text = "NA"
        }
        if (dataModel.UnitPrice.isNotEmpty()) {
            binding.tvUnitPrice.text = dataModel.UnitPrice
        } else {
            binding.tvUnitPrice.text = "NA"
        }
        if (dataModel.ItemType.isNotEmpty()) {
            binding.tvProductType.text = dataModel.ItemType
        } else {
            binding.tvProductType.text = "NA"
        }
        if (dataModel.ModelNo.isNotEmpty()) {
            binding.tvModelNo.text = dataModel.ModelNo
        } else {
            binding.tvModelNo.text = "NA"
        }

        if (dataModel.SerialNo.isNotEmpty()) {
            binding.tvSerialNo.text = dataModel.SerialNo
        } else {
            binding.tvSerialNo.text = "NA"
        }
        if (dataModel.WarrantyStartDate.isNotEmpty()) {
            binding.tvWarrantyStartDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.WarrantyStartDate)
        } else {
            binding.tvWarrantyStartDate.text = "NA"
        }

        if (dataModel.WarrantyEndDate.isNotEmpty()) {
            binding.tvWarrantyEndDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.WarrantyEndDate)
        } else {
            binding.tvWarrantyEndDate.text = "NA"
        }
        if (dataModel.InstallationDate.isNotEmpty()) {
            binding.tvInstallationDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.InstallationDate)
        } else {
            binding.tvInstallationDate.text = "NA"
        }
        if (dataModel.SiteSurvey.isNotEmpty()) {
            binding.tvSiteSurveyDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.SiteSurvey)
        } else {
            binding.tvSiteSurveyDate.text = "NA"
        }
        if (dataModel.CommissioningDate.isNotEmpty()) {
            binding.tvCommissioningDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.CommissioningDate)
        } else {
            binding.tvCommissioningDate.text = "NA"
        }
        if (dataModel.DeInstallationDate.isNotEmpty()) {
            binding.tvDeInstallationDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.DeInstallationDate)
        } else {
            binding.tvDeInstallationDate.text = "NA"
        }

        if (dataModel.ReInstallationDate.isNotEmpty()) {
            binding.tvReInstallationDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.ReInstallationDate)
        } else {
            binding.tvReInstallationDate.text = "NA"
        }
        if (dataModel.Remarks.isNotEmpty()) {
            binding.tvRemarks.text = dataModel.Remarks
        } else {
            binding.tvRemarks.text = "NA"
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }
    
    
    
}