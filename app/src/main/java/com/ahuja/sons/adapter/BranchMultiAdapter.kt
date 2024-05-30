package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.AddAccountActivity
import com.ahuja.sons.model.BPLID

class BranchMultiAdapter() : RecyclerView.Adapter<BranchMultiAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var taxList: List<BPLID>
    lateinit var databaseClick: Context


    constructor(
        context: AddAccountActivity,
        data: List<BPLID?>
    ) : this() {
        this.taxList = data as List<BPLID>
        this.databaseClick = context

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.district_item, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        /*  holder.title.setText(teamList.get(position).getBPLName());
        if(teamList.get(position).isSelected())
            holder.selected.setChecked(true);
        else
            holder.selected.setChecked(false);*/
        // holder.selected.setOnCheckedChangeListener(null);
        holder.selected.text = taxList[position].getBPLName()
        holder.selected.isChecked = taxList[position].isSelected()
        holder.selected.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            taxList[position].setSelected(isChecked)
        })
    }

    override fun getItemCount(): Int {
        return taxList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val selected: CheckBox

        init {
            selected = itemView.findViewById(R.id.selected)
            itemView.setOnClickListener {
                notifyDataSetChanged()
            }
        }
    }


}
