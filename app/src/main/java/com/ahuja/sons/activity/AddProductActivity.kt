package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.adapter.*
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityAddProductBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.ItemCategoryData
import com.ahuja.sons.newapimodel.AddProductRequestModel
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import com.ahuja.sons.newapimodel.DataCustomerListForContact
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AddProductActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddProductBinding
    var CardCode = ""
    var contractName = ""
    var branchID = ""
    var itemGroupCode = 0
    var itemGroupName = ""
    var productType = ""
    var modelNoValue = ""

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
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }


        if (Global.checkForInternet(this)) {
            viewModel.getCustomerListForContact()
            bindCustomerObserver()

            viewModel.getAllCategoryList()
            bindItemGroupObserver()
        }


        //todo customer item click--
        binding.acCustomer.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (businessPartnerList_gl.size > 0){
                    CardCode = businessPartnerList_gl[pos].CardCode
                    binding.acCustomer.setText(businessPartnerList_gl[pos].CardName)

                    binding.acBranch.setText("")
                    var jsonObject1 = JsonObject()
                    jsonObject1.addProperty("BPCode", CardCode)
                    viewModel.getBranchAllList(jsonObject1)
                    bindBranchListObserver()


                }else{
                    CardCode = ""
                    binding.acCustomer.setText("")
                }
            }

        }

        //todo contract name item click--
        binding.acContractName.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (businessPartnerList_gl.size > 0){
                    contractName = businessPartnerList_gl[pos].CardName
                    binding.acContractName.setText(businessPartnerList_gl[pos].CardName.toString())
                }else{
                    contractName = ""
                    binding.acContractName.setText("")
                }
            }

        }


        //todo branch item click--
        binding.acBranch.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (branchAllList.size > 0){
                    branchID = branchAllList[pos].id
                    binding.acBranch.setText(branchAllList[pos].AddressName)
                }else{
                    branchID = ""
                    binding.acBranch.setText("")
                }
            }

        }


        //todo item group item click--
        binding.acItemGroup.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (itemGroupList.size > 0){
                    itemGroupCode = itemGroupList[pos].Number
                    itemGroupName = itemGroupList[pos].GroupName
                    binding.acItemGroup.setText(itemGroupList[pos].GroupName)
                }else{
                    itemGroupCode = 0
                    itemGroupName = ""
                    binding.acItemGroup.setText("")
                }
            }

        }


        //todo set product type adapter--
        val productAdapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_dropdown_item_1line, Global.productTypeList_gl)
        binding.acProductType.setAdapter(productAdapter)

        //todo mode communication item selected
        binding.acProductType.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.productTypeList_gl.isNotEmpty()) {
                    productType = Global.productTypeList_gl[position]
                    binding.acProductType.setText(Global.productTypeList_gl[position])

                    val adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_dropdown_item_1line, Global.productTypeList_gl)
                    binding.acProductType.setAdapter(adapter)
                } else {
                    productType = ""
                    binding.acProductType.setText("")
                }
            }

        }


        //todo set model no. adapter--
        val modelAdapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
        binding.acModelNo.setAdapter(modelAdapter)

        //todo mode communication item selected
        binding.acModelNo.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.modelList_gl.isNotEmpty()) {
                    modelNoValue = Global.modelList_gl[position]
                    binding.acModelNo.setText(Global.modelList_gl[position])

                    val adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
                    binding.acModelNo.setAdapter(adapter)
                } else {
                    modelNoValue = ""
                    binding.acModelNo.setText("")
                }
            }

        }


        //todo click on dates
        binding.edtInstallationDate.setOnClickListener{
            Global.selectDate(this@AddProductActivity, binding.edtInstallationDate)
        }

        binding.edtWarrantyStartDate.setOnClickListener{
            Global.selectDate(this@AddProductActivity, binding.edtWarrantyStartDate)
        }

        binding.edtWarrantyStopDate.setOnClickListener{
            Global.selectDate(this@AddProductActivity, binding.edtWarrantyStopDate)
        }

        binding.edtSiteSurveyDate.setOnClickListener{
            Global.selectDate(this@AddProductActivity, binding.edtSiteSurveyDate)
        }

        binding.edtCommissioningDate.setOnClickListener{
            Global.selectDate(this@AddProductActivity, binding.edtCommissioningDate)
        }

        binding.edtDeInstallationDate.setOnClickListener{
            Global.selectDate(this@AddProductActivity, binding.edtDeInstallationDate)
        }

        binding.edtReInstallationDate.setOnClickListener{
            Global.selectDate(this@AddProductActivity, binding.edtReInstallationDate)
        }

        binding.submitBtn.setOnClickListener {

            if (Global.checkForInternet(this@AddProductActivity)){

                if (validation()){
                    var requestModel = AddProductRequestModel(
                        BranchId = branchID.toInt(),
                        CardCode = CardCode,
                        CommissioningDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtCommissioningDate.text.toString()),
                        ContractorName = contractName,
                        DeInstallationDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtDeInstallationDate.text.toString()),
                        InstallationDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtInstallationDate.text.toString()),
                        ItemCode = "",
                        ItemName = binding.edtItemName.text.toString(),
                        ItemType = productType,
                        ItemsGroupCode = itemGroupCode,
                        ItemsGroupName = itemGroupName,
                        ModelNo = modelNoValue,
                        Quantity = "1",
                        ReInstallationDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtReInstallationDate.text.toString()),
                        Remarks = binding.edtRemarks.text.toString(),
                        SerialNo = binding.edtSerialNo.text.toString(),
                        SiteSurvey = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtSiteSurveyDate.text.toString()),
                        UnitPrice = binding.edtUnitPrice.text.toString(),
                        WarrantyEndDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtWarrantyStopDate.text.toString()),
                        WarrantyStartDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtWarrantyStartDate.text.toString()),
                        WarrantyType = "",
                        id = ""
                    )

                    viewModel.createProduct(requestModel)
                    bindCreateProductObserver()
                }

            }
            else{
                Global.warningmessagetoast(this , "Please Check the Internet")
            }


        }


    }


    //todo create product api---
    private fun bindCreateProductObserver() {

        viewModel.productOneDetailData.observe(this, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(this, it)
                binding.loadingback.visibility = View.GONE
                binding.loadingview.stop()
            },
            onLoading = {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingview.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    Log.e("data", response.data.toString())
                    Global.successmessagetoast(this, response.message)
                    finish()

                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    Global.warningmessagetoast(this, response.message)
                }
            }

        ))

    }


    var businessPartnerList_gl = ArrayList<DataCustomerListForContact>()

    //todo bind observer...
    private fun bindCustomerObserver() {
        viewModel.customerListContact.observe(this, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(this, it)

            },
            onLoading = {
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    businessPartnerList_gl.clear()
                    businessPartnerList_gl.addAll(response.data)
                    var adapter = CustomerAutoAdapter(this, R.layout.drop_down_item_textview, businessPartnerList_gl)
                    binding.acCustomer.setAdapter(adapter)

                    binding.acContractName.setAdapter(adapter)

                    Log.e("data", response.data.toString())
                } else {
                    Global.warningmessagetoast(this, response.message)
                }
            }

        ))


    }


    var branchAllList = java.util.ArrayList<BranchAllListResponseModel.DataXXX>()
    //todo branch observer---
    private fun bindBranchListObserver() {
        viewModel.branchAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    branchAllList.clear()
                    branchAllList.addAll(it.data)

                    var adapter = BranchAutoCompleteAdapter(this, R.layout.drop_down_item_textview, branchAllList)
                    binding.acBranch.setAdapter(adapter)

                }else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


    var itemGroupList : ArrayList<ItemCategoryData> = ArrayList()
    private fun bindItemGroupObserver() {
        viewModel.itemCategoryList.observe(this, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    itemGroupList.clear()
                    itemGroupList.addAll(it.data)
                    var adapter = ItemGroupCategoryAdapter(this, R.layout.drop_down_item_textview, itemGroupList)
                    binding.acItemGroup.setAdapter(adapter)

                }else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


    //todo set validation---

    private fun validation(): Boolean {
        if (CardCode.isEmpty()) {
            Toast.makeText(this, "Select Customer is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (branchID.isEmpty()) {
            Toast.makeText(this, "Select Branch is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (contractName.isEmpty()) {
            Toast.makeText(this, "Contractor Name is Required", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (itemGroupName.isEmpty()) {
            Toast.makeText(this, "Item Group Name is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (binding.edtItemName.text.toString().isEmpty()) {
            Toast.makeText(this, "Item Name is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (binding.edtUnitPrice.text.toString().isEmpty()) {
            Toast.makeText(this, "Unit Price is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (productType.isEmpty()) {
            Toast.makeText(this, "Select Product Type is Required", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (modelNoValue.isEmpty()) {
            Toast.makeText(this, "Select Model No. is Required", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (binding.edtSerialNo.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter Serial No. is Required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }



}