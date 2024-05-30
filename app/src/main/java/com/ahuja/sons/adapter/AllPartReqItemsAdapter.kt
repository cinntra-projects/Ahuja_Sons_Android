package com.ahuja.sons.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import java.util.*

class AllPartReqItemsAdapter(val AllitemsList: ArrayList<com.ahuja.sons.newapimodel.Item>) :
    RecyclerView.Adapter<AllPartReqItemsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.part_request_itemview, parent, false)

        return ViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj: com.ahuja.sons.newapimodel.Item = AllitemsList[position]
        holder.sr_no.text = obj.ItemName
        holder.code.text = "ItemCode :" + obj.ItemCode
        holder.message.text = "Quantity : " + obj.ItemQty
        holder.name.text = "Unit Price : " + obj.UnitPrice
        holder.comment.text = "Remarks : " + obj.Comments
        holder.tvPartType.text = "Part Type : " + obj.PartRequestType
        holder.tvSerialNo.text = "Ref. Item Serial No : " + obj.ItemSrialNo
//        holder.stock.text = context!!.getString(R.string.instock) + " : " + obj.getInStock()
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sr_no: TextView
        var message: TextView
        var name: TextView
        var code: TextView
        var comment: TextView
        var tvPartType: TextView
        var tvSerialNo: TextView

        init {
            sr_no = itemView.findViewById(R.id.sr_no)
            message = itemView.findViewById(R.id.message)
            name = itemView.findViewById(R.id.name)
            code = itemView.findViewById(R.id.code)
            comment = itemView.findViewById(R.id.comment)
            tvSerialNo = itemView.findViewById(R.id.tvSerialNo)
            tvPartType = itemView.findViewById(R.id.tvPartType)
        }


    }

}