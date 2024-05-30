package com.ahuja.sons.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.TicketWiseItemAdapterLayoutBinding
import com.ahuja.sons.newapimodel.ItemAllListResponseModel

class TicketWiseItemAdapter(var itemAllListResponseList: List<ItemAllListResponseModel.DataXXX>) : RecyclerView.Adapter<TicketWiseItemAdapter.Category_Holder>() {

    private lateinit var context: Context

    private var OnItemClick: ((ItemAllListResponseModel.DataXXX) -> Unit)? = null

    fun setOnTicketItemListener(listener: (ItemAllListResponseModel.DataXXX) -> Unit) {
        OnItemClick = listener
    }

    private var OnEditClick: ((ItemAllListResponseModel.DataXXX) -> Unit)? = null

    fun setOnEditItemClick(listener: (ItemAllListResponseModel.DataXXX) -> Unit) {
        OnEditClick = listener
    }

    private var OnAttachClick: ((ItemAllListResponseModel.DataXXX) -> Unit)? = null

    fun setOnAttachItemClick(listener: (ItemAllListResponseModel.DataXXX) -> Unit) {
        OnAttachClick = listener
    }

    private var OnPdfClick: ((ItemAllListResponseModel.DataXXX) -> Unit)? = null

    fun setOnPdfItemClick(listener: (ItemAllListResponseModel.DataXXX) -> Unit) {
        OnPdfClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(TicketWiseItemAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        var dataModel = itemAllListResponseList[position]
        holder.binding.apply {
                tvItemCode.text = dataModel.ItemCode
                tvItemName.text = dataModel.ItemName
                tvQTY.text = dataModel.Quantity.toString()
                tvItemGroupName.text = dataModel.ItemsGroupName
                tvSerial.text = dataModel.SerialNo
                tvUnitPrice.text = dataModel.UnitPrice
            if (dataModel.Remarks.isNotEmpty()) {
                tvRemark.text = dataModel.Remarks
            }else{
                tvRemark.text = "NA"
            }


            threeDotsLayout.setOnClickListener {
                if (dataModel.is_Reported == false) {
                    showAddReportPopupMenu(holder.binding.threeDotsLayout, dataModel)
                }
                else if (dataModel.is_Reported == true){
                    showUpdateReportPopupMenu(holder.binding.threeDotsLayout, dataModel)
                }

            }

            if (dataModel.is_Reported == true){
                cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#DDF6E0")))
            }


        }

        holder.itemView.setOnClickListener {
            OnAttachClick?.let { click ->
                click(dataModel)
            }
        }




    }

    private fun showAddReportPopupMenu(view: View, dataModel: ItemAllListResponseModel.DataXXX) {
        var popupMenu : PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.ticket_type_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_report -> {
                    OnItemClick?.let { click ->
                        click(dataModel)
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showUpdateReportPopupMenu(view: View, dataModel: ItemAllListResponseModel.DataXXX) {
        var popupMenu : PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.update_report_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    OnEditClick?.let { click ->
                        click(dataModel)
                    }
                    true
                }
                R.id.pdfReport -> {
                    OnPdfClick?.let { click ->
                        click(dataModel)
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


    override fun getItemCount(): Int {
        return itemAllListResponseList.size
    }

    inner class Category_Holder(var binding: TicketWiseItemAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}