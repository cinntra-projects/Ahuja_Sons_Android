package com.ahuja.sons.activity

import `in`.aabhasjindal.otptextview.OTPListener
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.`interface`.SmsListener
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.custom.FileUtil.TAG
import com.ahuja.sons.databinding.SignupConfirmBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.fragment.FullScreenBottomScreenDialogFragment
import com.ahuja.sons.fragment.FullScreenEngineerDialogFragment
import com.ahuja.sons.`interface`.ImageEngineerSelectorListener
import com.ahuja.sons.`interface`.OnImageSelectedListener
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class SignedConfirmActivity : MainBaseActivity(), SmsListener, OnImageSelectedListener, ImageEngineerSelectorListener {

    private lateinit var ticketbiding: SignupConfirmBinding
    private lateinit var verificationId: String

    // variable for FirebaseAuth class
    private lateinit var mAuth: FirebaseAuth
    private lateinit var resendToken: ForceResendingToken
    val REQUEST_CODE_DRAW = 100001
    private var ticketid: Int = 0

    private var otpverified = false
    lateinit var viewModel: MainViewModel

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ticketbiding = SignupConfirmBinding.inflate(layoutInflater)
        setContentView(ticketbiding.root)

        setUpViewModel()
        setSupportActionBar(ticketbiding.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mAuth = FirebaseAuth.getInstance()
//        mAuth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true);

        var phone = Global.UserNumber
        ticketbiding.phoneNumber.text = phone

        Log.e(TAG, "onCreate: ${Global.AlternateUserNumber}")

        ticketbiding.alternatephone.setOnClickListener {
            if (Global.AlternateUserNumber.isEmpty()) {
                Toast.makeText(this, "No Alternate contact Found", Toast.LENGTH_SHORT).show()
            } else {
                phone = Global.AlternateUserNumber
                ticketbiding.phoneNumber.text = phone
//                sendVerificationCode(phone) //todo comment by chanchal
            }

        }


//        sendVerificationCode(phone)//todo comment by me---

        ticketid = intent.getIntExtra("ID", 1)

        ticketbiding.resend.setOnClickListener {
//            resendVerificationCode(phone, resendToken)//todo comment by chanchal
        }


        ticketbiding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {
                // fired when user has entered the OTP fully.
/*
                if (ticketbiding.otpView.otp.toString().isNotEmpty()) {
                    verifyCode(ticketbiding.otpView.otp.toString())
                }*/

                if (ticketbiding.otpView.otp.toString().isNotEmpty()) {

                    if (ticketbiding.otpView.otp.toString() == Global.formatAs6DigitNumber(Prefs.getString(Global._ReportingTO).toInt())) {
                        otpverified = true
                    }else{
                        Global.warningmessagetoast(this@SignedConfirmActivity, "Please Enter Valid OTP")
                    }
//                     verifyCode(ticketbiding.otpView.otp.toString()) //todo comment by chanchal
                }
            }
        }

        ticketbiding.confirm.setOnClickListener {
            if (otpverified) {

//                saveCustomerImage(ticketbiding.drawView.getBitmap())
                saveCustomerImage(customerSignatureDrawImage)

                saveEngineerImage(engineerSignatureDrawImage)

                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)

                //todo customer
                val file: File = getOutputMediaFileForCustomer()

                val requestFile = file.let { RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it) }

                //todo enginner
                val fileEng: File = getOutputMediaFileForEngineer()

                val requestFileEng = fileEng.let { RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it) }

                builder.addFormDataPart("CustomerSignature", file.name, requestFile)
                builder.addFormDataPart("EngineerSignature", fileEng.name, requestFileEng)
                builder.addFormDataPart("EmployeeId", Prefs.getString(Global.Employee_Code))
                builder.addFormDataPart("TicketId", "" + ticketid)
                builder.addFormDataPart("CustomerFeedback", ticketbiding.edtCustomerRemark.text.toString())
                builder.addFormDataPart("EngineerFeedback", ticketbiding.edtServiceEngineerRemark.text.toString())

                val requestBody = builder.build()
                Log.e("payload", requestBody.toString())

                viewModel.signandconfirm(requestBody)

                bindObserver()

            } else {
                Global.warningmessagetoast(this, "Kindly verify otp to proceed further")
            }//todo comment by me
        }

        ticketbiding.clearText.setOnClickListener {
            ticketbiding.setSignaturePic.visibility = View.GONE
            ticketbiding.openSignatureDialog.visibility = View.VISIBLE
//            ticketbiding.drawView.clearCanvas()
        }

        ticketbiding.clearEngineerText.setOnClickListener {
            ticketbiding.setEngineerSignaturePic.visibility = View.GONE
            ticketbiding.openEngineerSignatureDialog.visibility = View.VISIBLE
//            ticketbiding.engineerSignDrawView.clearCanvas()
        }



        ticketbiding.openSignatureDialog.setOnClickListener {
            val bottomSheetDialogFragment = FullScreenBottomScreenDialogFragment()
            // Set full screen behavior
            bottomSheetDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
            bottomSheetDialogFragment.setOnImageSelectedListener(this)

        }


        ticketbiding.openEngineerSignatureDialog.setOnClickListener {
            val bottomSheetDialogFragment = FullScreenEngineerDialogFragment()
            // Set full screen behavior
            bottomSheetDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
            bottomSheetDialogFragment.setOnImageSelectedListener(this)

        }

    }

    //todo signin confirm observer..
    private fun bindObserver() {
        viewModel.ticketSignnConfirmResponse.observe(this, Event.EventObserver(
            onError = {
                ticketbiding.loading.visibility = View.GONE
                Global.warningmessagetoast(this, it)
                Log.e("ticketAcceptReject", it)
            },
            onLoading = {
                        ticketbiding.loading.visibility = View.VISIBLE
            },
            onSuccess = { response ->
                Log.e("response",response.toString())
                ticketbiding.loading.visibility = View.GONE
                if (response.status == 200) {
                    Global.successmessagetoast(this@SignedConfirmActivity, response.message)
                   /* if (supportFragmentManager.findFragmentByTag("MyFragment") == null) {
                        val fragment = TicketFragment(1)
                        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.container, fragment, "MyFragment").addToBackStack(null).commit()
                    }*/

                    onBackPressed()
                } else {
                    Global.warningmessagetoast(this, response.message);
                }
            }
        ))
    }


    lateinit var customerSignatureDrawImage : Bitmap
    lateinit var engineerSignatureDrawImage : Bitmap


    //todo override function
    override fun onImageSelected(imageUri: Bitmap) {
        ticketbiding.setSignaturePic.visibility = View.VISIBLE
        ticketbiding.openSignatureDialog.visibility = View.GONE
        ticketbiding.setSignaturePic.setImageBitmap(imageUri)
        customerSignatureDrawImage = imageUri
    }



    //todo engineer signature image selector--
    override fun onEngImageSelected(imageUri: Bitmap) {
        ticketbiding.setEngineerSignaturePic.visibility = View.VISIBLE
        ticketbiding.openEngineerSignatureDialog.visibility = View.GONE
        ticketbiding.setEngineerSignaturePic.setImageBitmap(imageUri)
        engineerSignatureDrawImage = imageUri
    }



    //todo save draw bitmap data--
    private fun saveCustomerImage(bitmap: Bitmap) {
        val pictureFile: File = getOutputMediaFileForCustomer()
        try {
            val fos: FileOutputStream = FileOutputStream(pictureFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(FileUtil.TAG, "File not found: " + e.message)
        } catch (e: IOException) {
            Log.d(FileUtil.TAG, "Error accessing file: " + e.message)
        }
    }

    private fun getOutputMediaFileForCustomer(): File {
        //   val  mediaStorageDir =  File()

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        //        File f = new File(path);
        val f = applicationContext.getExternalFilesDir(
            Environment.getExternalStorageDirectory()
                .toString() + "/Android/data/" + applicationContext.packageName + "/Files"
        )
        // Create the storage directory if it does not exist
        /*  if (!mediaStorageDir.exists()) {
              if (!mediaStorageDir.mkdirs()) {
                  return null
              }
          }*/
        // Create a media file name
        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mImageName = "MI_" + timeStamp +"Customer"+ ".jpg"
        val mediaFile = File(f?.path + File.separator + mImageName)
        return mediaFile
    }


    //todo save draw bitmap data for engineer--
    private fun saveEngineerImage(bitmap: Bitmap) {
        val pictureFile: File = getOutputMediaFileForEngineer()
        try {
            val fos: FileOutputStream = FileOutputStream(pictureFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(FileUtil.TAG, "File not found: " + e.message)
        } catch (e: IOException) {
            Log.d(FileUtil.TAG, "Error accessing file: " + e.message)
        }
    }

    private fun getOutputMediaFileForEngineer(): File {
        //   val  mediaStorageDir =  File()

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        //        File f = new File(path);
        val f = applicationContext.getExternalFilesDir(
            Environment.getExternalStorageDirectory()
                .toString() + "/Android/data/" + applicationContext.packageName + "/Files"
        )
        // Create the storage directory if it does not exist
        /*  if (!mediaStorageDir.exists()) {
              if (!mediaStorageDir.mkdirs()) {
                  return null
              }
          }*/
        // Create a media file name
        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mImageName = "MI_" + timeStamp +"Engineer"+ ".jpg"
        val mediaFile = File(f?.path + File.separator + mImageName)
        return mediaFile
    }



    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // if the code is correct and the task is successful
                    // we are sending our user to new activity.

                    /*   finish()*/
                    ticketbiding.resend.visibility = View.GONE
                    otpverified = true
                    Global.successmessagetoast(this, "OTP verified successfully")


                } else {
                    // if the code is not correct then we are
                    // displaying an error message to the user.
                    Toast.makeText(
                        this,
                        task.exception?.message,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
    }

  /*  private fun callsignconfirmApi() {
        saveImage(ticketbiding.drawView.getBitmap())
        val file: File = getOutputMediaFile()

        val requestFile = file.let {
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it)
        }
        val body = MultipartBody.Part.createFormData("SignatureFile", file.name, requestFile)
        val employeeid = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), Prefs.getString(Global.Employee_Code))
        val ticketid = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "" + ticketid)
        val CustomerFeedback = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), ticketbiding.feedback.text.toString())

        val call: Call<TicketDetailsModel> = ApiClient().service.signandconfirm(body, employeeid, ticketid, CustomerFeedback)
        call.enqueue(object : Callback<TicketDetailsModel?> {
            override fun onResponse(
                call: Call<TicketDetailsModel?>,
                response: Response<TicketDetailsModel?>
            ) {
                if (response.code() == 200) {
                    Global.successmessagetoast(this@SignedConfirmActivity, response.message().toString())
                    onBackPressed()

                } else {
                    Global.warningmessagetoast(
                        this@SignedConfirmActivity,
                        response.message().toString()
                    )

                }
            }

            override fun onFailure(call: Call<TicketDetailsModel?>, t: Throwable) {
                Toast.makeText(this@SignedConfirmActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }*/


    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            // below method is used when
            // OTP is sent from Firebase
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                // when we receive the OTP it
                // contains a unique id which
                // we are storing in our string
                // which we have already created.
                verificationId = s
                resendToken = forceResendingToken
            }

            // this method is called when user
            // receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // below line is used for getting OTP code
                // which is sent in phone auth credentials.
                val code = phoneAuthCredential.smsCode

                // checking if the code
                // is null or not.
                if (code != null) {
                    // if the code is not null then
                    // we are setting that code to
                    // our OTP edittext field.
                    ticketbiding.otpView.setOTP(code)

                    // after setting this code
                    // to OTP edittext field we
                    // are calling our verifycode method.

//                    verifyCode(code) //todo comment by chanchal
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception.
                Toast.makeText(this@SignedConfirmActivity, e.message, Toast.LENGTH_LONG).show()
                Log.e("onVerificationFailed==>", e.message.toString())
            }
        }


    private fun verifyCode(code: String) {
        // below line is used for getting
        // credentials from our verification id and code.
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential)
    }

    private fun sendVerificationCode(number: String) {
        // this method is used for getting
        // OTP on user phone number.
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Global.successmessagetoast(this, "OTP sent to $number successfully")

    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: ForceResendingToken
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallBack,  // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks

        Global.successmessagetoast(this, "OTP sent to $phoneNumber successfully")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun messageReceived(messageText: String?) {
        if (messageText != null) {
            ticketbiding.otpView.setOTP(messageText)
        }
    }


}
