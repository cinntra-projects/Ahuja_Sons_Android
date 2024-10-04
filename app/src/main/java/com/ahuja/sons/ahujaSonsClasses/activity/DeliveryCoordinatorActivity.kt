package com.ahuja.sons.ahujaSonsClasses.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.DeliveryDetailsItemAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.DependencyOrderAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.EarrandsOrderAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.PreviousImageViewAdapter
import com.ahuja.sons.ahujaSonsClasses.model.AllDependencyAndErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.AllErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryItemListModel
import com.ahuja.sons.ahujaSonsClasses.model.TripDetailModel
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityDeliveryCoordinatorBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class DeliveryCoordinatorActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeliveryCoordinatorBinding
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
    companion object {
        private const val TAG = "DeliveryCoordinatorActi"
        private const val REQUEST_IMAGE_CAPTURE_DIALOG = 100
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1001
    }

    var orderID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()

        setUpViewModel()


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
        }

        orderID = intent.getStringExtra("id")!!

        binding.loadingBackFrame.visibility = View.GONE
        binding.loadingView.stop()


        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail()

        hideAndShowViews()



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
            binding.tvTripStatus.visibility = View.GONE
            binding.dispatchTripDetail.visibility = View.GONE
            binding.dispatchUpArrow.visibility = View.GONE
            binding.dispatchDownArrow.visibility = View.VISIBLE
        }

        binding.dispatchDownArrow.setOnClickListener {
            binding.tvTripStatus.visibility = View.VISIBLE
            binding.dispatchTripDetail.visibility = View.VISIBLE
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


    }


    var globalDataWorkQueueList = AllWorkQueueResponseModel.Data()

    //todo work queue detail api --
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


                        bindGetInspectionImages()


                        //todo calling dependency and errand list

                        callDependencyAllList()

                        callErrandsAllList()

                        callDeliveryDetailItemList()

                        callTripDetailsApi("")

                        //todo set deafult data---
                        setDefaultData(modelData)


                    }


                }


            }

        ))
    }



    //todo set deafult data here---
    private fun setDefaultData(modelData: AllWorkQueueResponseModel.Data) {

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


        binding.companyName.setText(modelData.OrderRequest!!.CardName)
        binding.tvOrderID.setText("Order ID : " + modelData.OrderRequest!!.id)
        binding.tvDoctorName.setText(modelData.OrderRequest!!.Doctor[0].DoctorFirstName + " " + modelData.OrderRequest!!.Doctor[0].DoctorLastName)
        binding.tvOrderInfo.setText("Order Information :  " + modelData.OrderRequest!!.OrderInformation)
        binding.tvSTatus.setText("Status  :  " + modelData.OrderRequest!!.Status)
        if (!modelData.OrderRequest.SurgeryDate.isNullOrEmpty()){
            binding.tvSurgeryDateAndTime.setText("Surgery Date & Time  :  "+ Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.OrderRequest!!.SurgeryDate) +" "+ modelData.OrderRequest!!.SurgeryTime)
        }
        else{
            binding.tvSurgeryDateAndTime.setText("NA")
        }

        if (modelData.OrderRequest!!.SapOrderId.isNotEmpty()) {
            binding.apply {

                itemViewDetailCardView.visibility = View.VISIBLE
                itemDetailView.setOnClickListener {
                    var intent = Intent(this@DeliveryCoordinatorActivity, ItemDetailActivity::class.java)
                    intent.putExtra("SapOrderId", modelData.OrderRequest!!.SapOrderId)
                    intent.putExtra("deliveryID", orderID)
                    intent.putExtra("flag", "Inspection")
                    startActivity(intent)
                }

            }

        } else {
            binding.apply {

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
            binding.tvPreparedBy.setText(modelData.OrderRequest!!.PreparedBy)
        }else{
            binding.tvPreparedBy.setText("NA")
        }
        if (!modelData.OrderRequest!!.InspectedBy.isNullOrEmpty()){
            binding.tvInspectedBy.setText(modelData.OrderRequest!!.InspectedBy)
        }else{
            binding.tvInspectedBy.setText("NA")
        }

        if (modelData.OrderRequest!!.Remarks.isNotEmpty()){
            binding.tvRemarks.setText(modelData.OrderRequest!!.Remarks)
        }else{
            binding.tvRemarks.setText("NA")
        }

    }




    //todo call trip detail--
    private fun callTripDetailsApi(flag: String) {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<TripDetailModel> = ApiClient().service.getTripDetailsApi(jsonObject1)
        call.enqueue(object : Callback<TripDetailModel?> {
            override fun onResponse(call: Call<TripDetailModel?>, response: Response<TripDetailModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data

                    if (listData.size > 0){

                        var data = listData[0]
                        binding.dispatchedDetailLayout.visibility = View.VISIBLE

                        if (data.StartAt.isNotEmpty()){

                            binding.apply {
                                tvStartLocation.setText(data.StartLocation)
                                tvStartTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))


                                if (data.EndAt.isNotEmpty()){
                                    if (data.Deliveryassigned.isNotEmpty()){
                                        tvDeliveryPersonOne.setText(data.Deliveryassigned[0].DeliveryPerson1)
                                        tvDeliveryPersonTwo.setText(data.Deliveryassigned[0].DeliveryPerson2)
                                        tvDeliveryPersonThree.setText(data.Deliveryassigned[0].DeliveryPerson3)
                                        tvVehicleNo.setText(data.Deliveryassigned[0].VechicleNo)

                                    }

                                    tvStartLocation12.setText(data.StartLocation)
                                    tvStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))
                                    tvDispatchEndLocation.setText(data.EndLocation)
                                    tvEndTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }else{
                                    tvStartTime.setText("NA")
                                    tvStartLocation12.setText("NA")
                                    tvDispatchEndLocation.setText("NA")
                                    tvEndTime.setText("NA")

                                }


                            }
                        }
                    }else{
                        binding.dispatchedDetailLayout.visibility = View.GONE
                    }

                } else {

                    Global.warningmessagetoast(this@DeliveryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@DeliveryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()

            }

        })

    }

    private fun bindGetInspectionImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getInspectionImages(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
//                    bindGETCameraImagesAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@DeliveryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@DeliveryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind image data----
    private fun bindGETCameraImagesAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

 /*       if (mArrayUriList.size > 0) {

            binding.inspectionViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), pdfurilist)
            binding.proofImageRecyclerView.layoutManager = linearLayoutManager
            binding.proofImageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.inspectionViewLayout.visibility = View.GONE

        }
*/

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

                        binding.tvDependency.setText("Dependency  " + "( " + response.body()!!.data.size.toString() + " )")
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
//                    Global.warningmessagetoast(this@DeliveryCoordinatorActivity, response.message().toString());

                }
            }

            override fun onFailure(call: Call<AllDependencyAndErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(
                    this@DeliveryCoordinatorActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
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
                    if (response.body()!!.data.size > 0) {
                        binding.tvErrands.setText("Errands  " + "( " + response.body()!!.data.size.toString() + " )")
                        binding.errandsCardViewLayout.visibility = View.VISIBLE
                        binding.tvErrandsNoDataFound.visibility = View.GONE
                        binding.rvEarrands.visibility = View.VISIBLE

                        errandsListModels.clear()
                        errandsListModels.addAll(response.body()!!.data)

                        earrandsOrderAdapter.submitList(errandsListModels)

                        setupEarrandRecyclerview()


                    } else {

                        setupEarrandRecyclerview()
                        binding.errandsCardViewLayout.visibility = View.GONE
                        binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                        binding.rvEarrands.visibility = View.GONE

                    }

                } else {
                    binding.tvErrandsNoDataFound.visibility = View.VISIBLE
                    binding.rvEarrands.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e(TAG, "onResponse: "+response.message() )
//                    Global.warningmessagetoast(this@DeliveryCoordinatorActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<AllErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(
                    this@DeliveryCoordinatorActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
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

                }
            }

            override fun onFailure(call: Call<DeliveryItemListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@DeliveryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupDependencyRecyclerview() = binding.rvDependency.apply {
        adapter = dependencyOrderAdapter
        layoutManager = LinearLayoutManager(this@DeliveryCoordinatorActivity)
        dependencyOrderAdapter.notifyDataSetChanged()
    }


    private fun setupEarrandRecyclerview() = binding.rvEarrands.apply {
        adapter = earrandsOrderAdapter
        layoutManager = LinearLayoutManager(this@DeliveryCoordinatorActivity)
        earrandsOrderAdapter.notifyDataSetChanged()
    }


    private fun setupDeliveryDetailRecyclerview() = binding.rvDeliveryDetail.apply {
        adapter = deliveryDetailAdapter
        layoutManager = LinearLayoutManager(this@DeliveryCoordinatorActivity)
        deliveryDetailAdapter.notifyDataSetChanged()
    }



    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val write =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = mutableListOf<String>()

        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            false
        } else {
            true
        }
    }


}