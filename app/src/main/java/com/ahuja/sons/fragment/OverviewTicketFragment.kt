package com.ahuja.sons.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.adapter.OverviewTicketAdapter
import com.ahuja.sons.databinding.OverviewTicketBinding

class OverviewTicketFragment : Fragment() {

    private lateinit var ticketbiding : OverviewTicketBinding
    lateinit var adapter: OverviewTicketAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ticketbiding = OverviewTicketBinding.inflate(layoutInflater)

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = OverviewTicketAdapter()
        ticketbiding.recyclerview.layoutManager = linearLayoutManager
        ticketbiding.recyclerview.adapter = adapter
        return ticketbiding.root
    }
}
