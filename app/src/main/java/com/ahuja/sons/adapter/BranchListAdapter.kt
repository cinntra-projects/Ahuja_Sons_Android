package com.ahuja.sons.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.UpdateEmployeeActivity
import com.ahuja.sons.databinding.BranchListAdapterLayoutBinding
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import java.util.*

class BranchListAdapter (val AllitemsList: ArrayList<BranchAllListResponseModel.DataXXX>): RecyclerView.Adapter<BranchListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<BranchAllListResponseModel.DataXXX> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(BranchListAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {

            tvBranchName.text = current.AddressName
            tvLocation.text = current.Street

            threeDotsLayout.setOnClickListener {
                showPopupMenu(holder.binding.threeDotsLayout, current.id)
            }


        }


    }

    private fun showPopupMenu(view: View, id: String) {
        var popupMenu : PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.employee_menu, popupMenu.menu)


        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    val intent = Intent(context, UpdateEmployeeActivity::class.java)
                    intent.putExtra("id", id)
                    context.startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: BranchListAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)



}