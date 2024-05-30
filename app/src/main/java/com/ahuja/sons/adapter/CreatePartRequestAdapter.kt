package com.ahuja.sons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.DocumentLine
import java.util.ArrayList


class CreatePartRequestAdapter(val totalitem: TextView, val itemdata: ArrayList<DocumentLine?>) : RecyclerView.Adapter<CreatePartRequestAdapter.Category_Holder>()   {

    private  lateinit var context: Context





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.create_part_request,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {


        holder.price.text ="Rs. "+ itemdata[position]!!.UnitPrice.toString()
        holder.sr_no.text = itemdata[position]!!.ItemName
        holder.itemcode.text = "Itemcode : "+itemdata[position]!!.ItemCode
        holder.stock.text = "StockQuantity : "+itemdata[position]!!.InStock
        holder.count.text = itemdata[position]!!.Quantity.toString()


        holder.add_botton.setOnClickListener(View.OnClickListener {
            try {


                    itemdata[position]!!.Quantity++
                    holder.count.text = itemdata[position]!!.Quantity.toString()
                    if (itemdata[position]?.let { it1 -> addtolist(it1) } == true) {

                        Global.cartList.add(itemdata[position])

                    }
               /* if (itemdata[position]!!.itemquantity < itemdata[position]!!.InStock.toInt())
                {
                } else {
                    Toast.makeText(context,"Out of Stock", Toast.LENGTH_LONG).show()
                }*/



            }catch ( e:NumberFormatException ){

            }
            notifyDataSetChanged()
        })





        holder.remove.setOnClickListener(View.OnClickListener {

            if(itemdata[position]!!.Quantity>0 ){
                itemdata[position]!!.Quantity--
                holder.count.text = itemdata[position]!!.Quantity.toString()

                itemdata[position]?.let { it1 -> addtolist(it1) }
                if(itemdata[position]!!.Quantity==0){
                    Global.cartList.remove(itemdata[position])
                }
            }

            notifyDataSetChanged()
        })


        totalitem.text = "Selected Items : " + Global.cartList.size.toString()
    }



    private fun addtolist(listdata: DocumentLine): Boolean {
        for (im: DocumentLine? in Global.cartList){
            if(im!!.id == listdata.id){
                Global.cartList[Global.cartList.indexOf(im)] = listdata
                return false
            }

        }
        return true
    }



    override fun getItemCount(): Int {
        return itemdata.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val remove = itemView.findViewById<ImageView>(R.id.remove)
        val add_botton = itemView.findViewById<ImageView>(R.id.add_botton)
        val count = itemView.findViewById<TextView>(R.id.count)
        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
        val stock = itemView.findViewById<TextView>(R.id.stock)
        val price = itemView.findViewById<TextView>(R.id.price)
        val itemcode = itemView.findViewById<TextView>(R.id.itemcode)






    }





}
