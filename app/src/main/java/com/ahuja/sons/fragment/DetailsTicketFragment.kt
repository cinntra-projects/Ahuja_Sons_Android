package com.ahuja.sons.fragment


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R

import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.adapter.AllAtachmentAdapter
import com.ahuja.sons.adapter.QualityInspectionAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.TicketInformationBinding
import com.ahuja.sons.dialogs.DialogQualityInstpection
import com.ahuja.sons.dialogs.DialogTicketTypeLogsDetials
import com.ahuja.sons.dialogs.DialogUpdateTicketDetails
import com.ahuja.sons.globals.Global
import com.ahuja.sons.jsonmodel.JsonModelForTicketDetails
import com.ahuja.sons.jsonmodel.ModelObjectForTicketDetails
import com.ahuja.sons.model.AllAttachmentResponse
import com.ahuja.sons.model.DataItem
import com.ahuja.sons.newapimodel.Filter
import com.ahuja.sons.newapimodel.PayLoadForInspectionList
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class DetailsTicketFragment(val ticketID: TicketData) : Fragment() {

    private lateinit var ticketbiding: TicketInformationBinding
    private lateinit var pdfUri: Uri
    private lateinit var openpdffrom: String
    private lateinit var openpdfpath: String
    private lateinit var ticketdata: TicketData
    var qualityInspectionAdapter = QualityInspectionAdapter()
    var jsonModelForTicketDetails: JsonModelForTicketDetails? = null

    lateinit var viewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e(TAG, "onCreateView: ")
        ticketbiding = TicketInformationBinding.inflate(layoutInflater)
        viewModel = (activity as TicketDetailsActivity).viewModel

        ticketdata = ticketID

//       ticketbiding.ivTicketDetails.setOnClickListener {
//           if (ticketbiding.tvTicketDetails.visibility==View.VISIBLE){
//               ticketbiding.tvTicketDetails.visibility=View.GONE
//           }else{
//
//               ticketbiding.tvTicketDetails.visibility=View.VISIBLE
//           }
//       }

        val text = resources.openRawResource(com.ahuja.sons.R.raw.departments)
            .bufferedReader().use { it.readText() }


        // Log.e(TAG, "SHUB===>: $text")

//        callAttachlistingApi();//todo


        val tickethistory = HashMap<String, Any>()
        tickethistory["LinkID"] = ticketdata.id
        tickethistory["LinkType"] = "TicketProject"

        viewModel.allattachment(tickethistory)

        viewModel.getQualityInspectionList(
            PayLoadForInspectionList(
                fields = arrayListOf(
                    "CreatedBy",
                    "id",
                    "TicketId",
                    "IssueType",
                    "Description",
                    "CreatedTime",
                    "CreatedDate",
                    "CreatedBy"
                ),
                Filter(TicketId = ticketdata.id.toString())
            )
        )


        ticketbiding.uploadDoc.setOnClickListener {
            when (ticketdata.TicketStatus) {
                "Accepted" -> {
                    if (ticketdata!!.Status == "Closed") {
                        Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                    }
                    else if (ticketdata!!.Status == "Resolved") {
                        Global.warningdialogbox(requireContext(), "You are not authorized to update report once ticket is resolved !")
                    }else{
                        selectPdf()
                    }

                }
                "Pending" -> {
                    Global.warningdialogbox(requireContext(), "Your ticket is not accepted yet")
                }
                else -> {
                    Global.warningdialogbox(requireContext(), "Your ticket is rejected")
                }
            }
           /* if (Global.TicketAuthentication) {

            } else {
                Global.warningdialogbox(requireContext(), "You have not authorization to work on Attachment")
            }*/

        }

        //   ticketbiding.attachview.isVisible = ticketdata.Type != "Installation"

        ticketbiding.docView.setOnClickListener {

            if (openpdffrom == "URI") {

                val pdfuristring = FileUtil.getPath(requireContext(), pdfUri)
                val file = File(Environment.getExternalStorageDirectory().absolutePath + "/" + pdfuristring)
                val target = Intent(Intent.ACTION_VIEW)
                target.setDataAndType(Uri.fromFile(file), "application/pdf")
                target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                val intent = Intent.createChooser(target, "Open File")
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Instruct the user to install a PDF reader here, or something
                }
            } else {


                /*  val i = Intent(context, OpenPdfView::class.java)
                  i.putExtra("PDFLink", ticketdata.CustomerPIR)
                  requireContext().startActivity(i)*/
                val pdf_url = Global.Image_URL + ticketdata.CustomerPIR
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
                startActivity(browserIntent)

            }

        }

        ticketbiding.ticketType.setOnClickListener {
            if (ticketdata.TypeChange.equals("Yes", ignoreCase = true)) {
                showDialogTicketTypeAssignerFragment()
            } else {

            }

        }
        ticketbiding.ibAddInspection.setOnClickListener {

            showDialogQualityInspectionFragment()
        }
        if (ticketID.SubType.equals("Other", ignoreCase = true)) {
            ticketbiding.ivEditTicketDetails.visibility = View.VISIBLE
        } else {
            ticketbiding.ivEditTicketDetails.visibility = View.INVISIBLE
        }
        ticketbiding.ivEditTicketDetails.setOnClickListener {
            if (ticketdata.TicketStatus.equals("Accepted", ignoreCase = true)) {
                showDialogUpdateTicketDetails()
            } else {
                Global.infomessagetoast(requireContext(), "Please Accept ticket first")
            }


        }


        bindAttachmentObserver()

        setUpRecyclerView()
        subscribeToObserver()

        return ticketbiding.root
    }


    //todo bind attchment..
    private fun bindAttachmentObserver() {
        viewModel.allAttachmentList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(requireContext(), it)
                Log.e(TAG, "attachmentObserverONERROR==>: $it")
            }, onLoading = {

            }, { response ->
                if (response.status == 200) {
                    val adapter = AllAtachmentAdapter(response!!.data as java.util.ArrayList<DataItem>)
                    ticketbiding.attachrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    ticketbiding.attachrecycler.adapter = adapter
                    adapter.notifyDataSetChanged()
                    adapter.setOnAttachmentDelete { dataItem ->
                        openDeleteAttachmentPopup(dataItem)
                    }
                } else {
                    Global.warningmessagetoast(requireContext(), response.message);

                }
            }
        ))
    }


    //todo item dialog open---
    private fun openDeleteAttachmentPopup(dataModel: DataItem) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Attachment")
        builder.setMessage("Are You Sure ? Want to Delete this Attachment .")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            val tickethistory = HashMap<String, Any>()
            tickethistory["id"] = dataModel.id

            viewModel.attachmentDelete(tickethistory)
            bindDeleteAttachmentObserver()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()

    }


    //todo delete attchment..
    private fun bindDeleteAttachmentObserver() {
        viewModel.allAttachmentList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(requireContext(), it)
                Log.e(TAG, "attachmentObserverONERROR==>: $it")
            }, onLoading = {

            }, { response ->
                if (response.status == 200) {

                    callRefreshAttachListingApi()

                } else {
                    Global.warningmessagetoast(requireContext(), response.message);

                }
            }
        ))
    }


    fun refreshTicktDetails() {
        Log.e(TAG, "refreshTicktDetails: ")
        if (Global.checkForInternet(requireContext())) {
            ticketbiding.tableLayout.removeAllViews()

            val tickethistory = HashMap<String, Int>()
            tickethistory["id"] = ticketID.id

            viewModel.particularTicketDetails(tickethistory)

            bindTicketDetailObserver()

        }
    }

    //todo observer for ticket detail..
    private fun bindTicketDetailObserver() {
        viewModel.allItemWiseTicket.observe(
            this, Event.EventObserver(
                onError = {
//                    Log.e("fail==>", it)
//                    Global.warningmessagetoast(requireContext(), it)
                },
                onLoading = {
                },
                onSuccess = { response ->
                    if (response.status == 200) {
                        ticketbiding.progressbar.start()
                        setData(response.data[0] as TicketData)
                    } else {
                        Global.warningmessagetoast(requireContext(), response.message)
                    }

                })
        )
    }


    fun refreshFragment() {
        Log.e(TAG, "refreshFragment: ")
        viewModel.getQualityInspectionList(
            PayLoadForInspectionList(
                fields = arrayListOf(
                    "CreatedBy",
                    "id",
                    "TicketId",
                    "IssueType",
                    "Description",
                    "CreatedTime",
                    "CreatedDate",
                    "CreatedBy"
                ),
                Filter(TicketId = ticketdata.id.toString())
            )
        )
        qualityInspectionAdapter.notifyDataSetChanged()
        // Perform the necessary actions to refresh the fragment
    }

    private fun showDialogTicketTypeAssignerFragment() {
        val dialogFragment = DialogTicketTypeLogsDetials()
        val dataBundle = Bundle()
        //  dataBundle.putString("key", type)
        dataBundle.putString("id", ticketdata!!.id.toString())

        // dataBundle.putString("ticketType", ticketdata!!.Type)
        dialogFragment.arguments = dataBundle
        dialogFragment.show(childFragmentManager, "DialogTicketTypeLogsDetials")
    }

    private fun showDialogQualityInspectionFragment() {
        var dialogFragment = DialogQualityInstpection()
        var dataBundle = Bundle()
        //  dataBundle.putString("key", type)
        dataBundle.putString("id", ticketdata!!.id.toString())
        dataBundle.putString("type", ticketdata!!.Type.toString())
        dataBundle.putString("appScheduleDate", ticketdata!!.AppScheduleDate.toString())

        // dataBundle.putString("ticketType", ticketdata!!.Type)
        dialogFragment.arguments = dataBundle
        dialogFragment.show(childFragmentManager, "DialogQualityInstpection")
    }

    private fun showDialogUpdateTicketDetails() {
        var dialogFragment = DialogUpdateTicketDetails()
        var dataBundle = Bundle()
        //  dataBundle.putString("key", type)
        dataBundle.putString("id", ticketdata!!.id.toString())
        dataBundle.putString("data", ticketdata.Data)
        dataBundle.putString("type", ticketdata!!.Type.toString())
        dataBundle.putString("appScheduleDate", ticketdata!!.AppScheduleDate.toString())
        //  dataBundle.putParcelable("data",jsonModelForTicketDetails)

        // dataBundle.putString("ticketType", ticketdata!!.Type)
        dialogFragment.arguments = dataBundle
        dialogFragment.show(childFragmentManager, "DialogUpdateTicketDetails")
    }


    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        if (Global.checkForInternet(requireContext())) {
            ticketbiding.tableLayout.removeAllViews()

            val tickethistory = HashMap<String, Int>()
            tickethistory["id"] = ticketID.id

            viewModel.particularTicketDetails(tickethistory)

            bindTicketDetailObserver()
        }
    }

    companion object {
        private const val TAG = "DetailsTicketFragment"
    }

    private fun setUpRecyclerView() = ticketbiding.rvQualityInspection.apply {
        adapter = qualityInspectionAdapter
        layoutManager = LinearLayoutManager(requireContext())

    }

    private fun subscribeToObserver() {
        viewModel.inspectionList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(requireContext(), it)
                Log.e(TAG, "subscribeToObserverONERROR==>: $it")
            }, onLoading = {

            }, { inspectionResponse ->
                if (inspectionResponse.status == 200) {
                    Log.e(TAG, "subscribeToObserver: ${inspectionResponse.data.toString()}")
                    qualityInspectionAdapter.announcement = inspectionResponse.data


                } else {
                    Log.e(TAG, "subscribeToObserverAPI:${inspectionResponse.message} ")
                    Global.warningmessagetoast(requireContext(), inspectionResponse.message)
                }

            }
        ))
    }


    //todo comment by chanchal...

    private fun callRefreshAttachListingApi() {
        val tickethistory = HashMap<String, Any>()
        tickethistory["LinkID"] = ticketdata.id
        tickethistory["LinkType"] = "TicketProject"

        val call: Call<AllAttachmentResponse> = ApiClient().service.allAttachment(tickethistory)
        call.enqueue(object : Callback<AllAttachmentResponse?> {
            override fun onResponse(
                call: Call<AllAttachmentResponse?>,
                response: Response<AllAttachmentResponse?>
            ) {
                if (response.body()!!.status == 200) {
                    val dapter = AllAtachmentAdapter(response.body()!!.data as java.util.ArrayList<DataItem>)
                    ticketbiding.attachrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    ticketbiding.attachrecycler.adapter = dapter
                    dapter.notifyDataSetChanged()

                } else {
                    Global.warningmessagetoast(requireContext(), response.errorBody().toString());

                }
            }

            override fun onFailure(call: Call<AllAttachmentResponse?>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun selectPdf() {
        pdfurilist.clear()
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "*/*"
        pdfIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 12)
    }

    var pdfurilist = ArrayList<String>()

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // For loading PDF
        when (requestCode) {
            12 -> if (resultCode == RESULT_OK) {


                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0
                    for (i in 0 until count) {
                        val imageUri: Uri = data.clipData?.getItemAt(i)?.uri!!
                        openpdfpath = copyFileToInternalStorage(
                            imageUri,
                            "com.android.servicesupport"
                        ).toString()

                        pdfurilist.add(openpdfpath)

                    }

                } else {
                    pdfUri = data?.data!!
                    val uri: Uri = data.data!!
                    val uriString: String = uri.toString()
                    openpdfpath =
                        copyFileToInternalStorage(pdfUri, "com.android.servicesupport").toString()
                    pdfurilist.add(openpdfpath)
                }
                var pdfName: String? = null
                /*if (pdfurilist[0].startsWith("content://")) {
                    var myCursor: Cursor? = null
                    try {
                        // Setting the PDF to the TextView
                        myCursor = requireContext().contentResolver.query(pdfUri, null, null, null, null)
                        if (myCursor != null && myCursor.moveToFirst()) {
                            pdfName = myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            openpdffrom = "URI"
                            ticketbiding.docView.isVisible = true
                            ticketbiding.docname.text = pdfName
                            ticketbiding.progressbar.start()
                            uploaddocument()
                        }
                    } finally {
                        myCursor?.close()
                    }
                }*/
                openpdffrom = "URI"
                /*   ticketbiding.docView.isVisible = true
                   ticketbiding.docname.text = pdfName*/
                ticketbiding.progressbar.start()
                uploaddocument()
            }
        }
    }


    private fun copyFileToInternalStorage(uri: Uri, newDirName: String): String? {
        val returnCursor: Cursor = requireContext().getContentResolver().query(
            uri, arrayOf(
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
            ), null, null, null
        )!!


        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val output: File = if (newDirName != "") {
            val dir: File = File(requireContext().filesDir.toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            File(requireContext().filesDir.toString() + "/" + newDirName + "/" + name)
        } else {
            File(requireContext().filesDir.toString() + "/" + name)
        }
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream!!.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("Exception", e.message!!)
        }
        return output.path
    }

    private fun uploaddocument() {
        //  val pdfuristring = FileUtil.getPath(requireContext(),pdfUri)

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        builder.addFormDataPart("EmployeeId", Prefs.getString(Global.Employee_Code))
        builder.addFormDataPart("LinkID", ticketdata.id.toString())
        builder.addFormDataPart("LinkType", "TicketProject")
        builder.addFormDataPart("Caption", "")
        builder.addFormDataPart("CreateDate", "")
        builder.addFormDataPart("CreateTime", "")

        if (pdfurilist.size > 0) {
            for (i in pdfurilist.indices) {
                val file: File = File(pdfurilist[i])
                builder.addFormDataPart("File", file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
            }
        } else {
            builder.addFormDataPart("File", "", RequestBody.create("multipart/form-data".toMediaTypeOrNull(), ""))
        }

        val requestBody = builder.build()
        Log.e("payload", requestBody.toString())

        viewModel.multiplfileupload(requestBody)

        bindCreateObserver()

    }

    //todo create many attchment observer..
    private fun bindCreateObserver() {
        viewModel.customerUpload.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                ticketbiding.progressbar.stop()
                ticketbiding.loadingback.visibility = View.GONE
//                Global.warningmessagetoast(requireContext(), it)
                Log.e("Error===>", "attachmentObserverONERROR==>: $it")
            }, onLoading = {
                ticketbiding.progressbar.start()
                ticketbiding.loadingback.visibility = View.VISIBLE
            },
            onSuccess = { response ->
                ticketbiding.progressbar.stop()
                ticketbiding.loadingback.visibility = View.GONE
                if (response.status == 200) {
                    Log.e("data", response.data.toString())
                    Toast.makeText(requireContext(), "Upload SuccessFully", Toast.LENGTH_LONG)
                        .show()
                    val tickethistory = HashMap<String, Any>()
                    tickethistory["LinkID"] = ticketdata.id
                    tickethistory["LinkType"] = "TicketProject"

                    viewModel.allattachment(tickethistory)
                    bindAttachmentObserver()
                } else {
                    Global.warningmessagetoast(requireContext(), response.message);

                }
            }
        ))
    }

    private fun setData(ticketdata: TicketData?) {
        if (ticketdata != null) {
            this.ticketdata = ticketdata
        }

        parseJson(ticketdata?.Data!!)
        //  parseJsonTother(ticketdata.Data)

        openpdffrom = "API"
        ticketbiding.docname.text = ticketdata!!.CustomerPIR
//        ticketbiding.docView.isVisible = ticketdata.CustomerPIR!!.isNotEmpty()
        Global.TicketAuthentication = Prefs.getString(Global.Employee_Code) == ticketdata.AssignToDetails[0].SalesEmployeeCode


        ticketbiding.addOnDuration.text = ticketdata.AddOnDuration
        ticketbiding.emailValue.text = ticketdata.ContactEmail
        ticketbiding.phoneNumber.text = ticketdata.ContactPhone
        ticketbiding.tvZone.text = ticketdata.Zone

        if (ticketdata.BusinessPartner.size > 0){
            if (ticketdata.BusinessPartner[0].BPEmployee?.isNotEmpty()!!) {
                ticketbiding.contactphoneNumber.text = ticketdata.BusinessPartner[0].Phone1
                ticketbiding.contactPersonValue.text = ticketdata.BusinessPartner[0].BPEmployee[0].FirstName
                ticketbiding.tvTAT.text = ticketdata.BusinessPartner[0].EscalationTAT
            }else {
                ticketbiding.contactphoneNumber.text = "N/A"
                ticketbiding.contactPersonValue.text = "N/A"
            }
        }
        else {
            ticketbiding.contactphoneNumber.text = "N/A"
            ticketbiding.contactPersonValue.text = "N/A"
        }



        ticketbiding.ticketType.text = ticketdata.Type
        ticketbiding.tvSubTypeTicket.text = ticketdata.SubType
        ticketbiding.tvProductSerialNumber.text = ticketdata.ProductSerialNo
        ticketbiding.alternaterphoneNumber.text = ticketdata.AlternatePhone

        if (ticketdata.AssignToDetails.size > 0){
            ticketbiding.assignedValue.text = ticketdata.AssignToDetails[0].SalesEmployeeName
        }else{
            ticketbiding.assignedValue.text = "NA"
        }

        if (ticketdata.Description.isEmpty()) {
            ticketbiding.description.text =
                getString(
                    com.ahuja.sons.R.string.description_dynamic,
                    ticketdata.Type
                );
        } else {
            ticketbiding.description.text = ticketdata.Description
        }


        if (ticketdata.BusinessPartner.size > 0){
            if (ticketdata.BusinessPartner[0].BPAddresses.isNotEmpty()) {
                ticketbiding.address.text = ticketdata.BusinessPartner[0].BPAddresses[0].Street + ", " + ticketdata.BusinessPartner[0].BPAddresses[0].City +
                            ", " + ticketdata.BusinessPartner[0].BPAddresses[0].U_STATE +
                            ", " + ticketdata.BusinessPartner[0].BPAddresses[0].U_COUNTRY
            }

        }else{
            ticketbiding.address.text = "NA"
        }

        Global.TicketStartDate = ticketdata.TicketStartDate.toString()
        Global.TicketEndDate = ticketdata.TicketEndDate.toString()

        if (ticketdata.CreatedByDetails.size > 0){
            ticketdata.CreatedByDetails[0].let {
                if (it.SalesEmployeeName.isNotEmpty())
                    ticketbiding.createdby.text = it.SalesEmployeeName
                else
                    ticketbiding.createdby.text = "Auto-generated"
            }
        }else{
            ticketbiding.createdby.text = "NA"
        }


        if (ticketdata.TicketStartDate!!.isNotEmpty()) {
            ticketbiding.starttimeValue.text =
                Global.formatserverDateFromDateString(ticketdata.TicketStartDate)
        } else {
            ticketbiding.starttimeValue.text = resources.getString(R.string.yet_to_start)
        }
        if (ticketdata.TicketEndDate!!.isNotEmpty()) {
            ticketbiding.endtimeValue.text =
                Global.formatserverDateFromDateString(ticketdata.TicketEndDate)
        }

        if (ticketdata.Title.isNotEmpty()){
            ticketbiding.tvTitle.text = ticketdata.Title
        }else{
            ticketbiding.tvTitle.text = "NA"
        }
        if (!ticketdata.id.equals(0)){
            ticketbiding.tvTicketNumber.text = ticketdata.id.toString()
        }else{
            ticketbiding.tvTicketNumber.text = "NA"
        }
        if (ticketdata.SubType.isNotEmpty()){
            ticketbiding.tvRequestType.text = ticketdata.SubType
        }else{
            ticketbiding.tvRequestType.text = "NA"
        }
        if (ticketdata.CaseOrigin.isNotEmpty()){
            ticketbiding.tvCaseOrigin.text = ticketdata.CaseOrigin
        }else{
            ticketbiding.tvCaseOrigin.text = "NA"
        }

        if (ticketdata.BusinessPartner.isNotEmpty()){
            if (ticketdata.BusinessPartner[0].CardName.isNotEmpty()){
                ticketbiding.tvCustomerName.text = ticketdata.BusinessPartner[0].CardName
            }else{
                ticketbiding.tvCustomerName.text = "NA"
            }

        }else{
            ticketbiding.tvCustomerName.text = "NA"
        }
        if (ticketdata.BusinessPartner.isNotEmpty()){
            if (ticketdata.BusinessPartner[0].BPEmployee.size > 0){
                ticketbiding.tvContactEmail.text = ticketdata.BusinessPartner[0].BPEmployee[0].E_Mail
            }else{
                ticketbiding.tvContactEmail.text = "NA"
            }

        }else{
            ticketbiding.tvContactEmail.text = "NA"
        }

      if (ticketdata.BusinessPartner.isNotEmpty()){
            if (ticketdata.BusinessPartner[0].BPAddresses.size > 0){
                ticketbiding.tvCity.text = ticketdata.BusinessPartner[0].BPAddresses[0].City
            }else{
                ticketbiding.tvCity.text = "NA"
            }

        }else{
            ticketbiding.tvCity.text = "NA"
        }
        if (ticketdata.Priority.isNotEmpty()){
            ticketbiding.tvPriority.text = ticketdata.Priority
        }else{
            ticketbiding.tvPriority.text = "NA"
        }

        if (ticketdata.TandC.isNotEmpty()){
            ticketbiding.tvTermCondition.text = ticketdata.TandC
        }else{
            ticketbiding.tvTermCondition.text = "NA"
        }

        if (ticketdata.CreateDate.isNotEmpty()){
            ticketbiding.tvCreatedDate.text = Global.convert_yyyy_mm_dd_to_dd_mm_yyyy(ticketdata.CreateDate)
        }else{
            ticketbiding.tvCreatedDate.text = "NA"
        }




        ticketbiding.progressbar.stop()
        ticketbiding.loadingback.visibility = View.GONE
    }


    fun parseJsonTother(jsonString: String) {
        try {
            val json = JSONObject(jsonString)

            if (json is JSONObject) {
                val modelObject = convertToModelObjectOther(json)
                Log.e(TAG, "parseJsonOTHER==>: ${modelObject.toString()}")

                val tableLayout: TableLayout = ticketbiding.tableLayout


                // First Row
                val row1 = TableRow(context)

                val tvStatusStatic = TextView(context)
                tvStatusStatic.typeface = resources.getFont(R.font.helvetica)
                tvStatusStatic.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                tvStatusStatic.textSize = 14F

                tvStatusStatic.text = "Status:  "
                row1.addView(tvStatusStatic)

                val tvStatusDynamic = TextView(context)
                tvStatusDynamic.text = "${
                    modelObject.Status
                }"
                row1.addView(tvStatusDynamic)

                tableLayout.addView(row1)


// Second Row
                val row2 = TableRow(context)

                val tvStaticCorrectIssueType = TextView(context)
                tvStaticCorrectIssueType.text = "CorrectIssueType:  "
                tvStaticCorrectIssueType.typeface = resources.getFont(R.font.helvetica)
                tvStaticCorrectIssueType.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                tvStaticCorrectIssueType.textSize = 14F
                row2.addView(tvStaticCorrectIssueType)

                val tvDynamicCorrectIssueType = TextView(context)
                tvDynamicCorrectIssueType.text = "${modelObject.CorrectIssueType}"
                row2.addView(tvDynamicCorrectIssueType)

                tableLayout.addView(row2)


// Third Row
                val row3 = TableRow(context)

                val tvStaticCorrectiveActions = TextView(context)
                tvStaticCorrectiveActions.typeface = resources.getFont(R.font.helvetica)
                tvStaticCorrectiveActions.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                tvStaticCorrectiveActions.textSize = 14F
                tvStaticCorrectiveActions.text = "CorrectiveActions :  "
                row3.addView(tvStaticCorrectiveActions)

                val tvDynamicCorrectiveActions = TextView(context)
                tvDynamicCorrectiveActions.text = "${modelObject.CorrectiveActions}"

                row3.addView(tvDynamicCorrectiveActions)

                tableLayout.addView(row3)

                val row4 = TableRow(context)
                val tvStaticScheduledVisitDate = TextView(context)
                tvStaticScheduledVisitDate.typeface = resources.getFont(R.font.helvetica)
                tvStaticScheduledVisitDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                tvStaticScheduledVisitDate.textSize = 14F

                tvStaticScheduledVisitDate.text = "ScheduledVisitDate :  "
                row4.addView(tvStaticScheduledVisitDate)

                val tvDynamicScheduledVisitDate = TextView(context)
                tvDynamicScheduledVisitDate.setText("${modelObject.RepairRequestNeeded}")
                row4.addView(tvDynamicScheduledVisitDate)

                tableLayout.addView(row4)


                val row5 = TableRow(context)
                val tvStaticMaterialUsed = TextView(context)
                tvStaticMaterialUsed.typeface = resources.getFont(R.font.helvetica)
                tvStaticMaterialUsed.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                tvStaticMaterialUsed.textSize = 14F

                tvStaticMaterialUsed.text = "MaterialUsed :  "
                row5.addView(tvStaticMaterialUsed)

                val tvDynamicMaterialUsed = TextView(context)
                tvDynamicMaterialUsed.setText("${modelObject.MaterialUsed}")
                row5.addView(tvDynamicMaterialUsed)

                tableLayout.addView(row5)


            }


        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun parseJson(jsonString: String) {
        try {
            val tableLayout: TableLayout = ticketbiding.tableLayout
            val json = JSONObject(jsonString)

            if (json is JSONObject) {
                Log.e(TAG, "parseJsonentry: ")
                // JSON is an object
                when (ticketdata.SubType) {
                    "" -> {
                        val modelObject = convertToModelObjectOther(json)
                        Log.e(TAG, "parseJsonEmpty: ${modelObject.toString()}")


                        // First Row
                        val row1 = TableRow(context)

                        val tvStatusStatic = TextView(context)
                        tvStatusStatic.typeface = resources.getFont(R.font.helvetica)
                        tvStatusStatic.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStatusStatic.textSize = 14F

                        tvStatusStatic.text = "Status:  "
                        row1.addView(tvStatusStatic)

                        val tvStatusDynamic = TextView(context)
                        tvStatusDynamic.text = "${
                            modelObject.Status
                        }"
                        row1.addView(tvStatusDynamic)

                        tableLayout.addView(row1)


// Second Row
                        val row2 = TableRow(context)

                        val tvStaticCorrectIssueType = TextView(context)
                        tvStaticCorrectIssueType.text = "CorrectIssueType:  "
                        tvStaticCorrectIssueType.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCorrectIssueType.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCorrectIssueType.textSize = 14F
                        row2.addView(tvStaticCorrectIssueType)

                        val tvDynamicCorrectIssueType = TextView(context)
                        tvDynamicCorrectIssueType.text = "${modelObject.CorrectIssueType}"
                        row2.addView(tvDynamicCorrectIssueType)

                        tableLayout.addView(row2)


// Third Row
                        val row3 = TableRow(context)

                        val tvStaticCorrectiveActions = TextView(context)
                        tvStaticCorrectiveActions.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCorrectiveActions.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCorrectiveActions.textSize = 14F
                        tvStaticCorrectiveActions.text = "CorrectiveActions :  "
                        row3.addView(tvStaticCorrectiveActions)

                        val tvDynamicCorrectiveActions = TextView(context)
                        tvDynamicCorrectiveActions.text = "${modelObject.CorrectiveActions}"

                        row3.addView(tvDynamicCorrectiveActions)

                        tableLayout.addView(row3)

                        val row4 = TableRow(context)
                        val tvStaticScheduledVisitDate = TextView(context)
                        tvStaticScheduledVisitDate.typeface = resources.getFont(R.font.helvetica)
                        tvStaticScheduledVisitDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticScheduledVisitDate.textSize = 14F

                        tvStaticScheduledVisitDate.text = "ScheduledVisitDate :  "
                        row4.addView(tvStaticScheduledVisitDate)

                        val tvDynamicScheduledVisitDate = TextView(context)
                        tvDynamicScheduledVisitDate.setText("${modelObject.RepairRequestNeeded}")
                        row4.addView(tvDynamicScheduledVisitDate)

                        tableLayout.addView(row4)


                        val row5 = TableRow(context)
                        val tvStaticMaterialUsed = TextView(context)
                        tvStaticMaterialUsed.typeface = resources.getFont(R.font.helvetica)
                        tvStaticMaterialUsed.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticMaterialUsed.textSize = 14F

                        tvStaticMaterialUsed.text = "MaterialUsed :  "
                        row5.addView(tvStaticMaterialUsed)

                        val tvDynamicMaterialUsed = TextView(context)
                        tvDynamicMaterialUsed.setText("${modelObject.MaterialUsed}")
                        row5.addView(tvDynamicMaterialUsed)

                        tableLayout.addView(row5)


                    }
                    Global.FINAL_SITE_SURVEY_SUBTYPE -> {
                        val modelObject = convertToModelObjectFinalSiteSurvey(json)
                        Log.e(TAG, "convertToModelObjectFinalSiteSurvey: ${modelObject.toString()}")

                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticDesignEngineerName = TextView(context)
                        tvStaticDesignEngineerName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDesignEngineerName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDesignEngineerName.textSize = 14F

                        tvStaticDesignEngineerName.text = "DesignEngineerName:  "
                        row1.addView(tvStaticDesignEngineerName)

                        val tvDynamicDesignEngineerName = TextView(context)
                        tvDynamicDesignEngineerName.text = "${
                            modelObject.DesignEngineerName
                        }"
                        row1.addView(tvDynamicDesignEngineerName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticFinalSurveyReportattached = TextView(context)
                        tvStaticFinalSurveyReportattached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticFinalSurveyReportattached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticFinalSurveyReportattached.textSize = 14F

                        tvStaticFinalSurveyReportattached.text = "FinalSurveyReportattached:  "
                        row2.addView(tvStaticFinalSurveyReportattached)

                        val tvDynamicFinalSurveyReportattached = TextView(context)
                        tvDynamicFinalSurveyReportattached.text = "${
                            modelObject.FinalSurveyReportattached
                        }"
                        row2.addView(tvDynamicFinalSurveyReportattached)

                        tableLayout.addView(row2)


                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row3.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row3.addView(tvDynamicRemark)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticstatus = TextView(context)
                        tvStaticstatus.typeface = resources.getFont(R.font.helvetica)
                        tvStaticstatus.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticstatus.textSize = 14F

                        tvStaticstatus.text = "status:  "
                        row4.addView(tvStaticstatus)

                        val tvDynamicstatus = TextView(context)
                        tvDynamicstatus.text = "${
                            modelObject.status
                        }"
                        row4.addView(tvDynamicstatus)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticshaft = TextView(context)
                        tvStaticshaft.typeface = resources.getFont(R.font.helvetica)
                        tvStaticshaft.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticshaft.textSize = 14F

                        tvStaticshaft.text = "shaft:  "
                        row5.addView(tvStaticshaft)

                        val tvDynamicshaft = TextView(context)
                        tvDynamicshaft.text = "${
                            modelObject.shaft
                        }"
                        row5.addView(tvDynamicshaft)

                        tableLayout.addView(row5)

                        //sixth Row
                        val row6 = TableRow(context)

                        val tvStaticFrom = TextView(context)
                        tvStaticFrom.typeface = resources.getFont(R.font.helvetica)
                        tvStaticFrom.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticFrom.textSize = 14F

                        tvStaticFrom.text = "From:  "
                        row6.addView(tvStaticFrom)

                        val tvDynamicFrom = TextView(context)
                        tvDynamicFrom.text = "${
                            modelObject.From
                        }"
                        row6.addView(tvDynamicFrom)

                        tableLayout.addView(row6)

                        //seventh Row
                        val row7 = TableRow(context)

                        val tvStaticTypeofElevator = TextView(context)
                        tvStaticTypeofElevator.typeface = resources.getFont(R.font.helvetica)
                        tvStaticTypeofElevator.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticTypeofElevator.textSize = 14F

                        tvStaticTypeofElevator.text = "TypeofElevator:  "
                        row7.addView(tvStaticTypeofElevator)

                        val tvDynamicTypeofElevator = TextView(context)
                        tvDynamicTypeofElevator.text = "${
                            modelObject.TypeofElevator
                        }"
                        row7.addView(tvDynamicTypeofElevator)

                        tableLayout.addView(row7)

                        //eight Row
                        val row8 = TableRow(context)

                        val tvStaticOther = TextView(context)
                        tvStaticOther.typeface = resources.getFont(R.font.helvetica)
                        tvStaticOther.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticOther.textSize = 14F

                        tvStaticOther.text = "Other:  "
                        row8.addView(tvStaticOther)

                        val tvDynamicOther = TextView(context)
                        tvDynamicOther.text = "${
                            modelObject.Other
                        }"
                        row8.addView(tvDynamicOther)

                        tableLayout.addView(row8)

                        //ninth Row
                        val row9 = TableRow(context)

                        val tvStaticShaftAvailability = TextView(context)
                        tvStaticShaftAvailability.typeface = resources.getFont(R.font.helvetica)
                        tvStaticShaftAvailability.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticShaftAvailability.textSize = 14F

                        tvStaticShaftAvailability.text = "ShaftAvailability:  "
                        row9.addView(tvStaticShaftAvailability)

                        val tvDynamicShaftAvailability = TextView(context)
                        tvDynamicShaftAvailability.text = "${
                            modelObject.ShaftAvailability
                        }"
                        row9.addView(tvDynamicShaftAvailability)

                        tableLayout.addView(row9)

                        //tenth Row
                        val row10 = TableRow(context)

                        val tvStaticShaftWidth = TextView(context)
                        tvStaticShaftWidth.typeface = resources.getFont(R.font.helvetica)
                        tvStaticShaftWidth.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticShaftWidth.textSize = 14F

                        tvStaticShaftWidth.text = "ShaftWidth:  "
                        row10.addView(tvStaticShaftWidth)

                        val tvDynamicShaftWidth = TextView(context)
                        tvDynamicShaftWidth.text = "${
                            modelObject.ShaftWidth
                        }"
                        row10.addView(tvDynamicShaftWidth)

                        tableLayout.addView(row10)

                        //eleventh Row
                        val row11 = TableRow(context)

                        val tvStaticShaftDepth = TextView(context)
                        tvStaticShaftDepth.typeface = resources.getFont(R.font.helvetica)
                        tvStaticShaftDepth.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticShaftDepth.textSize = 14F

                        tvStaticShaftDepth.text = "ShaftDepth:  "
                        row11.addView(tvStaticShaftDepth)

                        val tvDynamicShaftDepth = TextView(context)
                        tvDynamicShaftDepth.text = "${
                            modelObject.ShaftDepth
                        }"
                        row11.addView(tvDynamicShaftDepth)

                        tableLayout.addView(row11)


                        //twelth Row
                        val row12 = TableRow(context)

                        val tvStaticTravel = TextView(context)
                        tvStaticTravel.typeface = resources.getFont(R.font.helvetica)
                        tvStaticTravel.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticTravel.textSize = 14F

                        tvStaticTravel.text = "Travel:  "
                        row12.addView(tvStaticTravel)

                        val tvDynamicTravel = TextView(context)
                        tvDynamicTravel.text = "${
                            modelObject.Travel
                        }"
                        row12.addView(tvDynamicTravel)

                        tableLayout.addView(row12)

                        //thirteenth Row
                        val row13 = TableRow(context)

                        val tvStaticOverHead = TextView(context)
                        tvStaticOverHead.typeface = resources.getFont(R.font.helvetica)
                        tvStaticOverHead.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticOverHead.textSize = 14F

                        tvStaticOverHead.text = "OverHead:  "
                        row13.addView(tvStaticOverHead)

                        val tvDynamicOverHead = TextView(context)
                        tvDynamicOverHead.text = "${
                            modelObject.OverHead
                        }"
                        row13.addView(tvDynamicOverHead)

                        tableLayout.addView(row13)

                        //fourteenth Row
                        val row14 = TableRow(context)

                        val tvStaticPit = TextView(context)
                        tvStaticPit.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPit.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPit.textSize = 14F

                        tvStaticPit.text = "Pit:  "
                        row14.addView(tvStaticPit)

                        val tvDynamicPit = TextView(context)
                        tvDynamicPit.text = "${
                            modelObject.Pit
                        }"
                        row14.addView(tvDynamicPit)

                        tableLayout.addView(row14)

                        //fifteenth Row
                        val row15 = TableRow(context)

                        val tvStaticWidthA = TextView(context)
                        tvStaticWidthA.typeface = resources.getFont(R.font.helvetica)
                        tvStaticWidthA.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticWidthA.textSize = 14F

                        tvStaticWidthA.text = "WidthA:  "
                        row15.addView(tvStaticWidthA)

                        val tvDynamicWidthA = TextView(context)
                        tvDynamicWidthA.text = "${
                            modelObject.WidthA
                        }"
                        row15.addView(tvDynamicWidthA)

                        tableLayout.addView(row15)

                        //sixteen Row
                        val row16 = TableRow(context)

                        val tvStaticWidthC = TextView(context)
                        tvStaticWidthC.typeface = resources.getFont(R.font.helvetica)
                        tvStaticWidthC.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticWidthC.textSize = 14F

                        tvStaticWidthC.text = "WidthC:  "
                        row16.addView(tvStaticWidthC)

                        val tvDynamicWidthC = TextView(context)
                        tvDynamicWidthC.text = "${
                            modelObject.WidthC
                        }"
                        row16.addView(tvDynamicWidthC)

                        tableLayout.addView(row16)

                        //seventeen Row
                        val row17 = TableRow(context)

                        val tvStaticStructureOpening = TextView(context)
                        tvStaticStructureOpening.typeface = resources.getFont(R.font.helvetica)
                        tvStaticStructureOpening.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticStructureOpening.textSize = 14F

                        tvStaticStructureOpening.text = "StructureOpening:  "
                        row17.addView(tvStaticStructureOpening)

                        val tvDynamicStructureOpening = TextView(context)
                        tvDynamicStructureOpening.text = "${
                            modelObject.StructureOpening
                        }"
                        row17.addView(tvDynamicStructureOpening)

                        tableLayout.addView(row17)


                    }
                    Global.DRAWING_APPROVAL_SUBTYPE -> {
                        val modelObject = convertToModelObjectDrawingApproval(json)
                        Log.e(TAG, "convertToModelObjectDrawingApproval: ${modelObject.toString()}")
                        val row1 = TableRow(context)

                        val tvStaticDesignEngineerName = TextView(context)
                        tvStaticDesignEngineerName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDesignEngineerName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDesignEngineerName.textSize = 14F

                        tvStaticDesignEngineerName.text = "DesignEngineerName:  "
                        row1.addView(tvStaticDesignEngineerName)

                        val tvDesignEngineerNameDynamic = TextView(context)
                        tvDesignEngineerNameDynamic.text = "${
                            modelObject.DesignEngineerName
                        }"
                        row1.addView(tvDesignEngineerNameDynamic)

                        tableLayout.addView(row1)

