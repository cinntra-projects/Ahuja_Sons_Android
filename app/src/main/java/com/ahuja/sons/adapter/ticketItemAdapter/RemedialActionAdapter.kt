package com.ahuja.sons.adapter.ticketItemAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ahuja.sons.R
import com.ahuja.sons.model.ComplainDetailResponseModel

class RemedialActionAdapter (context: Context?, dataList: MutableList<ComplainDetailResponseModel.DataX>) : ArrayAdapter<String?>(context!!, R.layout.drop_down_item_textview) {

    private val dataList: MutableList<ComplainDetailResponseModel.DataX>
    private val inflater: LayoutInflater

    init {
        this.dataList = dataList
        inflater = LayoutInflater.from(context)
        for (data in dataList) {
            add(data.Name) // Replace with the actual method to get the first key
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Customize the appearance of the selected item in the dropdown (optional)
        return super.getView(position, convertView, parent)
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v = convertView
        if (v == null) {
            v = inflater.inflate(R.layout.drop_down_item_textview, null)
        }
        val title = v!!.findViewById<TextView>(R.id.title)
        //        if (!stagesList.get(position).getRole().equals("admin"))
        title.setText(dataList[position].Name)
        return v
    } // ... Other methods remain the same
}