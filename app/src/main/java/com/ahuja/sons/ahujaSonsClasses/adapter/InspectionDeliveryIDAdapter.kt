package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.databinding.InspectionDeliveryIdLayoutBinding
import com.ahuja.sons.globals.Global
import com.pixplicity.easyprefs.library.Prefs

class InspectionDeliveryIDAdapter (private val itemsList: ArrayList<AllWorkQueueResponseModel.DeliveryNote>) : RecyclerView.Adapter<InspectionDeliveryIDAdapter.InnerViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val binding = InspectionDeliveryIdLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return InnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val item = itemsList.get(position)
        holder.bind(item)

        Log.d("ChildAdapter", "Binding item at position $position: ${item.DocNum}")


        holder.itemView.setOnClickListener {

            if (Prefs.getString(Global.Employee_role, "").equals("Inspection")){
                if (itemsList[position].DeliveryStatus == "Prepared"){
                    val intent = Intent(holder.itemView.context, InspectDeliveryOrderDetailActivity::class.java)
                    intent.putExtra("deliveryID", itemsList[position].id)
                    intent.putExtra("DeliveryStatus", itemsList[position].DeliveryStatus)
                    intent.putExtra("inspectionDeliveryPos", position)
                    context.startActivity(intent)
                }

            }


        }


    }


    override fun getItemCount(): Int {
        return itemsList.size
    }

    inner class InnerViewHolder(var binding: InspectionDeliveryIdLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AllWorkQueueResponseModel.DeliveryNote) {
            binding.apply {
                innerTitle.text =  item.DocNum.toString()
                tvStatus.text = "Status : "+ item.DeliveryStatus
            }
        }


    }
}