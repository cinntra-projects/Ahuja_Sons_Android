package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global


class PartRequestSubmitAdapter(): RecyclerView.Adapter<PartRequestSubmitAdapter.Category_Holder>()   {

    private  lateinit var context: Context





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.request_submit,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        holder.parts.text = Global.cartList[position]!!.ItemName
        holder.qty.text = Global.cartList[position]!!.Quantity.toString()
        holder.amnt.text = (Global.cartList[position]!!.UnitPrice * Global.cartList[position]!!.Quantity).toString()




    }

    override fun getItemCount(): Int {
        return Global.cartList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val parts : TextView = itemView.findViewById(R.id.parts)
        val amnt : TextView = itemView.findViewById(R.id.amnt)
        val qty : TextView = itemView.findViewById(R.id.qty)






    }





}
