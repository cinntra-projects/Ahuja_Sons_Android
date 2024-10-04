package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.databinding.PendingItemsListAdapterLayoutBinding

class PendingItemsListAdapter (val AllitemsList: ArrayList<AllItemsForOrderModel.PendingItem>): RecyclerView.Adapter<PendingItemsListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<AllItemsForOrderModel.PendingItem> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(PendingItemsListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val obj = AllitemsList[holder.adapterPosition]
        holder.binding.apply {
            tvItemDescription.setText(obj.ItemDescription)
            tvQTy.setText("Qty : "+obj.Quantity)
//            tvItemCode.setText("Item Code : "+obj.Pen)

        }

    }


    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: PendingItemsListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)


}