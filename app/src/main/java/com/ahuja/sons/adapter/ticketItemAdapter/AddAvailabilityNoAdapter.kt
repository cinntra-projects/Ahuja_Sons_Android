package com.ahuja.sons.adapter.ticketItemAdapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.databinding.AvailabilityNoAdapterLayoutBinding

class AddAvailabilityNoAdapter(private val context: Context, private val attachList: MutableList<AvailabilityCustomModel.Availability>) : RecyclerView.Adapter<AddAvailabilityNoAdapter.ViewHolder>() {

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

    private var onItemDistanceClickListener: ((String, Int) -> Unit)? = null
    fun setonItemDistanceClickListener(listener: (String, Int) -> Unit) {
        onItemDistanceClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AvailabilityNoAdapterLayoutBinding =
            AvailabilityNoAdapterLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddAvailabilityNoAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = attachList[holder.absoluteAdapterPosition]

        holder.binding.apply {
            holder.binding.edtItemName.setText(item.ItemName)
            holder.binding.edtQty.setText(item.ItemQty)
            holder.binding.edtDistance.setText(item.ItemDistance)

            //todo item change listener for name---
            holder.binding.edtItemName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.e("TAG", "onTextChanged: ")
                    onItemTextContentClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.ItemName = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                    Log.e("TAG", "afterTextChanged: ")
                }

            })

            //todo item change listener for QTY---
            holder.binding.edtQty.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemContentQuantitiyClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.ItemQty = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })

            //todo item change listener for Distance---
            holder.binding.edtDistance.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemDistanceClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.ItemDistance = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })


            ivCross.setOnClickListener {
                onItemMinusClickListener?.let { click ->
                    click("", holder.absoluteAdapterPosition)
                }
                edtItemName.setText("")
                edtQty.setText("")
                edtDistance.setText("")
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

    class ViewHolder(val binding: AvailabilityNoAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun addItem(item: AvailabilityCustomModel.Availability) {
        attachList.add(item)
        notifyItemInserted(attachList.size - 1)
        flag = 1
    }

    fun removeItem(position: Int) {
        attachList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAttachList(): List<AvailabilityCustomModel.Availability> {
        return attachList.toList()
    }


}