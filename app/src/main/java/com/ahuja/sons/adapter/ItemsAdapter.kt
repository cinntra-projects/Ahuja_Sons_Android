package com.ahuja.sons.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.DocumentLine
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ItemsAdapter(val context: Context, val AllitemsList: ArrayList<DocumentLine>, val ticketID: String) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    private val SelectedTaxSlab = ""
    private var currentItemPo = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        tempList.addAll(AllitemsList)
        return ViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj: DocumentLine = getItem(position)
        holder.customerName.text = obj.ItemName
        holder.cardNumber.text = obj.ItemCode
//        holder.stock.text = context!!.getString(R.string.instock) + " : " + obj.getInStock()
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    fun getItem(position: Int): DocumentLine {
        return AllitemsList[position]
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var customerName: TextView
        var cardNumber: TextView
        var stock: TextView

        init {
            customerName = itemView.findViewById(R.id.customerName)
            cardNumber = itemView.findViewById(R.id.cardNumber)
            stock = itemView.findViewById(R.id.stock)
            stock.visibility = View.VISIBLE
            itemView.setOnClickListener {
                currentItemPo = adapterPosition
                val itemObj: DocumentLine = AllitemsList[currentItemPo]
                itemObj.UnitPrice = AllitemsList[currentItemPo].UnitPrice
                setQuantity(context, itemObj)
                /*
              ItemsList.get(currentItemPo).setItemUnitPrice(ItemsList.get(currentItemPo).getItemPrices().get(CreateContact.PriceListNum-1).getPrice());
              ItemsList.get(currentItemPo).setItemTaxCode(SelectedTaxSlab);
               */
            }
        }
    }


    /*********** Make Custom Views and Data  */
    private fun setQuantity(context: Context, itemsObj: DocumentLine) {
        val edUnitPrice: EditText
        val discount_value: EditText
        val remarks_value: EditText
        val button: Button
        val department_spinner: Spinner
        val dialog = Dialog(context)
        // LayoutInflater layoutInflater = context.getLayoutInflater();
        val layoutInflater = LayoutInflater.from(context)
        val custom_dialog: View = layoutInflater.inflate(R.layout.quantity_alert, null)
        edUnitPrice = custom_dialog.findViewById(R.id.editText)
        discount_value = custom_dialog.findViewById(R.id.discount_value)
        remarks_value = custom_dialog.findViewById(R.id.remarks_value)
        val ac_part_request_type: AutoCompleteTextView = custom_dialog.findViewById(R.id.ac_part_request_type)
        var acItems: AutoCompleteTextView = custom_dialog.findViewById(R.id.acItems)
        val acContractType: AutoCompleteTextView = custom_dialog.findViewById(R.id.acContractType)

        button = custom_dialog.findViewById(R.id.button)
        dialog.setContentView(custom_dialog)
        //dialog.setTitle("");
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        //editText.getText();

        edUnitPrice.isEnabled = false
        edUnitPrice.isClickable = false
        edUnitPrice.isFocusableInTouchMode = false
        edUnitPrice.setText(itemsObj.UnitPrice.toString())

        val partRequestList = arrayListOf("FOC", "Chargeable")
        val contractTypeList = arrayListOf("Parts Change", "Extra Requirement")


        //todo ticket by item list--
        callItemByTicketApi(acItems)


        //todo set part request type
        var partRequestSelect = ""
        var partTypeNo = ""
        val partReqADapter: ArrayAdapter<String> = ArrayAdapter<String>(context, R.layout.drop_down_item_textview, partRequestList)
        ac_part_request_type.setAdapter(partReqADapter)

        ac_part_request_type.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            partRequestSelect = selectedItem
            partTypeNo = position.toString()
            Log.e("TAG", "setQuantity: $partRequestSelect  $partTypeNo")
        }

        //todo set Contract type
        var contractTypeName = ""
        val contractAdapter: ArrayAdapter<String> = ArrayAdapter<String>(context, R.layout.drop_down_item_textview, contractTypeList)
        acContractType.setAdapter(contractAdapter)

        acContractType.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            contractTypeName = selectedItem
            Log.e("TAG", "setContractType: $contractTypeName ")
        }


        button.setOnClickListener {
            var alertStatus = true
            if (edUnitPrice.text.toString().isNotEmpty()) { // && edUnitPrice.text.toString().toDouble() > 0.00
                if (validation(discount_value.text.toString().trim(), remarks_value.text.toString().trim(), context, ac_part_request_type.text.toString(), acItems.text.toString(), acContractType.text.toString())) {
                    if (Global.cartList.size > 0) {
                        for (item in Global.cartList) {
                            if (item?.ItemCode.equals(itemsObj.ItemCode)) {
                                val total_qty = edUnitPrice.text.toString().toDouble()
                                val quantity: Int = discount_value.text.toString().toInt()
                                Global.cartList[Global.cartList.indexOf(item)]?.UnitPrice = total_qty
                                Global.cartList[Global.cartList.indexOf(item)]?.Quantity = quantity
                                Global.cartList[Global.cartList.indexOf(item)]?.Remarks = remarks_value.text.toString()
                                Global.cartList[Global.cartList.indexOf(item)]?.PartRequestType = partRequestSelect
                                Global.cartList[Global.cartList.indexOf(item)]?.ItemSrialNo = itemSerialName
                                Global.cartList[Global.cartList.indexOf(item)]?.ContractType = contractTypeName

                                // Globals.SelectedItems.get(Globals.SelectedItems.indexOf(item)).setItemType(Globals.ItemType);
                                alertStatus = false
                                dialog.dismiss()
                                (context as AppCompatActivity).finish()
                                break
                            }
                        }
                    }
                    if (Global.cartList.size > 0 && alertStatus) {
                        itemsObj.UnitPrice = (edUnitPrice.text.toString().toDouble())
                        itemsObj.Quantity = (discount_value.text.toString().toInt())
                        itemsObj.Remarks = (remarks_value.text.toString())
                        itemsObj.PartRequestType = (partRequestSelect)
                        itemsObj.ItemSrialNo = (itemSerialName)
                        itemsObj.ContractType = (contractTypeName)
                        dialog.dismiss()
                        //setTaxes(context, itemsObj);
                        Global.cartList.add(itemsObj)
                        val intent = Intent()
                        intent.putExtra(Global.CustomerItemData, itemsObj as Parcelable)
                        (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
                        context.finish()
                    } else {
                        itemsObj.UnitPrice = (edUnitPrice.text.toString().toDouble())
                        itemsObj.Quantity = (discount_value.text.toString().toInt())
                        itemsObj.Remarks = (remarks_value.text.toString())
                        itemsObj.PartRequestType = (partRequestSelect)
                        itemsObj.ItemSrialNo = (itemSerialName)
                        itemsObj.ContractType = (contractTypeName)
                        dialog.dismiss()
                        // setTaxes(context, itemsObj);
                        Global.cartList.add(itemsObj)
                        val intent = Intent()
                        intent.putExtra(Global.CustomerItemData, itemsObj as Parcelable)
                        (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
                        context.finish()
                    }
                }
            } else {
                Toast.makeText(context, "Enter valid Price", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var itemSerialName = ""
    private fun callItemByTicketApi(acItems: AutoCompleteTextView) {
        var jsonObject = JsonObject()
        jsonObject.addProperty("TicketId", ticketID)

        val call: Call<ItemAllListResponseModel> = ApiClient().service.getItemsByTicket2(jsonObject)
        call.enqueue(object : Callback<ItemAllListResponseModel> {
            override fun onResponse(call: Call<ItemAllListResponseModel>, response: Response<ItemAllListResponseModel>) {
                if (response.body()?.status == 200) {
                    try {
                        var itemAllListResponseList: ArrayList<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
                        itemAllListResponseList.clear()
                        itemAllListResponseList.addAll(response.body()?.data!!)
                        var mAdapter = ItemNameAdapter(context, R.layout.drop_down_item_textview ,itemAllListResponseList)
                        acItems.setAdapter(mAdapter)

                        acItems.setOnItemClickListener { adapterView, view, pos, l ->
                            if (itemAllListResponseList.size > 0){
                                itemSerialName = itemAllListResponseList[pos].SerialNo
                                acItems.setText(itemAllListResponseList[pos].ItemName + " ( " +itemAllListResponseList[pos].SerialNo + " )")
                            }

                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                        Log.e("TAG===>", "onResponse: "+e.message )
                    }

                } else {
                    Global.warningmessagetoast(context, response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<ItemAllListResponseModel>, t: Throwable) {
                context?.let { t.message?.let { it1 -> Global.errormessagetoast(it, it1) } }
            }
        })
    }

    private fun validation(
        quantity: String,
        remark: String,
        context: Context,
        ac_part_request_type: String,
        acItem: String,
        acContract: String
    ): Boolean {
        if (quantity.isEmpty() || quantity.toInt() == 0) {
            Toast.makeText(context, "Enter Quantity", Toast.LENGTH_SHORT).show()
            return false
        } else if (remark.isEmpty()) {
            Toast.makeText(context, "Enter Remarks", Toast.LENGTH_SHORT).show()
            return false
        } else if (ac_part_request_type.isNullOrEmpty()) {
            Toast.makeText(context, "Part Type can't be empty", Toast.LENGTH_SHORT).show()
            return false
        }else if (acItem.isNullOrEmpty()) {
            Toast.makeText(context, "Atleast 1 Item Select", Toast.LENGTH_SHORT).show()
            return false
        }else if (acContract.isNullOrEmpty()) {
            Toast.makeText(context, "Contract Type can't be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun postjson(itemsObj: DocumentLine): DocumentLine {
        val dc = DocumentLine()
        dc.ItemCode = (itemsObj.ItemCode)
        dc.Quantity = (itemsObj.Quantity)
        dc.TaxCode = ("") //BED+VAT
        dc.UnitPrice = (itemsObj.UnitPrice)
        dc.ItemDescription = (itemsObj.ItemName)

        dc.DiscountPercent = (0.0)
        dc.id = itemsObj.id
        return dc
    }

    var tempList = ArrayList<DocumentLine>()
    fun filter(charText: String) {
        var charText = charText
        charText = charText.lowercase(Locale.getDefault())
        AllitemsList.clear()
        if (charText.isEmpty()) {
            AllitemsList.addAll(tempList)
        } else {
            for (st in tempList) {
                if (st.ItemName.isNotEmpty()) {
                    if (st.ItemName.toLowerCase(Locale.getDefault())
                            .contains(charText) || st.ItemCode.toLowerCase(Locale.getDefault())
                            .contains(charText)
                    ) {
                        AllitemsList.add(st)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

}