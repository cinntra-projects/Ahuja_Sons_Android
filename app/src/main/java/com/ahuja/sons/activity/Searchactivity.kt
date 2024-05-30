package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import co.lujun.androidtagview.TagView
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.SearchPageBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.Global.taglist
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.newapimodel.ResponseTicket
import com.ahuja.sons.newapimodel.TicketData
import com.ahuja.sons.recyclerviewadapter.TicketNewAdapter
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Searchactivity : MainBaseActivity() {


    private lateinit var searchPage: SearchPageBinding

    var AllitemsList = ArrayList<TicketData>()
    lateinit var adapter: TicketNewAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchPage = SearchPageBinding.inflate(layoutInflater)
        setContentView(searchPage.root)

        setUpViewModel()

        searchPage.loadingback.visibility = View.GONE
        searchPage.backPress.setOnClickListener {
            onBackPressed()
        }


        searchPage.clearall.setOnClickListener {
            searchPage.tagview.removeAllTags()
        }



        searchPage.tagview.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {
                searchPage.loadingView.start()

                callSearchApi(text.toString())
            }

            override fun onTagLongClick(position: Int, text: String?) {
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
            }

            override fun onTagCrossClick(position: Int) {
                taglist.removeAt(position)
                searchPage.tagview.removeTag(position)
            }

        })
        searchPage.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.toString().isNotEmpty()) {
                    Log.e(TAG, "onQueryTextSubmit: $query")
                    searchPage.loadingView.start()
                    callSearchApi(query)

                }


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.toString().isNotEmpty()) {
                    Log.e(TAG, "onQueryTextChange: $newText")

                    searchPage.loadingView.start()

                    callSearchApi(newText)
                }
                return false
            }

        })


    }

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    //todo bind observer...
    private fun bindObserver(query: String) {
        viewModel.searchTicketsItem.observe(this, Event.EventObserver(
            onError = {
                searchPage.loadingback.visibility = View.GONE
                searchPage.loadingView.stop()
                Global.warningmessagetoast(this, it)
            },
            onLoading = {
                searchPage.loadingback.visibility = View.VISIBLE
                searchPage.loadingView.start()
            },
            onSuccess = {
                    response ->
                if (response.status == 200){
                    searchPage.loadingView.stop()
                    searchPage.loadingback.visibility = View.GONE
                    AllitemsList.clear()
                    AllitemsList.addAll(response!!.data)
                    linearLayoutManager = LinearLayoutManager(
                        this@Searchactivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = TicketNewAdapter(AllitemsList)
                    searchPage.recyclerview.layoutManager = linearLayoutManager
                    searchPage.recyclerview.adapter = adapter
                    adapter.notifyDataSetChanged()
                    if (searchPage.tagview.tags.size < 5) {
                        taglist.add(query)
                        searchPage.tagview.tags = taglist
                    }
                    searchPage.nodatafound.isVisible = adapter.itemCount == 0
                }else {
                    Global.warningmessagetoast(this@Searchactivity, response?.message.toString())

                }
            }

        ))


    }



    companion object {
        private const val TAG = "Searchactivity"
    }


    //todo comment by chanchal...
    private fun callSearchApi(query: String) {
        val data = HashMap<String, Any>()
        data["EmployeeId"] = Prefs.getString(Global.Employee_Code)
        data["PageNo"] = 1
        data["SearchText"] = query

        Log.e("payload", data.toString())
        val call: Call<ResponseTicket> = ApiClient().service.searchApi(data)
        call.enqueue(object : Callback<ResponseTicket> {
            override fun onResponse(
                call: Call<ResponseTicket>,
                response: Response<ResponseTicket>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.status == 200) {
                        searchPage.loadingback.visibility = View.GONE
                        AllitemsList.clear()
                        AllitemsList.addAll(response.body()!!.data)
                        linearLayoutManager = LinearLayoutManager(
                            this@Searchactivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        adapter = TicketNewAdapter(AllitemsList)
                        searchPage.recyclerview.layoutManager = linearLayoutManager
                        searchPage.recyclerview.adapter = adapter
                        adapter.notifyDataSetChanged()
                        if (searchPage.tagview.tags.size < 5) {
                            taglist.add(query)
                            searchPage.tagview.tags = taglist
                        }
                        searchPage.nodatafound.isVisible = adapter.itemCount == 0

                    }


                } else {
                    Global.warningmessagetoast(this@Searchactivity, response.body()?.message.toString())

                }
                searchPage.loadingback.visibility = View.GONE

                searchPage.loadingView.stop()
            }

            override fun onFailure(call: Call<ResponseTicket>, t: Throwable) {
                Global.errormessagetoast(this@Searchactivity, t.message.toString())
                searchPage.loadingback.visibility = View.GONE

                searchPage.loadingView.stop()
            }
        })
    }

}




