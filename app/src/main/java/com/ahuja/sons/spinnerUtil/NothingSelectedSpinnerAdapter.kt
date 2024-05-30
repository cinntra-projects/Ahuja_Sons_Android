package com.ahuja.sons.spinnerUtil

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.SpinnerAdapter

class NothingSelectedSpinnerAdapter(private val adapter: SpinnerAdapter, private val nothingSelectedLayout: Int, private val nothingSelectedDropdownLayout: Int, private val context: Context) : SpinnerAdapter, ListAdapter {

    private val EXTRA = 1
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (position == 0) {
            return getNothingSelectedView(parent)
        }
        return adapter.getView(position - EXTRA, null, parent)
    }

    protected fun getNothingSelectedView(parent: ViewGroup): View {
        return layoutInflater.inflate(nothingSelectedLayout, parent, false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (position == 0) {
            return if (nothingSelectedDropdownLayout == -1) View(context) else getNothingSelectedDropdownView(parent)
        }
        return adapter.getDropDownView(position - EXTRA, null, parent)
    }

    protected fun getNothingSelectedDropdownView(parent: ViewGroup): View {
        return layoutInflater.inflate(nothingSelectedDropdownLayout, parent, false)
    }

    override fun getCount(): Int {
        val count = adapter.count
        return if (count == 0) 0 else count + EXTRA
    }

    override fun getItem(position: Int): Any? {
        return if (position == 0) null else adapter.getItem(position - EXTRA)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return if (position >= EXTRA) adapter.getItemId(position - EXTRA) else (position - EXTRA).toLong()
    }

    override fun hasStableIds(): Boolean {
        return adapter.hasStableIds()
    }

    override fun isEmpty(): Boolean {
        return adapter.isEmpty
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        adapter.registerDataSetObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        adapter.unregisterDataSetObserver(observer)
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }
}