package com.ahuja.sons.activity


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.TicketDetailsData
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


class ScannerAct : MainBaseActivity() {
    private lateinit var codeScanner: CodeScanner
    lateinit var tdm: TicketDetailsData
    private lateinit var scannerView: CodeScannerView
    private lateinit var timer: CountDownTimer
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
        setContentView(com.ahuja.sons.R.layout.scanner_view)

        setUpViewModel()

        scannerView =
            findViewById<CodeScannerView>(com.ahuja.sons.R.id.scanner)

        codeScanner = CodeScanner(this, scannerView)
        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("start", "start")

            }

            override fun onFinish() {
                val intent = Intent()
                intent.putExtra("Barcode", "Scan properly")
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        timer.start()
        // Parameters (default values)
        codeScanner.isTouchFocusEnabled = true
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {

            runOnUiThread {
                if (it.toString().isNotEmpty()) {
                    timer.cancel()

                    val tdd = TicketDetailsData(
                        SerialNo = it.toString()
                    )

                    viewModel.getTicketDetails(tdd)
                    ticketDetailObserver()

                }
                /*val intent = Intent()
                intent.putExtra("Barcode",it.text)
                setResult(RESULT_OK, intent)
                finish()*/

            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Global.errormessagetoast(this, "Camera initialization error: ${it.message}")

            }
        }

        scannerView.setOnClickListener {
            timer.start()
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()

        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    //todo ticket detail
    private fun ticketDetailObserver() {
        viewModel.getTicketDetails.observe(this, Event.EventObserver(

            onError = {
                Log.e("fail==>", it.toString())
                Global.errormessagetoast(this@ScannerAct, it.toString())
            },
            onLoading = {

            },
            onSuccess = { response ->
                if (response.data.isNotEmpty()) {
                    Log.e("response", response.data.toString())
                    tdm = response.data[0]
                    Prefs.putString(Global.TicketFlowFrom, "Scanner")
                    val b = Bundle()
                    b.putParcelable(Global.TicketData, tdm)
                    if (Prefs.getString(Global.Employee_role) == "admin" || Prefs.getString(
                            Global.Employee_role
                        ) == "support" || Prefs.getString(Global.Employee_role) == "support manager"
                    ) {
                        val intent = Intent(this@ScannerAct, AddTicketActivity::class.java)
                        intent.putExtras(b)
                        startActivity(intent)
                    } else {
                        Global.warningmessagetoast(
                            this@ScannerAct,
                            "You have no authorization to create ticket"
                        )
                    }
                } else {
                    Toast.makeText(this@ScannerAct, response.message, Toast.LENGTH_SHORT).show()
                }

            }

        ))
    }


}