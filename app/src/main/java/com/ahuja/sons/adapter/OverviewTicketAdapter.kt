package com.ahuja.sons.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R


class OverviewTicketAdapter(): RecyclerView.Adapter<OverviewTicketAdapter.Category_Holder>()   {

    private  lateinit var context: Context





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.overview_ticket_detail,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        if(position>1){
            holder.imageman.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.grey))
            holder.divider.setBackgroundColor(R.color.grey)
            holder.message.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageman = itemView.findViewById<LinearLayout>(R.id.roundview)
        val divider = itemView.findViewById<View>(R.id.divider)
        val message = itemView.findViewById<TextView>(R.id.message)




    }





}
