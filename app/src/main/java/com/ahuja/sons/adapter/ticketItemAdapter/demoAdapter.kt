package com.ahuja.sons.adapter.ticketItemAdapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.apibody.BodySparePart
import com.ahuja.sons.databinding.SparePartsLayoutBinding

class demoAdapter (private val context: Context, private val attachList: MutableList<BodySparePart.SparePart>) : RecyclerView.Adapter<demoAdapter.ViewHolder>() {

    var flag = 0

    private var onItemMinusClickListener: ((String, Int) -> Unit)? = null
    fun setOnItemMinusClickListener(listener: (String, Int) -> Unit) {
        onItemMinusClickListener = listener
    }


    private var onItemTextContentClickListener: ((String, Int) -> Unit)? = null
    fun setOnTextContentClickListener(listener: (String, Int) -> Unit) {
        onItemTextContentClickListener = listener
    }


    private var onItemContentQuantitiyClickListener: ((String, Int) -> Unit)? = null
    fun setonItemContentQuantitiyClickListener(listener: (String, Int) -> Unit) {
        onItemContentQuantitiyClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SparePartsLayoutBinding = SparePartsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return demoAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = attachList[holder.absoluteAdapterPosition]

        holder.binding.apply {
          //  holder.binding.acItemName.setText(item.SparePartName)
            holder.binding.edtQty.setText(item.PartQty)

//            holder.binding.acItemName.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                    Log.e("TAG", "onTextChanged: ")
//                    onItemTextContentClickListener?.let { click ->
//                        click(p0.toString(), position)
//                    }
//                    item.SparePartName = p0.toString()
//                }
//
//                override fun afterTextChanged(p0: Editable?) {
//                    Log.e("TAG", "afterTextChanged: ")
//                }
//
//            })

            holder.binding.edtQty.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemContentQuantitiyClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.PartQty = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })


            ivCross.setOnClickListener {
                onItemMinusClickListener?.let { click ->
                    click("", holder.absoluteAdapterPosition)
                }
               // acItemName.setText("")
                edtQty.setText("")
            }

        }
    }

    fun splitFun(file: String): String {
        val parts = file.split("/")
        val lastWord = parts.last()
        return lastWord
    }

    override fun getItemCount(): Int {
        return attachList.size
    }

    class ViewHolder(val binding: SparePartsLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    fun addItem(item: BodySparePart.SparePart) {
        attachList.add(item)
        notifyItemInserted(attachList.size - 1)
        flag = 1
    }

    fun removeItem(position: Int) {
        attachList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAttachList(): List<BodySparePart.SparePart> {
        return attachList.toList()
    }


}