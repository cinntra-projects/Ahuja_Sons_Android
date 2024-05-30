package com.ahuja.sons.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.loadingview.LoadingView
import com.ahuja.sons.R
import com.ahuja.sons.`interface`.DataBaseClick
import com.ahuja.sons.adapter.CategoryAdapter
import com.ahuja.sons.adapter.CreatePartRequestAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.custom.FileUtil.getFileName
import com.ahuja.sons.databinding.CreatepartrequestfragmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.NewLoginData
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class CreatePartRequest : MainBaseActivity(), DataBaseClick {

    lateinit var adapter: CreatePartRequestAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var ticketFragment: CreatepartrequestfragmentBinding
    lateinit var viewModel: MainViewModel
    var openpdfpath = ""
    var attachentUri = ""

//    private lateinit var pdfUri: Uri

    var productcatcode = TicketData()
    var id = ""
    var pdfurilist = ArrayList<String>()

    //var productcatcode = TicketDataModel()

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    companion object {
        private const val TAG = "CreatePartRequest"
        private val THIRD_ACTIVITY_REQUEST_CODE = 3

    }


    private fun subscribeToObserver() {
        viewModel.particularTicket.observe(this, Event.EventObserver(
            onError = {
                Log.e(TAG, "errorInApi: $it")
                Global.warningmessagetoast(this, it)
            }, onLoading = {

            }, {
                if (it.status.equals(200)) {
                    productcatcode = it.data[0]

                    setData()

                } else {
                    Log.e(TAG, "responseError: ${it.message}")
                    Global.warningmessagetoast(this, it.message)
                }


            }
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ticketFragment = CreatepartrequestfragmentBinding.inflate(layoutInflater)
        setUpViewModel()
        setContentView(ticketFragment.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        id = intent.getStringExtra(Global.INTENT_TICKET_ID).toString()
        //   productcatcode = intent.getParcelableExtra<TicketDataModel>("TicketData")!!


        var hashMap = HashMap<String, String>()
        hashMap["id"] = id

        viewModel.getTicketOne(hashMap)
        ticketFragment.backPress.setOnClickListener {
            onBackPressed()
        }
        ticketFragment.add.setOnClickListener {
            openCategorydailog()
        }

        ticketFragment.tvSelectAttachment.setOnClickListener {
            // selectPdf()

// Create an intent to open the file picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // Set the file type to be picked


// Start the activity and wait for the user to pick a file

// Start the activity and wait for the user to pick a file
            startActivityForResult(
                intent,
                1111
            )
        }

        //  setData()
        ticketFragment.heading.text = "Selected Items"

        subscribeToObserver()

        // callitemApi(pageno)


        ticketFragment.done.setOnClickListener {

            if (Global.cartList.size > 0) {
//                val bundle = Bundle()
//                  bundle.putParcelable(Global.PartRequestData, productcatcode)
                val intent = Intent(this, CreateRequestSubmitReport::class.java)
                intent.putExtra("address", productcatcode.ContactAddress)
                intent.putExtra("id", productcatcode.id.toString())
                //  intent.putExtras(bundle)
                startActivityForResult(intent, THIRD_ACTIVITY_REQUEST_CODE)
                //  startActivity(intent)
            } else {
                Global.errormessagetoast(this, "Add atleast one part")
            }
        }


/*
        ticketFragment.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.


                if(Global.checkForInternet(this)&&recallApi){
                    pageno++
                    ticketFragment.idPBLoading.visibility = View.VISIBLE
                    callitemApi(pageno)
                }

            }
        })
*/

    }


    var TaxListdialog: Dialog? = null

    private fun openCategorydailog() {
        val backPress: ImageView
        val head_title: TextView
        val recyclerview: RecyclerView
        val loader: LoadingView
        TaxListdialog = Dialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val custom_dialog: View = layoutInflater.inflate(R.layout.taxes_alert, null)
        recyclerview = custom_dialog.findViewById(R.id.recyclerview)
        backPress = custom_dialog.findViewById(R.id.back_press)
        head_title = custom_dialog.findViewById(R.id.heading)
        loader = custom_dialog.findViewById(R.id.loader)
        head_title.text = "Select Category"
        TaxListdialog!!.setContentView(custom_dialog)
        TaxListdialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        TaxListdialog!!.show()
        backPress.setOnClickListener {
            TaxListdialog!!.dismiss()
        }


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
                    Global.warningmessagetoast(this@CreatePartRequest, it)
                },
                onLoading = {
                    loader.visibility = View.VISIBLE
                },
                onSuccess = { response ->
                    if (response.status == 200) {
                        loader.visibility = View.GONE
                        Log.e("response==>", response.message)
                        val adapter = CategoryAdapter(this@CreatePartRequest, response.data, TaxListdialog!!)
                        recyclerview.layoutManager = LinearLayoutManager(this@CreatePartRequest, RecyclerView.VERTICAL, false)
                        recyclerview.adapter = adapter
                    } else {
                        loader.visibility = View.GONE
                        Global.warningmessagetoast(this@CreatePartRequest, response.message)
                    }

                })
        )
    }


    fun getRealPathFromURI(uri: Uri?): String? {
        var result: String? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            openpdfpath = filePath
            result = filePath
            //  Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();

            // Do something with the file path, such as displaying it in a TextView
            // TextView filePathTextView = findViewById(R.id.file_path_text_view);
            //  filePathTextView.setText(filePath);
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            setData()
        }

        if (requestCode == THIRD_ACTIVITY_REQUEST_CODE && resultCode == CreateRequestSubmitReport.RESULT_CODE_FINISH_SECOND_ACTIVITY) {
            // Finish the 2nd activity
            finish()
        }


        if (requestCode == 1111 && resultCode == RESULT_OK) {
            // Get the URI of the selected file
            val uri = data!!.data
            attachentUri = uri.toString()
            openpdfpath = getFileName(this, uri!!).toString()
            Log.e(TAG, "onActivityResult: $openpdfpath")
            Log.e(TAG, "onActivityResult: $attachentUri")
            ticketFragment.tvselectedAttachment.setText(openpdfpath)
            // attachmentName.setText(attchmentName)
            // etNewAttachemnt.setText(attchmentName)
            //   picturePath = getRealPathFromURI(uri)
            //  Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();


            // Use the URI to access the file
            // ...
        }

//        when (requestCode) {
//            12 -> if (resultCode == RESULT_OK) {
//
//                data?.data?.let { uri ->
//                  //  openpdfpath = uri.path.toString()
//
//                    ticketFragment.tvselectedAttachment.text = getRealPathFromURI(uri)
//                    Log.e(TAG, "onActivityResult: $openpdfpath")
//                    // Use the filePath as needed
//                }
//
//
////                if (data?.clipData != null) {
////                    val count = data.clipData?.itemCount ?: 0
////                    for (i in 0 until count) {
////                        val imageUri: Uri = data.clipData?.getItemAt(i)?.uri!!
////                        openpdfpath = copyFileToInternalStorage(
////                            imageUri,
////                            "com.android.servicesupport"
////                        ).toString()
////
////                        pdfurilist.add(openpdfpath)
////
////                    }
////
////                } else {
////                    pdfUri = data?.data!!
////                    val uri: Uri = data.data!!
////                    val uriString: String = uri.toString()
////                    openpdfpath =
////                        copyFileToInternalStorage(pdfUri, "com.android.servicesupport").toString()
////                    pdfurilist.add(openpdfpath)
////                }
////                var pdfName: String? = null
////                /*if (pdfurilist[0].startsWith("content://")) {
////                    var myCursor: Cursor? = null
////                    try {
////                        // Setting the PDF to the TextView
////                        myCursor = requireContext().contentResolver.query(pdfUri, null, null, null, null)
////                        if (myCursor != null && myCursor.moveToFirst()) {
////                            pdfName = myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
////                            openpdffrom = "URI"
////                            ticketbiding.docView.isVisible = true
////                            ticketbiding.docname.text = pdfName
////                            ticketbiding.progressbar.start()
////                            uploaddocument()
////                        }
////                    } finally {
////                        myCursor?.close()
////                    }
////                }*/
////                openpdffrom = "URI"
////                /*   ticketbiding.docView.isVisible = true
////                   ticketbiding.docname.text = pdfName*/
////                ticketbiding.progressbar.start()
////                uploaddocument()
//            }
//        }

    }


    private fun copyFileToInternalStorage(uri: Uri, newDirName: String): String? {
        val returnCursor: Cursor = getContentResolver().query(
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
        val size = returnCursor.getLong(sizeIndex).toString()
        val output: File = if (newDirName != "") {
            val dir: File = File(filesDir.toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            File(filesDir.toString() + "/" + newDirName + "/" + name)
        } else {
            File(filesDir.toString() + "/" + name)
        }
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
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

    override fun onResume() {
        super.onResume()
        setData()
    }

    private fun setData() {
        ticketFragment.loadingView.stop()
        ticketFragment.loadingback.visibility = View.GONE
        // businessPartners.clear();
        linearLayoutManager = LinearLayoutManager(this@CreatePartRequest, LinearLayoutManager.VERTICAL, false)
        adapter = CreatePartRequestAdapter(ticketFragment.totalItem, Global.cartList)
        ticketFragment.productRecyclerView.layoutManager = linearLayoutManager
        ticketFragment.productRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    /* var itemdata = ArrayList<DocumentLine>()
     private fun callitemApi(pageno: Int) {
         val data = DocumentLine(
             PageNo = pageno,
             ItemsGroupCode = productcatcode.ProductCategory
         )
         val call: Call<AccountItemResponse> =
             ApiClient().service.getItemlist(data)
         call.enqueue(object : Callback<AccountItemResponse> {
             override fun onResponse(
                 call: Call<AccountItemResponse>,
                 response: Response<AccountItemResponse>
             ) {
                 if (response.code() == 200) {

                     ticketFragment.loadingView.stop()
                     ticketFragment.idPBLoading.visibility = View.GONE
                     recallApi = response.body()!!.data.isNotEmpty()
                     itemdata.addAll(response.body()!!.data)
                     // businessPartners.clear();
                     linearLayoutManager = LinearLayoutManager(this@CreatePartRequest, LinearLayoutManager.VERTICAL, false)
                     adapter = CreatePartRequestAdapter(ticketFragment.totalItem,itemdata)
                     ticketFragment.productRecyclerView.layoutManager = linearLayoutManager
                     ticketFragment.productRecyclerView.adapter = adapter
                     adapter.notifyDataSetChanged()

                     checknodata()
                 }
             }

             override fun onFailure(call: Call<AccountItemResponse>, t: Throwable) {
                 ticketFragment.idPBLoading.visibility = View.GONE
                 ticketFragment.loadingView.stop()
             }
         })



     }*/

    private fun checknodata() {
        ticketFragment.nodatafound.isVisible = adapter.itemCount == 0
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

            }
        }

    override fun onClick(po: Int) {
        val intent = Intent(this, ItemsList::class.java)
        intent.putExtra("CategoryID", po)
        intent.putExtra("ticketId", id)
        launcher.launch(intent)
    }


}
