package com.ahuja.sons.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.databinding.ItemDialogChecklistDataBinding
import com.ahuja.sons.newapimodel.DataFromJsonCheckList


class CheckListDialogItemAdapter(val AllitemsList: ArrayList<DataFromJsonCheckList>) :
    RecyclerView.Adapter<CheckListDialogItemAdapter.Category_Holder>() {

    var selectedItem = ""
    private lateinit var context: Context


    private var onItemClickListener: ((DataFromJsonCheckList) -> Unit)? = null
    fun setOnItemClickListener(listener: (DataFromJsonCheckList) -> Unit) {
        onItemClickListener = listener
    }


    private var onYesNoSpinnerClickListener: ((String, String, String, Int) -> Unit)? = null
    fun setOnYesNoSpinnerClickListener(listener: (String, String, String, Int) -> Unit) {
        onYesNoSpinnerClickListener = listener
    }

    private var onEditTextClickListener: ((String, String, Int) -> Unit)? = null
    fun setOnEditTextClickListener(listener: (String, String, Int) -> Unit) {
        onEditTextClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        context = parent.context
        return Category_Holder(ItemDialogChecklistDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        //  return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {
        var current = AllitemsList[position]
        var tempList = AllitemsList

        holder.binding.apply {

            tvInstruction.text = current.desc
            description.setText(current.remark)
            //  val spinner = findViewById<Spinner>(R.id.spinner)
//            var transportType = -1
//            transportType = R.array.phoneTypes
//            val spinnerItems = context.resources.getStringArray(R.array.imProtocols)
//
//            val modeOfTransportAdapter = ArrayAdapter.createFromResource(
//                context,
//                transportType,
//                R.layout.simple_spinner_item
//            )
//            modeOfTransportAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
//            spinnerYesNo.adapter = modeOfTransportAdapter

            var newText = current.remark

            spinnerYesNo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedItem = parent?.getItemAtPosition(position).toString()
                    onYesNoSpinnerClickListener?.let { click ->
                        Log.e("onyesClick", "onItemSelected: $selectedItem,....${holder.absoluteAdapterPosition}....NewTEXT====>$newText")
                        click(current.desc,selectedItem,newText,holder.absoluteAdapterPosition)

                    }
                    // Toast.makeText(context, "Selected item: $selectedItem", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case when no item is selected
                }
            }
            description.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    newText = p0.toString()

                    onYesNoSpinnerClickListener?.let { click ->
                        Log.e(
                            "onyesClick",
                            "onItemSelected: $selectedItem,....${holder.absoluteAdapterPosition}....NewTEXT====>$newText"
                        )
                        click(current.desc,selectedItem,newText,holder.absoluteAdapterPosition)

                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })


        }


//            holder.itemView.setOnClickListener {
//
//            }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(current)
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(var binding: ItemDialogChecklistDataBinding) :
        RecyclerView.ViewHolder(binding.root) {


//        val sr_no = itemView.findViewById<TextView>(R.id.sr_no)
//        val message = itemView.findViewById<TextView>(R.id.message)


    }


}
