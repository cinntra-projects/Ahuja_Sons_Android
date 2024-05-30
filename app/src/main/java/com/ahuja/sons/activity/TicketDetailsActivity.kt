package com.ahuja.sons.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.fragment.*
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.adapter.EscallationAdapter
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.TicketDetailNewpageBinding
import com.ahuja.sons.model.ComplainDetailResponseModel
import com.ahuja.sons.model.LogInResponse
import com.ahuja.sons.newapimodel.EscallationResponseModel
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class TicketDetailsActivity : MainBaseActivity() {

    private lateinit var binding: TicketDetailNewpageBinding
    lateinit var viewModel: MainViewModel
    var id = ""

    var ticketdata: TicketData? = null
    var ticketupdatedData: TicketData? = null

    companion object {
        private const val TAG = "TicketDetailsActivity"
    }


    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }
    var pdf_url =""

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {

                //  dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Permission(s) denied", Toast.LENGTH_SHORT).show()
            }
        }


    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            val permissionsArray = permissionsToRequest.toTypedArray()
            requestPermissionLauncher.launch(permissionsArray)
        } else {
            //  dispatchTakePictureIntent()
        }
    }


    lateinit var pagerAdapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TicketDetailNewpageBinding.inflate(layoutInflater)
        setUpViewModel()
        setContentView(binding.root)
        id = intent.getStringExtra("id").toString()
        Log.e(TAG, "onCreate: $id")

        val toolbar: Toolbar = binding.toolbar

        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        // using toolbar as ActionBar

        setSupportActionBar(toolbar)
        Global.cartList.clear()

