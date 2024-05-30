package com.ahuja.sons.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.ahuja.sons.R
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.animation.ViewAnimationUtils
import com.ahuja.sons.databinding.FragmentHomeBinding
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.ahuja.sons.activity.MainActivity
import com.ahuja.sons.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeFragment: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel

    var todaydatelist: Boolean = true

    lateinit var getstartDate: Calendar
    lateinit var getendDate: Calendar


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.findViewById<CollapsingToolbarLayout>(R.id.collapsetoolbar).visibility =
            View.VISIBLE

    }




    @SuppressLint("ResourceAsColor", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragment = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = (activity as MainActivity).viewModel


        val calendar = activity?.findViewById<ImageView>(R.id.calendar)
        homeFragment.calendar.setNavLeftImage(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_leftblue)!!
        )
        homeFragment.calendar.setNavRightImage(ContextCompat.getDrawable(requireContext(), R.drawable.ic_rightblue)!!)


        val sdf = SimpleDateFormat("dd/MM/yyyy").format(Date())

        val sdf1 = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = sdf1.parse(sdf) as Date
        val cal = Calendar.getInstance()
        cal.time = date
        getstartDate = cal

        val pagerAdapter = ViewPagerAdapter(childFragmentManager)
        pagerAdapter.add(TodayTicketFragment(getstartDate), "Today's")
        pagerAdapter.add(NewTicketFragment(), "Past")
//        pagerAdapter.add(CriticalTicketFragment(), "Critical")
        homeFragment.viewpagerView.viewpager.adapter = pagerAdapter
        homeFragment.calendar.setSelectedDateRange(cal, cal)
        calendar?.setOnClickListener {
            if (homeFragment.calendarView.isVisible) {
                ViewAnimationUtils.collapse(homeFragment.calendarView)
            } else
                ViewAnimationUtils.expand(homeFragment.calendarView)
        }


        homeFragment.done.setOnClickListener {
            ViewAnimationUtils.collapse(homeFragment.calendarView)

            if (todaydatelist) {

                val pagerAdapter1 = ViewPagerAdapter(childFragmentManager)
                pagerAdapter1.add(TodayTicketFragment(getstartDate), "Today's")
                pagerAdapter1.add(NewTicketFragment(), "New")
                pagerAdapter1.add(CriticalTicketFragment(), "Critical")

                homeFragment.viewpagerView.viewpager.adapter = pagerAdapter1
                Log.e("count", homeFragment.viewpagerView.viewpager.childCount.toString())
                // pagerAdapter1.notifyDataSetChanged()
            } else {

                val repagerAdapter = ViewPagerAdapter(childFragmentManager)
                repagerAdapter.add(
                    AccToDateTicketFragment(getstartDate, getendDate),
                    "Selected Dates"
                )
                repagerAdapter.add(NewTicketFragment(), "New")
                repagerAdapter.add(CriticalTicketFragment(), "Critical")
                homeFragment.viewpagerView.viewpager.invalidate()
                homeFragment.viewpagerView.viewpager.adapter = repagerAdapter
                Log.e("count", homeFragment.viewpagerView.viewpager.childCount.toString())
                // repagerAdapter.notifyDataSetChanged()

            }


        }
        homeFragment.cancel.setOnClickListener {
            ViewAnimationUtils.collapse(homeFragment.calendarView)
            val pagerAdapter = ViewPagerAdapter(childFragmentManager)
            pagerAdapter.add(TodayTicketFragment(getstartDate), "Today's")
            pagerAdapter.add(NewTicketFragment(), "New")
            pagerAdapter.add(CriticalTicketFragment(), "Critical")
            homeFragment.viewpagerView.viewpager.adapter = pagerAdapter
        }




        homeFragment.viewpagerView.tabLayout.setupWithViewPager(homeFragment.viewpagerView.viewpager)


        homeFragment.calendar.setCalendarListener(object : CalendarListener {
            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {

                todaydatelist = false
                getstartDate = startDate
                getendDate = endDate


            }


            override fun onFirstDateSelected(startDate: Calendar) {
                getstartDate = startDate
                todaydatelist = true
            }


        })

        return homeFragment.root
    }

}
