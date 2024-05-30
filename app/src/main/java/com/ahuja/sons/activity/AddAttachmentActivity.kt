package com.ahuja.sons.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ticketItemAdapter.ImageViewAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityAddAttachmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.newapimodel.ItemAllListResponseModel
import com.ahuja.sons.newapimodel.SiteSurveyTicketResponse
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.IncapableCause
import com.zhihu.matisse.internal.entity.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class AddAttachmentActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddAttachmentBinding
    lateinit var viewModel: MainViewModel
    var dataModel = ItemAllListResponseModel.DataXXX()
    var ticketData = TicketData()


    private val REQUEST_CODE = 1

    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    private val REQUEST_CODE_PERMISSIONS = 10
    lateinit var fileUri: Uri

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    var Flag = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAttachmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        Flag = intent.getStringExtra("Flag")!!
        if (Flag == "Site Survey") {
            ticketData = intent.getSerializableExtra("data")!! as TicketData
        } else {
            dataModel = intent.getSerializableExtra("data")!! as ItemAllListResponseModel.DataXXX
        }


        binding.loadingback.visibility = View.GONE

        binding.ivAttachmentFiles.setOnClickListener {
//            openImageUploader()
//            chooseImageFromGallery()

            if (allPermissionsGranted()) {
                captureImageFromCamera()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_PERMISSIONS
                )
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }

        }


        if (Global.checkForInternet(this@AddAttachmentActivity)) {
            //todo set default data--

            if (Flag != "Site Survey") {
                var jsonObject = JsonObject()
                jsonObject.addProperty("TicketId", ticketData.id)
                jsonObject.addProperty("ReportType", Flag)
                jsonObject.addProperty("ItemSerialNo", "")
                jsonObject.addProperty("ItemCode", "")

                viewModel.ticketSiteSurveyOneApi(jsonObject)
                bindDefaultObserver()
            }

        }



        binding.saveBtn.setOnClickListener {
            //todo calling remark api here---
            binding.loadingback.visibility = View.VISIBLE
            binding.loadingView.start()

            try {
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)

                builder.addFormDataPart("ReportId", "")
                builder.addFormDataPart("ReportType", Flag)
                if (Flag == "Site Survey") {
                    if (ticketData != null) {
                        builder.addFormDataPart("ItemSerialNo", "")
                        builder.addFormDataPart("TicketId", defaultDataModel.TicketId)
                    }

                } else {
                    if (dataModel != null) {
                        builder.addFormDataPart("ItemSerialNo", dataModel.SerialNo)
                        builder.addFormDataPart("TicketId", dataModel.TicketId)
                    }
                }


                Log.e(TAG, "onCreate: ${mArrayUriList.size}")

                if (mArrayUriList.size > 0) {
                    for (i in 0 until mArrayUriList.size) {
                        if (path.isNotEmpty()) {
                            val file: File
                            try {
                                file = File(path[i])
                                val attach = MultipartBody.Part.createFormData(
                                    "File",
                                    file.name,
                                    RequestBody.create(
                                        "multipart/form-data".toMediaTypeOrNull(),
                                        file
                                    )
                                )
                                builder.addPart(attach)
                            } catch (e: Exception) {
                                builder.addFormDataPart(
                                    "File",
                                    "",
                                    RequestBody.create(
                                        "multipart/form-data".toMediaTypeOrNull(),
                                        ""
                                    )
                                )
                                e.printStackTrace()
                            }
                        } else {
                            builder.addFormDataPart("File", "")
                        }
                    }
                } else {
                    builder.addFormDataPart("File", "")
                }


                val requestBody = builder.build()
                Log.e("payload", requestBody.toString())


                viewModel.addAttachment(requestBody)
                bindObserver()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


        }

    }

    private fun bindObserver() {
        viewModel.customerUpload.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()

                Log.e(TAG, "bindRemarkObserver: $it")
            },
            onLoading = {
                binding.loadingback.visibility = View.VISIBLE
                binding.loadingView.start()

            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.successmessagetoast(this, "Successful")
                    onBackPressed()
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this, response.message)
                }
            }
        ))
    }


    //todo bind default observer---
    var defaultDataModel = SiteSurveyTicketResponse.DataXXX()

    private fun bindDefaultObserver() {
        viewModel.siteSurveyOneData.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "bindRemarkObserver: $it")
            },
            onLoading = {
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    if (response.data.size > 0 && response.data != null) {
                        defaultDataModel = response.data[0]
                    }
                } else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this, response.message)
                }
            }
        ))
    }


    private val PICK_IMAGES_REQUEST_CODE = 1

    private fun captureImageFromCamera() {
        var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, PICK_IMAGES_REQUEST_CODE)
    }


    //todo camera capture--

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGES_REQUEST_CODE -> if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && null != data) {
                try {
                    var photo = data?.extras?.get("data")
                    file = savebitmap(photo as Bitmap)
                    picturePath = file.path
                    fileUri = Uri.fromFile(file)
                    Log.e("fileUri---", fileUri.toString())

                    //todo add camera bitmap file uri photo in arraylist...
                    mSelectedList.clear()
                    mSelectedList.add(fileUri)

                    mArrayUriList.addAll(mSelectedList)

                    Log.e(TAG, "onActivityResult: " + mArrayUriList.size)

                    path.clear()

                    for (i in mArrayUriList.indices) {
                        path.add(
                            FileUtils.getPath(this@AddAttachmentActivity, mArrayUriList[i])
                                .toString()
                        )
                    }

                    val adapter = ImageViewAdapter(this, mArrayUriList)
                    binding.rvAttachment.setLayoutManager(
                        GridLayoutManager(
                            this,
                            1,
                            GridLayoutManager.HORIZONTAL,
                            false
                        )
                    )
                    binding.rvAttachment.setAdapter(adapter)

                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

            } else {
                // Failed to take picture
                Global.warningmessagetoast(this, "Failed to take camera picture")
            }
        }
    }


    //todo to convert bitmap to file--
    private fun savebitmap(bmp: Bitmap): File {
//        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        var extStorageDirectory =
            this.cacheDir //todo this is use for temporairy file path storage //"/storage/emulated/0/Download"
        var outStream: OutputStream? = null
        val num: Int = random.nextInt(90) + 10
//        var file = File(extStorageDirectory, "temp$num.png")

        Log.e("extStorageDirectory---", extStorageDirectory.toString())
        var file = File.createTempFile("prefix", ".extension", extStorageDirectory)
        if (file.exists()) {
            file.delete()
            file = File(extStorageDirectory, "temp$num.png")
        }
        try {
            outStream = FileOutputStream(file)
            if (outStream != null) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null!!
        }
        Log.e("file---", file.toString())
        return file
    }


    private fun allPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    } //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera()
            } else {
                Global.warningmessagetoast(this, "denied permissions case")
            }
        }
    }


    private val REQUEST_CODE_CHOOSE = 1000

    private fun openImageUploader() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        Matisse.from(this@AddAttachmentActivity)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .maxSelectable(5)
                            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideEngine())
                            .showPreview(false) // Default is `true`.originalEnable(true)
                            .maxOriginalSize(10)
                            .autoHideToolbarOnSingleTap(true)
                            .addFilter(UriFilter(mArrayUriList)) // Add a UriFilter for preselected images
                            .showSingleMediaType(true)
