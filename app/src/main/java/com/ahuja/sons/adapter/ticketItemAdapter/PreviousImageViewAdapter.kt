package com.ahuja.sons.adapter.ticketItemAdapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.File

class PreviousImageViewAdapter(private val context: Context, private val uriList: List<File>) :
    RecyclerView.Adapter<PreviousImageViewAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.camera_image_list_adapter, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_bridge_service_icon_round)
            .error(R.mipmap.ic_launcher_bridge_service_icon_round)

        val imageUrl = Global.Image_URL + uriList[position].File

        Glide.with(context).load(imageUrl).apply(options).into(holder.loadImage)

        holder.loadImage.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl))
            context.startActivity(browserIntent)
        }

        holder.cross.visibility = View.GONE

    }

    override fun getItemCount(): Int = uriList.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadImage: ImageView = itemView.findViewById(R.id.iv_product_photo)
        val cross: ImageView = itemView.findViewById(R.id.iv_cancel_photo)
    }
}