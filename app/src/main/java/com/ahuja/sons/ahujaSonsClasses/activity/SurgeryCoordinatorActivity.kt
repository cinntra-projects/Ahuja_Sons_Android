package com.ahuja.sons.ahujaSonsClasses.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.*
import com.ahuja.sons.ahujaSonsClasses.adapter.autoCompleteAdapter.DeliveryPersonAdapter
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryPersonEmployeeModel
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonModelData
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonNameListModel
import com.ahuja.sons.ahujaSonsClasses.model.TripDetailModel
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivitySurgeryCoordinatorBinding
import com.ahuja.sons.databinding.AddSurgeryPersonDialogLayoutBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SurgeryCoordinatorActivity : AppCompatActivity() {
    var addSurgeryPersonAdapter: AddSurgeryPersonAdapter? = null
    var editSurgeryPersonAdapter: UpdateSurgeryPersonAdapter? = null
    var surgeryPersonListAdapter: SurgeryPersonListingAdapter? = null

    var surgeryPersonList = mutableListOf<SurgeryPersonModelData>()
    lateinit var binding: ActivitySurgeryCoordinatorBinding
    var content = ""
    var mutableListSurgery = mutableListOf<String>("Shubham", "Chanchal", "Ankit", "Arif")

    lateinit var viewModel: MainViewModel

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    companion object{
        private const val TAG = "SurgeryCoordinatorActiv"
    }

    var orderID = ""

    var OrderRequestID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurgeryCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        OrderRequestID = intent.getStringExtra("OrderRequestID")!!

        binding.loadingBackFrame.visibility = View.GONE
        binding.loadingView.stop()

        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("")


        hideAndShowViews()

        binding.addSurgerPersonBtn.setOnClickListener {
            showAddSurgeryDialog(this@SurgeryCoordinatorActivity, "AddSurgery")
        }


        binding.submitBtnChip.setOnClickListener {
            submitSurgeryCoordinator()
        }

        binding.tvEditSurgery.setOnClickListener {
            showEditSurgeryDialog(this@SurgeryCoordinatorActivity, "EditSurgery")
        }



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


        binding.surgeryUpArrow.setOnClickListener {
            binding.surgeryDetailsLayout.visibility = View.GONE
            binding.surgeryUpArrow.visibility = View.GONE
            binding.surgeryDownArrow.visibility = View.VISIBLE
        }

        binding.surgeryDownArrow.setOnClickListener {
            binding.surgeryDetailsLayout.visibility = View.VISIBLE
            binding.surgeryDownArrow.visibility = View.GONE
            binding.surgeryUpArrow.visibility = View.VISIBLE
        }


    }


    var globalDataWorkQueueList = AllWorkQueueResponseModel.Data()

    //todo work queue detail api --
    private fun bindWorkQueueDetail(fromWhere: String) {
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
                        setDefaultData(modelData, fromWhere)

                        bindGetInspectionImages()

                        bindGetDeliveryDispatchImages()

                        callTripDetailsApi("")

                        callSurgeryDetailApi()



                    }


                }


            }

        ))
    }


    //todo set default data here---
    private fun setDefaultData(modelData: AllWorkQueueResponseModel.Data, fromWhere: String) {

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
                    var intent = Intent(this@SurgeryCoordinatorActivity, ItemDetailActivity::class.java)
                    intent.putExtra("SapOrderId", modelData.OrderRequest!!.SapOrderId)
                    intent.putExtra("deliveryID", orderID)
                    intent.putExtra("flag", "Delivery Person")
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


    //todo order detail images--
    private fun bindGetInspectionImages() {
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<UploadedPictureModel> = ApiClient().service.getInspectionImages(jsonObject1)
        call.enqueue(object : Callback<UploadedPictureModel?> {
            override fun onResponse(call: Call<UploadedPictureModel?>, response: Response<UploadedPictureModel?>) {
                if (response.body()!!.status == 200) {

                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data
                    bindGETCameraImagesAdapter(listData)

                } else {

                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //todo bind image adapter data----
    private fun bindGETCameraImagesAdapter(mArrayUriList: ArrayList<UploadedPictureModel.Data>) {

        if (mArrayUriList.size > 0) {

            binding.inspectionViewLayout.visibility = View.VISIBLE

            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = PreviousImageViewAdapter(this, mArrayUriList, arrayOf(), arrayListOf())
            binding.proofImageRecyclerView.layoutManager = linearLayoutManager
            binding.proofImageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        } else {
            binding.inspectionViewLayout.visibility = View.GONE

        }


    }


    //todo order detail images--

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

                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo bind image data----
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


    //todo call trip detail--
    private fun callTripDetailsApi(flag: String) {
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
                                    tvVehicleNum.setText(data.Deliveryassigned[0].VechicleNo)
                                }

                                if (data.EndAt.isNotEmpty() && data.EndLocation.isNotEmpty()){
                                    dispatchEndTimeLayout.visibility = View.VISIBLE
                                    tvTripStatus.setText("Status : Ended")
                                    tvDispatchEndLocation.setText(data.EndLocation)
                                    tvEndTripTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.EndAt))

                                }else{
                                    tvTripStatus.setText("Status : Started")
                                    dispatchEndTimeLayout.visibility = View.GONE
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
                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //todo calling submit person api here---

    private fun submitSurgeryCoordinator() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject = JsonObject()
        jsonObject.addProperty("OrderID", globalDataWorkQueueList.OrderRequest?.id)
        jsonObject.addProperty("CreatedBy", Prefs.getString(Global.Employee_SalesEmpCode))

        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.submitSurgeryCoordinator(jsonObject)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {

                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.successmessagetoast(this@SurgeryCoordinatorActivity, response.message())
                    onBackPressed()
                    finish()

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //todo calling surgery person assign detail api here---

    var surgeryDetailList_gl : kotlin.collections.MutableList<SurgeryPersonNameListModel.Data> = mutableListOf()

    private fun callSurgeryDetailApi() {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject = JsonObject()
        jsonObject.addProperty("OrderID", globalDataWorkQueueList.OrderRequest?.id)

        val call: Call<SurgeryPersonNameListModel> = ApiClient().service.getAssignDetail(jsonObject)
        call.enqueue(object : Callback<SurgeryPersonNameListModel?> {
            override fun onResponse(call: Call<SurgeryPersonNameListModel?>, response: Response<SurgeryPersonNameListModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    var data = response.body()!!.data
                    surgeryDetailList_gl = response.body()!!.data
                    if (data.isNotEmpty()){
                        binding.apply {
                            surgeryDetailCardView.visibility = View.VISIBLE
                            submitChipGroup.visibility = View.VISIBLE
                            addSurgeryChipGroup.visibility = View.GONE
                           /*
                            tvSurgeryVehicleNo.setText(data[0].)
                            tvSurgeryDuration.setText(data[0].)
                            tvCRSNo.setText(data[0].)*/

                            tvSurgeryStatus.setText("Status : Assigned")
                            val innerAdapter = SurgeryNameListAdapter(data)
                            rvSurgeryPersonListName.layoutManager = LinearLayoutManager(this@SurgeryCoordinatorActivity, LinearLayoutManager.VERTICAL, false)
                            rvSurgeryPersonListName.adapter = innerAdapter
                            innerAdapter.notifyDataSetChanged()

                        }
                    }
                    else{
                        binding.submitChipGroup.visibility = View.GONE
                        binding.addSurgeryChipGroup.visibility = View.VISIBLE
                        binding.loadingBackFrame.visibility = View.GONE
                        binding.loadingView.stop()
                        binding.surgeryDetailCardView.visibility = View.GONE
                        val innerAdapter = SurgeryNameListAdapter(ArrayList())
                        innerAdapter.notifyDataSetChanged()
                    }
                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    binding.submitChipGroup.visibility = View.GONE
                    binding.addSurgeryChipGroup.visibility = View.VISIBLE
                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<SurgeryPersonNameListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                binding.submitChipGroup.visibility = View.GONE
                binding.addSurgeryChipGroup.visibility = View.VISIBLE
                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    lateinit var rvSurgeryPersons: RecyclerView
    lateinit var rvSurgeryPersonsList: RecyclerView
    lateinit var dialogBinding: AddSurgeryPersonDialogLayoutBinding

    //todo show surgery dialog here---
    private fun showAddSurgeryDialog(context: Context, str: String) {

        val dialog = Dialog(context)

        val layoutInflater = LayoutInflater.from(context)
        dialogBinding = AddSurgeryPersonDialogLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        /*
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))*/

        dialog.getWindow()!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.show()

        rvSurgeryPersons = dialogBinding.rvSurgeryPersons
        rvSurgeryPersonsList = dialogBinding.rvSurgeryPersonsList

        dialogBinding.cancelSurgeryPerson.setOnClickListener {
            dialog.cancel()
        }

        dialogBinding.ivCloseDialog.setOnClickListener {
            dialog.cancel()
        }


        callSurgeryPersonApi(dialogBinding.acSurgeryPerson1, dialogBinding.acSurgeryPerson2)

        addSurgeryPersonAdapter = AddSurgeryPersonAdapter(this@SurgeryCoordinatorActivity, surgeryPersonList)
        bindFocItemAdapter()

        dialogBinding.addSurgeryPerson.setOnClickListener {
            val newItem = SurgeryPersonModelData(
                SurgeryPersonCode = "", SurgeryPersonsName = "", OrderID = orderID, CreatedBy = Prefs.getString(Global.Employee_SalesEmpCode), id = "", OrderRequestID = globalDataWorkQueueList.OrderRequest!!.id.toString()
            )
            addSurgeryPersonAdapter!!.addItem(newItem)
            content = ""
            dialogBinding.rvSurgeryPersons.scrollToPosition(surgeryPersonList.size)
        }

        var autocompleteAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mutableListSurgery)
        dialogBinding.acSurgeryPerson1.setAdapter(autocompleteAdapter)
        dialogBinding.acSurgeryPerson2.setAdapter(autocompleteAdapter)

        dialogBinding.acSurgeryPerson1.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Global.successmessagetoast(this@SurgeryCoordinatorActivity, mutableListSurgery[p2].toString())
                dialogBinding.acSurgeryPerson1.setText(" ${mutableListSurgery[p2].toString()}")
                var autocompleteAdapter = ArrayAdapter<String>(this@SurgeryCoordinatorActivity, android.R.layout.simple_dropdown_item_1line, mutableListSurgery)
                dialogBinding.acSurgeryPerson1.setAdapter(autocompleteAdapter)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        dialogBinding.acSurgeryPerson2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Global.successmessagetoast(this@SurgeryCoordinatorActivity, mutableListSurgery[p2].toString())
                dialogBinding.acSurgeryPerson2.setText(" ${mutableListSurgery[p2].toString()}")
                var autocompleteAdapter = ArrayAdapter<String>(this@SurgeryCoordinatorActivity, android.R.layout.simple_dropdown_item_1line, mutableListSurgery)
                dialogBinding.acSurgeryPerson2.setAdapter(autocompleteAdapter)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        dialogBinding.submitSurgeryPerson.setOnClickListener {
            var writtenSurgeryPersonList=  Global.getNamesCommaSeparated(surgeryPersonList)

            Log.e(TAG, "showAddSurgeryDialog: $writtenSurgeryPersonList" )
            assignedSurgeryPerson(dialog, dialogBinding)

        }


        dialogBinding.tvEdit.setOnClickListener {
            dialogBinding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.VISIBLE
            dialogBinding.surgeryDetailLinearLayout.visibility = View.GONE

        }

        dialog.show()

    }

    var beforeSecondLastList : MutableList<SurgeryPersonNameListModel.Data> = mutableListOf()

    var withPersonCode = mutableListOf<SurgeryPersonNameListModel.Data>()
    var  withoutPersonCode = mutableListOf<SurgeryPersonNameListModel.Data>()
    //todo edit surgery dialog--
    private fun showEditSurgeryDialog(context: Context, str: String) {

        val dialog = Dialog(context)

        val layoutInflater = LayoutInflater.from(context)
        dialogBinding = AddSurgeryPersonDialogLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)


        dialog.getWindow()!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.show()

        rvSurgeryPersons = dialogBinding.rvSurgeryPersons
        rvSurgeryPersonsList = dialogBinding.rvSurgeryPersonsList

        dialogBinding.cancelSurgeryPerson.setOnClickListener {
            dialog.cancel()
        }

        dialogBinding.ivCloseDialog.setOnClickListener {
            dialog.cancel()
        }

        if (surgeryDetailList_gl.size >= 2) {
            // Get the last and second-to-last items
            val lastItem = surgeryDetailList_gl[surgeryDetailList_gl.size - 1]
            val secondLastItem = surgeryDetailList_gl[surgeryDetailList_gl.size - 2]

            // Get all elements before the second-to-last item
            beforeSecondLastList = surgeryDetailList_gl.subList(0, surgeryDetailList_gl.size - 2)

          /*  surgeryPersonOne = lastItem.SurgeryPersonCode
            surgeryPersonTwo = secondLastItem.SurgeryPersonCode
            surgeryPersonOneName = lastItem.SurgeryPersonsName
            surgeryPersonTwoName = secondLastItem.SurgeryPersonsName
            dialogBinding.acSurgeryPerson1.setText(lastItem.SurgeryPersonsName)
            dialogBinding.acSurgeryPerson2.setText(secondLastItem.SurgeryPersonsName)*/

            // Separate the list based on SurgeryPersonCode being empty or not
             withPersonCode = surgeryDetailList_gl.filter { it.SurgeryPersonCode.isNotEmpty() } as MutableList<SurgeryPersonNameListModel.Data>

            surgeryPersonOne = withPersonCode[0].SurgeryPersonCode
            surgeryPersonOneName = withPersonCode[0].SurgeryPersonsName
            surgeryPersonTwo = withPersonCode[1].SurgeryPersonCode
            surgeryPersonTwoName = withPersonCode[1].SurgeryPersonsName
            dialogBinding.acSurgeryPerson1.setText(withPersonCode[0].SurgeryPersonsName)
            dialogBinding.acSurgeryPerson2.setText(withPersonCode[1].SurgeryPersonsName)


        }

         withoutPersonCode = surgeryDetailList_gl.filter { it.SurgeryPersonCode.isEmpty() }.toMutableList()


        callSurgeryPersonApi(dialogBinding.acSurgeryPerson1, dialogBinding.acSurgeryPerson2)

        editSurgeryPersonAdapter = UpdateSurgeryPersonAdapter(this@SurgeryCoordinatorActivity, withoutPersonCode)
        bindEDITFocItemAdapter()

        dialogBinding.addSurgeryPerson.setOnClickListener {
            val newItem = SurgeryPersonNameListModel.Data(
                SurgeryPersonCode = "", SurgeryPersonsName = "", OrderID = orderID, CreatedBy = Prefs.getString(Global.Employee_SalesEmpCode), id = 0
            )
            editSurgeryPersonAdapter!!.addItem(newItem)
            content = ""
            dialogBinding.rvSurgeryPersons.scrollToPosition(withoutPersonCode.size)

        }

        var autocompleteAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mutableListSurgery)
        dialogBinding.acSurgeryPerson1.setAdapter(autocompleteAdapter)
        dialogBinding.acSurgeryPerson2.setAdapter(autocompleteAdapter)

        dialogBinding.acSurgeryPerson1.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Global.successmessagetoast(this@SurgeryCoordinatorActivity, mutableListSurgery[p2].toString())
                dialogBinding.acSurgeryPerson1.setText(" ${mutableListSurgery[p2].toString()}")
                var autocompleteAdapter = ArrayAdapter<String>(this@SurgeryCoordinatorActivity, android.R.layout.simple_dropdown_item_1line, mutableListSurgery)
                dialogBinding.acSurgeryPerson1.setAdapter(autocompleteAdapter)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        dialogBinding.acSurgeryPerson2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Global.successmessagetoast(this@SurgeryCoordinatorActivity, mutableListSurgery[p2].toString())
                dialogBinding.acSurgeryPerson2.setText(" ${mutableListSurgery[p2].toString()}")
                var autocompleteAdapter = ArrayAdapter<String>(this@SurgeryCoordinatorActivity, android.R.layout.simple_dropdown_item_1line, mutableListSurgery)
                dialogBinding.acSurgeryPerson2.setAdapter(autocompleteAdapter)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        dialogBinding.submitSurgeryPerson.setOnClickListener {
            var writtenSurgeryPersonList=  Global.getNamesCommaSeparated(surgeryPersonList)

            Log.e(TAG, "showAddSurgeryDialog: $writtenSurgeryPersonList" )
            updateAssignedSurgeryPerson(dialog, dialogBinding)

        }


        dialogBinding.tvEdit.setOnClickListener {
            dialogBinding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.VISIBLE
            dialogBinding.surgeryDetailLinearLayout.visibility = View.GONE

        }

        dialog.show()

    }


    var surgeryPersonOne = ""
    var surgeryPersonOneName = ""
    var surgeryPersonTwo = ""
    var surgeryPersonTwoName = ""

    //todo calling delivery person api here---

    private fun callSurgeryPersonApi(acSurgeryPerson1: AutoCompleteTextView, acSurgeryPerson2: AutoCompleteTextView) {

        val call: Call<DeliveryPersonEmployeeModel> = ApiClient().service.getSurgeryPerson()
        call.enqueue(object : Callback<DeliveryPersonEmployeeModel?> {
            override fun onResponse(call: Call<DeliveryPersonEmployeeModel?>, response: Response<DeliveryPersonEmployeeModel?>) {
                if (response.body()!!.status == 200) {

                    var adapter = DeliveryPersonAdapter(this@SurgeryCoordinatorActivity, R.layout.drop_down_item_textview ,response.body()!!.data )
                    acSurgeryPerson1.setAdapter(adapter)

                    acSurgeryPerson1.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acSurgeryPerson1.setText(response.body()!!.data[i].SalesEmployeeName)
                            surgeryPersonOne = response.body()!!.data[i].SalesEmployeeCode
                            surgeryPersonOneName = response.body()!!.data[i].SalesEmployeeName
                        }
                    }

                    var personAdapter2 = DeliveryPersonAdapter(this@SurgeryCoordinatorActivity, R.layout.drop_down_item_textview ,response.body()!!.data )
                    acSurgeryPerson2.setAdapter(personAdapter2)

                    acSurgeryPerson2.setOnItemClickListener { adapterView, view, i, l ->
                        if (response.body()!!.data.isNotEmpty()){
                            acSurgeryPerson2.setText(response.body()!!.data[i].SalesEmployeeName)
                            surgeryPersonTwo = response.body()!!.data[i].SalesEmployeeCode
                            surgeryPersonTwoName = response.body()!!.data[i].SalesEmployeeName
                        }
                    }


                } else {

                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<DeliveryPersonEmployeeModel?>, t: Throwable) {

                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //todo bind foc items parts adapter--
    private fun bindFocItemAdapter() = rvSurgeryPersons.apply {
        adapter = addSurgeryPersonAdapter
        layoutManager = LinearLayoutManager(this@SurgeryCoordinatorActivity)

        //todo remove foc items--
        if (addSurgeryPersonAdapter != null) {
            addSurgeryPersonAdapter!!.setOnItemMinusClickListener { s, i ->
                if (addSurgeryPersonAdapter!!.itemCount > 0) {
                    addSurgeryPersonAdapter!!.removeItem(i)
                    addSurgeryPersonAdapter!!.notifyDataSetChanged()
                }
            }
        }

    }


    //todo edit bind foc items parts adapter--
    private fun bindEDITFocItemAdapter() = rvSurgeryPersons.apply {
        adapter = editSurgeryPersonAdapter
        layoutManager = LinearLayoutManager(this@SurgeryCoordinatorActivity)

        //todo remove foc items--
        if (editSurgeryPersonAdapter != null) {
            editSurgeryPersonAdapter!!.setOnItemMinusClickListener { s, i ->
                if (editSurgeryPersonAdapter!!.itemCount > 0) {
                    editSurgeryPersonAdapter!!.removeItem(i)
                    editSurgeryPersonAdapter!!.notifyDataSetChanged()
                }
            }
        }

    }


    //todo bind foc items parts adapter--
    private fun binSurgeryPersonListAdapter() = rvSurgeryPersonsList.apply {
        adapter = surgeryPersonListAdapter
        layoutManager = LinearLayoutManager(this@SurgeryCoordinatorActivity)
    }



    //todo order detail images--
    private fun assignedSurgeryPerson(dialog: Dialog, dialogBinding: AddSurgeryPersonDialogLayoutBinding) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
      //  var writtenSurgeryPersonList=  Global.getNamesCommaSeparated(surgeryPersonList)
        val jsonArray = JsonArray() // Create a new JsonArray
        surgeryPersonList.forEach {surgery->
          var jsonObject=JsonObject().apply {
//              addProperty("id",surgery.id)
              addProperty("OrderID",globalDataWorkQueueList.OrderRequest!!.id)
              addProperty("SurgeryPersonCode",surgery.SurgeryPersonCode)
              addProperty("SurgeryPersonsName",surgery.SurgeryPersonsName)
              addProperty("CreatedBy",surgery.CreatedBy)
          }
            jsonArray.add(jsonObject)

        } // Add each string from the list into the JsonArray

        var jsonObjectSurPerson1=JsonObject().apply {
//            addProperty("id","")
            addProperty("OrderID",globalDataWorkQueueList.OrderRequest!!.id)
            addProperty("SurgeryPersonCode",surgeryPersonOne)
            addProperty("SurgeryPersonsName",surgeryPersonOneName)
            addProperty("CreatedBy",Prefs.getString(Global.Employee_SalesEmpCode))
        }

        var jsonObjectSurPerson2=JsonObject().apply {
//            addProperty("id","")
            addProperty("OrderID",globalDataWorkQueueList.OrderRequest!!.id)//OrderRequestID
            addProperty("SurgeryPersonCode",surgeryPersonTwo)
            addProperty("SurgeryPersonsName",surgeryPersonTwoName)
            addProperty("CreatedBy",Prefs.getString(Global.Employee_SalesEmpCode))

        }

        jsonArray.add(jsonObjectSurPerson1)
        jsonArray.add(jsonObjectSurPerson2)


       /* var jsonObject1 = JsonObject()
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)
        jsonObject1.addProperty("SurgeryPersonCode", surgeryPersonOne+","+surgeryPersonTwo)

        jsonObject1.add("SurgeryPersonsName",jsonArray)
        jsonObject1.addProperty("SalesEmployeeCode", Prefs.getString(Global.Employee_Code,""))
        jsonObject1.addProperty("CreatedBy", Prefs.getString(Global.Employee_role_ID,""))
*/
        Log.e(TAG, "assignedSurgeryPerson: $jsonArray" )


        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.assignedSurgeryPerson(jsonArray)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())
                 /* dialogBinding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.GONE
                    dialogBinding.surgeryDetailLinearLayout.visibility = View.VISIBLE
                    surgeryPersonListAdapter = SurgeryPersonListingAdapter(this@SurgeryCoordinatorActivity, surgeryPersonList)
                    binSurgeryPersonListAdapter()
                    surgeryPersonListAdapter!!.notifyDataSetChanged()*/
                    surgeryPersonList.clear()
                    dialog.hide()
                    binding.addSurgerPersonBtn.visibility = View.GONE
                    binding.submitChipGroup.visibility = View.VISIBLE

                    callSurgeryDetailApi()

                    binding.tvSurgeryStatus.setText("Status : Assigned")

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    binding.addSurgerPersonBtn.visibility = View.VISIBLE
                    binding.submitChipGroup.visibility = View.GONE
                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }


    //todo order detail images--
    private fun updateAssignedSurgeryPerson(dialog: Dialog, dialogBinding: AddSurgeryPersonDialogLayoutBinding) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()

        val jsonArray = JsonArray()
        withPersonCode.forEach {surgery->
          var jsonObject=JsonObject().apply {
              addProperty("id",surgery.id)
              addProperty("OrderID",globalDataWorkQueueList.OrderRequest!!.id)
              addProperty("SurgeryPersonCode",surgery.SurgeryPersonCode)
              addProperty("SurgeryPersonsName",surgery.SurgeryPersonsName)
              addProperty("CreatedBy",surgery.CreatedBy)
          }
            jsonArray.add(jsonObject)

        }

        withoutPersonCode.forEach {surgery->
          var jsonObject=JsonObject().apply {
              addProperty("id",surgery.id)
              addProperty("OrderID",globalDataWorkQueueList.OrderRequest!!.id)
              addProperty("SurgeryPersonCode",surgery.SurgeryPersonCode)
              addProperty("SurgeryPersonsName",surgery.SurgeryPersonsName)
              addProperty("CreatedBy",surgery.CreatedBy)
          }
            jsonArray.add(jsonObject)

        }

      /*  var jsonObjectSurPerson1=JsonObject().apply {
            addProperty("id","")
            addProperty("OrderID",globalDataWorkQueueList.OrderRequest!!.id)
            addProperty("SurgeryPersonCode",surgeryPersonOne)
            addProperty("SurgeryPersonsName",surgeryPersonOneName)
            addProperty("CreatedBy",Prefs.getString(Global.Employee_SalesEmpCode))
        }

        var jsonObjectSurPerson2=JsonObject().apply {
            addProperty("id","")
            addProperty("OrderID",globalDataWorkQueueList.OrderRequest!!.id)
            addProperty("SurgeryPersonCode",surgeryPersonTwo)
            addProperty("SurgeryPersonsName",surgeryPersonTwoName)
            addProperty("CreatedBy",Prefs.getString(Global.Employee_SalesEmpCode))

        }

        jsonArray.add(jsonObjectSurPerson1)
        jsonArray.add(jsonObjectSurPerson2)*/

        Log.e(TAG, "assignedSurgeryPerson: $jsonArray" )


        val call: Call<AllWorkQueueResponseModel> = ApiClient().service.updateAssignedSurgeryPerson(jsonArray)
        call.enqueue(object : Callback<AllWorkQueueResponseModel?> {
            override fun onResponse(call: Call<AllWorkQueueResponseModel?>, response: Response<AllWorkQueueResponseModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())
                 /* dialogBinding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.GONE
                    dialogBinding.surgeryDetailLinearLayout.visibility = View.VISIBLE
                    surgeryPersonListAdapter = SurgeryPersonListingAdapter(this@SurgeryCoordinatorActivity, surgeryPersonList)
                    binSurgeryPersonListAdapter()
                    surgeryPersonListAdapter!!.notifyDataSetChanged()*/
                    surgeryPersonList.clear()
                    dialog.hide()
                    binding.addSurgerPersonBtn.visibility = View.GONE
                    binding.submitChipGroup.visibility = View.VISIBLE

                    callSurgeryDetailApi()

                    binding.tvSurgeryStatus.setText("Status : Assigned")

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    binding.addSurgerPersonBtn.visibility = View.VISIBLE
                    binding.submitChipGroup.visibility = View.GONE
                    Global.warningmessagetoast(this@SurgeryCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<AllWorkQueueResponseModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@SurgeryCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }

        })


    }



}