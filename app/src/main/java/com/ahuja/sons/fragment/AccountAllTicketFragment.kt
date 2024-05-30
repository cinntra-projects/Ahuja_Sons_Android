package com.ahuja.sons.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.adapter.TicketAdapter
import com.ahuja.sons.databinding.CategoryseeAllFragmentBinding
import com.ahuja.sons.model.TicketDataModel

class AccountAllTicketFragment(val AllitemsList : ArrayList<TicketDataModel>) : Fragment() {

    private lateinit var ticketbiding : CategoryseeAllFragmentBinding

    lateinit var adapter: TicketAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = CategoryseeAllFragmentBinding.inflate(layoutInflater)



        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = TicketAdapter(AllitemsList)
        ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
        ticketbiding.productRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        ticketbiding.nodatafound.isVisible = adapter.itemCount==0
        ticketbiding.loadingView.stop()
        ticketbiding.loadingback.visibility = View.GONE

        return ticketbiding.root
    }


}
