package com.ahuja.sons.ahujaSonsClasses.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.*
import com.ahuja.sons.ahujaSonsClasses.model.*
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivitySalesPersonRoleDetailBinding
import com.ahuja.sons.databinding.BottomSheetItemListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SalesPersonRoleDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivitySalesPersonRoleDetailBinding
    var dependencyOrderAdapter = DependencyOrderAdapter()
    var earrandsOrderAdapter = EarrandsOrderAdapter()
    var deliveryDetailAdapter = DeliveryDetailsItemAdapter()
    lateinit var viewModel: MainViewModel

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    var OrderID = ""
    
    companion object{
        private const val TAG = "SalesPersonRoleDetailAc"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesPersonRoleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.linearSelectOrder.visibility = View.GONE //todo

        setUpViewModel()


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
            finish()
        }


        OrderID = intent.getStringExtra("id").toString()

        binding.loadingBackFrame.visibility = View.GONE
        binding.loadingView.stop()

        var jsonObject = JsonObject()
        jsonObject.addProperty("id", OrderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail()

        hideAndShowViews()
        setupFlow()


        binding.apply {
            chipCreateearner.setOnClickListener {

                Intent(this@SalesPersonRoleDetailActivity, AddErrandActivity::class.java).also {
                    startActivity(it)
                }

            }

            chipCreateDependency.setOnClickListener {
                Intent(this@SalesPersonRoleDetailActivity, SelectOrderForCreateDependencyActivity::class.java).also {
                    startActivity(it)
                }

            }

            editSaleOrder.setOnClickListener {

                Intent(this@SalesPersonRoleDetailActivity, UpdateSaleOrderActivity::class.java).also {
                    it.putExtra("id", globalDataWorkQueueList.OrderRequest!!.id)
                    startActivity(it)
                }

            }


        }


        initView()


    }


    override fun onResume() {
        super.onResume()
        var jsonObject = JsonObject()
        jsonObject.addProperty("id", OrderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail()
    }

    private fun initView(){
        //todo order details arrow--
        binding.orderUpArrow.setOnClickListener {
            binding.orderDetailsLayout.visibility = View.GONE
            binding.orderUpArrow.visibility = View.GONE
            binding.orderDownArrow.visibility = View.VISIBLE
        }

        binding.orderDownArrow.setOnClickListener {
            binding.orderDetailsLayout.visibility = View.VISIBLE
            binding.orderDownArrow.visibility = View.GONE
            binding.orderUpArrow.visibility = View.VISIBLE
        }


        binding.dispatchUpArrow.setOnClickListener {
            binding.dispatchDetailsLayout.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.GONE
            binding.dispatchDownArrow.visibility = View.VISIBLE
        }

        binding.dispatchDownArrow.setOnClickListener {
            binding.dispatchDetailsLayout.visibility = View.VISIBLE
            binding.dispatchDownArrow.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.VISIBLE
        }

/*
        binding.surgeryUpArrow.setOnClickListener {
            binding.surgeryDetailsLayout.visibility = View.GONE
            binding.surgeryUpArrow.visibility = View.GONE
            binding.surgeryDownArrow.visibility = View.VISIBLE
        }

        binding.surgeryDownArrow.setOnClickListener {
            binding.surgeryDetailsLayout.visibility = View.VISIBLE
            binding.surgeryDownArrow.visibility = View.GONE
            binding.surgeryUpArrow.visibility = View.VISIBLE
        }*/

        binding.deliveryUpArrow.setOnClickListener {
            binding.rvDeliveryDetail.visibility = View.GONE
            binding.deliveryUpArrow.visibility = View.GONE
            binding.deliveryDownArrow.visibility = View.VISIBLE

        }

        binding.deliveryDownArrow.setOnClickListener {
            binding.rvDeliveryDetail.visibility = View.VISIBLE
            binding.deliveryDownArrow.visibility = View.GONE
            binding.deliveryUpArrow.visibility = View.VISIBLE
        }

    }



    var globalDataWorkQueueList = AllWorkQueueResponseModel.Data()


    //todo...bind observer--
    private fun bindWorkQueueDetail() {
        viewModel.workQueueOne.observe(this, Event.EventObserver(
            onError = {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Global.warningmessagetoast(this, it)
            },
            onLoading = {
                binding.loadingBackFrame.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    if (response.data.size > 0) {
                        var modelData = response.data[0]

                        globalDataWorkQueueList = response.data[0]

                        //todo calling dependency and errand list

                        callDependencyAllList()

                        callErrandsAllList()

                        callDeliveryDetailItemList()

                        callDispatchDetailsApi()

                        bindGetDeliveryDispatchImages()

                        callSurgeryPersonDetailApi()

                        binding.itemDetailView.setOnClickListener {
                            var intent = Intent(this@SalesPersonRoleDetailActivity, ItemDetailActivity::class.java)
                            intent.putExtra("SapOrderId", modelData.OrderRequest!!.SapOrderId)
                            startActivity(intent)
                        }


                        binding.companyName.setText(modelData.OrderRequest!!.CardName)
                        binding.tvOrderID.setText("Order ID : "+modelData.OrderRequest!!.id)
                        binding.tvDoctorName.setText(modelData.OrderRequest!!.Doctor[0].DoctorFirstName + " "+modelData.OrderRequest!!.Doctor[0].DoctorLastName)
                        binding.tvOrderInfo.setText("Order Information :  "+ modelData.OrderRequest!!.OrderInformation)
                        binding.tvSTatus.setText("Status  :  "+ modelData.OrderRequest!!.Status)

                        //todo bind order detail

                        if (modelData.OrderRequest?.id.toString().isNotEmpty()){
                            binding.tvOMSID.setText(modelData.OrderRequest!!.id.toString())
                        }else{
                            binding.tvOMSID.setText("NA")
                        }
                        if (modelData.OrderRequest!!.Employee.isNotEmpty()){
                            binding.tvSalesPerson.setText(modelData.OrderRequest!!.Employee[0].SalesEmployeeName)
                        }else{
                            binding.tvSalesPerson.setText("NA")
                        }

                        if (!modelData.OrderRequest!!.PreparedBy.isNullOrEmpty()){
                            binding.preparedByLayout.visibility = View.VISIBLE
                            binding.tvPreparedBy.setText(modelData.OrderRequest!!.PreparedBy)
                        }else{
                            binding.preparedByLayout.visibility = View.GONE
                            binding.tvPreparedBy.setText("NA")
                        }
                        if (!modelData.OrderRequest!!.InspectedBy.isNullOrEmpty()){
                            binding.inspectedByLayout.visibility = View.VISIBLE
                            binding.tvInspectedBy.setText(modelData.OrderRequest!!.InspectedBy)
                        }else{
                            binding.inspectedByLayout.visibility = View.GONE
                            binding.tvInspectedBy.setText("NA")
                        }

                        if (modelData.OrderRequest!!.Remarks.isNotEmpty()){
                            binding.tvRemarks.setText(modelData.OrderRequest!!.Remarks)
                        }else{
                            binding.tvRemarks.setText("NA")
                        }


                    }

                }


            }

        ))
    }



    var dependencyListModels = ArrayList<AllDependencyAndErrandsListModel.Data>()

    private fun callDependencyAllList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        binding.loadingView.start()
        binding.loadingBackFrame.visibility = View.VISIBLE
        val call: Call<AllDependencyAndErrandsListModel> = ApiClient().service.getDependencyList(jsonObject)
        call.enqueue(object : Callback<AllDependencyAndErrandsListModel?> {
            override fun onResponse(
                call: Call<AllDependencyAndErrandsListModel?>,
                response: Response<AllDependencyAndErrandsListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){
                        binding.tvDependencyNoDataFound.visibility = View.GONE
                        binding.rvDependency.visibility = View.VISIBLE

                        dependencyListModels.clear()
                        dependencyListModels.addAll(response.body()!!.data)

                        dependencyOrderAdapter.submitList(dependencyListModels)

                        setupDependencyRecyclerview()

                    }
                    else{

                        setupDependencyRecyclerview()

                        binding.tvDependencyNoDataFound.visibility = View.VISIBLE
                        binding.rvDependency.visibility = View.GONE
                        binding.dependencyDownArrow.visibility = View.GONE
                        binding.dependencyUpArrow.visibility = View.GONE

                    }

                } else {

                    binding.tvDependencyNoDataFound.visibility = View.VISIBLE
                    binding.rvDependency.visibility = View.GONE
                    binding.dependencyDownArrow.visibility = View.GONE
                    binding.dependencyUpArrow.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e(TAG, "onResponse: "+response.message() )
//                    Global.warningmessagetoast(this@SalesPersonRoleDetailActivity, response.message().toString());

                }
            }

            override fun onFailure(call: Call<AllDependencyAndErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@SalesPersonRoleDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    var errandsListModels = ArrayList<AllErrandsListModel.Data>()

    private fun callErrandsAllList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        binding.loadingView.start()
        binding.loadingBackFrame.visibility = View.VISIBLE
        val call: Call<AllErrandsListModel> = ApiClient().service.getErrandsList(jsonObject)
        call.enqueue(object : Callback<AllErrandsListModel?> {
            override fun onResponse(
                call: Call<AllErrandsListModel?>,
                response: Response<AllErrandsListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){
                        binding.tvErrandsNoDataFound.visibility = View.GONE
                        binding.rvEarrands.visibility = View.VISIBLE

                        errandsListModels.clear()
                        errandsListModels.addAll(response.body()!!.data)

                        earrandsOrderAdapter.submitList(errandsListModels)

                        setupEarrandRecyclerview()


                    }else{

                        setupEarrandRecyclerview()

                        binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                        binding.rvEarrands.visibility = View.GONE
                        binding.errandsDownArrow.visibility = View.GONE
                        binding.errandsUpArrow.visibility = View.GONE

                    }

                } else {
                    binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                    binding.rvEarrands.visibility = View.GONE
                    binding.errandsDownArrow.visibility = View.GONE
                    binding.errandsUpArrow.visibility = View.GONE
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e(TAG, "onResponse: "+response.message() )
//                    Global.warningmessagetoast(this@SalesPersonRoleDetailActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<AllErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@SalesPersonRoleDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    var deliveryItemList_gl = ArrayList<DeliveryItemListModel.Data>()

    private fun callDeliveryDetailItemList() {
        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        binding.loadingView.start()
        binding.loadingBackFrame.visibility = View.VISIBLE
        val call: Call<DeliveryItemListModel> = ApiClient().service.getOrderDeliveryItems(jsonObject)
        call.enqueue(object : Callback<DeliveryItemListModel?> {
            override fun onResponse(
                call: Call<DeliveryItemListModel?>,
                response: Response<DeliveryItemListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    if (response.body()!!.data.size > 0){
                        binding.tvDeliveryDetail.setText("Delivery Details  " + "( "+response.body()!!.data.size.toString()+" )")
                        binding.deliveryDetailLayout.visibility = View.VISIBLE
                        binding.tvDeliveryDetailNoDataFound.visibility = View.GONE
                        binding.rvDeliveryDetail.visibility = View.VISIBLE

                        deliveryItemList_gl.clear()
                        deliveryItemList_gl.addAll(response.body()!!.data)

                        deliveryDetailAdapter.submitList(deliveryItemList_gl)

                        setupDeliveryDetailRecyclerview()


                    }else{
                        setupDeliveryDetailRecyclerview()
                        binding.deliveryDetailLayout.visibility = View.GONE
                        binding.tvDeliveryDetailNoDataFound.visibility = View.VISIBLE
                        binding.rvDeliveryDetail.visibility = View.GONE

                    }

                } else {
                    binding.deliveryDetailLayout.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
//                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<DeliveryItemListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@SalesPersonRoleDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun hideAndShowViews() {

        //todo header arrow-
        binding.headerUpArrow.setOnClickListener {
            binding.tvOrderID.visibility = View.GONE
            binding.tvOrderInfo.visibility = View.GONE
            binding.tvDoctorName.visibility = View.GONE
            binding.headerUpArrow.visibility = View.GONE
            binding.headerDownArrow.visibility = View.VISIBLE
        }

        binding.headerDownArrow.setOnClickListener {
            binding.tvOrderID.visibility = View.VISIBLE
            binding.tvOrderInfo.visibility = View.VISIBLE
            binding.tvDoctorName.visibility = View.VISIBLE
            binding.headerDownArrow.visibility = View.GONE
            binding.headerUpArrow.visibility = View.VISIBLE
        }



        binding.apply {
            errandsUpArrow.setOnClickListener {
                    rvEarrands.visibility = View.GONE
                    errandsUpArrow.visibility = View.GONE
                    errandsDownArrow.visibility = View.VISIBLE

            }

            errandsDownArrow.setOnClickListener {
                rvEarrands.visibility = View.VISIBLE
                errandsDownArrow.visibility = View.GONE
                errandsUpArrow.visibility = View.VISIBLE
            }

            dependencyUpArrow.setOnClickListener {
                rvDependency.visibility = View.GONE
                dependencyUpArrow.visibility = View.GONE
                dependencyDownArrow.visibility = View.VISIBLE

            }

            dependencyDownArrow.setOnClickListener {
                rvDependency.visibility = View.VISIBLE
                dependencyDownArrow.visibility = View.GONE
                dependencyUpArrow.visibility = View.VISIBLE
            }


           /* dependencyUpArrow.setOnClickListener {
                if (rvDependency.visibility == View.VISIBLE) {
                    rvDependency.visibility = View.GONE
                    dependencyUpArrow.setImageResource(R.drawable.arrow_up_icon)

                } else {
                    rvDependency.visibility = View.VISIBLE
                    dependencyDownArrow.setImageResource(R.drawable.down_arrow_icon)
                }
            }*/

            binding.dispatchUpArrow.setOnClickListener {
                binding.statusLayout.visibility = View.GONE
                binding.dispatchDetailsLayout.visibility = View.GONE
                binding.dispatchUpArrow.visibility = View.GONE
                binding.dispatchDownArrow.visibility = View.VISIBLE
            }

            binding.dispatchDownArrow.setOnClickListener {
                binding.statusLayout.visibility = View.VISIBLE
                binding.dispatchDetailsLayout.visibility = View.VISIBLE
                binding.dispatchDownArrow.visibility = View.GONE
                binding.dispatchUpArrow.visibility = View.VISIBLE
            }


            binding.apply {
                surgeryDownArrow.setOnClickListener {
                    surgeryDetailsLayout.visibility = View.VISIBLE
                    surgeryDownArrow.visibility = View.GONE
                    surgeryUpArrow.visibility = View.VISIBLE

                }

                surgeryUpArrow.setOnClickListener {
                    surgeryDownArrow.visibility = View.VISIBLE
                    surgeryUpArrow.visibility = View.GONE
                    surgeryDetailsLayout.visibility = View.GONE
                }

            }

        }
    }


    private fun setupDependencyRecyclerview() = binding.rvDependency.apply {
        adapter = dependencyOrderAdapter
        layoutManager = LinearLayoutManager(this@SalesPersonRoleDetailActivity)
        dependencyOrderAdapter.notifyDataSetChanged()
    }


    private fun setupEarrandRecyclerview() = binding.rvEarrands.apply {
        adapter = earrandsOrderAdapter
        layoutManager = LinearLayoutManager(this@SalesPersonRoleDetailActivity)
        earrandsOrderAdapter.notifyDataSetChanged()
    }

    private fun setupDeliveryDetailRecyclerview() = binding.rvDeliveryDetail.apply {
        adapter = deliveryDetailAdapter
        layoutManager = LinearLayoutManager(this@SalesPersonRoleDetailActivity)
        deliveryDetailAdapter.notifyDataSetChanged()
    }



    //todo call dispatch detail--
    private fun callDispatchDetailsApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<TripDetailModel> = ApiClient().service.getTripDetailsApi(jsonObject1)
        call.enqueue(object : Callback<TripDetailModel?> {
            override fun onResponse(call: Call<TripDetailModel?>, response: Response<TripDetailModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data

                    if (listData.size > 0){

                        var data = listData[0]
                        binding.dispatchDetailLayoutCardView.visibility = View.VISIBLE
                        Log.e(TAG, "onResponse: Trip Detail"+data )
                        if (data.StartAt.isNotEmpty()){

                            binding.apply {
//                                tvStartLocation.setText(data.StartLocation)
                                tvStartTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))


                                if (data.EndAt.isNotEmpty()){
                                    if (data.Deliveryassigned.isNotEmpty()){
                                        tvDeliveryPersonOne.setText(data.Deliveryassigned[0].DeliveryPerson1)
                                        tvDeliveryPersonTwo.setText(data.Deliveryassigned[0].DeliveryPerson2)
                                        tvDeliveryPersonThree.setText(data.Deliveryassigned[0].DeliveryPerson3)
                                        tvVehicleNum.setText(data.Deliveryassigned[0].VechicleNo)

                                    }

                                    tvTripStatus.setText("Status : Ended")
//                                    tvStartLocation.setText(data.StartLocation)
                                    tvStartTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))

//                                    tvDispatchEndLocation.setText(data.EndLocation)
                                    tvEndTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }else{
                                    tvStartTripTime.setText("NA")
//                                    tvStartLocation.setText("NA")
//                                    tvDispatchEndLocation.setText("NA")
                                    tvEndTripTime.setText("NA")

                                }


                            }


                        }

                    }

                    else{
                        binding.dispatchDetailLayoutCardView.visibility = View.GONE
                    }

                } else {
                    binding.dispatchDetailLayoutCardView.visibility = View.GONE
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@SalesPersonRoleDetailActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.dispatchDetailLayoutCardView.visibility = View.GONE
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SalesPersonRoleDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo delovery dispatch upload proof api here---

    var dispatchList = ArrayList<UploadedPictureModel.Data>()
    private fun bindGetDeliveryDispatchImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getDeliveryDispatchProofImage(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    dispatchList = response.body()!!.data


                    bindGETDispatchCameraImagesAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@SalesPersonRoleDetailActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SalesPersonRoleDetailActivity, t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }


    //todo bind dispatch image adater data----
    private fun bindGETDispatchCameraImagesAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.deliveryProofViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.rvDeliveryImage.layoutManager = linearLayoutManager
            binding.rvDeliveryImage.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.deliveryProofViewLayout.visibility = View.GONE

        }


    }


    //todo calling surgery person detail api here---

    var surgeryDetailData_gl: java.util.ArrayList<SurgeryPersonNameListModel.Data> = java.util.ArrayList<SurgeryPersonNameListModel.Data>()

    private fun callSurgeryPersonDetailApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject = JsonObject()
        jsonObject.addProperty("OrderID", globalDataWorkQueueList.OrderRequest?.id)

        val call: Call<SurgeryPersonNameListModel> = ApiClient().service.getSurgeryPersonDetail(jsonObject)
        call.enqueue(object : Callback<SurgeryPersonNameListModel?> {
            override fun onResponse(call: Call<SurgeryPersonNameListModel?>, response: Response<SurgeryPersonNameListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    surgeryDetailData_gl = response.body()!!.data
                    var data = response.body()!!.data
                    if (data.size > 0){

                        binding.apply {
                            surgeryDetailCardView.visibility = View.VISIBLE

                            tvSurgeryStatus.setText("Status : Ended")

                            tvSurgeryVehicleNo.setText("NA")
                            if (data[0].StartAt.isNotEmpty()){
                                tvSurgeryStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data[0].StartAt))
                            }else{
                                tvSurgeryStartTime.setText("NA")
                            }
                            if (data[0].EndAt.isNotEmpty()){
                                tvSurgeryEndTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data[0].EndAt))
                            }else{
                                tvSurgeryEndTime.setText("NA")
                            }
                            if (data[0].StartAt.isNotEmpty() && data[0].EndAt.isNotEmpty()){
                                tvSurgeryDuration.setText(Global.durationGet(data[0].StartAt, data[0].EndAt))
                            }
                            else{
                                tvSurgeryDuration.setText("00:00:00")
                            }
                            tvCRSNo.setText(data[0].NoOfCSRRequired)

                            val innerAdapter = SurgeryNameListAdapter(data)
                            rvSurgeryPersonListName.layoutManager = LinearLayoutManager(this@SalesPersonRoleDetailActivity, LinearLayoutManager.VERTICAL, false)
                            rvSurgeryPersonListName.adapter = innerAdapter
                            innerAdapter.notifyDataSetChanged()

                        }

                        getSurgeryUploadProofAPi()

                    }
                    else{

                        binding.loadingBackFrame.visibility = View.GONE
                        binding.loadingView.stop()
                        binding.surgeryDetailCardView.visibility = View.GONE
                        val innerAdapter = SurgeryNameListAdapter(java.util.ArrayList())
                        innerAdapter.notifyDataSetChanged()

                    }
                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    binding.surgeryDetailCardView.visibility = View.GONE
                    Log.e(TAG, "onResponse: "+response.body()!!.message)
//                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<SurgeryPersonNameListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Toast.makeText(this@SalesPersonRoleDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    //todo get surgery image proof list images--

    var surgeryProofList = java.util.ArrayList<UploadedPictureModel.Data>()
    private fun getSurgeryUploadProofAPi() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getSurgeryProof(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    surgeryProofList = response.body()!!.data
                    bindSurgeryPersonUploadedProofAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@SalesPersonRoleDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SalesPersonRoleDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind surgery image adapter data----
    private fun bindSurgeryPersonUploadedProofAdapter(mArrayUriList: java.util.ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.surgeryProofViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.rvSurgeryList.layoutManager = linearLayoutManager
            binding.rvSurgeryList.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.surgeryProofViewLayout.visibility = View.GONE

        }


    }



    private fun setupFlow() {
        binding.apply {
            btnOk.setOnClickListener {
                showItemListDialogBottomSheetDialog()

            }
        }
    }

    private fun showItemListDialogBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetItemListBinding = BottomSheetItemListBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())

        bottomSheetDialog.show()

        bindingBottomSheet.headingMore.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        var itemInOrderForDeliveryCoordinatorAdapter = ItemInOrderForDeliveryCoordinatorAdapter()

        bindingBottomSheet.btnConfirm.setOnClickListener {
            binding.apply {
                linearOkCancelButton.visibility = View.GONE
                linearCreateDependencyEarrands.visibility = View.VISIBLE
            }
            bottomSheetDialog.dismiss()
        }
        bindingBottomSheet.rvItemList.apply {
            adapter = itemInOrderForDeliveryCoordinatorAdapter
            layoutManager = LinearLayoutManager(this@SalesPersonRoleDetailActivity)
            itemInOrderForDeliveryCoordinatorAdapter.notifyDataSetChanged()
        }

    }
}