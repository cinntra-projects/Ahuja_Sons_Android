package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.ActivityEmployeeDetailBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.EmployeeOneModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class EmployeeDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityEmployeeDetailBinding
    lateinit var viewModel: MainViewModel

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
        binding = ActivityEmployeeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        binding.backPress.setOnClickListener {
            onBackPressed()
        }

        var id = intent.getStringExtra("id")

        if (Global.checkForInternet(this@EmployeeDetailActivity)) {

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", id)
            viewModel.callEmployeeOneApi(jsonObject)
            bindOneObserver()
        }


    }


    //todo bind observer
    private fun bindOneObserver() {
        viewModel.employeeOneDetail.observe(this,
            com.ahuja.sons.apihelper.Event.EventObserver(
                onError = {
                    Log.e(FileUtil.TAG, "errorInApi: $it")
//                    Global.warningmessagetoast(this@EmployeeDetailActivity, it)
                }, onLoading = {

                },
                onSuccess = {
                    try {
                        if (it.status == 200) {
                            if (it.data.isNotEmpty() && it.data != null) {
                                setDefaultData(it.data[0])
                            }

                        } else {
                            Log.e(FileUtil.TAG, "responseError: ${it.message}")
                            Global.warningmessagetoast(this@EmployeeDetailActivity, it.message!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            ))
    }


    //todo set default data---
    private fun setDefaultData(dataModel: EmployeeOneModel.DataXXX) {
        if (dataModel.firstName.isNotEmpty()) {
            binding.tvFullName.text = dataModel.firstName + " " + dataModel.lastName
        } else {
            binding.tvFullName.text = "NA"
        }
        if (dataModel.EmployeeID.isNotEmpty()) {
            binding.tvEmpID.text = dataModel.EmployeeID
        } else {
            binding.tvEmpID.text = "NA"
        }

        if (dataModel.EmployeeID.isNotEmpty()) {
            binding.tvEmpID.text = dataModel.EmployeeID
        } else {
            binding.tvEmpID.text = "NA"
        }
        if (dataModel.Email.isNotEmpty()) {
            binding.tvEmail.text = dataModel.Email
        } else {
            binding.tvEmail.text = "NA"
        }
        if (dataModel.password.isNotEmpty()) {
            binding.tvPassword.text = dataModel.password
        } else {
            binding.tvPassword.text = "NA"
        }
        if (dataModel.role.isNotEmpty()) {
            binding.tvRole.text = dataModel.role
        } else {
            binding.tvRole.text = "NA"
        }
        if (dataModel.position.isNotEmpty()) {
            binding.tvDesignation.text = dataModel.position
        } else {
            binding.tvDesignation.text = "NA"
        }
        if (dataModel.Mobile.isNotEmpty()) {
            binding.tvPhone.text = dataModel.Mobile
        } else {
            binding.tvPhone.text = "NA"
        }
        if (dataModel.zone.isNotEmpty()) {
            binding.tvZone.text = dataModel.zone
        } else {
            binding.tvZone.text = "NA"
        }


    }


}