package com.ahuja.sons.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ahuja.sons.R
import com.ahuja.sons.model.EmployeeData

class ServiceManagerAdapter  (private val context: Context, private val resourceId: Int, private val data: ArrayList<EmployeeData>) : ArrayAdapter<EmployeeData>(context, resourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            view = inflater.inflate(resourceId, parent, false)
        }
        val model: EmployeeData = getItem(position)!!
        val name = view!!.findViewById<TextView>(R.id.text_view_item)

        name.setText( model.getFirstName() + " ( " +model.getRole() + " )")

        return view
    }

    override fun getItem(position: Int): EmployeeData? {
        return data[position]
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}