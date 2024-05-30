package com.ahuja.sons.globals

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.ozcanalasalvar.library.view.datePicker.DatePicker
import com.ozcanalasalvar.library.view.popup.DatePickerPopup
import com.ahuja.sons.R
import com.ahuja.sons.apibody.BodySparePart
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.model.ComplainDetailResponseModel
import com.ahuja.sons.model.DocumentLine
import com.ahuja.sons.newapimodel.DataFromJsonCheckList
import com.ahuja.sons.newapimodel.SpareItemListApiModel
import com.ahuja.sons.receiver.DataEmployeeAllData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import taimoor.sultani.sweetalert2.Sweetalert
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object Global {


    const val LOGIN_ID: String = "_SESSION_MANAGER"
    const val FCM: String = "_FCM"
    val LogInUserName: String = "_LogInUserName"
    val OTP_VERIFY: String = "otp"
    val LogInPassword: String = "_LogInPassword"
    val RememberMe: String = "_RememberMe"
    val Employee_SalesEmpCode: String = "_Employee_SalesEmpCode"
    val ProductCategory: String = "_ProductCategory"
    val PartRequestData: String = "_PartRequestData"
    val Employee_Name: String = "_Employee_Name"
    val Employee_maILid: String = "_Employee_maILid"
    val Employee_Code: String = "_Employee_Code"
    val servcieID: String = "_servcieID"
    val Employee_role: String = "_Employee_role"
    val FirstName: String = "_FirstName"
    val _ReportingTO: String = "_ReportingTO"


    const val MyID = "_my_employee_id"
    var UserNumber: String = ""
    var AlternateUserNumber: String = ""
    val TicketFlowFrom: String = "TicketFlowFrom_"
    val AccountData: String = "AccountData_"
    val BPWiseticketlist: String = "BPWiseticketlist_"
    val TicketData: String = "TicketData_"
    var TicketAssigntoID: String = ""

    const val SpinnerBranchId = "_SpinnerBranchId"
    const val SpinnerAddressType = "_SpinnerAddressType"


    const val ADMIN_STRING: String = "admin"
    const val CustomerItemData = "_cus_itemData"
    const val PAGE_SIZE = 10
    const val INTENT_TICKET_ID = "id"
    const val INTENT_TICKET_STATUS = "status"
    const val INTENT_WHERE_STATUS = "where"

    var TicketAuthentication = false
    var TicketStartDate = ""
    var TicketEndDate = ""

    const val FINAL_SITE_SURVEY_SUBTYPE = "Final Site Survey"
    const val DRAWING_APPROVAL_SUBTYPE = "Drawing Approval"
    const val FINAL_ORDER_SPECIFICATION_SUBTYPE = "Final Order Specifications"
    const val PURCHASE_REQUEST_SUBTYPE = "Purchase Request"
    const val MATERIAL_DISPATCHED_SUBTYPE = "Material Dispatched"
    const val SITE_READY_EVALUATION_SUBTYPE = "Site Readiness Evaluation"
    const val MATERIAL_DELIVERED_SUBTYPE = "Material Delivered"
    const val INSTALLATION_INITIATION_SUBTYPE = "Installation Initiation"
    const val QUALITY_INSPECTION_1_SUBTYPE = "Quality Inspection 1"
    const val TESTING_AND_COMMISIONING_SUBTYPE = "Testing and Commissioning"
    const val QUALITY_INSPECTION_2_SUBTYPE = "Quality Inspection 2"
    const val THIRD_PARTY_INSPECTION_SUBTYPE = "Third Party Inspection"
    const val MAINTENANCE_INSPECTION_SUBTYPE = "Maintenance Inspection"
    const val HANDOVER_TO_CLIENT_SUBTYPE = "Handover to Client"
    const val TRANSFER_TO_MAINTENANCE_SUBTYPE = "Transfer to Maintenance"
    const val OTHER = "Other"
    const val ITEM_FLAG = "_ITEM_FLAG"
    const val CANVAS_IMAGE = "_CANVAS_IMAGE"


    //    const val BASE_URL = "http://103.234.187.197:8104/"

//    const val BASE_URL = "http://192.168.29.105:8009/"
//    const val BASE_URL = "http://192.168.29.106:8009/"


    /****TEST URL**/
   /* const val BASE_URL = "http://103.234.187.197:8009/"
    const val Image_URL = "http://103.234.187.197:8009"


    const val REPORT_PDF_URL = "http://103.234.187.197:4210/assets/html/CompanyInvoice.html?id="//"http://103.234.187.197:4233/assets/html/report.html?id="

    const val SERVICING_PDF_URL = "http://103.234.187.197:4210/assets/html/ServicePrevent.html?id="

    const val SITE_SERVEY_PDF_URL = "http://103.234.187.197:4210/assets/html/SiteserveyReport.html?id="

    //todo ticket type items pdf---
    const val INSTALLATION_TYPE_PDF_URL = "http://103.234.187.197:4210/assets/html/CompanyInvoice.html?TicketId="

    const val SITE_SURVEY_TYPE_PDF_URL = "http://103.234.187.197:4210/assets/html/SiteserveyReport.html?TicketId="

    const val MAINTAINANCE_TYPE_PDF_URL = "http://103.234.187.197:4210/assets/html/ServicePrevent.html?TicketId="*/


    /***LIVE URL***/
    const val BASE_URL = "http://103.234.187.197:8107/"
    const val Image_URL = "http://103.234.187.197:8107"

     //todo LIVE PDF URl---
     const val REPORT_PDF_URL = "http://waesupport.bridgexd.com/assets/html/CompanyInvoice.html?id="//"http://103.234.187.197:4233/assets/html/report.html?id="

     const val SERVICING_PDF_URL = "http://waesupport.bridgexd.com/assets/html/ServicePrevent.html?id="

     const val SITE_SERVEY_PDF_URL = "http://waesupport.bridgexd.com/assets/html/SiteserveyReport.html?id="

     //todo ticket type items pdf---
     const val INSTALLATION_TYPE_PDF_URL = "http://waesupport.bridgexd.com/assets/html/CompanyInvoice.html?TicketId="

     const val MAINTAINANCE_TYPE_PDF_URL = "http://waesupport.bridgexd.com/assets/html/ServicePrevent.html?TicketId="

     const val SITE_SURVEY_TYPE_PDF_URL = "http://waesupport.bridgexd.com/assets/html/SiteserveyReport.html?TicketId="


    val cartList = ArrayList<DocumentLine?>()
    val taglist = arrayListOf<String>()

    val mArrayUriList = java.util.ArrayList<Uri>()

    val listOfCheckList = arrayListOf<DataFromJsonCheckList>()

    val scopeWorkList = arrayOf(
        "Breakdown",
        "De-Installation",
        "Extra Work",
        "Installation",
        "Packaging",
        "Part change",
        "Pick up",
        "Preventive Maintenance",
        "Re-Installation",
        "Re-Visit Required",
        "Servicing",
        "Shifting",
        "Site Survey",
        "Water Testing",
        "System Checking",
        "Other",
        "Gas Reflling"
    )

    val reqestTypeList = arrayOf(
        "Anthracite Filter Check & Re-Filling",
        "Antiscalent Filter Check & Re-Filling",
        "Caustic Soda Filter Check & Re-Filling",
        "Cold Temperature Adjustment",
        "Compressure Failure",
        "Cord wire brust",
        "De-Installation",
        "Dirty water is coming",
        "Dust Problem",
        "External Housing Broken",
        "Filter Broken",
        "Filter Chocked",
        "Particles in RO water",
        "Fortnightly Servicing",
        "Funnel Broken",
        "Funnel Nozzle Broken",
        "Funnel problem",
        "Fuse Bum",
        "Gas Re-Filling",
        "Hot & Cold Thermostat off",
        "Hot Temperature Adjustment",
        "Hot water coming from both taps",
        "Hot water is not coming",
        "Installation",
        "L- Clamp Broken Leakage Problem",
        "Monthly Servicing",
        "Out Of Service Period",
        "Packaging",
        "Pick up",
        "Preventive Maintenance",
        "Quartely PM",
        "Quartely PM",
        "Re installation",
        "Re-Visit Required",
        "RO pipe broken/burst",
        "RO is not working",
        "RO Pipe Broken",
        "Servicing",
        "Shifting",
        "ShortCrcut Problem",
        "Site Survey",
        "Smell Problem",
        "Sound Problem",
        "System Checking",
        "Tap Broken(Single)",
        "Taps Broken (Both)",
        "Taps handover to client",
        "Taste Problem",
        "Water Flow is less",
        "Water is leaking",
        "Water is not coming at all",
        "Water is not coming from COLD tap",
        "Water is not coming from HOT tap",
        "Water Leakage",
        "Water is not getting cold Water is not getting hot",
        "Water Testing",
        "Oiner",
        "Dernsta laton-Packaoing"
    )

    val caseOrginList = arrayOf("By Call", "By Mail", "By Client Request", "By Internal Mail Request")

    val zoneList_gl = arrayOf("North", "East", "West", "South")

    val priorityList_gl = arrayOf("Medium", "Low", "High")

    val modeOfCommunication_list = arrayOf("Call", "Whatsapp", "SMS", "E-Mail")

    val contractTypeList_gl = arrayOf("Extended Warranty", "CMC", "AMC")

    val frequencyList_gl = arrayOf("Yearly", "Half Yearly", "Quarterly", "Monthly")

    val statusList_gl = arrayOf("Active", "Inactive")

    val ticketStatusList_gl = arrayOf("Pending", "Resolved", "In Progress", "Reject", "Closed")

    val modelList_gl = arrayOf("RO", "UF", "UV", "Dispensor")

    val productTypeList_gl = arrayOf("POU-Water Purifier", "Drinking Water Fountains", "RO Plant with Chiller", "Dispenser")


    val hourTimeList = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24")

    val minTimeList = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26",
    "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60")


    open fun isvalidateemail(email_value: TextInputEditText): Boolean {
        val checkEmail = email_value.text.toString()
        val hasSpecialEmail = Patterns.EMAIL_ADDRESS.matcher(checkEmail).matches()
        if (!hasSpecialEmail) {
            email_value.error = "This E-Mail address is not valid"
            return true
        }
        return false
    }

    fun hideKeybaord(v: View, context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
        v.clearFocus()

    }


     open fun convertListToJson(personList: List<BodySparePart.SparePart>): String {
        return Json.encodeToString(personList)
    }


    fun convert_yyyy_mm_dd_to_dd_mm_yyyy(str: String?): String? {
        var convertedDate = ""
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputDateFormat = SimpleDateFormat("dd-MM-yyyy")
        try {
            val date = inputDateFormat.parse(str)
            convertedDate = outputDateFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertedDate
    }

    fun enableAllCalenderDateSelect(context: Context, textView: EditText) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                try {
                    val strDate = dateFormatter.parse(selectedDate)
                    textView.setText(dateFormatter.format(strDate))
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            },
            mYear,
            mMonth,
            mDay
        )

        datePickerDialog.datePicker // setMinDate(System.currentTimeMillis() - 1000)
        datePickerDialog.setMessage(textView.hint.toString())
        datePickerDialog.show()
    }


    open fun convert_dd_MM_yyyy_into_yyyy_MM_dd(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }


    open fun convertTimestampToCustomFormat(timestamp: String): String {
        var convertedDate = ""
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC") // Set the time zone to UTC

        val customFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        try {
            val date = isoFormatter.parse(timestamp)
            convertedDate = customFormatter.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return convertedDate
    }


    open fun formatAs6DigitNumber(inputNumber: Int): String {
        return String.format("%06d", inputNumber)
    }


    fun getTimeStamp(): String {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        return ts
    }

    fun convertTimestamptoDate(s: String): String {
        try {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            val msz = "No date found"
            return msz
        }
    }

    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    fun setUPLoadingDialog() {

    }



    open fun countNotification(notification: Int):String{

        /* val displayString = if (notification > 99) {
             "+$notification"
         } else {
             notification.toString()
         }*/
        val displayString = if (notification > 99) {
            "+99"
        } else {
            notification.toString()
        }

        return displayString
    }

    fun getCustomerPos(list: ArrayList<DataEmployeeAllData>, inputString: String): Int {
        val parts = inputString.split("(")
        println(parts[0].trim())
        var index = -1
        if (parts.isNotEmpty()) {
            for (sd in list) {
                if (sd.SalesEmployeeName.equals(parts[0].trim())) {
                    index = list.indexOf(sd)
                    break
                }
            }
        }
        return index
    }


    fun showLoadingDialog(context: Context) {


        builder = AlertDialog.Builder(context)
        builder!!.setView(R.layout.dialog_progress)
            .setCancelable(false)


        alertDialog = builder!!.create()
        alertDialog!!.show()
    }


    fun hideLoadingDialog(context: Context) {


        builder = AlertDialog.Builder(context)
        builder!!.setView(R.layout.dialog_progress)
            .setCancelable(false)


        alertDialog = builder!!.create()
        alertDialog!!.dismiss()
    }

    fun getJsonFromRawFile(context: Context) {
        context.resources.openRawResource(R.raw.departments)
            .bufferedReader().use { it.readText() }
    }

    @JvmStatic
    fun datetoSimpleString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    fun splitdatefromtimestamp(s: String): String {
        return try {
            val date = SimpleDateFormat("dd/MM/yy")
            val netDate = Date(s.toLong() * 1000)

            date.format(netDate)
        } catch (e: Exception) {
            val msz = "No date found"
            msz
        }
    }

    fun splittimefromtimestamp(s: String): String {
        try {
            val netDate = Date(s.toLong() * 1000)
            val time = SimpleDateFormat("hh:mm a")
            return time.format(netDate)
        } catch (e: Exception) {
            val msz = "No date found"
            return msz
        }
    }


    fun warningmessagetoast(context: Context, message: String) {
        MotionToast.createColorToast(
            context as Activity, "Warning", message, MotionToastStyle.WARNING,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
        )

    }


    fun findDifference(
        start_date: String?,
        end_date: String?
    ): Long {
        var time: Long = 0
        // SimpleDateFormat converts the
        // string format to date object
        val sdf = SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss", Locale.getDefault()
        )

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            val d1 = sdf.parse(start_date)
            val d2 = sdf.parse(end_date)

            // Calucalte time difference
            // in milliseconds
            val difference_In_Time = d2.time - d1.time

            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            val difference_In_Seconds = ((difference_In_Time
                    / 1000))
            /*val difference_In_Minutes = ((difference_In_Time
                    / (1000 * 60))
                    % 60)
            val difference_In_Hours = ((difference_In_Time
                    / (1000 * 60 * 60))
                    % 24)
            val difference_In_Years = (difference_In_Time
                    / (1000L * 60 * 60 * 24 * 365))
            val difference_In_Days = ((difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365)*/

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds
            Log.e("microsec", difference_In_Seconds.toString())
            time = difference_In_Seconds

            return time
        } // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    //yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    @Throws(ParseException::class)
    fun formatserverDateFromDateString(
        inputDate: String?
    ): String? {
        return if (inputDate!!.isNotEmpty()) {
            val mParsedDate: Date
            val mOutputDateString: String
            val mInputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val mOutputDateFormat = SimpleDateFormat("dd/MM/yyyy, hh:mm aa", Locale.getDefault())
            mParsedDate = mInputDateFormat.parse(inputDate)
            mOutputDateString = mOutputDateFormat.format(mParsedDate)
            mOutputDateString
        } else {
            ""
        }
    }


    @Throws(ParseException::class)
    fun formatserverDateFromWeirdDateFormat(
        inputDate: String?
    ): String? {
        return if (inputDate!!.isNotEmpty()) {
            val mParsedDate: Date
            val mOutputDateString: String
            val mInputDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val mOutputDateFormat = SimpleDateFormat("dd/MM/yyyy, hh:mm aa", Locale.getDefault())
            mParsedDate = mInputDateFormat.parse(inputDate)
            mOutputDateString = mOutputDateFormat.format(mParsedDate)
            mOutputDateString
        } else {
            ""
        }
    }


    @Throws(ParseException::class)
    fun formatserverDateFromDateStringtimer(
        inputDate: String?
    ): String? {

        val mParsedDate: Date
        val mOutputDateString: String
        val mInputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val mOutputDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault())
        mParsedDate = mInputDateFormat.parse(inputDate)
        mOutputDateString = mOutputDateFormat.format(mParsedDate)
        return mOutputDateString

    }


    @Throws(ParseException::class)
    fun formatDateFromDateString(
        inputDate: String?
    ): String? {
        return if (inputDate!!.isNotEmpty()) {
            val mParsedDate: Date
            val mOutputDateString: String
            val mInputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val mOutputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            mParsedDate = mInputDateFormat.parse(inputDate)
            mOutputDateString = mOutputDateFormat.format(mParsedDate)
            mOutputDateString
        } else {
            "N/A"
        }
    }


    @Throws(ParseException::class)
    fun formatddmmyyDateFromDateString(
        inputDate: String?
    ): String? {
        return if (inputDate!!.isNotEmpty()) {
            val mParsedDate: Date
            val mOutputDateString: String
            val mInputDateFormat = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
            val mOutputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            mParsedDate = mInputDateFormat.parse(inputDate)
            mOutputDateString = mOutputDateFormat.format(mParsedDate)
            mOutputDateString
        } else {
            "N/A"
        }
    }

    @Throws(ParseException::class)
    fun newserverformatDateFromDateString(
        inputDate: String?
    ): String? {
        return if (inputDate!!.isNotEmpty()) {
            val mParsedDate: Date
            val mOutputDateString: String
            val mInputDateFormat =
                SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val mOutputDateFormat = SimpleDateFormat("dd/MM/yyyy, hh:mm aa ", Locale.getDefault())
            mParsedDate = mInputDateFormat.parse(inputDate)
            mOutputDateString = mOutputDateFormat.format(mParsedDate)
            mOutputDateString
        } else {
            ""
        }
    }


    @Throws(ParseException::class)
    fun formattimeFromDateString(
        inputDate: String?
    ): String? {
        val mParsedDate: Date
        val mOutputDateString: String
        val mInputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val mOutputDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        mParsedDate = mInputDateFormat.parse(inputDate)
        mOutputDateString = mOutputDateFormat.format(mParsedDate)
        return mOutputDateString
    }


    fun getDateAndTimeFromWeirdFormat(inputDateString: String, context: Context): String {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        var formattedDate = ""

        try {
            val date = inputFormat.parse(inputDateString)
            formattedDate = outputFormat.format(date)

            // Now, `formattedDate` contains the readable date and time
            println(formattedDate) // Example output: 29 May 2023, 16:09:34
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        return formattedDate
    }

    fun errormessagetoast(context: Context, message: String) {
        MotionToast.createColorToast(
            context as Activity, "Error", message, MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
        )

    }

    fun successmessagetoast(context: Context, message: String) {
        MotionToast.createColorToast(
            context as Activity, "Success", message, MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
        )

    }

    fun infomessagetoast(context: Context, message: String) {
        MotionToast.createColorToast(
            context as Activity, "Info", message, MotionToastStyle.INFO,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
        )

    }

    fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


    fun selectDate(context: Context, editdate: EditText) {

        DatePickerPopup.Builder()
            .from(context)
            .offset(3)
            .pickerMode(DatePicker.MONTH_ON_FIRST)
            .textSize(19)
            .listener { dp, date, day, month, year ->
                editdate.setText(day.toString() + "-" + (month + 1).toString() + "-" + year.toString())

            }
            .build().show()

    }

    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(Date())
    }

    fun getTodayDateDashFormat(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(Date())
    }

    fun getTodayDateDashFormatReverse(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

    fun getTCurrentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun getfullformatCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    open fun getCurrentDateTimeFormatted(): String {
        val pattern = "dd-MM-yyyy 'T' hh:mm:ss a"
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val currentDateAndTime = Date()
        return dateFormat.format(currentDateAndTime)
    }


    fun warningdialogbox(context: Context, msz: String) {
        val pDialog = Sweetalert(context, Sweetalert.WARNING_TYPE)
        pDialog.titleText = "Sorry"
        pDialog.contentText = msz
        pDialog.setCanceledOnTouchOutside(false)
        pDialog.confirmText = "Ok"

        pDialog.showConfirmButton(true)
        pDialog.setConfirmClickListener { sDialog ->
            sDialog.cancel()

        }

        pDialog.show()
    }

    fun getCountrypos(countrylist: ArrayList<BPLID>, s: String): Int {
        var index = -1
        for (cd in countrylist) {
            if (cd.getName() == s) {
                index = countrylist.indexOf(cd)
                break
            }
        }
        return index
    }

    fun getItemSparePartyPos(countrylist: ArrayList<SpareItemListApiModel.DataXXX>, s: String): Int {
        var index = -1
        for (cd in countrylist) {
            if (cd.ItemDescription == s) {
                index = countrylist.indexOf(cd)
                break
            }
        }
        return index
    }

    fun getRequestType(countrylist: ArrayList<ComplainDetailResponseModel.DataX>, s: String): Int {
        var index = -1
        for (cd in countrylist) {
            if (cd.Name == s) {
                index = countrylist.indexOf(cd)
                break
            }
        }
        return index
    }



}