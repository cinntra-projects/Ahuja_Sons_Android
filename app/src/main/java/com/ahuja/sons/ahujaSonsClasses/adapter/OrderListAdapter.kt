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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.ChatActivity
import com.ahuja.sons.ahujaSonsClasses.activity.AllOrdersCoordinatorDetailActivity
import com.ahuja.sons.ahujaSonsClasses.activity.OrderCoordinatorActivity
import com.ahuja.sons.ahujaSonsClasses.activity.ParticularOrderDetailActivity
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.RouteListModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderListModel
import com.ahuja.sons.databinding.ItemWorkQueueBinding
import com.ahuja.sons.globals.Global
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.lid.lib.LabelTextView
import com.pixplicity.easyprefs.library.Prefs
import java.util.*

class OrderListAdapter(var AllitemsList: ArrayList<AllOrderListModel.Data>, var where: String) : RecyclerView.Adapter<OrderListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList = ArrayList<AllOrderListModel.Data>()

    private var onItemClickListener: ((AllOrderListModel.Data, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (AllOrderListModel.Data, Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_work_queue, parent, false)
        val binding = ItemWorkQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        tempList.clear()
        tempList.addAll(AllitemsList)
        context = parent.context
        return Category_Holder(binding)

    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        val model = AllitemsList[position]

       /* if (where.equals(RoleClass.deliveryPerson, ignoreCase = true)) {
            holder.binding.chipOrderType.visibility = View.VISIBLE
        } else {
            holder.binding.chipOrderType.visibility = View.GONE
        }*///todo comment Order Type visibility

        with(AllitemsList[position]){

         /*   val innerAdapter = InspectionDeliveryIDAdapter(item)
            holder.binding.deliveryIdRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            holder.binding.deliveryIdRecyclerView.adapter = innerAdapter
            innerAdapter.notifyDataSetChanged()*/

            holder.binding.deliveriesLayoutView.visibility = View.GONE

            holder.binding.tvOrderId.text = "Order ID: " + id
            holder.binding.tvOrderInfo.text = OrderInformation

            if (!CardName.isEmpty()) {
                holder.binding.tvOrderName.text = "" + CardName
            } else {
                holder.binding.tvOrderName.text = "" + "NA"
            }

            if (!SurgeryDate.isNullOrEmpty()){
                holder.binding.tvSurgeryDateTime.text = "Surgery Date:${Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(SurgeryDate)}\n Surgery Time: ${SurgeryTime}"
            }

            if (Doctor.isNotEmpty()){
                holder.binding.tvOrderDoctorName.text = Doctor[0].DoctorFirstName + " "+Doctor[0].DoctorLastName
            }

            holder.binding.tvStatusOrder.text = Status


        }


        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor


        holder.itemView.setOnClickListener {
            if (Prefs.getString(Global.Employee_role, "").equals("Order Coordinator")){
                val intent = Intent(holder.itemView.context, AllOrdersCoordinatorDetailActivity::class.java)
                intent.putExtra("id",  model.id.toString())
                intent.putExtra("flag", "AllOrders")
                context.startActivity(intent)
            }
            else{
                val intent = Intent(context, ParticularOrderDetailActivity::class.java)
                intent.putExtra("id", model.id.toString())
                intent.putExtra("flag", "AllOrders")
                context.startActivity(intent)

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

    inner class Category_Holder(var binding: ItemWorkQueueBinding) : RecyclerView.ViewHolder(binding.root)


}