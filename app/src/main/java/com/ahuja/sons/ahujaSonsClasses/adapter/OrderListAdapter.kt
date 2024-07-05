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
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.databinding.ItemWorkQueueBinding
import com.ahuja.sons.globals.Global
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.lid.lib.LabelTextView
import java.util.*

class OrderListAdapter(
    var AllitemsList: ArrayList<AllOrderListResponseModel.Data>,
    var where: String
) :
    RecyclerView.Adapter<OrderListAdapter.Category_Holder>() {

    private lateinit var context: Context
    var tempList = ArrayList<AllOrderListResponseModel.Data>()


    private var onItemClickListener: ((AllOrderListResponseModel.Data, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (AllOrderListResponseModel.Data, Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_work_queue, parent, false)
        val binding =
            ItemWorkQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        tempList.clear()
        tempList.addAll(AllitemsList)
        context = parent.context
        return Category_Holder(binding)


    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        val model = AllitemsList[position]


        if (where.equals(RoleClass.deliveryPerson, ignoreCase = true)) {
            holder.binding.chipOrderType.visibility = View.VISIBLE
        } else {
            holder.binding.chipOrderType.visibility = View.GONE
        }



        holder.binding.tvOrderId.text = "Order ID: " + model.DocNum.toString()


        if (!model.CardName.isEmpty()) {
            holder.binding.tvOrderName.text = "" + model.CardName
        } else {
            holder.binding.tvOrderName.text = "" + "NA"
        }

        holder.binding.tvSurgeryDateTime.text = Global.formatDateFromDateString(model.DocDate)





        when (model.DocumentStatus) { //TicketStatus
            "bost_Open" -> {
                holder.binding.tvStatusOrder.text = "Status : Open"

            }
            "bost_Close" -> {
                holder.binding.tvStatusOrder.text = "Status : Close"


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
        /*       if (model.SalesPersonCode.size > 0) {
                   if (model.SalesPersonCode[0].SalesEmployeeName?.isNotEmpty()!!) {
                       val drawable: TextDrawable = TextDrawable.builder()
                           .beginConfig()
                           .withBorder(4) *//* thickness in px *//*
                    .endConfig()
                    .buildRound(
                        model.SalesPersonCode[0].SalesEmployeeName[0].toString()
                            .uppercase(Locale.getDefault()), color1
                    )
                holder.binding.profilePic.setImageDrawable(drawable)
            } else {
                holder.binding.profilePic.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
            }
        } else {
            holder.binding.profilePic.background =
                ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
        }*/

        if (model.CardName.isNotEmpty()) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    model.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            holder.binding.profilePic.setImageDrawable(drawable)
        } else {
            holder.binding.profilePic.background =
                ContextCompat.getDrawable(context, R.drawable.ic_group_18576)
        }


        holder.itemView.setOnClickListener {
            if (where.equals(RoleClass.deliveryPerson, ignoreCase = true)) {
                onItemClickListener?.let { click ->
                    click(model, position)
                }
            } else {
                val intent = Intent(context, ParticularOrderDetailActivity::class.java)
                intent.putExtra("id", model.id.toString())

                context.startActivity(intent)
            }


        }



        holder.binding.profilePic.setOnClickListener {

            if (AllitemsList.size > 0) {
                /*  if (AllitemsList[position].Status != "Resolved") {
                      SelectEmployeeDialogFragment.newInstance("ID", AllitemsList[position].id.toString()).show((context as AppCompatActivity).supportFragmentManager, SelectEmployeeDialogFragment.TAG)
                  } else {
                      Global.warningdialogbox(context, "Your ticket is resolved")

                  }*///todo comment
            }


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

    inner class Category_Holder(var binding: ItemWorkQueueBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }


}