//Second Row
                        val row2 = TableRow(context)

                        val tvStaticFinalDrawingAttached = TextView(context)
                        tvStaticFinalDrawingAttached.typeface = resources.getFont(R.font.helvetica)
                        tvStaticFinalDrawingAttached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticFinalDrawingAttached.textSize = 14F

                        tvStaticFinalDrawingAttached.text = "FinalDrawingAttached:  "
                        row2.addView(tvStaticFinalDrawingAttached)

                        val tvFinalDrawingAttachedDynamic = TextView(context)
                        tvFinalDrawingAttachedDynamic.text = "${
                            modelObject.FinalDrawingAttached
                        }"
                        row2.addView(tvFinalDrawingAttachedDynamic)

                        tableLayout.addView(row2)


                        //Third Row
                        val row3 = TableRow(context)

                        val tvStaticDeviationsObserved = TextView(context)
                        tvStaticDeviationsObserved.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDeviationsObserved.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDeviationsObserved.textSize = 14F

                        tvStaticDeviationsObserved.text = "DeviationsObserved:  "
                        row3.addView(tvStaticDeviationsObserved)

                        val tvDeviationsObservedDynamic = TextView(context)
                        tvDeviationsObservedDynamic.text = "${
                            modelObject.DeviationsObserved
                        }"
                        row3.addView(tvDeviationsObservedDynamic)

                        tableLayout.addView(row3)


                        //Fourth Row
                        val row4 = TableRow(context)

                        val tvStaticDeviationsDetails = TextView(context)
                        tvStaticDeviationsDetails.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDeviationsDetails.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDeviationsDetails.textSize = 14F

                        tvStaticDeviationsDetails.text = "DeviationsDetails:  "
                        row4.addView(tvStaticDeviationsDetails)

                        val tvDynamicDeviationsObserved = TextView(context)
                        tvDynamicDeviationsObserved.text = "${
                            modelObject.DeviationsDetails
                        }"
                        row4.addView(tvDynamicDeviationsObserved)

                        tableLayout.addView(row4)

                        //Fifth Row
                        val row5 = TableRow(context)

                        val tvStaticFinalDrawingApproval = TextView(context)
                        tvStaticFinalDrawingApproval.typeface = resources.getFont(R.font.helvetica)
                        tvStaticFinalDrawingApproval.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticFinalDrawingApproval.textSize = 14F

                        tvStaticFinalDrawingApproval.text = "FinalDrawingApproval:  "
                        row5.addView(tvStaticDeviationsDetails)

                        val tvDynamicFinalDrawingApproval = TextView(context)
                        tvDynamicFinalDrawingApproval.text = "${
                            modelObject.FinalDrawingApproval
                        }"
                        row5.addView(tvDynamicFinalDrawingApproval)

                        tableLayout.addView(row5)


                    }

                    Global.FINAL_ORDER_SPECIFICATION_SUBTYPE -> {
                        val modelObject = convertToModelObjectFinalOrderSpecification(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectFinalOrderSpecification: ${modelObject.toString()}"
                        )

                        val row1 = TableRow(context)

                        val tvStaticClientApprovalFOS = TextView(context)
                        tvStaticClientApprovalFOS.typeface = resources.getFont(R.font.helvetica)
                        tvStaticClientApprovalFOS.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticClientApprovalFOS.textSize = 14F

                        tvStaticClientApprovalFOS.text = "ClientApprovalFOS:  "
                        row1.addView(tvStaticClientApprovalFOS)

                        val tvDynamicClientApprovalFOS = TextView(context)
                        tvDynamicClientApprovalFOS.text = "${
                            modelObject.ClientApprovalFOS
                        }"
                        row1.addView(tvDynamicClientApprovalFOS)

                        tableLayout.addView(row1)


                    }
                    Global.PURCHASE_REQUEST_SUBTYPE -> {
                        val modelObject = convertToModelObjectPurchaseRequest(json)
                        Log.e(TAG, "convertToModelObjectPurchaseRequest: ${modelObject.toString()}")

                        //First Row
                        val row1 = TableRow(context)

                        val tvStaticSalespersonName = TextView(context)
                        tvStaticSalespersonName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticSalespersonName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticSalespersonName.textSize = 14F

                        tvStaticSalespersonName.text = "SalespersonName:  "
                        row1.addView(tvStaticSalespersonName)

                        val tvDynamicSalespersonName = TextView(context)
                        tvDynamicSalespersonName.text = "${
                            modelObject.SalespersonName
                        }"
                        row1.addView(tvDynamicSalespersonName)

                        tableLayout.addView(row1)


                        //Second Row
                        val row2 = TableRow(context)

                        val tvStaticPurchaeRequestNo = TextView(context)
                        tvStaticPurchaeRequestNo.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPurchaeRequestNo.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPurchaeRequestNo.textSize = 14F

                        tvStaticPurchaeRequestNo.text = "PurchaeRequestNo:  "
                        row2.addView(tvStaticPurchaeRequestNo)

                        val tvDynamicPurchaeRequestNo = TextView(context)
                        tvDynamicPurchaeRequestNo.text = "${
                            modelObject.PurchaeRequestNo
                        }"
                        row2.addView(tvDynamicPurchaeRequestNo)

                        tableLayout.addView(row2)


                        //Third Row
                        val row3 = TableRow(context)

                        val tvStaticPurchaeRequestDate = TextView(context)
                        tvStaticPurchaeRequestDate.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPurchaeRequestDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPurchaeRequestDate.textSize = 14F

                        tvStaticPurchaeRequestDate.text = "PurchaeRequestDate:  "
                        row3.addView(tvStaticPurchaeRequestDate)

                        val tvDynamicPurchaeRequestDate = TextView(context)
                        tvDynamicPurchaeRequestDate.text = "${
                            modelObject.PurchaeRequestDate
                        }"
                        row3.addView(tvDynamicPurchaeRequestNo)

                        tableLayout.addView(row3)

                        //Fourth Row
                        val row4 = TableRow(context)

                        val tvStaticPurchaeRequestTo = TextView(context)
                        tvStaticPurchaeRequestTo.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPurchaeRequestTo.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPurchaeRequestTo.textSize = 14F

                        tvStaticPurchaeRequestTo.text = "PurchaeRequestTo:  "
                        row4.addView(tvStaticPurchaeRequestTo)

                        val tvDynamicPurchaeRequestTo = TextView(context)
                        tvDynamicPurchaeRequestTo.text = "${
                            modelObject.PurchaeRequestTo
                        }"
                        row4.addView(tvDynamicPurchaeRequestTo)

                        tableLayout.addView(row4)


                        //Fifth Row
                        val row5 = TableRow(context)

                        val tvStaticPurchaeRequestType = TextView(context)
                        tvStaticPurchaeRequestType.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPurchaeRequestType.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPurchaeRequestType.textSize = 14F

                        tvStaticPurchaeRequestType.text = "PurchaeRequestType:  "
                        row5.addView(tvStaticPurchaeRequestType)

                        val tvDynamicPurchaeRequestType = TextView(context)
                        tvDynamicPurchaeRequestType.text = "${
                            modelObject.PurchaeRequestType
                        }"
                        row5.addView(tvDynamicPurchaeRequestType)

                        tableLayout.addView(row5)

                        //Sixth Row
                        val row6 = TableRow(context)

                        val tvStaticDocumentDate = TextView(context)
                        tvStaticDocumentDate.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDocumentDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDocumentDate.textSize = 14F

                        tvStaticDocumentDate.text = "DocumentDate:  "
                        row6.addView(tvStaticDocumentDate)

                        val tvDynamicDocumentDate = TextView(context)
                        tvDynamicDocumentDate.text = "${
                            modelObject.DocumentDate
                        }"
                        row6.addView(tvDynamicDocumentDate)

                        tableLayout.addView(row6)


                        //Seventh Row
                        val row7 = TableRow(context)

                        val tvStaticClientApprovalFOS = TextView(context)
                        tvStaticClientApprovalFOS.typeface = resources.getFont(R.font.helvetica)
                        tvStaticClientApprovalFOS.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticClientApprovalFOS.textSize = 14F

                        tvStaticClientApprovalFOS.text = "ClientApprovalFOS:  "
                        row7.addView(tvStaticClientApprovalFOS)

                        val tvDynamicClientApprovalFOS = TextView(context)
                        tvDynamicClientApprovalFOS.text = "${
                            modelObject.ClientApprovalFOS
                        }"
                        row7.addView(tvDynamicClientApprovalFOS)

                        tableLayout.addView(row7)


                        //eigth Row
                        val row8 = TableRow(context)

                        val tvStaticFinalApprovedDrawingAttached = TextView(context)
                        tvStaticFinalApprovedDrawingAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticFinalApprovedDrawingAttached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticFinalApprovedDrawingAttached.textSize = 14F

                        tvStaticFinalApprovedDrawingAttached.text =
                            "FinalApprovedDrawingAttached:  "
                        row8.addView(tvStaticFinalApprovedDrawingAttached)

                        val tvDynamicFinalApprovedDrawingAttached = TextView(context)
                        tvDynamicFinalApprovedDrawingAttached.text = "${
                            modelObject.FinalApprovedDrawingAttached
                        }"
                        row8.addView(tvDynamicFinalApprovedDrawingAttached)

                        tableLayout.addView(row8)

                        //ninth Row
                        val row9 = TableRow(context)

                        val tvStaticFinalSupplierofferAttached = TextView(context)
                        tvStaticFinalSupplierofferAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticFinalSupplierofferAttached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticFinalSupplierofferAttached.textSize = 14F

                        tvStaticFinalSupplierofferAttached.text = "FinalSupplierofferAttached:  "
                        row9.addView(tvStaticFinalSupplierofferAttached)

                        val tvDynamicFinalSupplierofferAttached = TextView(context)
                        tvDynamicFinalSupplierofferAttached.text = "${
                            modelObject.FinalSupplierofferAttached
                        }"
                        row9.addView(tvDynamicFinalSupplierofferAttached)

                        tableLayout.addView(row9)

                        //tenth Row
                        val row10 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row10.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row10.addView(tvDynamicRemark)

                        tableLayout.addView(row10)


                        //eleven Row
                        val row11 = TableRow(context)

                        val tvStaticProjectNumber = TextView(context)
                        tvStaticProjectNumber.typeface = resources.getFont(R.font.helvetica)
                        tvStaticProjectNumber.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticProjectNumber.textSize = 14F

                        tvStaticProjectNumber.text = "ProjectNumber:  "
                        row11.addView(tvStaticRemark)

                        val tvDynamicProjectNumber = TextView(context)
                        tvDynamicProjectNumber.text = "${
                            modelObject.ProjectNumber
                        }"
                        row11.addView(tvDynamicProjectNumber)

                        tableLayout.addView(row11)


                        //twelve Row
                        val row12 = TableRow(context)

                        val tvStaticRequestedName = TextView(context)
                        tvStaticRequestedName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRequestedName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRequestedName.textSize = 14F

                        tvStaticRequestedName.text = "RequestedName:  "
                        row12.addView(tvStaticRemark)

                        val tvDynamicRequestedName = TextView(context)
                        tvDynamicRequestedName.text = "${
                            modelObject.RequestedName
                        }"
                        row12.addView(tvDynamicRequestedName)

                        tableLayout.addView(row12)

                        //Thirteen Row
                        val row13 = TableRow(context)

                        val tvStaticBranchName = TextView(context)
                        tvStaticBranchName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticBranchName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticBranchName.textSize = 14F

                        tvStaticBranchName.text = "BranchName:  "
                        row13.addView(tvStaticRemark)

                        val tvDynamicBranchName = TextView(context)
                        tvDynamicBranchName.text = "${
                            modelObject.BranchName
                        }"
                        row13.addView(tvDynamicBranchName)

                        tableLayout.addView(row13)


                    }

                    Global.MATERIAL_DISPATCHED_SUBTYPE -> {
                        val modelObject = convertToModelObjectMaterialDispatch(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectMaterialDispatch: ${modelObject.toString()}"
                        )

                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticLogisticPerson = TextView(context)
                        tvStaticLogisticPerson.typeface = resources.getFont(R.font.helvetica)
                        tvStaticLogisticPerson.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticLogisticPerson.textSize = 14F

                        tvStaticLogisticPerson.text = "LogisticPerson:  "
                        row1.addView(tvStaticLogisticPerson)

                        val tvDynamicLogisticPerson = TextView(context)
                        tvDynamicLogisticPerson.text = "${
                            modelObject.LogisticPerson
                        }"
                        row1.addView(tvDynamicLogisticPerson)

                        tableLayout.addView(row1)


                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticShipmentConsignmentNo = TextView(context)
                        tvStaticShipmentConsignmentNo.typeface = resources.getFont(R.font.helvetica)
                        tvStaticShipmentConsignmentNo.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticShipmentConsignmentNo.textSize = 14F

                        tvStaticShipmentConsignmentNo.text = "ShipmentConsignmentNo:  "
                        row2.addView(tvStaticShipmentConsignmentNo)

                        val tvDynamicShipmentConsignmentNo = TextView(context)
                        tvDynamicShipmentConsignmentNo.text = "${
                            modelObject.ShipmentCsgNo
                        }"
                        row2.addView(tvDynamicShipmentConsignmentNo)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticContactNo = TextView(context)
                        tvStaticContactNo.typeface = resources.getFont(R.font.helvetica)
                        tvStaticContactNo.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticContactNo.textSize = 14F

                        tvStaticContactNo.text = "ContactNo:  "
                        row3.addView(tvStaticContactNo)

                        val tvDynamicContactNo = TextView(context)
                        tvDynamicContactNo.text = "${
                            modelObject.ContactNo
                        }"
                        row3.addView(tvDynamicContactNo)

                        tableLayout.addView(row3)


                        //fouth Row
                        val row4 = TableRow(context)

                        val tvStaticShippedDate = TextView(context)
                        tvStaticShippedDate.typeface = resources.getFont(R.font.helvetica)
                        tvStaticShippedDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticShippedDate.textSize = 14F

                        tvStaticShippedDate.text = "ShippedDate:  "
                        row4.addView(tvStaticShippedDate)

                        val tvDynamicShippedDate = TextView(context)
                        tvDynamicShippedDate.text = "${
                            modelObject.ShippedDate
                        }"
                        row4.addView(tvDynamicShippedDate)

                        tableLayout.addView(row4)

                        //Fifth Row
                        val row5 = TableRow(context)

                        val tvStaticExpectedDateofDelivery = TextView(context)
                        tvStaticExpectedDateofDelivery.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticExpectedDateofDelivery.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticExpectedDateofDelivery.textSize = 14F

                        tvStaticExpectedDateofDelivery.text = "ExpectedDateofDelivery:  "
                        row5.addView(tvStaticExpectedDateofDelivery)

                        val tvDynamicExpectedDateofDelivery = TextView(context)
                        tvDynamicExpectedDateofDelivery.text = "${
                            modelObject.ExpDeliveryDate
                        }"
                        row5.addView(tvDynamicExpectedDateofDelivery)

                        tableLayout.addView(row5)

                        //Sixth Row
                        val row6 = TableRow(context)

                        val tvStaticConsignmentDocumentsAttached = TextView(context)
                        tvStaticConsignmentDocumentsAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticConsignmentDocumentsAttached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticConsignmentDocumentsAttached.textSize = 14F

                        tvStaticConsignmentDocumentsAttached.text =
                            "ConsignmentDocumentsAttached:  "
                        row6.addView(tvStaticConsignmentDocumentsAttached)

                        val tvDynamicConsignmentDocumentsAttached = TextView(context)
                        tvDynamicConsignmentDocumentsAttached.text = "${
                            modelObject.ConsignmentDocumentsAttached
                        }"
                        row6.addView(tvDynamicConsignmentDocumentsAttached)

                        tableLayout.addView(row6)


                        //seven Row
                        val row7 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row7.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row7.addView(tvDynamicRemark)

                        tableLayout.addView(row7)


                    }
                    Global.SITE_READY_EVALUATION_SUBTYPE -> {
                        val modelObject = convertToModelObjectSiteReadinessEvaluation(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectSiteReadinessEvaluation: ${modelObject.toString()}"
                        )
                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticSiteEngineerName = TextView(context)
                        tvStaticSiteEngineerName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticSiteEngineerName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticSiteEngineerName.textSize = 14F

                        tvStaticSiteEngineerName.text = "SiteEngineerName:  "
                        row1.addView(tvStaticSiteEngineerName)

                        val tvDynamicSiteEngineerName = TextView(context)
                        tvDynamicSiteEngineerName.text = "${
                            modelObject.SiteEngineerName
                        }"
                        row1.addView(tvDynamicSiteEngineerName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticCivilWorkStatus = TextView(context)
                        tvStaticCivilWorkStatus.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCivilWorkStatus.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCivilWorkStatus.textSize = 14F

                        tvStaticCivilWorkStatus.text = "CivilWorkStatus:  "
                        row2.addView(tvStaticCivilWorkStatus)

                        val tvDynamicCivilWorkStatus = TextView(context)
                        tvDynamicCivilWorkStatus.text = "${
                            modelObject.CivilWorkStatus
                        }"
                        row2.addView(tvDynamicCivilWorkStatus)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticCivilWorkDetails = TextView(context)
                        tvStaticCivilWorkDetails.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCivilWorkDetails.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCivilWorkDetails.textSize = 14F

                        tvStaticCivilWorkDetails.text = "CivilWorkDetails:  "
                        row3.addView(tvStaticCivilWorkDetails)

                        val tvDynamicCivilWorkDetails = TextView(context)
                        tvDynamicCivilWorkDetails.text = "${
                            modelObject.CivilWorkDetails
                        }"
                        row3.addView(tvDynamicCivilWorkDetails)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticScaffoldingRequired = TextView(context)
                        tvStaticScaffoldingRequired.typeface = resources.getFont(R.font.helvetica)
                        tvStaticScaffoldingRequired.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticScaffoldingRequired.textSize = 14F

                        tvStaticScaffoldingRequired.text = "ScaffoldingRequired:  "
                        row4.addView(tvStaticScaffoldingRequired)

                        val tvDynamicScaffoldingRequired = TextView(context)
                        tvDynamicScaffoldingRequired.text = "${
                            modelObject.ScaffoldingRequired
                        }"
                        row4.addView(tvDynamicScaffoldingRequired)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticScaffoldingBy = TextView(context)
                        tvStaticScaffoldingBy.typeface = resources.getFont(R.font.helvetica)
                        tvStaticScaffoldingBy.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticScaffoldingBy.textSize = 14F

                        tvStaticScaffoldingBy.text = "ScaffoldingBy:  "
                        row5.addView(tvStaticScaffoldingBy)

                        val tvDynamicScaffoldingBy = TextView(context)
                        tvDynamicScaffoldingBy.text = "${
                            modelObject.ScaffoldingBy
                        }"
                        row5.addView(tvDynamicScaffoldingBy)

                        tableLayout.addView(row5)

                        //sixth Row
                        val row6 = TableRow(context)

                        val tvStaticScaffoldingType = TextView(context)
                        tvStaticScaffoldingType.typeface = resources.getFont(R.font.helvetica)
                        tvStaticScaffoldingType.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticScaffoldingType.textSize = 14F

                        tvStaticScaffoldingType.text = "ScaffoldingType:  "
                        row6.addView(tvStaticScaffoldingType)

                        val tvDynamicScaffoldingType = TextView(context)
                        tvDynamicScaffoldingType.text = "${
                            modelObject.ScaffoldingType
                        }"
                        row6.addView(tvDynamicScaffoldingType)

                        tableLayout.addView(row6)


                        //seventh Row
                        val row7 = TableRow(context)

                        val tvStaticScaffoldingInspectionReportAttached = TextView(context)
                        tvStaticScaffoldingInspectionReportAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticScaffoldingInspectionReportAttached.setTextColor(
                            resources.getColor(
                                taimoor.sultani.sweetalert2.R.color.text_color
                            )
                        )
                        tvStaticScaffoldingInspectionReportAttached.textSize = 14F

                        tvStaticScaffoldingInspectionReportAttached.text =
                            "ScaffoldingInspectionReportAttached:  "
                        row7.addView(tvStaticScaffoldingInspectionReportAttached)

                        val tvDynamicScaffoldingInspectionReportAttached = TextView(context)
                        tvDynamicScaffoldingInspectionReportAttached.text = "${
                            modelObject.ScaffoldingInspectionReportAttached
                        }"
                        row7.addView(tvDynamicScaffoldingInspectionReportAttached)

                        tableLayout.addView(row7)

                        //eigth Row
                        val row8 = TableRow(context)

                        val tvStaticScaffoldingStatus = TextView(context)
                        tvStaticScaffoldingStatus.typeface = resources.getFont(R.font.helvetica)
                        tvStaticScaffoldingStatus.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticScaffoldingStatus.textSize = 14F

                        tvStaticScaffoldingStatus.text = "ScaffoldingStatus:  "
                        row8.addView(tvStaticScaffoldingStatus)

                        val tvDynamicScaffoldingStatus = TextView(context)
                        tvDynamicScaffoldingStatus.text = "${
                            modelObject.ScaffoldingStatus
                        }"
                        row8.addView(tvDynamicScaffoldingStatus)

                        tableLayout.addView(row8)


                        //nine Row
                        val row9 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row9.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row9.addView(tvDynamicRemark)

                        tableLayout.addView(row9)


                    }

                    Global.MATERIAL_DELIVERED_SUBTYPE -> {
                        val modelObject = convertToModelObjectSiteMaterialDelivered(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectSiteMaterialDelivered: ${modelObject.toString()}"
                        )

                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticLogisticPersonName = TextView(context)
                        tvStaticLogisticPersonName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticLogisticPersonName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticLogisticPersonName.textSize = 14F

                        tvStaticLogisticPersonName.text = "LogisticPersonName:  "
                        row1.addView(tvStaticLogisticPersonName)

                        val tvDynamicLogisticPersonName = TextView(context)
                        tvDynamicLogisticPersonName.text = "${
                            modelObject.LogisticPersonName
                        }"
                        row1.addView(tvDynamicLogisticPersonName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticClientName = TextView(context)
                        tvStaticClientName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticClientName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticClientName.textSize = 14F

                        tvStaticClientName.text = "ClientName:  "
                        row2.addView(tvStaticClientName)

                        val tvDynamicClientName = TextView(context)
                        tvDynamicClientName.text = "${
                            modelObject.ClientName
                        }"
                        row2.addView(tvDynamicClientName)

                        tableLayout.addView(row2)


                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticRecepientName = TextView(context)
                        tvStaticRecepientName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRecepientName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRecepientName.textSize = 14F

                        tvStaticRecepientName.text = "RecepientName:  "
                        row3.addView(tvStaticClientName)

                        val tvDynamicRecepientName = TextView(context)
                        tvDynamicRecepientName.text = "${
                            modelObject.RecepientName
                        }"
                        row3.addView(tvDynamicRecepientName)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticRecepientPhoneNo = TextView(context)
                        tvStaticRecepientPhoneNo.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRecepientPhoneNo.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRecepientPhoneNo.textSize = 14F

                        tvStaticRecepientPhoneNo.text = "RecepientPhoneNo:  "
                        row4.addView(tvStaticRecepientPhoneNo)

                        val tvDynamicRecepientPhoneNoe = TextView(context)
                        tvDynamicRecepientPhoneNoe.text = "${
                            modelObject.RecepientPhoneNo
                        }"
                        row4.addView(tvDynamicRecepientPhoneNoe)

                        tableLayout.addView(row4)


                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticRecepientEmirateID = TextView(context)
                        tvStaticRecepientEmirateID.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRecepientEmirateID.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRecepientEmirateID.textSize = 14F

                        tvStaticRecepientEmirateID.text = "RecepientEmirateID:  "
                        row5.addView(tvStaticRecepientEmirateID)

                        val tvDynamicRecepientEmirateID = TextView(context)
                        tvDynamicRecepientEmirateID.text = "${
                            modelObject.RecepientEmirateID
                        }"
                        row5.addView(tvDynamicRecepientEmirateID)

                        tableLayout.addView(row5)


                        //sixth Row
                        val row6 = TableRow(context)

                        val tvStaticDeliveryStatus = TextView(context)
                        tvStaticDeliveryStatus.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDeliveryStatus.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDeliveryStatus.textSize = 14F

                        tvStaticDeliveryStatus.text = "DeliveryStatus:  "
                        row6.addView(tvStaticDeliveryStatus)

                        val tvDynamicDeliveryStatus = TextView(context)
                        tvDynamicDeliveryStatus.text = "${
                            modelObject.DeliveryStatus
                        }"
                        row6.addView(tvDynamicDeliveryStatus)

                        tableLayout.addView(row6)

                        //seventh Row
                        val row7 = TableRow(context)

                        val tvStaticProofofDeliveryAttached = TextView(context)
                        tvStaticProofofDeliveryAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticProofofDeliveryAttached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticProofofDeliveryAttached.textSize = 14F

                        tvStaticProofofDeliveryAttached.text = "ProofofDeliveryAttached:  "
                        row7.addView(tvStaticProofofDeliveryAttached)

                        val tvDynamicProofofDeliveryAttached = TextView(context)
                        tvDynamicProofofDeliveryAttached.text = "${
                            modelObject.ProofofDeliveryAttached
                        }"
                        row7.addView(tvDynamicProofofDeliveryAttached)

                        tableLayout.addView(row7)

                        //eigth Row

                        val row8 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row8.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row8.addView(tvDynamicRemark)

                        tableLayout.addView(row8)


                    }

                    Global.INSTALLATION_INITIATION_SUBTYPE -> {
                        val modelObject = convertToModelObjectInstallInitiation(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectInstallInitiation: ${modelObject.toString()}"
                        )

                        //first Row

                        val row1 = TableRow(context)

                        val tvStaticPaymentStatus = TextView(context)
                        tvStaticPaymentStatus.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPaymentStatus.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPaymentStatus.textSize = 14F

                        tvStaticPaymentStatus.text = "PaymentStatus:  "
                        row1.addView(tvStaticPaymentStatus)

                        val tvDynamicPaymentStatus = TextView(context)
                        tvDynamicPaymentStatus.text = "${
                            modelObject.PaymentStatus
                        }"
                        row1.addView(tvDynamicPaymentStatus)

                        tableLayout.addView(row1)

                        //second Row

                        val row2 = TableRow(context)

                        val tvStaticPendingAmount = TextView(context)
                        tvStaticPendingAmount.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPendingAmount.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPendingAmount.textSize = 14F

                        tvStaticPendingAmount.text = "PendingAmount:  "
                        row2.addView(tvStaticPendingAmount)

                        val tvDynamicPendingAmount = TextView(context)
                        tvDynamicPendingAmount.text = "${
                            modelObject.PendingAmount
                        }"
                        row2.addView(tvDynamicPendingAmount)

                        tableLayout.addView(row2)

                        //third row
                        val row3 = TableRow(context)

                        val tvStaticInvoiceID = TextView(context)
                        tvStaticInvoiceID.typeface = resources.getFont(R.font.helvetica)
                        tvStaticInvoiceID.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticInvoiceID.textSize = 14F

                        tvStaticInvoiceID.text = "InvoiceID:  "
                        row3.addView(tvStaticInvoiceID)

                        val tvDynamicInvoiceID = TextView(context)
                        tvDynamicInvoiceID.text = "${
                            modelObject.InvoiceID
                        }"
                        row3.addView(tvDynamicInvoiceID)

                        tableLayout.addView(row3)

                        //forth row
                        val row4 = TableRow(context)

                        val tvStaticInvoiceDate = TextView(context)
                        tvStaticInvoiceDate.typeface = resources.getFont(R.font.helvetica)
                        tvStaticInvoiceDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticInvoiceDate.textSize = 14F

                        tvStaticInvoiceDate.text = "InvoiceDate:  "
                        row4.addView(tvStaticInvoiceDate)

                        val tvDynamicInvoiceDate = TextView(context)
                        tvDynamicInvoiceDate.text = "${
                            modelObject.InvoiceDate
                        }"
                        row4.addView(tvDynamicInvoiceDate)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row5.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row5.addView(tvDynamicRemark)

                        tableLayout.addView(row5)


                    }

                    Global.QUALITY_INSPECTION_1_SUBTYPE -> {
                        val modelObject = convertToModelObjectQualityInspection1(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectQualityInspection1: ${modelObject.toString()}"
                        )
                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticQualityPersonName = TextView(context)
                        tvStaticQualityPersonName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticQualityPersonName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticQualityPersonName.textSize = 14F

                        tvStaticQualityPersonName.text = "QualityPersonName:  "
                        row1.addView(tvStaticQualityPersonName)

                        val tvDynamicQualityPersonName = TextView(context)
                        tvDynamicQualityPersonName.text = "${
                            modelObject.QualityPersonName
                        }"
                        row1.addView(tvDynamicQualityPersonName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticDiscrepenciesFound = TextView(context)
                        tvStaticDiscrepenciesFound.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDiscrepenciesFound.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDiscrepenciesFound.textSize = 14F

                        tvStaticDiscrepenciesFound.text = "DiscrepenciesFound:  "
                        row2.addView(tvStaticDiscrepenciesFound)

                        val tvDynamicDiscrepenciesFound = TextView(context)
                        tvDynamicQualityPersonName.text = "${
                            modelObject.DiscrepenciesFound
                        }"
                        row2.addView(tvDynamicDiscrepenciesFound)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticCorrectiveActionRequired = TextView(context)
                        tvStaticCorrectiveActionRequired.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticCorrectiveActionRequired.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCorrectiveActionRequired.textSize = 14F

                        tvStaticCorrectiveActionRequired.text = "CorrectiveActionRequired :  "
                        row3.addView(tvStaticCorrectiveActionRequired)

                        val tvDynamicCorrectiveActionRequired = TextView(context)
                        tvDynamicCorrectiveActionRequired.text = "${
                            modelObject.CorrectiveActionRequired
                        }"
                        row3.addView(tvDynamicCorrectiveActionRequired)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticQualityCheckReportAttached = TextView(context)
                        tvStaticQualityCheckReportAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticQualityCheckReportAttached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticQualityCheckReportAttached.textSize = 14F

                        tvStaticQualityCheckReportAttached.text = "QualityCheckReportAttached :  "
                        row4.addView(tvStaticQualityCheckReportAttached)

                        val tvDynamicQualityCheckReportAttached = TextView(context)
                        tvDynamicQualityCheckReportAttached.text = "${
                            modelObject.QualityCheckReportAttached
                        }"
                        row4.addView(tvDynamicQualityCheckReportAttached)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticQualityRating = TextView(context)
                        tvStaticQualityRating.typeface = resources.getFont(R.font.helvetica)
                        tvStaticQualityRating.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticQualityRating.textSize = 14F

                        tvStaticQualityRating.text = "QualityRating :  "
                        row5.addView(tvStaticQualityRating)

                        val tvDynamicQualityRating = TextView(context)
                        tvDynamicQualityRating.text = "${
                            modelObject.QualityRating
                        }"
                        row5.addView(tvDynamicQualityRating)

                        tableLayout.addView(row5)


                        //sixth Row
                        val row6 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row6.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row6.addView(tvDynamicRemark)

                        tableLayout.addView(row6)


                    }

                    Global.TESTING_AND_COMMISIONING_SUBTYPE -> {
                        val modelObject = convertToModelObjectTestingAndComissioning(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectTestingAndComissioning: ${modelObject.toString()}"
                        )
                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticCompletionReport = TextView(context)
                        tvStaticCompletionReport.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCompletionReport.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCompletionReport.textSize = 14F

                        tvStaticCompletionReport.text = "CompletionReport:  "
                        row1.addView(tvStaticCompletionReport)

                        val tvDynamicCompletionReport = TextView(context)
                        tvDynamicCompletionReport.text = "${
                            modelObject.CompletionReport
                        }"
                        row1.addView(tvDynamicCompletionReport)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticPowerSupplyAvailability = TextView(context)
                        tvStaticPowerSupplyAvailability.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticPowerSupplyAvailability.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPowerSupplyAvailability.textSize = 14F

                        tvStaticPowerSupplyAvailability.text = "PowerSupplyAvailability:  "
                        row2.addView(tvStaticPowerSupplyAvailability)

                        val tvDynamicPowerSupplyAvailability = TextView(context)
                        tvDynamicPowerSupplyAvailability.text = "${
                            modelObject.PowerSupplyAvailability
                        }"
                        row2.addView(tvDynamicPowerSupplyAvailability)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticPaymentConfirmation = TextView(context)
                        tvStaticPaymentConfirmation.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPaymentConfirmation.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPaymentConfirmation.textSize = 14F

                        tvStaticPaymentConfirmation.text = "PaymentConfirmation:  "
                        row3.addView(tvStaticPaymentConfirmation)

                        val tvDynamicPaymentConfirmation = TextView(context)
                        tvDynamicPaymentConfirmation.text = "${
                            modelObject.PaymentConfirmation
                        }"
                        row3.addView(tvDynamicPaymentConfirmation)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticInvoice = TextView(context)
                        tvStaticInvoice.typeface = resources.getFont(R.font.helvetica)
                        tvStaticInvoice.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticInvoice.textSize = 14F

                        tvStaticInvoice.text = "Invoice:  "
                        row4.addView(tvStaticInvoice)

                        val tvDynamicInvoice = TextView(context)
                        tvDynamicInvoice.text = "${
                            modelObject.Invoice
                        }"
                        row4.addView(tvDynamicInvoice)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticSupplyAvailabilityDate = TextView(context)
                        tvStaticSupplyAvailabilityDate.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticSupplyAvailabilityDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticSupplyAvailabilityDate.textSize = 14F

                        tvStaticSupplyAvailabilityDate.text = "SupplyAvailabilityDate:  "
                        row5.addView(tvStaticSupplyAvailabilityDate)

                        val tvDynamicSupplyAvailabilityDate = TextView(context)
                        tvDynamicSupplyAvailabilityDate.text = "${
                            modelObject.SupplyAvailabilityDate
                        }"
                        row5.addView(tvDynamicSupplyAvailabilityDate)

                        tableLayout.addView(row5)

                        //sixth Row
                        val row6 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row6.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row6.addView(tvDynamicRemark)

                        tableLayout.addView(row6)

                    }

                    Global.QUALITY_INSPECTION_2_SUBTYPE -> {
                        val modelObject = convertToModelObjectQualityInspectionTwo(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectQualityInspectionTwo: ${modelObject.toString()}"
                        )
                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticQualityPersonName = TextView(context)
                        tvStaticQualityPersonName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticQualityPersonName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticQualityPersonName.textSize = 14F

                        tvStaticQualityPersonName.text = "QualityPersonName:  "
                        row1.addView(tvStaticQualityPersonName)

                        val tvDynamicQualityPersonName = TextView(context)
                        tvDynamicQualityPersonName.text = "${
                            modelObject.QualityPersonName
                        }"
                        row1.addView(tvDynamicQualityPersonName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticDiscrepenciesFound = TextView(context)
                        tvStaticDiscrepenciesFound.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDiscrepenciesFound.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDiscrepenciesFound.textSize = 14F

                        tvStaticDiscrepenciesFound.text = "DiscrepenciesFound:  "
                        row2.addView(tvStaticDiscrepenciesFound)

                        val tvDynamicDiscrepenciesFound = TextView(context)
                        tvDynamicDiscrepenciesFound.text = "${
                            modelObject.DiscrepenciesFound
                        }"
                        row2.addView(tvDynamicDiscrepenciesFound)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticCorrectiveActionRequired = TextView(context)
                        tvStaticCorrectiveActionRequired.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticCorrectiveActionRequired.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCorrectiveActionRequired.textSize = 14F

                        tvStaticCorrectiveActionRequired.text = "CorrectiveActionRequired:  "
                        row3.addView(tvStaticCorrectiveActionRequired)

                        val tvDynamicCorrectiveActionRequired = TextView(context)
                        tvDynamicCorrectiveActionRequired.text = "${
                            modelObject.CorrectiveActionRequired
                        }"
                        row3.addView(tvDynamicCorrectiveActionRequired)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticQualityCheckReportAttached = TextView(context)
                        tvStaticQualityCheckReportAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticQualityCheckReportAttached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticQualityCheckReportAttached.textSize = 14F

                        tvStaticQualityCheckReportAttached.text = "QualityCheckReportAttached:  "
                        row4.addView(tvStaticQualityCheckReportAttached)

                        val tvDynamicQualityCheckReportAttached = TextView(context)
                        tvDynamicQualityCheckReportAttached.text = "${
                            modelObject.QualityCheckReportAttached
                        }"
                        row4.addView(tvDynamicQualityCheckReportAttached)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticCreatedRequest = TextView(context)
                        tvStaticCreatedRequest.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCreatedRequest.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCreatedRequest.textSize = 14F

                        tvStaticCreatedRequest.text = "CreatedRequest:  "
                        row5.addView(tvStaticCreatedRequest)

                        val tvDynamicCreatedRequest = TextView(context)
                        tvDynamicCreatedRequest.text = "${
                            modelObject.CreatedRequest
                        }"
                        row5.addView(tvDynamicCreatedRequest)

                        tableLayout.addView(row5)

                        //sixth Row
                        val row6 = TableRow(context)

                        val tvStaticQualityRating = TextView(context)
                        tvStaticQualityRating.typeface = resources.getFont(R.font.helvetica)
                        tvStaticQualityRating.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticQualityRating.textSize = 14F

                        tvStaticQualityRating.text = "QualityRating:  "
                        row6.addView(tvStaticQualityRating)

                        val tvDynamicQualityRating = TextView(context)
                        tvDynamicQualityRating.text = "${
                            modelObject.QualityRating
                        }"
                        row6.addView(tvDynamicQualityRating)

                        tableLayout.addView(row6)

                        //seventh Row
                        val row7 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row7.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row7.addView(tvDynamicRemark)

                        tableLayout.addView(row7)

                    }

                    Global.THIRD_PARTY_INSPECTION_SUBTYPE -> {
                        val modelObject = convertToModelObjectThirdPartyInspection(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectThirdPartyInspection: ${modelObject.toString()}"
                        )

                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticSupervisorName = TextView(context)
                        tvStaticSupervisorName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticSupervisorName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticSupervisorName.textSize = 14F

                        tvStaticSupervisorName.text = "SupervisorName:  "
                        row1.addView(tvStaticSupervisorName)

                        val tvDynamicSupervisorName = TextView(context)
                        tvDynamicSupervisorName.text = "${
                            modelObject.SupervisorName
                        }"
                        row1.addView(tvDynamicSupervisorName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticthirdrdPartyOrgName = TextView(context)
                        tvStaticthirdrdPartyOrgName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticthirdrdPartyOrgName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticthirdrdPartyOrgName.textSize = 14F

                        tvStaticthirdrdPartyOrgName.text = "thirdrdPartyOrgName:  "
                        row2.addView(tvStaticthirdrdPartyOrgName)

                        val tvDynamicthirdrdPartyOrgName = TextView(context)
                        tvDynamicthirdrdPartyOrgName.text = "${
                            modelObject.thirdrdPartyOrgName
                        }"
                        row2.addView(tvDynamicthirdrdPartyOrgName)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticCertificationDate = TextView(context)
                        tvStaticCertificationDate.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCertificationDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCertificationDate.textSize = 14F

                        tvStaticCertificationDate.text = "CertificationDate:  "
                        row3.addView(tvStaticCertificationDate)

                        val tvDynamicCertificationDate = TextView(context)
                        tvDynamicCertificationDate.text = "${
                            modelObject.CertificationDate
                        }"
                        row3.addView(tvDynamicCertificationDate)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticValidTill = TextView(context)
                        tvStaticValidTill.typeface = resources.getFont(R.font.helvetica)
                        tvStaticValidTill.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticValidTill.textSize = 14F

                        tvStaticValidTill.text = "ValidTill:  "
                        row4.addView(tvStaticValidTill)

                        val tvDynamicValidTill = TextView(context)
                        tvDynamicValidTill.text = "${
                            modelObject.ValidTill
                        }"
                        row4.addView(tvDynamicValidTill)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row5.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row5.addView(tvDynamicRemark)

                        tableLayout.addView(row5)

                    }

                    Global.MAINTENANCE_INSPECTION_SUBTYPE -> {
                        val modelObject = convertToModelObjectMaintenanceInspection(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectMaintenanceInspection: ${modelObject.toString()}"
                        )

                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticLineManagerName = TextView(context)
                        tvStaticLineManagerName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticLineManagerName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticLineManagerName.textSize = 14F

                        tvStaticLineManagerName.text = "LineManagerName:  "
                        row1.addView(tvStaticLineManagerName)

                        val tvDynamicLineManagerName = TextView(context)
                        tvDynamicLineManagerName.text = "${
                            modelObject.LineManagerName
                        }"
                        row1.addView(tvDynamicLineManagerName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticThirdPartyInspectionReportAttached = TextView(context)
                        tvStaticThirdPartyInspectionReportAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticThirdPartyInspectionReportAttached.setTextColor(
                            resources.getColor(
                                taimoor.sultani.sweetalert2.R.color.text_color
                            )
                        )
                        tvStaticThirdPartyInspectionReportAttached.textSize = 14F

                        tvStaticThirdPartyInspectionReportAttached.text =
                            "ThirdPartyInspectionReportAttached:  "
                        row2.addView(tvStaticThirdPartyInspectionReportAttached)

                        val tvDynamicThirdPartyInspectionReportAttached = TextView(context)
                        tvDynamicThirdPartyInspectionReportAttached.text = "${
                            modelObject.ThirdPartyInspectionReportAttached
                        }"
                        row2.addView(tvDynamicThirdPartyInspectionReportAttached)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticMNo = TextView(context)
                        tvStaticMNo.typeface = resources.getFont(R.font.helvetica)
                        tvStaticMNo.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticMNo.textSize = 14F

                        tvStaticMNo.text = "MNo:  "
                        row3.addView(tvStaticMNo)

                        val tvDynamicMNo = TextView(context)
                        tvDynamicThirdPartyInspectionReportAttached.text = "${
                            modelObject.MNo
                        }"
                        row3.addView(tvDynamicMNo)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticApprovedQualityInspectiontwoAttached = TextView(context)
                        tvStaticApprovedQualityInspectiontwoAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticApprovedQualityInspectiontwoAttached.setTextColor(
                            resources.getColor(
                                taimoor.sultani.sweetalert2.R.color.text_color
                            )
                        )
                        tvStaticApprovedQualityInspectiontwoAttached.textSize = 14F

                        tvStaticApprovedQualityInspectiontwoAttached.text =
                            "ApprovedQualityInspectiontwoAttached:  "
                        row4.addView(tvStaticApprovedQualityInspectiontwoAttached)

                        val tvDynamicApprovedQualityInspectiontwoAttached = TextView(context)
                        tvDynamicApprovedQualityInspectiontwoAttached.text = "${
                            modelObject.ApprovedQualityInspectiontwoAttached
                        }"
                        row4.addView(tvDynamicApprovedQualityInspectiontwoAttached)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row5.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row5.addView(tvDynamicRemark)

                        tableLayout.addView(row5)


                    }

                    Global.HANDOVER_TO_CLIENT_SUBTYPE -> {
                        val modelObject = convertToModelObjectHandOverToClient(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectHandOverToClient: ${modelObject.toString()}"
                        )

                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticSupervisorName = TextView(context)
                        tvStaticSupervisorName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticSupervisorName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticSupervisorName.textSize = 14F
                        tvStaticSupervisorName.text = "SupervisorName:  "
                        row1.addView(tvStaticSupervisorName)

                        val tvDynamicSupervisorName = TextView(context)
                        tvDynamicSupervisorName.text = "${
                            modelObject.SupervisorName
                        }"
                        row1.addView(tvDynamicSupervisorName)

                        tableLayout.addView(row1)

                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticPaymentStatus = TextView(context)
                        tvStaticPaymentStatus.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPaymentStatus.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPaymentStatus.textSize = 14F
                        tvStaticPaymentStatus.text = "PaymentStatus:  "
                        row2.addView(tvStaticPaymentStatus)

                        val tvDynamicPaymentStatus = TextView(context)
                        tvDynamicPaymentStatus.text = "${
                            modelObject.PaymentStatus
                        }"
                        row2.addView(tvDynamicPaymentStatus)

                        tableLayout.addView(row2)

                        //third Row
                        val row3 = TableRow(context)

                        val tvStaticTrainingStatus = TextView(context)
                        tvStaticTrainingStatus.typeface = resources.getFont(R.font.helvetica)
                        tvStaticTrainingStatus.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticTrainingStatus.textSize = 14F
                        tvStaticTrainingStatus.text = "TrainingStatus:  "
                        row3.addView(tvStaticTrainingStatus)

                        val tvDynamicTrainingStatus = TextView(context)
                        tvDynamicTrainingStatus.text = "${
                            modelObject.TrainingStatus
                        }"
                        row3.addView(tvDynamicTrainingStatus)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticMaintenanceInspectionReportAttached = TextView(context)
                        tvStaticMaintenanceInspectionReportAttached.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticMaintenanceInspectionReportAttached.setTextColor(
                            resources.getColor(
                                taimoor.sultani.sweetalert2.R.color.text_color
                            )
                        )
                        tvStaticMaintenanceInspectionReportAttached.textSize = 14F
                        tvStaticMaintenanceInspectionReportAttached.text = "TrainingStatus:  "
                        row4.addView(tvStaticMaintenanceInspectionReportAttached)

                        val tvDynamicMaintenanceInspectionReportAttached = TextView(context)
                        tvDynamicMaintenanceInspectionReportAttached.text = "${
                            modelObject.MaintenanceInspectionReportAttached
                        }"
                        row4.addView(tvDynamicMaintenanceInspectionReportAttached)

                        tableLayout.addView(row4)


                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row5.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row5.addView(tvDynamicRemark)

                        tableLayout.addView(row5)
                    }

                    Global.TRANSFER_TO_MAINTENANCE_SUBTYPE -> {
                        val modelObject = convertToModelObjectTransferToMaintenance(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectTransferToMaintenance: ${modelObject.toString()}"
                        )
                        //first Row
                        val row1 = TableRow(context)

                        val tvStaticLineManagerName = TextView(context)
                        tvStaticLineManagerName.typeface = resources.getFont(R.font.helvetica)
                        tvStaticLineManagerName.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticLineManagerName.textSize = 14F

                        tvStaticLineManagerName.text = "LineManagerName:  "
                        row1.addView(tvStaticLineManagerName)

                        val tvDynamicLineManagerName = TextView(context)
                        tvDynamicLineManagerName.text = "${
                            modelObject.LineManagerName
                        }"
                        row1.addView(tvDynamicLineManagerName)

                        tableLayout.addView(row1)


                        //second Row
                        val row2 = TableRow(context)

                        val tvStaticDocumentsattached = TextView(context)
                        tvStaticDocumentsattached.typeface = resources.getFont(R.font.helvetica)
                        tvStaticDocumentsattached.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticDocumentsattached.textSize = 14F

                        tvStaticDocumentsattached.text = "Documentsattached:  "
                        row2.addView(tvStaticDocumentsattached)

                        val tvDynamicDocumentsattached = TextView(context)
                        tvDynamicLineManagerName.text = "${
                            modelObject.Documentsattached
                        }"
                        row2.addView(tvDynamicDocumentsattached)

                        tableLayout.addView(row2)

                        //thirid Row
                        val row3 = TableRow(context)

                        val tvStaticProjectNo = TextView(context)
                        tvStaticProjectNo.typeface = resources.getFont(R.font.helvetica)
                        tvStaticProjectNo.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticProjectNo.textSize = 14F

                        tvStaticProjectNo.text = "ProjectNo:  "
                        row3.addView(tvStaticProjectNo)

                        val tvDynamicProjectNo = TextView(context)
                        tvDynamicProjectNo.text = "${
                            modelObject.ProjectNo
                        }"
                        row3.addView(tvDynamicProjectNo)

                        tableLayout.addView(row3)

                        //forth Row
                        val row4 = TableRow(context)

                        val tvStaticMaintenanceTransmittalDocument = TextView(context)
                        tvStaticMaintenanceTransmittalDocument.typeface =
                            resources.getFont(R.font.helvetica)
                        tvStaticMaintenanceTransmittalDocument.setTextColor(
                            resources.getColor(
                                taimoor.sultani.sweetalert2.R.color.text_color
                            )
                        )
                        tvStaticMaintenanceTransmittalDocument.textSize = 14F

                        tvStaticMaintenanceTransmittalDocument.text =
                            "MaintenanceTransmittalDocument:  "
                        row4.addView(tvStaticMaintenanceTransmittalDocument)

                        val tvDynamicMaintenanceTransmittalDocument = TextView(context)
                        tvDynamicMaintenanceTransmittalDocument.text = "${
                            modelObject.MaintenanceTransmittalDocument
                        }"
                        row4.addView(tvDynamicMaintenanceTransmittalDocument)

                        tableLayout.addView(row4)

                        //fifth Row
                        val row5 = TableRow(context)

                        val tvStaticPPSchedulePreparation = TextView(context)
                        tvStaticPPSchedulePreparation.typeface = resources.getFont(R.font.helvetica)
                        tvStaticPPSchedulePreparation.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticPPSchedulePreparation.textSize = 14F

                        tvStaticPPSchedulePreparation.text = "PPSchedulePreparation:  "
                        row5.addView(tvStaticPPSchedulePreparation)

                        val tvDynamicPPSchedulePreparation = TextView(context)
                        tvDynamicPPSchedulePreparation.text = "${
                            modelObject.PPSchedulePreparation
                        }"
                        row5.addView(tvDynamicPPSchedulePreparation)

                        tableLayout.addView(row5)


                        //sixth Row
                        val row6 = TableRow(context)

                        val tvStaticRemark = TextView(context)
                        tvStaticRemark.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRemark.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRemark.textSize = 14F

                        tvStaticRemark.text = "Remark:  "
                        row6.addView(tvStaticRemark)

                        val tvDynamicRemark = TextView(context)
                        tvDynamicRemark.text = "${
                            modelObject.Remarks
                        }"
                        row6.addView(tvDynamicRemark)

                        tableLayout.addView(row6)


                    }
                    Global.OTHER -> {
                        val modelObject = convertToModelObjectOther(json)
                        Log.e(
                            TAG,
                            "convertToModelObjectOther: ${modelObject.toString()}"
                        )


                        // First Row
                        val row1 = TableRow(context)

                        val tvStatusStatic = TextView(context)
                        tvStatusStatic.typeface = resources.getFont(R.font.helvetica)
                        tvStatusStatic.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStatusStatic.textSize = 14F

                        tvStatusStatic.text = "Status:  "
                        row1.addView(tvStatusStatic)

                        val tvStatusDynamic = TextView(context)
                        tvStatusDynamic.text = "${
                            modelObject.Status
                        }"
                        row1.addView(tvStatusDynamic)

                        tableLayout.addView(row1)


