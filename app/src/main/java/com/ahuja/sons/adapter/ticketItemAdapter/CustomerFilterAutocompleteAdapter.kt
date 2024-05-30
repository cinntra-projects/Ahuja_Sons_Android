package com.ahuja.sons.adapter.ticketItemAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ahuja.sons.R
import com.ahuja.sons.newapimodel.SpareItemListApiModel


class CustomerFilterAutocompleteAdapter(
    private val context: Context,
    private val resourceId: Int,
    private val data: ArrayList<SpareItemListApiModel.DataXXX>
) : ArrayAdapter<SpareItemListApiModel.DataXXX>(context, resourceId, data) {

    private val originalData: List<SpareItemListApiModel.DataXXX> = data
    private val filter = CustomFilter()
    private val inflater: LayoutInflater = LayoutInflater.from(context)



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent)
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v = convertView
        if (v == null) {
            v = inflater.inflate(R.layout.drop_down_item_textview, null)
        }
        val title = v!!.findViewById<TextView>(R.id.title)
        //        if (!stagesList.get(position).getRole().equals("admin"))
        title.setText(data[position].ItemDescription)
        return v
    }

    override fun getItem(position: Int): SpareItemListApiModel.DataXXX? {
        return data[position]
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getFilter(): Filter {
        return filter
    }

    private inner class CustomFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredResults = FilterResults()

            if (p0 != null) {
                val suggestions = originalData.filter {
                    it.ItemDescription.contains(p0, ignoreCase = true)
                }

                filteredResults.values = suggestions
                filteredResults.count = suggestions.size
            }

            return filteredResults
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            if (p1 != null && p1.count > 0) {
                clear()
                addAll(p1.values as ArrayList<SpareItemListApiModel.DataXXX>)
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }


}