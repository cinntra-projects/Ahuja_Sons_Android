package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalWorkQueueData
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.databinding.ItemWorkQueueBinding
import com.ahuja.sons.globals.Global
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.pixplicity.easyprefs.library.Prefs
import java.util.*
import kotlin.collections.ArrayList


class WorkQueueAdapter(var AllitemsList: ArrayList<AllWorkQueueResponseModel.Data>, var where: String) : RecyclerView.Adapter<WorkQueueAdapter.OrderViewHolder>() {//(OrderDiffCallback())

    private lateinit var context: Context
    var tempList = ArrayList<AllWorkQueueResponseModel.Data>()

    private var onItemClickListener: ((AllWorkQueueResponseModel.Data, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (AllWorkQueueResponseModel.Data, Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemWorkQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        tempList.clear()
        tempList.addAll(AllitemsList)
        context = parent.context
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (AllitemsList[position].OrderRequest!!.CardName.isNotEmpty()!!) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(AllitemsList[position].OrderRequest!!.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            holder.binding.profilePic.setImageDrawable(drawable)
        }


        if (AllitemsList.get(position).DeliveryNote.size > 0) {
            holder.bind(AllitemsList.get(position).DeliveryNote)
        }

        if (Prefs.getString(Global.Employee_role, "").equals("Inspection") || Prefs.getString(Global.Employee_role, "").equals("Delivery Person")){
            if (AllitemsList[position].is_return == true) {
                holder.binding.deliveriesLayoutView.visibility = View.GONE
                holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.return_icon))
            }else{
                holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.dispatched_icon))
                holder.binding.deliveriesLayoutView.visibility = View.VISIBLE
            }

