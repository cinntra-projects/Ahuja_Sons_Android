package com.ahuja.sons.ahujaSonsClasses.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.*
import com.ahuja.sons.ahujaSonsClasses.model.AllDependencyAndErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.AllErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryItemListModel
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonNameListModel
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllItemListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityOrderCoordinatorBinding
import com.ahuja.sons.databinding.BottomSheetItemListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import taimoor.sultani.sweetalert2.Sweetalert
import java.util.*
import kotlin.collections.ArrayList

class OrderCoordinatorActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderCoordinatorBinding
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

    companion object{
        private const val TAG = "OrderCoordinatorActivit"
    }

    var orderID = ""
    var FLAG = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderID = intent.getStringExtra("id")!!
        FLAG = intent.getStringExtra("flag")!!

        binding.linearSelectOrder.visibility = View.VISIBLE

        if (FLAG.equals("WorkQueue")){
            binding.saleOrderLayouts.visibility = View.GONE
        }else{
            binding.saleOrderLayouts.visibility = View.VISIBLE
        }

        setUpViewModel()

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

//        setupFlow()


        //todo on click listeers--
        onClickListeners()


    }



    override fun onResume() {
        super.onResume()

        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail("onResume")

    }


    private fun onClickListeners() {
        binding.apply {
            chipCreateearner.setOnClickListener {

                Intent(this@OrderCoordinatorActivity, AddErrandActivity::class.java).also {
                    it.putExtra("orderID", globalDataWorkQueueList.OrderRequest!!.id.toString())
                    startActivity(it)
                }

            }

            chipCreateDependency.setOnClickListener {

                Intent(this@OrderCoordinatorActivity, SelectOrderForCreateDependencyActivity::class.java).also {
                    startActivity(it)
                }

            }
        }


        binding.btnCancel.setOnClickListener {
            binding.SapOrderIdEdt.setText("")
        }

        binding.btnOk.setOnClickListener {
            callSAPLinkOrderApi()
        }

        binding.ClearSAPID.setOnClickListener {
            openUnLinkSAPID()
        }

        binding.apply {
            tvCreateOrder.setOnClickListener {
                showPopupMenu(binding.tvCreateOrder)
            }

        }


        binding.completeOrderChip.setOnClickListener {
            /*binding.loadingView.start()
            binding.loadingBackFrame.visibility = View.VISIBLE
            var jsonObject = JsonObject()
            jsonObject.addProperty("SalesEmployeeCode", Prefs.getString(Global.Employee_Code, ""))
            jsonObject.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)


            viewModel.completeOrderApi(jsonObject)
            bindObserverCompleteOrderApi()*///todo comment acc. to new requirement

        }


    }

    private fun openUnLinkSAPID() {
        val pDialog = Sweetalert(this@OrderCoordinatorActivity, Sweetalert.WARNING_TYPE)
        pDialog.titleText = "Are you sure?"
        pDialog.contentText = "You want to Clear SAP ID"
        pDialog.setCanceledOnTouchOutside(false)
        pDialog.cancelText = "No,cancel it!"
        pDialog.confirmText = "Yes,Clear!"
        pDialog.showCancelButton(true)
        pDialog.showConfirmButton(true)
        pDialog.setCancelClickListener { sDialog ->
            sDialog.cancel()

        }
        pDialog.setConfirmClickListener {sDialog->
            callSAPUnLinkOrderApi(sDialog)

        }
        pDialog.show()
    }


    //todo unlinked SAP ID--
    private fun callSAPUnLinkOrderApi(sDialog: Sweetalert) {
        var jsonObject = JsonObject()
        jsonObject.addProperty("SapOrderId", globalDataWorkQueueList.OrderRequest!!.SapOrderId)
        jsonObject.addProperty("order_id", globalDataWorkQueueList.OrderRequest!!.id)

        binding.loadingView.start()
        binding.loadingBackFrame.visibility = View.VISIBLE
        val call: Call<AllDependencyAndErrandsListModel> = ApiClient().service.getSAPUnLinkOrderApi(jsonObject)
        call.enqueue(object : Callback<AllDependencyAndErrandsListModel?> {
            override fun onResponse(
                call: Call<AllDependencyAndErrandsListModel?>,
                response: Response<AllDependencyAndErrandsListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())

                    Global.successmessagetoast(this@OrderCoordinatorActivity, response.message().toString());

                    sDialog.cancel()

                    var jsonObject = JsonObject()
                    jsonObject.addProperty("id", orderID)
                    viewModel.callWorkQueueDetailApi(jsonObject)
                    bindWorkQueueDetail("SapUnLinked")

                } else {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<AllDependencyAndErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //todo bind observer for complete order
    private fun bindObserverCompleteOrderApi(bottomSheetDialog: BottomSheetDialog) {
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
                       Global.successmessagetoast(this, response.message)
                        onBackPressed()
                        finish()
                        bottomSheetDialog.dismiss()

                    }
                }

            }

        ))



    }


    private fun showPopupMenu(view: View) {
        // Create a PopupMenu
        val popupMenu = PopupMenu(this, view)
        // Inflate the popup menu using the menu resource file
        popupMenu.getMenuInflater().inflate(R.menu.pop_menu_for_counter_role, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                return when (p0?.getItemId()) {
                    R.id.menuCreateDependency -> {
                        Intent(this@OrderCoordinatorActivity, SelectOrderForCreateDependencyActivity::class.java).also {
                            startActivity(it)
                        }
                        true
                    }
                    R.id.menuCreateErrand -> {
                        Intent(this@OrderCoordinatorActivity, AddErrandActivity::class.java).also {
                            it.putExtra("orderID", globalDataWorkQueueList.OrderRequest!!.id.toString())
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

/*
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


    //todo sap api link for order--
    private fun callSAPLinkOrderApi() {

        if (binding.SapOrderIdEdt.text.toString().trim().isEmpty()){
            Global.errormessagetoast(this@OrderCoordinatorActivity, "Enter SAP Order ID");
        }else{

            var jsonObject = JsonObject()
            jsonObject.addProperty("SalesEmployeeCode", Prefs.getString(Global.Employee_Code, ""))
            jsonObject.addProperty("SapOrderId", binding.SapOrderIdEdt.text.toString().trim())
            jsonObject.addProperty("order_id", globalDataWorkQueueList.OrderRequest!!.id)

            binding.loadingView.start()
            binding.loadingBackFrame.visibility = View.VISIBLE
            val call: Call<AllDependencyAndErrandsListModel> = ApiClient().service.getSAPLinkOrderApi(jsonObject)
            call.enqueue(object : Callback<AllDependencyAndErrandsListModel?> {
                override fun onResponse(
                    call: Call<AllDependencyAndErrandsListModel?>,
                    response: Response<AllDependencyAndErrandsListModel?>
                ) {
                    if (response.body()!!.status == 200) {
                        binding.loadingView.stop()
                        binding.loadingBackFrame.visibility = View.GONE
                        Log.e("data", response.body()!!.data.toString())
                        Global.successmessagetoast(this@OrderCoordinatorActivity, response.message().toString());


                        var jsonObject = JsonObject()
                        jsonObject.addProperty("id", orderID)
                        viewModel.callWorkQueueDetailApi(jsonObject)
                        bindWorkQueueDetail("SapLinked")

                    }
                    else {

                        binding.loadingView.stop()
                        binding.loadingBackFrame.visibility = View.GONE
                        Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.errors.toString());

                    }

                }

                override fun onFailure(call: Call<AllDependencyAndErrandsListModel?>, t: Throwable) {
                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
                    Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })

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

                        //todo calling dependency and errand list

                        callDependencyAllList()

                        callErrandsAllList()

                        callDeliveryDetailItemList()

                        bindGetDeliveryDispatchImages()

                        callSurgeryPersonDetailApi()

                        //todo set deafult data---
                        setDefaultData(modelData, flag)

                    }


                }


            }

        ))
    }


    //todo set deafult data here---
    private fun setDefaultData(modelData: AllWorkQueueResponseModel.Data, flag : String){

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
                SapOrderIdEdt.setText(modelData.OrderRequest!!.SapOrderId)
                SapOrderIdEdt.isClickable = false
                SapOrderIdEdt.isFocusable = false
                SapOrderIdEdt.isFocusableInTouchMode = false

                btnCancel.visibility = View.GONE
                btnOk.visibility = View.VISIBLE

                //todo make dependency / errands editable after sap id linked-

                tvCreateOrder.isClickable = false
                tvCreateOrder.isFocusable = false
                tvCreateOrder.isFocusableInTouchMode = false

                ClearSAPID.visibility = View.VISIBLE
                completeOrderChipLayout.visibility = View.GONE

                binding.orderIDMand.visibility = View.GONE
                binding.orderIDNonMand.visibility = View.VISIBLE

                itemViewDetailCardView.visibility = View.VISIBLE
                itemDetailView.setOnClickListener {
                    var intent = Intent(this@OrderCoordinatorActivity, ItemDetailActivity::class.java)
                    intent.putExtra("SapOrderId", modelData.OrderRequest!!.SapOrderId)
                    startActivity(intent)
                }

            }

        }else{
            binding.apply {
                Log.e(TAG, "setDefaultData: SAP ID  Blank" )
                SapOrderIdEdt.setText("")
                SapOrderIdEdt.isClickable = true
                SapOrderIdEdt.isFocusable = true
                SapOrderIdEdt.isFocusableInTouchMode = true

                tvCreateOrder.isClickable = false
                tvCreateOrder.isFocusable = false
                tvCreateOrder.isFocusableInTouchMode = false

                btnCancel.visibility = View.VISIBLE
                btnOk.visibility = View.VISIBLE
                itemViewDetailCardView.visibility = View.GONE
                ClearSAPID.visibility = View.GONE
                completeOrderChipLayout.visibility = View.GONE

                binding.orderIDMand.visibility = View.VISIBLE
                binding.orderIDNonMand.visibility = View.GONE

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


        if (flag.equals("SapLinked")) {

         /*   binding.SapOrderIdEdt.isClickable = false
            binding.SapOrderIdEdt.isFocusable = false
            binding.SapOrderIdEdt.isFocusableInTouchMode = false

            binding.btnCancel.visibility = View.GONE
            binding.btnOk.visibility = View.GONE
            binding.orderIDMand.visibility = View.GONE
            binding.orderIDNonMand.visibility = View.VISIBLE*/

            if (modelData.OrderRequest!!.DocumentLines.size > 0) {

                showItemListDialogBottomSheetDialog(modelData.OrderRequest, modelData.OrderRequest!!.DocumentLines[0], modelData)

            }

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

                    if (response.body()!!.data.size > 0){

                        binding.tvDependency.setText("Dependency  " + "( "+response.body()!!.data.size.toString()+" )")
                        binding.dependencyCardViewLayout.visibility = View.VISIBLE
                        binding.tvDependencyNoDataFound.visibility = View.GONE
                        binding.rvDependency.visibility = View.VISIBLE

                        dependencyListModels.clear()
                        dependencyListModels.addAll(response.body()!!.data)

                        dependencyOrderAdapter.submitList(dependencyListModels)

                        setupDependencyRecyclerview()

                    }
                    else{
                        binding.dependencyCardViewLayout.visibility = View.GONE
                        setupDependencyRecyclerview()

                        binding.tvDependencyNoDataFound.visibility = View.VISIBLE
                        binding.rvDependency.visibility = View.GONE

                    }

                } else {

                    binding.dependencyCardViewLayout.visibility = View.GONE

                    binding.loadingView.stop()
                    binding.loadingBackFrame.visibility = View.GONE
//                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.message().toString());

                }
            }

            override fun onFailure(call: Call<AllDependencyAndErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
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
//                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.errors.toString());

                }
            }

            override fun onFailure(call: Call<AllErrandsListModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingBackFrame.visibility = View.GONE
                Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupDependencyRecyclerview() = binding.rvDependency.apply {
        adapter = dependencyOrderAdapter
        layoutManager = LinearLayoutManager(this@OrderCoordinatorActivity)
        dependencyOrderAdapter.notifyDataSetChanged()
    }


    private fun setupEarrandRecyclerview() = binding.rvEarrands.apply {
        adapter = earrandsOrderAdapter
        layoutManager = LinearLayoutManager(this@OrderCoordinatorActivity)
        earrandsOrderAdapter.notifyDataSetChanged()
    }


    private fun setupDeliveryDetailRecyclerview() = binding.rvDeliveryDetail.apply {
        adapter = deliveryDetailAdapter
        layoutManager = LinearLayoutManager(this@OrderCoordinatorActivity)
        deliveryDetailAdapter.notifyDataSetChanged()
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

                    Log.e(TAG, "onResponse: "+response.body()!!.errors)
//                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.errors);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {
                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()

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
                            rvSurgeryPersonListName.layoutManager = LinearLayoutManager(this@OrderCoordinatorActivity, LinearLayoutManager.VERTICAL, false)
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
                Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
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

                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.message);

                }
            }

            override fun onFailure(call: Call<UploadedPictureModel?>, t: Throwable) {

                Log.e(TAG, "onFailure: "+t.message )
                Toast.makeText(this@OrderCoordinatorActivity, t.message, Toast.LENGTH_SHORT).show()
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
//                showItemListDialogBottomSheetDialog(modelData.OrderRequest!!.DocumentLines)

            }
        }
    }

    private fun showItemListDialogBottomSheetDialog(orderData: AllWorkQueueResponseModel.OrderRequest?, documentLines: ArrayList<AllWorkQueueResponseModel.DocumentLine>, modelData: AllWorkQueueResponseModel.Data) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetItemListBinding = BottomSheetItemListBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())



        bindingBottomSheet.headingMore.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        bindingBottomSheet.tvOrderName.setText(orderData?.CardName)
        bindingBottomSheet.tvOrderDoctorName.setText(orderData!!.Doctor[0].DoctorFirstName + " " +orderData!!.Doctor[0].DoctorLastName)
        bindingBottomSheet.tvOrderInfo.setText(orderData?.OrderInformation)

        callAllItemListAPi(bindingBottomSheet, modelData)


      /*  var itemInOrderForDeliveryCoordinatorAdapter = ItemInOrderForDeliveryCoordinatorAdapter()

        itemInOrderForDeliveryCoordinatorAdapter.submitList(documentLines)

        bindingBottomSheet.rvItemList.apply {
            adapter = itemInOrderForDeliveryCoordinatorAdapter
            layoutManager = LinearLayoutManager(this@OrderCoordinatorActivity)
            itemInOrderForDeliveryCoordinatorAdapter.notifyDataSetChanged()
        }
*/
        bindingBottomSheet.btnConfirm.setOnClickListener {

          /*  binding.apply {
                linearOkCancelButton.visibility = View.GONE
                linearCreateDependencyEarrands.visibility = View.VISIBLE
            }*///todo dependency and errands created
//            bottomSheetDialog.dismiss()

            binding.loadingView.start()
            binding.loadingBackFrame.visibility = View.VISIBLE
            var jsonObject = JsonObject()
            jsonObject.addProperty("SalesEmployeeCode", Prefs.getString(Global.Employee_Code, ""))
            jsonObject.addProperty("order_request_id", globalDataWorkQueueList.OrderRequest!!.id)


            viewModel.completeOrderApi(jsonObject)
            bindObserverCompleteOrderApi(bottomSheetDialog)

        }

        bottomSheetDialog.show()
    }

    private fun callAllItemListAPi(bindingBottomSheet: BottomSheetItemListBinding, modelData : AllWorkQueueResponseModel.Data) {
        bindingBottomSheet.loadingView.start()
        bindingBottomSheet.loadingback.visibility = View.VISIBLE

        var jsonObject = JsonObject()
        jsonObject.addProperty("order_request_id", modelData.OrderRequest!!.id)


        val call: Call<AllItemListResponseModel> = ApiClient().service.getAllItemListApi(jsonObject)
        call.enqueue(object : Callback<AllItemListResponseModel> {
            override fun onResponse(
                call: Call<AllItemListResponseModel>,
                response: Response<AllItemListResponseModel>
            ) {
                if (response.body()?.status == 200) {

                    if (response.body()!!.data.isEmpty() && response.body()!!.data.size == 0) {
                        bindingBottomSheet.noDataFound.visibility = View.VISIBLE
                    } else {
                        var data = response.body()!!.data
                        var itemInOrderForDeliveryCoordinatorAdapter = ItemInOrderForDeliveryCoordinatorAdapter()

                        itemInOrderForDeliveryCoordinatorAdapter.submitList(data)

                        bindingBottomSheet.rvItemList.apply {
                            adapter = itemInOrderForDeliveryCoordinatorAdapter
                            layoutManager = LinearLayoutManager(this@OrderCoordinatorActivity)
                            itemInOrderForDeliveryCoordinatorAdapter.notifyDataSetChanged()
                        }

                        bindingBottomSheet.noDataFound.visibility = View.GONE

                    }


                    bindingBottomSheet.loadingback.visibility = View.GONE
                    bindingBottomSheet.loadingView.stop()


                } else if (response.body()!!.status == 201) {
                    bindingBottomSheet.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.message)
                } else {
                    bindingBottomSheet.loadingback.visibility = View.GONE
                    bindingBottomSheet.noDataFound.visibility = View.VISIBLE
                    Global.warningmessagetoast(this@OrderCoordinatorActivity, response.body()!!.message)
                }

            }

            override fun onFailure(call: Call<AllItemListResponseModel>, t: Throwable) {
                bindingBottomSheet.loadingback.visibility = View.GONE
                bindingBottomSheet.loadingView.stop()

            }
        })


    }



}