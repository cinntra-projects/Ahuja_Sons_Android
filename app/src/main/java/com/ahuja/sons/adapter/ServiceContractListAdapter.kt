package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.databinding.ServiceContractListLayoutAdapterBinding
import com.ahuja.sons.newapimodel.ServiceContractListResponseModel
import java.util.*
import kotlin.collections.ArrayList

class ServiceContractListAdapter(val AllitemsList: ArrayList<ServiceContractListResponseModel.DataXXX>): RecyclerView.Adapter<ServiceContractListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<ServiceContractListResponseModel.DataXXX> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }

    private var onItemClickListener: ((ServiceContractListResponseModel.DataXXX, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (ServiceContractListResponseModel.DataXXX, Int) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(ServiceContractListLayoutAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {

            tvCustomerName.text = current.CardName
            id.text = current.id
            tvBranch.text = current.AddressName
            tvFrequency.text = current.Frequency
            tvAssignedTo.text = current.AssignedToName
            tvFromDate.text = com.ahuja.sons.globals.Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(current.FromDate)
            tvToDate.text = com.ahuja.sons.globals.Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(current.ToDate)
            tvContractType.text = current.ContractType

            var tempList = ArrayList<String>()
            for (item in current.ServiceItemList) {
                tempList.add(item.ItemName)
            }
            val separatedSolution = tempList.joinToString(",")

            tvProduct.text = separatedSolution

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(current, position)

                }

            }


        }


    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: ServiceContractListLayoutAdapterBinding) : RecyclerView.ViewHolder(binding.root)


    //todo search filter..
    fun filter(charText: String) {
        var charText = charText
        charText = charText.lowercase(Locale.getDefault())
        AllitemsList.clear()
        if (charText.length == 0) {
            AllitemsList.addAll(tempList)
        } else {
            for (st in tempList) {
                if (st.CardName != null && !st.CardName.isEmpty()) {
                    if (st.id.toLowerCase(Locale.getDefault()).contains(charText)) {
                        AllitemsList.add(st)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }


}