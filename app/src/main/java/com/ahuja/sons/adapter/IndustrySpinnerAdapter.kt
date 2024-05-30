package com.ahuja.sons.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ahuja.sons.R
import com.ahuja.sons.model.BPLID

class IndustrySpinnerAdapter() :
    BaseAdapter() {

    lateinit var getDepartMent: List<BPLID>
    lateinit var context: Context
    lateinit var inflter: LayoutInflater
     constructor(context: Context, getDepartMent: List<BPLID>) : this() {
        this.context = context
        this.getDepartMent = getDepartMent
        inflter = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return getDepartMent.size
    }

    override fun getItem(position: Int): Any {
        return getDepartMent!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = inflter.inflate(R.layout.stages_spinner_item,p2,false)

        val title = view.findViewById<TextView>(R.id.title)
        title.text = getDepartMent[p0].getName()
        return view
    }
}

