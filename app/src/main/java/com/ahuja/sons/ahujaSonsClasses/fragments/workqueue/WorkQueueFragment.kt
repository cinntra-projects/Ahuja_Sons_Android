package com.ahuja.sons.ahujaSonsClasses.fragments.workqueue

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.ahujaSonsClasses.activity.AddSalesOrderActivity
import com.ahuja.sons.ahujaSonsClasses.adapter.ItemInOrderForDeliveryCoordinatorAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.WorkQueueAdapter
import com.ahuja.sons.ahujaSonsClasses.fragments.order.DeliveryItemsFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.order.OrderFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.route.OrderForDeliveryCoordinatorFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.route.RouteFragment
import com.ahuja.sons.ahujaSonsClasses.fragments.surgeryperson.DetailSurgeryPersonFragment
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.BottomSheetItemListBinding
import com.ahuja.sons.databinding.BottomSheetSelectDateTimeBinding
import com.ahuja.sons.databinding.FragmentWorkQueueBinding
import com.ahuja.sons.globals.Global
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WorkQueueFragment : Fragment() {
    lateinit var binding: FragmentWorkQueueBinding
    val orderAdapter = WorkQueueAdapter()


    lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkQueueBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private const val TAG = "WorkQueueFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //todo hide and show view
       showViewForOtherRole()
       // showViewForDeliveryCoordinatorRole()

        pagerAdapter = ViewPagerAdapter(childFragmentManager)
        pagerAdapter.add(OrderForDeliveryCoordinatorFragment(binding.tvCreateRoute), "Order")
        pagerAdapter.add(RouteFragment(), "Route")
        binding.viewpager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)





        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            binding.apply {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.INVISIBLE
                rvWorkQue.visibility = View.VISIBLE
            }

            binding.rvWorkQue.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = orderAdapter
            }

            // Sample data
            val orders = List(10) { index ->
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

            orderAdapter.submitList(orders)
        }

        binding.apply {
         /*   ivCollapseCart.setOnClickListener {
                showItemListDialogBottomSheetDialog()
            }*/


            binding.fabWorkQueue!!.setOnClickListener {
                Intent(requireActivity(), AddSalesOrderActivity::class.java).also {
                    startActivity(it)
                }
            }

            rvWorkQue.setOnScrollChangeListener { view, i, i2, i3, i4 ->
                if (view.scrollY > 0) {
                    binding.fabWorkQueue!!.hide()
                } else {
                    binding.fabWorkQueue!!.show()
                }


            }

            // Add scroll listener to hide/show FAB
            rvWorkQue.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        // Scroll down
                        binding.fabWorkQueue!!.hide()
                    } else if (dy < 0) {
                        // Scroll up
                        binding.fabWorkQueue!!.show()
                    }
                }
            })
        }


    }


    private fun showViewForOtherRole() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            rvWorkQue.visibility = View.VISIBLE
            tvCreateRoute.visibility = View.GONE
            tabLayout.visibility = View.GONE
            viewpager.visibility = View.GONE
        }
    }

    private fun showViewForDeliveryCoordinatorRole() {
        binding.apply {
            shimmerLayout.visibility = View.GONE
            rvWorkQue.visibility = View.GONE
            tvCreateRoute.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
            viewpager.visibility = View.VISIBLE
        }
    }




}