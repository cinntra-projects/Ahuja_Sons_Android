package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.activity.*
import com.ahuja.sons.ahujaSonsClasses.ahujaconstant.RoleClass
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderListModel
import com.ahuja.sons.databinding.ItemRouteDeliveryCoordinatorBinding
import java.util.ArrayList


class RouteItemListAdapter : ListAdapter<LocalRouteData, RouteItemListAdapter.OrderViewHolder>(OrderDiffCallback()) {
    lateinit var orderAdapter: OrderListAdapter

    private var onItemClickListener: ((AllOrderListModel.Data, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (AllOrderListModel.Data, Int) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemRouteDeliveryCoordinatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
     }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            /*val intent = Intent(holder.itemView.context, DeliveryCoordinatorActivity::class.java)
            holder.itemView.context.startActivity(intent)*/
        }

    }

    inner class OrderViewHolder(private val binding: ItemRouteDeliveryCoordinatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: LocalRouteData) {

            binding.ivMore.setOnClickListener {
                showPopupMenu(binding.ivMore, itemView.context)
            }

            binding.tvDeliveryPerson.text = order.orderName

            orderAdapter = OrderListAdapter(order.orderList as ArrayList<AllOrderListModel.Data>,RoleClass.deliveryPerson)
            binding.rvOrderInRoute.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = orderAdapter
            }

            binding.ivArrow.setOnClickListener {
                if (binding.rvOrderInRoute.visibility == View.VISIBLE) {
                    binding.rvOrderInRoute.visibility = View.GONE
                    binding.ivArrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                } else {
                    binding.rvOrderInRoute.visibility = View.VISIBLE
                    binding.ivArrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                }

            }

        }


    }


    private fun showPopupMenu(anchorView: View, context: Context, ) {
        val popupMenu = PopupMenu(context, anchorView)
        popupMenu.menuInflater.inflate(R.menu.edit_assign, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.edit -> {
                    onItemClickListener?.let { click ->
//                        click(order, adapterPosition)
                    }
                    true
                }

                else -> false
            }
        }

        popupMenu.show()

    }



    class OrderDiffCallback : DiffUtil.ItemCallback<LocalRouteData>() {
        override fun areItemsTheSame(
            oldItem: LocalRouteData,
            newItem: LocalRouteData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LocalRouteData,
            newItem: LocalRouteData
        ): Boolean {
            return oldItem == newItem
        }
    }


}
