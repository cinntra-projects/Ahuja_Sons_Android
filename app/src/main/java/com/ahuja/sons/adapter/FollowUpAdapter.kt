package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.FollowUpLayoutBinding
import com.ahuja.sons.newapimodel.DataFollowUpList

class FollowUpAdapter(private val ticketFollowUpdata: ArrayList<DataFollowUpList>) : RecyclerView.Adapter<FollowUpAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(FollowUpLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatModel = ticketFollowUpdata[position]

        if (chatModel.Mode == "Call"){
            holder.binding.title.text = chatModel.Mode + " - " + chatModel.Message
            holder.binding.priorityDot.setImageResource(R.drawable.ic_call)
        } else if (chatModel.Mode.trim() == "Whatsapp") {
            holder.binding.title.text = chatModel.Mode +" - " + chatModel.Message
            holder.binding.priorityDot.setImageResource(R.drawable.ic_whatsapp)
        } else if (chatModel.Mode.trim() == "SMS") {
            holder.binding.title.text = chatModel.Mode +" - " + chatModel.Message
            holder.binding.priorityDot.setImageResource(R.drawable.ic_sms)
        } else if (chatModel.Mode.trim() == "E-Mail") {
            holder.binding.title.text = chatModel.Mode +" - " + chatModel.Message
            holder.binding.priorityDot.setImageResource(R.drawable.ic_email_latest)
        }

        holder.binding.message.text = chatModel.UpdateDate

    }


    override fun getItemCount(): Int {
        return ticketFollowUpdata.size
    }

    inner class ViewHolder(var binding: FollowUpLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}