// Second Row
                        val row2 = TableRow(context)

                        val tvStaticCorrectIssueType = TextView(context)
                        tvStaticCorrectIssueType.text = "CorrectIssueType:  "
                        tvStaticCorrectIssueType.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCorrectIssueType.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCorrectIssueType.textSize = 14F
                        row2.addView(tvStaticCorrectIssueType)

                        val tvDynamicCorrectIssueType = TextView(context)
                        tvDynamicCorrectIssueType.text = "${modelObject.CorrectIssueType}"
                        row2.addView(tvDynamicCorrectIssueType)

                        tableLayout.addView(row2)


// Third Row
                        val row3 = TableRow(context)

                        val tvStaticCorrectiveActions = TextView(context)
                        tvStaticCorrectiveActions.typeface = resources.getFont(R.font.helvetica)
                        tvStaticCorrectiveActions.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticCorrectiveActions.textSize = 14F
                        tvStaticCorrectiveActions.text = "CorrectiveActions :  "
                        row3.addView(tvStaticCorrectiveActions)

                        val tvDynamicCorrectiveActions = TextView(context)
                        tvDynamicCorrectiveActions.text = "${modelObject.CorrectiveActions}"

                        row3.addView(tvDynamicCorrectiveActions)

                        tableLayout.addView(row3)

                        val row4 = TableRow(context)
                        val tvStaticScheduledVisitDate = TextView(context)
                        tvStaticScheduledVisitDate.typeface = resources.getFont(R.font.helvetica)
                        tvStaticScheduledVisitDate.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticScheduledVisitDate.textSize = 14F

                        tvStaticScheduledVisitDate.text = "ScheduledVisitDate :  "
                        row4.addView(tvStaticScheduledVisitDate)

                        val tvDynamicScheduledVisitDate = TextView(context)
                        tvDynamicScheduledVisitDate.setText("${modelObject.ScheduledVisitDate}")
                        row4.addView(tvDynamicScheduledVisitDate)

                        tableLayout.addView(row4)


                        val row5 = TableRow(context)
                        val tvStaticMaterialUsed = TextView(context)
                        tvStaticMaterialUsed.typeface = resources.getFont(R.font.helvetica)
                        tvStaticMaterialUsed.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticMaterialUsed.textSize = 14F

                        tvStaticMaterialUsed.text = "MaterialUsed :  "
                        row5.addView(tvStaticMaterialUsed)

                        val tvDynamicMaterialUsed = TextView(context)
                        tvDynamicMaterialUsed.setText("${modelObject.MaterialUsed}")
                        row5.addView(tvDynamicMaterialUsed)

                        tableLayout.addView(row5)


                        val row6 = TableRow(context)
                        val tvStaticRepairRequestNeeded = TextView(context)
                        tvStaticRepairRequestNeeded.typeface = resources.getFont(R.font.helvetica)
                        tvStaticRepairRequestNeeded.setTextColor(resources.getColor(taimoor.sultani.sweetalert2.R.color.text_color))
                        tvStaticRepairRequestNeeded.textSize = 14F

                        tvStaticRepairRequestNeeded.text = "RepairRequestNeeded :  "
                        row6.addView(tvStaticRepairRequestNeeded)

                        val tvDynamicRepairRequestNeeded = TextView(context)
                        tvDynamicRepairRequestNeeded.setText("${modelObject.RepairRequestNeeded}")
                        row6.addView(tvDynamicRepairRequestNeeded)

                        tableLayout.addView(row6)
                    }

                    else -> {
                        Log.e(TAG, "parseJson:NOTHINGMATCHED ")
                    }


                }


