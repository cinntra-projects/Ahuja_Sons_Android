package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityServiceContractDetailBinding
import com.ahuja.sons.fragment.TicketFragment
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ServiceContractListResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ServiceContractDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityServiceContractDetailBinding
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
        binding = ActivityServiceContractDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }

/*
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)*/

        var intent = intent.getStringExtra("id")

        if (Global.checkForInternet(this@ServiceContractDetailActivity)) {

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", intent)
            viewModel.ServiceContractOneApi(jsonObject)
            bindObserver()
        }


        binding.viewTicketChip.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val mFragmentTransaction = supportFragmentManager.beginTransaction()
            var mFragment = TicketFragment(0)

            val mBundle = Bundle()
            mBundle.putString("serviceID", serviceID)
            mBundle.putString("Flag", "ServiceContract")
            mFragment.arguments = mBundle
            mFragmentTransaction.replace(R.id.frame_ticket, mFragment).addToBackStack(null).commit()
        }

    }

    var serviceID = ""
    private fun bindObserver() {
        viewModel.serviceContractList.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    Log.e(FileUtil.TAG, "errorInApi: $it")
                    Global.warningmessagetoast(this@ServiceContractDetailActivity, it)
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
                                this@ServiceContractDetailActivity,
                                it.message!!
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            ))
    }

    private fun setDefaultData(dataModel: ServiceContractListResponseModel.DataXXX) {

        if (dataModel.ServiceItemList.isNotEmpty()){
            var tempList = ArrayList<String>()
            for (item in dataModel.ServiceItemList) {
                tempList.add(item.ItemName)
            }
            val separatedSolution = tempList.joinToString(",")
            binding.tvItem.text = separatedSolution
        }else{
            binding.tvItem.text = "NA"
        }

        if (dataModel.CardName.isNotEmpty()) {
            binding.tvCustomer.text = dataModel.CardName
        } else {
            binding.tvCustomer.text = "NA"
        }
        if (dataModel.AddressName.isNotEmpty()) {
            binding.tvBranch.text = dataModel.AddressName
        } else {
            binding.tvBranch.text = "NA"
        }

        if (dataModel.ContractType.isNotEmpty()) {
            binding.tvContractType.text = dataModel.ContractType
        } else {
            binding.tvContractType.text = "NA"
        }

        if (dataModel.Frequency.isNotEmpty()) {
            binding.tvFrequency.text = dataModel.Frequency
        } else {
            binding.tvFrequency.text = "NA"
        }

        if (dataModel.FromDate.isNotEmpty()) {
            binding.tvFromDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.FromDate)
        } else {
            binding.tvFromDate.text = "NA"
        }

        if (dataModel.ToDate.isNotEmpty()) {
            binding.tvToDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.ToDate)
        } else {
            binding.tvToDate.text = "NA"
        }
        if (dataModel.TotalAmount.isNotEmpty()) {
            binding.tvTotalCost.text = dataModel.TotalAmount
        } else {
            binding.tvTotalCost.text = "NA"
        }

        if (dataModel.AssignedToName.isNotEmpty()) {
            binding.tvServiceManager.text = dataModel.AssignedToName
        } else {
            binding.tvServiceManager.text = "NA"
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