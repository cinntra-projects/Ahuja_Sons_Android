package com.ahuja.sons.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ManTrapLogAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityManTrapRescueLogBinding
import com.ahuja.sons.dialogs.DialogManTrapRescueLog
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.simform.refresh.SSPullToRefreshLayout
import kotlinx.coroutines.*

class ManTrapRescueLogActivity : AppCompatActivity() {
    lateinit var binding: ActivityManTrapRescueLogBinding
    lateinit var viewModel: MainViewModel
    var id = ""
    var manTrapLogAdapter = ManTrapLogAdapter()

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    private fun setUpRecyclerView() = binding.rvManTrap.apply {
        adapter = manTrapLogAdapter
        layoutManager = LinearLayoutManager(this@ManTrapRescueLogActivity)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManTrapRescueLogBinding.inflate(layoutInflater)
        setUpViewModel()
        setContentView(binding.root)

        Glide.with(this).asGif().load(R.raw.alertsos).into(binding.imageBellIcon)

        id = intent.getStringExtra("id").toString()
        binding.toolbarManTrap.toolbarAnnouncement.title =
            resources.getString(R.string.Man_Trap_Rescue_Log)
        binding.toolbarManTrap.toolbarAnnouncement.setOnClickListener {
            finish()
        }
        var data = HashMap<String, Any>()
        data["TicketId"] = id
        viewModel.getManTrapLog(data)
        viewModel.getDropDownManRescue()
        subsribeToObserver()
        setUpRecyclerView()

        binding.ssPullRefresh.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                viewModel.getManTrapLog(data)
                //   viewModel.getDropDownManRescue()
                subsribeToObserver()
                binding.ssPullRefresh.setRefreshing(false)
            }

        })
        binding.tvUpdateCurrentStatus.setOnClickListener {
            showDialogQualityInspectionFragment()
        }

    }


    private fun showDialogQualityInspectionFragment() {
        var dialogFragment = DialogManTrapRescueLog()
        var dataBundle = Bundle()
        //  dataBundle.putString("key", type)
        dataBundle.putString("id", id)

        // dataBundle.putString("ticketType", ticketdata!!.Type)
        dialogFragment.arguments = dataBundle
        dialogFragment.show(supportFragmentManager, "DialogManTrapRescueLog")
    }

    private fun subsribeToObserver() {
        viewModel.manTrapLog.observe(this, Event.EventObserver(
            onError = {
                binding.loader.isVisible = false
                Log.e(TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {
                binding.loader.isVisible = true
            }, {
                binding.loader.isVisible = false
                if (it.status == 200) {
                    manTrapLogAdapter.announcement = it.data
                } else {
                    Log.e(TAG, "responseError: ${it.message}")
                    Global.warningmessagetoast(this, it.message)
                }


            }
        ))

        viewModel.dropDownManRescue.observe(this, Event.EventObserver(
            onError = {
                binding.loader.isVisible = false
                Log.e(TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {
                binding.loader.isVisible = true
            }, {
                binding.loader.isVisible = false
                if (it.status == 200) {
                    Log.e(TAG, "subsribeToObserverDropDOWN=>: ${it.data}")
                    //  manTrapLogAdapter.announcement = it.data
                } else {
                    Log.e(TAG, "responseError: ${it.message}")
                    Global.warningmessagetoast(this, it.message)
                }


            }
        ))
    }

    companion object {
        private const val TAG = "ManTrapRescueLogActivit"
    }


}