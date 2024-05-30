package com.ahuja.sons.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.activity.EditTicketActivity
import com.ahuja.sons.adapter.SelectBranchAdapter
import com.ahuja.sons.databinding.FragmentSelectBranchBinding
import com.ahuja.sons.newapimodel.BranchAllListResponseModel

class SelectBranchFragment(val branchnameValue: EditText, val contactlist: ArrayList<BranchAllListResponseModel.DataXXX>, val ticketActivity: AddTicketActivity,
    val editTicketActivity: EditTicketActivity, val flag: String) : Fragment() {

    lateinit var binding : FragmentSelectBranchBinding
    lateinit var adapter: SelectBranchAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var addTicketActivity: AddTicketActivity
    lateinit var edtTcketAct : EditTicketActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectBranchBinding.inflate(layoutInflater)

        addTicketActivity = ticketActivity

        edtTcketAct = editTicketActivity

        binding.toolbarview.heading.text = "Select Branch"

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SelectBranchAdapter(requireContext(), branchnameValue, contactlist, ticketActivity, editTicketActivity, flag)
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = adapter
        binding.nodatafound.isVisible = adapter.itemCount == 0


        binding.toolbarview.backPress.setOnClickListener {
            activity?.onBackPressed()
        }


        return binding.root
    }
}