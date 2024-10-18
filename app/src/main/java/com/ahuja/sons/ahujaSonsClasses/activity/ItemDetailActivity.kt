package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.PendingItemsListAdapter
import com.ahuja.sons.ahujaSonsClasses.fragments.order.AllItemListFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.DeliveryItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.PendingItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.UploadProofImagesFragment
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityItemDetailBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityItemDetailBinding
    lateinit var viewModel: MainViewModel

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    var SapOrderId = ""
    var DeliveryID = 0
    var flag = ""
    var FlagFromWhere = ""

    lateinit var pagerAdapter : ViewPagerAdapter
    private val TAG = "ItemDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        SapOrderId = intent.getStringExtra("SapOrderId").toString()
        DeliveryID = intent.getIntExtra("deliveryID", 0)
        flag = intent.getStringExtra("flag").toString()
        FlagFromWhere = intent.getStringExtra("flagForItemViewList").toString()
        Log.e(TAG, "onCreate: $SapOrderId")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        // Enable the up button (back arrow) in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
            finish()
        }

//        callAllItemListApi()

        //todo inspection tabs
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)

        if (FlagFromWhere.equals("FromDeliveryIdSelect")){
            binding.heading.setText("Delivery Items")
            pagerAdapter.add(DeliveryItemsFragment(SapOrderId, DeliveryID, FlagFromWhere), "Delivery Items")
            binding.tabLayout.tabGravity = TabLayout.GRAVITY_START
            binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        }else{
            binding.heading.setText("Items List")
            pagerAdapter.add(AllItemListFragment(SapOrderId), "All Items")
            pagerAdapter.add(DeliveryItemsFragment(SapOrderId, DeliveryID, FlagFromWhere), "Delivery Items")
            pagerAdapter.add(PendingItemsFragment(SapOrderId), "Pending Items")
        }

        //  pagerAdapter.add(UploadProofImagesFragment(), "Proof")

        binding.viewpagerInspect.adapter = pagerAdapter

        binding.tabLayout.setupWithViewPager(binding.viewpagerInspect)


    }


}