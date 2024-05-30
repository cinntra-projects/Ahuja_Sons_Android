package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.OrderListAdapterLayoutBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.OrderListResponseModel
import java.util.*

class OrderListAdapter (val AllitemsList: ArrayList<OrderListResponseModel.DataXXX>): RecyclerView.Adapter<OrderListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<OrderListResponseModel.DataXXX> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }

    private var onItemClickListener: ((OrderListResponseModel.DataXXX, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (OrderListResponseModel.DataXXX, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onResetBtnClick: ((OrderListResponseModel.DataXXX, String, AlertDialog) -> Unit)? = null

    fun setOnResetBtnClickListener(listener: (OrderListResponseModel.DataXXX, String, AlertDialog) -> Unit) {
        onResetBtnClick = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(OrderListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {

            tvBusinessPartner.text = current.CardName
            tvOrderID.text = current.DocEntry
            tvDeliveryDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(current.TaxDate)
            tvOPSDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(current.DocDueDate)
            tvTotalAmount.text = "Rs " + current.NetTotal

            if (current.DocumentStatus == "bost_Close"){
                tvStatus.text = "Cancel"
                tvStatus.setTextColor(ContextCompat.getColor(context,R.color.red))
            }else{
                tvStatus.text = "Open"
                tvStatus.setTextColor(ContextCompat.getColor(context,R.color.green))
            }


            when(current.FinalStatus){
                "Approved" ->{
                    tvApprovalStatus.text = current.FinalStatus
                    tvApprovalStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                }
                "Pending" ->{
                    tvApprovalStatus.text = current.FinalStatus
                    tvApprovalStatus.setTextColor(ContextCompat.getColor(context, R.color.safron_barChart))
                }
                "Rejected" ->{
                    tvApprovalStatus.text = current.FinalStatus
                    tvApprovalStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }


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

    inner class Category_Holder(var binding: OrderListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    //todo search filter..
    fun filter(charText: String) {
        var charText = charText
        charText = charText.lowercase(Locale.getDefault())
        AllitemsList.clear()
        if (charText.length == 0) {
            AllitemsList.addAll(tempList)
        } else {
            for (st in tempList) {
                if (st.DocEntry != null && !st.DocEntry.isEmpty()) {
                    if (st.CardName.toLowerCase(Locale.getDefault()).contains(charText) || st.DocEntry.toLowerCase(Locale.getDefault()).contains(charText)) {
                        AllitemsList.add(st)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }


}