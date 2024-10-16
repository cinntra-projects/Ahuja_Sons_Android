package com.ahuja.sons.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.CustomerEquipmentMVVMAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ParticularOrderDetailsBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.*
import com.ahuja.sons.newapimodel.DataOrderOne
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.collections.ArrayList


class ParticularorderDetailsActivity : MainBaseActivity(), View.OnClickListener {

    private lateinit var binding: ParticularOrderDetailsBinding

    //var orderDetails:OrderDataModel?=null
    lateinit var viewModel: MainViewModel

    var ticketid = ""


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
        binding = ParticularOrderDetailsBinding.inflate(layoutInflater)
        setUpViewModel()
        setContentView(binding.root)

        val toolbar: Toolbar = binding.newtoolbar.toolbar


        setSupportActionBar(toolbar)


        //    orderDetails= intent.getParcelableExtra<OrderDataModel>("order")!!

//        orderDetails?.let {
//
//        }

        //   Toast.makeText(this, orderDetails!!.CardCode.toString(),Toast.LENGTH_SHORT).show()

        binding.newtoolbar.heading.text = "Order Details"

        binding.newtoolbar.backPress.setOnClickListener {
            onBackPressed()
        }
        ticketid = intent.getStringExtra("OrderId")!!


        if (Global.checkForInternet(this)) {
            binding.loader.isVisible = true
            var hashMap = HashMap<String, String>()
            hashMap["id"] = ticketid
            viewModel.getOrderOne(hashMap)
            //calldetailapi()
        }

        binding.details.setOnClickListener(this)
        binding.items.setOnClickListener(this)

        subscribeToObserver()


//    fragments.add(new LeadsActivity());

    }

    private fun subscribeToObserver() {
        viewModel.orderOne.observe(this, Event.EventObserver(
            onError = {
                binding.loader.isVisible = false
//                Global.warningmessagetoast(this, it)
            }, onLoading = {
                binding.loader.isVisible = true
            }, {
                binding.loader.isVisible = false
                setData(it.data[0])

                val layoutManager =
                    LinearLayoutManager(
                        this@ParticularorderDetailsActivity,
                        RecyclerView.VERTICAL,
                        false
                    )
                val messageAdapter = CustomerEquipmentMVVMAdapter(it.data[0].DocumentLines as ArrayList<com.ahuja.sons.newapimodel.DocumentLine> /* = java.util.ArrayList<com.massaed.servicesupportportal.newapimodel.DocumentLine> */)
                // recyclerView.smoothScrollToPosition(recyclerView.getBottom());
                // recyclerView.smoothScrollToPosition(recyclerView.getBottom());
                binding.recyclerview.layoutManager = layoutManager
                binding.recyclerview.adapter = messageAdapter
                binding.loader.isVisible = false
                messageAdapter.notifyDataSetChanged()

            }

        ))
    }


    val messagelist = ArrayList<DocumentLine>()


//    private fun calldetailapi() {
//        val tickethistory = TicketHistoryData(
//            id = ticketid.toInt()
//        )
//
//        val call: Call<OrderDataResponse> =
//            ApiClient().service.getparticularorder(tickethistory)
//        call.enqueue(object : Callback<OrderDataResponse?> {
//            override fun onResponse(
//                call: Call<OrderDataResponse?>,
//                response: Response<OrderDataResponse?>
//            ) {
//                if (response.code() == 200) {
//
//
//                    setData(response.body()!!.data[0])
//                    messagelist.clear()
//                    messagelist.addAll(response.body()!!.data[0].DocumentLines)
//                    val layoutManager =
//                        LinearLayoutManager(
//                            this@ParticularorderDetailsActivity,
//                            RecyclerView.VERTICAL,
//                            false
//                        )
//                    val messageAdapter = CustomerEquipmentAdapter(messagelist)
//                    // recyclerView.smoothScrollToPosition(recyclerView.getBottom());
//                    // recyclerView.smoothScrollToPosition(recyclerView.getBottom());
//                    binding.recyclerview.layoutManager = layoutManager
//                    binding.recyclerview.adapter = messageAdapter
//                    binding.loader.isVisible = false
//                    messageAdapter.notifyDataSetChanged()
//
//                } else {
//                    binding.loader.isVisible = false
//                    Global.warningmessagetoast(
//                        this@ParticularorderDetailsActivity,
//                        response.errorBody().toString()
//                    );
//
//                }
//
//            }
//
//            override fun onFailure(call: Call<OrderDataResponse?>, t: Throwable) {
//                Toast.makeText(this@ParticularorderDetailsActivity, t.message, Toast.LENGTH_SHORT)
//                    .show()
//                binding.loader.isVisible = false
//            }
//        })
//    }

    private fun setData(detaildata: DataOrderOne) {
        if (detaildata.ContactPersonCode.isNotEmpty())

            binding.contacnameValue.text = detaildata.ContactPersonCode[0].FirstName
        binding.cardcode.text = detaildata.CardCode
        binding.createDate.text = detaildata.CreateDate
        binding.cardnameValue.text = detaildata.CardName
        binding.addressValue.text =
            detaildata.AddressExtension.BillToBuilding + " " + detaildata.AddressExtension.BillToCity

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

        /*   selectedback.background.setTint(resources.getColor(R.color.colorPrimary))
           unselectback.background.setTint(resources.getColor(R.color.white))*/

        selectedback.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
        unselectback.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))

    }

    /*  override fun onBackPressed()
      {

          if (supportFragmentManager.backStackEntryCount > 1) {
                  supportFragmentManager.popBackStack()
              } else {
                  super.onBackPressed()
              }

      }*/

}