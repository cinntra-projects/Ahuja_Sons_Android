package com.ahuja.sons.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ahuja.sons.R
import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.DialogTicketTypeAssignBinding

import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs

class DialogTicketTypeAssignerFragment : DialogFragment() {
    private var _binding: DialogTicketTypeAssignBinding? = null
    private val binding get() = _binding!!
    var data: String? = null
    var id: String? = null
    var ticketType: String? = null
    lateinit var viewModel: MainViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTicketTypeAssignBinding.inflate(layoutInflater, null, false)
        viewModel = (activity as TicketDetailsActivity).viewModel
        val args = arguments
        data = args?.getString("key")
        id = args?.getString("id")
        ticketType = args?.getString("ticketType")
        Log.e(TAG, "onCreateDialog: $data")
        binding.tvTitleDialog.text = data
        binding.btnSave.setOnClickListener {
            var hashMap = HashMap<String, Any>()
            hashMap["EmployeeId"] = Prefs.getString(Global.Employee_Code, "")
            hashMap["AssignTo"] = typevalCode
            hashMap["Remarks"] = binding.description.text.toString()
            hashMap["Type"] = ticketType!!
            hashMap["TicketId"] = id!!
            if (binding.description.text.isNotEmpty()){
                viewModel.updateAssigner(hashMap)
            }else{
                binding.description.apply {
                    setError(" please Enter")
                    requestFocus()
                }
            }


        }

        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        var hashMap = HashMap<String, String>()
        hashMap["SalesPerson"] = Prefs.getString(Global.Employee_Code, "")
        hashMap["Team"] = "Operation"


        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        viewModel.getAssignerList(hashMap)
        subscribeToObserver()
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
        Log.e(TAG, "onViewCreated: $data")
        // subscribeToObserver()

    }

    var typeval = ""
    var typevalCode = ""

    private fun subscribeToObserver() {
        viewModel.assignUserListStatus.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserverError:$it ")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {

            }, {
                if (it.status == 200) {
                    var typeListAnd = it.data
                    var nameTypeTicket = mutableListOf<String>()
                    for (typical in typeListAnd) {
                        nameTypeTicket.add(typical.SalesEmployeeName)
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_row, nameTypeTicket
                    )
                    binding.contactPersonSpinner.adapter = adapter

                    binding.contactPersonSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View, position: Int, id: Long
                        ) {
//                            Toast.makeText(
//                                this@AddTicketActivity,
//                                nameTypeTicket[position], Toast.LENGTH_SHORT
//                            ).show()
                            typeval = nameTypeTicket[position]
                            typevalCode = typeListAnd[position].SalesEmployeeCode

                            Log.e(TAG, "onItemSelected: $typeval $typevalCode")

//                            var hash = HashMap<String, String>()
//                            hash["Type"] = typeval
//                            viewModel.getSubType(hash)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            typeval = nameTypeTicket[0]
                            typevalCode = typeListAnd[0].SalesEmployeeCode
                            // write code to perform some action
                        }
                    }

                } else {
                    Log.e(TAG, "subscribeToObserverError:${it.message} ")
                    Global.warningmessagetoast(requireContext(), it.message)
                }


            }
        ))
        viewModel.updateAssigner.observe(this, Event.EventObserver(
            onError = {
                binding.loadingView.visibility = View.GONE
                binding.btnSave.isEnabled = true
                binding.btnCancel.isEnabled = true
                Log.e(TAG, "subscribeToObserverError:$it ")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.loadingView.visibility = View.VISIBLE
                binding.btnSave.isEnabled = false
                binding.btnCancel.isEnabled = false
            }, {
                binding.loadingView.visibility = View.GONE
                binding.btnSave.isEnabled = true
                binding.btnCancel.isEnabled = true

                if (it.status == 200) {
                    Global.successmessagetoast(requireContext(),"Updated Successfully")
                    dialog?.dismiss()
                } else {
                    Log.e(TAG, "subscribeToObserverApi: ${it.message}")
                }
            }
        ))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}