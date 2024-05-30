package com.ahuja.sons.adapter

import com.ahuja.sons.databinding.ItemAnnouncementBinding


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ahuja.sons.R
import com.ahuja.sons.newapimodel.DataQualityInspectionList


class QualityInspectionAdapter :
    RecyclerView.Adapter<QualityInspectionAdapter.QualityInspectionViewHolder>(
    ) {


//    interface RvItemClickListener {
//        fun onChildItemClick(parentPosition: Int, childPosition: Int, item: ContestAll?)
//    }
//
//    private var rvItemClickListener: RvItemClickListener? = null
//
//    fun setRvClickListener(rvItemClickListener: RvItemClickListener?) {
//        this.rvItemClickListener = rvItemClickListener
//    }


    private val differCallback = object :
        DiffUtil.ItemCallback<DataQualityInspectionList>() {
        override fun areItemsTheSame(
            oldItem: DataQualityInspectionList,
            newItem: DataQualityInspectionList
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataQualityInspectionList,
            newItem: DataQualityInspectionList
        ): Boolean {
            return oldItem == newItem
        }

    }


    inner class QualityInspectionViewHolder(var binding: ItemAnnouncementBinding) :
        ViewHolder(binding.root) {


        fun bind(currentAnnouncement: DataQualityInspectionList, parentPosition: Int) {
            binding.apply {

              tvAnnouncementName.text=currentAnnouncement.TicketId
              tvAnnouncementDesc.text=currentAnnouncement.Description
                imageView2.setImageResource(R.drawable.ic_log_item)



            }
        }


    }


    val differ = AsyncListDiffer(this, differCallback)
    var announcement: MutableList<DataQualityInspectionList>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityInspectionViewHolder {
        return QualityInspectionViewHolder(
            ItemAnnouncementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: QualityInspectionViewHolder, position: Int) {
        val contest = announcement[position]
        holder.bind(contest, position)
        //childMembersAdapter.childItemClickListener = holder


    }

    override fun getItemCount(): Int {
        return announcement.size
    }


}