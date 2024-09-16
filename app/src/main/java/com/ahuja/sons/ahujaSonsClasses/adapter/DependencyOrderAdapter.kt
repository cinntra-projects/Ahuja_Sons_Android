package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.model.AllDependencyAndErrandsListModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.databinding.ItemDependecyOrderBinding
import com.ahuja.sons.databinding.ItemOfDeliveryCoordinatorOrderItemBinding
import com.ahuja.sons.databinding.ItemWorkQueueBinding


class DependencyOrderAdapter : ListAdapter<AllDependencyAndErrandsListModel.Data, DependencyOrderAdapter.OrderViewHolder>(OrderDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemDependecyOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SurgeryCoordinatorActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    class OrderViewHolder(private val binding: ItemDependecyOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: AllDependencyAndErrandsListModel.Data) {
            binding.tvHospitalName.text = order.OrderDependency[0].CardName

            binding.tvSaleOrderId.text = order.OrderDependency[0].id.toString()

            binding.tvOrderInfo.text = "${order.OrderDependency[0].OrderInformation}"
            binding.tvSurgeryName.text = "${order.OrderDependency[0].SurgeryName}"
            binding.tvDoctorName.text = "${order.OrderDependency[0].Doctor[0].DoctorFirstName} ${ order.OrderDependency[0].Doctor[0].DoctorLastName}"
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<AllDependencyAndErrandsListModel.Data>() {
        override fun areItemsTheSame(
            oldItem: AllDependencyAndErrandsListModel.Data,
            newItem: AllDependencyAndErrandsListModel.Data
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AllDependencyAndErrandsListModel.Data,
            newItem: AllDependencyAndErrandsListModel.Data
        ): Boolean {
            return oldItem == newItem
        }
    }


}
