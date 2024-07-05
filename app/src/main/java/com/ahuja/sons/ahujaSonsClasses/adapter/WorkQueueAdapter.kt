package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.ItemWorkQueueBinding


class WorkQueueAdapter :
    ListAdapter<LocalWorkQueueData, WorkQueueAdapter.OrderViewHolder>(OrderDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemWorkQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, InspectDeliveryOrderDetailActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }


    }

    class OrderViewHolder(private val binding: ItemWorkQueueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: LocalWorkQueueData) {
            binding.tvOrderName.text = order.orderName
            binding.tvOrderDoctorName.text = order.doctor
            binding.tvSurgeryDateTime.text = "Date:${order.date}\n Time: ${order.time}"
            binding.tvOmsIdOrder.text = "OMS ID: ${order.omsID}"
            binding.tvStatusOrder.text = "Status: ${order.status}"
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<LocalWorkQueueData>() {
        override fun areItemsTheSame(
            oldItem: LocalWorkQueueData,
            newItem: LocalWorkQueueData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LocalWorkQueueData,
            newItem: LocalWorkQueueData
        ): Boolean {
            return oldItem == newItem
        }
    }
}
