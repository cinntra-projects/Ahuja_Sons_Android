package com.ahuja.sons.ahujaSonsClasses.activity


import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.model.DoctorNameListModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.OrderOneResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityUpdateSaleOrderBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class UpdateSaleOrderActivity : AppCompatActivity() {
    lateinit var binding : ActivityUpdateSaleOrderBinding
    lateinit var viewModel: MainViewModel
    var HospitalName = ""
    var HospitalCode = ""

    var DoctorName = ""
    var DoctorCode = ""

    var OrderId = 0

    companion object{
        private const val TAG = "UpdateSaleOrderActivity"

    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateSaleOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        OrderId = intent.getIntExtra("id",0)

        val toolbar = findViewById<Toolbar>(com.ahuja.sons.R.id.toolbarChanchal)
        setSupportActionBar(toolbar)

        binding.loadingView.stop()
        binding.loadingback.visibility = View.GONE


        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
            finish()
        }


        if (Global.checkForInternet(this@UpdateSaleOrderActivity)) {
            var jsonObject = JsonObject()
            jsonObject.addProperty("id", OrderId)
            viewModel.callOrderRequestOneApi(jsonObject)
            bindOneObserver()
        }


        viewModel.getBPList()
        bindHospitalObserver()


        viewModel.getDoctorNameList()
        bindDoctorNameObserver()


        binding.loadingback.visibility = View.GONE
        binding.loadingView.stop()
        binding.heading.text = "Update Order"


        binding.search.visibility= View.GONE

        val order = arrayOf("0","1", "2", "3")
        val orderInfoAdapter = ArrayAdapter(this, R.layout.drop_down_item_textview, order)
        binding.acCRSNo.setAdapter(orderInfoAdapter)


        binding.edtDate.setOnClickListener {
//            Global.selectDate(this, binding.edtDate)
            Global.disablePastDates(this, binding.edtDate)
        }


        var t1hr = 0
        var t1min = 0

        binding.edtTime.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                t1hr = hourOfDay
                t1min = minute
                val myTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, t1hr)
                    set(Calendar.MINUTE, t1min)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                binding.edtTime.setText(DateFormat.format("hh:mm aa", myTime))
                // setAlarm()
            },
                t1hr, t1min, false
            )
            timePickerDialog.updateTime(t1hr, t1min)
            timePickerDialog.show()
        }



        binding.submitChip.setOnClickListener {
            if (validation(binding.edtOrderInfo.text.toString().trim(), binding.acHospitalName.text.toString().trim(), binding.acDoctorName.text.toString().trim(), binding.edtDate.text.toString().trim(), binding.edtTime.text.toString().trim())){

                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()

                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)

                Log.e(TAG, "onCreate: "+binding.edtDate.text.toString() )
                Log.e(TAG, "onCreate: "+binding.edtTime.text.toString() )

                builder.addFormDataPart("id", OrderId.toString())
                builder.addFormDataPart("CardName", HospitalName)
                builder.addFormDataPart("CardCode", HospitalCode)
                builder.addFormDataPart("Doctor", DoctorCode)
                builder.addFormDataPart("SurgeryName", "")//binding.edtSurgeryName.text.toString()
                builder.addFormDataPart("SurgeryDate", Global.convert_dd_MM_yyyy_into_yyyy_MM_dd(binding.edtDate.text.toString()))
                builder.addFormDataPart("SurgeryTime",  binding.edtTime.text.toString())
                builder.addFormDataPart("NoOfCSRRequired", binding.acCRSNo.text.toString())
                builder.addFormDataPart("Employee", Prefs.getString(Global.MyID, ""))
                builder.addFormDataPart("OrderInformation", binding.edtOrderInfo.text.toString())
                builder.addFormDataPart("Remarks", binding.edRemarks.text.toString())
                builder.addFormDataPart("SapOrderId", "")
                builder.addFormDataPart("Status", "Info Received")
                builder.addFormDataPart("CreateDate", Global.getTodayDateDashFormatReverse())
                builder.addFormDataPart("CreateTime", Global.getTCurrentTime_hh_mm_ss_a())
                builder.addFormDataPart("UpdateDate",  Global.getTodayDateDashFormatReverse())
                builder.addFormDataPart("UpdateTime",  Global.getTCurrentTime_hh_mm_ss_a())
                builder.addFormDataPart("SalesPersonCode", Prefs.getString(Global.Employee_Code, ""))
                builder.addFormDataPart("CreditLimit", "0.0")


                val requestBody = builder.build()
                Log.e("payload", requestBody.toString())

                bindCreateOrderRequestObserver(requestBody)

            }

        }


    }



    //todo bind default data--
    private fun bindOneObserver() {
        viewModel.orderOneDetail.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Log.e(FileUtil.TAG, "errorInApi: $it")
                    Global.warningmessagetoast(this@UpdateSaleOrderActivity, it)
                }, onLoading = {
                    binding.loadingView.start()
                    binding.loadingback.visibility = View.VISIBLE
                },
                onSuccess = {
                    try {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        if (it.status == 200) {
                            if (it.data.isNotEmpty() && it.data != null) {
                                setDefaultData(it.data[0])
                            }

                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@UpdateSaleOrderActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        e.printStackTrace()
                    }

                }
            ))

    }

    //todo set deafult data
    private fun setDefaultData(modelData: OrderOneResponseModel.Data) {

        binding.edtOrderInfo.setText(modelData.OrderInformation)
        binding.acHospitalName.setText(modelData.CardName)
        HospitalName = modelData.CardName
        HospitalCode = modelData.CardCode
        binding.acDoctorName.setText(modelData.Doctor[0].DoctorFirstName)
        DoctorName = modelData.Doctor[0].DoctorFirstName
        DoctorCode = modelData.Doctor[0].DoctorCode
        binding.edtSurgeryName.setText(modelData.SurgeryName)
        binding.edtDate.setText(Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(modelData.SurgeryDate))
        binding.edtTime.setText(modelData.SurgeryTime)
        binding.acCRSNo.setText(modelData.NoOfCSRRequired)
        binding.edRemarks.setText(modelData.Remarks)

        val order = arrayOf("1", "2", "3")
        val orderInfoAdapter = ArrayAdapter(this, R.layout.drop_down_item_textview, order)
        binding.acCRSNo.setAdapter(orderInfoAdapter)

    }


    private fun bindCreateOrderRequestObserver(requestBody: MultipartBody) {
        binding.loadingView.start()
        binding.loadingback.visibility = View.VISIBLE
        val call: Call<OrderOneResponseModel> = ApiClient().service.updateOrderRequestMVC(requestBody)
        call.enqueue(object : Callback<OrderOneResponseModel?> {
            override fun onResponse(call: Call<OrderOneResponseModel?>, response: Response<OrderOneResponseModel?>) {
                if (response.body()!!.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Log.e("data", response.body()!!.data.toString())
                    Global.successmessagetoast(this@UpdateSaleOrderActivity, "Created Order Successfully")
                    onBackPressed()
                    finish()
                } else {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(this@UpdateSaleOrderActivity, response.errorBody().toString());

                }
            }

            override fun onFailure(call: Call<OrderOneResponseModel?>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingback.visibility = View.GONE
                Toast.makeText(this@UpdateSaleOrderActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }



    var AllitemsList = ArrayList<AccountBpData>()

    //todo bind observer...
    private fun bindHospitalObserver() {
        viewModel.businessPartnerList.observe(this, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(this, it)
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
            },
            onLoading = {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    /*AllitemsList.clear()
                    AllitemsList.addAll(response.data)
                      var adapter = HospitalNameAdapter(this, com.ahuja.sons.R.layout.drop_down_item_textview, AllitemsList)
                    binding.acHospitalName.setAdapter(adapter)
                    */

                    AllitemsList.clear()
                    var itemsList = filterList(response.data)
                    AllitemsList.addAll(itemsList)

                    val itemNames = itemsList.map { it.CardName }
                    val cardCodeName = itemsList.map { it.CardCode }

                    val adapter = ArrayAdapter(this, com.ahuja.sons.R.layout.drop_down_item_textview, itemNames)
                    binding.acHospitalName.setAdapter(adapter)

                    // Handle bill to address dropdown item selection
                    binding.acHospitalName.setOnItemClickListener { parent, _, position, _ ->
                        try {
                            val hospitalName = parent.getItemAtPosition(position) as String
                            HospitalName = hospitalName

                            val pos = Global.getHospitalPos(AllitemsList, hospitalName)
                            HospitalCode = AllitemsList[pos].CardCode

                            if (hospitalName.isEmpty()) {
                                binding.hospitalRecyclerViewLayout.visibility = View.GONE
                                binding.rvHospitalList.visibility = View.GONE
                            } else {
                                binding.hospitalRecyclerViewLayout.visibility = View.VISIBLE
                                binding.rvHospitalList.visibility = View.VISIBLE

                            }

                            if (hospitalName.isNotEmpty()) {
                                adapter.notifyDataSetChanged()
                                binding.acHospitalName.setText(hospitalName)
                                binding.acHospitalName.setSelection(hospitalName.length)

                            } else {
                                HospitalName = ""
                                HospitalCode = ""
                                binding.acHospitalName.setText("")
                            }
                        } catch (e: Exception) {
                            Log.e("catch", "onItemClick: ${e.message}")
                            e.printStackTrace()
                        }
                    }



                }


            }

        ))


    }


    private fun filterList(value: List<AccountBpData>): List<AccountBpData> {
        val tempList = mutableListOf<AccountBpData>()
        for (customer in value) {
            if (customer.CardName != "foo") {
                tempList.add(customer)
            }
        }
        return tempList
    }

    private fun doctorNameFilter(value: List<DoctorNameListModel.Data>): List<DoctorNameListModel.Data> {
        val tempList = mutableListOf<DoctorNameListModel.Data>()
        for (customer in value) {
            if (customer.DoctorFirstName != "foo") {
                tempList.add(customer)
            }
        }
        return tempList
    }



    var allDoctorNameList = ArrayList<DoctorNameListModel.Data>()
    //todo bind observer for Doctor...
    private fun bindDoctorNameObserver() {
        viewModel.doctorNameList.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                Global.warningmessagetoast(this, it)
            },
            onLoading = {

                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = { response ->

                if (response.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()

                    allDoctorNameList.clear()
                    var itemsList = doctorNameFilter(response.data)
                    allDoctorNameList.addAll(itemsList)

                    val itemNames = itemsList.map { it.DoctorName }
                    val cardCodeName = itemsList.map { it.id }

                    val adapter = ArrayAdapter(this, com.ahuja.sons.R.layout.drop_down_item_textview, itemNames)
                    binding.acDoctorName.setAdapter(adapter)

                    // Handle bill to address dropdown item selection
                    binding.acDoctorName.setOnItemClickListener { parent, _, position, _ ->
                        try {
                            val doctorName = parent.getItemAtPosition(position) as String
                            DoctorName = doctorName

                            val pos = Global.getDoctorPos(allDoctorNameList, doctorName)
                            DoctorCode = allDoctorNameList[pos].id.toString()

                            if (doctorName.isEmpty()) {
                                binding.doctorRecyclerViewLayout.visibility = View.GONE
                                binding.rvDoctorListName.visibility = View.GONE
                            } else {
                                binding.doctorRecyclerViewLayout.visibility = View.VISIBLE
                                binding.rvDoctorListName.visibility = View.VISIBLE
                            }

                            if (doctorName.isNotEmpty()) {
                                adapter.notifyDataSetChanged()
                                binding.acDoctorName.setText(doctorName)
                                binding.acDoctorName.setSelection(doctorName.length)

                            } else {
                                DoctorName = ""
                                DoctorCode = ""
                                binding.acDoctorName.setText("")
                            }
                        } catch (e: Exception) {
                            Log.e("catch", "onItemClick: ${e.message}")
                            e.printStackTrace()
                        }
                    }



                }



            }

        ))


    }


    fun validation(edtOrderInfo : String, acHospitalName : String, acDoctorName : String, edtDate: String, edtTime: String) : Boolean{
        if (edtOrderInfo.isEmpty()) {
            binding.edtOrderInfo.requestFocus()
            binding.edtOrderInfo.setError("Order Info is Required")
            return false
        }

        else if (acHospitalName.isEmpty()) {
            binding.acHospitalName.requestFocus()
            binding.acHospitalName.setError("Select Hospital is Required")
            return false
        }

        else if (acDoctorName.isEmpty()) {
            binding.acDoctorName.requestFocus()
            binding.acDoctorName.setError("Select Doctor is Required")
            return false
        }

       /* else if (edtSurgeryName.isEmpty()) {
            binding.edtSurgeryName.requestFocus()
            binding.edtSurgeryName.setError("Surgery Name is Required")
            return false
        }*/

        else if (edtDate.isEmpty()) {
            binding.edtDate.requestFocus()
            binding.edtDate.setError("Date is Required")
            return false
        }

        else if (edtTime.isEmpty()) {
            binding.edtTime.requestFocus()
            binding.edtTime.setError("Time is Required")
            return false
        }

        return true
    }



}