package com.ahuja.sons.ahujaSonsClasses.demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.databinding.FragmentSelectAllCheckBoxBinding

class SelectAllCheckBoxFragment(var textView: TextView) : Fragment() {

    lateinit var binding : FragmentSelectAllCheckBoxBinding
    private lateinit var parentItemList: MutableList<ParentItemModel>
    private lateinit var parentAdapter: ParentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectAllCheckBoxBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentItemList = getParentItems()
        parentAdapter = ParentAdapter(parentItemList)
        binding.recyclerViewParent.adapter = parentAdapter
        binding.recyclerViewParent.layoutManager = LinearLayoutManager(requireActivity())

        binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            parentItemList.forEach { parentItem ->
                parentItem.isSelected = isChecked
                parentItem.childItemList.forEach { it.isSelected = isChecked }
            }
            parentAdapter.notifyDataSetChanged()
        }


    }


    private fun getParentItems(): MutableList<ParentItemModel> {
        // Populate your parent items with children
        val parentItems = mutableListOf<ParentItemModel>()

        // Example data
        val childItems1 = mutableListOf(
            ParentItemModel.ChildItem("Child 1", false,"1"),
            ParentItemModel.ChildItem("Child 2", false,"2")
        )
        parentItems.add(ParentItemModel("Parent 1", false, childItems1))

        val childItems2 = mutableListOf(
            ParentItemModel.ChildItem("Child 3", false,"3"),
            ParentItemModel.ChildItem("Child 4", false,"4")
        )
        parentItems.add(ParentItemModel("Parent 2", false, childItems2))

        return parentItems
    }




}