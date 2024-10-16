package com.ahuja.sons.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.DialogTicketTypeLogBinding

import com.ahuja.sons.globals.Global
import com.ahuja.sons.recyclerviewadapter.TicketTypeLogTicketDetailsAdapter
import com.ahuja.sons.viewmodel.MainViewModel

class DialogTicketTypeLogsDetials : DialogFragment() {
    private var _binding: DialogTicketTypeLogBinding? = null
    private val binding get() = _binding!!
  //  var data: String? = null
    var id: String? = null
   // var ticketType: String? = null
    lateinit var viewModel: MainViewModel
    var ticketTypeLogTicketDetailsAdapter=TicketTypeLogTicketDetailsAdapter()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTicketTypeLogBinding.inflate(layoutInflater, null, false)
        viewModel = (activity as TicketDetailsActivity).viewModel
        val args = arguments
     //   data = args?.getString("key")
        id = args?.getString("id")
       // ticketType = args?.getString("ticketType")
      //  Log.e(TAG, "onCreateDialog: $data")
        binding.tvTitleDialog.text = "Ticket Type Status"

//        binding.btnSave.setOnClickListener {
//            var hashMap = HashMap<String, Any>()
//            hashMap["EmployeeId"] = Prefs.getString(Global.Employee_Code, "")
//            hashMap["AssignTo"] = typevalCode
//            hashMap["Remarks"] = binding.description.text.toString()
//            hashMap["Type"] = ticketType!!
//            hashMap["TicketId"] = id!!
//            if (binding.description.text.isNotEmpty()){
//                viewModel.updateAssigner(hashMap)
//            }else{
//                binding.description.apply {
//                    setError(" please Enter")
//                    requestFocus()
//                }
//            }
//
//
//        }

        binding.ibCross.setOnClickListener {
            dialog?.dismiss()
        }

        var hashMap = HashMap<String, Any>()
        hashMap["TicketId"] = id.toString()
        //  hashMap["Team"] = "Operation"


        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        viewModel.getTicketTypeLogsInDetails(hashMap)
        subscribeToObserver()
        setupRecyclerView()
        return builder.create()

    }

    companion object {
        private const val TAG = "DialogTicketTypeAssigne"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        Log.e(TAG, "onCreateView: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // Log.e(TAG, "onViewCreated: $data")
        // subscribeToObserver()

    }

    var typeval = ""
    var typevalCode = ""

    private fun setupRecyclerView()=binding.rvTicketTypeLogs.apply {
        adapter=ticketTypeLogTicketDetailsAdapter
        layoutManager=LinearLayoutManager(requireContext())

    }

    private fun subscribeToObserver() {
        viewModel.logticketTyelist.observe(this, Event.EventObserver(
            onError = {
                binding.loadingView.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverERROR: $it")
//                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.loadingView.visibility = View.VISIBLE
            }, { response ->
                binding.loadingView.visibility = View.GONE
                if (response.status == 200) {
                   ticketTypeLogTicketDetailsAdapter.ticketLog= response.data

                } else {
                    Log.e(TAG, "subscribeToObserverERROR: ${response.message}")
                    Global.warningmessagetoast(requireContext(), response.message)
                }

            }
        ))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}