package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.adapter.*
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityAddServiceContractActivtyBinding
import com.ahuja.sons.fragment.SelectBusinessPartnerFragement
import com.ahuja.sons.globals.Global
import com.ahuja.sons.`interface`.SelectBusinessPartneer
import com.ahuja.sons.model.EmployeeData
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import com.ahuja.sons.newapimodel.CreateServiceContractRequestModel
import com.ahuja.sons.newapimodel.DataCustomerListForContact
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AddServiceContractActivty : AppCompatActivity(), SelectBusinessPartneer {

    lateinit var binding : ActivityAddServiceContractActivtyBinding
    lateinit var viewModel: MainViewModel
    var branchAllList = ArrayList<BranchAllListResponseModel.DataXXX>()
    var CardCode = ""
    var insallItemList: ArrayList<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
    var selectedDataList = ArrayList<ItemAllListResponseModel.DataXXX>()
    var branchSelectedDataList = ArrayList<BranchAllListResponseModel.DataXXX>()
    var ServiceManagerID = ""
    var contractTypeName = ""
    var frequencyName = ""

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddServiceContractActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }

        //todo calling customer fragment--
        binding.businesspartnerValue.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.addServiceFrame, SelectBusinessPartnerFragement(AddTicketActivity(),EditTicketActivity(), AddContactPerson(), "AddServiceContract",this)).addToBackStack(null)
            transaction.commit()
        }

        //todo calling sales employee--
        if (Global.checkForInternet(this@AddServiceContractActivty)){
            viewModel.getSalesEmployeeAllList()
            bindServiceManagerObserver()

        }else{
            Global.warningmessagetoast(this@AddServiceContractActivty, "Please Check Internet")
        }


        binding.submitBtn.setOnClickListener {
            if (Global.checkForInternet(this@AddServiceContractActivty)){

                if (validation(binding.businesspartnerValue.text.toString(), binding.edtFromDate.text.toString(), binding.edtToDate.text.toString())){

                    var itemList : ArrayList<CreateServiceContractRequestModel.ServiceItemX> = ArrayList()

                    for (i in 0 until selectedDataList.size) {
                        var serviceItemList = CreateServiceContractRequestModel.ServiceItemX(
                            BranchId = selectedDataList[i].BranchId,
                            ItemCode = selectedDataList[i].ItemCode,
                            ItemName = selectedDataList[i].ItemName,
                            ItemsGroupCode = selectedDataList[i].ItemsGroupCode,
                            ItemsGroupName = selectedDataList[i].ItemsGroupName,
                            ModelNo = selectedDataList[i].ModelNo,
                            Quantity = selectedDataList[i].Quantity.toString(),
                            SerialNo = selectedDataList[i].SerialNo,
                            UnitPrice = selectedDataList[i].UnitPrice,
                            WarrantyEndDate = selectedDataList[i].WarrantyEndDate,
                            WarrantyStartDate = selectedDataList[i].WarrantyStartDate
                        )

                        itemList.add(serviceItemList)
                    }

                    var modelData = CreateServiceContractRequestModel(
                        AssignedToId = ServiceManagerID,
                        BillingFrequency = frequencyName,
                        BranchId = separatedSolution,
                        CardCode = CardCode,
                        ContractType = contractTypeName,
                        FromDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtFromDate.text.toString()),
                        PaymentTerm = binding.edtPaymentTerm.text.toString(),
                        ToDate = Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtToDate.text.toString()),
                        CreatedById = Prefs.getString(Global.Employee_Code),
                        Remarks = binding.edtRemarks.text.toString(),
                        ServiceItemList = itemList,
                        id = ""

                    )

                    val gson = Gson()
                    val jsonTut: String = gson.toJson(modelData)
                    Log.e("data", jsonTut)
                    if (Global.checkForInternet(this)) {
                        viewModel.createServiceContract(modelData)
                        bindCreateServiceContractObserver()
                    }
                }


            }else{
                Global.warningmessagetoast(this@AddServiceContractActivty, "Please Check Internet")
            }
        }


        //todo item selecetd on Servcie manager--
        binding.acServiceManager.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (serviceManagerList_gl.size > 0){
                    ServiceManagerID = serviceManagerList_gl[pos].getId().toString()
                    binding.acServiceManager.setText(serviceManagerList_gl[pos].getFirstName().toString())
                }else{
                    ServiceManagerID = ""
                    binding.acServiceManager.setText("")
                }
            }

        }

        //todo click on dates
        binding.edtFromDate.setOnClickListener{
            Global.selectDate(this@AddServiceContractActivty, binding.edtFromDate)
        }


        binding.edtToDate.setOnClickListener{
            Global.selectDate(this@AddServiceContractActivty, binding.edtToDate)
        }


        //todo set Contract type adapter--
        val adapter = ArrayAdapter(this@AddServiceContractActivty, android.R.layout.simple_dropdown_item_1line, Global.contractTypeList_gl)
        binding.acContractTye.setAdapter(adapter)

        //todo mode communication item selected
        binding.acContractTye.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.contractTypeList_gl.isNotEmpty()) {
                    contractTypeName = Global.contractTypeList_gl[position]
                    binding.acContractTye.setText(Global.contractTypeList_gl[position])

                    val adapter = ArrayAdapter(this@AddServiceContractActivty, android.R.layout.simple_dropdown_item_1line, Global.contractTypeList_gl)
                    binding.acContractTye.setAdapter(adapter)
                } else {
                    contractTypeName = ""
                    binding.acContractTye.setText("")
                }
            }

        }


        //todo set frequency type adapter--
        val frequencyAdapter = ArrayAdapter(this@AddServiceContractActivty, android.R.layout.simple_dropdown_item_1line, Global.frequencyList_gl)
        binding.acFrequency.setAdapter(frequencyAdapter)

        //todo mode communication item selected
        binding.acFrequency.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.frequencyList_gl.isNotEmpty()) {
                    frequencyName = Global.frequencyList_gl[position]
                    binding.acFrequency.setText(Global.frequencyList_gl[position])

                    val adapter = ArrayAdapter(this@AddServiceContractActivty, android.R.layout.simple_dropdown_item_1line, Global.frequencyList_gl)
                    binding.acFrequency.setAdapter(adapter)
                } else {
                    frequencyName = ""
                    binding.acFrequency.setText("")
                }
            }

        }

    }



    //todo calling create observer---
    private fun bindCreateServiceContractObserver() {
        viewModel.serviceContractList.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    Log.e(FileUtil.TAG, "errorInApi: $it")
                    Global.warningmessagetoast(this@AddServiceContractActivty, it)
                }, onLoading = {
                    binding.loadingback.visibility = View.VISIBLE
                    binding.loadingview.start()
                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Global.successmessagetoast(this@AddServiceContractActivty, "Successfully Service Contract Create")
                            onBackPressed()

                        } else {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@AddServiceContractActivty, it.message!!)
                        }
                    } catch (e: Exception) {
                        binding.loadingback.visibility = View.GONE
                        binding.loadingview.stop()
                        e.printStackTrace()
                    }

                }
            ))
    }


    //todo select customer override fucntion
    override fun selectpartner(bpdata: DataCustomerListForContact) {
        binding.businesspartnerValue.setText(bpdata.CardName)
        CardCode = bpdata.CardCode

        //todo calling branch api list here---

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("BPCode", CardCode)
        viewModel.getBranchAllList(jsonObject1)
        bindBranchListObserver()

    }

    var separatedSolution = ""

    //todo branch observer---
    private fun bindBranchListObserver() {
        viewModel.branchAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    var SalesEmployeeList: List<BranchAllListResponseModel.DataXXX> = ArrayList<BranchAllListResponseModel.DataXXX>()
                    SalesEmployeeList = branchFilterlist(it.data)
                    branchAllList.clear()
                    branchAllList.addAll(SalesEmployeeList)

                    val itemNames: MutableList<String> = ArrayList()
                    for (item in branchAllList) {
                        itemNames.add("Branch - "  +  item.AddressName )
                    }

                    var adapter = BranchItemSelected(this, R.layout.drop_down_item_textview, branchAllList)
                    binding.acBranch.setAdapter(adapter)


                    binding.acBranch.onItemClickListener = object : AdapterView.OnItemClickListener {
                        override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                            val selectedData = branchAllList[position]

                            if (selectedData != null) {
                                binding.rvBranchItems.visibility = View.VISIBLE
                            } else {
                                binding.rvBranchItems.visibility = View.GONE
                            }
                            if (selectedData != null && !branchSelectedDataList.contains(selectedData)) {
                                branchSelectedDataList.add(selectedData)
                                adapter.notifyDataSetChanged()
                                Log.e("selected", "onItemClick: " + branchSelectedDataList.size)
                                val gridLayoutManager = GridLayoutManager(this@AddServiceContractActivty, 3)
                                val adapterEmp = SelectedBranchAdapter(this@AddServiceContractActivty, branchSelectedDataList)
                                binding.rvBranchItems.layoutManager = gridLayoutManager
                                binding.rvBranchItems.adapter = adapterEmp
                                adapterEmp.notifyDataSetChanged()
                                adapter.notifyDataSetChanged()
                            }

                            var tempList = ArrayList<String>()
                            for (item in branchSelectedDataList) {
                                tempList.add(item.id)
                            }
                            separatedSolution = tempList.joinToString(",")

                            var jsonObject = JsonObject()
                            jsonObject.addProperty("BranchId", separatedSolution)
                            jsonObject.addProperty("CardCode", CardCode)

                            viewModel.getItemAllList(jsonObject)
                            bindItemListObserver()

                            binding.acBranch.text.clear()
                        }

                    }


                }else {
                    Toast.makeText(this@AddServiceContractActivty, it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }

    private fun branchFilterlist(value: ArrayList<BranchAllListResponseModel.DataXXX>): List<BranchAllListResponseModel.DataXXX> {
        val tempList: MutableList<BranchAllListResponseModel.DataXXX> = ArrayList<BranchAllListResponseModel.DataXXX>()
        for (installedItemModel in value) {
            if (!installedItemModel.AddressName.equals("admin")) {
                tempList.add(installedItemModel)
            }
        }
        return tempList
    }

    //todo item seleced observer---

    private fun bindItemListObserver() {
        viewModel.itemAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        if (it.data.size > 0 && it.data != null) {
                            var SalesEmployeeList: List<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
                            SalesEmployeeList = filterlist(it.data)
                            insallItemList.clear()
                            insallItemList.addAll(SalesEmployeeList)

                            val itemNames: MutableList<String> = ArrayList()
                            for (item in insallItemList) {
                                itemNames.add("Item - " + item.ItemCode + " ( " + item.ItemName + " )")
                            }

                            var adapter = ItemNameAdapter(this, R.layout.drop_down_item_textview, insallItemList)
                            binding.acItemName.setAdapter(adapter)


                            binding.acItemName.onItemClickListener = object : AdapterView.OnItemClickListener {
                                override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                                    val selectedData = insallItemList[position]

                                    if (selectedData != null) {
                                        binding.rvItemNames.visibility = View.VISIBLE
                                    } else {
                                        binding.rvItemNames.visibility = View.GONE
                                    }
                                    if (selectedData != null && !selectedDataList.contains(selectedData)) {
                                        selectedDataList.add(selectedData)
                                        adapter.notifyDataSetChanged()
                                        Log.e("selected", "onItemClick: " + selectedDataList.size)
                                        val gridLayoutManager = GridLayoutManager(this@AddServiceContractActivty, 2)
                                        val adapterEmp = SelectedItemAdapter(this@AddServiceContractActivty, selectedDataList, "")
                                        binding.rvItemNames.layoutManager = gridLayoutManager
                                        binding.rvItemNames.adapter = adapterEmp
                                        adapterEmp.notifyDataSetChanged()
                                        adapter.notifyDataSetChanged()
                                    }

                                    binding.acItemName.text.clear()
                                }

                            }

                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        Global.warningmessagetoast(this@AddServiceContractActivty, it.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }


    private fun filterlist(value: ArrayList<ItemAllListResponseModel.DataXXX>): List<ItemAllListResponseModel.DataXXX> {
        val tempList: MutableList<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
        for (installedItemModel in value) {
            if (!installedItemModel.ItemName.equals("admin")) {
                tempList.add(installedItemModel)
            }
        }
        return tempList
    }


    var serviceManagerList_gl : ArrayList<EmployeeData> = ArrayList()
    //todo calling service manager observer---
    private fun bindServiceManagerObserver() {
        viewModel.salesEmployeeResponse.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {

                    Log.e(FileUtil.TAG, "errorInApi: $it")
                    Global.warningmessagetoast(this@AddServiceContractActivty, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.getStatus() == 200) {
                            serviceManagerList_gl.clear()
                            serviceManagerList_gl.addAll(it.getData())
                            var adapter = ServiceManagerAdapter(this, R.layout.drop_down_item_textview, serviceManagerList_gl)
                            binding.acServiceManager.setAdapter(adapter)


                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.getMessage()}")
                            Global.warningmessagetoast(this@AddServiceContractActivty, it.getMessage()!!)
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                }
            ))
    }


    private fun validation(businesspartner_value: String , edtFromDate : String, edtToDate :String): Boolean {
        if (selectedDataList.size == 0) {
            Toast.makeText(this, "Select Atleast one Items Please !", Toast.LENGTH_SHORT).show()
        }
        else if (branchSelectedDataList.size == 0) {
            Toast.makeText(this, "Select Atleast one Branch Please !", Toast.LENGTH_SHORT).show()
        }
        else if (businesspartner_value.isEmpty()) {
            Toast.makeText(this, "Select Customer", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (contractTypeName.isEmpty()) {
            Toast.makeText(this, "Select Contract Type", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (frequencyName.isEmpty()) {
            Toast.makeText(this, "Select Frequency", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (edtFromDate.isEmpty()) {
            Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (edtToDate.isEmpty()) {
            Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (ServiceManagerID.isEmpty()) {
            Toast.makeText(this, "Select Service Manager", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }



}