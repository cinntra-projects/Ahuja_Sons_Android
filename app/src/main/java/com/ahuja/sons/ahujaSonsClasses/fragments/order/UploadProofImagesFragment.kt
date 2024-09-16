package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahuja.sons.BuildConfig
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.ahujaSonsClasses.adapter.UploadImageListAdapter
import com.ahuja.sons.databinding.FragmentUploadProofImagesBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UploadProofImagesFragment : Fragment() {

    lateinit var binding: FragmentUploadProofImagesBinding
    private val PICK_IMAGES_REQUEST_CODE = 1111
    private val REQUEST_CODE_PERMISSIONS = 10
    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    val mArrayUriList = ArrayList<Uri>()
    lateinit var fileUri: Uri
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 9999


    private fun checkAndRequestPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
        val writePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val listPermissionsNeeded = mutableListOf<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        perms[permissions[i]] = grantResults[i]
                    }
                    // Check for both permissions
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {
                        dispatchTakePictureIntent()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Some permissions are not granted. App cannot function without them.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    interface UploadItemClickListener {
        fun onUploadItemClick(mArrayUriList: ArrayList<Uri>)
    }

    companion object {
        private const val TAG = "UploadProofImagesFragme"
        private const val REQUEST_IMAGE_CAPTURE = 1

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUploadProofImagesBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linearAddImage.setOnClickListener {
            /*    if (allPermissionsGranted()) {
                    dispatchTakePictureIntent()
                } else {
                  *//*  ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSIONS
                )*//*
                dispatchTakePictureIntent()
            }*/

            if (checkAndRequestPermissions()) {
                dispatchTakePictureIntent()
            } else {
                dispatchTakePictureIntent()
                /*  ActivityCompat.requestPermissions(
                      requireActivity(),
                      arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                      REQUEST_CODE_PERMISSIONS
                  )*/
                //  dispatchTakePictureIntent()

            }
        }


        binding.tvAddMoreImages.setOnClickListener {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            } else {
                /*  ActivityCompat.requestPermissions(
                      requireActivity(),
                      arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                      REQUEST_CODE_PERMISSIONS
                  )*/
                dispatchTakePictureIntent()
            }
        }


    }


    @SuppressLint("SuspiciousIndentation")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(requireActivity(), "Error occurred while creating the file", Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    // The Android version is Nougat (7.0) or higher
                    // Perform actions for Android 7.0 or higher
                    val photoURI: Uri = FileProvider.getUriForFile(requireActivity(), "${BuildConfig.APPLICATION_ID}.fileprovider", it)
//                    val photoURI: Uri = Uri.fromFile(it) //todo getting error on Android Version 7 above
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

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


/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGES_REQUEST_CODE -> if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && null != data) {
                try {
                    *//*var photo = data?.extras?.get("data")
                    file = savebitmap(photo as Bitmap)
                    picturePath = file.path
                    fileUri = Uri.fromFile(file)
                    Log.e("fileUri---", fileUri.toString())

                    //todo add camera bitmap file uri photo in arraylist...
                    mArrayUriList.add(fileUri)
                    bindCameraImagesAdapter(mArrayUriList)*//*
                    val imgFile = File(currentPhotoPath)
                    if (imgFile.exists()) {
                        fileUri = Uri.fromFile(imgFile)
                        mArrayUriList.add(fileUri)

                        bindCameraImagesAdapter(mArrayUriList)
                        *//* imageView.setImageURI(Uri.fromFile(imgFile))
                         // Here you can get the path of the image and upload it to your server
                         uploadImageToServer(currentPhotoPath!!)*//*
                        Log.e(TAG, "onActivityResult: $currentPhotoPath")
                    }

                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

            } else {
                // Failed to take picture
                Toast.makeText(
                    requireContext(),
                    "Failed to take camera picture",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }*/


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
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = UploadImageListAdapter(context, mArrayUriList, arrayOf(), ArrayList())
            binding.proofImageRecyclerView.layoutManager = linearLayoutManager
            binding.proofImageRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            adapter.setOnItemClickListener { list, position, pdfLisr ->

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

    //todo to convert bitmap to file--
    private fun savebitmap(bmp: Bitmap): File {
//        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        var extStorageDirectory =
            requireContext().cacheDir //todo this is use for temporairy file path storage //"/storage/emulated/0/Download"
        var outStream: OutputStream? = null
        val num: Int = random.nextInt(90) + 10

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
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    /*   override fun onRequestPermissionsResult(
           requestCode: Int,
           permissions: Array<String>,
           grantResults: IntArray
       ) {
           if (requestCode == REQUEST_CODE_PERMISSIONS) {
               if (allPermissionsGranted()) {
                   dispatchTakePictureIntent()
               } else {
                   Toast.makeText(requireContext(), "denied permissions case", Toast.LENGTH_SHORT)
                       .show()
                   // Show an error message or handle denied permissions case
               }
         */


}