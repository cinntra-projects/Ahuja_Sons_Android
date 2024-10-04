package com.ahuja.sons.ahujaSonsClasses.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.activity.AllOrdersCoordinatorDetailActivity
import com.ahuja.sons.ahujaSonsClasses.activity.ParticularOrderDetailActivity
import com.ahuja.sons.ahujaSonsClasses.model.RouteListModel
import com.ahuja.sons.databinding.ItemWorkQueueBinding
import com.ahuja.sons.globals.Global
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.pixplicity.easyprefs.library.Prefs
import java.util.*
import kotlin.collections.ArrayList

class RouteOrderListAdapter(var AllitemsList: ArrayList<RouteListModel.Data.OrderIDs>,var deliveryID: ArrayList<RouteListModel.Data.DeliveryIDs>) : RecyclerView.Adapter<RouteOrderListAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList = ArrayList<RouteListModel.Data.OrderIDs>()

    private var onItemClickListener: ((RouteListModel.Data.OrderIDs, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (RouteListModel.Data.OrderIDs, Int) -> Unit) {
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

        //todo dropdown arrows for delivery id's--
        holder.binding.deliveryIDUpArrow.setOnClickListener {
            holder.binding.deliveryIdRecyclerView.visibility = View.GONE
            holder.binding.deliveryIDUpArrow.visibility = View.GONE
            holder.binding.deliveryIDDownArrow.visibility = View.VISIBLE
        }

        holder.binding.deliveryIDDownArrow.setOnClickListener {
            holder.binding.deliveryIdRecyclerView.visibility = View.VISIBLE
            holder.binding.deliveryIDDownArrow.visibility = View.GONE
            holder.binding.deliveryIDUpArrow.visibility = View.VISIBLE
        }

        AllitemsList?.let {
            // Proceed with AllitemsList

            with(AllitemsList[position]){

                val innerAdapter = RouteDeliveryIDAdapter(deliveryID)
                holder.binding.deliveryIdRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                holder.binding.deliveryIdRecyclerView.adapter = innerAdapter
                innerAdapter.notifyDataSetChanged()

                holder.binding.tvOrderId.text = "Order ID: " + id

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

        } ?: run {
            // Handle the case where AllitemsList is null
        }



        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor


    /*    holder.itemView.setOnClickListener {
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

        }*/


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