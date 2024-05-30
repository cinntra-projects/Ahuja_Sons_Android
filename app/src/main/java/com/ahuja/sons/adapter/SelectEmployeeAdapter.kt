package com.ahuja.sons.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global
import com.ahuja.sons.receiver.DataEmployeeAllData
import java.util.*


class SelectEmployeeAdapter() : RecyclerView.Adapter<SelectEmployeeAdapter.Category_Holder>() {

    lateinit var AllitemsList: ArrayList<DataEmployeeAllData>
    private lateinit var oncontext: Activity
    lateinit var contactval: EditText

    constructor(requireContext: Context , contacnameValue: EditText, contactlist: ArrayList<DataEmployeeAllData>) : this() {
        this.oncontext = requireContext as Activity
        this.AllitemsList = contactlist
        this.contactval = contacnameValue
    }


    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_view, parent, false)

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        holder.name.text = AllitemsList[position].SalesEmployeeName + " ( " + AllitemsList[position].role + " ) "

        holder.itemView.setOnClickListener {
            Global.TicketAssigntoID = AllitemsList[position].SalesEmployeeCode
            contactval.setText(AllitemsList[position].SalesEmployeeName)
            oncontext.onBackPressed()
        }
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profile_pic: ImageView = itemView.findViewById(R.id.profile_pic)
        val name: TextView = itemView.findViewById(R.id.name)
        val email: TextView = itemView.findViewById(R.id.email)


    }


}
