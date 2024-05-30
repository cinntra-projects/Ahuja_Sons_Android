package com.ahuja.sons.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ahuja.sons.R
import com.ahuja.sons.databinding.FragmentFullScreenEngineerDialogBinding
import com.ahuja.sons.`interface`.ImageEngineerSelectorListener

class FullScreenEngineerDialogFragment : DialogFragment() {

    lateinit var binding : FragmentFullScreenEngineerDialogBinding

    private var onImageSelectedListener: ImageEngineerSelectorListener? = null

    fun setOnImageSelectedListener(listener: ImageEngineerSelectorListener) {
        this.onImageSelectedListener = listener
    }

    // Inside your code where you handle image selection
    private fun selectImage(imageUri: Bitmap) {
        onImageSelectedListener?.onEngImageSelected(imageUri)
        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFullScreenEngineerDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearBtn.setOnClickListener {
            binding.drawView.clearCanvas()
        }

        binding.ivCancel.setOnClickListener {
            dismiss()
        }

        binding.saveBtn.setOnClickListener {
            selectImage(binding.drawView.getBitmap())
        }

    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }


}