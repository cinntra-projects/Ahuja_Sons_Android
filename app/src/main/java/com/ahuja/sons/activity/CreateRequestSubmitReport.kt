package com.ahuja.sons.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.adapter.PartRequestSubmitAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.custom.FileUtil
import com.ahuja.sons.databinding.*
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.Apis
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
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateRequestSubmitReport : MainBaseActivity() {

    private lateinit var m_imagePath: String
    private lateinit var openpdfpath: String
    private lateinit var m_curentDateandTime: String
    lateinit var adapter: PartRequestSubmitAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var ticketFragment: AftersubmitrequestBinding
    private lateinit var pdfUri: Uri
    private var pRID = ""
    var attachuri = false
    val REQUEST_IMAGE_CAPTURE = 1001
    var id = ""
    var contactAddress = ""
    lateinit var viewModel: MainViewModel

    // var ticketdata = TicketDataModel()

    companion object {
        const val RESULT_CODE_FINISH_SECOND_ACTIVITY = 2
    }

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
        ticketFragment = AftersubmitrequestBinding.inflate(layoutInflater)
        setContentView(ticketFragment.root)

        setUpViewModel()

        setSupportActionBar(ticketFragment.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        contactAddress = intent.getStringExtra("address").toString()
        id = intent.getStringExtra("id").toString()

        //   ticketdata = intent.getParcelableExtra<TicketDataModel>(Global.PartRequestData)!!


        val totlqty = Global.cartList.sumOf { it!!.Quantity }
        val totalprice = Global.cartList.sumOf { (it!!.UnitPrice * it.Quantity) }
        ticketFragment.totalqty.text = totlqty.toString()
        ticketFragment.totalprice.text = totalprice.toString()
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = PartRequestSubmitAdapter()
        ticketFragment.heading.text = "Part Request"
        ticketFragment.recyclerview.layoutManager = linearLayoutManager
        ticketFragment.recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

        ticketFragment.attachementupload.setOnClickListener {
            selectPdf()
        }


        ticketFragment.uploadImg.setOnClickListener {

            val m_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            pdfUri = getImageUri()
            m_intent.putExtra(MediaStore.EXTRA_OUTPUT, pdfUri)
            startActivityForResult(m_intent, REQUEST_IMAGE_CAPTURE)

        }

        ticketFragment.done.setOnClickListener {


            val partdata = CreatePartrequestpayload(
                BillToAddress = contactAddress,
                OwnerId = Prefs.getString(Global.Employee_Code, "").toInt(),
                TicketId = id.toInt(),
                PRItems = managepayload()
            )

            ticketFragment.loadingView.start()
            Log.e("payload", partdata.toString())
            Log.e("TAG====>>", "createpartreqapi: ")

            viewModel.createPartRequest(partdata)
            bindCRObserver()

        }

    }

    //todo create request observer..

    private fun bindCRObserver() {
        viewModel.createPartRequest.observe(this, Event.EventObserver(
            onError = {
                ticketFragment.loadingView.stop()
                ticketFragment.loadingback.visibility = View.GONE
                Log.e("tag==>", "errorInApi: $it")
//                Global.warningmessagetoast(this, it)
            }, onLoading = {
                ticketFragment.loadingView.start()
                ticketFragment.loadingback.visibility = View.VISIBLE

            },
            onSuccess = { response ->
                ticketFragment.loadingView.stop()
                ticketFragment.loadingback.visibility = View.GONE
                if (response.status == 200) {
                    Global.cartList.clear()
                    pRID = response.data[0].PRID.toString()
                    if (attachuri) {
                        callUploadAttachmentApi()
                    } else {
                        val intent = Intent()
                        setResult(RESULT_CODE_FINISH_SECOND_ACTIVITY, intent)
//                        val allPartRequest = AllPartRequest()
//                        val bundle = Bundle()
//                        bundle.putString("TicketID", id)
//                        val transaction = supportFragmentManager.beginTransaction()
//                        allPartRequest.arguments = bundle
//                        transaction.add(R.id.main_container, allPartRequest).addToBackStack("")
//                        transaction.commit()

                        finish()
                        //   onBackPressed()
                    }
                } else {
                    Log.e("error=== >", "responseError: ${response.message}")
                    Global.warningmessagetoast(this, response.message)
                }


            }
        ))
    }


    private fun callUploadAttachmentApi() {
        val pdfuristring = FileUtil.getPath(this, pdfUri)

        //todo calling multipart api..
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        val file: File = File(openpdfpath)

        builder.addFormDataPart("Attachment", file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
        builder.addFormDataPart("EmployeeId", Prefs.getString(Global.Employee_Code))
        builder.addFormDataPart("PRID", pRID)

        val requestBody = builder.build()
        Log.e("payload", requestBody.toString())

        viewModel.prUpload(requestBody)

        bindPRAttachmentObserver()

    }


    //todo calling pr upload observer..
    private fun bindPRAttachmentObserver() {
        viewModel.customerUpload.observe(this, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(this, it)
                Log.e("Error===>", "attachmentObserverONERROR==>: $it")
            }, onLoading = {

            },
            onSuccess = { response ->

                if (response.status == 200) {
                    val intent = Intent()
                    setResult(RESULT_CODE_FINISH_SECOND_ACTIVITY, intent)
                    finish()
                    Global.successmessagetoast(this@CreateRequestSubmitReport, "Upload SuccessFully")
                } else {

                    Global.warningmessagetoast(this, response.message);

                }
            }
        ))
    }

    private fun selectPdf() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        pdfIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(pdfIntent, 12)
    }


    private fun getImageUri(): Uri {
        var m_imgUri: Uri? = null
        val m_file: File
        try {
            val m_sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
            m_curentDateandTime = m_sdf.format(Date())
            m_imagePath =
                Environment.getExternalStorageDirectory().absolutePath + File.separator + m_curentDateandTime.toString() + ".jpg"
            m_file = File(m_imagePath)
            m_imgUri = Uri.fromFile(m_file)
        } catch (p_e: Exception) {
        }
        return m_imgUri!!
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // For loading PDF
        when (requestCode) {
            12 -> if (resultCode == RESULT_OK) {
                attachuri = true
                pdfUri = data?.data!!
                val uri: Uri = data.data!!
                val uriString: String = uri.toString()
                openpdfpath =
                    copyFileToInternalStorage(pdfUri, "com.massaed.servicesupportportal").toString()
                var pdfName: String? = null
                if (uriString.startsWith("content://")) {
                    var myCursor: Cursor? = null
                    try {
                        // Setting the PDF to the TextView
                        myCursor = contentResolver.query(uri, null, null, null, null)
                        if (myCursor != null && myCursor.moveToFirst()) {
                            pdfName =
                                myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                            ticketFragment.docView.isVisible = true
                            ticketFragment.docname.text = pdfName
//                            uploaddocument()
                        }
                    } finally {
                        myCursor?.close()
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                attachuri = true
                Log.d("test1", "" + pdfUri)
            }
        }

    }


    private fun copyFileToInternalStorage(uri: Uri, newDirName: String): String? {
        val returnCursor: Cursor = contentResolver.query(
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
            val dir: File = File(getFilesDir().toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            output = File(getFilesDir().toString() + "/" + newDirName + "/" + name)
        } else {
            output = File(getFilesDir().toString() + "/" + name)
        }
        try {
            val inputStream: InputStream? = getContentResolver().openInputStream(uri)
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

    private fun managepayload(): ArrayList<CreatePartrequestpayloadItem> {

        val createpartdata = ArrayList<CreatePartrequestpayloadItem>()


        for (data in Global.cartList) {
            val payload = CreatePartrequestpayloadItem(
                Comments = data!!.Remarks,
                ItemCode = data.ItemCode,
                ItemQty = data.Quantity,
                UnitPrice = data.UnitPrice.toString(),
                ProjectCode = data.ItemsGroupCode,
                id = data.id,
                PartRequestType = data.PartRequestType,
                ItemSrialNo = data.ItemSrialNo,
                ContractType = data.ContractType
            )
            createpartdata.add(payload)
        }
        return createpartdata

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }


}
