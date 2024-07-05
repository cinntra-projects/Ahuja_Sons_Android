package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.ItemDependecyOrderBinding
import com.ahuja.sons.databinding.ItemEarrandsBinding
import com.ahuja.sons.databinding.ItemOfDeliveryCoordinatorOrderItemBinding
import com.ahuja.sons.databinding.ItemWorkQueueBinding


class EarrandsOrderAdapter :
    ListAdapter<LocalWorkQueueData, EarrandsOrderAdapter.OrderViewHolder>(OrderDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            ItemEarrandsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, SurgeryCoordinatorActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    class OrderViewHolder(private val binding: ItemEarrandsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: LocalWorkQueueData) {
       /*     binding..text = order.orderName

            binding.tvSaleOrderId.text = "12,11,13"
            binding.tvOrderInfo.text = "${order.omsID}"
            binding.tvSurgeryName.text = "${order.status}"*/
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
