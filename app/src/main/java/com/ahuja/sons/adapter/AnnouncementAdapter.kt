package com.ahuja.sons.adapter

import com.ahuja.sons.databinding.ItemAnnouncementBinding
import com.ahuja.sons.newapimodel.ResponseAnnouncementData



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder



class AnnouncementAdapter :
    RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>(
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
        DiffUtil.ItemCallback<ResponseAnnouncementData>() {
        override fun areItemsTheSame(
            oldItem: ResponseAnnouncementData,
            newItem: ResponseAnnouncementData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ResponseAnnouncementData,
            newItem: ResponseAnnouncementData
        ): Boolean {
            return oldItem == newItem
        }

    }


    inner class AnnouncementViewHolder(var binding: ItemAnnouncementBinding) :
        ViewHolder(binding.root) {


        fun bind(currentAnnouncement: ResponseAnnouncementData, parentPosition: Int) {
            binding.apply {

              tvAnnouncementName.text=currentAnnouncement.CampaignSetName
              tvAnnouncementDesc.text=currentAnnouncement.Description


            }
        }


    }


    val differ = AsyncListDiffer(this, differCallback)
    var announcement: MutableList<ResponseAnnouncementData>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder(
            ItemAnnouncementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val contest = announcement[position]
        holder.bind(contest, position)
        //childMembersAdapter.childItemClickListener = holder


    }

    override fun getItemCount(): Int {
        return announcement.size
    }


}