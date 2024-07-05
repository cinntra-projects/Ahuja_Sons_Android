package com.ahuja.sons.ahujaSonsClasses.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonModelData
import com.ahuja.sons.databinding.AddSurgeryPersonLayoutBinding
import com.ahuja.sons.databinding.ItemSurgeryCoordinatorPersonListBinding
import com.ahuja.sons.newapimodel.SpareItemListApiModel

class SurgeryPersonListingAdapter(
    private val context: Context,
    private val attachList: MutableList<SurgeryPersonModelData>
) : RecyclerView.Adapter<SurgeryPersonListingAdapter.ViewHolder>() { // private val customerList_gl: ArrayList<SpareItemListApiModel.DataXXX>

    var flag = 0
    var customerFilterName = ""
    var customerFilterCode = ""

    var tempList: ArrayList<SpareItemListApiModel.DataXXX> = ArrayList()


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
        val binding: ItemSurgeryCoordinatorPersonListBinding = ItemSurgeryCoordinatorPersonListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SurgeryPersonListingAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = attachList[holder.absoluteAdapterPosition]

        holder.binding.apply {
          //  tvSurgeryPersonCount.text = "${position + 2}"
            //     acItemName.setText(item.SparePartName)
            tvDoctor.setText(item.str)

           tvSurgeryPersonCounter.setText("Surgery Person ${position+3}")

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

    class ViewHolder(val binding: ItemSurgeryCoordinatorPersonListBinding) :
        RecyclerView.ViewHolder(binding.root)


    fun addItem(item: SurgeryPersonModelData) {
        attachList.add(item)
        notifyItemInserted(attachList.size - 1)
        flag = 1

    }

    fun removeItem(position: Int) {
        attachList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAttachList(): List<SurgeryPersonModelData> {
        return attachList.toList()
    }


}