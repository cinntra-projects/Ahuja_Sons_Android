package com.ahuja.sons.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.ParticularItemDetailsActivity


class CustomerEquipmentMVVMAdapter(val AllitemsList: ArrayList<com.ahuja.sons.newapimodel.DocumentLine>) : RecyclerView.Adapter<CustomerEquipmentMVVMAdapter.Category_Holder>()   {

    private  lateinit var context: Context






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.customer_order_view,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
    holder.sr_no.text = AllitemsList[position].ItemSerialNo
    holder.message.text = AllitemsList[position].ItemDescription

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ParticularItemDetailsActivity::class.java)
            intent.putExtra("ProductSerialNo",AllitemsList[position].ItemSerialNo)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val message = itemView.findViewById<TextView>(R.id.message)



    }





}
