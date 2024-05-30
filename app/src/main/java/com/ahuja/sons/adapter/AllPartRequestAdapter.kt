package com.ahuja.sons.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.AllPartRequestItemList
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.DataAllPartRequest


class AllPartRequestAdapter(val data: ArrayList<DataAllPartRequest>) :
    RecyclerView.Adapter<AllPartRequestAdapter.Category_Holder>() {

    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.part_request_view,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        holder.sr_no.text = "Part Request Id : PR-" + data[position].id.toString()
        holder.code.text = "BP Name : " + data[position].BusinessPartnerDetails.CardName
        holder.message.text = "RequestedDate : " + Global.formatDateFromDateString(data[position].RequestedDate)
        holder.name.text = "Status : " + data[position].Status
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val message = itemView.findViewById<TextView>(R.id.message)
        val code = itemView.findViewById<TextView>(R.id.code)
        val name = itemView.findViewById<TextView>(R.id.name)

        init {
            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(Global.PartRequestData, data[position].id)
                val intent = Intent(context, AllPartRequestItemList::class.java)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        }
    }


}
