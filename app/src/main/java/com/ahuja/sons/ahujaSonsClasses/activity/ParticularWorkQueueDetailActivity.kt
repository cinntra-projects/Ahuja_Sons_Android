package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahuja.sons.databinding.ActivityParticularWorkQueueDetailBinding

class ParticularWorkQueueDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityParticularWorkQueueDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityParticularWorkQueueDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}