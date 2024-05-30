package com.ahuja.sons.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.TicketHistoryData
import java.util.*
import kotlin.collections.ArrayList


class ProductConversationAdapter(val tickethistorydata: ArrayList<TicketHistoryData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        context = parent.context
        val inflater = LayoutInflater.from(context)
        /*  when(viewType){
              Common.VIEWTYPE_GROUP ->{
                  val group : ViewGroup = inflater.inflate(R.layout.group_item_date,parent,false) as ViewGroup
                  return GroupViewHolder(group)
              }
              Common.VIEWTYPE_DATA ->{
                  val salesLayout : ViewGroup = inflater.inflate(R.layout.conv_ticekt_detail,parent,false) as ViewGroup
                  return Category_Holder(salesLayout)
              }
              else ->{
                  val group : ViewGroup = inflater.inflate(R.layout.group_item_date,parent,false) as ViewGroup
                  return GroupViewHolder(group)
              }
          }*/
        val salesLayout: ViewGroup =
            inflater.inflate(R.layout.conv_ticekt_detail, parent, false) as ViewGroup
        return Category_Holder(salesLayout)
    }


    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var text_date: TextView = itemView.findViewById(R.id.text_date)

        /*   init {
               itemView.setOnClickListener(View.OnClickListener {
                 //  context?.startActivity(Intent(context, Order_details::class.java))
               })
           }*/
    }


    override fun getItemViewType(position: Int): Int {
        return tickethistorydata[position].viewType

    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            val groupViewHolder: ProductConversationAdapter.GroupViewHolder = holder
            groupViewHolder.text_date.text = Global.convertTimestampToCustomFormat(tickethistorydata[position].Datetime)
        } else if (holder is Category_Holder) {
            val salesreturnholder: ProductConversationAdapter.Category_Holder = holder
            salesreturnholder.title.text = tickethistorydata[position].OwnerName
            salesreturnholder.message.text = tickethistorydata[position].Message
            salesreturnholder.time.text = Global.convertTimestampToCustomFormat(tickethistorydata[position].Datetime)
            salesreturnholder.status.text = tickethistorydata[position].Type

           /* val generator: ColorGenerator = ColorGenerator.MATERIAL
            val color1: Int = generator.randomColor
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) *//* thickness in px *//*
                .endConfig()
                .buildRound(tickethistorydata[position].OwnerName[0].toString().uppercase(Locale.getDefault()), color1)
            salesreturnholder.name_icon.setImageDrawable(drawable)*/
        }


    }


    override fun getItemCount(): Int {
        return tickethistorydata.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val threedot = itemView.findViewById<LinearLayout>(R.id.threedot)
        val message = itemView.findViewById<TextView>(R.id.message)
        val title = itemView.findViewById<TextView>(R.id.company_name)
        val time = itemView.findViewById<TextView>(R.id.date)
        val status = itemView.findViewById<TextView>(R.id.status)
        val name_icon = itemView.findViewById<ImageView>(R.id.name_icon)


    }


}
