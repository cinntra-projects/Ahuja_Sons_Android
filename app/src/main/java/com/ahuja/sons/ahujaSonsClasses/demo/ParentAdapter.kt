package com.ahuja.sons.ahujaSonsClasses.demo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses

class ParentAdapter(private val parentItemList: List<ParentItemModel>) : RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parent, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = parentItemList[position]
        holder.checkBoxParent.text = parentItem.name
        holder.checkBoxParent.isChecked = parentItem.isSelected

        // Initialize child RecyclerView
        val childAdapter = ChildAdapter(parentItem.childItemList)
        holder.recyclerViewChild.adapter = childAdapter
        holder.recyclerViewChild.layoutManager = LinearLayoutManager(holder.itemView.context)

       /* holder.checkBoxParent.setOnCheckedChangeListener { _, isChecked ->
            parentItem.isSelected = isChecked
            parentItem.childItemList.forEach { it.isSelected = isChecked }
            childAdapter.notifyDataSetChanged()
        }
*/
        holder.checkBoxParent.setOnCheckedChangeListener { _, isChecked ->
            parentItem.isSelected = isChecked
            if (isChecked) {

                val itemsToAdd = parentItem.childItemList.filterNot { GlobalClasses.demoForOrderRequest.contains(it) }

                if (itemsToAdd.isNotEmpty()) {
                    GlobalClasses.demoForOrderRequest.addAll(itemsToAdd)
                    Log.e("childItemCheck==>", "onBindViewHolder: Added new items")
                } else {
                    Log.e("childItemCheck==>", "onBindViewHolder: Already exists")
                }
                /*if (GlobalClasses.demoForOrderRequest.contains(parentItem.childItemList)){
                    Log.e("childItemCheck==>", "onBindViewHolder: Already exist")
                }else{
                    GlobalClasses.demoForOrderRequest.addAll(parentItem.childItemList)
                }*/

            } else {
                GlobalClasses.demoForOrderRequest.removeAll(parentItem.childItemList)
            }
            parentItem.childItemList.forEach { it.isSelected = isChecked }
            childAdapter.notifyDataSetChanged()

            Log.e("SELECTED ORDER>>>>>", "bindParent : ${GlobalClasses.demoForOrderRequest.size}")

            for (item in GlobalClasses.demoForOrderRequest) {
                Log.e("SELECTED ORDER12>>>>>", "bindParent: ${item.toString()}")
            }
        }


    }

    override fun getItemCount(): Int = parentItemList.size

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBoxParent: CheckBox = itemView.findViewById(R.id.checkbox_parent)
        val recyclerViewChild: RecyclerView = itemView.findViewById(R.id.recycler_view_child)
    }
}
