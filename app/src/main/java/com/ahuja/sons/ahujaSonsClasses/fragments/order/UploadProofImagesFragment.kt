package com.ahuja.sons.ahujaSonsClasses.fragments.order

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.ahujaSonsClasses.adapter.UploadImageListAdapter
import com.ahuja.sons.databinding.FragmentUploadProofImagesBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class UploadProofImagesFragment : Fragment() {

    lateinit var binding : FragmentUploadProofImagesBinding
    private val PICK_IMAGES_REQUEST_CODE = 1
    private  val REQUEST_CODE_PERMISSIONS = 10
    lateinit var file: File
    lateinit var picturePath: String
    var random: Random = Random()
    val mArrayUriList = ArrayList<Uri>()
    lateinit var fileUri: Uri

    interface UploadItemClickListener {
        fun onUploadItemClick(mArrayUriList: ArrayList<Uri>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentUploadProofImagesBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivAddNewImage.setOnClickListener {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            }else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }
        }


        binding.tvAddMoreImages.setOnClickListener {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            }else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }
        }


    }



    private fun dispatchTakePictureIntent() {
        var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, PICK_IMAGES_REQUEST_CODE)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_IMAGES_REQUEST_CODE-> if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && null != data){
                try {
                    var photo = data?.extras?.get("data")
                    file = savebitmap(photo as Bitmap)
                    picturePath = file.path
                    fileUri = Uri.fromFile(file)
                    Log.e("fileUri---", fileUri.toString())

                    //todo add camera bitmap file uri photo in arraylist...
                    mArrayUriList.add(fileUri)
                    bindCameraImagesAdapter(mArrayUriList)

                }catch (e:NullPointerException){
                    e.printStackTrace()
                }

            }else {
                // Failed to take picture
                Toast.makeText(requireContext(), "Failed to take camera picture", Toast.LENGTH_SHORT).show()

            }
        }

    }


    private fun bindCameraImagesAdapter(mArrayUriList: ArrayList<Uri>) {

        if (mArrayUriList.size > 0){
            binding.recyclerViewMoreImageLayout.visibility = View.VISIBLE
            binding.clickNewImageLayout.visibility = View.GONE
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = UploadImageListAdapter(context, mArrayUriList, arrayOf())
            binding.proofImageRecyclerView.layoutManager = linearLayoutManager
            binding.proofImageRecyclerView.adapter = adapter

            adapter.setOnItemClickListener { list, position ->

                if (position >= 0 && position < mArrayUriList.size) {
                    mArrayUriList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()

                }
                if (mArrayUriList.size > 0) {
                    binding.recyclerViewMoreImageLayout.visibility = View.VISIBLE
                    binding.clickNewImageLayout.visibility = View.GONE

                }else{
                    binding.recyclerViewMoreImageLayout.visibility = View.GONE
                    binding.clickNewImageLayout.visibility = View.VISIBLE
                }
            }
            adapter.notifyDataSetChanged()
        }else{
            binding.recyclerViewMoreImageLayout.visibility = View.GONE
            binding.clickNewImageLayout.visibility = View.VISIBLE
        }


    }

    //todo to convert bitmap to file--
    private fun savebitmap(bmp: Bitmap): File {
//        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        var extStorageDirectory = requireContext().cacheDir //todo this is use for temporairy file path storage //"/storage/emulated/0/Download"
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
            if (outStream!= null) {
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
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(requireContext(), "denied permissions case", Toast.LENGTH_SHORT).show()
                // Show an error message or handle denied permissions case
            }
        }
    }




}