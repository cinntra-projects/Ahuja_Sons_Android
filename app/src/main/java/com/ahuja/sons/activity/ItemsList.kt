package com.ahuja.sons.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ItemsAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityItemBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.DocumentLine
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ItemsList : MainBaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityItemBinding
    lateinit var layoutManager: LinearLayoutManager

    lateinit var viewModel: MainViewModel

    var AllitemsList = ArrayList<DocumentLine>()

    lateinit var adapter: ItemsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        if (actionBar != null){
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayUseLogoEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }

        setDefaults()

    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    var ticketID = ""
    private fun setDefaults() {
        binding.toolbarview.heading.text = "Item List"
        binding.toolbarview.backPress.setOnClickListener(this)
        val fromwhere = java.lang.String.valueOf(intent.extras!!.getInt("CategoryID"))
        ticketID = java.lang.String.valueOf(intent.extras!!.getString("ticketId"))
        // Toast.makeText(this,fromwhere,Toast.LENGTH_SHORT).show();

        binding.loadingback.visibility = View.VISIBLE
        if (Global.checkForInternet(applicationContext)) {
            val id = DocumentLine(
                ItemsGroupCode = fromwhere
            )

            viewModel.getItemlist(id)
            bindObserver()
//            callApi(fromwhere)
        }


    }

    private fun bindObserver() {
        viewModel.getAllItemList.observe(this, Event.EventObserver(
            onError = {
                binding.loadingback.visibility = View.GONE
                binding.noDatafound.visibility = View.VISIBLE
                Log.e("fail", it)
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {
                binding.loadingback.visibility = View.VISIBLE
            },
            onSuccess = {
                if (it.status == 200) {
                    if (it.data.isEmpty()) {
                        binding.noDatafound.visibility = View.VISIBLE
                    }else {
                        Log.e("data", it.data.toString())
                        binding.noDatafound.visibility = View.GONE
                        binding.loadingback.visibility = View.GONE
                        layoutManager = LinearLayoutManager(this@ItemsList, RecyclerView.VERTICAL, false)
                        AllitemsList.clear()
                        AllitemsList.addAll(it.data)
                        binding.itemsRecycler.layoutManager = layoutManager
                        adapter = ItemsAdapter(this@ItemsList, AllitemsList, ticketID)
                        binding.itemsRecycler.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }

                } else {
                    Global.warningmessagetoast(this@ItemsList, it.toString());

                }

            }

        ))
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.back_press -> {

            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_filter, menu)
        val item = menu.findItem(R.id.search)
        val searchView = SearchView(
            supportActionBar!!.themedContext
        )
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        item.actionView = searchView
        searchView.queryHint = "Search Item"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter(newText)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()

            }
        }
        return true
    }

}