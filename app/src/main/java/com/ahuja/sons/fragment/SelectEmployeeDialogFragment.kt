package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.adapter.SelectEmployeeDialogAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.SelectDepartmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.*
import com.ahuja.sons.receiver.DataEmployeeAllData
import com.ahuja.sons.viewmodel.MainViewModel
import java.util.ArrayList

class SelectEmployeeDialogFragment() : DialogFragment() {



    lateinit var adapter: SelectEmployeeDialogAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel: MainViewModel


    private  lateinit var ticketFragment: SelectDepartmentBinding



    companion object {

        const val TAG = "SimpleDialog"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"



        fun newInstance(title: String, id: String): SelectEmployeeDialogFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, id)
            val fragment = SelectEmployeeDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        ticketFragment = SelectDepartmentBinding.inflate(layoutInflater)
        viewModel=(activity as MainActivity).viewModel

        ticketFragment.toolbarview.heading.text = "Select Employee"
        ticketFragment.loadingView.isVisible= true
        if(Global.checkForInternet(requireContext())){
            ticketFragment.loadingView.start()
            viewModel.getAllEmployeeList()
            subscribeToObserver()
        }
        



        ticketFragment.toolbarview.backPress.setOnClickListener {
            dismiss()
        }
        return ticketFragment.root
    }



    private fun subscribeToObserver() {
        viewModel.employeesAll.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserver: $it", )
                ticketFragment.loadingView.stop()
            }, onLoading = {
                ticketFragment.loadingView.start()
            }, { employeeAll ->
                ticketFragment.loadingView.stop()
                if (employeeAll.status.equals(200)) {
                    Log.e(TAG, "subscribeToObserveAPir: $employeeAll", )

                    //  Toast.makeText(requireContext(), employeeAll.data.size.toString(), Toast.LENGTH_SHORT).show()
                    linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = SelectEmployeeDialogAdapter(this@SelectEmployeeDialogFragment,arguments?.getString(KEY_SUBTITLE),
                        employeeAll.data as ArrayList<DataEmployeeAllData>
                    )
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

//    private fun callemployeeapi() {
//        val data = HashMap<String,String>()
//        data["SalesEmployeeCode"] = Prefs.getString(Global.Employee_Code)
//
//
//        val call: Call<LogInResponse> =
//            ApiClient().service.allemployeelist(data)
//        call.enqueue(object : Callback<LogInResponse?> {
//            override fun onResponse(
//                call: Call<LogInResponse?>,
//                response: Response<LogInResponse?>
//            ) {
//                if (response.body()!!.getStatus() == 200) {
//                    linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                    adapter = SelectEmployeeDialogAdapter(this@SelectEmployeeDialogFragment,arguments?.getString(KEY_SUBTITLE),
//                        response.body()?.getLogInDetail() as ArrayList<NewLoginData>
//                    )
//                    ticketFragment.recyclerview.layoutManager = linearLayoutManager
//                    ticketFragment.recyclerview.adapter = adapter
//
//                }
//                else {
//                    response.body()!!.getMessage()
//                        ?.let { Global.warningmessagetoast(requireContext(), it) }
//                }
//               // ticketFragment.loadingback.visibility = View.GONE
//
//                ticketFragment.loadingView.isVisible= true
//                ticketFragment.loadingView.stop()
//
//
//            }
//
//            override fun onFailure(call: Call<LogInResponse?>, t: Throwable) {
//             //   ticketFragment.loadingback.visibility = View.GONE
//
//                ticketFragment.loadingView.stop()
//                ticketFragment.loadingView.isVisible= true
//                Global.errormessagetoast(requireContext(),t.message.toString())
//            }
//        })
//    }


}
