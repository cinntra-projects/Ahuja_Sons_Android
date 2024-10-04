package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.OpenPdfView
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.databinding.CameraImageListAdapterBinding
import com.ahuja.sons.globals.Global
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PreviousImageViewAdapter (private val context: Context?, private val list: ArrayList<UploadedPictureModel.Data>, private val stringList: Array<String>, private val pdfurilist: java.util.ArrayList<String>) : RecyclerView.Adapter<PreviousImageViewAdapter.ViewHolder>() {

    private var onItemClickListener: ((Any, Int,Any) -> Unit)? = null

    fun setOnItemClickListener(listener: (Any, Int, Any) -> Unit) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousImageViewAdapter.ViewHolder {
        val binding = CameraImageListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: PreviousImageViewAdapter.ViewHolder, position: Int) {
        with(holder) {
            with(list[position]){
                Log.e("stringList------", stringList.toString())
                try {
                   /* if (list.size > 0) {
                        binding.ivProductPhoto.setImageURI(this.Attachment as Uri)
                    } else {
                        binding.ivProductPhoto.setImageResource(R.drawable.default_image)
                    }*/
                    val options: RequestOptions = RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                    val ImageUrl: String = Global.Image_URL + list.get(position).Attachment

                    Glide.with(context!!).load(ImageUrl).apply(options).into(binding.ivProductPhoto)

                    binding.ivCancelPhoto.visibility = View.GONE

                    binding.ivCancelPhoto.setOnClickListener {

                        onItemClickListener?.let { click ->
                            click(list[position], position, pdfurilist[position])
                        }

                        /* if (position >= 0 && position < list.size) {
                             list.removeAt(position)
                             notifyItemRemoved(position)
                             notifyDataSetChanged()
                         }*/
                    }


                    binding.ivProductPhoto.setOnClickListener {

                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ImageUrl))
                        context!!.startActivity(browserIntent)

                       /* val extension: String = list[position].Attachment.substring(list[position].Attachment.lastIndexOf("."))
                        if (extension == "jpg" || extension == "jpeg" || extension == "png" || extension == "HEIC") {
                            val i = Intent(context, OpenPdfView::class.java)
                            i.putExtra("PDFLink", list[position].Attachment)
                            context?.startActivity(i)
                        } else {
                            val pdf_url = Global.Image_URL + list[position].Attachment
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                            context?.startActivity(browserIntent)
                        }*/
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: CameraImageListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)
}