//                ticketbiding.tvTicketDetails.text="CorrectIssueType: ${modelObject.CorrectIssueType}\n\nScheduledVisitDate: ${modelObject.ScheduledVisitDate}\n\n" +
//                        "CorrectiveActions: ${modelObject.CorrectiveActions}\n\nRepairRequestNeeded: ${modelObject.RepairRequestNeeded}\n" +
//                        "\nMaterialUsed: ${modelObject.MaterialUsed}\n" +
//                        "\n"


                // Use the model object as needed
            } else if (json is JSONArray) {
                // JSON is an array
                val modelList = convertToModelList(json)
                Log.e(TAG, "parseJson: ${modelList.toString()}")
                // Use the model list as needed
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Convert JSON object to your Kotlin model class
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


    fun convertToModelObjectFinalSiteSurvey(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val DesignEngineerName = jsonObject.getString("DesignEngineerName")
        val FinalSurveyReportattached = jsonObject.getString("FinalSurveyReportattached")
        val Remark = jsonObject.getString("Remark")
        val status = jsonObject.getString("status")
        val shaft = jsonObject.getString("shaft")
        val From = jsonObject.getString("From")
        val TypeofElevator = jsonObject.getString("TypeofElevator")
        val Other = jsonObject.getString("Other")
        val ShaftAvailability = jsonObject.getString("ShaftAvailability")
        val ShaftWidth = jsonObject.getString("ShaftWidth")
        val ShaftDepth = jsonObject.getString("ShaftDepth")
        val Travel = jsonObject.getString("Travel")
        val OverHead = jsonObject.getString("OverHead")
        val Pit = jsonObject.getString("Pit")
        val WidthA = jsonObject.getString("WidthA")
        val WidthC = jsonObject.getString("WidthC")
        val StructureOpening = jsonObject.getString("StructureOpening")
        // ...

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            DesignEngineerName = DesignEngineerName,
            FinalSurveyReportattached = FinalSurveyReportattached,
            Remarks = Remark,
            status = status,
            shaft = shaft,
            From = From,
            TypeofElevator = TypeofElevator,
            Other = Other,
            ShaftAvailability = ShaftAvailability,
            ShaftWidth = ShaftWidth,
            ShaftDepth = ShaftDepth,
            Travel = Travel,
            OverHead = OverHead,
            Pit = Pit,
            WidthA = WidthA,
            WidthC = WidthC,
            StructureOpening = StructureOpening
        )
    }

    fun convertToModelObjectDrawingApproval(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val DesignEngineerName = jsonObject.getString("DesignEngineerName")
        val FinalDrawingAttached = jsonObject.getString("FinalDrawingAttached")
        val DeviationsObserved = jsonObject.getString("DeviationsObserved")
        val DeviationsDetails = jsonObject.getString("DeviationsDetails")
        val FinalDrawingApproval = jsonObject.getString("FinalDrawingApproval")
//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            DesignEngineerName = DesignEngineerName,
            FinalDrawingAttached = FinalDrawingAttached,
            DeviationsObserved = DeviationsObserved,
            DeviationsDetails = DeviationsDetails,
            FinalDrawingApproval = FinalDrawingApproval
        )
    }

    fun convertToModelObjectFinalOrderSpecification(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val ClientApprovalFOS = jsonObject.getString("ClientApprovalFOS")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            ClientApprovalFOS = ClientApprovalFOS,

            )
    }


    fun convertToModelObjectPurchaseRequest(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val SalespersonName = jsonObject.getString("SalespersonName")
        val PurchaeRequestNo = jsonObject.getString("PurchaeRequestNo")
        val PurchaeRequestDate = jsonObject.getString("PurchaeRequestDate")
        val PurchaeRequestTo = jsonObject.getString("PurchaeRequestTo")
        val PurchaeRequestType = jsonObject.getString("PurchaeRequestType")
        val DocumentDate = jsonObject.getString("DocumentDate")
        val ClientApprovalFOS = jsonObject.getString("ClientApprovalFOS")
        val FinalApprovedDrawingAttached = jsonObject.getString("FinalApprovedDrawingAttached")
        val FinalSupplierofferAttached = jsonObject.getString("FinalSupplierofferAttached")
        val Remark = jsonObject.getString("Remark")
        val ProjectNumber = jsonObject.getString("ProjectNumber")
        val RequestedName = jsonObject.getString("RequestedName")
        val BranchName = jsonObject.getString("BranchName")
//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            SalespersonName = SalespersonName,
            PurchaeRequestNo = PurchaeRequestNo,
            PurchaeRequestDate = PurchaeRequestDate,
            PurchaeRequestTo = PurchaeRequestTo,
            PurchaeRequestType = PurchaeRequestType,
            DocumentDate = DocumentDate,
            ClientApprovalFOS = ClientApprovalFOS,
            FinalApprovedDrawingAttached = FinalApprovedDrawingAttached,
            FinalSupplierofferAttached = FinalSupplierofferAttached,
            Remarks = Remark,
            ProjectNumber = ProjectNumber,
            RequestedName = RequestedName,
            BranchName = BranchName
        )
    }


    fun convertToModelObjectMaterialDispatch(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val LogisticPerson = jsonObject.getString("LogisticPerson")
        val ShipmentConsignmentNo = jsonObject.getString("ShipmentConsignmentNo")
        val ContactNo = jsonObject.getInt("ContactNo")
        val ShippedDate = jsonObject.getString("ShippedDate")
        val ExpectedDateofDelivery = jsonObject.getString("ExpectedDateofDelivery")
        val ConsignmentDocumentsAttached = jsonObject.getString("ConsignmentDocumentsAttached")
        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            LogisticPerson = LogisticPerson,
            ShipmentCsgNo = ShipmentConsignmentNo,
            ContactNo = ContactNo,
            ShippedDate = ShippedDate,
            ExpDeliveryDate = ExpectedDateofDelivery,
            ConsignmentDocumentsAttached = ConsignmentDocumentsAttached,
            Remarks = Remark
        )
    }


    fun convertToModelObjectSiteReadinessEvaluation(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val SiteEngineerName = jsonObject.getString("SiteEngineerName")
        val CivilWorkStatus = jsonObject.getString("CivilWorkStatus")
        val CivilWorkDetails = jsonObject.getString("CivilWorkDetails")
        val ScaffoldingRequired = jsonObject.getString("ScaffoldingRequired")
        val ScaffoldingBy = jsonObject.getString("ScaffoldingBy")
        val ScaffoldingType = jsonObject.getString("ScaffoldingType")
        val ScaffoldingInspectionReportAttached =
            jsonObject.getString("ScaffoldingInspectionReportAttached")
        val ScaffoldingStatus = jsonObject.getString("ScaffoldingStatus")
        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            SiteEngineerName = SiteEngineerName,
            CivilWorkStatus = CivilWorkStatus,
            CivilWorkDetails = CivilWorkDetails,
            ScaffoldingRequired = ScaffoldingRequired,
            ScaffoldingBy = ScaffoldingBy,
            ScaffoldingType = ScaffoldingType,
            Remarks = Remark,
            ScaffoldingInspectionReportAttached = ScaffoldingInspectionReportAttached,
            ScaffoldingStatus = ScaffoldingStatus
        )
    }


    fun convertToModelObjectSiteMaterialDelivered(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val LogisticPersonName = jsonObject.getString("LogisticPersonName")
        val ClientName = jsonObject.getString("ClientName")
        val RecepientName = jsonObject.getString("RecepientName")
        val RecepientPhoneNo = jsonObject.getString("RecepientPhoneNo")
        val RecepientEmirateID = jsonObject.getString("RecepientEmirateID")
        val DeliveryStatus = jsonObject.getString("DeliveryStatus")
        val ProofofDeliveryAttached = jsonObject.getString("ProofofDeliveryAttached")

        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            LogisticPersonName = LogisticPersonName,
            ClientName = ClientName,
            RecepientName = RecepientName,
            RecepientPhoneNo = RecepientPhoneNo,
            RecepientEmirateID = RecepientEmirateID,
            DeliveryStatus = DeliveryStatus,
            Remarks = Remark,
            ProofofDeliveryAttached = ProofofDeliveryAttached
        )
    }


    fun convertToModelObjectInstallInitiation(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val PaymentStatus = jsonObject.getString("PaymentStatus")
        val PendingAmount = jsonObject.getInt("PendingAmount")
        val InvoiceID = jsonObject.getString("InvoiceID")
        val InvoiceDate = jsonObject.getString("InvoiceDate")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            PaymentStatus = PaymentStatus,
            PendingAmount = PendingAmount,
            InvoiceID = InvoiceID,
            InvoiceDate = InvoiceDate
        )
    }


    fun convertToModelObjectQualityInspection1(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val QualityPersonName = jsonObject.getString("QualityPersonName")
        val DiscrepenciesFound = jsonObject.getString("DiscrepenciesFound")
        val CorrectiveActionRequired = jsonObject.getString("CorrectiveActionRequired")
        val QualityCheckReportAttached = jsonObject.getString("QualityCheckReportAttached")
        val QualityRating = jsonObject.getString("QualityRating")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            QualityPersonName = QualityPersonName,
            DiscrepenciesFound = DiscrepenciesFound,
            CorrectiveActionRequired = CorrectiveActionRequired,
            QualityCheckReportAttached = QualityCheckReportAttached,
            QualityRating = QualityRating,
            Remarks = Remark
        )
    }

    fun convertToModelObjectTestingAndComissioning(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val CompletionReport = jsonObject.getString("CompletionReport")
        val PowerSupplyAvailability = jsonObject.getString("PowerSupplyAvailability")
        val PaymentConfirmation = jsonObject.getString("PaymentConfirmation")
        val Invoice = jsonObject.getString("Invoice")
        val SupplyAvailabilityDate = jsonObject.getString("SupplyAvailabilityDate")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            CompletionReport = CompletionReport,
            PowerSupplyAvailability = PowerSupplyAvailability,
            PaymentConfirmation = PaymentConfirmation,
            Invoice = Invoice,
            SupplyAvailabilityDate = SupplyAvailabilityDate,
            Remarks = Remark
        )
    }


    fun convertToModelObjectQualityInspectionTwo(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val QualityPersonName = jsonObject.getString("QualityPersonName")
        val DiscrepenciesFound = jsonObject.getString("DiscrepenciesFound")
        val CorrectiveActionRequired = jsonObject.getString("CorrectiveActionRequired")
        val QualityCheckReportAttached = jsonObject.getString("QualityCheckReportAttached")
        val CreatedRequest = jsonObject.getString("CreatedRequest")
        val QualityRating = jsonObject.getString("QualityRating")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            QualityPersonName = QualityPersonName,
            DiscrepenciesFound = DiscrepenciesFound,
            CorrectiveActionRequired = CorrectiveActionRequired,
            QualityCheckReportAttached = QualityCheckReportAttached,
            QualityRating = QualityRating,
            CreatedRequest = CreatedRequest,
            Remarks = Remark
        )
    }


    fun convertToModelObjectThirdPartyInspection(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val SupervisorName = jsonObject.getString("SupervisorName")
        val thirdrdPartyOrgName = jsonObject.getString("thirdrdPartyOrgName")
        val CertificationDate = jsonObject.getString("CertificationDate")
        val ValidTill = jsonObject.getString("ValidTill")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            SupervisorName = SupervisorName,
            thirdrdPartyOrgName = thirdrdPartyOrgName,
            CertificationDate = CertificationDate,
            ValidTill = ValidTill,
            Remarks = Remark
        )
    }


    fun convertToModelObjectMaintenanceInspection(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val LineManagerName = jsonObject.getString("LineManagerName")
        val ThirdPartyInspectionReportAttached =
            jsonObject.getString("ThirdPartyInspectionReportAttached")
        val MNo = jsonObject.getString("MNo")
        val ApprovedQualityInspectiontwoAttached =
            jsonObject.getString("ApprovedQualityInspectiontwoAttached")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            LineManagerName = LineManagerName,
            ThirdPartyInspectionReportAttached = ThirdPartyInspectionReportAttached,
            MNo = MNo,
            ApprovedQualityInspectiontwoAttached = ApprovedQualityInspectiontwoAttached,
            Remarks = Remark
        )
    }


    fun convertToModelObjectHandOverToClient(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val SupervisorName = jsonObject.getString("SupervisorName")
        val PaymentStatus = jsonObject.getString("PaymentStatus")
        val TrainingStatus = jsonObject.getString("TrainingStatus")
        val MaintenanceInspectionReportAttached =
            jsonObject.getString("MaintenanceInspectionReportAttached")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            SupervisorName = SupervisorName,
            PaymentStatus = PaymentStatus,
            TrainingStatus = TrainingStatus,
            MaintenanceInspectionReportAttached = MaintenanceInspectionReportAttached,
            Remarks = Remark
        )
    }

    fun convertToModelObjectTransferToMaintenance(jsonObject: JSONObject): ModelObjectForTicketDetails {
        // Parse the JSON object and extract values to populate your model class
        val LineManagerName = jsonObject.getString("LineManagerName")
        val Documentsattached = jsonObject.getString("Documentsattached")
        val ProjectNo = jsonObject.getString("ProjectNo")
        val MaintenanceTransmittalDocument = jsonObject.getString("MaintenanceTransmittalDocument")
        val PPSchedulePreparation = jsonObject.getString("PPSchedulePreparation")


        val Remark = jsonObject.getString("Remark")

//

        // Create and return an instance of your model class
        return ModelObjectForTicketDetails(
            LineManagerName = LineManagerName,
            Documentsattached = Documentsattached,
            ProjectNo = ProjectNo,
            MaintenanceTransmittalDocument = MaintenanceTransmittalDocument,
            PPSchedulePreparation = PPSchedulePreparation,
            Remarks = Remark
        )
    }


    // Convert JSON array to a list of your Kotlin model class
    fun convertToModelList(jsonArray: JSONArray): List<JsonModelForTicketDetails> {
        val modelList = mutableListOf<JsonModelForTicketDetails>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val modelObject = convertToModelObjectOther(jsonObject)
            modelList.add(modelObject)
        }

        return modelList
    }


}
