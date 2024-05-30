package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.ParticularItemDetailsActivity
import com.ahuja.sons.fragment.ParticularTicketDetailsFragement
import com.ahuja.sons.newapimodel.TicketData


class OrderEquipmentAdapter(val AllitemsList: ArrayList<TicketData>) : RecyclerView.Adapter<OrderEquipmentAdapter.Category_Holder>()   {

    private  lateinit var context: Context






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.customer_order_view,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
    holder.sr_no.text = AllitemsList[position].Type
    holder.message.text = AllitemsList[position].Title
        holder.itemView.setOnClickListener {
           /* val intent = Intent(context, TicketDetailsActivity::class.java)
            intent.putExtra("TicketData",AllitemsList[position])
            context.startActivity(intent)*/
            val activity = it.context as ParticularItemDetailsActivity
            activity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.add(R.id.main_container, ParticularTicketDetailsFragement(AllitemsList[position].id)).addToBackStack(null)
            transaction.commit()
        }

    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val message = itemView.findViewById<TextView>(R.id.message)



    }





}
