package com.ahuja.sons.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.FirebaseMessageReceiver
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.activity.AhujaSonsMainActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.LoginUiBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.globals.MyApp
import com.ahuja.sons.model.NewLoginData
import com.ahuja.sons.newapimodel.ResponseEmployeeAtLogin
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class LoginActivity : MainBaseActivity() {

    private lateinit var binding: LoginUiBinding

    private lateinit var viewModel: MainViewModel

    var hashMap = HashMap<String, Any>()
    val currentActivity = MyApp.currentApp
    val ticketdata = NewLoginData()
    var typo = ""
    var ticketId = ""
    var sos = ""
    var saleEmployeeCodeNotification = ""
    lateinit var dialog: Dialog
    val PERMISSION_REQUEST_CODE = 112


    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "getNotificationPermission: ")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // allow
                    Log.e(TAG, "onRequestPermissionsResult: " + " Allow Permission" )
                } else {
                    //deny
                    Log.e(TAG, "onRequestPermissionsResult: " + " Denied Permission" )
                }
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginUiBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setUpViewModel()
        dialog = Dialog(this)

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }

        val text = resources.openRawResource(R.raw.departments).bufferedReader().use { it.readText() }

        Log.e(TAG, "SHUB===>: $text")


        var token = FirebaseMessageReceiver.getToken(this)

        Log.e(TAG, "onCreate: $token")
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                Log.e(TAG, "retrieve token successful : $token")
                Prefs.putString(Global.FCM, token)
            } else {
                Log.e(TAG, "token should not be null...")
            }
        }.addOnFailureListener { e: Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                Log.e(
                    TAG,
                    "This is the token : " + task.result
                )
                Prefs.putString(Global.FCM, task.result)

            }


        if (Prefs.getString(Global.RememberMe, "CheckOut") == "CheckIn") {
            binding.loginUsername.setText(Prefs.getString(Global.LogInUserName))
            binding.loginPassword.setText(Prefs.getString(Global.LogInPassword))
            binding.rememberme.isChecked = true
        } else {
            binding.loginUsername.setText("")
            binding.loginPassword.setText("")
            binding.rememberme.isChecked = false
        }

        binding.rememberme.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                Prefs.putString(Global.RememberMe, "CheckIn")
            } else {
                Prefs.putString(Global.RememberMe, "CheckOut")
            }
        }



        binding.loginGo.setOnClickListener {
            if (validation(binding.loginUsername.text, binding.loginPassword.text)) {
                if (Global.checkForInternet(this)) {
                    binding.loader.visibility = View.VISIBLE
                    binding.loader.start()
                    val logInDetail = NewLoginData()
                    logInDetail.setUserName(binding.loginUsername.text.toString().trim())
                    logInDetail.setPassword(binding.loginPassword.text.toString().trim())
                    logInDetail.setFcm(token!!)

                    var pass = binding.loginPassword.text.toString()
                    var hashMap = HashMap<String, String>()
                    //  {"userName":"anjli@gmail.com","password":"123","FCM":""}

                    hashMap.put("userName", binding.loginUsername.text.toString().trim())
                    hashMap.put("password", pass)
                    hashMap.put("FCM", Prefs.getString(Global.FCM))
//                    hashMap.put("p_id", "")

                    viewModel.getLoginUser(hashMap)
                    // viewModel.loginTestUser(hashMap)

                    //  callloginAPI(binding.loginUsername.text.toString(),binding.loginPassword.text.toString())
                }
            }

        }

        subscribeToObserver()
        //  testObserver()
    }

    private fun showCustomDialog() {
        Log.e(TAG, "showCustomDialog: ")
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                Log.e(TAG, "run: $currentActivity.")
                Log.e(TAG, "run====>: ${MyApp.currentActivity}.")
                dialog.setContentView(R.layout.dialog_man_trap_rescue)
                // dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                // var tvAnnounce:TextView= dialog.findViewById<TextView>(R.id.tvAnnouncement)
                var bellIcon: ImageView = dialog.findViewById<ImageView>(R.id.imageBellIcon)
                var acceptButton: Button = dialog.findViewById<Button>(R.id.btnAccept)
                var rejectButton: Button = dialog.findViewById<Button>(R.id.btnReject)
                Glide.with(this@LoginActivity).asGif().load(R.raw.alertsos).into(bellIcon)
                var mediaPlayer: MediaPlayer? = null
                mediaPlayer = MediaPlayer.create(this@LoginActivity, R.raw.sos_tone)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()



                dialog.show()

                dialog.setOnDismissListener {
                    mediaPlayer?.stop()

                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                acceptButton.setOnClickListener {
                    ticketdata.setTicketid(ticketId)
                    ticketdata.setEmployeeId(saleEmployeeCodeNotification)
                    ticketdata.setTicketStatus("Accepted")
                    typo = "Accepted"

                    viewModel.acceptRejectTicket(ticketdata)
                    bindTicketStatusObserver()

                }
                rejectButton.setOnClickListener {
                    ticketdata.setTicketid(ticketId)
                    ticketdata.setEmployeeId(saleEmployeeCodeNotification)
                    ticketdata.setTicketStatus("Rejected")
                    typo = "Rejected"

                    viewModel.acceptRejectTicket(ticketdata)
                    bindTicketStatusObserver()

                }


            }
        })


    }

    //todo observer for ticket accept and reject..

    private fun bindTicketStatusObserver() {
        viewModel.ticketAcceptRejectResponse.observe(this, Event.EventObserver(
            onError = {
                dialog.dismiss()
                Global.warningmessagetoast(this, it)
                Log.e("ticketAcceptReject", it)
            }, onLoading = {

            },
            onSuccess = { response ->
                Log.e("response", response.getLogInDetail().toString())

                if (response.getStatus() == 200) {
                    dialog.dismiss()
                    if (typo.equals("Rejected", ignoreCase = true)) {
                        Global.successmessagetoast(this@LoginActivity, "Rejected Successfully")
                    } else {
                        Global.successmessagetoast(this@LoginActivity, "Accepted Successfully")
                    }
                } else {
                    dialog.dismiss()
                    Global.warningmessagetoast(this, response.getMessage().toString());
                }
            }
        ))
    }


    @Throws(IOException::class)
    fun AssetJSONFile(filename: String?, context: Context): String? {
        val manager: AssetManager = context.getAssets()
        val file = manager.open(filename!!)
        val formArray = ByteArray(file.available())
        file.read(formArray)
        file.close()
        return String(formArray)
    }


    fun readAssetFile(context: Context, fileName: String): String {
        val assetManager = context.assets
        val stringBuilder = StringBuilder()

        try {
            Log.e(TAG, "readAssetFileTRY===>: ")
            val inputStream = assetManager.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }

            reader.close()
        } catch (e: Exception) {
            Log.e(TAG, "readAssetFile: ${e.printStackTrace()}")
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }


    private fun validation(username: Editable?, pasword: Editable?): Boolean {
        if (username?.length == 0) {
            binding.textField.error = "Enter Username"
            Global.warningmessagetoast(this, "Enter Username")
            return false
        } else if (pasword?.length == 0) {
            binding.passwordField.error = "Enter Password"
            Global.warningmessagetoast(this, "Enter Password")
            return false
        }
        return true

    }


    private fun callloginAPI(username: String, password: String) {
        if (binding.rememberme.isChecked) {
            Prefs.putString(Global.LogInUserName, username)
            Prefs.putString(Global.LogInPassword, password)
        }
        //progressBar.setVisibility(View.VISIBLE)
        val logInDetail = NewLoginData()
        logInDetail.setUserName(username)
        logInDetail.setPassword(password)
        logInDetail.setFcm("")
        Log.e("data", logInDetail.toString())
        val call: Call<ResponseEmployeeAtLogin> =
            ApiClient().service.loginEmployee(logInDetail)
        call.enqueue(object : Callback<ResponseEmployeeAtLogin> {
            override fun onResponse(
                call: Call<ResponseEmployeeAtLogin>,
                response: Response<ResponseEmployeeAtLogin>
            ) {
                if (response.isSuccessful) {


                    // LoginHierarchy2ndLevel("manager");
                    gotoHome()
                    Prefs.putString(Global.Employee_Name, response.body()!!.data.SalesEmployeeName)
                    Prefs.putString(Global.Employee_maILid, response.body()!!.data.Email)
                    Prefs.putString(Global.Employee_Code, response.body()!!.data.SalesEmployeeCode)
                    Prefs.putString(Global._ReportingTO, response.body()!!.data.reportingTo)
                    Prefs.putString(Global.Employee_role, response.body()!!.data.role.Name)
                    Prefs.putString(Global.Employee_SalesEmpCode, response.body()!!.data.id.toString())
                    if (binding.rememberme.isChecked) {
                        Prefs.putString(Global.LogInUserName, response.body()!!.data.userName)
                        Prefs.putString(Global.LogInPassword, response.body()!!.data.password)
                        Prefs.putString(Global.RememberMe, "CheckIn")
                    }

                    Log.e("data", response.body().toString())
                } else {
                    //   progressBar.setVisibility(View.GONE)
                    Global.warningmessagetoast(this@LoginActivity, "Check Login Credentials.")

                }
                binding.loader.stop()
                binding.loader.visibility = View.GONE
            }

            override fun onFailure(call: Call<ResponseEmployeeAtLogin>, t: Throwable) {
                binding.loader.stop()
                binding.loader.visibility = View.GONE
                Log.e("data", t.message.toString())
                Global.errormessagetoast(this@LoginActivity, "Check Login Credentials.")
            }
        })
    }

    private fun gotoHome() {
        val intent = Intent(this, AhujaSonsMainActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }


    //todo login observer---
    private fun subscribeToObserver() {
        viewModel.userStatus.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "subscribeToObserver: ${it}")
                binding.loader.visibility = View.GONE
//                Global.errormessagetoast(this, it)
                if (it.contains("java.lang.IllegalStateException: Expected BEGIN_OBJECT but was BEGIN_ARRAY at line 1 column 69 path \$.data")){
                    Global.errormessagetoast(this, "Username or Password is incorrect")
                }else{
                    Global.errormessagetoast(this, "Something Went Wrong!")
                }
                if (it == "For input string: \"NA\"") {
                }
            }, onLoading = {
                binding.loader.visibility = View.VISIBLE
            }, {
                Log.e(TAG, "subscribeToObserver: ${it.status}")
                binding.loader.visibility = View.GONE

                if (it.status == 200) {
                    gotoHome()

                    Global.APILog = ""

                    Prefs.putBoolean(Global.AutoLogIn, true)
                    Prefs.putString(Global.Employee_Name, it.data.SalesEmployeeName)
                    Prefs.putString(Global.MyID, it.data.id.toString())
                    Prefs.putString(Global.Employee_maILid, it.data.Email)
                    Prefs.putString(Global.Employee_Code, it.data.SalesEmployeeCode.toString())
                    Prefs.putString(Global.Employee_role, it.data.role.Name)
                    Prefs.putString(Global.Employee_role_ID, it.data.role.id.toString())
                    Prefs.putString(Global.Employee_SalesEmpCode, it.data.id.toString())
                    Prefs.putString(Global.LogInUserName, it.data.userName)
                    Prefs.putString(Global.LogInPassword, it.data.password)
                    Prefs.putString(Global.FirstName, it.data.firstName)
                    Prefs.putString(Global._ReportingTO, it.data.reportingTo)

                    //todo store otp ..
                    Prefs.putString(Global.OTP_VERIFY, Global.formatAs6DigitNumber(Prefs.getString(Global.Employee_SalesEmpCode).toInt()))

                    if (binding.rememberme.isChecked) {
                        Prefs.putString(Global.RememberMe, "CheckIn")
                    }

                    Prefs.putBoolean(Global.Location_FirstTime, true)
                    Prefs.putBoolean(Global.LocationRestart, false)

                    var session = "30".toLong()
                    session = session * 60 * 1000

                    Prefs.putLong(Global.SESSION_TIMEOUT, session)
                    Prefs.putLong(Global.SESSION_REMAIN_TIME, 0)

                    finish()

                }
               else {
                    Global.errormessagetoast(this, it.message)
                }

            }

        ))


    }

    private fun testObserver() {
        viewModel.userTestStatus.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "testObserver: $it")
            }, onLoading = {

            }, {
                Log.e(TAG, "testObserverSucces===>: $it")
            }
        ))
    }


}