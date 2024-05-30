package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.EscallationListAdapterLayoutBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.EscallationResponseModel
import java.util.ArrayList

class EscallationAdapter (val AllitemsList: ArrayList<EscallationResponseModel.DataXXX>): RecyclerView.Adapter<EscallationAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<EscallationResponseModel.DataXXX> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(EscallationListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {

            tvBranchName.text = current.Name
            tvMobile.text = current.Phone
            tvEmail.text = current.Email
            tvDateTime.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(current.CreateDate) + " " + current.CreateTime
            tvEmail.tooltipText = current.Email

            if (position == 0){
                backgroundCardView.setCardBackgroundColor(context.resources.getColor(R.color.primaryShade))
                tvBranchName.setTextColor(context.resources.getColor(R.color.colorPrimary))
            }else{
                backgroundCardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
                tvBranchName.setTextColor(context.resources.getColor(R.color.grey))
            }

        }


    }



    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: EscallationListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)



}