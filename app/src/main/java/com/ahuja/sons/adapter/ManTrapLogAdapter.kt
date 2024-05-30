package com.ahuja.sons.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ahuja.sons.databinding.ItemManTapListBinding
import com.ahuja.sons.globals.Global.formatserverDateFromWeirdDateFormat
import com.ahuja.sons.newapimodel.DataManTrapLogo


class ManTrapLogAdapter :
    RecyclerView.Adapter<ManTrapLogAdapter.AnnouncementViewHolder>(
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
        DiffUtil.ItemCallback<DataManTrapLogo>() {
        override fun areItemsTheSame(
            oldItem: DataManTrapLogo,
            newItem: DataManTrapLogo
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataManTrapLogo,
            newItem: DataManTrapLogo
        ): Boolean {
            return oldItem == newItem
        }

    }


    inner class AnnouncementViewHolder(var binding: ItemManTapListBinding) :
        ViewHolder(binding.root) {


        fun bind(currentAnnouncement: DataManTrapLogo, parentPosition: Int) {
            binding.apply {

              tvCurrentStatus.text=currentAnnouncement.Status
              tvAnnouncementDesc.text=currentAnnouncement.Remarks
              tvDateOfManTrap.text= formatserverDateFromWeirdDateFormat(currentAnnouncement.Datetime)

            }
        }


    }


    val differ = AsyncListDiffer(this, differCallback)
    var announcement: MutableList<DataManTrapLogo>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder(
            ItemManTapListBinding.inflate(
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