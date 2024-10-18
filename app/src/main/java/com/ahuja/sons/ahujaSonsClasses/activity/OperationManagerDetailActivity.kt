package com.ahuja.sons.ahujaSonsClasses.activity

import android.content.ClipData.Item
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.*
import com.ahuja.sons.ahujaSonsClasses.model.*
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityOperationManagerDetailBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OperationManagerDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityOperationManagerDetailBinding
    lateinit var viewModel: MainViewModel
    var earrandsOrderAdapter = EarrandsOrderAdapter()
    var dependencyOrderAdapter = DependencyOrderAdapter()
    var deliveryDetailAdapter = DeliveryDetailsItemAdapter()

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    companion object{
        private const val TAG = "OperationManagerDetailA"
    }

    var orderID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOperationManagerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        orderID = intent.getStringExtra("id")!!


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
        }


        binding.loadingBackFrame.visibility = View.GONE
        binding.loadingView.stop()


        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("")


        hideAndShowViews()

        binding.apply {
            tvCreateOrder.setOnClickListener {
                showPopupMenu(binding.tvCreateOrder)
            }

        }

        if (globalDataWorkQueueList.is_return == true){
            binding.allowIncompleteChipBtn.setText("Allow Incomplete Return")
        }
        else if (globalDataWorkQueueList.is_return == false){
            binding.allowIncompleteChipBtn.setText("Allow Incomplete Dispatch")
        }

        binding.allowIncompleteChipBtn.setOnClickListener {
            showOperationalAction(binding.allowIncompleteChipBtn)
        }

    }

    override fun onResume() {
        super.onResume()
        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("onResume")

    }

    //todo hide and show arrows--

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

            deliveryUpArrow.setOnClickListener {
                rvDeliveryDetail.visibility = View.GONE
                deliveryUpArrow.visibility = View.GONE
                deliveryDownArrow.visibility = View.VISIBLE

            }

            deliveryDownArrow.setOnClickListener {
                rvDeliveryDetail.visibility = View.VISIBLE
                deliveryDownArrow.visibility = View.GONE
                deliveryUpArrow.visibility = View.VISIBLE
            }

        }


        binding.dispatchUpArrow.setOnClickListener {
            binding.statusLayout.visibility = View.GONE
            binding.dispatchTripDetail.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.GONE
            binding.dispatchDownArrow.visibility = View.VISIBLE
        }

        binding.dispatchDownArrow.setOnClickListener {
            binding.statusLayout.visibility = View.VISIBLE
            binding.dispatchTripDetail.visibility = View.VISIBLE
            binding.dispatchDownArrow.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.VISIBLE
        }

        binding.apply {
            surgeryDownArrow.setOnClickListener {
                tvSurgeryStatus.visibility = View.VISIBLE
                surgeryDetailsLayout.visibility = View.VISIBLE
                surgeryDownArrow.visibility = View.GONE
                surgeryUpArrow.visibility = View.VISIBLE

            }

            surgeryUpArrow.setOnClickListener {
                surgeryDownArrow.visibility = View.VISIBLE
                surgeryUpArrow.visibility = View.GONE
                tvSurgeryStatus.visibility = View.GONE
                surgeryDetailsLayout.visibility = View.GONE
            }

        }

        binding.apply {
            pickupDownArrow.setOnClickListener {
                pickUpDetailsLayout.visibility = View.VISIBLE
                pickupDownArrow.visibility = View.GONE
                pickupUpArrow.visibility = View.VISIBLE

            }

            pickupUpArrow.setOnClickListener {
                pickupDownArrow.visibility = View.VISIBLE
                pickupUpArrow.visibility = View.GONE
                pickUpDetailsLayout.visibility = View.GONE
            }

        }

    }

    var globalDataWorkQueueList = AllWorkQueueResponseModel.Data()

    //todo work queue detail api --
    private fun bindWorkQueueDetail(flag : String) {
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


                        //todo set deafult data---
                        setDefaultData(modelData, flag)

                        //todo calling dependency and errand list

                        callDependencyAllList()

                        //todo errands list-

                        callErrandsAllList()

                        //todo delivery list--

                        callDeliveryDetailItemList()


                        bindGetInspectionImages()

                        callDispatchDetailsApi("")

                        callSurgeryPersonDetailApi()

                        if (globalDataWorkQueueList.is_return == true){
                            callPickUpTripDetailsApi("")
                        }

                    }

                }

            }

        ))
    }


    //todo set deafult data here---
    private fun setDefaultData(modelData: AllWorkQueueResponseModel.Data, flag : String) {

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (modelData.OrderRequest!!.CardName.isNotEmpty()!!) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    modelData.OrderRequest!!.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            binding.nameIcon.setImageDrawable(drawable)
        }

        binding.tvSurgeryName.setText(modelData.OrderRequest!!.SurgeryName)
        binding.companyName.setText(modelData.OrderRequest!!.CardName)
        binding.tvOrderID.setText("Order ID : "+modelData.OrderRequest!!.id)
        binding.tvDoctorName.setText(modelData.OrderRequest!!.Doctor[0].DoctorFirstName + " "+modelData.OrderRequest!!.Doctor[0].DoctorLastName)
        binding.tvOrderInfo.setText("Order Information :  "+ modelData.OrderRequest!!.OrderInformation)
        binding.tvSTatus.setText("Status  :  "+ modelData.OrderRequest!!.Status)
        if (!modelData.OrderRequest.SurgeryDate.isNullOrEmpty()){
            binding.tvSurgeryDateAndTime.setText("Surgery Date & Time  :  "+ Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.OrderRequest!!.SurgeryDate) +" "+ modelData.OrderRequest!!.SurgeryTime)
        }
        else{
            binding.tvSurgeryDateAndTime.setText("NA")
        }
        if (modelData.OrderRequest!!.SapOrderId.isNotEmpty()) {
            binding.apply {
                Log.e(TAG, "setDefaultData: SAP ID Not Blank" )

                //todo make dependency / errands editable after sap id linked-

                /*  tvCreateOrder.isClickable = false
                  tvCreateOrder.isFocusable = false
                  tvCreateOrder.isFocusableInTouchMode = false*/


                itemViewDetailCardView.visibility = View.VISIBLE
                itemDetailView.setOnClickListener {
                    var intent = Intent(this@OperationManagerDetailActivity, ItemDetailActivity::class.java)
                    intent.putExtra("SapOrderId", modelData.OrderRequest!!.SapOrderId)
                    startActivity(intent)
                }

            }

        }else{
            binding.apply {
                Log.e(TAG, "setDefaultData: SAP ID  Blank" )

                itemViewDetailCardView.visibility = View.GONE

            }

        }

        //todo bind order detail--

        if (modelData.OrderRequest?.id.toString().isNotEmpty()){
            binding.tvOMSID.setText(modelData.OrderRequest.id.toString())
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


    //todo get inspection images--
    private fun bindGetInspectionImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getInspectionImages(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    bindInspectionUploadedProofAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind inspection image adapter data----
    private fun bindInspectionUploadedProofAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.inspectionViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.inspectionProofImageRecyclerView.layoutManager = linearLayoutManager
            binding.inspectionProofImageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.inspectionViewLayout.visibility = View.GONE

        }


    }


    //todo call trip detail--

    private fun callDispatchDetailsApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID",  globalDataWorkQueueList.OrderRequest!!.id)

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
                        binding.dispatchedDetailLayout.visibility = View.VISIBLE
                        if (data.StartAt.isNotEmpty()){

                            binding.apply {
                                tvStartLocation.setText(data.StartLocation)
                                tvStartTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))

                                if (!data.Deliveryassigned.isNullOrEmpty()){
                                    Log.e(TAG, "Deliveryassigned: "+data.Deliveryassigned )
                                    tvDeliveryPersonOne.setText(data.Deliveryassigned[0].DeliveryPerson1)
                                    tvDeliveryPersonTwo.setText(data.Deliveryassigned[0].DeliveryPerson2)
                                    tvDeliveryPersonThree.setText(data.Deliveryassigned[0].DeliveryPerson3)
                                    tvVehicleNo.setText(data.Deliveryassigned[0].VechicleNo)
                                }

                                if (data.EndAt.isNotEmpty() && data.EndLocation.isNotEmpty()){
                                    linearTripEndDetails.visibility = View.VISIBLE
                                    tvTripStatus.setText("Status : Ended")
                                    tvDispatchEndLocation.setText(data.EndLocation)
                                    tvEndTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }else{
                                    tvTripStatus.setText("Status : Started")
                                    linearTripEndDetails.visibility = View.GONE
                                    tvDispatchEndLocation.setText("NA")
                                    tvEndTripTime.setText("NA")

                                }

                            }
                        }
                    }else{
                        binding.dispatchedDetailLayout.visibility = View.GONE
                    }

                }
                else if (response.body()!!.status == 401){
                    binding.dispatchedDetailLayout.visibility = View.GONE
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                }else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo call pickup trip detail--
    private fun callPickUpTripDetailsApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)

        val call: Call<TripDetailModel> = ApiClient().service.getPickUpTripDetailsApi(jsonObject1)
        call.enqueue(object : Callback<TripDetailModel?> {
            override fun onResponse(call: Call<TripDetailModel?>, response: Response<TripDetailModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    var proofData = response.body()!!.proof_data

                    if (listData.size > 0){

                        var data = listData[0]
                        binding.pickupDetailCardView.visibility = View.VISIBLE
                        binding.pickUpAllDetail.visibility = View.VISIBLE

                        if (data.StartAt.isNotEmpty()){


                            if(globalDataWorkQueueList.is_return == true){

                                binding.apply {

                                    if (data.StartAt.isNotEmpty() && data.EndAt.isEmpty()){
                                        tvPickUpStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))
                                        pickUpEndTimeLayout.visibility = View.GONE
                                    }

                                    if (data.StartAt.isNotEmpty() && data.EndAt.isNotEmpty()){
                                        pickUpEndTimeLayout.visibility = View.VISIBLE
                                        tvPickUpStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))
                                        tvPickUpEndTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                    }

                                    if (data.Deliveryassigned.isNotEmpty()){
                                        Log.e(TAG, "Deliveryassigned: "+data.Deliveryassigned )
                                        tvPickUpDeliveryPersonOne.setText(data.Deliveryassigned[0].DeliveryPerson1)
                                        tvPickUpDeliveryPersonTwo.setText(data.Deliveryassigned[0].DeliveryPerson2)
                                        tvPickUpDeliveryPersonThree.setText(data.Deliveryassigned[0].DeliveryPerson3)
                                        tvVehicleNo.setText(data.Deliveryassigned[0].VechicleNo)

                                    }


                                }

                            }

                            if (!proofData.isNullOrEmpty()){

                                var mArrayUriList : ArrayList<UploadedPictureModel.Data> = ArrayList()

                                mArrayUriList.clear()
                                mArrayUriList.addAll(proofData)

                                if (mArrayUriList.size > 0) {

                                    binding.pickUpViewLayout.visibility = View.VISIBLE

                                    val linearLayoutManager = LinearLayoutManager(this@OperationManagerDetailActivity, LinearLayoutManager.HORIZONTAL, false)
                                    val adapter = PreviousImageViewAdapter(this@OperationManagerDetailActivity, mArrayUriList, arrayOf(), arrayListOf())
                                    binding.pickUpProofRecyclerView.layoutManager = linearLayoutManager
                                    binding.pickUpProofRecyclerView.adapter = adapter
                                    adapter.notifyDataSetChanged()

                                } else {

                                    binding.pickUpViewLayout.visibility = View.GONE

                                }
                            }

                        }
                    }else{
                        binding.pickupDetailCardView.visibility = View.GONE
                    }

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo delivery dispatch upload proof api here---

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

                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }


    //todo bind dispatch image adater data----
    private fun bindGETDispatchCameraImagesAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.dispatchViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.dispatchDeliveryProofRecyclerView.layoutManager = linearLayoutManager
            binding.dispatchDeliveryProofRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.dispatchViewLayout.visibility = View.GONE

        }


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

                    if (response.body()!!.data.size > 0) {
                        binding.tvDependency.setText("Dependency  " + "( "+response.body()!!.data.size.toString()+" )")
                        binding.dependencyCardViewLayout.visibility = View.VISIBLE
                        binding.tvDependencyNoDataFound.visibility = View.GONE
                        binding.rvDependency.visibility = View.VISIBLE

                        dependencyListModels.clear()
                        dependencyListModels.addAll(response.body()!!.data)

                        dependencyOrderAdapter.submitList(dependencyListModels)

                        setupDependencyRecyclerview()

                    } else {
                        binding.dependencyCardViewLayout.visibility = View.GONE
                        setupDependencyRecyclerview()

                        binding.tvDependencyNoDataFound.visibility = View.VISIBLE
                        binding.rvDependency.visibility = View.GONE

                    }

                } else {

                    binding.tvDependencyNoDataFound.visibility = View.VISIBLE
                    binding.rvDependency.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e(TAG, "onResponse: "+response.message() )
//                    Global.warningmessagetoast(
//                        this@OperationManagerDetailActivity,
//                        response.message().toString()
//                    );

                }
            }

            override fun onFailure(call: Call<AllDependencyAndErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(
                    this@OperationManagerDetailActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupDependencyRecyclerview() = binding.rvDependency.apply {
        adapter = dependencyOrderAdapter
        layoutManager = LinearLayoutManager(this@OperationManagerDetailActivity)
        dependencyOrderAdapter.notifyDataSetChanged()
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
                        binding.tvErrands.setText("Errands  " + "( "+response.body()!!.data.size.toString()+" )")
                        binding.errandsCardViewLayout.visibility = View.VISIBLE
                        binding.tvErrandsNoDataFound.visibility = View.GONE
                        binding.rvEarrands.visibility = View.VISIBLE

                        errandsListModels.clear()
                        errandsListModels.addAll(response.body()!!.data)

                        earrandsOrderAdapter.submitList(errandsListModels)

                        setupEarrandRecyclerview()


                    }else{
                        setupEarrandRecyclerview()
                        binding.errandsCardViewLayout.visibility = View.GONE
                        binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                        binding.rvEarrands.visibility = View.GONE

                    }

                } else {
                    binding.errandsCardViewLayout.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e(TAG, "onResponse: "+response.message() )

                }
            }

            override fun onFailure(call: Call<AllErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupEarrandRecyclerview() = binding.rvEarrands.apply {
        adapter = earrandsOrderAdapter
        layoutManager = LinearLayoutManager(this@OperationManagerDetailActivity)
        earrandsOrderAdapter.notifyDataSetChanged()
    }

    //todo delivery detail list items--

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

                }
            }

            override fun onFailure(call: Call<DeliveryItemListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupDeliveryDetailRecyclerview() = binding.rvDeliveryDetail.apply {
        adapter = deliveryDetailAdapter
        layoutManager = LinearLayoutManager(this@OperationManagerDetailActivity)
        deliveryDetailAdapter.notifyDataSetChanged()
    }




    //todo calling surgery person detail api here---

    var surgeryDetailData_gl:  ArrayList<SurgeryPersonNameListModel.Data> = ArrayList<SurgeryPersonNameListModel.Data>()

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
                            rvSurgeryPersonListName.layoutManager = LinearLayoutManager(this@OperationManagerDetailActivity, LinearLayoutManager.VERTICAL, false)
                            rvSurgeryPersonListName.adapter = innerAdapter
                            innerAdapter.notifyDataSetChanged()

                        }

                        getSurgeryUploadProofAPi()

                    }
                    else{

                        binding.loadingBackFrame.visibility = View.GONE
                        binding.loadingView.stop()
                        binding.surgeryDetailCardView.visibility = View.GONE
                        val innerAdapter = SurgeryNameListAdapter(ArrayList())
                        innerAdapter.notifyDataSetChanged()

                    }
                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e(TAG, "onResponse: "+response.body()!!.message)
