package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.AddContactPerson
import com.ahuja.sons.activity.AddServiceContractActivty
import com.ahuja.sons.`interface`.SelectBusinessPartneer
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.activity.EditTicketActivity
import com.ahuja.sons.adapter.SelectAccountAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.SelectDepartmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.DataCustomerListForContact
import com.ahuja.sons.viewmodel.MainViewModel

class SelectBusinessPartnerFragement(val addTicketActivity: AddTicketActivity, val editTicketActivity: EditTicketActivity ,val addContact: AddContactPerson, val flag : String, val addServiceContractActivty: AddServiceContractActivty) : Fragment() {


    lateinit var adapter: SelectAccountAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var selectBusinessPartner: SelectBusinessPartneer
    lateinit var viewModel: MainViewModel
    private lateinit var ticketFragment: SelectDepartmentBinding


   /* constructor(addTicketActivity: AddTicketActivity) : this(this, "AddTicketContext") {
        this.selectBusinessPartner = addTicketActivity
    }

    constructor(addServiceContractActivty: AddServiceContractActivty) : this {
        this.selectBusinessPartner = addServiceContractActivty
    }

    constructor(addcp: AddContactPerson) : this(this){
        this.selectBusinessPartner = addcp
    }
*/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ticketFragment = SelectDepartmentBinding.inflate(layoutInflater)

        //todo set interface context as per activity selecetd
        if (flag == "AddTicket"){
            this.selectBusinessPartner = addTicketActivity
            viewModel = (activity as AddTicketActivity).viewModel

        }
        else if (flag == "AddContact"){
            this.selectBusinessPartner = addContact
            viewModel = (activity as AddContactPerson).viewModel

        }
        else if (flag == "EditTicket"){
            this.selectBusinessPartner = editTicketActivity
            viewModel = (activity as EditTicketActivity).viewModel
        }
        else{
            this.selectBusinessPartner = addServiceContractActivty
            viewModel = (activity as AddServiceContractActivty).viewModel

        }

        if (Global.checkForInternet(requireContext())) {
            viewModel.getCustomerListForContact()

//            viewModel.getAllBPList() //todo change selected BP name list---
            bindObserver()
            ticketFragment.loadingView.start()
        }

        ticketFragment.toolbarview.heading.text = "Select BusinessPartner"

        ticketFragment.toolbarview.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        return ticketFragment.root
    }

//    var AllitemsList = ArrayList<AccountBpData>()
    var AllitemsList = ArrayList<DataCustomerListForContact>()

    //todo bind observer...
    private fun bindObserver() {
        viewModel.customerListContact.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(requireContext(), it)
                ticketFragment.loadingView.stop()
            },
            onLoading = {
                ticketFragment.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    ticketFragment.loadingView.stop()

                    if (response.data != null) {
                        AllitemsList.clear()
                        AllitemsList.addAll(response.data)
                        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = context?.let {
                            SelectAccountAdapter(it, selectBusinessPartner, AllitemsList)
                        }!!
                        ticketFragment.recyclerview.layoutManager = linearLayoutManager
                        ticketFragment.recyclerview.adapter = adapter
                        adapter.notifyDataSetChanged()
                        ticketFragment.nodatafound.isVisible = adapter.itemCount == 0

                        Log.e("data", response.data.toString())
                    }
                } else {
                    Global.warningmessagetoast(requireContext(), response.message)
                }
            }

        ))


    }


}
