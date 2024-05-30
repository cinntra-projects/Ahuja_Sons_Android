package com.ahuja.sons.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.adapter.SelectItemAdapter
import com.ahuja.sons.databinding.SelectDepartmentBinding

class SelectItemFragement(
    val itemValue: AddTicketActivity,
    val itemlist :  ArrayList<com.ahuja.sons.newapimodel.DocumentLine>
) : Fragment() {


    lateinit var adapter: SelectItemAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var selectOrderData : AddTicketActivity


    private  lateinit var ticketFragment: SelectDepartmentBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ticketFragment = SelectDepartmentBinding.inflate(layoutInflater)
        selectOrderData = itemValue
        ticketFragment.toolbarview.heading.text  = "Select Item"


        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SelectItemAdapter(itemValue,requireContext(),itemlist)
        ticketFragment.recyclerview.layoutManager = linearLayoutManager
        ticketFragment.recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()
        ticketFragment.nodatafound.isVisible = adapter.itemCount==0



        ticketFragment.toolbarview.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        return ticketFragment.root
    }




}
