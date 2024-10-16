package com.ahuja.sons.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.activity.AnnouncementActivity
import com.ahuja.sons.adapter.AnnouncementAdapter
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.databinding.FragmentAnnouncementListBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.viewmodel.MainViewModel
import com.simform.refresh.SSPullToRefreshLayout


class AnnouncementListFragment : Fragment() {
    private var _binding: FragmentAnnouncementListBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: MainViewModel

    var builder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null
    var announcementAdapter = AnnouncementAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcement_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as AnnouncementActivity).viewModel
        _binding = FragmentAnnouncementListBinding.bind(view)

        setupLoadingDialog()
        setupRecyclerView()

        viewModel.getAnnouncementList()
        subscribeToObserver()
        refreshList()


    }

    private fun refreshList(){
        binding.refreshAnnouncement.setOnRefreshListener(object : SSPullToRefreshLayout.OnRefreshListener
        {
            override fun onRefresh() {
                if (Global.checkForInternet(requireContext())){
                    viewModel.getAnnouncementList()
                }
                binding.refreshAnnouncement.setRefreshing(false)
            }

        })
    }

    private fun setupRecyclerView() = binding.rvAnnouncement.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = announcementAdapter

    }


    private fun setupLoadingDialog() {
        builder = AlertDialog.Builder(requireContext())
        builder!!.setView(R.layout.dialog_progress)
            .setCancelable(false)
        alertDialog = builder!!.create()
    }

    companion object {
        private const val TAG = "AnnouncementListFragmen"
    }


    private fun subscribeToObserver() {
        viewModel.getAnnouncementist.observe(viewLifecycleOwner, Event.EventObserver(
            onError = {
                alertDialog?.dismiss()
//                Global.warningmessagetoast(requireContext(), it)
            }, onLoading = {
                alertDialog?.show()
            }, { responseData ->
                alertDialog?.dismiss()
                if (responseData.status == 200) {
                    announcementAdapter.announcement = responseData.data
                    Log.e(TAG, "subscribeToObserver: ${responseData.data.size}")
                } else {

                    Global.warningmessagetoast(requireContext(), responseData.message)
                }

            }
        ))
    }


}