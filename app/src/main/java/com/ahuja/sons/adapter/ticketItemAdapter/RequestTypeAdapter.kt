package com.ahuja.sons.adapter.ticketItemAdapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ahuja.sons.R
import com.ahuja.sons.model.ComplainDetailResponseModel

class RequestTypeAdapter(private val context: Context, private val resourceId: Int,var dataList: MutableList<ComplainDetailResponseModel.DataX>) : ArrayAdapter<ComplainDetailResponseModel.DataX>(context!!,resourceId, dataList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            view = inflater.inflate(resourceId, parent, false)
        }
        val model: ComplainDetailResponseModel.DataX? = getItem(position)
        val name = view!!.findViewById<TextView>(R.id.text_view_item)
        name.text = model?.Name
        return view
    }

    override fun getItem(position: Int): ComplainDetailResponseModel.DataX? {
        return dataList[position]
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


}