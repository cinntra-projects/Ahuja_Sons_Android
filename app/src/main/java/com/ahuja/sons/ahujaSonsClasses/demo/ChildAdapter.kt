package com.ahuja.sons.ahujaSonsClasses.demo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.globals.Global

class ChildAdapter(private val childItemList: List<ParentItemModel.ChildItem>) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val childItem = childItemList[position]
        holder.checkBoxChild.text = childItem.name
        holder.checkBoxChild.isChecked = childItem.isSelected

      /*  holder.checkBoxChild.setOnCheckedChangeListener { _, isChecked ->
            childItem.isSelected = isChecked
        }*/

        holder.checkBoxChild.setOnCheckedChangeListener { _, isChecked ->
            childItem.isSelected = isChecked

            if (isChecked) {

                if (GlobalClasses.demoForOrderRequest.contains(childItem)){
                    Log.e("childItemCheck==>", "onBindViewHolder: Already exist" )
                }else{
                    GlobalClasses.demoForOrderRequest.add(childItem)
                }

            } else {
                GlobalClasses.demoForOrderRequest.remove(childItem)
            }

            Log.e("SELECTED ORDER>>>>>", "bindChild: ${GlobalClasses.demoForOrderRequest.size}")

            for (item in GlobalClasses.demoForOrderRequest) {
                Log.e("SELECTED ORDER11>>>>>", "bindChild: ${item.toString()}")
            }


        }


    }

    override fun getItemCount(): Int = childItemList.size

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBoxChild: CheckBox = itemView.findViewById(R.id.checkbox_child)
    }


}
