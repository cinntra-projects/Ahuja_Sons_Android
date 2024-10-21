package com.ahuja.sons.ahujaSonsClasses.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.fragments.route.OrderForDeliveryCoordinatorFragment
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.databinding.InspectionDeliveryIdLayoutBinding

class DeliveryCoordinatorIDsAdapter (private val items: ArrayList<AllWorkQueueResponseModel.InspectedDelivery>, var isMultiOrderCardSelectEnabled: Boolean) : RecyclerView.Adapter<DeliveryCoordinatorIDsAdapter.InnerViewHolder>() {

    interface ClickOnDeliveryID {
        fun onClickDeliveryID(mArrayUriList: ArrayList<Uri>)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val binding = InspectionDeliveryIdLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerViewHolder(binding)
    }

    fun isUpdated(isMultiOrder: Boolean) {
        this.isMultiOrderCardSelectEnabled = isMultiOrder
        notifyDataSetChanged()
    }

    // Method to return the list of items
    fun getItems(): MutableList<AllWorkQueueResponseModel.InspectedDelivery> {
        return items
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)


        if (isMultiOrderCardSelectEnabled) {
            holder.binding.checkBoxOrder.visibility = View.VISIBLE

        } else {
            holder.binding.checkBoxOrder.visibility = View.INVISIBLE

        }

        holder.binding.checkBoxOrder.isChecked = item.isSelected

//        holder.binding.checkBoxOrder.visibility = View.VISIBLE


        holder.binding.checkBoxOrder.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked

            if (isChecked) {

                if (GlobalClasses.deliveryIDsList.contains(item)){
                    Log.e("childItemCheck==>", "onBindViewHolder: Already exist" )
                }else{
                    GlobalClasses.deliveryIDsList.add(item)
                }


            } else {
                GlobalClasses.deliveryIDsList.remove(item)
            }

            Log.e("SELECTED ORDER>>>>>", "bindChild: ${GlobalClasses.deliveryIDsList.size}")

            for (item in GlobalClasses.deliveryIDsList) {
                Log.e("SELECTED ORDER11>>>>>", "bindChild: ${item.toString()}")
            }

        }



    }

    override fun getItemCount(): Int = items.size

    inner class InnerViewHolder(var binding: InspectionDeliveryIdLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AllWorkQueueResponseModel.InspectedDelivery) {
            binding.apply {
                innerTitle.text =  item.DocNum
                tvStatus.text = "Status : "+ item.DeliveryStatus
            }



        }
    }
}