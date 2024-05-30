package com.ahuja.sons.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.ProductDetailActivity
import com.ahuja.sons.newapimodel.ProductResponseModel


class CustomerEquipmentAdapter(val AllitemsList: ArrayList<ProductResponseModel.DataXXX>) :
    RecyclerView.Adapter<CustomerEquipmentAdapter.Category_Holder>() {

    private lateinit var context: Context


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
        holder.sr_no.text = AllitemsList[position].SerialNo
        holder.message.text = AllitemsList[position].ContractorName
        holder.tvOrderName.visibility = View.GONE

        holder.itemView.setOnClickListener {
           /* val intent = Intent(context, ParticularItemDetailsActivity::class.java)
            intent.putExtra("ProductSerialNo", AllitemsList[position].SerialNo)
            context.startActivity(intent)*/

            var intent : Intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", AllitemsList[position].id)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val message = itemView.findViewById<TextView>(R.id.message)
        val tvOrderName = itemView.findViewById<TextView>(R.id.tvOrderName)


    }


}
