package com.ahuja.sons.ahujaSonsClasses.fragments.route

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.adapter.RouteItemListAdapter
import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalRouteData
import com.ahuja.sons.databinding.DialogAssignDeliveryPersonBinding
import com.ahuja.sons.databinding.FragmentRouteBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.DocumentLine

class RouteFragment : Fragment() {
    lateinit var binding: FragmentRouteBinding
    var routeItemListAdapter = RouteItemListAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        private const val TAG = "RouteFragment"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sampleData = listOf(
            LocalRouteData(
                id = "1",
                orderName = "Route 1",
                orderList = mutableListOf(
                    AllOrderListResponseModel.Data(id = "101", CardName = "Order details 101"),
                    AllOrderListResponseModel.Data(id = "102", CardName = "Order details 102")
                )
            ),
            LocalRouteData(
                id = "2",
                orderName = "Route 2",
                orderList = mutableListOf(
                    AllOrderListResponseModel.Data(id = "201", CardName = "Order details 201"),
                    AllOrderListResponseModel.Data(id = "202", CardName = "Order details 202")
                )
            ),
            LocalRouteData(
                id = "3",
                orderName = "Route 3",
                orderList = mutableListOf(
                    AllOrderListResponseModel.Data(id = "301", CardName = "Order details 301"),
                    AllOrderListResponseModel.Data(id = "302", CardName = "Order details 302")
                )
            ),
            LocalRouteData(
                id = "4",
                orderName = "Route 4",
                orderList = mutableListOf(
                    AllOrderListResponseModel.Data(id = "401", CardName = "Order details 401"),
                    AllOrderListResponseModel.Data(id = "402", CardName = "Order details 402")
                )
            ),
            LocalRouteData(
                id = "5",
                orderName = "Route 5",
                orderList = mutableListOf(
                    AllOrderListResponseModel.Data(id = "501", CardName = "Order details 501"),
                    AllOrderListResponseModel.Data(id = "502", CardName = "Order details 502")
                )
            )
        )

        routeItemListAdapter.submitList(sampleData)



        binding.rvRoute.apply {
            adapter = routeItemListAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            routeItemListAdapter.notifyDataSetChanged()
        }

    }




}