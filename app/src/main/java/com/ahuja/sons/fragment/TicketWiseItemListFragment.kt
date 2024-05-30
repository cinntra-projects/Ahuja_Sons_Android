package com.ahuja.sons.fragment

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.activity.*
import com.ahuja.sons.adapter.TicketWiseItemAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.AddItemRemarkLayoutBinding
import com.ahuja.sons.databinding.FragmentTicketWiseItemListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.viewmodel.MainViewModel

class TicketWiseItemListFragment(var ticketID: TicketData) : Fragment() {

    lateinit var ticketbiding: FragmentTicketWiseItemListBinding
    private lateinit var ticketdata: TicketData
    lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        ticketbiding = FragmentTicketWiseItemListBinding.inflate(layoutInflater)
        return ticketbiding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as TicketDetailsActivity).viewModel
        ticketdata = ticketID

        Log.e(TAG, "onViewCreated:  calling for ticketData")

        //todo calling ticket one api---
        var hashMap = java.util.HashMap<String, String>()
        hashMap["id"] = ticketdata.id.toString()
        viewModel.getTicketOne(hashMap)
        subscribeToObserver()

        if (Global.checkForInternet(requireContext())) {

            Log.e(TAG, "onViewCreated: view set", )
            if (ticketdata.Type == "Site Survey" && ticketdata.is_SiteReported == false) { //Prefs.getString(Global.ITEM_FLAG, "") == "Site Survey"
                ticketbiding.rvTicketWiseItemList.visibility = View.GONE
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.ivNoFoundData.visibility = View.GONE
                ticketbiding.updateAndDownSiteSurveyReport.visibility = View.GONE
                ticketbiding.textView.visibility = View.GONE
                ticketbiding.createSiteSurveyReport.visibility = View.VISIBLE

            }
            else if (ticketdata.Type == "Site Survey" && ticketdata.is_SiteReported == true) { //Prefs.getString(Global.ITEM_FLAG, "") == "Site Survey"
                ticketbiding.rvTicketWiseItemList.visibility = View.GONE
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.ivNoFoundData.visibility = View.GONE
                ticketbiding.createSiteSurveyReport.visibility = View.GONE
                ticketbiding.textView.visibility = View.GONE
                ticketbiding.updateAndDownSiteSurveyReport.visibility = View.VISIBLE
            }
            else {
                ticketbiding.createSiteSurveyReport.visibility = View.GONE
                ticketbiding.textView.visibility = View.VISIBLE
                //todo ticket by item list--
                var jsonObject = JsonObject()
                jsonObject.addProperty("TicketId", ticketdata.id)
                viewModel.getItemsByTicket(jsonObject)
                bindItemListObserver()
            }

        }


        ticketbiding.createSiteSurveyReport.setOnClickListener {
            when (ticketdata.TicketStatus) {
                "Accepted" -> {
                    if (ticketdata.Status == "Closed"){
                        Global.warningdialogbox(requireContext(), "Your Ticket is Closed")
                    }
                    else if (ticketdata.Status != "Resolved") {
                        if (ticketdata.Type == "Site Survey") {
                            val intent = Intent(context, TicketSiteSurveyTypeActivity::class.java)
//                            intent.putExtra("SiteSurvey", data as java.io.Serializable)
                            intent.putExtra("ticketType", ticketdata.Type)
                            intent.putExtra("ticketData", ticketdata)
                            startActivity(intent)
                        } else {
                            com.ahuja.sons.globals.Global.warningmessagetoast(
                                requireContext(),
                                "Sorry! This Type has not Access to Create Report"
                            )
                        }

                    }
                    else if (ticketdata.Status == "Resolved") {
                        if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
                            if (ticketdata.Type == "Site Survey") {
                                val intent = Intent(context, TicketSiteSurveyTypeActivity::class.java)
                                intent.putExtra("ticketType", ticketdata.Type)
                                intent.putExtra("ticketData", ticketdata)
                                startActivity(intent)
                            } else {
                                com.ahuja.sons.globals.Global.warningmessagetoast(
                                    requireContext(), "Sorry! This Type has not Access to Create Report"
                                )
                            }
                        }else {
                            Global.warningdialogbox(requireContext(), "You do no have authentication to work on ticket")
                        }

                    }

                    else {
                        com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")
                    }
                }

                "Pending" -> {
                    com.ahuja.sons.globals.Global.warningdialogbox(
                        requireContext(),
                        "Your ticket is in pending state,Kindly accept it"
                    )
                }

                "Rejected" -> {
                    com.ahuja.sons.globals.Global.warningdialogbox(
                        requireContext(),
                        "Your ticket will be rejected"
                    )
                }
            }
        }


        ticketbiding.threeDotsLayout.setOnClickListener {
            if (ticketdata.is_SiteReported == true) {
                showUpdateReportPopupMenu(ticketbiding.threeDotsLayout)
            }
        }

        ticketbiding.downloadSiteSurveyReport.setOnClickListener {
            when (ticketdata.TicketStatus) {
                "Accepted" -> {
                    if (ticketdata.Status != "Closed") {

                        if (ticketdata.is_SiteReported == true) {
                            var pdf_url = ""
                            if (ticketdata.Type == "Site Survey") {

                                pdf_url = "${Global.SITE_SURVEY_TYPE_PDF_URL}${ticketdata.id}&ReportType=${ticketdata.Type}&ItemSerialNo=${""}&ItemCode=${""}"

                                Log.e(TAG, "onCreate: " + pdf_url)
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                                startActivity(browserIntent)
                            } else {
                                com.ahuja.sons.globals.Global.warningmessagetoast(
                                    requireContext(),
                                    "Sorry! This Type has not Access to Download Report"
                                )
                            }


                        } else {
                            Global.warningmessagetoast(
                                requireContext(),
                                "Your Report is not Created"
                            )
                        }

                    } else {
                        if (ticketdata.is_SiteReported == true) {
                            var pdf_url = ""
                            if (ticketdata.Type == "Site Survey") {

                                pdf_url = "${Global.SITE_SURVEY_TYPE_PDF_URL}${ticketdata.id}&ReportType=${ticketdata.Type}&ItemSerialNo=${""}&ItemCode=${""}"

                                Log.e(TAG, "onCreate: " + pdf_url)
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                                startActivity(browserIntent)
                            } else {
                                com.ahuja.sons.globals.Global.warningmessagetoast(
                                    requireContext(),
                                    "Sorry! This Type has not Access to Download Report"
                                )
                            }


                        } else {
                            Global.warningmessagetoast(
                                requireContext(),
                                "Your Report is not Created"
                            )
                        }
                    }
                }

                "Pending" -> {
                    Global.warningdialogbox(
                        requireContext(),
                        "Your ticket is in pending state,Kindly accept it"
                    )
                }

                "Rejected" -> {
                    Global.warningdialogbox(requireContext(), "Your ticket will be rejected")
                }
            }

        }
    }


    override fun onResume() {
        super.onResume()

        Log.e(TAG, "onResume:  Ticket Wise fragment calling---")
        if (Global.checkForInternet(requireContext())) {

            //todo calling ticket one api---
            var hashMap = java.util.HashMap<String, String>()
            hashMap["id"] = ticketdata.id.toString()
            viewModel.getTicketOne(hashMap)
            subscribeToObserver()

        }


    }


    var ticketOneData = TicketData()
    private fun subscribeToObserver() {
        viewModel.particularTicket.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(TAG, "errorInApi: $it")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {

            }, {
                if (it.status.equals(200)) {
                    ticketOneData = it.data[0]
                    Log.e(TAG, "responseSuccessful===> :  ${ticketOneData}")

                    ticketdata = ticketOneData

                    Log.e(TAG, "responseSuccessful2334===> :  ${ticketdata}")
                    if (ticketdata.Type == "Site Survey" && ticketdata.is_SiteReported == false) { //Prefs.getString(Global.ITEM_FLAG, "") == "Site Survey"
                        ticketbiding.rvTicketWiseItemList.visibility = View.GONE
                        ticketbiding.loadingback.visibility = View.GONE
                        ticketbiding.ivNoFoundData.visibility = View.GONE
                        ticketbiding.updateAndDownSiteSurveyReport.visibility = View.GONE
                        ticketbiding.createSiteSurveyReport.visibility = View.VISIBLE

                    }
                    else if (ticketdata.Type == "Site Survey" && ticketdata.is_SiteReported == true) { //Prefs.getString(Global.ITEM_FLAG, "") == "Site Survey"
                        ticketbiding.rvTicketWiseItemList.visibility = View.GONE
                        ticketbiding.loadingback.visibility = View.GONE
                        ticketbiding.ivNoFoundData.visibility = View.GONE
                        ticketbiding.createSiteSurveyReport.visibility = View.GONE
                        ticketbiding.updateAndDownSiteSurveyReport.visibility = View.VISIBLE

                    }
                    else {
                        ticketbiding.createSiteSurveyReport.visibility = View.GONE
                        //todo ticket by item list--
                        var jsonObject = JsonObject()
                        jsonObject.addProperty("TicketId", ticketdata.id)
                        viewModel.getItemsByTicket(jsonObject)
                        bindItemListObserver()
                    }


                } else {
                    Log.e(TAG, "responseError: ${it.message}")
                    Global.warningmessagetoast(requireContext(), it.message)
                }

            }
        ))
    }


    //todo bind item list observer--
    private fun bindItemListObserver() {
        viewModel.itemAllList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.progressbar.stop()
                Log.e(FileUtil.TAG, "errorInApi: $it")
                com.ahuja.sons.globals.Global.warningmessagetoast(
                    requireActivity(),
                    it
                )
            }, onLoading = {
                ticketbiding.loadingback.visibility = View.VISIBLE
                ticketbiding.progressbar.start()
            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        ticketbiding.loadingback.visibility = View.GONE
                        ticketbiding.progressbar.stop()

                        if (it.data.size > 0 && it.data != null) {
                            var itemAllListResponseList: ArrayList<ItemAllListResponseModel.DataXXX> =
                                ArrayList<ItemAllListResponseModel.DataXXX>()

                            itemAllListResponseList.clear()
                            itemAllListResponseList.addAll(it.data)
                            ticketbiding.rvTicketWiseItemList.visibility = View.VISIBLE
                            val layoutManager = LinearLayoutManager(requireContext())
                            var ticketWiseItemAdapter = TicketWiseItemAdapter(itemAllListResponseList)
                            ticketbiding.rvTicketWiseItemList.layoutManager = layoutManager
                            ticketbiding.rvTicketWiseItemList.adapter = ticketWiseItemAdapter


                            //todo on item click on create report open dialog--
                            ticketWiseItemAdapter.setOnTicketItemListener { data ->
                                when (ticketdata.TicketStatus) {
                                    "Accepted" -> {
                                        if (ticketdata.Status == "Closed"){
                                            Global.warningdialogbox(requireContext(), "Your Ticket is Closed")
                                        }
                                        else if (ticketdata.Status != "Resolved") {

                                            if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                            ) {
                                                val intent = Intent(context, TicketItemPreventiveMaintenanceActivity::class.java)
                                                intent.putExtra("data", data as java.io.Serializable)
                                                intent.putExtra("ticketType", ticketdata.Type)
                                                intent.putExtra("ticketData", ticketdata)
                                                startActivity(intent)

                                            } else if (ticketdata.Type == "Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "Shifting" || ticketdata.Type == "Packaging") {
                                                val intent = Intent(context, TicketInstallationTypeActivity::class.java)
                                                intent.putExtra("Installation", data as java.io.Serializable)
                                                intent.putExtra("ticketType", ticketdata.Type)
                                                intent.putExtra("ticketData", ticketdata)
                                                startActivity(intent)
                                            }/*else if (ticketdata.Type == "Site Survey") {
                                                val intent = Intent(context, TicketSiteSurveyTypeActivity::class.java)
                                                intent.putExtra("SiteSurvey", data as java.io.Serializable)
                                                intent.putExtra("ticketType", ticketdata.Type)
                                                intent.putExtra("ticketData", ticketdata)
                                                startActivity(intent)
                                            }*/
                                            else {
                                                com.ahuja.sons.globals.Global.warningmessagetoast(
                                                    requireContext(),
                                                    "Sorry! This Type has not Access to Create Report"
                                                )
                                            }

                                        }

                                        else if (ticketdata.Status == "Resolved") {
                                            if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
                                                if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                    || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                    || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                                ) {
                                                    val intent = Intent(context, TicketItemPreventiveMaintenanceActivity::class.java)
                                                    intent.putExtra("data", data as java.io.Serializable)
                                                    intent.putExtra("ticketType", ticketdata.Type)
                                                    intent.putExtra("ticketData", ticketdata)
                                                    startActivity(intent)

                                                } else if (ticketdata.Type == "Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "Shifting" || ticketdata.Type == "Packaging") {
                                                    val intent = Intent(context, TicketInstallationTypeActivity::class.java)
                                                    intent.putExtra("Installation", data as java.io.Serializable)
                                                    intent.putExtra("ticketType", ticketdata.Type)
                                                    intent.putExtra("ticketData", ticketdata)
                                                    startActivity(intent)
                                                }
                                                else {
                                                    com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Create Report")
                                                }
                                            }else {
                                                Global.warningdialogbox(requireContext(), "You do no have authentication to work on ticket")
                                            }

                                        }

                                        else {
                                            if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
                                                com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")

                                            }else{
                                                com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")

                                            }
                                        }
                                    }

                                    "Pending" -> {
                                        com.ahuja.sons.globals.Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket is in pending state,Kindly accept it"
                                        )
                                    }

                                    "Rejected" -> {
                                        com.ahuja.sons.globals.Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket will be rejected"
                                        )
                                    }
                                }

                            }


                            //todo on item click edit report open dialog--
                            ticketWiseItemAdapter.setOnEditItemClick { data ->
                                when (ticketdata.TicketStatus) {
                                    "Accepted" -> {
                                        if (ticketdata.Status == "Closed"){
                                            Global.warningdialogbox(requireContext(), "Your Ticket is Closed")
                                        }


                                        else if (ticketdata.Status != "Resolved") {

                                            if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) || Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true) )){
                                                if (ticketdata.Type == "Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "Shifting" || ticketdata.Type == "Packaging") {
                                                    val intent = Intent(context, EditInstallationTypeTicketActivity::class.java)
                                                    intent.putExtra("Installation", data as java.io.Serializable)
                                                    intent.putExtra("ticketType", ticketdata.Type)
                                                    intent.putExtra("ticketData", ticketdata)
                                                    startActivity(intent)
                                                } else if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                    || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                    || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                                ) {
                                                    val intent = Intent(context, EditPreventiveMaintenanceTicketActivity::class.java)
                                                    intent.putExtra("data", data as java.io.Serializable)
                                                    intent.putExtra("ticketType", ticketdata.Type)
//                                                    intent.putExtra("ticketData", ticketdata)
                                                    startActivity(intent)

                                                } /*else if (ticketdata.Type == "Site Survey") {
                                                val intent = Intent(context, EditSiteSurveyTypeActivity::class.java)
                                                intent.putExtra("SiteSurvey", data as java.io.Serializable)
                                                startActivity(intent)
                                            }*/ else {
                                                    com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Report")
                                                }
                                            }



                                        }


                                        else if (ticketdata.Status == "Resolved") {
                                            if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) )){
                                                if (ticketdata.Type == "Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "Shifting"
                                                    || ticketdata.Type == "Packaging"
                                                ) {
                                                    val intent = Intent(context, EditInstallationTypeTicketActivity::class.java)
                                                    intent.putExtra("Installation", data as java.io.Serializable)
                                                    intent.putExtra("ticketType", ticketdata.Type)
                                                    startActivity(intent)
                                                } else if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                    || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                    || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                                ) {
                                                    val intent = Intent(context, EditPreventiveMaintenanceTicketActivity::class.java)
                                                    intent.putExtra("data", data as java.io.Serializable)
                                                    intent.putExtra("ticketType", ticketdata.Type)
                                                    startActivity(intent)

                                                } /*else if (ticketdata.Type == "Site Survey") {
                                                val intent = Intent(context, EditSiteSurveyTypeActivity::class.java)
                                                intent.putExtra("SiteSurvey", data as java.io.Serializable)
                                                startActivity(intent)
                                            }*/ else {
                                                    com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Report")
                                                }
                                            }else {
                                                Global.warningdialogbox(requireContext(), "You do no have authentication to work on ticket")
                                            }

                                        }

                                        else {
                                            if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
                                                com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")

                                            }else{
                                                com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")

                                            }
                                        }
                                    }

                                    "Pending" -> {
                                        com.ahuja.sons.globals.Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket is in pending state,Kindly accept it"
                                        )
                                    }

                                    "Rejected" -> {
                                        com.ahuja.sons.globals.Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket will be rejected"
                                        )
                                    }
                                }

                            }


                            //todo on item click for attachment edit open dialog--
                            ticketWiseItemAdapter.setOnAttachItemClick { data ->
                                when (ticketdata.TicketStatus) {
                                    "Accepted" -> {
                                        if (ticketdata.Status == "Closed"){
                                            Global.warningdialogbox(requireContext(), "Your Ticket is Closed")
                                        }
                                        else if (ticketdata.Status != "Resolved") {

                                            if (data.is_Reported == true) {
                                                if (ticketdata.Type == "Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Shifting" || ticketdata.Type == "Packaging") {
                                                    val intent = Intent(context, AddAttachmentActivity::class.java)
                                                    intent.putExtra("data", data as java.io.Serializable)
                                                    intent.putExtra("Flag", ticketdata.Type)
                                                    startActivity(intent)
//                                                openItemRemarksDialog(data, "Installation")
                                                } else if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                    || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                    || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                                ) {
                                                    val intent = Intent(context, AddAttachmentActivity::class.java)
                                                    intent.putExtra("data", data as java.io.Serializable)
                                                    intent.putExtra("Flag", ticketdata.Type)
                                                    startActivity(intent)
                                                } /*else if (ticketdata.Type == "Site Survey") {
                                                    val intent = Intent(context, AddAttachmentActivity::class.java)
                                                    intent.putExtra("data", data as java.io.Serializable)
                                                    intent.putExtra("Flag", ticketdata.Type)
                                                    startActivity(intent)
                                                }*/ else {
                                                    com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Attachment")
                                                }

                                            } else {
                                                Global.warningmessagetoast(requireContext(), "Your Report is not Created")
                                            }

                                        }
                                        else if (ticketdata.Status == "Resolved") {
                                            if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true))){
                                                if (data.is_Reported == true) {
                                                    if (ticketdata.Type == "Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Shifting" || ticketdata.Type == "Packaging") {
                                                        val intent = Intent(context, AddAttachmentActivity::class.java)
                                                        intent.putExtra("data", data as java.io.Serializable)
                                                        intent.putExtra("Flag", ticketdata.Type)
                                                        startActivity(intent)
//                                                openItemRemarksDialog(data, "Installation")
                                                    } else if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                        || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                        || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                                    ) {
                                                        val intent = Intent(context, AddAttachmentActivity::class.java)
                                                        intent.putExtra("data", data as java.io.Serializable)
                                                        intent.putExtra("Flag", ticketdata.Type)
                                                        startActivity(intent)
                                                    }
                                                    else {
                                                        com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Attachment")
                                                    }

                                                } else {
                                                    Global.warningmessagetoast(requireContext(), "Your Report is not Created")
                                                }
                                            }else {
                                                Global.warningdialogbox(requireContext(), "You do no have authentication to work on ticket")
                                            }

                                        }

                                        else {
                                            if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
                                                com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")

                                            }else{
                                                com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")

                                            }
                                        }
                                    }

                                    "Pending" -> {
                                        Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket is in pending state,Kindly accept it"
                                        )
                                    }

                                    "Rejected" -> {
                                        Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket will be rejected"
                                        )
                                    }
                                }

                            }


                            //todo on item click for pdf open dialog--
                            ticketWiseItemAdapter.setOnPdfItemClick { data ->
                                when (ticketdata.TicketStatus) {
                                    "Accepted" -> {
                                        if (ticketdata.Status != "Closed") {

                                            if (data.is_Reported == true) {
                                                var pdf_url = ""

                                                if (ticketdata.Type == "Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "Shifting" || ticketdata.Type == "Packaging") {

                                                    pdf_url = "${Global.INSTALLATION_TYPE_PDF_URL}${ticketdata.id}&ReportType=${ticketdata.Type}&ItemSerialNo=${data.SerialNo}&ItemCode=${data.ItemCode}"

                                                    Log.e(TAG, "onCreate: " + pdf_url)
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                                                    startActivity(browserIntent)
                                                }
                                                else if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                    || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                    || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                                ) {

                                                    pdf_url = "${Global.MAINTAINANCE_TYPE_PDF_URL}${ticketdata.id}&ReportType=${ticketdata.Type}&ItemSerialNo=${data.SerialNo}&ItemCode=${data.ItemCode}"

                                                    Log.e(TAG, "onCreate: " + pdf_url)
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                                                    startActivity(browserIntent)

                                                } /*else if (ticketdata.Type == "Site Survey") {

                                                    pdf_url = "${Global.SITE_SURVEY_TYPE_PDF_URL}${ticketdata.id}&ReportType=${ticketdata.Type}&ItemSerialNo=${data.SerialNo}&ItemCode=${data.ItemCode}"

                                                    Log.e(TAG, "onCreate: "+pdf_url)
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                                                    startActivity(browserIntent)
                                                }*/ else {
                                                    com.ahuja.sons.globals.Global.warningmessagetoast(
                                                        requireContext(),
                                                        "Sorry! This Type has not Access to Download Report"
                                                    )
                                                }


                                            } else {
                                                Global.warningmessagetoast(
                                                    requireContext(),
                                                    "Your Report is not Created"
                                                )
                                            }

                                        }
                                        else {
                                            if (data.is_Reported == true) {
                                                var pdf_url = ""

                                                if (ticketdata.Type == "Installation" || ticketdata.Type == "De-Installation" || ticketdata.Type == "Re-Installation" || ticketdata.Type == "Shifting"
                                                    || ticketdata.Type == "Packaging"
                                                ) {

                                                    pdf_url = "${Global.INSTALLATION_TYPE_PDF_URL}${ticketdata.id}&ReportType=${ticketdata.Type}&ItemSerialNo=${data.SerialNo}&ItemCode=${data.ItemCode}"

                                                    Log.e(TAG, "onCreate: " + pdf_url)
                                                    val browserIntent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(pdf_url)
                                                    )
                                                    startActivity(browserIntent)
                                                } else if (ticketdata.Type == "Preventive Maintenance" || ticketdata.Type == "Servicing" || ticketdata.Type == "Breakdown" || ticketdata.Type == "Re-Visit Required"
                                                    || ticketdata.Type == "Extra Work" || ticketdata.Type == "Part change" || ticketdata.Type == "Gas Reflling" || ticketdata.Type == "Other"
                                                    || ticketdata.Type == "System Checking" || ticketdata.Type == "Water Testing"
                                                ) {

                                                    pdf_url =
                                                        "${Global.MAINTAINANCE_TYPE_PDF_URL}${ticketdata.id}&ReportType=${ticketdata.Type}&ItemSerialNo=${data.SerialNo}&ItemCode=${data.ItemCode}"

                                                    Log.e(TAG, "onCreate: " + pdf_url)
                                                    val browserIntent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(pdf_url)
                                                    )
                                                    startActivity(browserIntent)

                                                }  else {
                                                    com.ahuja.sons.globals.Global.warningmessagetoast(
                                                        requireContext(),
                                                        "Sorry! This Type has not Access to Download Report"
                                                    )
                                                }


                                            } else {
                                                Global.warningmessagetoast(
                                                    requireContext(),
                                                    "Your Report is not Created"
                                                )
                                            }
                                        }
                                    }

                                    "Pending" -> {
                                        Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket is in pending state,Kindly accept it"
                                        )
                                    }

                                    "Rejected" -> {
                                        Global.warningdialogbox(
                                            requireContext(),
                                            "Your ticket will be rejected"
                                        )
                                    }
                                }

                            }


                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        com.ahuja.sons.globals.Global.warningmessagetoast(
                            requireActivity(),
                            it.message!!
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }


    lateinit var rvAttachment: RecyclerView

    //todo item dialog open---
    private fun openItemRemarksDialog(dataModel: ItemAllListResponseModel.DataXXX, Flag: String) {
        val dialogbinding: AddItemRemarkLayoutBinding =
            AddItemRemarkLayoutBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogbinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER

        rvAttachment = dialog.findViewById(R.id.rvAttachment)

        dialogbinding.ivCrossDailog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window!!.attributes = lp
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }


    private fun showUpdateReportPopupMenu(view: View) {
        var popupMenu: PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.update_site_survey, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    when (ticketdata.TicketStatus) {
                        "Accepted" -> {
                            if (ticketdata.Status == "Closed"){
                                Global.warningdialogbox(requireContext(), "Your Ticket is Closed")
                            }

                            else if (ticketdata.Status != "Resolved") {
                                if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) || Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true) )){
                                    if (ticketdata.Type == "Site Survey") {
                                        val intent = Intent(context, EditSiteSurveyTypeActivity::class.java)
                                        intent.putExtra("ticketData", ticketdata)
                                        startActivity(intent)
                                    } else {
                                        com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Report")
                                    }
                                }

                            }

                            else if (ticketdata.Status == "Resolved") {
                                if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true))){
                                    if (ticketdata.Type == "Site Survey") {
                                        val intent = Intent(context, EditSiteSurveyTypeActivity::class.java)
                                        intent.putExtra("ticketData", ticketdata)
                                        startActivity(intent)
                                    } else {
                                        com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Report")
                                    }
                                }else {
                                    Global.warningdialogbox(requireContext(), "You do no have authentication to work on ticket")
                                }

                            }

                            else {
                                com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")
                            }

                        }

                        "Pending" -> {
                            com.ahuja.sons.globals.Global.warningdialogbox(requireContext(), "Your ticket is in pending state,Kindly accept it")
                        }

                        "Rejected" -> {
                            com.ahuja.sons.globals.Global.warningdialogbox(
                                requireContext(),
                                "Your ticket will be rejected"
                            )
                        }
                    }
                    true
                }
                R.id.attachment -> {
                    when (ticketdata.TicketStatus) {
                        "Accepted" -> {
                            if (ticketdata.Status == "Closed"){
                                Global.warningdialogbox(requireContext(), "Your Ticket is Closed")
                            }
                            else if (ticketdata.Status != "Resolved") {

                                if (ticketdata.is_SiteReported == true) {
                                    //todo calling report one api here for get report id ---

                                    if (ticketdata.Type == "Site Survey") {
                                        val intent = Intent(context, AddAttachmentActivity::class.java)
                                        intent.putExtra("data", ticketdata as java.io.Serializable)
                                        intent.putExtra("Flag", ticketdata.Type)
                                        startActivity(intent)
                                    } else {
                                        com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Attachment")
                                    }

                                } else {
                                    Global.warningmessagetoast(requireContext(), "Your Report is not Created")
                                }

                            }
                            else if (ticketdata.Status == "Resolved") {
                                if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true))){
                                    if (ticketdata.Type == "Site Survey") {
                                        val intent = Intent(context, AddAttachmentActivity::class.java)
                                        intent.putExtra("data", ticketdata as java.io.Serializable)
                                        intent.putExtra("Flag", ticketdata.Type)
                                        startActivity(intent)
                                    } else {
                                        com.ahuja.sons.globals.Global.warningmessagetoast(requireContext(), "Sorry! This Type has not Access to Update Attachment")
                                    }
                                }else {
                                    Global.warningdialogbox(requireContext(), "You do no have authentication to work on ticket")
                                }

                            }
                            else {
                                Global.warningmessagetoast(
                                    requireContext(),
                                    "Your Ticket is Resolved"
                                )
                            }
                        }

                        "Pending" -> {
                            Global.warningdialogbox(
                                requireContext(),
                                "Your ticket is in pending state,Kindly accept it"
                            )
                        }

                        "Rejected" -> {
                            Global.warningdialogbox(
                                requireContext(),
                                "Your ticket will be rejected"
                            )
                        }
                    }

                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


    companion object {
        private const val TAG = "TicketWiseItemListFragm"
    }
}