package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.ahuja.sons.activity.AccountDetailActivity
import com.ahuja.sons.adapter.*
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.FragmentBranchListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import com.ahuja.sons.viewmodel.MainViewModel
import java.util.ArrayList


class BranchListFragment(val accountdata: AccountBpData) : Fragment(), AccountDetailActivity.MyFragmentCustomerListener {

    lateinit var binding : FragmentBranchListBinding
    lateinit var adapter: BranchListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    var AllitemsList : ArrayList<BranchAllListResponseModel.DataXXX> = ArrayList()
    var CardCode = ""
    lateinit var viewModel: MainViewModel

    companion object{
        private const val TAG = "BranchListFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBranchListBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as AccountDetailActivity).viewModel

        if (Global.checkForInternet(requireContext())){

            CardCode = accountdata.CardCode

            //todo calling branch api list here---

            var jsonObject1 = JsonObject()
            jsonObject1.addProperty("BPCode", CardCode)
            viewModel.getBranchAllList(jsonObject1)

            bindBranchListObserver()

        }

      /*  binding.swipeRefreshLayout.setOnRefreshListener {
            if (Global.checkForInternet(requireContext())) {
                //todo calling branch api list here---

                var jsonObject1 = JsonObject()
                jsonObject1.addProperty("BPCode", CardCode)
                viewModel.getBranchAllList(jsonObject1)

                bindBranchListObserver()
            } else {
                binding.swipeRefreshLayout.setRefreshing(false)
            }
        }*/


    }

    private fun bindBranchListObserver() {
        viewModel.branchAllList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                Log.e("fail==>", it.toString())
//                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    if (it.data.size > 0){
                        binding.nodatafound.visibility = View.GONE
                        Log.e("response", it.data.toString())
                        var SalesEmployeeList: List<BranchAllListResponseModel.DataXXX> = ArrayList<BranchAllListResponseModel.DataXXX>()
                        SalesEmployeeList = it.data
                        AllitemsList.clear()
                        AllitemsList.addAll(SalesEmployeeList)
                        setAdapter()
                    }else{
                        binding.nodatafound.visibility = View.VISIBLE
                    }


                }else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


    private fun setAdapter() {
        linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.branchRecyclerView.layoutManager = linearLayoutManager
        adapter = BranchListAdapter(AllitemsList)
        binding.branchRecyclerView.adapter = adapter


    }
    
    
    override fun onDataPassedCustomer(startDate: String?, endDate: String?, pos: Int?) {
        Log.e(TAG, "onDataPassedCustomer: ", )
    }
    


}