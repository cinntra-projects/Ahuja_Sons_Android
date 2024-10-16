package com.ahuja.sons.dialogs

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
import com.ahuja.sons.apibody.BodyForIssueSubCategory
import com.ahuja.sons.apibody.Filter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.DialogQualityInspectionBinding
import com.ahuja.sons.fragment.DetailsTicketFragment
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.DataQualityIssueSubCategory
import com.ahuja.sons.recyclerviewadapter.TicketTypeLogTicketDetailsAdapter
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs

class DialogQualityInstpection : DialogFragment() {
    private var _binding: DialogQualityInspectionBinding? = null
    private val binding get() = _binding!!
  //  var data: String? = null
    var id: String? = null
   // var ticketType: String? = null
    lateinit var viewModel: MainViewModel
    var ticketTypeLogTicketDetailsAdapter=TicketTypeLogTicketDetailsAdapter()
//
//    var typeval = ""
//    var typevalCode = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogQualityInspectionBinding.inflate(layoutInflater, null, false)
        viewModel = (activity as TicketDetailsActivity).viewModel
        val args = arguments
     //   data = args?.getString("key")
        id = args?.getString("id")
       // ticketType = args?.getString("ticketType")
      //  Log.e(TAG, "onCreateDialog: $data")
      //  binding.tvTitleDialog.text = "Ticket Type Status"

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
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        var hashMap = HashMap<String, Any>()
        hashMap["TicketId"] = id.toString()
        //  hashMap["Team"] = "Operation"

        binding.btnSave.setOnClickListener {
            if (binding.description.text.isEmpty()){
                binding.description.apply {
                    error = resources.getString(R.string.cannot_empty)
                    requestFocus()
                }


            }else{
                binding.btnSave.isEnabled=false
                binding.btnCancel.isEnabled=false
                var data=HashMap<String,String>()
                data["IssueType"]=subTypName
                data["TicketId"]=id.toString()
                data["Description"]=binding.description.text.toString()
                data["CreatedBy"]=Prefs.getString(Global.Employee_Code)
                data["CreatedDate"]=Global.getTodayDateDashFormat()
                data["CreatedTime"]=Global.getTCurrentTime()

                viewModel.addQualityInspection(data)
                postResponse()
            }

        }


        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        viewModel.getQualityIssueCategory()
        subscribeToObserver()
       // setupRecyclerView()
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

    var subTypName = ""
    var subTypeCode = ""
    var typesub:MutableList<DataQualityIssueSubCategory> = mutableListOf()

//    private fun setupRecyclerView()=binding.rvTicketTypeLogs.apply {
//        adapter=ticketTypeLogTicketDetailsAdapter
//        layoutManager=LinearLayoutManager(requireContext())
//
//    }

    private fun subscribeToObserver() {
        viewModel.inspectionIssueCategory.observe(this, Event.EventObserver(
            onError = {
                binding.loadingView.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverERROR: $it")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.loadingView.visibility = View.VISIBLE
            }, { response ->
                binding.loadingView.visibility = View.GONE
                if (response.status == 200) {

                    var typeListAnd = response.data
                    var nameTypeTicket = mutableListOf<String>()
                    for (typical in typeListAnd) {
                        nameTypeTicket.add(typical.Title)
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_row, nameTypeTicket
                    )
                    binding.issueCategorySpinner.adapter = adapter

                    binding.issueCategorySpinner.onItemSelectedListener = object :
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
                            typevalCode = typeListAnd[position].id.toString()

                            Log.e(TAG, "onItemSelected: $typeval $typevalCode")

//                            var hash = HashMap<String, String>()
//                            hash["Type"] = typeval
//                            viewModel.getSubType(hash)
                            var data=BodyForIssueSubCategory(
                                fields = listOf("id","Title","IssueCategory","CreatedBy","CreatedDate","CreatedTime")
                            , filter =  Filter(IssueCategory = typevalCode)
                            )

                            viewModel.getQualityIssueSubCategory(data)

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            typeval = nameTypeTicket[0]
                            typevalCode = typeListAnd[0].id.toString()
                            // write code to perform some action
                        }
                    }

                } else {
                    Log.e(TAG, "subscribeToObserverERROR: ${response.message}")
                    Global.warningmessagetoast(requireContext(), response.message)
                }

            }
        ))


        viewModel.inspectionIssueSubCategory.observe(this, Event.EventObserver(
            onError = {
                binding.loadingView.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverERROR: $it")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.loadingView.visibility = View.VISIBLE
            }, { response ->
                binding.loadingView.visibility = View.GONE
                if (response.status == 200) {

                   typesub = response.data
                    var nameTypeTicket = mutableListOf<String>()
                    for (typical in typesub) {
                        nameTypeTicket.add(typical.Title)
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_row, nameTypeTicket
                    )
                    binding.selectSubTypeSpinner.adapter = adapter

                    binding.selectSubTypeSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View, position: Int, id: Long
                        ) {
//                            Toast.makeText(
//                                this@AddTicketActivity,
//                                nameTypeTicket[position], Toast.LENGTH_SHORT
//                            ).show()

                            subTypName = nameTypeTicket[position]
                            subTypeCode = typesub[position].id.toString()

                            Log.e(TAG, "onItemSelected: $subTypName $subTypeCode")

////                            var hash = HashMap<String, String>()
////                            hash["Type"] = typeval
////                            viewModel.getSubType(hash)
//                            var data=BodyForIssueSubCategory(
//                                fields = listOf("id","Title","IssueCategory","CreatedBy","CreatedDate","CreatedTime")
//                                , Filter(IssueCategory = typevalCode)
//                            )
//
//                            viewModel.getQualityIssueSubCategory(data)

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            subTypName = nameTypeTicket[0]
                            subTypeCode = typesub[0].id.toString()
                            // write code to perform some action
                        }
                    }

                } else {
                    Log.e(TAG, "subscribeToObserverERROR: ${response.message}")
                    Global.warningmessagetoast(requireContext(), response.message)
                }

            }
        ))




    }

    private fun postResponse(){
        viewModel.addQuality.observe(this,Event.EventObserver(
            onError = {
                binding.btnSave.isEnabled=false
                binding.btnCancel.isEnabled=false
                binding.loadingView.visibility=View.GONE
                Log.e(TAG, "ADDQUALITY===>: $it")
                Global.warningmessagetoast(requireContext(),it)
            }, onLoading = {
                binding.btnSave.isEnabled=false
                binding.btnCancel.isEnabled=false
                binding.loadingView.visibility=View.VISIBLE
            },{response->
                Log.e(TAG, "subscribeToObserverDialog: ", )
                binding.btnSave.isEnabled=true
                binding.btnCancel.isEnabled=true
                binding.loadingView.visibility=View.GONE
                if (response.status.equals(200)) {
                    //binding.description.setText("")
                    binding.description.clearComposingText()
                    Global.successmessagetoast(
                        requireContext(),
                        resources.getString(R.string.updated_successfully)
                    )
                    dialog?.dismiss()
                    // Get a reference to the parent fragment

                    // Get a reference to the parent fragment

                    // Call the refresh method on the parent fragment

                    // Call the refresh method on the parent fragment
                    (this.parentFragment as DetailsTicketFragment?)?.refreshFragment()

                }else{
                    Log.e(TAG, "ADDQUALITYAPI===>: ${response.message}")
                    Global.warningmessagetoast(requireContext(),response.message)
                }
            }
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}