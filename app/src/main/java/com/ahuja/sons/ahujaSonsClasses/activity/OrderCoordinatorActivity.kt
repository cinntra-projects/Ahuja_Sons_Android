package com.ahuja.sons.ahujaSonsClasses.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.DependencyOrderAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.EarrandsOrderAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.ItemInOrderForDeliveryCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.ActivityOrderCoordinatorBinding
import com.ahuja.sons.databinding.BottomSheetItemListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class OrderCoordinatorActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderCoordinatorBinding
    var dependencyOrderAdapter = DependencyOrderAdapter()
    var earrandsOrderAdapter = EarrandsOrderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.linearSelectOrder.visibility = View.VISIBLE

        val orders = List(5) { index ->
            LocalWorkQueueData(
                id = index.toString(),
                date = "2024-05-30",
                time = "12:00 PM",
                orderName = "Order #$index",
                doctor = "Doctor #$index",
                status = "Pending",
                omsID = "OMSID#$index"
            )
        }

        dependencyOrderAdapter.submitList(orders)
        earrandsOrderAdapter.submitList(orders)
        setupDependencyRecyclerview()
        setupEarrandRecyclerview()
        hideAndShowViews()
        setupFlow()

        binding.apply {
            chipCreateearner.setOnClickListener {
                Intent(this@OrderCoordinatorActivity, AddErrandActivity::class.java).also {
                    startActivity(it)
                }
            }

            chipCreateDependency.setOnClickListener {
                Intent(
                    this@OrderCoordinatorActivity,
                    SelectOrderForCreateDependencyActivity::class.java
                ).also {
                    startActivity(it)
                }
            }
        }


    }

    private fun hideAndShowViews() {
        binding.apply {
            errandsUpArrow.setOnClickListener {
                if (rvEarrands.visibility == View.VISIBLE) {
                    rvEarrands.visibility = View.GONE
                    errandsUpArrow.setImageResource(R.drawable.arrow_up_icon)

                } else {
                    rvEarrands.visibility = View.VISIBLE
                    errandsUpArrow.setImageResource(R.drawable.down_arrow_icon)
                }
            }


            dependencyUpArrow.setOnClickListener {
                if (rvDependency.visibility == View.VISIBLE) {
                    rvDependency.visibility = View.GONE
                    dependencyUpArrow.setImageResource(R.drawable.arrow_up_icon)

                } else {
                    rvDependency.visibility = View.VISIBLE
                    dependencyUpArrow.setImageResource(R.drawable.down_arrow_icon)
                }
            }

        }
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


    private fun setupFlow() {
        binding.apply {
            btnOk.setOnClickListener {
                showItemListDialogBottomSheetDialog()

            }
        }
    }

    private fun showItemListDialogBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bindingBottomSheet: BottomSheetItemListBinding =
            BottomSheetItemListBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingBottomSheet.getRoot())

        bottomSheetDialog.show()

        bindingBottomSheet.headingMore.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        var itemInOrderForDeliveryCoordinatorAdapter = ItemInOrderForDeliveryCoordinatorAdapter()

        // Sample data
        val orders = List(5) { index ->
            LocalWorkQueueData(
                id = index.toString(),
                date = "2024-05-30",
                time = "12:00 PM",
                orderName = "Order #$index",
                doctor = "Doctor #$index",
                status = "Pending",
                omsID = "OMSID#$index"
            )
        }

        itemInOrderForDeliveryCoordinatorAdapter.submitList(orders)

        bindingBottomSheet.btnConfirm.setOnClickListener {
            binding.apply {
                linearOkCancelButton.visibility = View.GONE
                linearCreateDependencyEarrands.visibility = View.VISIBLE
            }
            bottomSheetDialog.dismiss()
        }
        bindingBottomSheet.rvItemList.apply {
            adapter = itemInOrderForDeliveryCoordinatorAdapter
            layoutManager = LinearLayoutManager(this@OrderCoordinatorActivity)
            itemInOrderForDeliveryCoordinatorAdapter.notifyDataSetChanged()
        }


    }
}