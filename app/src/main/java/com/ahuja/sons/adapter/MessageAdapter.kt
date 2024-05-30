package com.ahuja.sons.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.TicketHistoryData
import com.pixplicity.easyprefs.library.Prefs

class MessageAdapter(val chatActivity: Activity,val messagelist: ArrayList<TicketHistoryData>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val rootView: View =
            LayoutInflater.from(chatActivity).inflate(R.layout.chatter_layout, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chatModel = messagelist[position]
        holder.receivertext.text = chatModel.Message
       holder.receiverdatetime.text = Global.getDateAndTimeFromWeirdFormat(chatModel.Datetime,holder.itemView.context)

        if (chatModel.OwnerId.toString() == (Prefs.getString(Global.Employee_Code))) {
            holder.receiver_view.gravity = Gravity.RIGHT
            holder.textimage.setTextColor(chatActivity.resources.getColor(R.color.colorPrimary))
            holder.receivertext.background
                .setColorFilter(Color.parseColor("#956387DA"), PorterDuff.Mode.SRC_ATOP)
        } else {
            holder.receiver_view.gravity = Gravity.LEFT
            holder.textimage.setTextColor(chatActivity.resources.getColor(R.color.green))
            holder.receivertext.background
                .setColorFilter(Color.parseColor("#9455BD63"), PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun getItemCount(): Int {
       return messagelist.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var receivertext: TextView = itemView.findViewById(R.id.receivertext)
        var receiverdatetime: TextView = itemView.findViewById(R.id.receiverdatetime)
        var textimage: TextView = itemView.findViewById(R.id.textimage)
        var imageicon: LinearLayout = itemView.findViewById(R.id.imageicon)
        var receiver_view: LinearLayout = itemView.findViewById(R.id.receiver_view)
        var messagelayout: LinearLayout = itemView.findViewById(R.id.messagelayout)


    }

}
