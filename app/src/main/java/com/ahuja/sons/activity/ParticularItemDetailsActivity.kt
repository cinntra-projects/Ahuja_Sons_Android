package com.ahuja.sons.activity


import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.OrderEquipmentAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ParticularItemDetailsBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.collections.ArrayList


class ParticularItemDetailsActivity : MainBaseActivity(), View.OnClickListener {

    private lateinit var binding: ParticularItemDetailsBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    var ticketid = ""
    lateinit var viewModel: MainViewModel


    private fun setUpLoadingDialog() {
        builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_progress_dialog)
        dialog = builder.create()
    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ParticularItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        setUpLoadingDialog()
        val toolbar: Toolbar = binding.newtoolbar.toolbar

        // using toolbar as ActionBar
        setSupportActionBar(toolbar)
        binding.items.text = "Tickets"
        binding.newtoolbar.heading.text = "Item Details"

        binding.newtoolbar.backPress.setOnClickListener {
            onBackPressed()
        }
        ticketid = intent.getStringExtra("ProductSerialNo")!!


        if (Global.checkForInternet(this)) {
            dialog.show()
            val tickethistory = TicketDataModel(
                ProductSerialNo = ticketid
            )
            viewModel.getitemwiseticket(tickethistory)
            bindObserver()
        }

        binding.details.setOnClickListener(this)
        binding.items.setOnClickListener(this)


    }

    val messagelist = ArrayList<TicketData>()
    private fun bindObserver() {
        viewModel.allItemWiseTicket.observe(
            this, Event.EventObserver(
                onError = {
                    dialog.dismiss()
                    Log.e("ProductTicket===>", "subsribeToObserverERROR===>:$it ")
                },
                onLoading = {
                    dialog.dismiss()
                },
                onSuccess = { ticket ->
                    if (ticket.status == 200) {
                        dialog.dismiss()
                        if (ticket.data.isNotEmpty()) {
                            setData(ticket.data[0])
                            messagelist.clear()
                            messagelist.addAll(ticket.data)
                        } else {
                            Toast.makeText(
                                this@ParticularItemDetailsActivity,
                                "No Data Found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        val layoutManager = LinearLayoutManager(
                            this@ParticularItemDetailsActivity,
                            RecyclerView.VERTICAL,
                            false
                        )
                        val messageAdapter = OrderEquipmentAdapter(messagelist)
                        binding.recyclerview.layoutManager = layoutManager
                        binding.recyclerview.adapter = messageAdapter

                        messageAdapter.notifyDataSetChanged()
                    } else {
                        Global.warningmessagetoast(
                            this@ParticularItemDetailsActivity,
                            ticket.message
                        )
                        dialog.dismiss()
                    }

                })
        )
    }


    private fun setData(detaildata: TicketData) {
        binding.cardnameValue.text = detaildata.ProductName
        binding.cardcode.text = detaildata.ProductModelNo
        binding.serialNum.text = detaildata.CreateDate
        binding.itemPriceValue.text = detaildata.Status
        binding.category.text = detaildata.ProductCategoryName
        binding.addressValue.text = detaildata.BusinessPartner[0].CardName
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.details -> {
                binding.detailFrame.isVisible = true
                binding.itemFrame.isVisible = false
                changebackground(binding.details, binding.items)
            }
            R.id.items -> {
                binding.detailFrame.isVisible = false
                binding.itemFrame.isVisible = true
                changebackground(binding.items, binding.details)
            }
        }
    }

    private fun changebackground(selectedback: TextView, unselectback: TextView) {
        selectedback.setTextColor(resources.getColor(R.color.white))
        unselectback.setTextColor(resources.getColor(R.color.black))

        selectedback.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
        unselectback.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))

    }


    companion object{
        private const val TAG = "ParticularItemDetailsAc"
    }

}

