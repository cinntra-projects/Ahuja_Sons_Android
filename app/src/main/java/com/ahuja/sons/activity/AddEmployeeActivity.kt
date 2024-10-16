package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ahuja.sons.R
import com.ahuja.sons.adapter.EmployeeDepartmentAdapter
import com.ahuja.sons.adapter.EmployeeRoleAdapter
import com.ahuja.sons.adapter.ServiceManagerAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityAddEmployeeBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.EmployeeData
import com.ahuja.sons.newapimodel.EmployeeCreateRequestModel
import com.ahuja.sons.newapimodel.EmployeeRoleResponseModel
import com.ahuja.sons.newapimodel.EmployeeSubDepResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AddEmployeeActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddEmployeeBinding
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
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }


        //todo calling Reposting To employee--
        if (Global.checkForInternet(this@AddEmployeeActivity)){

            viewModel.getEmployeeRoleList()
            bindRoleObserver()

            viewModel.getEmployeeSubDepList()
            bindSubDepartmentObserver()


            viewModel.getSalesEmployeeAllList()
            bindServiceManagerObserver()

        }
        else{
            Global.warningmessagetoast(this@AddEmployeeActivity, "Please Check Internet")
        }


        //todo set frequency type adapter--
        val statusAdapter = ArrayAdapter(this@AddEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.statusList_gl)
        binding.acStatus.setAdapter(statusAdapter)

        //todo mode communication item selected
        binding.acStatus.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.statusList_gl.isNotEmpty()) {
                    StatusNAme = Global.statusList_gl[position]
                    binding.acStatus.setText(Global.statusList_gl[position])

                    val adapter = ArrayAdapter(this@AddEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.statusList_gl)
                    binding.acStatus.setAdapter(adapter)
                } else {
                    StatusNAme = ""
                    binding.acStatus.setText("")
                }
            }

        }


        //todo set frequency type adapter--
        val zoneAdapter = ArrayAdapter(this@AddEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.zoneList_gl)
        binding.acZone.setAdapter(zoneAdapter)

        //todo mode communication item selected
        binding.acZone.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.zoneList_gl.isNotEmpty()) {
                    zoneValue = Global.zoneList_gl[position]
                    binding.acZone.setText(Global.zoneList_gl[position])

                    val adapter = ArrayAdapter(this@AddEmployeeActivity, android.R.layout.simple_dropdown_item_1line, Global.zoneList_gl)
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
                    depValue = departmentList_gl[pos].id.toString()
                    binding.acDepartment.setText(departmentList_gl[pos].Name)
                }else{
                    depValue = ""
                    binding.acDepartment.setText("")
                }
            }

        }


        binding.submitBtn.setOnClickListener {
            if (validation(binding.edtFirstName.text.toString(), binding.edtLastName.text.toString(), binding.edtPassword.text.toString(), binding.edtEmail.text.toString(), binding.edtPhone.text.toString())){

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
                    id = "",
                    lastLoginOn = "",
                    lastName = binding.edtLastName.text.toString(),
                    middleName = "",
                    password = binding.edtPassword.text.toString(),
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
                    viewModel.createEmployee(modelData)
                    bindCreateEmployeeObserver()
                }
            }
        }

    }

    private fun bindCreateEmployeeObserver() {
        viewModel.productOneDetailData.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@AddEmployeeActivity, it)
                }, onLoading = {
                    binding.loadingback.visibility = View.VISIBLE
                    binding.loadingview.start()
                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Global.successmessagetoast(this@AddEmployeeActivity, "Successfully Employee Create")
                            onBackPressed()

                        } else {
                            binding.loadingback.visibility = View.GONE
                            binding.loadingview.stop()
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@AddEmployeeActivity, it.message!!)
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
//                    Global.warningmessagetoast(this@AddEmployeeActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            departmentList_gl.clear()
                            departmentList_gl.addAll(it.data)
                            var adapter = EmployeeDepartmentAdapter(this, R.layout.drop_down_item_textview, departmentList_gl)
                            binding.acDepartment.setAdapter(adapter)

                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@AddEmployeeActivity, it.message)
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
//                    Global.warningmessagetoast(this@AddEmployeeActivity, it)
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
                            Global.warningmessagetoast(this@AddEmployeeActivity, it.message)
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                }
            ))
    }


    var reportingToList_gl : ArrayList<EmployeeData> = ArrayList()
    //todo calling Reporting to observer---
    private fun bindServiceManagerObserver() {
        viewModel.salesEmployeeResponse.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {

                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@AddEmployeeActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.getStatus() == 200) {
                            reportingToList_gl.clear()
                            reportingToList_gl.addAll(it.getData())
                            var adapter = ServiceManagerAdapter(this, R.layout.drop_down_item_textview, reportingToList_gl)
                            binding.acReportingTO.setAdapter(adapter)


                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.getMessage()}")
                            Global.warningmessagetoast(this@AddEmployeeActivity, it.getMessage()!!)
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
        password: String,
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
        else if (password.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
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