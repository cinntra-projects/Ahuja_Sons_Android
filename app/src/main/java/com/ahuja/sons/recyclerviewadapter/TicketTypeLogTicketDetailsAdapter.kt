package com.ahuja.sons.recyclerviewadapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ahuja.sons.R
import com.ahuja.sons.databinding.LogTicketDetailBinding
import com.ahuja.sons.newapimodel.DataTicketLogForTicketDetails

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TicketTypeLogTicketDetailsAdapter :
    RecyclerView.Adapter<TicketTypeLogTicketDetailsAdapter.TicketTypeLogTicketDetailsViewHolder>(
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
        DiffUtil.ItemCallback<DataTicketLogForTicketDetails>() {
        override fun areItemsTheSame(
            oldItem: DataTicketLogForTicketDetails,
            newItem: DataTicketLogForTicketDetails
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataTicketLogForTicketDetails,
            newItem: DataTicketLogForTicketDetails
        ): Boolean {
            return oldItem == newItem
        }

    }


    inner class TicketTypeLogTicketDetailsViewHolder(var binding: LogTicketDetailBinding) :
        ViewHolder(binding.root) {


        fun bind(currentTicketTypeLog: DataTicketLogForTicketDetails, parentPosition: Int) {
            binding.apply {

                title.text =itemView.resources.getString(R.string.logPrevType_dynamic, currentTicketTypeLog.PrevType)
                message.text = currentTicketTypeLog.Remarks

                val inputFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                try {
                    val date: Date = inputFormat.parse(currentTicketTypeLog.Datetime)
                    val formattedDate: String = outputFormat.format(date)
                    // println(formattedDate)
                    time.text = formattedDate
                } catch (e: ParseException) {
                    e.printStackTrace()
                }


            }
        }


    }


    val differ = AsyncListDiffer(this, differCallback)
    var ticketLog: MutableList<DataTicketLogForTicketDetails>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TicketTypeLogTicketDetailsViewHolder {
        return TicketTypeLogTicketDetailsViewHolder(
            LogTicketDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TicketTypeLogTicketDetailsViewHolder, position: Int) {
        val contest = ticketLog[position]
        holder.bind(contest, position)
        //childMembersAdapter.childItemClickListener = holder


    }

    override fun getItemCount(): Int {
        return ticketLog.size
    }


}