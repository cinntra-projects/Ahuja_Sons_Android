package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R

class StringSolutionAdapter  (private val context: Context, private val itemName : ArrayList<String>) : RecyclerView.Adapter<StringSolutionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(context).inflate(R.layout.item_participant_chip, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNameOfEmployee.setText("Item - " + itemName)

        holder.ivCrossIcon.setOnClickListener(View.OnClickListener { view: View? ->
            itemName.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        })
    }

    override fun getItemCount(): Int {
        return itemName.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameOfEmployee: TextView = itemView.findViewById(R.id.tvNameEmployee)
        val ivCrossIcon: ImageView = itemView.findViewById(R.id.ivCrossIcon)

    }
}