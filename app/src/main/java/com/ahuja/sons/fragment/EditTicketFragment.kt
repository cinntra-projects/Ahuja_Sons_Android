package com.ahuja.sons.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.adapter.TypeAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.EditTicketDetailsBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.*
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.adapter.SelectedItemAdapter
import com.ahuja.sons.`interface`.ContactItemSelect
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.newapimodel.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.MutableList
import kotlin.collections.indexOf
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableListOf
import kotlin.collections.set


class EditTicketFragment : Fragment() , ContactItemSelect{
    lateinit var viewModel: MainViewModel

    private lateinit var binding: EditTicketDetailsBinding

    // var ticketdata= TicketDataModel()
    var parcelTicketData = TicketData()

    var ticketdata = TicketData()
    var ticketOneData_gl = TicketData()

    var statusval = "--None--"
    var priorityval = ""
    var typeval = ""
    var subTypeSPinner = ""
    var zoneval = "South"
    var CatID = ""
    var zoneList = ArrayList<BPLID>()
    var typelist = ArrayList<BPLID>()
    var priorityList = ArrayList<BPLID>()
    var nameTypeTicket = mutableListOf<String>()
    val nameSubTypeTicket = mutableListOf<String>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.findViewById<AppBarLayout>(R.id.appbar)?.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        activity?.findViewById<AppBarLayout>(R.id.appbar)?.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = EditTicketDetailsBinding.inflate(layoutInflater)
        viewModel = (activity as TicketDetailsActivity).viewModel
        //  ticketdata = arguments?.getParcelable<TicketData>(Global.TicketData)!!

//        parcelTicketData = arguments?.getSerializable()<TicketData>(Global.TicketData)!!

        binding.addTicket.duedateValue.setOnClickListener {
            Global.selectDate(requireContext(), binding.addTicket.duedateValue)
        }


        binding.addTicket.createAndUpdateTicketBtn.visibility = View.GONE

        binding.toolbar.heading.text = "Edit Tickets"
        binding.toolbar.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        if (Global.checkForInternet(requireContext())) {
            var hashMap = HashMap<String, String>()
            hashMap.put("id", parcelTicketData.id.toString())
            viewModel.getTicketOne(hashMap)
            subscribeToObserverr()
            //callZoneApi()
        }

        eventmanager()

        //  setData()
        disablekeys()

        binding.update.setOnClickListener {
            updateTicketsData()
        }

        return binding.root
    }

    lateinit var spinnerArrayAdapter: TypeAdapter
    lateinit var zoneArrayAdapter: ArrayAdapter<String>
    lateinit var statusArrayAdapter: ArrayAdapter<String>
    lateinit var priorityArrayAdapter: ArrayAdapter<String>

    companion object {
        private const val TAG = "EditTicketFragment"
    }


    private fun subscribeToObserverr() {
        viewModel.particularTicket.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverrError:$it ")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.loadingback.visibility = View.VISIBLE
            }, { ticketResponse ->
                binding.loadingback.visibility = View.GONE
                if (ticketResponse.status == 200) {
                    ticketOneData_gl = ticketResponse.data[0]
                    setData(ticketResponse.data[0])
                    viewModel.getTypeTicket()


                } else {
                    Global.warningdialogbox(requireContext(), ticketResponse.message)
                    Log.e(TAG, "subscribeToObserverrAPIERROR===>: ${ticketResponse.message}")
                }

            }


        ))




        viewModel.typeTicket.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {

            }, { type ->
                Log.e(TAG, "TYPE>>>>>>: ")
                if (type.status == 200) {
                    var typeListAnd = type.data

                    for (typical in typeListAnd) {
                        nameTypeTicket.add(typical.Type)
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_row, nameTypeTicket
                    )
                    binding.addTicket.channelDropdown.adapter = adapter
                    binding.addTicket.channelDropdown.setSelection(
                        getTicketTypePos(
                            nameTypeTicket,
                            ticketOneData_gl.Type
                        )
                    )



                    binding.addTicket.channelDropdown.onItemSelectedListener = object :
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
                            nameSubTypeTicket.clear()
                            var hash = HashMap<String, String>()
                            hash["Type"] = typeval
                            viewModel.getSubType(hash)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }

                } else {
                    Global.warningmessagetoast(requireContext(), type.message)
                }
            }

        ))

        viewModel.subTypeTicket.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {

            }, { subType ->
                Log.e(TAG, "SUBTYPETYPE>>>>>>: ")
                if (subType.status == 200) {
                    if (subType.data.isNotEmpty()) {
                        val typeListAnd = subType.data



                        for (typical in typeListAnd) {
                            nameSubTypeTicket.add(typical.SubType)
                        }
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_row, nameSubTypeTicket
                    )
                    binding.addTicket.spinnerSubType.adapter = adapter
                    binding.addTicket.spinnerSubType.setSelection(
                        getSubTicketTypePos(
                            nameSubTypeTicket,
                            ticketOneData_gl.SubType
                        )
                    )

                    binding.addTicket.spinnerSubType.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View, position: Int, id: Long
                        ) {
//                            Toast.makeText(
//                                this@AddTicketActivity,
//                                nameTypeTicket[position], Toast.LENGTH_SHORT
//                            ).show()
                            if (nameSubTypeTicket.isNotEmpty()) {
                                subTypeSPinner = nameSubTypeTicket[position]
                            }

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }
                } else {
                    Global.warningmessagetoast(requireContext(), subType.message)
                }
            }

        ))


    }

    private fun eventmanager() {

        val priorityList = resources.getStringArray(R.array.priority_list)
        statusArrayAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.ticket_status)
        )

        statusArrayAdapter.setDropDownViewResource(R.layout.dropdownview)


        binding.addTicket.statusdropdown.adapter = statusArrayAdapter


