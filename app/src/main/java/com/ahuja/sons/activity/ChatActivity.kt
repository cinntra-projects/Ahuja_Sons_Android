package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.adapter.MessageAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ChatterfragmentBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.TicketDataModel
import com.ahuja.sons.model.TicketHistoryData
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.collections.HashMap


class ChatActivity : MainBaseActivity() {

    private lateinit var binding: ChatterfragmentBinding
    var pageno = 1
    var recallApi = true
    var id = ""
    var status = ""

    var ticketdata = TicketDataModel()
    lateinit var viewModel: MainViewModel


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
        binding = ChatterfragmentBinding.inflate(layoutInflater)
        setUpViewModel()
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar.toolbar

        // using toolbar as ActionBar

        // using toolbar as ActionBar
        setSupportActionBar(toolbar)


        // Display application icon in the toolbar


        // Display application icon in the toolbar
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        id = intent?.getStringExtra(Global.INTENT_TICKET_ID)!!
        status = intent?.getStringExtra(Global.INTENT_TICKET_STATUS)!!
        Log.e(TAG, "onCreate: status $status")


        //  ticketdata = intent.getParcelableExtra<TicketDataModel>("TicketData")!!


        if (Global.checkForInternet(this) && recallApi) {
            pageno = 1
            recallApi = true
            messagelist.clear()
            binding.loadingview.start()

            val tickethistory = HashMap<String, Int>()
            tickethistory["TicketId"] = id.toInt()
            tickethistory["PageNo"] = pageno

            viewModel.getTicketConversation(tickethistory)
            bindConversationObserver()

        }

        binding.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {

                if (Global.checkForInternet(this) && recallApi) {
                    pageno++
                    binding.idPBLoading.visibility = View.VISIBLE

                    val tickethistory = HashMap<String, Int>()
                    tickethistory["TicketId"] = id.toInt()
                    tickethistory["PageNo"] = pageno

                    viewModel.getTicketConversation(tickethistory)
                    bindConversationObserver()

                }

            }
        })


        binding.send.setOnClickListener {
            if (binding.sendmessageText.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                val chatModel = TicketHistoryData(
                    Message = binding.sendmessageText.text.toString().trim { it <= ' ' },
                    OwnerId = Prefs.getString(Global.Employee_Code),
                    OwnerType = "Employee",
                    Type = "Public",
                    TicketId = id.toInt()
                )

                if (Global.checkForInternet(this)) {
                    viewModel.createTicketConversation(chatModel)
                    bindCreateConversationObserver()

                    Global.hideKeybaord(binding.sendmessageText, this)
                    binding.sendmessageText.setText("")
                }
            }
        }


    }

    //todo bind create conversation observer..

    private fun bindCreateConversationObserver() {
        viewModel.ticketAllHistory.observe(
            this, Event.EventObserver(
                onError = {
                    Log.e(TAG, "subsribeToObserverERROR===>:$it ")
                },
                onLoading = {

                },
                onSuccess = { ticket ->
                    if (ticket.status == 200) {
                        binding.loadingview.start()
                        pageno = 1
                        messagelist.clear()
                        recallApi = true

                        val tickethistory = HashMap<String, Int>()
                        tickethistory["TicketId"] = id.toInt()
                        tickethistory["PageNo"] = pageno

                        viewModel.getTicketConversation(tickethistory)
                        bindConversationObserver()
                    } else {
                        Global.warningmessagetoast(this@ChatActivity, ticket.message);
                    }

                })
        )
    }


    val messagelist = ArrayList<TicketHistoryData>()
    //todo bind observer..
    private fun bindConversationObserver() {
        viewModel.ticketAllHistory.observe(
            this, Event.EventObserver(
                onError = {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingview.stop()
                    binding.idPBLoading.visibility = View.GONE
                    Log.e(TAG, "subsribeToObserverERROR===>:$it ")
                },
                onLoading = {
                    binding.loadingback.visibility = View.VISIBLE
                    binding.loadingview.start()
                    binding.idPBLoading.visibility = View.VISIBLE
                },
                onSuccess = { ticket ->
                    if (ticket.status == 200) {
                        binding.loadingback.visibility = View.GONE
                        binding.loadingview.stop()
                        binding.idPBLoading.visibility = View.GONE

                        recallApi = ticket.data.isNotEmpty()
                        messagelist.addAll(ticket.data)
                        val layoutManager =
                            LinearLayoutManager(this@ChatActivity, RecyclerView.VERTICAL, false)
                        val messageAdapter = MessageAdapter(this@ChatActivity, messagelist)
                        layoutManager.stackFromEnd = true
                        // recyclerView.smoothScrollToPosition(recyclerView.getBottom());
                        // recyclerView.smoothScrollToPosition(recyclerView.getBottom());
                        binding.recyclerView.layoutManager = layoutManager
                        binding.recyclerView.adapter = messageAdapter

                        messageAdapter.notifyDataSetChanged()
                        binding.nodatafound.isVisible = messageAdapter.itemCount == 0
                    } else {
                        Global.warningmessagetoast(this@ChatActivity, ticket.message);
                    }

                })
        )
    }


    companion object {
        private const val TAG = "ChatActivity"
    }

    private fun subsribeToObserver() {
        viewModel.particularTicket.observe(this, Event.EventObserver(onError = {
            Log.e(TAG, "subsribeToObserverERROR===>:$it ")
        }, onLoading = {

        }, { ticket ->
            if (ticket.status == 200) {

            } else {

            }

        }))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }


        }
        return true
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