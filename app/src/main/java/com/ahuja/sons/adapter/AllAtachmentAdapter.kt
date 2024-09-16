package com.ahuja.sons.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.OpenPdfView
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.DataItem
import java.util.*


class AllAtachmentAdapter(val AllitemsList: ArrayList<DataItem>) :
    RecyclerView.Adapter<AllAtachmentAdapter.Category_Holder>() {

    private lateinit var context: Context
    private var OnAttachmentDeleteClick: ((DataItem) -> Unit)? = null

    fun setOnAttachmentDelete(listener: (DataItem) -> Unit) {
        OnAttachmentDeleteClick = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.attchment,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

//        val f = File(AllitemsList[position].file)

        holder.name.text = AllitemsList[position].File


        holder.itemView.setOnClickListener {
            val extension: String = AllitemsList[position].File.substring(AllitemsList[position].File.lastIndexOf("."))
            if (extension == "jpg" || extension == "jpeg" || extension == "png" || extension == "HEIC") {
                val i = Intent(context, OpenPdfView::class.java)
                i.putExtra("PDFLink", AllitemsList[position].File)
                context.startActivity(i)
            } else {
                val pdf_url = Global.Image_URL + AllitemsList[position].File
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                context.startActivity(browserIntent)
            }
        }

        holder.deleteImage.setOnClickListener {
            OnAttachmentDeleteClick?.let { click ->
                click(AllitemsList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.name)
        val deleteImage: ImageView = itemView.findViewById(R.id.deleteImage)


    }


}
