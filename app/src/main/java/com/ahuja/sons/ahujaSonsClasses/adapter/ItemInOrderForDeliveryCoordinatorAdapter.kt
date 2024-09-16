package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllItemListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.databinding.ItemOfDeliveryCoordinatorOrderItemBinding
import com.ahuja.sons.databinding.ItemWorkQueueBinding


class ItemInOrderForDeliveryCoordinatorAdapter : ListAdapter<AllItemListResponseModel.Data, ItemInOrderForDeliveryCoordinatorAdapter.OrderViewHolder>(OrderDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOfDeliveryCoordinatorOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SurgeryCoordinatorActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    class OrderViewHolder(private val binding: ItemOfDeliveryCoordinatorOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: AllItemListResponseModel.Data) {
            binding.ItemDescription.text = order.ItemDescription

            binding.tvItemQuantity.text = "12,11,13"
            binding.tvUOM.text = "${order.MeasureUnit}"
            binding.tvQty.text = "${order.Quantity}"
        }


    }

    class OrderDiffCallback : DiffUtil.ItemCallback<AllItemListResponseModel.Data>() {
        override fun areItemsTheSame(
            oldItem: AllItemListResponseModel.Data,
            newItem: AllItemListResponseModel.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AllItemListResponseModel.Data,
            newItem: AllItemListResponseModel.Data
        ): Boolean {
            return oldItem == newItem
        }
    }
}
