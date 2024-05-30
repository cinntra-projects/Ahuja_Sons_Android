package com.ahuja.sons.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.adapter.NotificationAdapter
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityNotificationBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.`interface`.FragmentRefresher
import com.ahuja.sons.newapimodel.NotificationListResponseModel
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() , FragmentRefresher{
    lateinit var binding:ActivityNotificationBinding
    lateinit var viewModel: MainViewModel
    var pageno = 1
    var isScrollingpage: Boolean = false
    var maxItem = 10
    var recallApi = true
    lateinit var adapter: NotificationAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory = MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()

        binding.loadingback.visibility = View.GONE

        binding.headerLayout.backPress.setOnClickListener{
            onBackPressed()
            finish()
        }

        if (Global.checkForInternet(this)){
            callApi(pageno)
            setAdapter()
        }


        binding.swipeContainer.setOnRefreshListener {
            if (Global.checkForInternet(this)){
                pageno = 1
//                recallApi = true
//                arrayList_gl.clear()
                callApi(pageno)
            }else{
                binding.swipeContainer.setRefreshing(false)
            }
        }


        binding.notificationList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleitempositon: Int = (linearLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition() //first item

                val visibleItemCount: Int = (linearLayoutManager as LinearLayoutManager).getChildCount() //total number of visible item

                val totalItemCount: Int = (linearLayoutManager as LinearLayoutManager).getItemCount() //total number of item

                if (isScrollingpage && visibleItemCount + firstVisibleitempositon == totalItemCount) {
                    pageno++
                    Log.e("page--->", pageno.toString())
                    callAllPageAPi(pageno)
                    isScrollingpage = false
                } else {
                    recyclerView.setPadding(0, 0, 0, 0);
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //it means we are scrolling
                    isScrollingpage = true

                }
            }
        })




    }

    var arrayList_gl : ArrayList<NotificationListResponseModel.DataXXX> = ArrayList()

    private fun callApi(pageno: Int) {
        binding.loadingback.visibility = View.VISIBLE
        binding.loadingView.start()

        var jsonObject = JsonObject()
        var js = JsonObject()
        js.addProperty("FinalStatus", "")
        jsonObject.addProperty("Emp", Prefs.getString(Global.Employee_Code))
        jsonObject.addProperty("PageNo", pageno)
        jsonObject.addProperty("maxItem", "10")
        jsonObject.addProperty("field", js.toString())

        val call: Call<NotificationListResponseModel> = ApiClient().service.getAllNotificationList(jsonObject)
        call.enqueue(object : Callback<NotificationListResponseModel> {
            override fun onResponse(
                call: Call<NotificationListResponseModel>,
                response: Response<NotificationListResponseModel>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.status == 200) {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        binding.swipeContainer.setRefreshing(false)

                        if (response.body()?.data.isNullOrEmpty()) {
                            arrayList_gl.clear()
                            arrayList_gl.addAll(response.body()?.data!!)
                            setAdapter()
                            binding.noNoti.visibility = View.VISIBLE
                            binding.notiDis.visibility = View.GONE
                        }

                        else {
                            val valueList = response.body()?.data
                            if (pageno == 1) {
                                arrayList_gl.clear()
                                arrayList_gl.addAll(valueList!!)
                            } else {
                                arrayList_gl.addAll(valueList!!)
                            }
                            setAdapter()
                            binding.noNoti.visibility = View.GONE

                            binding.swipeContainer.setRefreshing(false)

                            adapter.notifyDataSetChanged()

                            if (valueList.size < 10) {
                                recallApi = false
                            }
                        }

                    }
                    else if (response.body()!!.status == 201) {
                        binding.loadingback.visibility = View.GONE
                        binding.loadingView.stop()
                        Global.warningmessagetoast(this@NotificationActivity, response.body()!!.message)
                    }
                    else {
                        binding.loadingView.stop()
                        binding.loadingback.visibility = View.GONE
                        Global.warningmessagetoast(this@NotificationActivity, response.body()!!.message)
                    }

                }
               else {
                    binding.loadingback.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(this@NotificationActivity, response.body()!!.message)
                }

            }

            override fun onFailure(call: Call<NotificationListResponseModel>, t: Throwable) {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                binding.swipeContainer.setRefreshing(false)

            }
        })


    }



    //todo calling customerList api ---
    private fun callAllPageAPi(page: Int) {
        binding.loadingback.visibility = View.VISIBLE
        binding.loadingView.start()

        var jsonObject = JsonObject()
        var js = JsonObject()
        js.addProperty("FinalStatus", "")
        jsonObject.addProperty("Emp", Prefs.getString(Global.Employee_Code))
        jsonObject.addProperty("PageNo", pageno)
        jsonObject.addProperty("maxItem", "10")
        jsonObject.addProperty("field", js.toString())

        val call: Call<NotificationListResponseModel> = ApiClient().service.getAllNotificationList(jsonObject)
        call.enqueue(object : Callback<NotificationListResponseModel> {
            override fun onResponse(
                call: Call<NotificationListResponseModel>,
                response: Response<NotificationListResponseModel>
            ) {
                if (response.body()?.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    binding.swipeContainer.setRefreshing(false)
                    if (response.body()?.data.isNullOrEmpty()) {

                        arrayList_gl.addAll(response.body()?.data!!)
                        adapter.notifyDataSetChanged()

                    } else {
                        val valueList = response.body()?.data
                        if (page == 1) {
                            arrayList_gl.clear()
                            arrayList_gl.addAll(valueList!!)
                        } else {
                            arrayList_gl.addAll(valueList!!)
                        }

                        binding.swipeContainer.setRefreshing(false)
                        adapter.notifyDataSetChanged()

                        if (valueList.size < 10) {
                            recallApi = false
                        }
                    }

                } else {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(this@NotificationActivity, response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<NotificationListResponseModel>, t: Throwable) {
                binding.loadingback.visibility = View.GONE
                binding.loadingView.stop()
                binding.swipeContainer.setRefreshing(false)

                this?.let { t.message?.let { it1 -> Global.errormessagetoast(this@NotificationActivity, it1) } }

            }
        })
    }


    private fun setAdapter() {
        linearLayoutManager = LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
        adapter = NotificationAdapter(this@NotificationActivity, arrayList_gl, this@NotificationActivity)
        binding.notificationList.layoutManager = linearLayoutManager
        binding.notificationList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onRefresh() {

       /* if (Global.checkForInternet(this)){
            pageno = 1
            recallApi = true
            arrayList_gl.clear()
            callApi(pageno)
        }*/

        if (Global.checkForInternet(this)) {
            callApi(pageno)
        }
    }

}