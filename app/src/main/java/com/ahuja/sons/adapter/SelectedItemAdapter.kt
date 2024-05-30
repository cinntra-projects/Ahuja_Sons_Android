package com.ahuja.sons.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.newapimodel.ItemAllListResponseModel

class SelectedItemAdapter(private val context: Context, private val itemName : ArrayList<ItemAllListResponseModel.DataXXX>,   private val flag: String) : RecyclerView.Adapter<SelectedItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(context).inflate(R.layout.item_participant_chip, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val nameParts: Array<String> = itemName.get(position).split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

//        val nameParts1: String = itemName[position].ItemName
//        val firstName = nameParts1

        holder.tvNameOfEmployee.setText("Item - " + itemName[position].ItemCode + " ( " +itemName[position].ItemName + " )")


        holder.ivCrossIcon.setOnClickListener(View.OnClickListener { view: View? ->
            if (flag == "DefaultSet"){

            }else{
                Log.e("ivcross", "onBindViewHolder: $position")
                itemName.removeAt(holder.adapterPosition)
                notifyDataSetChanged()
            }
        })
        /*holder.ivCrossIcon.setOnClickListener(View.OnClickListener { view: View? ->
            Log.e("ivcross", "onBindViewHolder: $position")
            itemName.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        })*/
    }

    override fun getItemCount(): Int {
        return itemName.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameOfEmployee: TextView = itemView.findViewById(R.id.tvNameEmployee)
        val ivCrossIcon: ImageView = itemView.findViewById(R.id.ivCrossIcon)

    }
}