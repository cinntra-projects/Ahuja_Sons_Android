package com.ahuja.sons.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.adapter.AllPartRequestAdapter
import com.ahuja.sons.databinding.*
import com.ahuja.sons.globals.Global
import com.ahuja.sons.activity.AllPartRequestActivity
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.newapimodel.DataAllPartRequest
import com.ahuja.sons.viewmodel.MainViewModel

class AllPartRequest : Fragment() {

    lateinit var adapter: AllPartRequestAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private  lateinit var ticketFragment: AllpartrequestfragmentBinding
    var TicketID=""
    var pageNo = 1
    var recallApi = true
    lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketFragment = AllpartrequestfragmentBinding.inflate(layoutInflater)
        viewModel = (activity as AllPartRequestActivity).viewModel


        TicketID = activity?.intent?.getStringExtra("TicketID").toString()


        ticketFragment.backPress.setOnClickListener {
          //  parentFragmentManager.popBackStackImmediate()
            activity?.finish()

        }
        pageNo = 1
        recallApi = true
        AllPartData.clear()
        if (Global.checkForInternet(requireContext())){
            ticketFragment.progressBar.isVisible = true

            val data = HashMap<String,Any>()
            data["TicketId"]=TicketID.toInt()
            data["EmployeeId"]= ""
            data["PageNo"]=pageNo
            Log.e("payload",data.toString())

            viewModel.getAllpartrequest(data)
            bindObserver()
        }
        ticketFragment.nestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // in this method we are incrementing page number,
                // making progress bar visible and calling get data method.


                if(Global.checkForInternet(requireContext())&&recallApi){
                    pageNo++
                    ticketFragment.idPBLoading.visibility = View.VISIBLE

                    val data = HashMap<String,Any>()
                    data["TicketId"]=TicketID.toInt()
                    data["EmployeeId"]= ""
                    data["PageNo"]=pageNo
                    Log.e("payload",data.toString())

                    viewModel.getAllpartrequest(data)
                    bindObserver()

                }

            }
        })


        return ticketFragment.root
    }


    var AllPartData = ArrayList<DataAllPartRequest>()
    //todo bind observer..
    private fun bindObserver() {
        viewModel.partFilterAllResponse.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                ticketFragment.progressBar.isVisible = false
                ticketFragment.idPBLoading.isVisible = false
                Global.warningmessagetoast(requireContext(), it)
                Log.e("partdata==>", "attachmentObserverONERROR==>: $it")
            }, onLoading = {
                ticketFragment.progressBar.isVisible = true
                ticketFragment.idPBLoading.isVisible = true
            },
            onSuccess = { response ->
                Log.e("partdata",response.data.toString())
                ticketFragment.progressBar.isVisible = false
                ticketFragment.idPBLoading.isVisible = false
                if (response.status == 200) {
                    recallApi = response.data.isNotEmpty()
                    AllPartData.addAll(response.data)
                    linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = AllPartRequestAdapter(AllPartData)
                    ticketFragment.productRecyclerView.layoutManager = linearLayoutManager
                    ticketFragment.productRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    ticketFragment.nodatafound.isVisible = AllPartData.isEmpty()
                } else {
                    Global.warningmessagetoast(requireContext(), response.message);
                }
            }
        ))
    }


}
