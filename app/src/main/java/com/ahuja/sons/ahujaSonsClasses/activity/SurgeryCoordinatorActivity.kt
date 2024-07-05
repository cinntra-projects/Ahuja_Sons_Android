package com.ahuja.sons.ahujaSonsClasses.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.ahujaSonsClasses.adapter.AddSurgeryPersonAdapter
import com.ahuja.sons.ahujaSonsClasses.adapter.SurgeryPersonListingAdapter
import com.ahuja.sons.ahujaSonsClasses.model.SurgeryPersonModelData
import com.ahuja.sons.databinding.ActivitySurgeryCoordinatorBinding
import com.ahuja.sons.globals.Global

class SurgeryCoordinatorActivity : AppCompatActivity() {
    var addSurgeryPersonAdapter: AddSurgeryPersonAdapter? = null
    var surgeryPersonListAdapter: SurgeryPersonListingAdapter? = null

    var surgeryPersonList = mutableListOf<SurgeryPersonModelData>()
    lateinit var binding: ActivitySurgeryCoordinatorBinding
    var content = ""
    var mutableListSurgery = mutableListOf<String>("Shubham", "Chanchal", "Ankit", "Arif")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurgeryCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnClickListener {
            finish()
        }

        addSurgeryPersonAdapter =
            AddSurgeryPersonAdapter(this@SurgeryCoordinatorActivity, surgeryPersonList)
        bindFocItemAdapter()
        binding.addSurgeryPerson.setOnClickListener {
            val newItem = SurgeryPersonModelData(
                str = "FOC"

            )
            addSurgeryPersonAdapter!!.addItem(newItem)
            content = ""
            binding.rvSurgeryPersons.scrollToPosition(surgeryPersonList.size)
        }

        var autocompleteAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mutableListSurgery
        )
        binding.acSurgeryPerson1.setAdapter(autocompleteAdapter)
        binding.acSurgeryPerson2.setAdapter(autocompleteAdapter)

        binding.acSurgeryPerson1.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Global.successmessagetoast(
                    this@SurgeryCoordinatorActivity,
                    mutableListSurgery[p2].toString()
                )
                binding.acSurgeryPerson1.setText(" ${mutableListSurgery[p2].toString()}")
                var autocompleteAdapter = ArrayAdapter<String>(
                    this@SurgeryCoordinatorActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    mutableListSurgery
                )
                binding.acSurgeryPerson1.setAdapter(autocompleteAdapter)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.acSurgeryPerson2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Global.successmessagetoast(
                    this@SurgeryCoordinatorActivity,
                    mutableListSurgery[p2].toString()
                )
                binding.acSurgeryPerson2.setText(" ${mutableListSurgery[p2].toString()}")
                var autocompleteAdapter = ArrayAdapter<String>(
                    this@SurgeryCoordinatorActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    mutableListSurgery
                )
                binding.acSurgeryPerson2.setAdapter(autocompleteAdapter)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.btnSubmit.setOnClickListener {
            binding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.GONE
            binding.surgeryDetailLinearLayout.visibility = View.VISIBLE
            surgeryPersonListAdapter = SurgeryPersonListingAdapter(this, surgeryPersonList)
            binSurgeryPersonListAdapter()
            surgeryPersonListAdapter!!.notifyDataSetChanged()


        }

        binding.tvEdit.setOnClickListener {
            binding.fillOutFormForSurgeryPersonLinearLayout.visibility = View.VISIBLE
            binding.surgeryDetailLinearLayout.visibility = View.GONE


        }
    }


    //todo bind foc items parts adapter--
    private fun bindFocItemAdapter() = binding.rvSurgeryPersons.apply {
        adapter = addSurgeryPersonAdapter
        layoutManager = LinearLayoutManager(this@SurgeryCoordinatorActivity)

        //todo remove foc items--
        if (addSurgeryPersonAdapter != null) {
            addSurgeryPersonAdapter!!.setOnItemMinusClickListener { s, i ->
                if (addSurgeryPersonAdapter!!.itemCount > 0) {
                    addSurgeryPersonAdapter!!.removeItem(i)
                }
            }
        }

    }


    //todo bind foc items parts adapter--
    private fun binSurgeryPersonListAdapter() = binding.rvSurgeryPersonsList.apply {
        adapter = surgeryPersonListAdapter
        layoutManager = LinearLayoutManager(this@SurgeryCoordinatorActivity)


    }
}