//                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<SurgeryPersonNameListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    //todo get surgery image proof list images--

    var surgeryProofList = ArrayList<UploadedPictureModel.Data>()
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

                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind surgery image adapter data----
    private fun bindSurgeryPersonUploadedProofAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

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



    //todo show popup dialog---
    private fun showPopupMenu(view: View) {
        // Create a PopupMenu
        val popupMenu = PopupMenu(this, view)
        // Inflate the popup menu using the menu resource file
        popupMenu.getMenuInflater().inflate(R.menu.pop_menu_for_counter_role, popupMenu.getMenu())

        var createOrderItem = popupMenu.menu.findItem(R.id.createOrderItem)

        createOrderItem.isVisible = true

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                return when (p0?.getItemId()) {
                    R.id.menuCreateDependency -> {
                        Intent(this@OperationManagerDetailActivity, SelectOrderForCreateDependencyActivity::class.java).also {
                            it.putExtra("orderID", globalDataWorkQueueList.OrderRequest!!.id.toString())
                            startActivity(it)
                        }
                        true
                    }
                    R.id.menuCreateErrand -> {
                        Intent(this@OperationManagerDetailActivity, AddErrandActivity::class.java).also {
                            it.putExtra("orderID", globalDataWorkQueueList.OrderRequest!!.id.toString())
                            startActivity(it)
                        }
                        true
                    }

                    R.id.createOrderItem -> {
                        Intent(this@OperationManagerDetailActivity, AddSalesOrderActivity::class.java).also {
                            startActivity(it)
                        }
                        true
                    }

                    else -> false
                }
            }

        })


        // Show the popup menu
        popupMenu.show()
    }


    //todo show popup dialog---
    private fun showOperationalAction(view: View) {
        // Create a PopupMenu
        val popupMenu = PopupMenu(this, view)
        // Inflate the popup menu using the menu resource file
        popupMenu.dismiss()
        popupMenu.getMenuInflater().inflate(R.menu.operational_allow_menu, popupMenu.getMenu())

        var allowReturn = popupMenu.menu.findItem(R.id.allowIncompleteReturn)
        var allowDispatch = popupMenu.menu.findItem(R.id.allowIncompleteDispatch)
        var cancelOrder = popupMenu.menu.findItem(R.id.cancelOrder)

        if (globalDataWorkQueueList.is_return == true){
            allowReturn.isVisible = true
            allowReturn.setTitle("Allow Incomplete Return") // Ensuring title is set correctly
            binding.allowIncompleteChipBtn.setText("Allow Incomplete Return")
            cancelOrder.isVisible = true
            allowDispatch.isVisible = false
        }
        else if (globalDataWorkQueueList.is_return == false){
            allowDispatch.isVisible = true
            allowDispatch.setTitle("Allow Incomplete Dispatch") // Ensuring title is set correctly
            cancelOrder.isVisible = true
            allowReturn.isVisible = false
            binding.allowIncompleteChipBtn.setText("Allow Incomplete Dispatch")
//            popupMenu.invalidate()
        }

        Log.d("MenuVisibility", "is_return: ${globalDataWorkQueueList.is_return}, allowReturn visibility: ${allowReturn.isVisible}, allowDispatch visibility: ${allowDispatch.isVisible}")


        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                return when (p0?.getItemId()) {
                    R.id.allowIncompleteReturn -> {
                        callOperationalAllowApi("Allow Incomplete Return")
                        true
                    }
                    R.id.allowIncompleteDispatch -> {
                        callOperationalAllowApi("Allow Incomplete Dispatch")
                        true
                    }
                    R.id.cancelOrder -> {
                        callOperationalAllowApi("Cancel Order")
                        true
                    }
                    else -> false
                }
            }

        })


        // Show the popup menu
        popupMenu.show()
    }


    private fun callOperationalAllowApi(type : String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("ReturnTypeStatus", type)
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)
        jsonObject1.addProperty("CreatedBy", Prefs.getString(Global.Employee_Code))

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.allowIncompleteReturn(jsonObject1)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message)
                    onBackPressed()
                    finish()

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@OperationManagerDetailActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OperationManagerDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


}