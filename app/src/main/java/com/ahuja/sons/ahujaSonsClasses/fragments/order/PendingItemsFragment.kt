package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.ahujaSonsClasses.adapter.PendingItemsListAdapter
import com.ahuja.sons.databinding.FragmentPendingItemsBinding


class PendingItemsFragment : Fragment() {

    lateinit var binding : FragmentPendingItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingItemsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var dataList = arrayListOf<String>("Attune Femur123", "Attune Femur1", "Attune Femur2")

        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pendingRecyclerView.layoutManager = linearLayoutManager
        val adapter = PendingItemsListAdapter(dataList)
        binding.pendingRecyclerView.adapter = adapter


    }


}