//                            .theme(R.style.Matisse_Zhihu1)

                            .forResult(REQUEST_CODE_CHOOSE)
                    } else {
                        Toast.makeText(
                            this@AddAttachmentActivity,
                            "Please enable permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()


    }


    var mArrayUriList: ArrayList<Uri> = ArrayList()
    var mSelectedList: ArrayList<Uri> = ArrayList()
    var path: ArrayList<String> = ArrayList()


/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == AppCompatActivity.RESULT_OK && null != data) {
            //mSelected.add(data.getData());
            mArrayUriList = Matisse.obtainResult(data) as ArrayList<Uri>
            for (i in mArrayUriList.indices) {
                path.add(FileUtils.getPath(this@AddAttachmentActivity, mArrayUriList.get(i)).toString())
            }
            val adapter = ImageViewAdapter(this@AddAttachmentActivity, mArrayUriList)
            binding.rvAttachment.setLayoutManager(GridLayoutManager(this@AddAttachmentActivity, 1, GridLayoutManager.HORIZONTAL, false))
            binding.rvAttachment.setAdapter(adapter)
//            adapter.notifyDataSetChanged()
            // Get the Image from data
        }

        else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the Image from data
            if (data.clipData != null) {
                val mClipData: ClipData = data.clipData!!
                var cout = data.clipData!!.itemCount
                for (i in 0 until cout) {
                    // adding imageuri in array
                    val imageurl: Uri = data.clipData!!.getItemAt(i).uri
                    mArrayUriList.add(imageurl)
                }

                Log.e(TAG, "onActivityResult: "+mArrayUriList.size )
                for (i in mArrayUriList.indices) {
                    path.add(FileUtils.getPath(this@AddAttachmentActivity, mArrayUriList.get(i)).toString())
                }
                val adapter = ImageViewAdapter(this, mArrayUriList)
                binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
                binding.rvAttachment.setAdapter(adapter)

            } else {
                val imageurl: Uri = data.data!!
                mArrayUriList.add(imageurl)
                Log.e(TAG, "onActivityResult: " + mArrayUriList.size )
                for (i in mArrayUriList.indices) {
                    path.add(FileUtils.getPath(this@AddAttachmentActivity, mArrayUriList.get(i)).toString())
                }
                val adapter = ImageViewAdapter(this, mArrayUriList)
                binding.rvAttachment.setLayoutManager(GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false))
                binding.rvAttachment.setAdapter(adapter)

            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }


    }*/


    //todo bind attachment from gallery--
    private fun chooseImageFromGallery() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE)
        } else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    class UriFilter(private val preselectedUris: List<Uri>) : Filter() {

        override fun constraintTypes(): Set<MimeType> {
            Log.e(TAG, "filter0: " + preselectedUris.size)

            return MimeType.ofImage()
        }

        override fun filter(context: Context, item: Item): IncapableCause? {
            // Return an IncapableCause if the item should be filtered
            return if (!preselectedUris.contains(item.contentUri)) {
                Log.e(TAG, "filter: " + preselectedUris.size)

                null // No incapability, the item is allowed
            } else {
                Log.e(TAG, "filter2: " + preselectedUris.size)

                IncapableCause(IncapableCause.TOAST, "This image is Already Selected.")
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {

            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }

    companion object {
        private const val TAG = "AddAttachmentActivity"
    }
}