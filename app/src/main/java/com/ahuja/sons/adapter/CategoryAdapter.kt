package com.ahuja.sons.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.`interface`.DataBaseClick
import com.ahuja.sons.activity.CreatePartRequest
import com.ahuja.sons.fragment.ServiceTicketFragment
import com.ahuja.sons.model.ItemCategoryData

class CategoryAdapter() : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    lateinit var  context: Context
    lateinit var taxList: List<ItemCategoryData>
    lateinit var databaseClick: DataBaseClick
    lateinit var TaxListdialog: Dialog




    constructor(context: ServiceTicketFragment,
                data: List<ItemCategoryData?>,
                taxListdialog: Dialog) : this()  {
        this.taxList = data as List<ItemCategoryData>
        this.databaseClick = context as DataBaseClick
        this.TaxListdialog = taxListdialog
    }

    constructor(context: CreatePartRequest, data: List<ItemCategoryData>, taxListdialog: Dialog) : this(){
        this.taxList = data as List<ItemCategoryData>
        this.databaseClick = context as DataBaseClick
        this.TaxListdialog = taxListdialog
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.tax_adapter_item, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tax.text = taxList[position].GroupName
    }

    override fun getItemCount(): Int {
        return taxList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tax: TextView

        init {
            tax = itemView.findViewById(R.id.tax)
            itemView.setOnClickListener {
                databaseClick.onClick(taxList[adapterPosition].Number)
                TaxListdialog.dismiss()
            }
        }
    }


}
