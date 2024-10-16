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
import com.google.gson.Gson
import com.ahuja.sons.R
import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.DialogTicketDetailsBinding
import com.ahuja.sons.fragment.DetailsTicketFragment
import com.ahuja.sons.globals.Global
import com.ahuja.sons.jsonmodel.JsonModelForTicketDetails
import com.ahuja.sons.newapimodel.DataQualityIssueSubCategory
import com.ahuja.sons.recyclerviewadapter.TicketTypeLogTicketDetailsAdapter
import com.ahuja.sons.viewmodel.MainViewModel
import org.json.JSONObject

class DialogUpdateTicketDetails : DialogFragment() {
    private var _binding: DialogTicketDetailsBinding? = null
    private val binding get() = _binding!!
    var statusOfDetails = "";
    var materialUsedOfDetails = "";
    var appScheduleDate: String? = null;
    var typeTicket: String? = null
    var dataString = ""
    var correctIssueTypeOfDetails = "";
    var scheduledVisitDateTypeOfDetails = "";
    var correctiveActionsTypeOfDetails = "";
    var RepairRequestNeededTypeOfDetails = "";
    val spinnerStatusValues = arrayOf("Active", "Inactive")
    val spinnerMaterialUsedValues = arrayOf("Yes", "No")
    var indexStatus = -1
    var indexMaterial = -1
    var indexRepair = -1
    val keyToRemove = "nameValuePairs"

    //  var data: String? = null
    var id: String? = null
    var status: String? = null
    var jsonModelForTicketDetails: JsonModelForTicketDetails? = null
    val gson = Gson()

    // var ticketType: String? = null
    lateinit var viewModel: MainViewModel
    var ticketTypeLogTicketDetailsAdapter = TicketTypeLogTicketDetailsAdapter()
//
//    var typeval = ""
//    var typevalCode = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTicketDetailsBinding.inflate(layoutInflater, null, false)
        viewModel = (activity as TicketDetailsActivity).viewModel
        val args = arguments

        id = args?.getString("id")
        Log.e(TAG, "onCreateDialogIIIDD: $id")

        var hashMapGLoba = java.util.HashMap<String, String>()
        hashMapGLoba["id"] = id.toString()


        //  viewModel.getTicketOne(hashMapGLoba)
        subscribeToOne()

        status = args?.getString("data")
        typeTicket = args?.getString("type")
        appScheduleDate = args?.getString("appScheduleDate")


        val obj = gson.fromJson(status, JsonModelForTicketDetails::class.java)

