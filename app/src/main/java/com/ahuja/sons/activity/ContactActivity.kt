package com.ahuja.sons.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.adapter.CustomerContactAdpater
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ContactActivtyBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*


class ContactActivity : MainBaseActivity() {

    private lateinit var binding: ContactActivtyBinding

    var ticketdata = TicketDataModel()
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContactActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        val toolbar: Toolbar = binding.toolbar.toolbar

        // using toolbar as ActionBar
        setSupportActionBar(toolbar)
        binding.toolbar.heading.text = "Contacts"
        binding.toolbar.backPress.setOnClickListener {
            onBackPressed()
        }

        if (Global.checkForInternet(this)) {
            binding.loadingview.start()
//            setData()

            viewModel.getallcontact()
            bindObserver()

        }

        binding.addcontact.setOnClickListener {
            val intent = Intent(this, AddContactPerson::class.java)
            startActivity(intent)
        }


    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    val messagelist = ArrayList<ContactEmployee>()


    //todo sales empoyee list
    private fun bindObserver() {
        viewModel.contactResponse.observe(this, Event.EventObserver(
            onError = {
                binding.loadingview.stop()
                binding.loadingback.visibility = View.GONE
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                binding.loadingview.start()
                binding.loadingback.visibility = View.VISIBLE
            },
            onSuccess = {
                if (it.status == 200) {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    messagelist.clear()
                    messagelist.addAll(it.data)
                    val layoutManager =
                        LinearLayoutManager(this@ContactActivity, RecyclerView.VERTICAL, false)
                    val messageAdapter = CustomerContactAdpater(messagelist)
                    binding.recyclerView.layoutManager = layoutManager
                    binding.recyclerView.adapter = messageAdapter

                    messageAdapter.notifyDataSetChanged()
                    if (messagelist.isEmpty()) {
                        binding.nodatafound.visibility = View.VISIBLE
                    }

                } else {
                    Global.warningmessagetoast(this@ContactActivity, it.toString());

                }

            }

        ))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }


}