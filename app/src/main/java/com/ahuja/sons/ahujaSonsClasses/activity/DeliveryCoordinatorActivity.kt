package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahuja.sons.databinding.ActivityDeliveryCoordinatorBinding

class DeliveryCoordinatorActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeliveryCoordinatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}