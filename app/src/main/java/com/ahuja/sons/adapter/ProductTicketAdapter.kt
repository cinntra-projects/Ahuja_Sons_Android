package com.ahuja.sons.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.model.TicketHistoryData


class ProductTicketAdapter(val tickethistorydata: ArrayList<TicketHistoryData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()   {

    private  lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       context = parent.context
        val inflater = LayoutInflater.from(context)

        /*when(viewType){
            Common.VIEWTYPE_GROUP ->{
                val group : ViewGroup = inflater.inflate(R.layout.group_item_date,parent,false) as ViewGroup
                return GroupViewHolder(group)
            }
            Common.VIEWTYPE_DATA ->{
                val salesLayout : ViewGroup = inflater.inflate(R.layout.log_ticket_detail,parent,false) as ViewGroup
                return Category_Holder(salesLayout)
            }
            else ->{
                val group : ViewGroup = inflater.inflate(R.layout.group_item_date,parent,false) as ViewGroup
                return GroupViewHolder(group)
            }
        }*/
        val salesLayout : ViewGroup = inflater.inflate(R.layout.log_ticket_detail,parent,false) as ViewGroup
        return Category_Holder(salesLayout)




    }

    override fun getItemViewType(position: Int): Int {
        return tickethistorydata[position].viewType

    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var text_date : TextView = itemView.findViewById(R.id.text_date)

        /*   init {
               itemView.setOnClickListener(View.OnClickListener {
                 //  context?.startActivity(Intent(context, Order_details::class.java))
               })
           }*/
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is GroupViewHolder){
            val groupViewHolder : GroupViewHolder = holder
            groupViewHolder.text_date.text = tickethistorydata[position].Datetime
        }else if (holder is Category_Holder){
            val salesreturnholder : Category_Holder = holder
            salesreturnholder.title.text = tickethistorydata[position].Type
            salesreturnholder.message.text = tickethistorydata[position].Remarks
            salesreturnholder.time.text = tickethistorydata[position].Datetime
        }

    }

    override fun getItemCount(): Int {
        return tickethistorydata.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.title)
        val message = itemView.findViewById<TextView>(R.id.message)
        val time = itemView.findViewById<TextView>(R.id.time)





    }





}
