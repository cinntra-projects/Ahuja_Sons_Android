package com.ahuja.sons.recyclerviewadapter

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.ahuja.sons.R
import com.ahuja.sons.activity.ChatActivity
import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.fragment.SelectEmployeeDialogFragment
import com.ahuja.sons.globals.Global
import com.lid.lib.LabelTextView
import com.ahuja.sons.newapimodel.TicketData
import java.util.*


class TicketNewAdapter(var AllitemsList: ArrayList<TicketData>) :
    RecyclerView.Adapter<TicketNewAdapter.Category_Holder>() {

    private lateinit var context: Context
    var tempList = ArrayList<TicketData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.tickets_view,
            parent,
            false
        )
        tempList.clear()
        tempList.addAll(AllitemsList)
        context = parent.context
        return Category_Holder(view)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        val model = AllitemsList[position]

        holder.company_name.text = "Status :" + model.Status
        try {
            if (model.AssignToDetails.size > 0){
                holder.name.text = "Assigned To : " +model.AssignToDetails[0].SalesEmployeeName
            }else{
                holder.name.text = "Assigned To : NA"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.date_value.text = Global.formatDateFromDateString(model.CreateDate)
        if (model.Title == ""){
            holder.message.text = "Title : "+model.Title
        }else{
            holder.message.text = "NA"
        }

        holder.priority.labelText = model.Priority
        holder.tvTicketType.text = "Type : " + model.Type
        if (model.BusinessPartner.size > 0){
            holder.tvCustomerName.text = "BP Name : " + model.BusinessPartner[0].CardName
        }else{
            holder.tvCustomerName.text = "BP Name : " + "NA"
        }

        if (model.BusinessPartner.size > 0){
            if (model.BusinessPartner[0].BPAddresses.size > 0){
                holder.tvSolID.text = "Sol Id : " + model.BusinessPartner[0].BPAddresses[0].AddressName
            }else{
                holder.tvSolID.text = "Sol Id : " + "NA"
            }
        }else{
            holder.tvSolID.text = "Sol ID : " + "NA"
        }

        if (model.Status == "In Progress") {
            holder.blink.isVisible = true
            val animation: Animation =
                AlphaAnimation(1.0f, 0.0f) //to change visibility from visible to invisible

            animation.duration = 1000 //1 second duration for each animation cycle

            animation.interpolator = LinearInterpolator()
            animation.repeatCount = Animation.INFINITE //repeating indefinitely

            animation.repeatMode =
                Animation.REVERSE //animation will start from end point once ended.

            holder.blink.startAnimation(animation) //to start animation

        } else {
            holder.blink.isVisible = false

        }


        when (model.Status) { //TicketStatus
            "Pending" -> {
                holder.color_type.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.safron_barChart))

            }
            "Rejected" -> {
                holder.color_type.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.red))
            }
            "Accepted" -> {
                holder.color_type.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.green))
            }
        }




        when (model.Priority) {
            "High" -> {
                holder.priority.labelBackgroundColor = context.getColor(R.color.red)
            }
            "Medium" -> {
                holder.priority.labelBackgroundColor = context.getColor(R.color.orange)

            }
            "Low" -> {
                holder.priority.labelBackgroundColor = context.getColor(R.color.yellow)

            }
        }

        holder.sr_no.text = "Ticket ID #" + model.id.toString()
        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (model.AssignToDetails.size > 0){
            if (model.AssignToDetails[0].SalesEmployeeName?.isNotEmpty()!!) {
                val drawable: TextDrawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4) /* thickness in px */
                    .endConfig()
                    .buildRound(
                        model.AssignToDetails[0].SalesEmployeeName[0].toString()
                            .uppercase(Locale.getDefault()), color1
                    )
                holder.imageman.setImageDrawable(drawable)
            } else {
                holder.imageman.background = ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
            }
        } else {
            holder.imageman.background = ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
        }


        holder.itemView.setOnClickListener {


            val intent = Intent(context, TicketDetailsActivity::class.java)
            // intent.putExtra("TicketData",model)
            intent.putExtra("id", model.id.toString())
            intent.putExtra("type", model.Type.toString())

            // intent.putExtra("TicketData",model)

            context.startActivity(intent)
        }



        holder.imageman.setOnClickListener {


            /* when (model.TicketStatus) {
                 "Pending" -> {
                     Global.warningdialogbox(context,"This ticket is in pending state and assigned to ${model.AssignToDetails.SalesEmployeeName}")

                 }
                 "Rejected" -> {
                     SelectEmployeeDialogFragment.newInstance("ID", AllitemsList[position].id.toString()).show((context as AppCompatActivity).supportFragmentManager, SelectEmployeeDialogFragment.TAG)
                 }
                 "Accepted" -> {
                     Global.warningdialogbox(context,"This ticket is already accepted")
                 }
             }*/

            if (AllitemsList.size > 0) {
                if (AllitemsList[position].Status != "Resolved") {
                    SelectEmployeeDialogFragment.newInstance("ID", AllitemsList[position].id.toString()).show(
                        (context as AppCompatActivity).supportFragmentManager,
                        SelectEmployeeDialogFragment.TAG
                    )
                } else {
                    Global.warningdialogbox(context, "Your ticket is resolved")

                }
            }


        }

        holder.chat.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(Global.INTENT_TICKET_ID, model.id.toString())
            intent.putExtra(Global.INTENT_TICKET_STATUS, model.Status)
            // intent.putExtra("TicketData",model)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }


    fun AllData(tmp: ArrayList<TicketData>?) {
        tempList.clear()
        tempList.addAll(tmp!!)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filter(newText: String) {
        Log.e("AllSiz", AllitemsList.size.toString())
        Log.e("tmpSiz", tempList.size.toString())
        AllitemsList.clear()
        if (newText.isEmpty())
            AllitemsList.addAll(tempList)
        else {
            AllitemsList.addAll(tempList.filter {
                it.AssignToDetails[0].SalesEmployeeName?.lowercase(Locale.getDefault())
                    ?.contains(newText)!!
            })


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
            AllitemsList.addAll(tempList.filter { it.Status == newText })
        }
        notifyDataSetChanged()
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageman = itemView.findViewById<ImageView>(R.id.imageman)
        val message = itemView.findViewById<TextView>(R.id.message)
        val name = itemView.findViewById<TextView>(R.id.name)
        val company_name = itemView.findViewById<TextView>(R.id.company_name)
        val date_value = itemView.findViewById<TextView>(R.id.date_value)
        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val chat = itemView.findViewById<ImageView>(R.id.chat)
        val priority = itemView.findViewById<LabelTextView>(R.id.priority)
        val color_type = itemView.findViewById<ImageView>(R.id.color_type)
        val blink = itemView.findViewById<ImageView>(R.id.blink)
        val tvCustomerName = itemView.findViewById<TextView>(R.id.tvCustomerName)
        val tvSolID = itemView.findViewById<TextView>(R.id.tvSolID)
        val tvTicketType = itemView.findViewById<TextView>(R.id.tvTicketType)


    }


}