            if (AllitemsList[position].is_errands == true) {
                holder.binding.deliveriesLayoutView.visibility = View.GONE
                holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.errand_icon))
            }

        }else{
            holder.binding.deliveriesLayoutView.visibility = View.GONE
            if (AllitemsList[position].is_return == true) {
                holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.return_icon))
            }else{
                holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.dispatched_icon))
            }
           /* holder.binding.ivDeliveryCoordinator.setImageDrawable(context.resources.getDrawable(R.drawable.dispatched_icon))
            holder.binding.deliveriesLayoutView.visibility = View.GONE*/
        }


        if (AllitemsList[position].has_dependency == true && AllitemsList[position].is_return == false && AllitemsList[position].is_errands == false){
            holder.binding.constraintLayoutWorkQueue.setBackgroundColor(context.resources.getColor(R.color.card_sky_blue_back))
        }

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

        with(AllitemsList[position]){
            holder.binding.tvOrderId.text = "Order ID : "+OrderRequest?.id
            holder.binding.tvOrderName.text = OrderRequest?.CardName
            if (OrderRequest!!.Doctor.isNotEmpty()){
                holder.binding.tvOrderDoctorName.text = OrderRequest!!.Doctor[0].DoctorFirstName + " "+OrderRequest!!.Doctor[0].DoctorLastName
            }
            holder.binding.tvSurgeryDateTime.text = "Surgery Date:${Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(OrderRequest?.SurgeryDate)}\n Surgery Time: ${OrderRequest?.SurgeryTime}"

            if (is_errands == true){
                if(Prefs.getString(Global.Employee_role, "").equals("Delivery Person")){
                    holder.binding.tvStatusOrder.text = "Assigned"
                }
            }else{
                holder.binding.tvStatusOrder.text = OrderRequest?.Status
            }
//            holder.binding.tvStatusOrder.text = OrderRequest?.Status
            holder.binding.tvOrderInfo.text = OrderRequest?.OrderInformation
            holder.binding.tvLocationName.text = "Ahuja Sons Enterprises"
        }

        if (Prefs.getString(Global.Employee_role, "").equals("Delivery Person")){
            if (AllitemsList[position].is_return_to_office == true) {
                holder.binding.returnToOfficeLayout.visibility = View.VISIBLE
                holder.binding.constraintLayoutWorkQueue.visibility = View.GONE
            }else{
                holder.binding.returnToOfficeLayout.visibility = View.GONE
                holder.binding.constraintLayoutWorkQueue.visibility = View.VISIBLE
            }
        }


        holder.itemView.setOnClickListener {

            if  (!Prefs.getString(Global.Employee_role, "").equals("Inspection")){

                if (Prefs.getString(Global.Employee_role, "").equals("Sales Person")) {
                    val intent = Intent(holder.itemView.context, SalesPersonRoleDetailActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    context.startActivity(intent)
                }

                else if (Prefs.getString(Global.Employee_role, "").equals("Order Coordinator")){
                    val intent = Intent(holder.itemView.context, OrderCoordinatorActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    intent.putExtra("flag", "WorkQueue")
                    context.startActivity(intent)
                }

                else if (Prefs.getString(Global.Employee_role, "").equals("Counter")){
                    val intent = Intent(holder.itemView.context, CounterActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    context.startActivity(intent)
                }

                else if (Prefs.getString(Global.Employee_role, "").equals("Delivery Person")){
                    if (AllitemsList[position].is_return_to_office == true) {
                        val intent = Intent(holder.itemView.context, ReturnAutoTrackingActivity::class.java)
                        intent.putExtra("id", AllitemsList[position].id)
                        context.startActivity(intent)
                    }else{
                        if (AllitemsList[position].is_return == true){
                            Log.e("adapter--", "onBindViewHolder: Return Delivery Person" )
                            val intent = Intent(holder.itemView.context, ReturnDeliveryPersonActivity::class.java)
                            intent.putExtra("id", AllitemsList[position].id)
                            context.startActivity(intent)
                        }
                        else if (AllitemsList[position].is_errands == true){
                            Log.e("adapter--", "onBindViewHolder: Errand Delivery Person" )
                            val intent = Intent(holder.itemView.context, ErrandDeliveryPersonActivity::class.java)
                            intent.putExtra("id", AllitemsList[position].id)
                            context.startActivity(intent)
                        }
                        else{
                            Log.e("adapter--", "onBindViewHolder: Delivery Person" )
                            val intent = Intent(holder.itemView.context, OrderScreenForDeliveryPersonActivity::class.java)
                            intent.putExtra("id", AllitemsList[position].id)
                            context.startActivity(intent)
                        }

                    }

                }

                else if (Prefs.getString(Global.Employee_role, "").equals("Surgery Coordinator")){
                    val intent = Intent(holder.itemView.context, SurgeryCoordinatorActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    intent.putExtra("OrderRequestID", AllitemsList[position].OrderRequest?.id.toString())
                    context.startActivity(intent)
                }

                else if (Prefs.getString(Global.Employee_role, "").equals("Surgery Person")){
                    val intent = Intent(holder.itemView.context, SurgeryPersonActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    context.startActivity(intent)
                }

                else if (Prefs.getString(Global.Employee_role, "").equals("Billing Coordinator")){
                    val intent = Intent(holder.itemView.context, BillingCoordinatorDetailActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    context.startActivity(intent)
                }

                else if (Prefs.getString(Global.Employee_role, "").equals("Operation Manager")){
                    val intent = Intent(holder.itemView.context, OperationManagerDetailActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    context.startActivity(intent)
                }


                /*else if (Prefs.getString(Global.Employee_role, "").equals("Inspection")){

                    val intent = Intent(holder.itemView.context, InspectDeliveryOrderDetailActivity::class.java)
                    intent.putExtra("id", AllitemsList[position].id)
                    context.startActivity(intent)
                }*/


            }else{
                if (AllitemsList[position].is_return == true){
                    if (Prefs.getString(Global.Employee_role, "").equals("Inspection")){
                        val intent = Intent(holder.itemView.context, InspectDeliveryOrderDetailActivity::class.java)
                        intent.putExtra("deliveryID", AllitemsList[position].DeliveryId)
                        intent.putExtra("DeliveryStatus", AllitemsList[position].DeliveryStatus)
                        intent.putExtra("inspectionDeliveryPos", position)
                        intent.putExtra("OrderID", AllitemsList[position].OrderRequest!!.id.toString())
                        intent.putExtra("is_return", AllitemsList[position].is_return)
                        intent.putExtra("flagForItemViewList", "FromOrderSelect")
                        context.startActivity(intent)
                    }
                }
                else if (AllitemsList[position].is_return == false){
                    if (Prefs.getString(Global.Employee_role, "").equals("Inspection")){
                        val intent = Intent(holder.itemView.context, InspectDeliveryOrderDetailActivity::class.java)
                        intent.putExtra("deliveryID", AllitemsList[position].DeliveryId)
                        intent.putExtra("DeliveryStatus", AllitemsList[position].DeliveryStatus)
                        intent.putExtra("inspectionDeliveryPos", position)
                        intent.putExtra("OrderID", AllitemsList[position].OrderRequest!!.id.toString())
                        intent.putExtra("is_return", AllitemsList[position].is_return)
                        intent.putExtra("flagForItemViewList", "FromOrderSelect")
                        context.startActivity(intent)
                    }
                }
            }

        }


    }


    inner class OrderViewHolder(var binding: ItemWorkQueueBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : ArrayList<AllWorkQueueResponseModel.DeliveryNote>) {

            Log.d("ParentAdapter", "Binding child adapter with ${item.size} items")

            val innerAdapter = InspectionDeliveryIDAdapter(item)
            binding.deliveryIdRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            binding.deliveryIdRecyclerView.adapter = innerAdapter

            innerAdapter.notifyDataSetChanged()

        }

    }



    class OrderDiffCallback : DiffUtil.ItemCallback<LocalWorkQueueData>() {
        override fun areItemsTheSame(
            oldItem: LocalWorkQueueData,
            newItem: LocalWorkQueueData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LocalWorkQueueData,
            newItem: LocalWorkQueueData
        ): Boolean {
            return oldItem == newItem
        }
    }


}
