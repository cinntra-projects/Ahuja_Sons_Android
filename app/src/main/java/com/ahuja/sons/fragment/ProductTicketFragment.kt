package com.ahuja.sons.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.activity.TicketDetailsActivity
import com.ahuja.sons.adapter.FollowUpAdapter
import com.ahuja.sons.adapter.ProductConversationAdapter
import com.ahuja.sons.adapter.ProductTicketAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.OverviewTicketBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.TicketHistoryData
import com.ahuja.sons.model.TicketHistoryResponse
import com.ahuja.sons.newapimodel.DataFollowUpList
import com.ahuja.sons.newapimodel.ResponseFollowUp
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.viewmodel.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.abs


class ProductTicketFragment(val ticketdata: TicketData) : Fragment() {

    private lateinit var ticketbiding: OverviewTicketBinding
    lateinit var adapter: ProductTicketAdapter
    lateinit var covadapter: ProductConversationAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var convlinearLayoutManager: LinearLayoutManager
    lateinit var followUpLinearLayout: LinearLayoutManager
    lateinit var viewModel: MainViewModel
    var tickethistorydata = ArrayList<TicketHistoryData>()
    var ticketFollowUpdata = ArrayList<DataFollowUpList>()
    var ticketconvodata = ArrayList<TicketHistoryData>()


    var pageno = 1
    var maxItem = 10
    var covopageno = 1
    var isScrollingpageHistory: Boolean = false

    var followPageNo = 1
    var recallApi = true
    var convorecallApi = true
    var followUpApi = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = OverviewTicketBinding.inflate(layoutInflater)

        viewModel = (activity as TicketDetailsActivity).viewModel

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        convlinearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        followUpLinearLayout = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        val appbar = activity?.findViewById<AppBarLayout>(R.id.appbar)


