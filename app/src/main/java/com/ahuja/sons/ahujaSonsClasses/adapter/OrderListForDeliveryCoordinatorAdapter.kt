package com.ahuja.sons.ahujaSonsClasses.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.ChatActivity
import com.ahuja.sons.ahujaSonsClasses.activity.ParticularOrderDetailActivity
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.GlobalClasses
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalSelectedOrder
import com.ahuja.sons.databinding.ItemWorkQueueBinding
import com.ahuja.sons.globals.Global
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.lid.lib.LabelTextView
import java.util.*

class OrderListForDeliveryCoordinatorAdapter(
    var AllitemsList: ArrayList<AllOrderListResponseModel.Data>,
    var where: String, var isMultiOrderCardSelectEnabled: Boolean,
    var checkBox: CheckBox
) :
    RecyclerView.Adapter<OrderListForDeliveryCoordinatorAdapter.Category_Holder>() {

    private lateinit var context: Context
    var tempList = ArrayList<AllOrderListResponseModel.Data>()

    var isAllSelectedMethodisWorking = false
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


    fun isUpdated(isMultiOrder: Boolean) {
        this.isMultiOrderCardSelectEnabled = isMultiOrder
        notifyDataSetChanged()
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        val model = AllitemsList[position]



       /* checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                holder.binding.checkBoxOrder.isChecked = true
                isMultiOrderCardSelectEnabled = true
                isAllSelectedMethodisWorking = true
                GlobalClasses.cartListForOrderRequest.clear()
                for (currentDocLine in AllitemsList) {
                    var localSelectedOrder = LocalSelectedOrder()
                    localSelectedOrder.apply {
                        orderId = currentDocLine.id
                        orderName = currentDocLine.CardName
                    }

                    *//*   GlobalClasses.cartListForOrderRequest.add(
                           localSelectedOrder
                       )*//*

                    GlobalClasses.cartListForOrderRequest[currentDocLine.id] = localSelectedOrder
                    val newColorStateList =
                        ColorStateList.valueOf(context.resources.getColor(R.color.blue_light))
                    holder.binding.constraintLayoutWorkQueue.backgroundTintList = newColorStateList
                }
            } else {
                isMultiOrderCardSelectEnabled = false
                isAllSelectedMethodisWorking = true
                holder.binding.checkBoxOrder.isChecked = false
                GlobalClasses.cartListForOrderRequest.clear()
                val newColorStateList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.white))
                holder.binding.constraintLayoutWorkQueue.backgroundTintList = newColorStateList
            }

            notifyDataSetChanged()

            Log.e("SIZESELECTALL>>>>", "onBindViewHolder: ${GlobalClasses.cartListForOrderRequest.size}" )

        }*/


        holder.bind(model, context)
        holder.binding.chipOrderType.visibility = View.VISIBLE


        /*    holder.binding.checkBoxOrder.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {

                } else {

                }
            }*/

        /*holder.binding.checkBoxOrder.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                holder.binding.checkBoxOrder.isChecked = true
                var localSelectedOrder = LocalSelectedOrder()
                localSelectedOrder.apply {
                    orderId = model.DocNum
                    orderName = model.CardName
                }
                GlobalClasses.cartListForOrderRequest.add(
                    localSelectedOrder
                )


            } else {
                var pos = -1
                holder.binding.checkBoxOrder.isChecked = false
                GlobalClasses.cartListForOrderRequest.forEachIndexed { index, documentLine ->
                    if (model.DocNum == documentLine.orderId) {
                        pos = index
                        GlobalClasses.cartListForOrderRequest.removeAt(pos)
                    }

                }
            }

            notifyDataSetChanged()
            Log.e("SELECTED ORDER>>>>>", "bind: ${GlobalClasses.cartListForOrderRequest.size}")

            for (item in GlobalClasses.cartListForOrderRequest) {
                Log.e(
                    "SELECTED ORDER>>>>>",
                    "bind: ${item.toString()}"
                )
            }

        }*/

        if (isMultiOrderCardSelectEnabled) {
            holder.binding.profilePic.visibility = View.INVISIBLE
            holder.binding.checkBoxOrder.visibility = View.VISIBLE

        } else {
            holder.binding.checkBoxOrder.visibility = View.INVISIBLE
            holder.binding.profilePic.visibility = View.VISIBLE

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


        fun bind(currentDocLine: AllOrderListResponseModel.Data, context: Context) {
            if (isAllSelectedMethodisWorking) {

            } else {

            }


            if (GlobalClasses.cartListForOrderRequest.isNotEmpty()) {

                for ((k, v) in GlobalClasses.cartListForOrderRequest) {
                    if (k.equals(currentDocLine.id)) {
                        binding.checkBoxOrder.visibility = View.VISIBLE
                        binding.profilePic.visibility = View.INVISIBLE
                        binding.checkBoxOrder.isChecked = true
                    } else {
                        binding.checkBoxOrder.visibility = View.INVISIBLE
                        binding.profilePic.visibility = View.VISIBLE
                        binding.checkBoxOrder.isChecked = false
                    }
                }


                /*   if (setupLocalArrayList(currentDocLine)) {
                       binding.checkBoxOrder.visibility = View.VISIBLE
                       binding.profilePic.visibility = View.INVISIBLE
                       try {
                           for (currentInItem in GlobalClasses.cartListForOrderRequest) {
                               binding.checkBoxOrder.isChecked =
                                   currentInItem.orderId == currentDocLine.DocNum
                           }
                       } catch (e: Exception) {
                       }


                   } else {
                       binding.checkBoxOrder.visibility = View.INVISIBLE
                       binding.profilePic.visibility = View.VISIBLE
                   }*/


            }


            binding.checkBoxOrder.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    isAllSelectedMethodisWorking = false
                    var localSelectedOrder = LocalSelectedOrder()
                    localSelectedOrder.apply {
                        orderId = currentDocLine.id
                        orderName = currentDocLine.CardName
                    }

                    /*   GlobalClasses.cartListForOrderRequest.add(
                           localSelectedOrder
                       )*/

                    GlobalClasses.cartListForOrderRequest[currentDocLine.id] =
                        localSelectedOrder

                    val newColorStateList =
                        ColorStateList.valueOf(context.resources.getColor(R.color.blue_light))
                    binding.constraintLayoutWorkQueue.backgroundTintList = newColorStateList


                } else {
                    isAllSelectedMethodisWorking = false
                    var pos = -1

                    val newColorStateList = ColorStateList.valueOf(Color.WHITE)
                    binding.constraintLayoutWorkQueue.backgroundTintList = newColorStateList

                    GlobalClasses.cartListForOrderRequest.remove(currentDocLine.id)

                    /* GlobalClasses.cartListForOrderRequest.forEachIndexed { index, documentLine ->
                         if (currentDocLine.DocNum == documentLine.orderId) {
                             pos = index
                             GlobalClasses.cartListForOrderRequest.removeAt(pos)
                         }

                     }*/

                }


                Log.e(
                    "SELECTED ORDER>>>>>",
                    "bind: ${GlobalClasses.cartListForOrderRequest.size}"
                )

                for (item in GlobalClasses.cartListForOrderRequest) {
                    Log.e(
                        "SELECTED ORDER>>>>>",
                        "bind: ${item.toString()}"
                    )
                }

            }


        }


    }

    /* private fun setupLocalArrayList(
         currentItem: AllOrderListResponseModel.Data,

         ): Boolean {


         return GlobalClasses.cartListForOrderRequest.any { item ->
             item.orderId.equals(currentItem.DocNum, ignoreCase = true)


         }


     }*/
}