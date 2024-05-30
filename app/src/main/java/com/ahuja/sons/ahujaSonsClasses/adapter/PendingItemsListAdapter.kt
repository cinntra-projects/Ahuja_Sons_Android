package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.databinding.PendingItemsListAdapterLayoutBinding

class PendingItemsListAdapter (val AllitemsList: ArrayList<String>): RecyclerView.Adapter<PendingItemsListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<String> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(PendingItemsListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val obj = AllitemsList[position]
        holder.binding.apply {

            holder.binding.name.text = obj

        }

        holder.itemView.setOnClickListener {

        }


    }



    override fun getItemCount(): Int {
        return 3
    }

    inner class Category_Holder(var binding: PendingItemsListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)



}