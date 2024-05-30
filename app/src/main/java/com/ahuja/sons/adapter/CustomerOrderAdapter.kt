package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.newapimodel.DataParticularCustomerOrder


class CustomerOrderAdapter(val AllitemsList: ArrayList<DataParticularCustomerOrder>) :
    RecyclerView.Adapter<CustomerOrderAdapter.Category_Holder>() {

    private lateinit var context: Context


    private var onItemClickListener: ((DataParticularCustomerOrder) -> Unit)? = null
    fun setOnItemClickListener(listener: (DataParticularCustomerOrder) -> Unit) {
        onItemClickListener = listener
    }


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
        var current = AllitemsList[position]

        holder.sr_no.text = "Order No. #" + AllitemsList[position].id
        holder.name.text = AllitemsList[position].CardName
        holder.message.visibility = View.GONE


//            holder.itemView.setOnClickListener {
//
//            }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(current)
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val message = itemView.findViewById<TextView>(R.id.message)
        val name = itemView.findViewById<TextView>(R.id.tvOrderName)


    }


}
