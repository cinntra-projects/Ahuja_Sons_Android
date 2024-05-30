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
import com.ahuja.sons.adapter.BranchAutoCompleteAdapter
import com.ahuja.sons.adapter.CustomerAutoAdapter
import com.ahuja.sons.adapter.ItemGroupCategoryAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityUpdateProductBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.ItemCategoryData
import com.ahuja.sons.newapimodel.AddProductRequestModel
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import com.ahuja.sons.newapimodel.DataCustomerListForContact
import com.ahuja.sons.newapimodel.ProductResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UpdateProductActivity : AppCompatActivity() {

    lateinit var binding : ActivityUpdateProductBinding
    var CardCode = ""
    var contractName = ""
    var branchID = ""
    var itemGroupCode = 0
    var itemGroupName = ""
    var productType = ""
    var modelNoValue = ""
    lateinit var viewModel: MainViewModel
    var id = ""

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()


        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }

        id = intent.getStringExtra("id")!!

        if (Global.checkForInternet(this@UpdateProductActivity)) {

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", id)
            viewModel.getProductOneDetailApi(jsonObject)
            bindDefaultObserver()

            //todo item group observer--
            viewModel.getAllCategoryList()
            bindItemGroupObserver()

            //todo customer---
            viewModel.getCustomerListForContact()
            bindCustomerObserver()
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
        val productAdapter = ArrayAdapter(this@UpdateProductActivity, android.R.layout.simple_dropdown_item_1line, Global.productTypeList_gl)
        binding.acProductType.setAdapter(productAdapter)

        //todo mode communication item selected
        binding.acProductType.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.productTypeList_gl.isNotEmpty()) {
                    productType = Global.productTypeList_gl[position]
                    binding.acProductType.setText(Global.productTypeList_gl[position])

                    val adapter = ArrayAdapter(this@UpdateProductActivity, android.R.layout.simple_dropdown_item_1line, Global.productTypeList_gl)
                    binding.acProductType.setAdapter(adapter)
                } else {
                    productType = ""
                    binding.acProductType.setText("")
                }
            }

        }


        //todo set model no. adapter--
        val modelAdapter = ArrayAdapter(this@UpdateProductActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
        binding.acModelNo.setAdapter(modelAdapter)

        //todo mode communication item selected
        binding.acModelNo.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.modelList_gl.isNotEmpty()) {
                    modelNoValue = Global.modelList_gl[position]
                    binding.acModelNo.setText(Global.modelList_gl[position])

                    val adapter = ArrayAdapter(this@UpdateProductActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
                    binding.acModelNo.setAdapter(adapter)
                } else {
                    modelNoValue = ""
                    binding.acModelNo.setText("")
                }
            }

        }

        

        //todo click on dates
        binding.edtInstallationDate.setOnClickListener{
            Global.selectDate(this@UpdateProductActivity, binding.edtInstallationDate)
        }

        binding.edtWarrantyStartDate.setOnClickListener{
            Global.selectDate(this@UpdateProductActivity, binding.edtWarrantyStartDate)
        }

        binding.edtWarrantyStopDate.setOnClickListener{
            Global.selectDate(this@UpdateProductActivity, binding.edtWarrantyStopDate)
        }

        binding.edtSiteSurveyDate.setOnClickListener{
            Global.selectDate(this@UpdateProductActivity, binding.edtSiteSurveyDate)
        }

        binding.edtCommissioningDate.setOnClickListener{
            Global.selectDate(this@UpdateProductActivity, binding.edtCommissioningDate)
        }

        binding.edtDeInstallationDate.setOnClickListener{
            Global.selectDate(this@UpdateProductActivity, binding.edtDeInstallationDate)
        }

        binding.edtReInstallationDate.setOnClickListener{
            Global.selectDate(this@UpdateProductActivity, binding.edtReInstallationDate)
        }


        binding.updateBtn.setOnClickListener {

            if (Global.checkForInternet(this@UpdateProductActivity)){

                if (validation()){
                    var requestModel = AddProductRequestModel(
                        BranchId = branchID.toInt(),
                        CardCode = CardCode,
                        CommissioningDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtCommissioningDate.text.toString()),
                        ContractorName = contractName,
                        DeInstallationDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtDeInstallationDate.text.toString()),
                        InstallationDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtInstallationDate.text.toString()),
                        ItemCode = oneDataModel.ItemCode,
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
                        id = id
                    )

                    viewModel.createProduct(requestModel)
                    bindUpdateProductObserver()
                }

            }
            else{
                Global.warningmessagetoast(this , "Please Check the Internet")
            }


        }

    }

    private fun bindUpdateProductObserver() {
        viewModel.productOneDetailData.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
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

    var oneDataModel = ProductResponseModel.DataXXX()
    private fun bindDefaultObserver() {
        viewModel.productOneDetailData.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    Log.e(FileUtil.TAG, "errorInApi: $it")
                    Global.warningmessagetoast(this@UpdateProductActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            if (it.data.isNotEmpty() && it.data != null) {
                                setDefaultData(it.data[0])
                                oneDataModel = it.data[0]
                            }

                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@UpdateProductActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            ))
    }


    //todo bind default data---
    private fun setDefaultData(dataModel: ProductResponseModel.DataXXX) {

        //todo calling branch all api--
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("BPCode", dataModel.CardCode)
        viewModel.getBranchAllList(jsonObject1)
        bindBranchListObserver()

        if (dataModel.CardName.isNotEmpty()) {
            CardCode = dataModel.CardCode
            binding.acCustomer.setText(dataModel.CardName)
        } else {
            CardCode = ""
            binding.acCustomer.setText("")
        }

        if (dataModel.BPBranch.isNotEmpty()) {
            branchID = dataModel.BPBranch[0].id.toString()
            binding.acBranch.setText(dataModel.BPBranch[0].AddressName)
        } else {
            branchID = ""
            binding.acBranch.setText("")
        }
        if (dataModel.ContractorName.isNotEmpty()) {
            contractName = dataModel.ContractorName
            binding.acContractName.setText(dataModel.ContractorName)
        } else {
            contractName = ""
            binding.acContractName.setText("")
        }

        if (dataModel.ItemsGroupName.isNotEmpty()) {
            itemGroupCode = dataModel.ItemsGroupCode.toInt()
            itemGroupName = dataModel.ItemsGroupName
            binding.acItemGroup.setText(dataModel.ItemsGroupName)
        } else {
            itemGroupCode = 0
            itemGroupName = ""
            binding.acItemGroup.setText("")
        }
        if (dataModel.ItemName.isNotEmpty()) {
            binding.edtItemName.setText(dataModel.ItemName)
        } else {
            binding.edtItemName.setText("")
        }
        if (dataModel.UnitPrice.isNotEmpty()) {
            binding.edtUnitPrice.setText(dataModel.UnitPrice)
        } else {
            binding.edtUnitPrice.setText("")
        }

        //todo bind product--
        val productAdapter = ArrayAdapter(this@UpdateProductActivity, android.R.layout.simple_dropdown_item_1line, Global.productTypeList_gl)
        binding.acProductType.setAdapter(productAdapter)

        if (dataModel.ItemType.isNotEmpty()) {
            productType = dataModel.ItemType
            binding.acProductType.setText(dataModel.ItemType)
        } else {
            productType = ""
            binding.acProductType.setText("")
        }

        //todo set model no. adapter--
        val modelAdapter = ArrayAdapter(this@UpdateProductActivity, android.R.layout.simple_dropdown_item_1line, Global.modelList_gl)
        binding.acModelNo.setAdapter(modelAdapter)

        if (dataModel.ModelNo.isNotEmpty()) {
            modelNoValue = dataModel.ModelNo
            binding.acModelNo.setText(dataModel.ModelNo)
        } else {
            modelNoValue = ""
            binding.acModelNo.setText("")
        }

        if (dataModel.SerialNo.isNotEmpty()) {
            binding.edtSerialNo.setText(dataModel.SerialNo)
        } else {
            binding.edtSerialNo.setText("")
        }

        if (dataModel.InstallationDate.isNotEmpty()) {
            binding.edtInstallationDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.InstallationDate))
        } else {
            binding.edtInstallationDate.setText("")
        }
        if (dataModel.WarrantyStartDate.isNotEmpty()) {
            binding.edtWarrantyStartDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.WarrantyStartDate))
        } else {
            binding.edtWarrantyStartDate.setText("")
        }
        if (dataModel.WarrantyEndDate.isNotEmpty()) {
            binding.edtWarrantyStopDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.WarrantyEndDate))
        } else {
            binding.edtWarrantyStopDate.setText("")
        }
        if (dataModel.SiteSurvey.isNotEmpty()) {
            binding.edtSiteSurveyDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.SiteSurvey))
        } else {
            binding.edtSiteSurveyDate.setText("")
        }
        if (dataModel.CommissioningDate.isNotEmpty()) {
            binding.edtCommissioningDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.CommissioningDate))
        } else {
            binding.edtCommissioningDate.setText("")
        }
        if (dataModel.DeInstallationDate.isNotEmpty()) {
            binding.edtDeInstallationDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.DeInstallationDate))
        } else {
            binding.edtDeInstallationDate.setText("")
        }
        if (dataModel.ReInstallationDate.isNotEmpty()) {
            binding.edtReInstallationDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataModel.ReInstallationDate))
        } else {
            binding.edtReInstallationDate.setText("")
        }
        if (dataModel.Remarks.isNotEmpty()) {
            binding.edtRemarks.setText(dataModel.Remarks)
        } else {
            binding.edtRemarks.setText("")
        }

    }


    var businessPartnerList_gl = ArrayList<DataCustomerListForContact>()

    //todo bind observer...
    private fun bindCustomerObserver() {
        viewModel.customerListContact.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
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
                Log.e("fail==>", it)
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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