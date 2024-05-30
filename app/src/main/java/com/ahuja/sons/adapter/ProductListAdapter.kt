package com.ahuja.sons.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.UpdateProductActivity
import com.ahuja.sons.databinding.ProductListAdapterLayoutBinding
import com.ahuja.sons.newapimodel.ProductResponseModel
import java.util.*
import kotlin.collections.ArrayList

class ProductListAdapter (val AllitemsList: ArrayList<ProductResponseModel.DataXXX>): RecyclerView.Adapter<ProductListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<ProductResponseModel.DataXXX> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }

    private var onItemClickListener: ((ProductResponseModel.DataXXX, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (ProductResponseModel.DataXXX, Int) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(ProductListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {

            tvProductName.text = current.ItemName
            tvSerialNo.text = current.SerialNo
            tvCustomerName.text = current.CardName
            tvContractorName.text = current.ContractorName
            if (current.BPBranch.isNotEmpty()){
                tvBranch.text = current.BPBranch[0].AddressName
                tvBranch.tooltipText = current.BPBranch[0].AddressName
            }
            tvWarrantyStartDate.text = com.ahuja.sons.globals.Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(current.WarrantyStartDate)
            tvWarrantyEndDate.text = com.ahuja.sons.globals.Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(current.WarrantyEndDate)

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(current, position)

                }

            }


            threeDotsLayout.setOnClickListener {
                showPopupMenu(holder.binding.threeDotsLayout, current.id, current)
            }

        }


    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: ProductListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    private fun showPopupMenu(view: View, id: String, current: ProductResponseModel.DataXXX, ) {
        var popupMenu : PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.product_edit_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    val intent = Intent(context, UpdateProductActivity::class.java)
                    intent.putExtra("id", id)
                    context.startActivity(intent)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }


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