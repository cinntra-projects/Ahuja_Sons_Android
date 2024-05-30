package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.Interface.OrderStageItemClick
import com.ahuja.sons.databinding.OrderStageLinesBinding
import java.util.*

class OrderStageLineAdapter (val AllitemsList: ArrayList<String>, val orderStageItemClick: OrderStageItemClick): RecyclerView.Adapter<OrderStageLineAdapter.Category_Holder>() {

    private lateinit var context: Context

    var tempList : ArrayList<String> = ArrayList()
    init {
        this.tempList.addAll(AllitemsList)
    }

//    var refreshFragment : FragmentRefresher()
/*

    var onItemClickListener: ((String, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (String, Int) -> Unit) {
        onItemClickListener = listener
    }
*/

    var orderStageItem : OrderStageItemClick? = null


    init {
        this.orderStageItem = orderStageItemClick
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(OrderStageLinesBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        val obj = AllitemsList[position]
        holder.binding.apply {

            holder.binding.stageName.text = obj

            val parts = obj.split(" ")

         /*   if (parts.size >= 2) {
                holder.binding.stageName.text = parts[0]

                val restOfString = parts.subList(1, parts.size).joinToString(" ")
                if (restOfString.equals("")){
                    holder.binding.date.visibility = View.GONE
                }else{
                    holder.binding.date.text = restOfString
                }

            }else{
                holder.binding.stageName.text = obj
            }*/

            if (position == AllitemsList.size - 1) {
                holder.binding.divider.setVisibility(View.GONE)
            }

        /*    if (obj.status === 1) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_blue))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            } else if (obj.status === 0) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_grey))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
            } else if (obj.status === 2) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_green))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            }*/

           /* if (position === 1) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_blue))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            } else*/

            if (position === 2) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_grey))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
            } else if (position === 0) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_green))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            }
            else if (position === 1) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_green))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            }

        }

        holder.itemView.setOnClickListener {

            selectedPosition = position

            orderStageItem?.stagesOnClick(position, obj)
            Log.e("string==> ", "onBindViewHolder: "+obj )
            Toast.makeText(context, obj, Toast.LENGTH_SHORT).show()

            if (selectedPosition == position) {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_blue))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            else {
                holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_grey))
                holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
            }


           /* holder.binding.tickGreen.setBackground(context.resources.getDrawable(R.drawable.tick_square_blue))


            holder.binding.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))*/

            /*onItemClickListener?.let { click ->
                click(obj, position)
            }*/


        }



    }

    // Variable to hold the selected item position
    private var selectedPosition = RecyclerView.NO_POSITION


    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: OrderStageLinesBinding) : RecyclerView.ViewHolder(binding.root)



}