package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.DeliveryDetailItemListModel
import com.ahuja.sons.databinding.DeliveryItemsListAdapterLayoutBinding

class DeliveryDetailItemAdapter(val AllitemsList: ArrayList<DeliveryDetailItemListModel.Data>): RecyclerView.Adapter<DeliveryDetailItemAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<DeliveryDetailItemListModel.Data> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(DeliveryItemsListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val obj = AllitemsList[position]
        holder.binding.apply {
            tvItemDescription.setText(obj.ItemDescription)
            tvQTy.setText("Qty : "+obj.Quantity)
            tvUOM.setText("UOM : "+obj.UomNo)
            tvItemCode.setText("Item Code : "+obj.ItemCode)

        }

        holder.itemView.setOnClickListener {

        }

    }



    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: DeliveryItemsListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)



}