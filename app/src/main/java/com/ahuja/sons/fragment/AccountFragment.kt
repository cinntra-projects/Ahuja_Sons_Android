package com.ahuja.sons.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baoyz.widget.PullRefreshLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.R
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.adapter.AccountAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.databinding.FragmentAccoutBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBPResponse
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.newapimodel.CustomerRequestModel
import com.ahuja.sons.viewmodel.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {

    lateinit var adapter: AccountAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var binding: FragmentAccoutBinding

    //    private lateinit var binding: DemoLayoutBinding
    lateinit var viewModel: MainViewModel
    var page = 1
    var apicall: Boolean = true
    var isScrollingpage: Boolean = false
    var maxItem = 10
    var searchText = ""
    var AllitemsList = ArrayList<AccountBpData>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        (activity as AppCompatActivity?)!!.findViewById<CollapsingToolbarLayout>(R.id.collapsetoolbar).visibility =
            View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccoutBinding.inflate(layoutInflater)

        //  Toast.makeText(requireContext(), Prefs.getString(Global.Employee_Code,""), Toast.LENGTH_SHORT).show()

        viewModel = (activity as MainActivity).viewModel

        if (Global.checkForInternet(requireContext())) {
            /*  viewModel.getAllBPList()
              bindObserver()*/

            callCustomerListAPi(page)
            setAdapter()
        }

        binding.addTicket.setOnClickListener {
            if (Prefs.getString(Global.Employee_role) == "admin" || Prefs.getString(Global.Employee_role) == "support manager") {
                val intent = Intent(context, AddTicketActivity::class.java)
                startActivity(intent)
            } else {
                Global.warningmessagetoast(
                    requireContext(),
                    "You have no authorization to create Business Partner"
                )
            }
        }


        binding.toolbar.search.setOnClickListener {
            if (binding.searchView.isVisible) {
                binding.searchView.visibility = View.GONE
            } else {
                binding.searchView.visibility = View.VISIBLE
            }
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    newText.lowercase()
                }
                Log.e(TAG, "onQueryTextChange: $newText")
                if (adapter != null) {
                    adapter.filter(newText!!)
                }
                return true
            }

        })


        //todo recycler view scrollListener for add more items in list...
        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var lastCompletelyVisibleItemPosition =
                    (linearLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                /*if (isScrollingpage && lastCompletelyVisibleItemPosition == AllitemsList.size - 1 && apicall) {
                    page++
                    Log.e("page--->", page.toString())
                    callCustomerListAPi(page)
                    isScrollingpage = false
                } else {
                    recyclerView.setPadding(0, 0, 0, 0);
                }*/

                val firstVisibleitempositon: Int = (linearLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition() //first item

                val visibleItemCount: Int = (linearLayoutManager as LinearLayoutManager).getChildCount() //total number of visible item

                val totalItemCount: Int = (linearLayoutManager as LinearLayoutManager).getItemCount() //total number of item

                if (isScrollingpage && visibleItemCount + firstVisibleitempositon == totalItemCount) {
                    page++
                    Log.e("page--->", page.toString())
                    callCustomerListAllPageAPi(page)
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


        binding.ssPullRefresh.setOnRefreshListener(object : PullRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                binding.searchView.clearFocus()
                binding.searchView.visibility = View.GONE

                if (Global.checkForInternet(requireContext())) {
                    page = 1
                    searchText = ""
                    callCustomerListAPi(page)

                } else {
                    binding.ssPullRefresh.setRefreshing(false)
                }
            }
        })


        return binding.root
    }

    //todo calling customerList api ---
    private fun callCustomerListAPi(page: Int) {
        binding.loadingView.start()
        binding.loadingback.visibility = View.VISIBLE

        var field = CustomerRequestModel.Field(FinalStatus = "")
        var requestModel = CustomerRequestModel(
            PageNo = page,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = searchText,
            field = field,
            maxItem = maxItem
        )

        val call: Call<AccountBPResponse> = ApiClient().service.getCustomerAllList(requestModel)
        call.enqueue(object : Callback<AccountBPResponse> {
            override fun onResponse(
                call: Call<AccountBPResponse>,
                response: Response<AccountBPResponse>
            ) {
                if (response.body()?.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    binding.ssPullRefresh.setRefreshing(false)
                    if (response.body()?.data.isNullOrEmpty()) {
                        AllitemsList.clear()
                        AllitemsList.addAll(response.body()?.data!!)
                        setAdapter()
                        binding.nodatafound.visibility = View.VISIBLE
                    } else {
                        val valueList = response.body()?.data
                        if (page == 1) {
                            AllitemsList.clear()
                            AllitemsList.addAll(valueList!!)
                            adapter.AllData(valueList)
                        } else {
                            AllitemsList.addAll(valueList!!)
                            adapter.AllData(valueList)
                        }
                        setAdapter()
                        binding.ssPullRefresh.setRefreshing(false)
                        adapter.notifyDataSetChanged()
                        binding.nodatafound.visibility = View.GONE

                        if (valueList.size < 10) {
                            apicall = false
                        }
                    }

                } else {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<AccountBPResponse>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingback.visibility = View.GONE

                context?.let { t.message?.let { it1 -> Global.errormessagetoast(it, it1) } }
                binding.ssPullRefresh.setRefreshing(false)
            }
        })
    }


    //todo calling customerList api ---
    private fun callCustomerListAllPageAPi(page: Int) {
        binding.loadingView.start()
        binding.loadingback.visibility = View.VISIBLE

        var field = CustomerRequestModel.Field(FinalStatus = "")
        var requestModel = CustomerRequestModel(
            PageNo = page,
            SalesPersonCode = Prefs.getString(Global.Employee_Code, ""),
            SearchText = searchText,
            field = field,
            maxItem = maxItem
        )

        val call: Call<AccountBPResponse> = ApiClient().service.getCustomerAllList(requestModel)
        call.enqueue(object : Callback<AccountBPResponse> {
            override fun onResponse(
                call: Call<AccountBPResponse>,
                response: Response<AccountBPResponse>
            ) {
                if (response.body()?.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    binding.ssPullRefresh.setRefreshing(false)
                    if (response.body()?.data.isNullOrEmpty()) {

                        AllitemsList.addAll(response.body()?.data!!)
                        adapter.notifyDataSetChanged()

                    } else {
                        val valueList = response.body()?.data
                        if (page == 1) {
                            AllitemsList.clear()
                            AllitemsList.addAll(valueList!!)
                            adapter.AllData(valueList)
                        } else {
                            AllitemsList.addAll(valueList!!)
                            adapter.AllData(valueList)
                        }

                        binding.ssPullRefresh.setRefreshing(false)
                        adapter.notifyDataSetChanged()

                        if (valueList.size < 10) {
                            apicall = false
                        }
                    }

                } else {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    Global.warningmessagetoast(requireContext(), response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<AccountBPResponse>, t: Throwable) {
                binding.loadingView.stop()
                binding.loadingback.visibility = View.GONE

                context?.let { t.message?.let { it1 -> Global.errormessagetoast(it, it1) } }
                binding.ssPullRefresh.setRefreshing(false)
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if (Global.checkForInternet(requireContext())) {
            binding.loadingView.start()
            callCustomerListAPi(page)
        }
    }

    fun setAdapter() {
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = AccountAdapter(AllitemsList)
        binding.recyclerview.layoutManager = linearLayoutManager
        binding.recyclerview.adapter = adapter
    }


    private fun checknodata() {
        binding.nodatafound.isVisible = adapter.itemCount == 0
    }

    //todo bind observer...
    private fun bindObserver() {
        viewModel.businessPartnerList.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
//                Global.warningmessagetoast(requireContext(), it)
                binding.loadingView.stop()
                binding.loadingback.visibility = View.GONE
                binding.ssPullRefresh.setRefreshing(false)
            },
            onLoading = {
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingView.stop()
                    binding.loadingback.visibility = View.GONE
                    binding.ssPullRefresh.setRefreshing(false)

                    if (response.data != null) {
                        Log.e("data", response.data.toString())
                        AllitemsList.clear()
                        AllitemsList.addAll(response.data)
                        linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = AccountAdapter(AllitemsList)
                        binding.recyclerview.layoutManager = linearLayoutManager
                        binding.recyclerview.adapter = adapter

                        adapter.notifyDataSetChanged()
                        checknodata()
                    }
                } else {
                    Global.warningmessagetoast(requireContext(), response.message)
                }
            }

        ))


    }


}

private const val TAG = "AccountFragment"