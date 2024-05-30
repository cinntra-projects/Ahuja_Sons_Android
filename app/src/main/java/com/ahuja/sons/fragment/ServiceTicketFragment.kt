package com.ahuja.sons.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.loadingview.LoadingView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.ahuja.sons.R
import com.ahuja.sons.`interface`.DataBaseClick
import com.ahuja.sons.`interface`.RecallApi
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.*
import com.ahuja.sons.viewmodel.MainViewModel
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.activity.*
import com.ahuja.sons.adapter.*
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.DialogCheckListStatusChangerBinding
import com.ahuja.sons.databinding.ServiceTicketBinding
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList


class ServiceTicketFragment(val ticketID: TicketData) : Fragment(), View.OnClickListener, DataBaseClick, RecallApi {

    private lateinit var ticketbiding: ServiceTicketBinding

    //var checkBinding: DialogCheckListStatusChangerBinding
    lateinit var adapter: ServiceTicketAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel: MainViewModel
    private var seconds: Long = 0

    // Is the stopwatch running?
    private var running = false
    private lateinit var pdfUri: Uri
    private lateinit var openpdffrom: String
    private lateinit var openpdfpath: String
    private lateinit var ticketdata: TicketData

    val tickethistorydata = ArrayList<TicketChecklistData>()
    val checkList = ArrayList<DataCheckList>()
    lateinit var adapterCheckList: CheckListTicketAdapter
    var pageNo = 1
    var maxItem = 10

    val issueList_gl = ArrayList<IssueListResponseModel.DataXXX>()
    var issueListAdapter : IssueListAdapter? = null

    var solutionDataList_gl: ArrayList<SolutionListResponseModel.DataXXX> = ArrayList()
    var selectedDataList = ArrayList<SolutionListResponseModel.DataXXX>()
    var issueCategoryList_gl: ArrayList<IssueCategoryListResponseModel.DataXXX> = ArrayList()
    var itemAllList_gl: ArrayList<ItemAllListResponseModel.DataXXX> = ArrayList()

    companion object {
        private const val TAG = "HistoryTicketFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = ServiceTicketBinding.inflate(layoutInflater)
        viewModel = (activity as TicketDetailsActivity).viewModel
        ticketdata = ticketID

        ticketbiding.attachview.isVisible = ticketdata.Type == "Installation"

        //  Toast.makeText(requireContext(), "${Global.TicketAuthentication}", Toast.LENGTH_SHORT).show()


        if (ticketdata.TicketEndDate.toString().isBlank() && ticketdata.TicketStartDate.toString().isNotBlank()) {
            if (ticketdata.TicketEndDate.toString() != "foo" && ticketdata.TicketStartDate.toString() != "foo") {
                running = true
                /* seconds = (Global.findDifference(
                     Global.formatserverDateFromDateStringtimer(ticketdata.TicketStartDate),
                     Global.getTodayDate() + " " + Global.getfullformatCurrentTime()
                 ))*/
                seconds = ticketdata.DurationOfService.toDouble().toLong()
            }
            Log.e("sec", seconds.toString())
        } else if (ticketdata.TicketEndDate.toString().isNotBlank() && ticketdata.TicketStartDate.toString().isNotBlank()) {
            if (ticketdata.TicketEndDate.toString() != "foo" && ticketdata.TicketStartDate.toString() != "foo") {
                /*seconds = (Global.findDifference(
                    Global.formatserverDateFromDateStringtimer(ticketdata.TicketStartDate),
                    Global.formatserverDateFromDateStringtimer(ticketdata.TicketEndDate)
                ))*/
                seconds = ticketdata.DurationOfService.toDouble().toLong()
                Log.e("sec", seconds.toString())
            }
        }

        runTimer()
        if (Global.checkForInternet(requireContext())) {
            //todo checklist api here---
            /*var hashMap = HashMap<String, String>()
            hashMap["TicketId"] = ticketdata.id.toString()
            viewModel.getCheckListOfTicket(hashMap)
            subscribeToObserver()*/

            callCheckListApi()

            callIssueListApi()


        }


        eventmanager()


        ticketbiding.uploadDoc.setOnClickListener {
            if (Global.TicketAuthentication || Prefs.getString(Global.Employee_role, "").equals(Global.ADMIN_STRING, ignoreCase = true)) {
                when (ticketdata.TicketStatus) {
                    "Accepted" -> {
                        if (ticketdata.Status != "Resolved") {
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
            } else {
                Global.warningdialogbox(
                    requireContext(),
                    "You have not authorization to work on ticket"
                )
            }

        }

        ticketbiding.docView.setOnClickListener {

            if (openpdffrom == "URI") {

                val pdfuristring = FileUtil.getPath(requireContext(), pdfUri)
                val file =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/" + pdfuristring)
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
        return ticketbiding.root
    }


    private fun eventmanager() {
        ticketbiding.showallrequest.setOnClickListener(this)
        ticketbiding.createRequest.setOnClickListener(this)
        ticketbiding.stop.setOnClickListener(this)
        ticketbiding.play.setOnClickListener(this)
        ticketbiding.reset.setOnClickListener(this)
//        ticketbiding.reset.isEnabled = ticketdata.Status!="Resolved"
        if (ticketdata.Status == "Closed" )
            ticketbiding.reset.isEnabled = false

        else if (ticketdata.Status != "Resolved")
            ticketbiding.reset.isEnabled = ticketdata.TicketStartDate!!.isNotEmpty()
        else
            ticketbiding.reset.isEnabled = false


        ticketbiding.play.isEnabled = ticketdata.TicketStartDate.toString().isEmpty()

        ticketbiding.stop.isEnabled = ticketdata.TicketEndDate.toString().isEmpty()

        if (!ticketbiding.play.isEnabled) {
            ticketbiding.play.background = ContextCompat.getDrawable(requireContext(), R.drawable.play_off)
        } else {
            ticketbiding.play.background = ContextCompat.getDrawable(requireContext(), R.drawable.play_238046)
        }
        if (!ticketbiding.stop.isEnabled) {
            ticketbiding.stop.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stopoff)
        } else {
            ticketbiding.stop.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop)
        }
        if (!ticketbiding.reset.isEnabled) {
            ticketbiding.reset.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_refreshoff)
        } else {
            ticketbiding.reset.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_refresh)
        }



        ticketbiding.addNewIssue.setOnClickListener {
            when (ticketdata.TicketStatus) {
                "Accepted" -> {
                    if (ticketdata.Status == "Closed" ){
                        Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                    }
                    else if (ticketdata.Status != "Resolved" ) {
                        if (ticketdata.TicketStartDate.isNotEmpty()) {
                            IssueAddPopupDialog("AddIssueItem", 0)
                        } else {
                            Global.warningmessagetoast(requireContext(), resources.getString(R.string.yet_to_start))
                        }
                    }
                    else {
                        Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")
                    }
                }
                "Pending" -> {
                    Global.warningdialogbox(requireContext(), "Your ticket is in pending state,Kindly accept it")
                }

                "Rejected" -> {
                    Global.warningdialogbox(requireContext(), "Your ticket will be rejected")
                }
            }

        }


    }



    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: ")
        if (Global.checkForInternet(requireContext())) {
            //  checkList.clear()
            var hashMap = HashMap<String, String>()
            hashMap["TicketId"] = ticketdata.id.toString()

            val tickethistory = HashMap<String, Int>()
            tickethistory["id"] = ticketdata.id

            viewModel.particularTicketDetails(tickethistory)

            bindTicketDetailObserver()

            CoroutineScope(Dispatchers.IO).launch {
                delay(2000)
                callCheckListApi()
            }
        }

    }

