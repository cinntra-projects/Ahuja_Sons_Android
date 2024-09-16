package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.model.AllDependencyAndErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.AllErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.ItemDependecyOrderBinding
import com.ahuja.sons.databinding.ItemEarrandsBinding
import com.ahuja.sons.databinding.ItemOfDeliveryCoordinatorOrderItemBinding
import com.ahuja.sons.databinding.ItemWorkQueueBinding


class EarrandsOrderAdapter : ListAdapter<AllErrandsListModel.Data, EarrandsOrderAdapter.OrderViewHolder>(OrderDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemEarrandsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.binding.edit.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateErrandsActivity::class.java)
            intent.putExtra("OrderID", getItem(position).OrderRequestID.id)
            intent.putExtra("pos", holder.absoluteAdapterPosition)
            holder.itemView.context.startActivity(intent)
        }

    }

    inner class OrderViewHolder(var binding: ItemEarrandsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: AllErrandsListModel.Data) {
            binding.tvPickUpLocation.text = order.PickupLocation

            binding.tvDropLocation.text = order.DropLocation
            binding.tvNatureEarrand.text = "${order.NatureOfErrands.Name}"
            binding.tvContactPerson.text = "${order.ContactPerson}"
            binding.status.text = "Status : ${order.OrderRequestID.Status}"

            if(order.Remark.isNotEmpty())
                binding.tvRemark.text ="${order.Remark}"
            else
                binding.tvRemark.text = "NA"



        }


    }

    class OrderDiffCallback : DiffUtil.ItemCallback<AllErrandsListModel.Data>() {
        override fun areItemsTheSame(
            oldItem: AllErrandsListModel.Data,
            newItem: AllErrandsListModel.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AllErrandsListModel.Data,
            newItem: AllErrandsListModel.Data
        ): Boolean {
            return oldItem == newItem
        }
    }




}
