package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahuja.sons.databinding.ActivityAddErrandAcitivtyBinding

class AddErrandActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddErrandAcitivtyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddErrandAcitivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.toolbar.setOnClickListener {
            finish()
        }

        binding.toolbar.heading.setText("Errand")
    }
}