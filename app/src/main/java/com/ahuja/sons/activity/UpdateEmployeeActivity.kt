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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.adapter.EmployeeDepartmentAdapter
import com.ahuja.sons.adapter.EmployeeRoleAdapter
import com.ahuja.sons.adapter.ServiceManagerAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityUpdateEmployeeBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.EmployeeData
import com.ahuja.sons.newapimodel.EmployeeCreateRequestModel
import com.ahuja.sons.newapimodel.EmployeeOneModel
import com.ahuja.sons.newapimodel.EmployeeRoleResponseModel
import com.ahuja.sons.newapimodel.EmployeeSubDepResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UpdateEmployeeActivity : AppCompatActivity() {
    lateinit var binding : ActivityUpdateEmployeeBinding
    lateinit var viewModel: MainViewModel
    var StatusNAme = ""
    var zoneValue = ""
    var roleValue = ""
    var reportingValue = ""
    var depValue = ""

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }
        
        var id = intent.getStringExtra("id")


        //todo calling Reposting To employee--
        if (Global.checkForInternet(this@UpdateEmployeeActivity)){

            //todo calling one api for default data---
            var jsonObject = JsonObject()
            jsonObject.addProperty("id", id)
            viewModel.callEmployeeOneApi(jsonObject)
            bindOneObserver()


            viewModel.getEmployeeRoleList()
            bindRoleObserver()

            viewModel.getEmployeeSubDepList()
            bindSubDepartmentObserver()


            viewModel.getSalesEmployeeAllList()
            bindReportingToObserver()

        }
        else{
            Global.warningmessagetoast(this@UpdateEmployeeActivity, "Please Check Internet")
        }


        //todo set frequency type adapter--
        val statusAdapter = ArrayAdapter(this@UpdateEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.statusList_gl)
        binding.acStatus.setAdapter(statusAdapter)

        //todo mode communication item selected
        binding.acStatus.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.statusList_gl.isNotEmpty()) {
                    StatusNAme = Global.statusList_gl[position]
                    binding.acStatus.setText(Global.statusList_gl[position])

                    val adapter = ArrayAdapter(this@UpdateEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.statusList_gl)
                    binding.acStatus.setAdapter(adapter)
                } else {
                    StatusNAme = ""
                    binding.acStatus.setText("")
                }
            }

        }


        //todo set frequency type adapter--
        val zoneAdapter = ArrayAdapter(this@UpdateEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.zoneList_gl)
        binding.acZone.setAdapter(zoneAdapter)

        //todo mode communication item selected
        binding.acZone.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.zoneList_gl.isNotEmpty()) {
                    zoneValue = Global.zoneList_gl[position]
                    binding.acZone.setText(Global.zoneList_gl[position])

                    val adapter = ArrayAdapter(this@UpdateEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.zoneList_gl)
                    binding.acZone.setAdapter(adapter)
                } else {
                    zoneValue = ""
                    binding.acZone.setText("")
                }
            }

        }


        //todo role employee--
        binding.acRole.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (employeeRoleList_gl.size > 0){
                    roleValue = employeeRoleList_gl[pos].Name.toString()
                    binding.acRole.setText(employeeRoleList_gl[pos].Name.toString())
                }else{
                    roleValue = ""
                    binding.acRole.setText("")
                }
            }

        }

        //todo reporting to employee--
        binding.acReportingTO.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (reportingToList_gl.size > 0){
                    reportingValue = reportingToList_gl[pos].getId().toString()
                    binding.acReportingTO.setText(reportingToList_gl[pos].getFirstName() + " ( " + reportingToList_gl[pos].getPosition() + " ) ")
                }else{
                    reportingValue = ""
                    binding.acReportingTO.setText("")
                }
            }

        }


        //todo reporting to employee--
        binding.acDepartment.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (departmentList_gl.size > 0){
                    depValue = departmentList_gl[pos].id
                    binding.acDepartment.setText(departmentList_gl[pos].Name)
                }else{
                    depValue = ""
                    binding.acDepartment.setText("")
                }
            }

        }


        binding.submitBtn.setOnClickListener {
            if (validation(binding.edtFirstName.text.toString(), binding.edtLastName.text.toString(), binding.edtEmail.text.toString(), binding.edtPhone.text.toString())){

                var activeValue = ""
                if (StatusNAme == "Active"){
                    activeValue = "tYES"
                }
                else if (StatusNAme == "Inactive"){
                    activeValue = "tNO"
                }else{
                    activeValue = "tYES"
                }
                var modelData = EmployeeCreateRequestModel(
                    Active = activeValue,
                    Email = binding.edtEmail.text.toString(),
                    EmployeeID = binding.edtEmployeeID.text.toString(),
                    Mobile = binding.edtPhone.text.toString(),
                    SalesEmployeeCode = "",
                    SalesEmployeeName = binding.edtFirstName.text.toString(),
                    branch = "1",
                    companyID = "1",
                    div = "100",
                    firstName = binding.edtFirstName.text.toString(),
                    id = oneDataModel.id.toString(),
                    lastLoginOn = "",
                    lastName = binding.edtLastName.text.toString(),
                    middleName = "",
                    password = oneDataModel.password,
                    passwordUpdatedOn = "",
                    position = roleValue,
                    reportingTo = reportingValue,
                    role = roleValue,
                    salesUnit = "",
                    subdep = depValue,
                    timestamp = Global.getCurrentDateTimeFormatted(),
                    userName = binding.edtEmail.text.toString(),
                    zone = zoneValue
                )

                val gson = Gson()
                val jsonTut: String = gson.toJson(modelData)
                Log.e("data", jsonTut)
                if (Global.checkForInternet(this)) {
                    viewModel.updateEmployee(modelData)
                    bindCreateEmployeeObserver()
                }
            }
        }
        
        

    }

    var oneDataModel = EmployeeOneModel.DataXXX()

    //todo bind one observer
    private fun bindOneObserver() {
        viewModel.employeeOneDetail.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@UpdateEmployeeActivity, it)
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
                            Global.warningmessagetoast(this@UpdateEmployeeActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            ))
    }

    private fun setDefaultData(dataModel: EmployeeOneModel.DataXXX) {
        val adapter = ArrayAdapter(this@UpdateEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.statusList_gl)
        binding.acStatus.setAdapter(adapter)


        if (dataModel.firstName.isNotEmpty()) {
            binding.edtFirstName.setText(dataModel.firstName)
        } else {
            binding.edtFirstName.setText("")
        }
        if (dataModel.lastName.isNotEmpty()) {
            binding.edtLastName.setText(dataModel.lastName)
        } else {
            binding.edtLastName.setText("")
        }
        if (dataModel.EmployeeID.isNotEmpty()) {
            binding.edtEmployeeID.setText(dataModel.EmployeeID)
        } else {
            binding.edtEmployeeID.setText("")
        }

        if (dataModel.Active.isNotEmpty()) {
            if (dataModel.Active == "tYES"){
                StatusNAme = "tYES"
                binding.acStatus.setText("Active")
            }else{
                StatusNAme = "tNO"
                binding.acStatus.setText("Inactive")
            }

        } else {
            binding.edtFirstName.setText("")
        }
        if (dataModel.role.isNotEmpty()) {
            roleValue = dataModel.role
            binding.acRole.setText(dataModel.role)
        } else {
            roleValue = ""
            binding.acRole.setText("")
        }
        if (dataModel.position.isNotEmpty()) {
            binding.edtDesignation.setText(dataModel.position)
        } else {
            binding.edtDesignation.setText("")
        }

        if (dataModel.Email.isNotEmpty()) {
            binding.edtEmail.setText(dataModel.Email)
        } else {
            binding.edtEmail.setText("")
        }
        if (dataModel.Mobile.isNotEmpty()) {
            binding.edtPhone.setText(dataModel.Mobile)
        } else {
            binding.edtPhone.setText("")
        }

        if (dataModel.zone.isNotEmpty()) {
            zoneValue = dataModel.zone
            binding.acZone.setText(dataModel.zone)
        } else {
            zoneValue = ""
            binding.acZone.setText("")
        }
        val zoneAdapter = ArrayAdapter(this@UpdateEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.zoneList_gl)
        binding.acZone.setAdapter(zoneAdapter)


    }



    private fun bindCreateEmployeeObserver() {
        viewModel.productOneDetailData.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@UpdateEmployeeActivity, it)
                }, onLoading = {
                    binding.loadingback.visibility = View.VISIBLE
                    binding.loadingview.start()
                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Global.successmessagetoast(this@UpdateEmployeeActivity, "Successfully Employee Update")
                            onBackPressed()

                        } else {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@UpdateEmployeeActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        binding.loadingback.visibility = View.GONE
                        binding.loadingview.stop()
                        e.printStackTrace()
                    }

                }
            ))
    }

    var departmentList_gl : ArrayList<EmployeeSubDepResponseModel.DataXXX> = ArrayList()

    //todo bind department observer---
    private fun bindSubDepartmentObserver() {
        viewModel.employeeSubDepList.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@UpdateEmployeeActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            departmentList_gl.clear()
                            departmentList_gl.addAll(it.data)
                            var adapter = EmployeeDepartmentAdapter(this, R.layout.drop_down_item_textview, departmentList_gl)
                            binding.acDepartment.setAdapter(adapter)

                            if (oneDataModel.dep.isNotEmpty()) {
                                for (i in 0..   departmentList_gl.size){
                                    if (oneDataModel.dep == departmentList_gl[i].id){
                                        depValue = oneDataModel.dep
                                        binding.acDepartment.setText(departmentList_gl[i].Name)
                                    }
                                }
                            } else {
                                depValue = ""
                                binding.acDepartment.setText("")
                            }

                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@UpdateEmployeeActivity, it.message)
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                }
            ))
    }

    var employeeRoleList_gl : ArrayList<EmployeeRoleResponseModel.DataXXX> = ArrayList()

    //todo employee role observer---
    private fun bindRoleObserver() {
        viewModel.employeeRoleList.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {

                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@UpdateEmployeeActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            employeeRoleList_gl.clear()
                            employeeRoleList_gl.addAll(it.data)
                            var adapter = EmployeeRoleAdapter(this, R.layout.drop_down_item_textview, employeeRoleList_gl)
                            binding.acRole.setAdapter(adapter)


                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@UpdateEmployeeActivity, it.message)
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                }
            ))
    }


    var reportingToList_gl : ArrayList<EmployeeData> = ArrayList()
    //todo calling Reporting to observer---
    private fun bindReportingToObserver() {
        viewModel.salesEmployeeResponse.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {

                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@UpdateEmployeeActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.getStatus() == 200) {
                            reportingToList_gl.clear()
                            reportingToList_gl.addAll(it.getData())
                            var adapter = ServiceManagerAdapter(this, R.layout.drop_down_item_textview, reportingToList_gl)
                            binding.acReportingTO.setAdapter(adapter)

                            if (oneDataModel.reportingTo.isNotEmpty()) {
                                for (i in 0..   reportingToList_gl.size){
                                    if (oneDataModel.reportingTo == reportingToList_gl[i].getSalesEmployeeCode()){
                                        reportingValue = oneDataModel.reportingTo
                                        binding.acReportingTO.setText(reportingToList_gl[i].getFirstName() + " ( " + reportingToList_gl[i].getRole() + " ) ")
                                    }
                                }
                            } else {
                                reportingValue = ""
                                binding.acReportingTO.setText("")
                            }


                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.getMessage()}")
                            Global.warningmessagetoast(this@UpdateEmployeeActivity, it.getMessage()!!)
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                }
            ))
    }


    private fun validation(
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ): Boolean {
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (lastName.isEmpty()) {
            Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (email.length != 0 && Global.isvalidateemail(binding.edtEmail) && email.isEmpty()) {
            Toast.makeText(this, "Enter Email Address", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (phone.isEmpty()) {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    


}