//        zoneArrayAdapter = ArrayAdapter<String>(
//            requireContext(), android.R.layout.simple_spinner_item,
//            resources.getStringArray(R.array.zone_list)
//        )
//
//        zoneArrayAdapter.setDropDownViewResource(R.layout.dropdownview)
//
//
//        binding.addTicket.zonedropdown.adapter = zoneArrayAdapter

//
//        priorityArrayAdapter = ArrayAdapter<String>(
//            requireContext(), android.R.layout.simple_spinner_item,
//          priorityList
//        )
//
//        priorityArrayAdapter.setDropDownViewResource(R.layout.dropdownview)
//
//
//        binding.addTicket.prioritySpinner.adapter = priorityArrayAdapter


        binding.addTicket.channelDropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (typelist.isNotEmpty()) {
                    typeval = typelist[p2].getType()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                if (typelist.isNotEmpty()) {
                    typeval = typelist[0].getType()
                }
            }

        }

//        binding.addTicket.zonedropdown.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                if (zoneList.isNotEmpty()) {
//                    zoneval = zoneList[p2].getZone()
//                }
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                if (zoneList.isNotEmpty()) {
//                    zoneval = zoneList[0].getZone()
//                }
//            }
//
//        }

        binding.addTicket.statusdropdown.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                statusval = statusArrayAdapter.getItem(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                statusval = statusArrayAdapter.getItem(0).toString()
            }

        }


        //TODO select priority from static list..
        binding.addTicket.prioritySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    priorityval = parent!!.selectedItem.toString()
                    Log.e(TAG, "onItemSelectedproio====?: $priorityval")

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    priorityval = parent!!.selectedItem.toString()
                }

            }


        //TODO select priority from static list..
        binding.addTicket.zonedropdown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    zoneval = parent!!.selectedItem.toString()
                    Log.e(TAG, "onItemSelectedzone====?: $zoneval")

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    zoneval = parent!!.selectedItem.toString()
                }

            }


    }


    private fun updateTicketsData() {
        if (validation(
                binding.addTicket.contacnameValue.text.toString(),
                binding.addTicket.orderValue.text.toString(),
                binding.addTicket.businesspartnerValue.text.toString(),
                binding.addTicket.assignedValue
            )
        ) {
            var hashMap = HashMap<String, Any>()
            hashMap["id"] = ticketOneData_gl.id
            hashMap["Type"] = typeval
            hashMap["Type"] = typeval
            hashMap["Title"] = binding.addTicket.subject.text.toString()
            hashMap["ContactPhone"] = "${binding.addTicket.phoneNumber.text.toString()}"
            hashMap["ContactEmail"] = binding.addTicket.email.text.toString()
            hashMap["Zone"] = zoneval
            hashMap["Description"] = binding.addTicket.description.text.toString()
            hashMap["DueDate"] = binding.addTicket.duedateValue.text.toString()
            hashMap["CountryCode"] =
                binding.addTicket.countryPickerPhone.selectedCountryCodeWithPlus
            hashMap["CountryCode1"] =
                binding.addTicket.countryPickerAlternate.selectedCountryCodeWithPlus
            hashMap["AlternatePhone"] = "${binding.addTicket.alternatephoneNumber.text.toString()}"
            hashMap["SubType"] = subTypeSPinner
            hashMap["Data"] = ticketOneData_gl.Data

            val tdm = BodyAddTicketData(
                id = ticketOneData_gl.id,
                CMCDueDate = ticketOneData_gl.CMCDueDate,
                ContactName = binding.addTicket.contacnameValue.text.toString(),
                DeliveryID = binding.addTicket.orderValue.text.toString(),
                AssignTo = Global.TicketAssigntoID,
                CreatedBy = Prefs.getString(Global.Employee_Code, ""),
                Type = typeval,
                Title = binding.addTicket.subject.text.toString(),
                BpCardCode = ticketOneData_gl.BpCardCode,
                ContactAddress = binding.addTicket.address.text.toString(),
                ProductSerialNo = binding.addTicket.tvProductSerialNumber.text.toString(),
                ProductName = binding.addTicket.itemCodeValue.text.toString(),
                ProductCategory = ticketOneData_gl.ProductCategory,
                ProductModelNo = ticketOneData_gl.ProductModelNo,
                Zone = zoneval,
                Description = binding.addTicket.description.text.toString(),
                DurationOfService = ticketOneData_gl.DurationOfService,
                SignatureStatus = ticketOneData_gl.SignatureStatus,
                WarrantyStartDate = ticketOneData_gl.WarrantyStartDate,
                WarrantyDueDate = ticketOneData_gl.WarrantyDueDate,
                ExtWarrantyStartDate = ticketOneData_gl.ExtWarrantyStartDate,
                ExtWarrantyDueDate = ticketOneData_gl.ExtWarrantyDueDate,
                AMCStartDate = ticketOneData_gl.AMCStartDate,
                AMCDueDate = ticketOneData_gl.AMCDueDate,
                CMCStartDate = ticketOneData_gl.CMCStartDate,
                CreateDate = "${Global.getTodayDate()} ${Global.getfullformatCurrentTime()}",
                ClosedDate = binding.addTicket.duedateValue.text.toString(),
                DueDate = binding.addTicket.duedateValue.text.toString(),
                Datetime = Global.getTimeStamp(),
                ManufacturingDate = ticketOneData_gl.ManufacturingDate,
                ExpiryDate = ticketOneData_gl.ExpiryDate,
                ContactEmail = binding.addTicket.email.text.toString(),
                ContactPhone = "${binding.addTicket.phoneNumber.text.toString()}",
                Status = ticketOneData_gl.Status,
                Priority = priorityval,
                AlternatePhone = "${binding.addTicket.alternatephoneNumber.text.toString()}",
                CountryCode = binding.addTicket.countryPickerPhone.selectedCountryCodeWithPlus,
                CountryCode1 = binding.addTicket.countryPickerAlternate.selectedCountryCodeWithPlus,
                BpBranch = ticketOneData_gl.BpBranch,
                SubType = subTypeSPinner,


                )
            if (Global.checkForInternet(requireContext())) {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()
//                viewModel.updateParticularTicket(hashMap)//todo comment
                subcribeToUpdateTicket()
                //    callUpdateTicketApi(tdm)
            }
        }

    }


    private fun subcribeToUpdateTicket() {
        viewModel.createTicket.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                Log.e(TAG, "subscribeToObserverrError:$it ")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                binding.loadingback.visibility = View.VISIBLE
            }, { ticketResponse ->
                binding.loadingback.visibility = View.GONE
                if (ticketResponse.status == 200) {
                    Global.successmessagetoast(requireContext(), "Updated Successfully")
                    //  apiDataForTypes= ticketResponse.data[0]
                    //   setData(ticketResponse.data[0])
                    //   viewModel.getTypeTicket()
                    // activity?.onBackPressed()
                    val fragmentManager: FragmentManager = parentFragmentManager
                    fragmentManager.popBackStack()
                    Global.TicketAssigntoID = ""

                } else {
                    Global.warningdialogbox(requireContext(), ticketResponse.message)
                    Log.e(TAG, "subscribeToObserverrAPIERROR===>: ${ticketResponse.message}")
                }

            }


        ))
    }


    private fun validation(
        contactname: String,
        orderval: String,
        serialnum: String,
        assignedTo: EditText
    ): Boolean {
        if (contactname.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Contact Details", Toast.LENGTH_SHORT).show()
            return false
        } else if (orderval.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Order Details", Toast.LENGTH_SHORT).show()
            return false
        } else if (serialnum.isEmpty()) {
            Toast.makeText(requireContext(), "Enter Business Partner", Toast.LENGTH_SHORT).show()
            return false
        } else if (assignedTo.text.isEmpty()) {
            assignedTo.requestFocus()
            assignedTo.error = "Can't Be Empty"
            Toast.makeText(requireContext(), "Choose Assigned To ", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun getPriorityPos(value: String): Int {
        var pos = -1
        for (sd in priorityList) {
            if (sd.equals(value)) {
                pos = priorityList.indexOf(sd)
                break
            }
        }
        return pos
    }


    private fun filterlist(value: ArrayList<ItemAllListResponseModel.DataXXX>): List<ItemAllListResponseModel.DataXXX> {
        val tempList: MutableList<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
        for (installedItemModel in value) {
            if (!installedItemModel.ItemName.equals("admin")) {
                tempList.add(installedItemModel)
            }
        }
        return tempList
    }

    lateinit var contactfragement: SelectDepartmentFragement
    var contactlist = ArrayList<DataXX>()
    var branchAllList = ArrayList<BranchAllListResponseModel.DataXXX>()

    var selectedDataList = ArrayList<ItemAllListResponseModel.DataXXX>()

    private fun setData(ticketData: TicketData) {
        Log.e(TAG, "setData>>>>>: ")
        statusval = ticketData.Status
        priorityval = ticketData.Priority
        Global.TicketAssigntoID = ticketData.AssignTo


        //todo item list bind as per ticket one api--
        var SalesEmployeeList: List<ItemAllListResponseModel.DataXXX> = ArrayList<ItemAllListResponseModel.DataXXX>()
        SalesEmployeeList = filterlist(ticketData.TicketItems)

        selectedDataList.clear()
        selectedDataList.addAll(SalesEmployeeList)
        if (selectedDataList != null && selectedDataList.size > 0) {
            binding.addTicket.rvItemNames.visibility = View.VISIBLE
        } else {
            binding.addTicket.rvItemNames.visibility = View.GONE
        }
        Log.e("selected", "onItemClick: " + selectedDataList.size)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        val adapterEmp = SelectedItemAdapter(requireActivity(), selectedDataList, "")
        binding.addTicket.rvItemNames.layoutManager = gridLayoutManager
        binding.addTicket.rvItemNames.adapter = adapterEmp
        adapterEmp.notifyDataSetChanged()



        //todo contact name selectable--

        contactlist.clear()


        //todo calling contact name api list here---

        var jsonObject = JsonObject()
        jsonObject.addProperty("CardCode", ticketData.CardCode)
        viewModel.getContactNameList(jsonObject)
        bindContactNameObserver()


        binding.addTicket.contacnameValue.setOnClickListener {
            if (binding.addTicket.businesspartnerValue.length() == 0) {
                Global.warningmessagetoast(requireContext(), "Select Business Partner")
            } else {
//                contactfragement = SelectDepartmentFragement(binding.addTicket.contacnameValue, contactlist, requireActivity())
                // args.putStringArrayList("data", position)
                requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.add(R.id.container, contactfragement).addToBackStack(null)
                transaction.commit()
            }

        }


        typeval = ticketData.Type
        CatID = ticketData.ProductCategory

        binding.addTicket.statusdropdown.setSelection(statusArrayAdapter.getPosition(statusval))
        //  binding.addTicket.zonedropdown.setSelection(getCurrentZonePos(zoneList, ticketData.Zone))

        Log.e(TAG, "setData: 1==> ${ticketData.CountryCode} 2====>${ticketData.CountryCode1}")
        binding.addTicket.countryPickerPhone.setCountryForPhoneCode(ticketData.CountryCode.toInt())
        if (ticketData.CountryCode1.isNotEmpty()) {
            binding.addTicket.countryPickerAlternate.setCountryForPhoneCode(ticketData.CountryCode1.toInt())
        } else {
            binding.addTicket.countryPickerAlternate.setCountryForPhoneCode(+971)
        }


        zoneval = ticketData.Zone
        val zoneSelectedValue = ticketData.Zone
        val prioritySelectedValue = ticketData.Priority// Replace with your actual selected value
        Log.e(TAG, "setData: $prioritySelectedValue")

        val position = resources.getStringArray(R.array.zone_list).indexOf(zoneSelectedValue)
        if (position != -1) {
            binding.addTicket.zonedropdown.setSelection(position)
        } else {
            // String value not found, handle accordingly
        }

        val positionPrior =
            resources.getStringArray(R.array.priority_list).indexOf(prioritySelectedValue)
        if (position != -1) {
            Log.e(TAG, "setDataPos==>: $positionPrior")
            binding.addTicket.prioritySpinner.setSelection(positionPrior)
        } else {
            // String value not found, handle accordingly
        }

        //   binding.addTicket.prioritySpinner.setSelection(getPriorityPos(priorityval))


        binding.addTicket.alternatephoneNumber.setText(ticketData.AlternatePhone)
        binding.addTicket.email.setText(ticketData.ContactEmail)
        binding.addTicket.phoneNumber.setText(ticketData.ContactPhone)
        binding.addTicket.itemCodeValue.setText(ticketData.ProductName)
        binding.addTicket.category.setText(ticketData.ProductCategoryName)
        binding.addTicket.tvProductSerialNumber.setText(ticketData.ProductSerialNo)
        binding.addTicket.duedateValue.setText(ticketData.DueDate)
        binding.addTicket.contacnameValue.setText(ticketData.ContactName)
        binding.addTicket.businesspartnerValue.setText(ticketData.BusinessPartner[0].CardName)
        //  binding.addTicket.phoneNumber.setText(ticketdata.BusinessPartner.Phone1)
        binding.addTicket.accountName.setText(ticketData.BusinessPartner[0].U_ACCNT)
        binding.addTicket.orderValue.setText(ticketData.DeliveryID)
        binding.addTicket.description.setText(ticketData.Description)
        binding.addTicket.address.setText(ticketData.ContactAddress)
        binding.addTicket.assignedValue.setText(ticketData.AssignToDetails[0].SalesEmployeeName)

        binding.addTicket.subject.setText(ticketData.Title)
        binding.addTicket.acScopeWork.setText(ticketData.Type)
        binding.addTicket.acRequestType.setText(ticketData.SubType)
        binding.addTicket.acCaseOrigin.setText(ticketData.CaseOrigin)



        binding.loadingback.visibility = View.GONE


    }


    //todo contact name observer--
    private fun bindContactNameObserver() {
        viewModel.contactNameList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e("fail==>", it.toString())
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200 && it.data.isNotEmpty()) {
                    Log.e("response", it.data.toString())
                    contactlist.clear()
                    contactlist.addAll(it.data)

                }else {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


    var contactName = ""
    //todo set text while select contact item--call override function
    override fun selectContactItem(bpdata: DataXX) {
        binding.addTicket.email.setText(bpdata.E_Mail)
        binding.addTicket.phoneNumber.setText(bpdata.MobilePhone)
        contactName = bpdata.FirstName.toString()
    }


    private fun disablekeys() {

        /**********Clickable false ****************/
        binding.addTicket.businesspartnerValue.isClickable = false
        binding.addTicket.orderValue.isClickable = false
        binding.addTicket.itemCodeValue.isClickable = false

//        binding.addTicket.contacnameValue.isClickable = false
        binding.addTicket.category.isClickable = false
//        binding.addTicket.assignedValue.isClickable = false
//        binding.addTicket.zonedropdown.isEnabled = true
//        binding.addTicket.channelDropdown.isEnabled = true

        /**********Focusable false ****************/
        binding.addTicket.businesspartnerValue.isFocusable = false
        binding.addTicket.orderValue.isFocusable = false
//        binding.addTicket.contacnameValue.isFocusable = false
        binding.addTicket.itemCodeValue.isFocusable = false
        binding.addTicket.category.isFocusable = false
//        binding.addTicket.assignedValue.isFocusable = true
    }




    private fun getZonepos(zoneList: ArrayList<BPLID>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.getZone() == code) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getCurrentZonePos(zoneList: ArrayList<String>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.equals(code, ignoreCase = true)) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }


    private fun getTicketTypePos(zoneList: MutableList<String>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.equals(code, ignoreCase = true)) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getSubTicketTypePos(zoneList: MutableList<String>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.equals(code, ignoreCase = true)) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getTypepos(zoneList: ArrayList<BPLID>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.getType() == code) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }

    private fun getprioritypos(zoneList: ArrayList<BPLID>, code: String): Int {
        var pos = -1
        for (i in zoneList) {
            if (i.getPriority() == code) {
                pos = zoneList.indexOf(i)
            }
        }
        return pos;
    }



}