    var AllTicketOneData : ArrayList<ItemAllListResponseModel.DataXXX> = ArrayList()

    //todo bind observer for ticket details..
    private fun bindTicketDetailObserver() {
        viewModel.allItemWiseTicket.observe(
            this, Event.EventObserver(
                onError = {
                    ticketbiding.progressbar.stop()
                    Log.e("error===>", it)
                    Global.warningmessagetoast(requireContext(), it)
                },
                onLoading = {
                    ticketbiding.progressbar.start()
                },
                onSuccess = { response ->
                    if (response.status == 200) {
                        ticketbiding.progressbar.stop()
                        setdata(response.data[0])
                    } else {
                        ticketbiding.progressbar.stop()
                        Global.warningmessagetoast(requireContext(), response.message)
                    }

                })
        )
    }


    //todo calling checklist api here--
    private fun callCheckListApi(){
        var hashMap = HashMap<String, String>()
        hashMap["TicketId"] = ticketdata.id.toString()

        val call: Call<ResponseCheckListTicket> = ApiClient().service.getAllCheckList(hashMap)
        call.enqueue(object : Callback<ResponseCheckListTicket?> {
            override fun onResponse(call: Call<ResponseCheckListTicket?>, response: Response<ResponseCheckListTicket?>) {
                try {
                    if (response.isSuccessful){

                        if (response.body()!!.status == 200) {

                            Log.e(TAG, "subscribeToObserver: ")
                            checkList.clear()
                            checkList.addAll(response.body()!!.data)

                            if (checkList.size > 0) {
                                ticketbiding.headCheckList.visibility = View.VISIBLE
                            } else {
                                ticketbiding.headCheckList.visibility = View.GONE
                            }

                            try {
                                val layoutManager = LinearLayoutManager(requireContext())
                                adapterCheckList = CheckListTicketAdapter(checkList)
                                ticketbiding.rvCheeckList.layoutManager = layoutManager
                                ticketbiding.rvCheeckList.adapter = adapterCheckList
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e(TAG, "onResponse: "+e.printStackTrace() )
                            }


                            adapterCheckList.setOnItemClickListener { data ->
                                when (ticketdata.TicketStatus) {
                                    "Accepted" -> {
                                        if (ticketdata.Status == "Closed") {
                                            Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                                        }
                                        else if (ticketdata.Status != "Resolved" ) {
                                            if (ticketdata.TicketStartDate.isNotEmpty()) {
                                                openCHeklistDialog(requireContext(), data)
                                            } else {
                                                Global.warningmessagetoast(requireContext(), resources.getString(R.string.yet_to_start))
                                            }
                                        }

                                        else {
                                            Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")
                                        }
                                    }
                                    "Pending" -> {
                                        Global.warningdialogbox(requireContext(), "Your ticket is in pending state,Kindly accept it")
                                    }

                                    "Rejected" -> {
                                        Global.warningdialogbox(requireContext(), "Your ticket will be rejected")
                                    }
                                }

                            }


                        }else{
                            Log.e(TAG, "subscribeToObserverApiError: ${response.message()}")
                            Global.warningmessagetoast(requireContext(), response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseCheckListTicket?>, t: Throwable) {
                Global.errormessagetoast(requireContext(), t.message.toString())
            }
        })
    }


    //todo calling issue list api here---
    private fun callIssueListApi(){
        var hashMap = HashMap<String, String>()
        hashMap["TicketId"] = ticketdata.id.toString()

        var JsonObject : JsonObject = JsonObject()
        JsonObject.addProperty("TicketId", ticketdata.id.toString())
        JsonObject.addProperty("PageNo", pageNo)
        JsonObject.addProperty("maxItem", maxItem)

        val call: Call<IssueListResponseModel> = ApiClient().service.getAllIssueList(JsonObject)
        call.enqueue(object : Callback<IssueListResponseModel?> {
            override fun onResponse(call: Call<IssueListResponseModel?>, response: Response<IssueListResponseModel?>) {
                try {
                    if (response.isSuccessful){

                        if (response.body()!!.status == 200) {

                            Log.e(TAG, "subscribeToObserver: ")
                            issueList_gl.clear()
                            issueList_gl.addAll(response.body()!!.data)

                            if (issueList_gl.size > 0) {
                                ticketbiding.rvIssueList.visibility = View.VISIBLE
                            } else {
                                ticketbiding.rvIssueList.visibility = View.GONE
                            }

                            val layoutManager = LinearLayoutManager(requireContext())
                            issueListAdapter = IssueListAdapter(issueList_gl)
                            ticketbiding.rvIssueList.layoutManager = layoutManager
                            ticketbiding.rvIssueList.adapter = issueListAdapter


                            issueListAdapter!!.setOnIssueItemClickListener { data , id->
                                when (ticketdata.TicketStatus) {
                                    "Accepted" -> {
                                        if (ticketdata.Status == "Closed") {
                                            Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                                        }
                                        else if (ticketdata.Status != "Resolved") {
                                            if (ticketdata.TicketStartDate.isNotEmpty()) {
                                                IssueAddPopupDialog("ViewIssue", id)
                                            } else {
                                                Global.warningmessagetoast(requireContext(), resources.getString(R.string.yet_to_start))
                                            }
                                        }
                                        else {
                                            Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")
                                        }
                                    }
                                    "Pending" -> {
                                        Global.warningdialogbox(requireContext(), "Your ticket is in pending state,Kindly accept it")
                                    }

                                    "Rejected" -> {
                                        Global.warningdialogbox(requireContext(), "Your ticket will be rejected")
                                    }
                                }

                            }


                        }else{
                            Log.e(TAG, "subscribeToObserverApiError: ${response.message()}")
                            Global.warningmessagetoast(requireContext(), response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<IssueListResponseModel?>, t: Throwable) {
                Global.errormessagetoast(requireContext(), t.message.toString())
            }
        })
    }


    //todo issue add dialog--
    private fun IssueAddPopupDialog(flag: String, position: Int) {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_issue_item_dialog)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER

        val try_again: Button = dialog.findViewById(R.id.try_again)
        val done: Button = dialog.findViewById(R.id.done)
        val edttext: EditText = dialog.findViewById(R.id.edttext)
        val acItem: AutoCompleteTextView = dialog.findViewById(R.id.acItem)
        val acIssueItem: AutoCompleteTextView = dialog.findViewById(R.id.acIssueItem)
        val acSolutionItems: AutoCompleteTextView = dialog.findViewById(R.id.acSolutionItems)
        val rvSolutionItems: RecyclerView = dialog.findViewById(R.id.rvSolutionItems)
        val btnLayout: LinearLayout = dialog.findViewById(R.id.btnLayout)
        val headerCrossDialog: TextView = dialog.findViewById(R.id.headerCrossDialog)

        solutionDataList_gl.clear()

        if (flag == "AddIssueItem"){
            btnLayout.visibility = View.VISIBLE
            edttext.isEnabled = true
            edttext.isClickable = true
//            edttext.isFocusableInTouchMode = true
            edttext.isFocusable = true

            acItem.isEnabled = true
            acItem.isClickable = true
//            acItem.isFocusableInTouchMode = true
            acItem.isFocusable = true

            acIssueItem.isEnabled = true
            acIssueItem.isClickable = true
//            acIssueItem.isFocusableInTouchMode = true
            acIssueItem.isFocusable = true

            acSolutionItems.isEnabled = true
            acSolutionItems.isClickable = true
//            acSolutionItems.isFocusableInTouchMode = true
            acSolutionItems.isFocusable = true

            rvSolutionItems.isEnabled = true
            rvSolutionItems.isClickable = true
//            rvSolutionItems.isFocusableInTouchMode = true
            rvSolutionItems.isFocusable = true
        }
        else if (flag == "ViewIssue"){
            btnLayout.visibility = View.GONE
            edttext.isEnabled = false
            edttext.isClickable = false
            edttext.isFocusableInTouchMode = false
            edttext.isFocusable = false

            acItem.isEnabled = false
            acItem.isClickable = false
            acItem.isFocusableInTouchMode = false
            acItem.isFocusable = false

            acIssueItem.isEnabled = false
            acIssueItem.isClickable = false
            acIssueItem.isFocusableInTouchMode = false
            acIssueItem.isFocusable = false

            acSolutionItems.isEnabled = false
            acSolutionItems.isClickable = false
            acSolutionItems.isFocusableInTouchMode = false
            acSolutionItems.isFocusable = false

            rvSolutionItems.isEnabled = false
            rvSolutionItems.isClickable = false
            rvSolutionItems.isFocusableInTouchMode = false
            rvSolutionItems.isFocusable = false

            edttext.setText(issueList_gl[position].Description.toString())
            acItem.setText(issueList_gl[position].SerialNo)
            acIssueItem.setText(issueList_gl[position].IssueType)

            val dataList = ArrayList(issueList_gl[position].Solution.split(", "))
            var temList = ArrayList<String>()
            temList.clear()

            for (item in dataList) {
                temList.add(item)
            }


            if (temList != null && temList.size > 0) {
                rvSolutionItems.visibility = View.VISIBLE
            } else {
                rvSolutionItems.visibility = View.GONE
            }
            Log.e("selected", "onItemClick: " + temList.size)
            val gridLayoutManager = GridLayoutManager(requireActivity(), 2)
            val adapterEmp = StringSolutionAdapter(requireActivity(), temList)
            rvSolutionItems.layoutManager = gridLayoutManager
            rvSolutionItems.adapter = adapterEmp
            adapterEmp.notifyDataSetChanged()
        }

        var SerialNo = ""
        var IssueValue = ""

        //todo calling ticket by item list--
        var jsonObject = JsonObject()
        jsonObject.addProperty("TicketId", ticketdata.id)
        viewModel.getItemsByTicket(jsonObject)
        bindItemSelectObserver(acItem)


        //todo calling issue category list--
        viewModel.getIssueCategoryList()
        bindIssueCateogryObserver(acIssueItem)



        //todo mode communication item selected
        acItem.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (itemAllList_gl.isNotEmpty()) {
                    SerialNo = itemAllList_gl[position].SerialNo
                    acItem.setText(itemAllList_gl[position].ItemName)

                } else {
                    SerialNo = ""
                    acItem.setText("")
                }
            }

        }


        //todo on issue item click listener---
        acIssueItem.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (issueCategoryList_gl.isNotEmpty()) {
                    IssueValue = issueCategoryList_gl[position].Title
                    acIssueItem.setText(issueCategoryList_gl[position].Title)
                    selectedDataList.clear()

                    //todo calling solution payload category list--
                    var filedList = ArrayList<String>()
                    filedList.add("id")
                    filedList.add("Title")
                    filedList.add("IssueCategory")
                    filedList.add("CreatedBy")
                    filedList.add("CreatedDate")
                    filedList.add("CreatedTime")

                    var filter = SolutionRequestModel.FilterX(issueCategoryList_gl[position].id)
                    var data = SolutionRequestModel(filedList,filter)

                    viewModel.getSolutionList(data)
                    bindSolutionListObserver(acSolutionItems, rvSolutionItems)


                } else {
                    IssueValue = ""
                    acItem.setText("")
                }
            }
        }


        try_again.setOnClickListener {
            dialog.dismiss()

        }

        headerCrossDialog.setOnClickListener {
            dialog.dismiss()

        }

        done.setOnClickListener {
            if (edttext.text.toString().isNotEmpty()) {
                if (Global.checkForInternet(requireContext())) {

                    var tempList = ArrayList<String>()
                    for (item in selectedDataList) {
                        tempList.add(item.Title)
                    }
                    val separatedSolution = tempList.joinToString(",")

                    var jsonObject = JsonObject()
                    jsonObject.addProperty("id", "")
                    jsonObject.addProperty("TicketId", ticketdata.id)
                    jsonObject.addProperty("IssueType", IssueValue)
                    jsonObject.addProperty("SerialNo", SerialNo)
                    jsonObject.addProperty("CreatedBy", Prefs.getString(Global.Employee_Code, ""))
                    jsonObject.addProperty("Description", edttext.text.toString())
                    jsonObject.addProperty("Solution", separatedSolution )

                    viewModel.createIssue(jsonObject)
                    bindCreateIssueObserver(dialog)

                }
            }
        }


        dialog.window!!.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    //todo bind add issue observer--
    private fun bindCreateIssueObserver(dialog: Dialog) {
        viewModel.solutionList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                Global.warningmessagetoast(requireActivity(), it)
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        if (it.data.size > 0 && it.data != null) {
                            callIssueListApi()
                            dialog.dismiss()
                        }else{
                            callIssueListApi()
                            dialog.dismiss()
                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        Global.warningmessagetoast(requireActivity(), it.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }


    //todo solution observer bind here---
    private fun bindSolutionListObserver(acSolutionItems: AutoCompleteTextView, rvSolutionItems: RecyclerView) {
        viewModel.solutionList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                Global.warningmessagetoast(requireActivity(), it)
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        if (it.data.size > 0 && it.data != null) {
                            var tempList: List<SolutionListResponseModel.DataXXX> = ArrayList<SolutionListResponseModel.DataXXX>()
                            tempList = filterlist(it.data)
                            solutionDataList_gl.clear()
                            solutionDataList_gl.addAll(tempList)

                            val itemNames: MutableList<String> = ArrayList()
                            for (item in solutionDataList_gl) {
                                itemNames.add(item.Title)
                            }

                            var adapter = SolutionListAutoCompleteADapter(requireActivity(), R.layout.drop_down_item_textview, solutionDataList_gl)
                            acSolutionItems.setAdapter(adapter)

                            acSolutionItems.onItemClickListener = object : AdapterView.OnItemClickListener {
                                override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                                    val selectedData = solutionDataList_gl[position]

                                    if (selectedData != null) {
                                        rvSolutionItems.visibility = View.VISIBLE
                                    } else {
                                        rvSolutionItems.visibility = View.GONE
                                    }
                                    if (selectedData != null && !selectedDataList.contains(selectedData)) {
                                        selectedDataList.add(selectedData)
                                        adapter.notifyDataSetChanged()
                                        Log.e("selected", "onItemClick: " + selectedDataList.size)
                                        val gridLayoutManager = GridLayoutManager(requireActivity(), 2)
                                        val adapterEmp = CategoryItemSelectedAdapter(requireActivity(), selectedDataList)
                                        rvSolutionItems.layoutManager = gridLayoutManager
                                        rvSolutionItems.adapter = adapterEmp
                                        adapterEmp.notifyDataSetChanged()
                                        adapter.notifyDataSetChanged()
                                    }

                                    acSolutionItems.text.clear()
                                }

                            }

                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        Global.warningmessagetoast(requireActivity(), it.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }


    private fun filterlist(value: ArrayList<SolutionListResponseModel.DataXXX>): List<SolutionListResponseModel.DataXXX> {
        val tempList: MutableList<SolutionListResponseModel.DataXXX> = ArrayList<SolutionListResponseModel.DataXXX>()
        for (installedItemModel in value) {
            if (!installedItemModel.Title.equals("admin")) {
                tempList.add(installedItemModel)
            }
        }
        return tempList
    }


    //todo issue category observe bind here---
    private fun bindIssueCateogryObserver(acIssueItem: AutoCompleteTextView) {
        viewModel.IssueCategoryList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                Global.warningmessagetoast(requireActivity(), it)
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        if (it.data.size > 0 && it.data != null) {

                            issueCategoryList_gl.clear()
                            issueCategoryList_gl.addAll(it.data)

                            var adapter = IssueCategoryAdapter(requireActivity(), R.layout.drop_down_item_textview, issueCategoryList_gl)
                            acIssueItem.setAdapter(adapter)

                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        Global.warningmessagetoast(requireActivity(), it.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }


    //todo item observer bind here---
    private fun bindItemSelectObserver(acItem: AutoCompleteTextView) {
        viewModel.itemAllList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                Global.warningmessagetoast(requireActivity(), it)
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        if (it.data.size > 0 && it.data != null) {

                            itemAllList_gl.clear()
                            itemAllList_gl.addAll(it.data)

                            var adapter = ItemListAutoCompleteAdapter(requireActivity(), R.layout.drop_down_item_textview, itemAllList_gl)
                            acItem.setAdapter(adapter)

                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        Global.warningmessagetoast(requireActivity(), it.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }

    private fun subscribeToObserver() {
        viewModel.checkList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserverError: $it")
                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {

            }, {
                if (it.status.equals(200)) {
                    Log.e(TAG, "subscribeToObserver: ")
                    checkList.clear()
                    checkList.addAll(it.data)

                    if (checkList.size > 0) {
                        ticketbiding.headCheckList.visibility = View.VISIBLE
                    } else {
                        ticketbiding.headCheckList.visibility = View.GONE
                    }

                    val layoutManager = LinearLayoutManager(requireContext())
                    adapterCheckList = CheckListTicketAdapter(checkList)
                    ticketbiding.rvCheeckList.layoutManager = layoutManager
                    ticketbiding.rvCheeckList.adapter = adapterCheckList


                    adapterCheckList.setOnItemClickListener { data ->
                        when (ticketdata.TicketStatus) {
                            "Accepted" -> {
                                if (ticketdata.Status == "Closed") {
                                    Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                                }
                                if (ticketdata.Status != "Resolved") {
                                    if (ticketdata.TicketStartDate.isNotEmpty()) {
                                        openCHeklistDialog(requireContext(), data)
                                    } else {
                                        Global.warningmessagetoast(requireContext(), resources.getString(R.string.yet_to_start))
                                    }
                                } else {
                                    Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")
                                }
                            }
                            "Pending" -> {
                                Global.warningdialogbox(requireContext(), "Your ticket is in pending state,Kindly accept it")
                            }

                            "Rejected" -> {
                                Global.warningdialogbox(requireContext(), "Your ticket will be rejected")
                            }
                        }

                    }


                }
                else {
                    Log.e(TAG, "subscribeToObserverApiError: ${it.message}")
                }
            }

        ))


    }


    fun refreshFragment() {
        Log.e(TAG, "refreshFragment: ")

       /* var hashMap = HashMap<String, String>()
        hashMap["TicketId"] = ticketdata.id.toString()
        viewModel.getCheckListOfTicket(hashMap)
        subscribeToObserver() */ // Perform the necessary actions to refresh the fragment
    }



    private fun openCHeklistDialog(context: Context, ticketChecklistData: DataCheckList) {
        val dialogbinding: DialogCheckListStatusChangerBinding =
            DialogCheckListStatusChangerBinding.inflate(layoutInflater)
        val dialog = Dialog(context, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogbinding.root)
//        dialog.window!!.setBackgroundDrawable(
//            ColorDrawable(Color.TRANSPARENT)
//        )
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
//        val try_again: Button = dialog.findViewById(R.id.try_again)
//        val done: Button = dialog.findViewById(R.id.done)
//        val edttext: EditText = dialog.findViewById(R.id.edttext)
        dialogbinding.headerNewChekList.text = ticketChecklistData.Name

        val gson = Gson()
        try {
            val person = gson.fromJson(ticketChecklistData.Data, Array<DataFromJsonCheckList>::class.java).toList()
            for (instruction in person) {
                println("Desc: ${instruction.desc}")
                println("Status: ${instruction.status}")
                println("Remark: ${instruction.remark}")
                println()
            }

            val layoutManager = LinearLayoutManager(requireContext())
            var adapterItemCheckList =
                CheckListDialogItemAdapter(person as ArrayList<DataFromJsonCheckList>)

            dialogbinding.rvDialogCheckList.layoutManager = layoutManager
            dialogbinding.rvDialogCheckList.adapter = adapterItemCheckList
            Global.listOfCheckList.addAll(person)


            adapterItemCheckList.setOnYesNoSpinnerClickListener { desc, spinnerValue, editText, position ->
                Log.e(
                    TAG,
                    "openCHeklistDialogSPinner: STRINGEDit====>$editText,STRINGSPINNER====>$spinnerValue,STRINGDESC=====>$desc  POS===>$position "
                )
//                Global.listOfCheckList.add(i, DataFromJsonCheckList(s1,s,))
                Global.listOfCheckList[position].remark = editText;
                Global.listOfCheckList[position].status = spinnerValue;
                Log.e("fChangeREMARK==>", Global.listOfCheckList[position].remark)
                Log.e("fChangeSTATUS==>", Global.listOfCheckList[position].status)

            }


            dialogbinding.done.setOnClickListener {

                var requestModel = BodyUpdateCheckListItem(
                    Comment = "",
                    CreatedDate = "",
                    CreatedTime = "",
                    Data = Global.listOfCheckList,
                    Description = ticketChecklistData.Description,
                    Duration = "",
                    Field1 = ticketdata.Type,
                    Field2 = ticketdata.SubType,
                    Field3 = "",
                    Field4 = "",
                    Field5 = "",
                    Status = 1,
                    TicketId = ticketdata.id.toString(),
                    UpdatedDate = "",
                    UpdatedTime = "",
                    id = ticketChecklistData.id,
                    Name = ticketChecklistData.Name
                )
                val gson = Gson()
                val jsonTut: String = gson.toJson(requestModel)
                Log.e("data", jsonTut)

                viewModel.getUpdateTicketCheckList(requestModel)

                viewModel.updatecheckList.observe(viewLifecycleOwner, Event.EventObserver(
                    onError = {
                        Log.e(TAG, "subscribeToObserverError: $it")
                        Global.warningmessagetoast(requireContext(), it)

                    }, onLoading = {

                    }, {
                        if (it.status.equals(200)) {
                            Global.listOfCheckList.clear()
                            Log.e(TAG, "subscribeToObserver: ")
                            Global.successmessagetoast(requireContext(), it.message)
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                refreshFragment()
                            }

                            dialog.dismiss()

                        } else {
                            Log.e(TAG, "subscribeToObserverApiError: ${it.message}")
                        }
                    }

                ))

            }


        } catch (e: Exception) {
            Log.e("TAG", "onBindViewHolderERROR: ${e.message}")

            //   Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }


        dialog.setOnDismissListener {
            Log.e(TAG, "onDismiss: ")
            Global.listOfCheckList.clear()
        }


        if (!ticketChecklistData.Status.equals(2)) {
            dialogbinding.linearButton.visibility = View.VISIBLE
        } else {
            dialogbinding.linearButton.visibility = View.GONE
        }

        dialogbinding.tryAgain.setOnClickListener {
            dialog.dismiss()
        }


        dialog.window!!.attributes = lp
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }


    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // For loading PDF
        when (requestCode) {
            12 -> if (resultCode == Activity.RESULT_OK) {

                pdfUri = data?.data!!
                val uri: Uri = data.data!!
                val uriString: String = uri.toString()
                openpdfpath =
                    copyFileToInternalStorage(pdfUri, "com.android.servicesupport").toString()

                var pdfName: String? = null
                if (uriString.startsWith("content://")) {
                    var myCursor: Cursor? = null
                    try {
                        // Setting the PDF to the TextView
                        myCursor =
                            requireContext().contentResolver.query(uri, null, null, null, null)
                        if (myCursor != null && myCursor.moveToFirst()) {
                            pdfName =
                                myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            openpdffrom = "URI"
                            ticketbiding.docView.isVisible = true
                            ticketbiding.docname.text = pdfName
                            ticketbiding.progressbar.start()

                            uploaddocument()


                        }
                    } finally {
                        myCursor?.close()
                    }
                }

            }
        }
    }


    private fun uploaddocument() {
        val pdfuristring = FileUtil.getPath(requireContext(), pdfUri)

        //todo calling multipart api..
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        val file: File = File(openpdfpath)

        builder.addFormDataPart("Attachments", file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
        builder.addFormDataPart("EmployeeId", Prefs.getString(Global.Employee_Code))
        builder.addFormDataPart("TicketId", ticketdata.id.toString())

        val requestBody = builder.build()
        Log.e("payload", requestBody.toString())

        viewModel.imageupload(requestBody)

        bindAttachmentObserver()

    }

    //todo upload history attachment observer..

    private fun bindAttachmentObserver() {
        viewModel.customerUpload.observe(this, Event.EventObserver(
            onError = {
                ticketbiding.progressbar.stop()
                ticketbiding.loadingback.visibility = View.GONE
                Global.warningmessagetoast(requireContext(), it)
                Log.e("ticketAcceptReject", it)
            },
            onLoading = {
                ticketbiding.progressbar.start()
                ticketbiding.loadingback.visibility = View.VISIBLE
            },
            onSuccess = { response ->
                Log.e("response", response.toString())
                ticketbiding.progressbar.stop()
                ticketbiding.loadingback.visibility = View.GONE
                if (response.status == 200) {
                    Toast.makeText(requireContext(), "Upload SuccessFully", Toast.LENGTH_LONG).show()

                    val tickethistory = HashMap<String, Int>()
                    tickethistory["id"] = ticketdata.id

                    viewModel.particularTicketDetails(tickethistory)

                    bindTicketDetailObserver()
                } else {
                    Global.warningmessagetoast(requireContext(), response.message);
                }
            }
        ))
    }


    private fun runTimer() {

        // Get the text view.

        // Creates a new Handler
        val handler = Handler()

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(object : Runnable {
            override fun run() {
                val hours: Long = seconds / 3600
                val minutes: Long = seconds % 3600 / 60
                val secs: Long = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                // Set the text view text.
                ticketbiding.timer.text = "Duration : " + time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun setdata(ticketdata: TicketData) {
        if (ticketdata != null) {
            this.ticketdata = ticketdata
        }
        if (ticketdata!!.CustomerPIR.toString().isNotEmpty()) {
            openpdffrom = "API"
            ticketbiding.docView.isVisible = true
            ticketbiding.docname.text = ticketdata.CustomerPIR
        } else
            ticketbiding.docView.isVisible = false

        ticketbiding.serialNum.text = ticketdata.ProductSerialNo
        if (ticketdata.Status == "Closed") {
            ticketbiding.reset.isEnabled = false
        }
        else if (ticketdata.Status != "Resolved")
            ticketbiding.reset.isEnabled = ticketdata.TicketStartDate!!.isNotEmpty()
        else
            ticketbiding.reset.isEnabled = false
        ticketbiding.play.isEnabled = ticketdata.TicketStartDate.toString().isEmpty()
        ticketbiding.stop.isEnabled = ticketdata.TicketEndDate.toString().isEmpty()
        ticketbiding.productname.text = ticketdata.ProductName
        ticketbiding.productcategory.text = ticketdata.ProductCategoryName
        ticketbiding.orderNo.text = ticketdata.DeliveryID
        if (ticketdata.WarrantyDueDate.isNotEmpty() && ticketdata.WarrantyDueDate != "None") {
            ticketbiding.warrantydate.text =
                Global.formatDateFromDateString(ticketdata.WarrantyDueDate)
        }
        if (ticketdata.ExtWarrantyDueDate.isNotEmpty() && ticketdata.ExtWarrantyDueDate != "None") {
            ticketbiding.extWarranty.text =
                Global.formatDateFromDateString(ticketdata.ExtWarrantyDueDate)
        }
        if (ticketdata.AMCDueDate.isNotEmpty()) {
            ticketbiding.amcDate.text = Global.formatDateFromDateString(ticketdata.AMCDueDate)
        }
        if (ticketdata.CMCDueDate.isNotEmpty()) {
            ticketbiding.cmcDate.text = Global.formatDateFromDateString(ticketdata.CMCDueDate)
        }
        ticketbiding.progressbar.stop()
        ticketbiding.loadingback.visibility = View.GONE
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.create_request -> {

                if (Global.TicketAuthentication || Prefs.getString(Global.Employee_role, "")
                        .equals(Global.ADMIN_STRING, ignoreCase = true)
                ) {
                    when (ticketdata.TicketStatus) {

                        "Accepted" -> {
                            if (ticketdata.Status == "Closed") {
                                Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                            }
                            else if (ticketdata.Status != "Resolved") {
                                if (ticketdata.TicketStartDate!!.isNotEmpty() && ticketdata.TicketEndDate!!.isEmpty()) {

                                    val intent = Intent(context, CreatePartRequest::class.java)
                                    intent.putExtra(Global.INTENT_TICKET_ID, ticketdata.id.toString())
                                    //  intent.putExtra("TicketData", ticketdata)
                                    startActivity(intent)
                                } else {
                                    Global.warningmessagetoast(
                                        requireContext(),
                                        "Your ticket timer is not started yet"
                                    )
                                }
                            }else{
                                Global.warningmessagetoast(requireContext(), "Your Ticket is Resolved")
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
                } else {
                    Global.warningdialogbox(requireContext(), "You do not have authentication of create request")
                }
            }
            R.id.showallrequest -> {
//                val allPartRequest = AllPartRequest()
//                val bundle = Bundle()
//                bundle.putString("TicketID", ticketdata.id.toString())
//                val transaction = childFragmentManager.beginTransaction()
//                allPartRequest.arguments = bundle
//                transaction.replace(R.id.main_container, allPartRequest).addToBackStack(null)
//                transaction.commit()


                val intent = Intent(activity, AllPartRequestActivity::class.java).also {
                    it.putExtra("TicketID", ticketdata.id.toString())
                    startActivity(it)
                }

            }

            R.id.play -> {
                if (Global.TicketAuthentication || Prefs.getString(Global.Employee_role, "").equals(Global.ADMIN_STRING, ignoreCase = true)) {
                    Log.e(TAG, "TICKETSTATUS====>: ${ticketdata.TicketStatus}")
                    when (ticketdata.TicketStatus) {

                        "Accepted" -> {
                            if (ticketdata.Status == "Closed") {
                                Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                            }
                            if (ticketdata.Status != "Resolved") {

                                if (ticketdata.Type == "Installation") {
                                    ticketbiding.play.background = ContextCompat.getDrawable(requireContext(), R.drawable.play_off)
                                    ticketbiding.stop.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop)
                                    ticketbiding.reset.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_refresh)

                                    val data = HashMap<String, Any>()
                                    data["TicketId"] = ticketdata.id
                                    data["EmployeeId"] = Prefs.getString(Global.Employee_Code)
                                    data["TicketStartDate"] = Global.getTodayDate()

                                    running = true

                                    viewModel.startstoptimer(data)
                                    bindStartStopObserver()

                                } else {

                                    ticketbiding.play.background = ContextCompat.getDrawable(requireContext(), R.drawable.play_off)
                                    ticketbiding.stop.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop)
                                    ticketbiding.reset.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_refresh)

                                    val data = HashMap<String, Any>()
                                    data["TicketId"] = ticketdata.id
                                    data["EmployeeId"] = Prefs.getString(Global.Employee_Code)
                                    data["TicketStartDate"] = Global.getTodayDate()

                                    running = true

                                    viewModel.startstoptimer(data)
                                    bindStartStopObserver()
                                }


                            } else {
                                Global.warningdialogbox(
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
                } else {
                    Global.warningdialogbox(
                        requireContext(),
                        "You do not have authentication to work on this ticket"
                    )
                }
            }


            R.id.stop -> {
                if (Global.TicketAuthentication || Prefs.getString(Global.Employee_role, "")
                        .equals(Global.ADMIN_STRING, ignoreCase = true)
                ) {
                    if (running) {

                        val data = HashMap<String, Any>()
                        data["TicketId"] = ticketdata.id
                        data["EmployeeId"] = Prefs.getString(Global.Employee_Code)
                        data["TicketEndDate"] = Global.getTodayDate()

                        Log.e("payload", data.toString())
                        viewModel.startstoptimer(data)
                        bindStartStopObserver()

                        running = false

                        ticketbiding.play.background = ContextCompat.getDrawable(requireContext(), R.drawable.play_off)
                        ticketbiding.stop.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stopoff)
                        ticketbiding.reset.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_refresh)

                        /*  if(checkallchecklist()){
                      }else{
                              Global.warningdialogbox(requireContext(),"Your checklist did not complete yet")
                          }*/
                    }
                } else {
                    Global.warningdialogbox(
                        requireContext(),
                        "You do not have authentication to work on this ticket"
                    )
                }
            }
            R.id.reset -> {

                if (Global.TicketAuthentication || Prefs.getString(Global.Employee_role, "").equals(Global.ADMIN_STRING, ignoreCase = true)) {
                    running = false
                    seconds = 0
                    if (ticketdata.Status == "Closed") {
                        Global.warningmessagetoast(requireContext(), "Your Ticket is Closed")
                    }

                    if (ticketdata.Status != "Resolved") {

                        ticketbiding.play.background = ContextCompat.getDrawable(requireContext(), R.drawable.play_238046)
                        ticketbiding.stop.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_stop)
                        ticketbiding.reset.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_refreshoff)

                        val data = HashMap<String, Any>()
                        data["TicketId"] = ticketdata.id
                        data["EmployeeId"] = Prefs.getString(Global.Employee_Code)

                        viewModel.resettimer(data)
                        bindStartStopObserver()
                    } else {
                        Global.warningdialogbox(requireContext(), "Your Ticket is Resolved")
                    }
                } else {
                    Global.warningdialogbox(
                        requireContext(),
                        "You do not have authentication to work on this ticket"
                    )
                }
            }


        }


    }

    private fun bindStartStopObserver() {
        viewModel.ticketCheckList.observe(this, Event.EventObserver(
            onError = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {

            },
            onSuccess = {
                if (it.status == 200) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

                    val tickethistory = HashMap<String, Int>()
                    tickethistory["id"] = ticketdata.id

                    viewModel.particularTicketDetails(tickethistory)

                    bindTicketDetailObserver()

                } else {
                    Global.warningmessagetoast(requireContext(), it.toString());
                }

            }

        ))
    }

    private fun checkallchecklist(): Boolean {
        for (listdat in tickethistorydata) {
            if (listdat.Status == "False") {
                return false
            }
        }
        return true

    }


    override fun recallApi() {
        val tickethistory = TicketHistoryData(TicketId = ticketdata.id)
        viewModel.getticketchecklist(tickethistory)
        bindObserver()
    }


    //todo bind observer...
    private fun bindObserver() {
        viewModel.ticketCheckList.observe(this, Event.EventObserver(
            onError = {
                ticketbiding.loadingback.visibility = View.GONE

                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                ticketbiding.loadingback.visibility = View.VISIBLE

            },
            onSuccess = {
                if (it.status == 200) {
                    tickethistorydata.clear()
                    tickethistorydata.addAll(it.data)
                    linearLayoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = ServiceTicketAdapter(
                        ticketdata.TicketStatus,
                        tickethistorydata,
                        this@ServiceTicketFragment
                    )
                    ticketbiding.recyclerview.layoutManager = linearLayoutManager
                    ticketbiding.recyclerview.adapter = adapter

                } else {
                    Global.warningmessagetoast(requireContext(), it.toString());

                }

            }

        ))
    }


    var TaxListdialog: Dialog? = null
    private fun openCategorydailog() {
        val backPress: ImageView
        val head_title: TextView
        val recyclerview: RecyclerView
        val loader: LoadingView
        TaxListdialog = Dialog(requireContext())
        val layoutInflater = LayoutInflater.from(context)
        val custom_dialog: View = layoutInflater.inflate(R.layout.taxes_alert, null)
        recyclerview = custom_dialog.findViewById(R.id.recyclerview)
        backPress = custom_dialog.findViewById(R.id.back_press)
        head_title = custom_dialog.findViewById(R.id.heading)
        loader = custom_dialog.findViewById(R.id.loader)
        head_title.text = "Select Category"
        TaxListdialog!!.setContentView(custom_dialog)
        TaxListdialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        TaxListdialog!!.show()
        backPress.setOnClickListener { TaxListdialog!!.dismiss() }

        //todo calling item category
        val opportunityValue = NewLoginData()
        opportunityValue.setSalesEmployeeCode(Prefs.getString(Global.Employee_Code, ""))
        Log.e("payload==>", opportunityValue.toString())

        viewModel.getAllCategory(opportunityValue)

        bindCategoryObserver(recyclerview, loader)

    }


    //todo category observer..
    private fun bindCategoryObserver(recyclerview: RecyclerView, loader: LoadingView) {
        viewModel.itemCategoryList.observe(
            this, Event.EventObserver(
                onError = {
                    loader.visibility = View.GONE
                    Log.e("fail==>", it)
                    Global.warningmessagetoast(requireContext(), it)
                },
                onLoading = {
                    loader.visibility = View.VISIBLE
                },
                onSuccess = { response ->
                    if (response.status == 200) {
                        loader.visibility = View.GONE
                        Log.e("response==>", response.message)
                        val adapter = CategoryAdapter(
                            this@ServiceTicketFragment,
                            response.data,
                            TaxListdialog!!
                        )
                        recyclerview.layoutManager =
                            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                        recyclerview.adapter = adapter
                    } else {
                        loader.visibility = View.GONE
                        Global.warningmessagetoast(requireContext(), response.message)
                    }

                })
        )
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

            }
        }


    override fun onClick(po: Int) {
        val intent = Intent(context, ItemsList::class.java)
        intent.putExtra("CategoryID", po)
        launcher.launch(intent)

    }


    private fun selectPdf() {
        Dexter.withActivity(requireActivity())
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        // do you work now
                        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
                        pdfIntent.type = "application/pdf"
                        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
                        startActivityForResult(pdfIntent, 12)
                        /* FilePickerBuilder.instance
                             .setMaxCount(1) //optional
                             .setActivityTheme(R.style.AppTheme) //optional
                             .pickFile(requireActivity());*/
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently, we will show user a dialog message.
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(requireContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }.onSameThread().check()

    }


    lateinit var docPaths: ArrayList<Uri>


    private fun copyFileToInternalStorage(uri: Uri, newDirName: String): String? {
        val returnCursor: Cursor = requireContext().getContentResolver().query(
            uri, arrayOf(
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
            ), null, null, null
        )!!


        /*
     * Get the column indexes of the data in the Cursor,
     *     * move to the first row in the Cursor, get the data,
     *     * and display it.
     * */
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val output: File
        if (newDirName != "") {
            val dir: File = File(requireContext().getFilesDir().toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            output = File(requireContext().getFilesDir().toString() + "/" + newDirName + "/" + name)
        } else {
            output = File(requireContext().getFilesDir().toString() + "/" + name)
        }
        try {
            val inputStream: InputStream? =
                requireContext().getContentResolver().openInputStream(uri)
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


}
