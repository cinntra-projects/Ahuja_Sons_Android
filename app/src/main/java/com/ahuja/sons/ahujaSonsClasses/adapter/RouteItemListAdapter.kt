package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.ItemRouteDeliveryCoordinatorBinding
import com.ahuja.sons.databinding.ItemWorkQueueBinding
import com.ahuja.sons.newapimodel.IssueListResponseModel
import java.util.ArrayList


class RouteItemListAdapter :
    ListAdapter<LocalRouteData, RouteItemListAdapter.OrderViewHolder>(OrderDiffCallback()) {
    lateinit var orderAdapter: OrderListAdapter



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemRouteDeliveryCoordinatorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, DeliveryCoordinatorActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class OrderViewHolder(private val binding: ItemRouteDeliveryCoordinatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: LocalRouteData) {
            binding.tvDeliveryPerson.text = order.orderName
            orderAdapter =
                OrderListAdapter(order.orderList as ArrayList<AllOrderListResponseModel.Data>,RoleClass.deliveryPerson)
            binding.rvOrderInRoute.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = orderAdapter
            }

            binding.ivArrow.setOnClickListener {
                if (binding.rvOrderInRoute.visibility == View.VISIBLE) {
                    binding.rvOrderInRoute.visibility = View.GONE
                    binding.ivArrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                } else {
                    binding.rvOrderInRoute.visibility = View.VISIBLE
                    binding.ivArrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                }
            }


        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<LocalRouteData>() {
        override fun areItemsTheSame(
            oldItem: LocalRouteData,
            newItem: LocalRouteData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LocalRouteData,
            newItem: LocalRouteData
        ): Boolean {
            return oldItem == newItem
        }
    }
}
