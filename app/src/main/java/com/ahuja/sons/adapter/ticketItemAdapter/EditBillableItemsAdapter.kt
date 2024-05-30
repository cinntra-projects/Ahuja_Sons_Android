package com.ahuja.sons.adapter.ticketItemAdapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.databinding.SparePartsLayoutBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.SpareItemListApiModel
import com.ahuja.sons.spinneradapter.SparePartItemSearchableSpinnerAdapter

class EditBillableItemsAdapter (
    private val context: Context,
    private val attachList: MutableList<SparePart>,
    private val customerList_gl: ArrayList<SpareItemListApiModel.DataXXX>
) : RecyclerView.Adapter<EditBillableItemsAdapter.ViewHolder>() {

    var flag = 0
    var customerFilterName = ""

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
        return EditBillableItemsAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditBillableItemsAdapter.ViewHolder, position: Int) {

        var item = attachList[holder.absoluteAdapterPosition]

        holder.binding.apply {
            //   holder.binding.acItemName.setText(item.SparePartName)
            holder.binding.edtQty.setText(item.PartQty)
            holder.binding.edtSerialNo.setText(item.SpareSerialNo)
            edtItemPrice.setText(item.SparePartPrice)


            var leadTypeAdapter = SparePartItemSearchableSpinnerAdapter(holder.itemView.context, customerList_gl)
            acItemName.adapter = leadTypeAdapter
            acItemName.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {


                    val selectedItem: SpareItemListApiModel.DataXXX = customerList_gl[position]

                    var branchName = selectedItem.ItemDescription

                    onItemTextContentClickListener?.let { click ->
                        click(customerFilterName, position)
                    }
                    item.SparePartName = selectedItem.ItemDescription
                    item.SparePartId = selectedItem.ItemNo
                    item.SpareSerialNo = selectedItem.SerialNo
                    item.SparePartPrice = selectedItem.SparePartPrice

                    edtSerialNo.setText(selectedItem.SerialNo)
                    edtItemPrice.setText(selectedItem.SparePartPrice)

                    Log.e("SEARCHABLE SPINNER", "onItemSelected: $branchName--$position")

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    // Handle when nothing is selected (optional)
//                    val selectedItem: DataBranchAll = dataList[0]
//                    branchName = selectedItem.AddressName
//
//                    Prefs.putString(Global.SpinnerAddressType, selectedItem.AddressType)
//                    Prefs.putString(Global.SpinnerBranchId, selectedItem.id.toString())
//                    listenerCustomer!!.onDataPassedCustomer(
//                        selectedItem.id.toString(),
//                        selectedItem.AddressType
//                    )
                }
            }

            acItemName.setSelection(
                Global.getItemSparePartyPos(
                    customerList_gl,
                    item.SparePartName
                )
            )


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

            holder.binding.edtSerialNo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemContentQuantitiyClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.SpareSerialNo = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })


            ivCross.setOnClickListener {
                onItemMinusClickListener?.let { click ->
                    click("", holder.absoluteAdapterPosition)
                }
                //  acItemName.setText("")
                edtQty.setText("")
                edtSerialNo.setText("")
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

    fun addItem(item: SparePart) {
        attachList.add(item)
        notifyItemInserted(attachList.size - 1)
        flag = 1
    }

    fun removeItem(position: Int) {
        attachList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAttachList(): List<SparePart> {
        return attachList.toList()
    }


}