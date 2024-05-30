package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.activity.EditTicketActivity
import com.ahuja.sons.adapter.SelectEmployeeAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.SelectDepartmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.receiver.DataEmployeeAllData
import com.ahuja.sons.viewmodel.MainViewModel
import java.util.ArrayList

class SelectEmployeeFragement(val contacnameValue: EditText, val flag : String) : Fragment() {
    lateinit var viewModel: MainViewModel


    lateinit var adapter: SelectEmployeeAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var ticketFragment: SelectDepartmentBinding
    private val TAG = "SelectEmployeeFragement"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ticketFragment = SelectDepartmentBinding.inflate(layoutInflater)

        if (flag == "AddTicketFlag") {
            viewModel = (activity as AddTicketActivity).viewModel
        }else if(flag == "EditTicketFlag"){
            viewModel = (activity as EditTicketActivity).viewModel
        }
        ticketFragment.toolbarview.heading.text = "Select Employee"
       // ticketFragment.loadingView.isVisible = true
        if (Global.checkForInternet(requireContext())) {
            viewModel.getAllEmployeeList()
            subscribeToObserver()
        }


        ticketFragment.toolbarview.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        return ticketFragment.root
    }


    private fun subscribeToObserver() {
        viewModel.employeesAll.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserver: "+it )
                ticketFragment.loadingView.stop()
            }, onLoading = {
                ticketFragment.loadingView.start()
            },
            onSuccess = { employeeAll ->
                ticketFragment.loadingView.stop()
                if (employeeAll.status == 200) {
                  //  Toast.makeText(requireContext(), employeeAll.data.size.toString(), Toast.LENGTH_SHORT).show()
                    linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = SelectEmployeeAdapter(requireContext(), contacnameValue, employeeAll.data as ArrayList<DataEmployeeAllData>)
                    ticketFragment.recyclerview.layoutManager = linearLayoutManager
                    ticketFragment.recyclerview.adapter = adapter
                    adapter.notifyDataSetChanged()
                    ticketFragment.nodatafound.isVisible = adapter.itemCount == 0
                } else {
                    Global.warningmessagetoast(requireContext(), employeeAll.message)
                }

            }
        ))
    }


}