        obj.apply {
            statusOfDetails = Status
            materialUsedOfDetails = MaterialUsed
            correctIssueTypeOfDetails = CorrectIssueType
            scheduledVisitDateTypeOfDetails = ScheduledVisitDate
            correctiveActionsTypeOfDetails = CorrectiveActions
            RepairRequestNeededTypeOfDetails = RepairRequestNeeded
        }
        binding.etCorrectiveActions.setText(correctiveActionsTypeOfDetails)
        binding.etCorrectIssueType.setText(correctIssueTypeOfDetails)
        val statusAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_row, spinnerStatusValues)
        binding.issueCategorySpinner.adapter = statusAdapter
        val materialAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_row, spinnerMaterialUsedValues)
        binding.spinnerMaterialUsed.adapter = materialAdapter

        val repairRequestAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_row, spinnerMaterialUsedValues)
        binding.spinnerRepairRequestNeeded.adapter = repairRequestAdapter

        indexStatus = spinnerStatusValues.indexOf(statusOfDetails)
        if (indexStatus != -1) {
            binding.issueCategorySpinner.setSelection(indexStatus)
        }

        indexMaterial = spinnerMaterialUsedValues.indexOf(materialUsedOfDetails)
        if (indexMaterial != -1) {
            binding.spinnerMaterialUsed.setSelection(indexMaterial)
        }

        indexRepair = spinnerMaterialUsedValues.indexOf(materialUsedOfDetails)
        if (indexRepair != -1) {
            binding.spinnerRepairRequestNeeded.setSelection(indexRepair)
        }

        val jsonObjectdata = JSONObject()
        jsonObjectdata.put("Status", statusOfDetails)
        jsonObjectdata.put("ScheduledVisitDate", scheduledVisitDateTypeOfDetails)
        jsonObjectdata.put("CorrectIssueType", correctIssueTypeOfDetails)
        jsonObjectdata.put("MaterialUsed", materialUsedOfDetails)
        jsonObjectdata.put("CorrectiveActions", binding.etCorrectiveActions.text.toString())
        jsonObjectdata.put("RepairRequestNeeded", RepairRequestNeededTypeOfDetails)
        Log.e(TAG, "onJSONOBJECT===>: $jsonObjectdata")

        binding.issueCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    statusOfDetails = p0?.getItemAtPosition(p2)!!.toString()
                    Log.e(TAG, "STATUS====?: $statusOfDetails")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    statusOfDetails = p0?.getItemAtPosition(0).toString()
                }

            }

        binding.spinnerMaterialUsed.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    materialUsedOfDetails = p0?.getItemAtPosition(p2)!!.toString()
                    Log.e(TAG, "materialUsedOfDetails====?: $materialUsedOfDetails")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    materialUsedOfDetails = p0?.getItemAtPosition(0).toString()
                }

            }

        binding.spinnerRepairRequestNeeded.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    RepairRequestNeededTypeOfDetails = p0?.getItemAtPosition(p2)!!.toString()
                    Log.e(
                        TAG,
                        "RepairRequestNeededTypeOfDetails====?: $RepairRequestNeededTypeOfDetails",
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    RepairRequestNeededTypeOfDetails = p0?.getItemAtPosition(0).toString()
                }

            }

        binding.etScheduleVisitDate.setOnClickListener {
            Global.selectDate(requireContext(), binding.etScheduleVisitDate)
        }

        binding.ivSchedueVisitDate.setOnClickListener {
            Global.selectDate(requireContext(), binding.etScheduleVisitDate)
        }


        //  dataString= "{\"Status\": \"$statusOfDetails\", \"ScheduledVisitDate\": \"$scheduledVisitDateTypeOfDetails\", \"CorrectIssueType\": \"$correctIssueTypeOfDetails \", \"MaterialUsed\": \"$materialUsedOfDetails\", \"CorrectiveActions\": \"${binding.etCorrectiveActions.text.toString()} \", \"RepairRequestNeeded\": \"$RepairRequestNeededTypeOfDetails\"}"


        Log.e(TAG, "JSON====>: $status")
        Log.e(TAG, "OBJ====>: ${obj.Status}")
        Log.e(TAG, "OBJ====>: ${obj.MaterialUsed}")

        binding.ibCross.setOnClickListener {
            dialog?.dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        var hashMap = HashMap<String, Any>()
        //  hashMap["TicketId"] = id.toString()
        //  hashMap["Team"] = "Operation"

        //  convertToModelObjectOther(status)


        binding.btnSave.setOnClickListener {
            //   val jsonObject = JSONObject()
            jsonObjectdata.put("Status", statusOfDetails)
            jsonObjectdata.put("ScheduledVisitDate", scheduledVisitDateTypeOfDetails)
            jsonObjectdata.put("CorrectIssueType", correctIssueTypeOfDetails)
            jsonObjectdata.put("MaterialUsed", materialUsedOfDetails)
            jsonObjectdata.put("CorrectiveActions", binding.etCorrectiveActions.text.toString())
            jsonObjectdata.put("RepairRequestNeeded", RepairRequestNeededTypeOfDetails)
            //   dataString=jsonObject
            Log.e(TAG, "onCreateDialogASHU===>: $jsonObjectdata")
            binding.btnSave.isEnabled = false
            binding.btnCancel.isEnabled = false
            var data = HashMap<String, Any>()
            data["SubType"] = "Other"
            data["id"] = id.toString()
            data["Type"] = typeTicket!!.toString()
            data["AppScheduleDate"] = appScheduleDate.toString()
           // data["Data"] = jsonObjectdata
            Log.e(TAG, "onCreateDialogDDDD==>: ${data["Data"]}")
            val modifiedJsonString = removeKeyFromJson(jsonObjectdata.toString(), keyToRemove)
            Log.e(TAG, "onCreateDialogModify=>: $modifiedJsonString")

            data["Data"] = modifiedJsonString


//            viewModel.updateParticularTicket(data)//todo comment by me--
            subcribeToUpdateTicket()


        }


        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)


        return builder.create()

    }

    private fun subscribeToOne() {
        viewModel.particularTicket.observe(this, Event.EventObserver(
            onError = {
                // binding.loadingView.visibility=View.GONE
            }, onLoading = {
                // binding.loadingView.visibility=View.VISIBLE
            }, {
                // binding.loadingView.visibility=View.GONE
                typeTicket = it.data[0].Type
                appScheduleDate = it.data[0].AppScheduleDate
                Log.e(TAG, "subscribeToOne:$typeTicket$appScheduleDate ")
            }
        ))
    }

    companion object {
        private const val TAG = "DialogTicketTypeAssigne"
    }

    fun convertToModelObjectOther(jsonObject: JSONObject): JsonModelForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val Status = jsonObject.getString("Status")
        val CorrectIssueType = jsonObject.getString("CorrectIssueType")
        val ScheduledVisitDate = jsonObject.getString("ScheduledVisitDate")
        val CorrectiveActions = jsonObject.getString("CorrectiveActions")
        val RepairRequestNeeded = jsonObject.getString("RepairRequestNeeded")
        val MaterialUsed = jsonObject.getString("MaterialUsed")
        jsonModelForTicketDetails?.Status = Status
        jsonModelForTicketDetails?.CorrectIssueType = CorrectIssueType
        jsonModelForTicketDetails?.ScheduledVisitDate = ScheduledVisitDate
        jsonModelForTicketDetails?.CorrectiveActions = CorrectiveActions
        jsonModelForTicketDetails?.RepairRequestNeeded = RepairRequestNeeded
        jsonModelForTicketDetails?.MaterialUsed = MaterialUsed
        // ...

        // Create and return an instance of your model class
        return JsonModelForTicketDetails(
            Status,
            CorrectIssueType,
            ScheduledVisitDate,
            CorrectiveActions,
            RepairRequestNeeded,
            MaterialUsed
        )
    }


    fun removeKeyFromJson(jsonString: String, keyToRemove: String): String {
        val jsonObject = JSONObject(jsonString)
        val modifiedJsonObject = JSONObject(jsonObject.toString())
        modifiedJsonObject.remove(keyToRemove)
        return modifiedJsonObject.toString()
    }

    private fun subcribeToUpdateTicket() {
        viewModel.createTicket.observe(this, Event.EventObserver(
            onError = {
                binding.loadingView.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverrError:$it ")
//                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.loadingView.visibility = View.VISIBLE
            }, { ticketResponse ->
                binding.loadingView.visibility = View.GONE
                if (ticketResponse.status == 200) {
                    Global.successmessagetoast(requireContext(), "Updated Successfully")
                    dialog?.dismiss()
                    (this.parentFragment as DetailsTicketFragment?)?.refreshTicktDetails()
                    //  apiDataForTypes= ticketResponse.data[0]
                    //   setData(ticketResponse.data[0])
                    //   viewModel.getTypeTicket()
                    // activity?.onBackPressed()
//                    val fragmentManager: FragmentManager = parentFragmentManager
//                    fragmentManager.popBackStack()
//                    Global.TicketAssigntoID=""

                } else {
                    Global.warningdialogbox(requireContext(), ticketResponse.message)
                    Log.e(TAG, "subscribeToObserverrAPIERROR===>: ${ticketResponse.message}")
                }

            }


        ))
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
    var typesub: MutableList<DataQualityIssueSubCategory> = mutableListOf()

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
//                Global.warningmessagetoast(requireContext(), it)
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
                    //  binding.issueCategorySpinner.adapter = adapter

//                    binding.issueCategorySpinner.onItemSelectedListener = object :
//                        AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View, position: Int, id: Long
//                        ) {
////                            Toast.makeText(
////                                this@AddTicketActivity,
////                                nameTypeTicket[position], Toast.LENGTH_SHORT
////                            ).show()
//                            typeval = nameTypeTicket[position]
//                            typevalCode = typeListAnd[position].id.toString()
//
//                            Log.e(TAG, "onItemSelected: $typeval $typevalCode")
//
////                            var hash = HashMap<String, String>()
////                            hash["Type"] = typeval
////                            viewModel.getSubType(hash)
//                            var data=BodyForIssueSubCategory(
//                                fields = listOf("id","Title","IssueCategory","CreatedBy","CreatedDate","CreatedTime")
//                            , filter =  Filter(IssueCategory = typevalCode)
//                            )
//
//                            viewModel.getQualityIssueSubCategory(data)
//
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//                            typeval = nameTypeTicket[0]
//                            typevalCode = typeListAnd[0].id.toString()
//                            // write code to perform some action
//                        }
//                    }

                } else {
                    Log.e(TAG, "subscribeToObserverERROR: ${response.message}")
                    Global.warningmessagetoast(requireContext(), response.message)
                }

            }
        ))


    }

    private fun postResponse() {
        viewModel.addQuality.observe(this, Event.EventObserver(
            onError = {
                binding.btnSave.isEnabled = false
                binding.btnCancel.isEnabled = false
                binding.loadingView.visibility = View.GONE
                Log.e(TAG, "ADDQUALITY===>: $it")
//                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.btnSave.isEnabled = false
                binding.btnCancel.isEnabled = false
                binding.loadingView.visibility = View.VISIBLE
            }, { response ->
                Log.e(TAG, "subscribeToObserverDialog: ")
                binding.btnSave.isEnabled = true
                binding.btnCancel.isEnabled = true
                binding.loadingView.visibility = View.GONE
                if (response.status.equals(200)) {
                    //binding.description.setText("")
                    // binding.description.clearComposingText()
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

                } else {
                    Log.e(TAG, "ADDQUALITYAPI===>: ${response.message}")
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