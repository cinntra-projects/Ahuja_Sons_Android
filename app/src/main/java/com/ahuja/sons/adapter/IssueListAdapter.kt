package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.databinding.IssueListLayoutAdapterBinding
import com.ahuja.sons.newapimodel.IssueListResponseModel
import java.util.ArrayList

class IssueListAdapter (val AllitemsList: ArrayList<IssueListResponseModel.DataXXX>) : RecyclerView.Adapter<IssueListAdapter.Category_Holder>() {

    private lateinit var context: Context

    private var onItemClickListener: ((IssueListResponseModel.DataXXX, Int) -> Unit)? = null

    fun setOnIssueItemClickListener(listener: (IssueListResponseModel.DataXXX, Int) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(IssueListLayoutAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val current = AllitemsList[position]
        holder.binding.apply {

            tvIssueType.text = current.IssueType
            tvSolution.text = current.Solution

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

    inner class Category_Holder(var binding: IssueListLayoutAdapterBinding) : RecyclerView.ViewHolder(binding.root)


}