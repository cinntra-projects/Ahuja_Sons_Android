package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.fragments.order.DeliveryItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.PendingItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.UploadProofImagesFragment
import com.ahuja.sons.databinding.ActivityInspectDeliveryOrderDetailBinding

class InspectDeliveryOrderDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityInspectDeliveryOrderDetailBinding
    lateinit var pagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspectDeliveryOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.add(DeliveryItemsFragment(), "Delivery Items")
        pagerAdapter.add(PendingItemsFragment(), "Pending Items")
        pagerAdapter.add(UploadProofImagesFragment(), "Proof")
        binding.viewpagerInspect.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewpagerInspect)


    }
}