package com.ahuja.sons.adapter.ticketItemAdapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.databinding.AddAreaItemAdapterLayoutBinding
import com.ahuja.sons.newapimodel.SiteSurveyTicketResponse

class EditAreaAdapter(private val context: Context, private val attachList: MutableList<SiteSurveyTicketResponse.Area>) : RecyclerView.Adapter<EditAreaAdapter.ViewHolder>() {

    var flag = 0

    private var onItemMinusClickListener: ((String, Int) -> Unit)? = null
    fun setOnItemMinusClickListener(listener: (String, Int) -> Unit) {
        onItemMinusClickListener = listener
    }


    private var onItemLocationContentClickListener: ((String, Int) -> Unit)? = null
    fun setOnLocationContentClickListener(listener: (String, Int) -> Unit) {
        onItemLocationContentClickListener = listener
    }


    private var onItemLengthContentClickListener: ((String, Int) -> Unit)? = null
    fun setonItemLengthContentClickListener(listener: (String, Int) -> Unit) {
        onItemLengthContentClickListener = listener
    }

    private var onItemWidthClickListener: ((String, Int) -> Unit)? = null
    fun setonItemWidthClickListener(listener: (String, Int) -> Unit) {
        onItemWidthClickListener = listener
    }

    private var onItemHeightClickListener: ((String, Int) -> Unit)? = null
    fun setonItemHeightClickListener(listener: (String, Int) -> Unit) {
        onItemHeightClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AddAreaItemAdapterLayoutBinding = AddAreaItemAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditAreaAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = attachList[holder.absoluteAdapterPosition]

        holder.binding.apply {
            holder.binding.edtLoation.setText(item.Location)
            holder.binding.edtLength.setText(item.Length)
            holder.binding.edtWidth.setText(item.Width)
            holder.binding.edtHeight.setText(item.Height)

            //todo item change listener for Location---
            holder.binding.edtLoation.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    Log.e("TAG", "onTextChanged: ")
                    onItemLocationContentClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.Location = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                    Log.e("TAG", "afterTextChanged: ")
                }

            })

            //todo item change listener for Length---
            holder.binding.edtLength.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemLengthContentClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.Length = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })

            //todo item change listener for Width---
            holder.binding.edtWidth.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemWidthClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.Width = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })


            //todo item change listener for height---
            holder.binding.edtHeight.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemHeightClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.Height = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })


            ivCross.setOnClickListener {
                onItemMinusClickListener?.let { click ->
                    click("", holder.absoluteAdapterPosition)
                }
                edtLoation.setText("")
                edtLength.setText("")
                edtWidth.setText("")
                edtHeight.setText("")
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

    class ViewHolder(val binding: AddAreaItemAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun addItem(item: SiteSurveyTicketResponse.Area) {
        attachList.add(item)
        notifyItemInserted(attachList.size - 1)
        flag = 1
    }

    fun removeItem(position: Int) {
        attachList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAttachList(): List<SiteSurveyTicketResponse.Area> {
        return attachList.toList()
    }


}