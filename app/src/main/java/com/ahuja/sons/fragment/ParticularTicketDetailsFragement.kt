package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.adapter.SelectEmployeeAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.ParticulartTicketInformationBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.viewmodel.MainViewModel

class ParticularTicketDetailsFragement(val tickid: Int) : Fragment() {

    lateinit var adapter: SelectEmployeeAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel: MainViewModel
    private lateinit var ticketFragment: ParticulartTicketInformationBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ticketFragment = ParticulartTicketInformationBinding.inflate(layoutInflater)
        viewModel = (activity as TicketDetailsActivity).viewModel

        ticketFragment.toolbarview.heading.text = "Ticket Details"

        if (Global.checkForInternet(requireContext())) {

            val tickethistory = HashMap<String, Int>()
            tickethistory["id"] = tickid
            viewModel.particularTicketDetails(tickethistory)
            bindTicketDetailObserver()

        }

        ticketFragment.toolbarview.backPress.setOnClickListener {
            requireActivity().onBackPressed()
        }


        ticketFragment.toolbarview.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        return ticketFragment.root
    }

    //todo observer for ticket detail.

    private fun bindTicketDetailObserver() {
        viewModel.allItemWiseTicket.observe(
            viewLifecycleOwner, Event.EventObserver(
                onError = {
                    Log.e(TAG, "onFailure: $it")
//                    Global.warningmessagetoast(requireContext(), it)
                },
                onLoading = {
                },
                onSuccess = { response ->
                    if (response.status == 200) {
                        setData(response.data[0])
                    } else {
                        response.message?.let { Global.warningmessagetoast(requireContext(), it) }
                    }

                })
        )
    }


    private fun setData(ticketdata: TicketData) {
        ticketFragment.contactPersonValue.text = ticketdata.ContactName
        ticketFragment.duedateValue.text = ticketdata.DueDate
        ticketFragment.emailValue.text = ticketdata.ContactEmail
        ticketFragment.phoneNumber.text = ticketdata.ContactPhone
        ticketFragment.assignedValue.text = ticketdata.AssignToDetails[0].SalesEmployeeName
        ticketFragment.description.text = ticketdata.Description
        if (ticketdata.BusinessPartner[0].BPAddresses?.isNotEmpty()!!) {
            ticketFragment.address.text =
                ticketdata.BusinessPartner[0].BPAddresses[0].Street + ", " + ticketdata.BusinessPartner[0].BPAddresses[0].City
        }
        ticketFragment.createdby.text = ticketdata.CreatedByDetails[0].SalesEmployeeName
        ticketFragment.starttimeValue.text = ticketdata.TicketStartDate
        ticketFragment.endtimeValue.text = ticketdata.TicketEndDate
        ticketFragment.productname.text = ticketdata.ProductName
        ticketFragment.productcategory.text = ticketdata.ProductCategoryName
        ticketFragment.orderNo.text = ticketdata.DeliveryID
        ticketFragment.warrantydate.text = ticketdata.WarrantyDueDate
        ticketFragment.extWarranty.text = ticketdata.ExtWarrantyDueDate
        ticketFragment.amcDate.text = ticketdata.AMCDueDate
        ticketFragment.cmcDate.text = ticketdata.CMCDueDate
    }


}

private const val TAG = "ParticularTicketDetails"