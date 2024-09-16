package com.ahuja.sons.ahujaSonsClasses.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.BuildConfig
import com.ahuja.sons.ahujaSonsClasses.adapter.UploadImageListAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityOrderScreenForDeliveryPersonBinding
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderScreenForDeliveryPersonActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderScreenForDeliveryPersonBinding
    var isTripStarted = false
    lateinit var viewModel: MainViewModel

    private val PICK_IMAGES_REQUEST_CODE = 1111
    private val REQUEST_CODE_PERMISSIONS = 10
    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    val mArrayUriList = ArrayList<Uri>()
    lateinit var fileUri: Uri


    companion object {
        private const val TAG = "OrderScreenForDeliveryP"
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    var orderID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderScreenForDeliveryPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()
        orderID = intent.getIntExtra("id",0)

        binding.btnTrip.visibility = View.VISIBLE

        binding.btnENdTrip.setOnClickListener {
            binding.apply {
                tvCountText.visibility = View.INVISIBLE
                btnTrip.visibility = View.INVISIBLE
                btnENdTrip.visibility = View.GONE
                btnTrip.visibility = View.GONE
                linearTripEndDetails.visibility = View.VISIBLE
                tvCountText.visibility = View.GONE
                tvClickStartText.visibility = View.GONE
                uploadProofLayout.visibility = View.VISIBLE
                btnSubmit.visibility = View.VISIBLE


            }
        }



        binding.btnTrip.setOnClickListener {
            binding.apply {
                linearTripDetails.visibility = View.VISIBLE
                btnENdTrip.visibility = View.VISIBLE
                tvCountText.visibility = View.VISIBLE
                tvClickStartText.visibility = View.INVISIBLE
                linearTripEndDetails.visibility = View.GONE
                uploadProofLayout.visibility = View.GONE
                startTimer()
            }


        }


        binding.ivAddNewImage.setOnClickListener {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }


        binding.tvAddMoreImages.setOnClickListener {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imgFile = File(currentPhotoPath!!)
            if (imgFile.exists()) {
                fileUri = Uri.fromFile(imgFile)
                mArrayUriList.add(fileUri)

                bindCameraImagesAdapter(mArrayUriList)

                Log.e(TAG, "onActivityResult: $currentPhotoPath")
                //   imageView.setImageURI(Uri.fromFile(imgFile))
                // Here you can get the path of the image and upload it to your server
                // uploadImageToServer(currentPhotoPath!!)
            }
        }
    }


    private fun bindCameraImagesAdapter(mArrayUriList: ArrayList<Uri>) {

        if (mArrayUriList.size > 0) {
            binding.recyclerViewMoreImageLayout.visibility = View.VISIBLE
            binding.clickNewImageLayout.visibility = View.GONE
            val linearLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = UploadImageListAdapter(this, mArrayUriList, arrayOf(), ArrayList())
            binding.proofImageRecyclerView.layoutManager = linearLayoutManager
            binding.proofImageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            adapter.setOnItemClickListener { list, position , pdfList->

                if (position >= 0 && position < mArrayUriList.size) {
                    mArrayUriList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()

                }
                if (mArrayUriList.size > 0) {
                    binding.recyclerViewMoreImageLayout.visibility = View.VISIBLE
                    binding.clickNewImageLayout.visibility = View.GONE

                } else {
                    binding.recyclerViewMoreImageLayout.visibility = View.GONE
                    binding.clickNewImageLayout.visibility = View.VISIBLE
                }
            }
            adapter.notifyDataSetChanged()
        } else {
            binding.recyclerViewMoreImageLayout.visibility = View.GONE
            binding.clickNewImageLayout.visibility = View.VISIBLE
        }


    }


    private fun allPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "denied permissions case", Toast.LENGTH_SHORT)
                    .show()
                // Show an error message or handle denied permissions case
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", it)
//                    val photoURI: Uri = Uri.fromFile(it) //todo ==> using Uri.fromFile to create the URI for the photo file, which leads to a FileUriExposedException on Android 7.0 and above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                   /* val photoURI: Uri = Uri.fromFile(it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)*/
                }
            }
        }
    }

    private var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/Ahuja"
        )
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        val image = File.createTempFile(
            imageFileName,
            ".png",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    private var isRunning = false
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    private fun startTimer() {
        isRunning = true
        binding.btnTrip.text = "End trip"
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        isRunning = false
        binding.btnTrip.text = "Start trip"
        handler.removeCallbacks(timerRunnable)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime += 1000
                val hours = (elapsedTime / (1000 * 60 * 60)).toInt()
                val minutes = ((elapsedTime / (1000 * 60)) % 60).toInt()
                val seconds = ((elapsedTime / 1000) % 60).toInt()
                binding.tvCountText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }
}