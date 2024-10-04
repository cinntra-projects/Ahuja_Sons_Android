package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.model.RouteListModel
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonNameListModel
import com.ahuja.sons.databinding.InspectionDeliveryIdLayoutBinding
import com.ahuja.sons.databinding.SurgeryPersonNameLayoutBinding

class SurgeryNameListAdapter (private val itemsList: ArrayList<SurgeryPersonNameListModel.Data>) : RecyclerView.Adapter<SurgeryNameListAdapter.InnerViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val binding = SurgeryPersonNameLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return InnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val item = itemsList.get(position)
        holder.bind(item)


    }


    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class InnerViewHolder(var binding: SurgeryPersonNameLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SurgeryPersonNameListModel.Data) {
            binding.apply {
//                tvSurgeryName.text = "Surgery Assistant : "+ itemsList.size+1
                tvSureryAssistantThree.text =  item.SurgeryPersonsName.toString()
                tvSurgeryName.text = "Surgery Assistant ${position + 1}"

            }
        }


    }
}