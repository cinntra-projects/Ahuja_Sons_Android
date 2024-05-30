package com.ahuja.sons.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.AllPartRequestItemList
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.PRAttachment
import java.util.*

class AllPartReqAttachmentAdapter(
    val cont: AllPartRequestItemList,
    val AllitemsList: ArrayList<PRAttachment>
): RecyclerView.Adapter<AllPartReqAttachmentAdapter.ViewHolder>() {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.attchment, parent, false)

        return ViewHolder(rootView)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj: PRAttachment =AllitemsList[position]

        holder.name.text = obj.Attachment

        holder.name.setOnClickListener {
            val pdf_url = Global.Image_URL + obj.Attachment
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
            cont.startActivity(browserIntent)
        }
//        holder.stock.text = context!!.getString(R.string.instock) + " : " + obj.getInStock()
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }







   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView


        init {

            name=itemView.findViewById(R.id.name)
        }


    }

}