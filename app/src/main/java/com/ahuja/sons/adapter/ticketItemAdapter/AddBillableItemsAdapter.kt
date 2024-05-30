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
import com.ahuja.sons.apibody.BodySparePart
import com.ahuja.sons.databinding.SparePartsLayoutBinding
import com.ahuja.sons.newapimodel.SpareItemListApiModel
import com.ahuja.sons.spinneradapter.SparePartItemSearchableSpinnerAdapter

class AddBillableItemsAdapter (private val context: Context, private val attachList: MutableList<BodySparePart.SparePart>, private val customerList_gl: ArrayList<SpareItemListApiModel.DataXXX>) : RecyclerView.Adapter<AddBillableItemsAdapter.ViewHolder>() { // private val customerList_gl: ArrayList<SpareItemListApiModel.DataXXX>

    var flag = 0
    var customerFilterName = ""
    var customerFilterCode = ""

    var tempList: ArrayList<SpareItemListApiModel.DataXXX> = ArrayList()

    init {
        this.tempList.addAll(customerList_gl)
    }

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
        return AddBillableItemsAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddBillableItemsAdapter.ViewHolder, position: Int) {

        var item = attachList[holder.absoluteAdapterPosition]

        holder.binding.apply {

            //     acItemName.setText(item.SparePartName)
            edtQty.setText(item.PartQty)
            edtSerialNo.setText(item.SpareSerialNo)
            edtItemPrice.setText(item.SparePartPrice)

            var leadTypeAdapter = SparePartItemSearchableSpinnerAdapter(holder.itemView.context, customerList_gl)
            acItemName.adapter = leadTypeAdapter

            acItemName.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

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


            edtQty.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onItemContentQuantitiyClickListener?.let { click ->
                        click(p0.toString(), position)
                    }
                    item.PartQty = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })


            edtSerialNo.addTextChangedListener(object : TextWatcher {
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