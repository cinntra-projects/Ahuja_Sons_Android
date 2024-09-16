package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.CameraImageListAdapterBinding

class UploadImageListAdapter(
    private val context: Context?,
    private val list: ArrayList<*>,
    private val stringList: Array<String>,
    private val pdfurilist: java.util.ArrayList<String>
) : RecyclerView.Adapter<UploadImageListAdapter.ViewHolder>() {

    private var onItemClickListener: ((Any, Int,Any) -> Unit)? = null

    fun setOnItemClickListener(listener: (Any, Int, Any) -> Unit) {
        onItemClickListener = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImageListAdapter.ViewHolder {
        val binding = CameraImageListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: UploadImageListAdapter.ViewHolder, position: Int) {
        with(holder) {
            with(list[position]){
                Log.e("stringList------", stringList.toString())
                try {
                    if (list.size > 0) {
                        binding.ivProductPhoto.setImageURI(this as Uri)

                    } else {
                        binding.ivProductPhoto.setImageResource(R.drawable.default_image)
                    }
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