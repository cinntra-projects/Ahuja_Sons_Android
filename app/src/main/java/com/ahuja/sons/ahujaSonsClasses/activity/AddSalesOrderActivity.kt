package com.ahuja.sons.ahujaSonsClasses.activity

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.ahuja.sons.databinding.ActivityAddSalesOrderBinding

class AddSalesOrderActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddSalesOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSalesOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        binding.loadingback.visibility = View.GONE
        binding.loadingView.stop()
        binding.toolbar.heading.text = "Add Order"

        binding.submitChip.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.toolbar.search.visibility=View.GONE


        val order = arrayOf("Hospital Name 1", "Hospital Name 2", "Hospital Name 3")
        val orderInfoAdapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, order)
        binding.acHospitalName.setAdapter(orderInfoAdapter)

        val list = arrayOf("Doctor Name 1", "Doctor Name 2", "Doctor Name 3")
        val doctorAdapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, list)
        binding.acDoctorName.setAdapter(doctorAdapter)


    }


}