package com.ahuja.sons.ahujaSonsClasses.activity


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.DependencyOrderAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.EarrandsOrderAdapter
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.ActivityCounterBinding


class CounterActivity : AppCompatActivity() {
    lateinit var binding: ActivityCounterBinding
    var dependencyOrderAdapter = DependencyOrderAdapter()
    var earrandsOrderAdapter = EarrandsOrderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCounterBinding.inflate(layoutInflater)
        setContentView(binding.root)


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

        binding.apply {
            tvCreateOrder.setOnClickListener {
                showPopupMenu(binding.tvCreateOrder)
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


    private fun showPopupMenu(view: View) {
        // Create a PopupMenu
        val popupMenu = PopupMenu(this, view)
        // Inflate the popup menu using the menu resource file
        popupMenu.getMenuInflater().inflate(R.menu.pop_menu_for_counter_role, popupMenu.getMenu())
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                return when (p0?.getItemId()) {
                    R.id.menuCreateDependency -> {
                        Intent(
                            this@CounterActivity,
                            SelectOrderForCreateDependencyActivity::class.java
                        ).also {
                            startActivity(it)
                        }
                        true
                    }
                    R.id.menuCreateErrand -> {
                        Intent(this@CounterActivity, AddErrandActivity::class.java).also {
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


    private fun setupDependencyRecyclerview() = binding.rvDependency.apply {
        adapter = dependencyOrderAdapter
        layoutManager = LinearLayoutManager(this@CounterActivity)
        dependencyOrderAdapter.notifyDataSetChanged()
    }


    private fun setupEarrandRecyclerview() = binding.rvEarrands.apply {
        adapter = earrandsOrderAdapter
        layoutManager = LinearLayoutManager(this@CounterActivity)
        earrandsOrderAdapter.notifyDataSetChanged()
    }
}