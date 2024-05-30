package com.ahuja.sons.ahujaSonsClasses.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.ChatActivity
import com.ahuja.sons.ahujaSonsClasses.activity.ParticularOrderDetailActivity
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.globals.Global
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.lid.lib.LabelTextView
import java.util.*

class OrderListAdapter(var AllitemsList: ArrayList<AllOrderListResponseModel.Data>) : RecyclerView.Adapter<OrderListAdapter.Category_Holder>() {

    private lateinit var context: Context
    var tempList = ArrayList<AllOrderListResponseModel.Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_view_adapter_layout, parent, false)
        tempList.clear()
        tempList.addAll(AllitemsList)
        context = parent.context
        return Category_Holder(view)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        val model = AllitemsList[position]

        holder.sr_no.text = "Order DocNum #" + model.DocNum.toString()


        if (!model.CardName.isEmpty()){
            holder.tvCustomerName.text = "Customer : " + model.CardName
        }else{
            holder.tvCustomerName.text = "Customer : " + "NA"
        }

        holder.date_value.text = Global.formatDateFromDateString(model.DocDate)


        if (model.DocumentStatus == "bost_Close") {
            holder.blink.isVisible = true
            val animation: Animation = AlphaAnimation(1.0f, 0.0f) //to change visibility from visible to invisible

            animation.duration = 1000 //1 second duration for each animation cycle

            animation.interpolator = LinearInterpolator()
            animation.repeatCount = Animation.INFINITE //repeating indefinitely

            animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.

            holder.blink.startAnimation(animation) //to start animation

        } else {
            holder.blink.isVisible = false

        }


        when (model.DocumentStatus) { //TicketStatus
            "bost_Open" -> {
                holder.tvSolID.text = "Status : Open"
                holder.color_type.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.green))
//                holder.color_type.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.safron_barChart))

            }
            "bost_Close" -> {
                holder.tvSolID.text = "Status : Close"
                holder.color_type.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(R.color.red))
            }

        }


    /*    when (model.Priority) {
            "High" -> {
                holder.priority.labelBackgroundColor = context.getColor(R.color.red)
            }
            "Medium" -> {
                holder.priority.labelBackgroundColor = context.getColor(R.color.orange)

            }
            "Low" -> {
                holder.priority.labelBackgroundColor = context.getColor(R.color.yellow)

            }
        }*/

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (model.SalesPersonCode.size > 0){
            if (model.SalesPersonCode[0].SalesEmployeeName?.isNotEmpty()!!) {
                val drawable: TextDrawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4) /* thickness in px */
                    .endConfig()
                    .buildRound(
                        model.SalesPersonCode[0].SalesEmployeeName[0].toString().uppercase(Locale.getDefault()), color1
                    )
                holder.imageman.setImageDrawable(drawable)
            } else {
                holder.imageman.background = ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
            }
        } else {
            holder.imageman.background = ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ParticularOrderDetailActivity::class.java)
            intent.putExtra("id", model.id.toString())

            context.startActivity(intent)
        }



        holder.imageman.setOnClickListener {

            if (AllitemsList.size > 0) {
              /*  if (AllitemsList[position].Status != "Resolved") {
                    SelectEmployeeDialogFragment.newInstance("ID", AllitemsList[position].id.toString()).show((context as AppCompatActivity).supportFragmentManager, SelectEmployeeDialogFragment.TAG)
                } else {
                    Global.warningdialogbox(context, "Your ticket is resolved")

                }*///todo comment
            }


        }

        holder.chat.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(Global.INTENT_TICKET_ID, model.id.toString())
//            intent.putExtra(Global.INTENT_TICKET_STATUS, model.Status)
            // intent.putExtra("TicketData",model)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }



    @SuppressLint("NotifyDataSetChanged")
    fun filter(newText: String) {
        Log.e("AllSiz", AllitemsList.size.toString())
        Log.e("tmpSiz", tempList.size.toString())
        AllitemsList.clear()
        if (newText.isEmpty())
            AllitemsList.addAll(tempList)
        else {
          /*  AllitemsList.addAll(tempList.filter {
                it.AssignToDetails[0].SalesEmployeeName?.lowercase(Locale.getDefault())?.contains(newText)!!
            })*/

        }
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun statusfilter(newText: String) {
        Log.e("AllSiz", AllitemsList.size.toString())
        Log.e("tmpSiz", tempList.size.toString())
        AllitemsList.clear()
        if (newText.isEmpty())
            AllitemsList.addAll(tempList)
        else {
//            AllitemsList.addAll(tempList.filter { it.Status == newText })
        }
        notifyDataSetChanged()
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageman = itemView.findViewById<ImageView>(R.id.imageman)
        val message = itemView.findViewById<TextView>(R.id.message)
        val date_value = itemView.findViewById<TextView>(R.id.date_value)
        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val chat = itemView.findViewById<ImageView>(R.id.chat)
        val priority = itemView.findViewById<LabelTextView>(R.id.priority)
        val color_type = itemView.findViewById<ImageView>(R.id.color_type)
        val blink = itemView.findViewById<ImageView>(R.id.blink)
        val tvCustomerName = itemView.findViewById<TextView>(R.id.tvCustomerName)
        val tvSolID = itemView.findViewById<TextView>(R.id.tvSolID)


    }


}