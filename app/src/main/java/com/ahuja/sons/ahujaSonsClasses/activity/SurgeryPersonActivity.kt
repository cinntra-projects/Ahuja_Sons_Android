package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.fragments.order.DeliveryItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.PendingItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.surgeryperson.DetailSurgeryPersonFragment
import com.ahuja.sons.databinding.ActivitySurgeryPersonBinding
import com.ahuja.sons.databinding.BottomSheetSelectDateTimeBinding
import com.ahuja.sons.globals.Global
import com.google.android.material.bottomsheet.BottomSheetDialog

class SurgeryPersonActivity : AppCompatActivity() {
    lateinit var binding: ActivitySurgeryPersonBinding
    lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurgeryPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)




        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.add(DetailSurgeryPersonFragment(), "Details")
        pagerAdapter.add(DeliveryItemsFragment(), "Delivery")
        pagerAdapter.add(PendingItemsFragment(), "Pending")
        binding.viewpagerInspect.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewpagerInspect)

        //todo header arrow-
        binding.headerUpArrow.setOnClickListener {
            binding.tvOrderID.visibility = View.GONE
            binding.tvExpectedData.visibility = View.GONE
            binding.headerUpArrow.visibility = View.GONE
            binding.headerDownArrow.visibility = View.VISIBLE
        }

        binding.headerDownArrow.setOnClickListener {
            binding.tvOrderID.visibility = View.VISIBLE
            binding.tvExpectedData.visibility = View.VISIBLE
            binding.headerDownArrow.visibility = View.GONE
            binding.headerUpArrow.visibility = View.VISIBLE
        }

        binding.chipReschedule.setOnClickListener {
            showRescheduleDialogBottomSheetDialog()

        }


    }


    private fun showRescheduleDialogBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetSelectDateTimeBinding =
            BottomSheetSelectDateTimeBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())

        bottomSheetDialog.show()

        bindingBottomSheet.ivCross.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        bindingBottomSheet.edtDate.setOnClickListener {
            Global.selectDate(this, bindingBottomSheet.edtDate)
        }


        bindingBottomSheet.edtTime.setOnClickListener {

            Global.selectTime(this, bindingBottomSheet.edtTime)
        }

        bindingBottomSheet.btnConfirm.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


    }
}