        appbar?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) > 30) {

                /*           ticketbiding.addcomment.margin(left = 0F)
                           ticketbiding.addcomment.margin(right = 30F)
                           ticketbiding.addcomment.margin(top = 0F)
                           ticketbiding.addcomment.margin(bottom = 90F)*/

            } else {
               /* ticketbiding.addcomment.margin(left = 0F)
                ticketbiding.addcomment.margin(right = 30F)
                ticketbiding.addcomment.margin(top = 0F)
                ticketbiding.addcomment.margin(bottom = 150F)*///todo comment by me

            }
        }




        ticketbiding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.

                if (Global.checkForInternet(requireContext())) {
                    var lastCompletelyVisibleItemPosition = (linearLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                    if (ticketbiding.itemCount.text.equals("History")) {
                        if (isScrollingpageHistory && lastCompletelyVisibleItemPosition == tickethistorydata.size - 2 &&recallApi) {
                            pageno++
                            isScrollingpageHistory = false
                            callHistoryAPi()
                        }else{
                            isScrollingpageHistory = true
                        }
                    } else if (ticketbiding.itemCount.text.equals("Conversation")) {

                        if (convorecallApi) {
                            covopageno++
                            callConversationApi()
                        }
                    }
                    else{
                        if (followUpApi) {
                            followPageNo++
                            callFollowUpApi()
                        }
                    }

                }

            }
        })


        //todo bind popup view of menu--
        ticketbiding.itemCount.setOnClickListener {
            ticketbiding.loadingView.start()
            ticketbiding.loadingback.visibility = View.GONE
            ticketbiding.loadingView.stop()
            if (ticketbiding.itemCount.text.equals("History")) {
                showPopupMenu(ticketbiding.itemCount)

            }else  if (ticketbiding.itemCount.text.equals("Conversation")){
                showPopupMenu(ticketbiding.itemCount)

            }
            else {
                showPopupMenu(ticketbiding.itemCount)

            }

        }

        ticketbiding.addcomment.setOnClickListener {
            when (ticketdata.TicketStatus) {
                "Accepted" -> {
                    openCommentDialog()
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

        ticketbiding.addFollowUp.setOnClickListener {
            when (ticketdata.TicketStatus) {
                "Accepted" -> {
                    openAddFollowUpDialog()
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

        return ticketbiding.root
    }

    private fun bindObserver() {
        viewModel.ticketAllHistory.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.loadingView.stop()
                ticketbiding.idPBLoading.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                ticketbiding.loadingback.visibility = View.VISIBLE
                ticketbiding.loadingView.start()
                ticketbiding.idPBLoading.visibility = View.VISIBLE
            },
            onSuccess = {
                if (it.status == 200) {

                    if (it.data.isNullOrEmpty() && it.data.size == 0){
                        tickethistorydata.clear()
                        tickethistorydata.addAll(it.data as ArrayList<TicketHistoryData>)
                        setHistoryAdapter()
                        ticketbiding.nodatafound.visibility = View.VISIBLE
                    }else{
                        val valueList = it.data

                        if (pageno == 1) {
                            tickethistorydata.clear()
                            tickethistorydata.addAll(valueList)
                        } else {
                            tickethistorydata.addAll(valueList)
                        }
                        ticketbiding.nodatafound.visibility = View.GONE
                        setHistoryAdapter()
                        adapter.notifyDataSetChanged()

                        if (valueList.size < 10) {
                            recallApi = false
                        }

                    }
                    ticketbiding.loadingback.visibility = View.GONE
                    ticketbiding.loadingView.stop()
                    ticketbiding.idPBLoading.visibility = View.GONE

                } else {
                    Global.warningmessagetoast(requireContext(), it.toString());

                }

            }

        ))
    }


    //todo calling conversation api here...
    private fun callHistoryAPi() {
        ticketbiding.idPBLoading.visibility = View.VISIBLE

        val tickethistory = HashMap<String, Int>()
        tickethistory["TicketId"] = ticketdata.id
        tickethistory["PageNo"] = pageno
        tickethistory["maxItem"] = maxItem

        val call: Call<TicketHistoryResponse> = ApiClient().service.getHistoryList(tickethistory)
        call.enqueue(object : Callback<TicketHistoryResponse?> {
            override fun onResponse(call: Call<TicketHistoryResponse?>, response: Response<TicketHistoryResponse?>) {
                try {
                    if (response.isSuccessful){
                        ticketbiding.loadingback.visibility = View.GONE
                        ticketbiding.idPBLoading.visibility = View.GONE
                        ticketbiding.loadingView.stop()
                        if (response.body()!!.status == 200) {

                            if (response.body()!!.data.isNullOrEmpty() && response.body()!!.data.size == 0){
                                tickethistorydata.clear()
                                tickethistorydata.addAll(response.body()!!.data as ArrayList<TicketHistoryData>)
                                setHistoryAdapter()
                                ticketbiding.nodatafound.visibility = View.VISIBLE
                            }else{
                                val valueList = response.body()!!.data

                                if (pageno == 1) {
                                    tickethistorydata.clear()
                                    tickethistorydata.addAll(valueList)
                                } else {
                                    tickethistorydata.addAll(valueList)
                                }
                                ticketbiding.nodatafound.visibility = View.GONE
                                setHistoryAdapter()
                                adapter.notifyDataSetChanged()

                                if (valueList.size < 10) {
                                    recallApi = false
                                }

                            }

                        }else{
                            ticketbiding.loadingback.visibility = View.GONE
                            ticketbiding.idPBLoading.visibility = View.GONE
                            ticketbiding.loadingView.stop()
                            ticketbiding.nodatafound.visibility = View.VISIBLE
                            Global.warningmessagetoast(requireContext(), response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<TicketHistoryResponse?>, t: Throwable) {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.loadingView.stop()
                Global.errormessagetoast(requireContext(), t.message.toString())
            }
        })

    }

    fun setHistoryAdapter(){
        adapter = ProductTicketAdapter(tickethistorydata)
        ticketbiding.recyclerview.layoutManager = linearLayoutManager
        ticketbiding.recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    //todo calling conversation api here...
    private fun callConversationApi() {
        ticketbiding.idPBLoading.visibility = View.VISIBLE
        val tickethistory = HashMap<String, Int>()
        tickethistory["TicketId"] = ticketdata.id
        tickethistory["PageNo"] = covopageno
        val call: Call<TicketHistoryResponse> = ApiClient().service.getTicketConversationAll(tickethistory)
        call.enqueue(object : Callback<TicketHistoryResponse?> {
            override fun onResponse(call: Call<TicketHistoryResponse?>, response: Response<TicketHistoryResponse?>) {
                try {
                    if (response.isSuccessful){
                        ticketbiding.loadingback.visibility = View.GONE
                        ticketbiding.idPBLoading.visibility = View.GONE
                        ticketbiding.loadingView.stop()
                        if (response.body()!!.status == 200) {

                            if (response.body()!!.data.isNullOrEmpty()){
                                ticketconvodata.clear()
                                ticketconvodata.addAll(response.body()!!.data as ArrayList<TicketHistoryData>)
                                setConversationAdapter()
                                ticketbiding.nodatafound.visibility = View.VISIBLE
                            }else{
                                val valueList = response.body()!!.data

                                if (covopageno == 1) {
                                    ticketconvodata.clear()
                                    ticketconvodata.addAll(valueList)
                                } else {
                                    ticketconvodata.addAll(valueList)
                                }

                                setConversationAdapter()
                                ticketbiding.nodatafound.visibility = View.GONE
                                covadapter.notifyDataSetChanged()

                                if (valueList.size < 10) {
                                    convorecallApi = false
                                }

                            }

                        }else{
                            ticketbiding.loadingback.visibility = View.GONE
                            ticketbiding.idPBLoading.visibility = View.GONE
                            ticketbiding.loadingView.stop()
                            ticketbiding.nodatafound.visibility = View.VISIBLE
                            Global.warningmessagetoast(requireContext(), response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<TicketHistoryResponse?>, t: Throwable) {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.loadingView.stop()
                Global.errormessagetoast(requireContext(), t.message.toString())
            }
        })

    }


    fun setConversationAdapter(){
        covadapter = ProductConversationAdapter(ticketconvodata)
        ticketbiding.convorecyclerview.layoutManager = convlinearLayoutManager
        ticketbiding.convorecyclerview.adapter = covadapter
        covadapter.notifyDataSetChanged()
    }


    //todo calling follow up api here...
    private fun callFollowUpApi() {
        ticketbiding.idPBLoading.visibility = View.VISIBLE

        val tickethistory = HashMap<String, Any>()
        tickethistory["SourceID"] = ticketdata.id
        tickethistory["SourceType"] = "Ticket"
        tickethistory["Emp"] = Prefs.getString(Global.Employee_Code, "") //Global.Employee_SalesEmpCode
        val call: Call<ResponseFollowUp> = ApiClient().service.getFollowUpAllList(tickethistory)
        call.enqueue(object : Callback<ResponseFollowUp?> {
            override fun onResponse(call: Call<ResponseFollowUp?>, response: Response<ResponseFollowUp?>) {
                try {
                    if (response.isSuccessful){

                        if (response.body()!!.status == 200) {
                            ticketbiding.loadingback.visibility = View.GONE
                            ticketbiding.idPBLoading.visibility = View.GONE
                            ticketbiding.loadingView.stop()

                            if (response.body()!!.data.isNullOrEmpty() && response.body()!!.data.size == 0){
                                ticketFollowUpdata.clear()
                                ticketFollowUpdata.addAll(response.body()!!.data)
                                setFollowUpAdapter()
                            }
                            else{
                                var valueList = response.body()!!.data

                                if (followPageNo == 1){
                                    ticketFollowUpdata.clear()
                                    ticketFollowUpdata.addAll(valueList)
                                }else{
                                    ticketFollowUpdata.addAll(valueList)
                                }

                                setFollowUpAdapter()
                                ticketbiding.nodatafound.visibility = View.GONE

                                if (valueList.size < 10)
                                    followUpApi = false
                            }
                        }else{
                            ticketbiding.loadingback.visibility = View.GONE
                            ticketbiding.idPBLoading.visibility = View.GONE
                            ticketbiding.loadingView.stop()
                            ticketbiding.nodatafound.visibility = View.VISIBLE
                            Global.warningmessagetoast(requireContext(), response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseFollowUp?>, t: Throwable) {
                ticketbiding.loadingback.visibility = View.GONE
                ticketbiding.idPBLoading.visibility = View.GONE
                ticketbiding.loadingView.stop()
                Global.errormessagetoast(requireContext(), t.message.toString())
            }
        })

    }


    private fun setFollowUpAdapter() {
        ticketbiding.rvFollowUp.visibility = View.VISIBLE
        var adapter : FollowUpAdapter = FollowUpAdapter(ticketFollowUpdata)
        ticketbiding.rvFollowUp.layoutManager = followUpLinearLayout
        ticketbiding.rvFollowUp.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.menuInflater.inflate(R.menu.menu_log, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.history -> {
                    // Handle item 1 click
                    ticketbiding.recyclerview.visibility = View.VISIBLE
                    ticketbiding.rvFollowUp.visibility = View.GONE
                    ticketbiding.addFollowUp.visibility = View.GONE
                    ticketbiding.convorecyclerview.visibility = View.GONE
                    ticketbiding.addcomment.visibility = View.GONE
                    ticketbiding.itemCount.text = "History"
                    pageno = 1
                    recallApi = true
                    tickethistorydata.clear()
                   /* val tickethistory = HashMap<String, Int>()
                    tickethistory["TicketId"] = ticketdata.id
                    tickethistory["PageNo"] = pageno

                    viewModel.getTicketHistory(tickethistory)
                    bindObserver()*/

                    callHistoryAPi()

                    true
                }
                R.id.conversation -> {
                    // Handle item 2 click
                    ticketbiding.addcomment.visibility = View.VISIBLE
                    ticketbiding.addFollowUp.visibility = View.GONE
                    ticketbiding.recyclerview.visibility = View.GONE
                    ticketbiding.convorecyclerview.visibility = View.VISIBLE
                    ticketbiding.rvFollowUp.visibility = View.GONE
                    ticketbiding.itemCount.text = "Conversation"
                    covopageno = 1
                    convorecallApi = true
                    ticketconvodata.clear()

                    callConversationApi()

                    true
                }

                R.id.followup -> {
                    // Handle item 2 click
                    ticketbiding.rvFollowUp.visibility = View.VISIBLE
                    ticketbiding.recyclerview.visibility = View.GONE
                    ticketbiding.convorecyclerview.visibility = View.GONE
                    ticketbiding.addcomment.visibility = View.GONE
                    ticketbiding.addFollowUp.visibility = View.VISIBLE
                    ticketbiding.itemCount.text = "Follow Up"
                    followPageNo = 1
                    followUpApi = true
                    ticketFollowUpdata.clear()


                    callFollowUpApi()

                   /* val tickethistory = HashMap<String, Any>()
                    tickethistory["SourceID"] = ticketdata.id
                    tickethistory["SourceType"] = "Ticket"
                    tickethistory["Emp"] = Prefs.getString(Global.Employee_Code, "") //Global.Employee_SalesEmpCode
                    viewModel.getFollowUpList(tickethistory)

                    bindFollowUpOnserver()*/

                    true
                }
                // Add more cases for additional menu items

                else -> false
            }
        }

        popupMenu.show()
    }



    private lateinit var myTime: Calendar
    var t1hr = 0
    var t1min = 0


    //todo added followup
    private fun openAddFollowUpDialog() {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_follow_up_layout)
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
        val time_view: RelativeLayout = dialog.findViewById(R.id.time_view)
        val time_value: TextView = dialog.findViewById(R.id.time_value)
        val date: EditText = dialog.findViewById(R.id.date)
        val acModeCommunication: AutoCompleteTextView = dialog.findViewById(R.id.acModeCommunication)

        var modeValue = ""

        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, Global.modeOfCommunication_list)
        acModeCommunication.setAdapter(adapter)

        //todo mode communication item selected
        acModeCommunication.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (Global.modeOfCommunication_list.isNotEmpty()) {
                    modeValue = Global.modeOfCommunication_list[position]
                    acModeCommunication.setText(Global.modeOfCommunication_list[position])

                    val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, Global.modeOfCommunication_list)
                    acModeCommunication.setAdapter(adapter)
                } else {
                    modeValue = ""
                    acModeCommunication.setText("")
                }
            }

        }


        date.setOnClickListener {
            Global.selectDate(requireContext(), date)
        }

        time_value.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                context, { view, hourOfDay, minute ->
                    t1hr = hourOfDay
                    t1min = minute
                    myTime = Calendar.getInstance()
                    //                        myTime.set(0,0,0,t1hr,t1min);
                    myTime.set(Calendar.HOUR_OF_DAY, t1hr)
                    myTime.set(Calendar.MINUTE, t1min)
                    myTime.set(Calendar.SECOND, 0)
                    myTime.set(Calendar.MILLISECOND, 0)
                    time_value.setText(DateFormat.format("hh:mm aa", myTime))
//                    setAlarm()
                }, 12, 0, false
            )
            timePickerDialog.updateTime(t1hr, t1min)
            timePickerDialog.setMessage(time_value.getHint().toString())
            timePickerDialog.show()
        }


        try_again.setOnClickListener {
            dialog.dismiss()

        }

        done.setOnClickListener {
            if (edttext.text.toString().isNotEmpty()) {
                val tickethistory = HashMap<String, Any>()
                tickethistory["SourceID"] = ticketdata.id
                tickethistory["SourceType"] = "Ticket"
                tickethistory["Emp"] = Prefs.getString(Global.Employee_Code, "")
                tickethistory["Message"] = edttext.text.toString().trim()
                tickethistory["Mode"] = modeValue
                tickethistory["UpdateDate"] = date.text.toString().trim()
                tickethistory["UpdateTime"] = time_value.text.toString().trim()
                tickethistory["Emp_Name"] = Prefs.getString(Global.FirstName, "")

                if (Global.checkForInternet(requireContext())) {
                    createFollowUp(tickethistory, dialog)
                }
            }
        }


        dialog.window!!.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun createFollowUp(tickethistory: HashMap<String, Any>, dialog: Dialog) {

        val call: Call<ResponseFollowUp> = ApiClient().service.addFollowUp(tickethistory)
        call.enqueue(object : Callback<ResponseFollowUp?> {
            override fun onResponse(call: Call<ResponseFollowUp?>, response: Response<ResponseFollowUp?>) {
                try {
                    if (response.isSuccessful){

                        if (response.body()!!.status == 200) {

                            ticketbiding.loadingback.visibility = View.GONE
                            ticketbiding.loadingView.stop()

                            ticketFollowUpdata.clear()
                            followPageNo = 1
                            followUpApi = true

                            callFollowUpApi()

                            dialog.dismiss()


                        }else{
                            Global.warningmessagetoast(requireContext(), response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseFollowUp?>, t: Throwable) {
                Global.errormessagetoast(requireContext(), t.message.toString())
            }
        })

    }


    /*   private fun setAlarm() {
           var alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
           val i = Intent(context, NotificationPublisher::class.java)
           i.putExtra("value", activity!!.resources.getString(R.string.meeting_notification))
           i.putExtra("title", activity!!.resources.getString(R.string.meeting))
           val id = System.currentTimeMillis().toInt()
           var pendingIntent: PendingIntent? = null
           pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
               PendingIntent.getBroadcast(activity, id, i, PendingIntent.FLAG_MUTABLE)
           } else {
               PendingIntent.getBroadcast(activity, id, i, PendingIntent.FLAG_ONE_SHOT)
           }
           //        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, i, PendingIntent.FLAG_IMMUTABLE);//todo comment
           alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, myTime.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
       }*/


    override fun onResume() {
        super.onResume()
        pageno = 1
        covopageno = 1
        recallApi = true
        convorecallApi = true
        ticketconvodata.clear()
        tickethistorydata.clear()

        if (Global.checkForInternet(requireContext())) {
            ticketbiding.loadingView.start()
            if (ticketbiding.itemCount.text.equals("History")) {
                ticketbiding.convorecyclerview.isVisible = false
                ticketbiding.rvFollowUp.isVisible = false

               /* val tickethistory = HashMap<String, Int>()
                tickethistory["TicketId"] = ticketdata.id
                tickethistory["PageNo"] = pageno

                viewModel.getTicketHistory(tickethistory)
                bindObserver()*/

                callHistoryAPi()

            } else if (ticketbiding.itemCount.text.equals("Conversation")){
                ticketbiding.recyclerview.isVisible = false
                ticketbiding.rvFollowUp.isVisible = false

                callConversationApi()
            }
            else {
                ticketbiding.recyclerview.isVisible = false
                ticketbiding.convorecyclerview.isVisible = false

                callFollowUpApi()
            }
        }
    }


    fun View.margin(
        left: Float? = null,
        top: Float? = null,
        right: Float? = null,
        bottom: Float? = null
    ) {
        layoutParams<ViewGroup.MarginLayoutParams> {
            left?.run { leftMargin = dpToPx(this) }
            top?.run { topMargin = dpToPx(this) }
            right?.run { rightMargin = dpToPx(this) }
            bottom?.run { bottomMargin = dpToPx(this) }
        }
    }

    inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
    }

    fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    fun Context.dpToPx(dp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

    private fun openCommentDialog() {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.comment_dialog)
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
        val markprivate: CheckBox = dialog.findViewById(R.id.markprivate)

        markprivate.visibility = View.GONE
        try_again.setOnClickListener {
            dialog.dismiss()

        }
        val stri = if (markprivate.isChecked) {
            "Private"
        } else {
            "Public"
        }

        done.setOnClickListener {
            if (edttext.text.toString().isNotEmpty()) {
                val chatModel = TicketHistoryData(
                    Message = edttext.text.toString().trim { it <= ' ' },
                    OwnerId = Prefs.getString(Global.Employee_Code),
                    OwnerType = "Employee",
                    Type = stri,
                    TicketId = ticketdata.id
                )

                if (Global.checkForInternet(requireContext())) {
                    viewModel.createTicketConversation(chatModel)
                    bindCreateConversationObserver(dialog)
                }
            }
        }


        dialog.window!!.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }


    //todo bind create conversation observer..

    private fun bindCreateConversationObserver(dialog: Dialog) {
        viewModel.ticketAllHistory.observe(
            viewLifecycleOwner, Event.EventObserver(
                onError = {
                    Log.e("ProductTicket===>", "subsribeToObserverERROR===>:$it ")
                },
                onLoading = {

                },
                onSuccess = { ticket ->
                    if (ticket.status == 200) {
                        ticketbiding.loadingback.visibility = View.GONE
                        ticketbiding.loadingView.stop()

                        ticketconvodata.clear()
                        covopageno = 1
                        convorecallApi = true

                        callConversationApi()


                        dialog.dismiss()

                        //todo history list refresh--
//                        onResume()
                    } else {
                        Global.warningmessagetoast(requireContext(), ticket.message);
                    }

                })
        )
    }



    private fun filterlist(tickethistorydata: ArrayList<TicketHistoryData>): java.util.ArrayList<TicketHistoryData> {
        val templist = ArrayList<TicketHistoryData>()
        for (td: TicketHistoryData in tickethistorydata) {
            td.Datetime = Global.newserverformatDateFromDateString(td.Datetime)
                .toString()
            templist.add(td)

        }

        return templist
    }
}
