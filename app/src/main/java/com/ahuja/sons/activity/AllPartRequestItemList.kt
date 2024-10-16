package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.AllPartReqAttachmentAdapter
import com.ahuja.sons.adapter.AllPartReqItemsAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.PartrequatachitemBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.*
import com.ahuja.sons.newapimodel.Item
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AllPartRequestItemList : MainBaseActivity() {

    lateinit var binding: PartrequatachitemBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var attachlayoutManager: LinearLayoutManager
    var AllitemsList = ArrayList<Item>()
    var AttachmentList = ArrayList<PRAttachment>()

    lateinit var adapter: AllPartReqItemsAdapter
    lateinit var attachadapter: AllPartReqAttachmentAdapter
    var partreqdata: Int = 0
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
        binding = PartrequatachitemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        setDefaults()

        partreqdata = intent.getIntExtra(Global.PartRequestData, 0)


        binding.loadingView.start()
        if (Global.checkForInternet(this)) {

            val data = HashMap<String, Any>()
            data["PartId"] = partreqdata
            Log.e("payload", data.toString())
            viewModel.getAllPartRequestItem(data)
            bindObserver()
        }

    }

    //todo bind observer for one part request..

    private fun bindObserver() {
        viewModel.partRequestOneResponse.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
//                Global.warningmessagetoast(this, it)
//                Log.e("partdata", it)
            }, onLoading = {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = { response ->
                Log.e("partdata",response.data.toString())
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                if (response.status == 200) {
                    layoutManager = LinearLayoutManager(this@AllPartRequestItemList, RecyclerView.VERTICAL, false)
                    attachlayoutManager = LinearLayoutManager(this@AllPartRequestItemList, RecyclerView.VERTICAL, false)
                    AllitemsList.clear()
                    AllitemsList.addAll(response.data[0].Items)
                    binding.itemsRecycler.layoutManager = layoutManager
                    adapter = AllPartReqItemsAdapter(AllitemsList)
                    binding.attachmentRecycler.layoutManager = attachlayoutManager
                    AttachmentList.clear()
                    AttachmentList.addAll(response.data[0].PRAttachments)
                    attachadapter = AllPartReqAttachmentAdapter(this@AllPartRequestItemList, AttachmentList)
                    binding.attachmentRecycler.adapter = attachadapter
                    binding.itemsRecycler.adapter = adapter
                    adapter.notifyDataSetChanged()
                    attachadapter.notifyDataSetChanged()

                    binding.attach.isVisible = attachadapter.itemCount != 0

                    setData(response.data[0])

                } else {
                    Global.warningmessagetoast(this, response.message);
                }
            }
        ))

    }

    private fun setData(dataPartOne: DataPartOne) {
        if (dataPartOne.EmployeeDetails != null){
            binding.tvServiceEngineetName.text = dataPartOne.EmployeeDetails.SalesEmployeeName
        }else{
            binding.tvServiceEngineetName.text = "NA"
        }
        if (dataPartOne.RequestedDate.isNotEmpty()){
            binding.tvRequestDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataPartOne.RequestedDate)
        }else{
            binding.tvRequestDate.text = "NA"
        }
        if (dataPartOne.ApproverId.isNotEmpty()){
            binding.tvPRApprover.text = dataPartOne.ApproverId
        }else{
            binding.tvPRApprover.text = "NA"
        }

        if (dataPartOne.WarrantyStatus.isNotEmpty()){
            binding.tvWarrantStatus.text = dataPartOne.WarrantyStatus
        }else{
            binding.tvWarrantStatus.text = "NA"
        }
        if (dataPartOne.WarrantyDate.isNotEmpty()){
            binding.tvWarrantyValidDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(dataPartOne.WarrantyDate)
        }else{
            binding.tvWarrantyValidDate.text = "NA"
        }
        if (dataPartOne.PRStatusRemarks.isNotEmpty()){
            binding.tvRemarks.text = dataPartOne.PRStatusRemarks
        }else{
            binding.tvRemarks.text = "NA"
        }
        binding.tvRejectDate.text = "NA"

        if (dataPartOne.BusinessPartnerDetails != null){
            if (dataPartOne.BusinessPartnerDetails.CardName.isNotEmpty()) {
                binding.tvCustomerName.text = dataPartOne.BusinessPartnerDetails.CardName
            }else{
                binding.tvCustomerName.text = "NA"
            }
            if (dataPartOne.BusinessPartnerDetails.Phone1.isNotEmpty()) {
                binding.tvCustomerPhone.text = dataPartOne.BusinessPartnerDetails.Phone1
            }else{
                binding.tvCustomerPhone.text = "NA"
            }
            if (dataPartOne.BusinessPartnerDetails.EmailAddress.isNotEmpty()) {
                binding.tvCustomerEmail.text = dataPartOne.BusinessPartnerDetails.EmailAddress
            }else{
                binding.tvCustomerEmail.text = "NA"
            }

        }
        if (dataPartOne.BusinessPartnerDetails.BPAddresses.isNotEmpty()) {
            binding.tvCustAddress.text = dataPartOne.BusinessPartnerDetails.BPAddresses[0].AddressName + " " + dataPartOne.BusinessPartnerDetails.BPAddresses[0].City + " "+
                    dataPartOne.BusinessPartnerDetails.BPAddresses[0].U_STATE + " "+ dataPartOne.BusinessPartnerDetails.BPAddresses[0].ZipCode + " " +dataPartOne.BusinessPartnerDetails.BPAddresses[0].U_COUNTRY
        }else{
            binding.tvCustAddress.text = "NA"
        }

    }


    private fun setDefaults() {
        binding.toolbarview.heading.text = "Part Request Item"
        binding.toolbarview.backPress.setOnClickListener {
            onBackPressed()
        }

    }


}