package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.ahujaSonsClasses.adapter.DeliveryItemsListAdapter
import com.ahuja.sons.databinding.FragmentDeliveryItemsBinding

class DeliveryItemsFragment : Fragment() {

    lateinit var binding : FragmentDeliveryItemsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeliveryItemsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var dataList = arrayListOf<String>("Attune Femur123", "Attune Femur1", "Attune Femur2","Attune Femur3","Attune Femur4")

        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.deliveryRecyclerView.layoutManager = linearLayoutManager
        val adapter = DeliveryItemsListAdapter(dataList)
        binding.deliveryRecyclerView.adapter = adapter


    }


}