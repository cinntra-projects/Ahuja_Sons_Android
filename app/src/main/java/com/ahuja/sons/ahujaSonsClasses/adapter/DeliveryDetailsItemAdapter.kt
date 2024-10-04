package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.activity.UpdateErrandsActivity
import com.ahuja.sons.ahujaSonsClasses.model.AllErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.DeliveryItemListModel
import com.ahuja.sons.databinding.DeliveryDetailItemLayoutBinding
import com.ahuja.sons.databinding.DeliveryItemsListAdapterLayoutBinding
import com.ahuja.sons.databinding.ItemEarrandsBinding

class DeliveryDetailsItemAdapter: ListAdapter<DeliveryItemListModel.Data, DeliveryDetailsItemAdapter.OrderViewHolder>(OrderDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = DeliveryDetailItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class OrderViewHolder(var binding: DeliveryDetailItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: DeliveryItemListModel.Data) {

            binding.tvDeliveryNum.text = order.DocNum
            binding.status.text = order.DeliveryStatus

        }


    }

    class OrderDiffCallback : DiffUtil.ItemCallback<DeliveryItemListModel.Data>() {
        override fun areItemsTheSame(
            oldItem: DeliveryItemListModel.Data,
            newItem: DeliveryItemListModel.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DeliveryItemListModel.Data,
            newItem: DeliveryItemListModel.Data
        ): Boolean {
            return oldItem == newItem
        }
    }




}