//        requestPermissions()

       /* var hashMap = HashMap<String, String>()
        hashMap["id"] = id
        viewModel.getTicketOne(hashMap)
        subscribeToObserver()*/


        Log.e(TAG, "onResume: "+" again calling one view pager api " )
        //todo for view pager adapter---
        var hashMap = HashMap<String, String>()
        hashMap["id"] = id
        viewModel.getTicketOneViewPager(hashMap)
        subscribeToObserverForPagerAdapterOnly()

        binding.linearPdf.setOnClickListener {

          /*  Intent(this, ReportActivity::class.java).also {
                it.putExtra("id", id)
                it.putExtra("Type", ticketdata!!.Type)
                startActivity(it)
            }*/

            if (ticketdata!!.Type == "Installation" || ticketdata!!.Type == "De-Installation" || ticketdata!!.Type == "Re-Installation" || ticketdata!!.Type == "Shifting" || ticketdata!!.Type == "Packaging") {
                pdf_url = "${Global.INSTALLATION_TYPE_PDF_URL}${ticketdata!!.id}&ReportType=${ticketdata!!.Type}&ItemSerialNo=${itemListData.SerialNo}&ItemCode=${itemListData.ItemCode}"

            }
            else if (ticketdata!!.Type == "Preventive Maintenance" || ticketdata!!.Type == "Servicing" || ticketdata!!.Type == "Breakdown" || ticketdata!!.Type == "Re-Visit Required"
                || ticketdata!!.Type == "Extra Work" || ticketdata!!.Type == "Part change" || ticketdata!!.Type == "Gas Reflling" || ticketdata!!.Type == "Other"
                || ticketdata!!.Type == "System Checking" || ticketdata!!.Type == "Water Testing"
            ) {
                pdf_url = "${Global.MAINTAINANCE_TYPE_PDF_URL}${ticketdata!!.id}&ReportType=${ticketdata!!.Type}&ItemSerialNo=${itemListData.SerialNo}&ItemCode=${itemListData.ItemCode}"

            }  else if (ticketdata!!.Type == "Site Survey"){
                pdf_url = "${Global.SITE_SURVEY_TYPE_PDF_URL}${ticketdata!!.id}&ReportType=${ticketdata!!.Type}&ItemSerialNo=${""}&ItemCode=${""}"
            }

            Log.e(TAG, "onCreate: "+pdf_url)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url))
            startActivity(browserIntent)
        }

        binding.linearManTrap.setOnClickListener {
            Intent(this, ManTrapRescueLogActivity::class.java).also {
                it.putExtra("id", id)
                startActivity(it)
            }
        }


        // Display application icon in the toolbar
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        //   ticketdata = intent.getParcelableExtra<TicketData>("TicketData")!!


    }

    var itemListData = ItemAllListResponseModel.DataXXX()

    //todo bind item list observer--
    private fun bindItemListObserver() {
        viewModel.itemAllList.observe(this, Event.EventObserver(
            onError = {
                Log.e(FileUtil.TAG, "errorInApi: $it")
                com.ahuja.sons.globals.Global.warningmessagetoast(
                    this,
                    it
                )
            }, onLoading = {

            },
            onSuccess = {
                try {
                    if (it.status == 200) {
                        if (it.data.size > 0 && it.data != null) {
                            itemListData = it.data[0]
                        }

                    } else {
                        Log.e(FileUtil.TAG, "responseError: ${it.message}")
                        com.ahuja.sons.globals.Global.warningmessagetoast(this, it.message!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        ))
    }


    override fun onResume() {
        super.onResume()
        if (pagerAdapter != null){
            Log.e(TAG, "onResume: pagerAdapter notify")
            pagerAdapter.notifyDataSetChanged()
        }

        Log.e(TAG, "onResume: "+" again calling one api " )
        var hashMap = HashMap<String, String>()
        hashMap["id"] = id
        viewModel.getTicketOne(hashMap)
        subscribeToObserver()
    }


    private fun setData() {
        binding.companyName.text = ticketdata!!.BusinessPartner[0].CardName

        binding.mainTitle.text = ticketdata!!.Title
        binding.status.text = ticketdata!!.Status
        binding.id.text = "#" + ticketdata!!.id.toString()
        binding.priority.text = ticketdata!!.Priority
        binding.createddate.text = Global.formatDateFromDateString(ticketdata!!.CreateDate)
        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (ticketdata!!.BusinessPartner[0].CardName?.isNotEmpty()!!) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    ticketdata!!.BusinessPartner[0].CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            binding.nameIcon.setImageDrawable(drawable)
        }

    }


    private fun subscribeToObserver() {
        viewModel.particularTicket.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            }, {
                if (it.status.equals(200)) {
//                    var bpaddressList=it.data[0].BusinessPartner.BPAddresses
//                    bpaddressList.removeAt(0)
//                  //  it.data[0].BusinessPartner.BPAddresses.addAll(bpaddressList)

                    ticketdata = it.data[0]
                    val gson=Gson()

                    if (ticketdata!!.Status == "Resolved" || ticketdata!!.Status == "Closed" ) {
                        if (ticketdata!!.Type == "Installation" ||  ticketdata!!.Type == "Preventive Maintenance" ||
                            ticketdata!!.Type == "Servicing" ||  ticketdata!!.Type == "Site Survey"){
                            binding.linearPdf.visibility = View.VISIBLE
                        }
                    } else {
                        binding.linearPdf.visibility = View.INVISIBLE
                    }
                    if (ticketdata!!.Type.equals("Man-Trap", ignoreCase = false)) {
                        Log.e(TAG, "${ticketdata!!.Type} TRUE==>: ")
                        binding.linearManTrap.visibility = View.GONE
                    } else {
                        Log.e(TAG, "${ticketdata!!.Type} FALSE==>: ")
                        binding.linearManTrap.visibility = View.GONE
                    }

                    if (ticketdata!!.AssignToDetails.isNotEmpty()){
                        Global.TicketAuthentication = Prefs.getString(Global.Employee_Code) == ticketdata!!.AssignToDetails[0].SalesEmployeeCode
                    }
                    Global.TicketStartDate = ticketdata!!.TicketStartDate.toString()
                    Global.TicketEndDate = ticketdata!!.TicketEndDate.toString()
                    Log.e(TAG, "subscribeToObserver:START DATE===> ${ticketdata!!.TicketStartDate}  END DATE===> ${ticketdata!!.TicketEndDate}")
                    Log.e(TAG, "GLOBAL:START DATE===> ${Global!!.TicketStartDate}  END DATE===> ${Global!!.TicketEndDate}")

                    setData()

                    if (ticketdata!!.Type != "Site Survey"){
                        //todo ticket by item list--
                        var jsonObject = JsonObject()
                        jsonObject.addProperty("TicketId", ticketdata!!.id)
                        viewModel.getItemsByTicket(jsonObject)
                        bindItemListObserver()
                    }


                   /* val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
                    pagerAdapter.add(ServiceTicketFragment(ticketdata!!), "Service")
                    pagerAdapter.add(DetailsTicketFragment(ticketdata!!), "Details")
                    pagerAdapter.add(ProductTicketFragment(ticketdata!!), "Logs")
                    pagerAdapter.add(TicketWiseItemListFragment(ticketdata!!), "Items")
                    binding.viewpager.adapter = pagerAdapter

                    binding.tabLayout.setupWithViewPager(binding.viewpager)*/
//    fragments.add(new LeadsActivity());


                    binding.chatView.setOnClickListener {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.setType("text/plain");
                        //  intent.data = Uri.parse("mailto:")
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(ticketdata!!.ContactEmail))

                        val packageManager = packageManager
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Global.warningmessagetoast(this, getString(R.string.no_gmail_found))
                            // Handle the case where Gmail is not installed on the device
                        }

                    }
                    binding.callView.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:" + ticketdata!!.BusinessPartner[0].Phone1)
                        startActivity(intent)
                    }


                    //todo call open menu items popup--
                    binding.openThreeDotMenu.setOnClickListener {
                        openThreeDotOptionMenu(binding.openThreeDotMenu)

                    }


                } else {
                    Log.e(TAG, "responseError: ${it.message}")
                    Global.warningmessagetoast(this, it.message)
                }


            }
        ))
    }


    private fun subscribeToObserverForPagerAdapterOnly() {
        viewModel.particularTicketViewPager.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            }, {
                if (it.status.equals(200)) {

                    Log.e(TAG, "onResume: "+" again calling one view pager api " )

                    ticketdata = it.data[0]
                    val gson=Gson()


                    pagerAdapter.add(ServiceTicketFragment(ticketdata!!), "Service")
                    pagerAdapter.add(DetailsTicketFragment(ticketdata!!), "Details")
                    pagerAdapter.add(ProductTicketFragment(ticketdata!!), "Logs")
                    if (ticketdata!!.Type == "Site Survey"){
//                        Prefs.putString(Global.ITEM_FLAG, "Site Survey")
                        pagerAdapter.add(TicketWiseItemListFragment(ticketdata!!), "Report")
                    }else{
                        Prefs.putString(Global.ITEM_FLAG, "")
                        pagerAdapter.add(TicketWiseItemListFragment(ticketdata!!), "Items")
                    }

                    binding.viewpager.adapter = pagerAdapter

                    binding.tabLayout.setupWithViewPager(binding.viewpager)

                } else {
                    Log.e(TAG, "responseError: ${it.message}")
                    Global.warningmessagetoast(this, it.message)
                }


                //todo call open menu items popup--
                binding.openThreeDotMenu.setOnClickListener {
                    openThreeDotOptionMenu(binding.openThreeDotMenu)

                }


            }
        ))
    }


    private fun showDialogTicketTypeAssignerFragment(type: String) {
        val dialogFragment = DialogTicketTypeAssignerFragment()
        val dataBundle = Bundle()
        dataBundle.putString("key", type)
        dataBundle.putString("id", ticketdata!!.id.toString())

        dataBundle.putString("ticketType", ticketdata!!.Type)
        dialogFragment.arguments = dataBundle
        dialogFragment.show(supportFragmentManager, "DialogTicketTypeAssignerFragment")
    }

    var type = ""


    lateinit var addOnTime : MenuItem
    //todo comment create option menu---

    fun openThreeDotOptionMenu(view: View){
        var popupMenu : PopupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.ticket_detail_menu, popupMenu.menu)
        popupMenu.menu.findItem(R.id.breakDown).isVisible = false
        popupMenu.menu.findItem(R.id.technical_assign).isVisible = false
        popupMenu.menu.findItem(R.id.repair_assign).isVisible = false
        popupMenu.menu.findItem(R.id.escallation).isVisible = true

        addOnTime = popupMenu.menu.findItem(R.id.addOnTime)

        if (ticketdata != null){
            when (ticketdata!!.TicketStatus) {
                "Accepted" -> {
                    if (ticketdata!!.AddOnDuration != ""){
                        addOnTime.isVisible = false
                    }
                    else if (ticketdata!!.AddOnDuration == ""  || ticketdata!!.Status == "Resolved" && Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true)){
                        addOnTime.isVisible = true
                    }
                }
            }
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    onBackPressed()
                    true
                }
                R.id.action_settings -> {

                    if (Global.TicketAuthentication || Prefs.getString(Global.Employee_role, "").equals(Global.ADMIN_STRING, ignoreCase = true)) {
                        Global.UserNumber = "${ticketdata!!.CountryCode}${ticketdata!!.ContactPhone}"
                        Global.AlternateUserNumber = "${ticketdata!!.CountryCode1}${ticketdata!!.AlternatePhone}"

                        when (ticketdata!!.TicketStatus) {
                            "Accepted" -> {

                                if (ticketdata!!.Status == "Closed") {
                                    Global.warningmessagetoast(this@TicketDetailsActivity, "Your Ticket is Closed")
                                }
                                else if (ticketdata!!.is_SiteReported == false){
                                    Global.warningmessagetoast(this@TicketDetailsActivity, "Create Report Before Resolve the Ticket.")
                                }
                                else if (ticketdata!!.Status != "Resolved") {
                                    if (Global.TicketStartDate.isNotEmpty() && Global.TicketEndDate.isNotEmpty()) {
//                                    checkstoragepermission() //todo comment bt chancahl---

                                        val intent = Intent(this@TicketDetailsActivity, SignedConfirmActivity::class.java)
                                        intent.putExtra("ID", ticketdata!!.id)
                                        startActivity(intent)
                                    } else {
                                        Global.warningdialogbox(
                                            this,
                                            "End date or start date not find"
                                        )
                                    }
                                } else {
                                    Global.warningdialogbox(this, "Your ticket has been resolved")

                                }

                            }
                            "Pending" -> {
                                Global.warningdialogbox(
                                    this,
                                    "Your ticket is in pending state,Kindly accept it"
                                )
                            }
                            "Rejected" -> {
                                Global.warningdialogbox(this, "Your ticket will be rejected")
                            }
                        }
                    } else {
                        Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                    }
                    true
                }


                R.id.escallation ->{
                    showEscallationPopup()
                    true
                }


                R.id.breakDown -> {

                    type = "Break Down"
                    when (ticketdata!!.TicketStatus) {

                        "Accepted" -> {
                            showDialogTicketTypeAssignerFragment(type)
                        }
                        "Pending" -> {
                            Global.warningdialogbox(
                                this,
                                "Your ticket is in pending state,Kindly accept it"
                            )
                        }
                        "Rejected" -> {
                            Global.warningdialogbox(this, "Your ticket will be rejected")
                        }

                    }

                    true
                }


                R.id.technical_assign -> {

                    type = "Technical Assign"
                    when (ticketdata!!.TicketStatus) {

                        "Accepted" -> {
                            showDialogTicketTypeAssignerFragment(type)
                        }
                        "Pending" -> {
                            Global.warningdialogbox(
                                this,
                                "Your ticket is in pending state,Kindly accept it"
                            )
                        }
                        "Rejected" -> {
                            Global.warningdialogbox(this, "Your ticket will be rejected")
                        }

                    }
                    // }

                    true
                }


                R.id.repair_assign -> {

                    when (ticketdata!!.TicketStatus) {

                        "Accepted" -> {
                            showDialogTicketTypeAssignerFragment(type)
                        }
                        "Pending" -> {
                            Global.warningdialogbox(
                                this,
                                "Your ticket is in pending state,Kindly accept it"
                            )
                        }
                        "Rejected" -> {
                            Global.warningdialogbox(this, "Your ticket will be rejected")
                        }

                        // }
                    }

                    true
                }


                R.id.edit -> {
                    Log.e(TAG, "onOptionsItemSelected: ${Prefs.getString(Global.Employee_role)}")
                    if ((Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true))){
                        when (ticketdata!!.TicketStatus) {
                            "Accepted" -> {
                                if (ticketdata!!.Status == "Closed"){
                                    Global.warningdialogbox(this, "Your Ticket is Closed")
                                }

                                else if (ticketdata!!.Status != "Resolved") {
                                    Global.warningdialogbox(this, "You do no have Authentication to work on ticket")
                                }

                                else if (ticketdata!!.Status == "Resolved") {
                                    Global.warningdialogbox(this, "You are not authorized to update report once ticket is resolved !")
                                }
                            }
                        }

                    }
                    else if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) || Prefs.getString(Global.Employee_role).equals("Sr. Executive", ignoreCase = true)
                                || Prefs.getString(Global.Employee_role).equals("Service Head", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true)) // && Global.TicketAuthentication
                    ) {
                        when (ticketdata!!.TicketStatus) {
                            "Accepted" -> {
                                if (ticketdata!!.Status == "Closed"){
                                    Global.warningdialogbox(this, "Your Ticket is Closed")
                                }
                                else if (ticketdata!!.Status != "Resolved") {

                                    val intent : Intent = Intent(this, EditTicketActivity::class.java)
                                    intent.putExtra(Global.TicketData, ticketdata )
                                    startActivity(intent)
                                }

                                else if (ticketdata!!.Status == "Resolved") {
                                    if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
                                        val intent : Intent = Intent(this, EditTicketActivity::class.java)
                                        intent.putExtra(Global.TicketData, ticketdata )
                                        startActivity(intent)
                                    }else {
                                        Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                                    }
                                }else{
                                    Global.warningdialogbox(this, "Your Ticket is Resolved")
                                }

                            }
                            "Pending" -> {
                                Global.warningdialogbox(
                                    this,
                                    "Your ticket is in pending state,Kindly accept it"
                                )
                            }
                            "Rejected" -> {
                                Global.warningdialogbox(this, "Your ticket will be rejected")
                            }
                            "" -> {
                                val intent : Intent = Intent(this, EditTicketActivity::class.java)
                                intent.putExtra(Global.TicketData, ticketdata as Parcelable)
                                startActivity(intent)
                            }

                        }
                    } else {
                        Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                    }

                    true
                }


                R.id.closedTicket ->{
                    if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))) {
                        when (ticketdata!!.TicketStatus) {
                            "Accepted" -> {
                                if (ticketdata!!.Status == "Closed"){
                                    Global.warningdialogbox(this, "Your Ticket is Closed")
                                }
                                else if (ticketdata!!.Status != "Resolved") {
                                    Global.warningdialogbox(this, "Not in Closed State")
                                }
                                else if (ticketdata!!.Status == "Resolved"){
                                    openClosedTicketPopup()
                                }else{
                                    Global.warningdialogbox(this, "Your Ticket is Resolved")
                                }
                            }
                            "Pending" -> {
                                Global.warningdialogbox(this, "Your ticket is in pending state,Kindly accept it")
                            }
                            "Rejected" -> {
                                Global.warningdialogbox(this, "Your ticket will be rejected")
                            }

                        }
                    } else {
                        Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                    }

                    true
                }


                R.id.addOnTime ->{
                    if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true))) {
                        when (ticketdata!!.TicketStatus) {
                            "Accepted" -> {
                                if (ticketdata!!.Status == "Closed"){
                                    Global.warningdialogbox(this, "Your Ticket is Closed")
                                }
                                else if (ticketdata!!.Status != "Resolved") {
                                    Global.warningdialogbox(this, "Not in Add On Time State")
                                }
                                else if (ticketdata!!.Status == "Resolved"){
                                    openAddOnTimeDialog()
                                }else{
                                    Global.warningdialogbox(this, "Your Ticket is Resolved")
                                }
                            }
                            "Pending" -> {
                                Global.warningdialogbox(this, "Your ticket is in pending state,Kindly accept it")
                            }
                            "Rejected" -> {
                                Global.warningdialogbox(this, "Your ticket will be rejected")
                            }

                        }
                    } else {
                        Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                    }

                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

  /*  override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.ticket_detail_menu, menu)
        menu.findItem(R.id.breakDown).isVisible = false
        menu.findItem(R.id.technical_assign).isVisible = false
        menu.findItem(R.id.repair_assign).isVisible = false
        menu.findItem(R.id.escallation).isVisible = true

        if (ticketdata != null){
            when (ticketdata!!.TicketStatus) {
                "Accepted" -> {
                    if (ticketdata!!.Status == "Resolved" && Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true)){
                        menu.findItem(R.id.addOnTime).isVisible = true
                    }
                }
            }
        }

        return true
    }
*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }

       /*     R.id.action_settings -> {

                if (Global.TicketAuthentication || Prefs.getString(Global.Employee_role, "").equals(Global.ADMIN_STRING, ignoreCase = true)) {
                    Global.UserNumber = "${ticketdata!!.CountryCode}${ticketdata!!.ContactPhone}"
                    Global.AlternateUserNumber = "${ticketdata!!.CountryCode1}${ticketdata!!.AlternatePhone}"

                    when (ticketdata!!.TicketStatus) {
                        "Accepted" -> {

                            if (ticketdata!!.Status == "Closed") {
                                Global.warningmessagetoast(this@TicketDetailsActivity, "Your Ticket is Closed")
                            }
                            else if (ticketdata!!.is_SiteReported == false){
                                Global.warningmessagetoast(this@TicketDetailsActivity, "Create Report Before Resolve the Ticket.")
                            }
                            else if (ticketdata!!.Status != "Resolved") {
                                if (Global.TicketStartDate.isNotEmpty() && Global.TicketEndDate.isNotEmpty()) {
//                                    checkstoragepermission() //todo comment bt chancahl---

                                    val intent = Intent(this@TicketDetailsActivity, SignedConfirmActivity::class.java)
                                    intent.putExtra("ID", ticketdata!!.id)
                                    startActivity(intent)
                                } else {
                                    Global.warningdialogbox(
                                        this,
                                        "End date or start date not find"
                                    )
                                }
                            } else {
                                Global.warningdialogbox(this, "Your ticket has been resolved")

                            }

                        }
                        "Pending" -> {
                            Global.warningdialogbox(
                                this,
                                "Your ticket is in pending state,Kindly accept it"
                            )
                        }
                        "Rejected" -> {
                            Global.warningdialogbox(this, "Your ticket will be rejected")
                        }
                    }
                } else {
                    Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                }

            }


            R.id.escallation ->{
                showEscallationPopup()
            }

            R.id.breakDown -> {

                type = "Break Down"
                when (ticketdata!!.TicketStatus) {

                    "Accepted" -> {
                        showDialogTicketTypeAssignerFragment(type)
                    }
                    "Pending" -> {
                        Global.warningdialogbox(
                            this,
                            "Your ticket is in pending state,Kindly accept it"
                        )
                    }
                    "Rejected" -> {
                        Global.warningdialogbox(this, "Your ticket will be rejected")
                    }

                }


            }


            R.id.technical_assign -> {

                type = "Technical Assign"
                when (ticketdata!!.TicketStatus) {

                    "Accepted" -> {
                        showDialogTicketTypeAssignerFragment(type)
                    }
                    "Pending" -> {
                        Global.warningdialogbox(
                            this,
                            "Your ticket is in pending state,Kindly accept it"
                        )
                    }
                    "Rejected" -> {
                        Global.warningdialogbox(this, "Your ticket will be rejected")
                    }

                }
                // }
            }


            R.id.repair_assign -> {

                when (ticketdata!!.TicketStatus) {

                    "Accepted" -> {
                        showDialogTicketTypeAssignerFragment(type)
                    }
                    "Pending" -> {
                        Global.warningdialogbox(
                            this,
                            "Your ticket is in pending state,Kindly accept it"
                        )
                    }
                    "Rejected" -> {
                        Global.warningdialogbox(this, "Your ticket will be rejected")
                    }

                    // }
                }
            }


            R.id.edit -> {
                Log.e(TAG, "onOptionsItemSelected: ${Prefs.getString(Global.Employee_role)}")
                if ((Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true))){
                    when (ticketdata!!.TicketStatus) {
                        "Accepted" -> {
                            if (ticketdata!!.Status == "Closed"){
                                Global.warningdialogbox(this, "Your Ticket is Closed")
                            }

                            else if (ticketdata!!.Status != "Resolved") {
                                Global.warningdialogbox(this, "You do no have Authentication to work on ticket")
                            }

                            else if (ticketdata!!.Status == "Resolved") {
                                Global.warningdialogbox(this, "You are not authorized to update report once ticket is resolved !")
                            }
                        }
                    }

                }
                else if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) || Prefs.getString(Global.Employee_role).equals("Sr. Executive", ignoreCase = true)
                            || Prefs.getString(Global.Employee_role).equals("Service Head", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true)) // && Global.TicketAuthentication
                ) {
                    when (ticketdata!!.TicketStatus) {
                        "Accepted" -> {
                            if (ticketdata!!.Status == "Closed"){
                                Global.warningdialogbox(this, "Your Ticket is Closed")
                            }
                            else if (ticketdata!!.Status != "Resolved") {

                                val intent : Intent = Intent(this, EditTicketActivity::class.java)
                                intent.putExtra(Global.TicketData, ticketdata )
                                startActivity(intent)
                            }

                           else if (ticketdata!!.Status == "Resolved") {
                                if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))){
                                    val intent : Intent = Intent(this, EditTicketActivity::class.java)
                                    intent.putExtra(Global.TicketData, ticketdata )
                                    startActivity(intent)
                                }else {
                                    Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                                }
                            }else{
                                Global.warningdialogbox(this, "Your Ticket is Resolved")
                            }

                        }
                        "Pending" -> {
                            Global.warningdialogbox(
                                this,
                                "Your ticket is in pending state,Kindly accept it"
                            )
                        }
                        "Rejected" -> {
                            Global.warningdialogbox(this, "Your ticket will be rejected")
                        }
                        "" -> {
                            val intent : Intent = Intent(this, EditTicketActivity::class.java)
                            intent.putExtra(Global.TicketData, ticketdata as Parcelable)
                            startActivity(intent)
                        }

                    }
                } else {
                    Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                }
            }


            R.id.closedTicket ->{
                if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Zonal Manager", ignoreCase = true))) {
                    when (ticketdata!!.TicketStatus) {
                        "Accepted" -> {
                            if (ticketdata!!.Status == "Closed"){
                                Global.warningdialogbox(this, "Your Ticket is Closed")
                            }
                            else if (ticketdata!!.Status != "Resolved") {
                                Global.warningdialogbox(this, "Not in Closed State")
                            }
                            else if (ticketdata!!.Status == "Resolved"){
                                openClosedTicketPopup()
                            }else{
                                Global.warningdialogbox(this, "Your Ticket is Resolved")
                            }
                        }
                        "Pending" -> {
                            Global.warningdialogbox(this, "Your ticket is in pending state,Kindly accept it")
                        }
                        "Rejected" -> {
                            Global.warningdialogbox(this, "Your ticket will be rejected")
                        }

                    }
                } else {
                    Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                }
            }


            R.id.addOnTime ->{
                if ((Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) ||  Prefs.getString(Global.Employee_role).equals("Service Engineer", ignoreCase = true))) {
                    when (ticketdata!!.TicketStatus) {
                        "Accepted" -> {
                            if (ticketdata!!.Status == "Closed"){
                                Global.warningdialogbox(this, "Your Ticket is Closed")
                            }
                            else if (ticketdata!!.Status != "Resolved") {
                                Global.warningdialogbox(this, "Not in Add On Time State")
                            }
                            else if (ticketdata!!.Status == "Resolved"){
                                openAddOnTimeDialog()
                            }else{
                                Global.warningdialogbox(this, "Your Ticket is Resolved")
                            }
                        }
                        "Pending" -> {
                            Global.warningdialogbox(this, "Your ticket is in pending state,Kindly accept it")
                        }
                        "Rejected" -> {
                            Global.warningdialogbox(this, "Your ticket will be rejected")
                        }

                    }
                } else {
                    Global.warningdialogbox(this, "You do no have authentication to work on ticket")
                }
            }*/

        }
        return true
    }


    //todo open add remark dialog for add On time ticket---
    private fun openAddOnTimeDialog(){
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view = LayoutInflater.from(this).inflate(R.layout.add_on_time_layout, null)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        builder.window?.setGravity(Gravity.CENTER)
        builder.setView(view)

        val edttext = view.findViewById<EditText>(R.id.edttext)
        val acHour = view.findViewById<AutoCompleteTextView>(R.id.acHour)
        val acMinutes = view.findViewById<AutoCompleteTextView>(R.id.acMinutes)
        val acSeconds = view.findViewById<AutoCompleteTextView>(R.id.acSeconds)
        val done = view.findViewById<Button>(R.id.done)
        val try_again = view.findViewById<Button>(R.id.try_again)


        var hourTime = ""
        val hourADapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.hourTimeList)
        acHour.setAdapter(hourADapter)

        acHour.setOnItemClickListener { adapterView, view, i, l ->
            if (Global.hourTimeList.isNotEmpty()) {
                hourTime = Global.hourTimeList[i]
                acHour.setText(Global.hourTimeList[i])

                val hourADapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.hourTimeList)
                acHour.setAdapter(hourADapter)
            }
        }


        var minutesTime = ""
        val minADapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.minTimeList)
        acMinutes.setAdapter(minADapter)


        acMinutes.setOnItemClickListener { adapterView, view, i, l ->
            if (Global.minTimeList.isNotEmpty()) {
                minutesTime = Global.minTimeList[i]
                acMinutes.setText(Global.minTimeList[i])

                val minADapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.minTimeList)
                acMinutes.setAdapter(minADapter)
            }
        }


        var secondsTime = ""
        val secADapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.minTimeList)
        acSeconds.setAdapter(secADapter)

        acSeconds.setOnItemClickListener { adapterView, view, i, l ->
            if (Global.minTimeList.isNotEmpty()) {
                secondsTime = Global.minTimeList[i]
                acSeconds.setText(Global.minTimeList[i])

                val secADapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Global.minTimeList)
                acSeconds.setAdapter(secADapter)
            }
        }

        try_again.setOnClickListener {
            builder.cancel()
        }


        done.setOnClickListener {
            var addOnDuration = hourTime+":"+minutesTime+":"+secondsTime

            var jsonObject = JsonObject()
            jsonObject.addProperty("id", ticketdata!!.id)
            jsonObject.addProperty("AddOnDuration", addOnDuration)
            jsonObject.addProperty("AddOnRemark", edttext.text.toString().trim())

            val call: Call<ComplainDetailResponseModel> = ApiClient().service.createAddOnTimeApi(jsonObject)
            call.enqueue(object : Callback<ComplainDetailResponseModel?> {
                override fun onResponse(call: Call<ComplainDetailResponseModel?>, response: Response<ComplainDetailResponseModel?>) {
                    if (response.code() == 200){
                        if (response.body()!!.status == 200) {
                            Log.e("msz", response.body().toString())
                            Global.successmessagetoast(this@TicketDetailsActivity, response.body()!!.message!!)
                            builder.dismiss()
                        }else{
                            Global.errormessagetoast(this@TicketDetailsActivity, response.body()!!.message!!)
                        }
                    }else{
                        Global.errormessagetoast(this@TicketDetailsActivity, response.message())
                    }


                }

                override fun onFailure(call: Call<ComplainDetailResponseModel?>, t: Throwable) {
                    Global.errormessagetoast(this@TicketDetailsActivity, t.message.toString())
                }
            })
        }

        builder.setCancelable(true)
        builder.show()
    }


    //todo open add remark dialog for closed ticket---
    private fun openClosedTicketPopup(){
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view = LayoutInflater.from(this).inflate(R.layout.comment_dialog, null)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        builder.window?.setGravity(Gravity.CENTER)
        builder.setView(view)

        val edttext = view.findViewById<EditText>(R.id.edttext)
        val done = view.findViewById<Button>(R.id.done)
        val try_again = view.findViewById<Button>(R.id.try_again)

        try_again.setOnClickListener {
            builder.cancel()
        }

        done.setOnClickListener {
            var jsonObject = JsonObject()
            jsonObject.addProperty("TicketId", ticketdata!!.id)
            jsonObject.addProperty("EmployeeId", Prefs.getString(Global.Employee_Code, ""))
            jsonObject.addProperty("TicketClosedRemark", edttext.text.toString().trim())

            val call: Call<LogInResponse> = ApiClient().service.closedTicketApi(jsonObject)
            call.enqueue(object : Callback<LogInResponse?> {
                override fun onResponse(call: Call<LogInResponse?>, response: Response<LogInResponse?>) {
                    if (response.code() == 200){
                        if (response.body()!!.getStatus() == 200) {
                            Log.e("msz", response.body().toString())
                            Global.successmessagetoast(this@TicketDetailsActivity, response.body()!!.getMessage()!!)
                            builder.dismiss()
                        }else{
                            Global.errormessagetoast(this@TicketDetailsActivity, response.body()!!.getMessage()!!)
                        }
                    }else{
                        Global.errormessagetoast(this@TicketDetailsActivity, response.message())
                    }


                }

                override fun onFailure(call: Call<LogInResponse?>, t: Throwable) {
                    Global.errormessagetoast(this@TicketDetailsActivity, t.message.toString())
                }
            })
        }

        builder.setCancelable(true)
        builder.show()
    }



    //todo disable button click popup.
    @SuppressLint("MissingInflatedId")
    private fun showEscallationPopup() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view = LayoutInflater.from(this).inflate(R.layout.custom_popup_alert, null)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        builder.window?.setGravity(Gravity.CENTER)
        builder.setView(view)

        //todo set ui..
        val escallation1_btn = view.findViewById<MaterialButton>(R.id.escallation1_btn)
        val escallation2_btn = view.findViewById<MaterialButton>(R.id.escallation2_btn)
        val escallation3_btn = view.findViewById<MaterialButton>(R.id.escallation3_btn)
        val escallation_one_details_layout = view.findViewById<LinearLayout>(R.id.escallation_one_details_layout)
        val tv_name_escOne = view.findViewById<TextView>(R.id.tv_name_escOne)
        val tv_phone_escOne = view.findViewById<TextView>(R.id.tv_phone_escOne)
        val tv_email_escOne = view.findViewById<TextView>(R.id.tv_email_escOne)
        val escallation_two_details_layout = view.findViewById<LinearLayout>(R.id.escallation_two_details_layout)
        val tv_name_escTwo = view.findViewById<TextView>(R.id.tv_name_escTwo)
        val tv_phone_escTwo = view.findViewById<TextView>(R.id.tv_phone_escTwo)
        val tv_email_escTwo = view.findViewById<TextView>(R.id.tv_email_escTwo)
        val escallation_three_details_layout = view.findViewById<LinearLayout>(R.id.escallation_three_details_layout)
        val tv_name_escThree = view.findViewById<TextView>(R.id.tv_name_escThree)
        val tv_phone_escThree = view.findViewById<TextView>(R.id.tv_phone_escThree)
        val tv_email_escThree = view.findViewById<TextView>(R.id.tv_email_escThree)
        val iv_cancel = view.findViewById<ImageView>(R.id.iv_cancel)
        val nodatafound = view.findViewById<ImageView>(R.id.nodatafound)
        val rvEscallation = view.findViewById<RecyclerView>(R.id.rvEscallation)


        //todo calling escallation aoi here---
        callEscallationListApi(rvEscallation, nodatafound)

        escallation1_btn.setOnClickListener {
            if (escallation_one_details_layout.visibility == View.VISIBLE) {
                escallation_one_details_layout.visibility = View.GONE
            } else {
                escallation_one_details_layout.visibility = View.VISIBLE
            }
        }

        escallation2_btn.setOnClickListener {
            if (escallation_two_details_layout.visibility == View.VISIBLE) {
                escallation_two_details_layout.visibility = View.GONE
            } else {
                escallation_two_details_layout.visibility = View.VISIBLE
            }
        }

        iv_cancel.setOnClickListener {
            builder.cancel()
        }

        builder.setCancelable(true)
        builder.show()

    }


    private fun callEscallationListApi(rvEscallation: RecyclerView, nodatafound: ImageView) {

        var jsonObject = JsonObject()
        jsonObject.addProperty("ticketId", ticketdata!!.id)
        val call: Call<EscallationResponseModel> = ApiClient().service.getEscallationList(jsonObject)
        call.enqueue(object : Callback<EscallationResponseModel?> {
            override fun onResponse(call: Call<EscallationResponseModel?>, response: Response<EscallationResponseModel?>) {
                try {
                    if (response.isSuccessful){

                        if (response.body()!!.status == 200) {
                            Log.e(TAG, "subscribeToObserver: ")

                            if (response.body()!!.data.isNotEmpty()){
                                var dataList = response.body()!!.data
                                nodatafound.visibility = View.GONE
                                var linearLayoutManager = LinearLayoutManager(this@TicketDetailsActivity, LinearLayoutManager.VERTICAL, false)
                                var adapter = EscallationAdapter(dataList)
                                rvEscallation.layoutManager = linearLayoutManager
                                rvEscallation.adapter = adapter

                            }else{
                                nodatafound.visibility = View.VISIBLE
                                Global.warningmessagetoast(this@TicketDetailsActivity, "NO Data Found")
                            }

                        }else{
                            nodatafound.visibility = View.GONE
                            Log.e(TAG, "subscribeToObserverApiError: ${response.message()}")
                            Global.warningmessagetoast(this@TicketDetailsActivity, response.body()!!.message)
                        }
                    }
                }catch (e:Exception){
                    nodatafound.visibility = View.GONE
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<EscallationResponseModel?>, t: Throwable) {
                Global.errormessagetoast(this@TicketDetailsActivity, t.message.toString())
            }
        })
    }

    private fun checkstoragepermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(this@TicketDetailsActivity, SignedConfirmActivity::class.java)
                        intent.putExtra("ID", ticketdata!!.id)
                        startActivity(intent)
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
                Toast.makeText(this, "Error occurred! ", Toast.LENGTH_SHORT).show();
            }.onSameThread().check()


    }

    override fun onBackPressed() {
//
        if (supportFragmentManager.backStackEntryCount >= 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
        super.onBackPressed()
    }




}