package com.ahuja.sons.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.adapter.ViewPagerAdapter
import com.ahuja.sons.databinding.TicektDetailsBinding
import com.ahuja.sons.globals.Global


class TicketFragment(val pos: Int) : Fragment() {


    private lateinit var ticketFragment: TicektDetailsBinding

   /* override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        (activity as AppCompatActivity?)!!.findViewById<CollapsingToolbarLayout>(R.id.collapsetoolbar).visibility = View.GONE

    }

*/

    var flag = ""
    var servcieID = ""

    companion object {
        private const val TAG = "TicketFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        ticketFragment = TicektDetailsBinding.inflate(layoutInflater)

        val bundle = arguments
        try {
            servcieID = bundle!!.getString("serviceID")!!
            flag = bundle.getString("Flag")!!
            if (servcieID.isNotEmpty()){
                Prefs.putString(Global.servcieID, servcieID)
            }else{
                Prefs.putString(Global.servcieID, "")
            }

        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }


        ticketFragment.kebabMoreFilter.setOnClickListener {
            Log.e(TAG, "onCreateView: setnclick")
        }


        val pagerAdapter = ViewPagerAdapter(childFragmentManager)
        pagerAdapter.add(AllTicketFragment(ticketFragment, flag, servcieID), "All")
        pagerAdapter.add(OpenTicketFragment(ticketFragment, flag), "PPM")
        pagerAdapter.add(OverdueTicketFragment(ticketFragment, flag), "Installation")
        pagerAdapter.add(OnHoldTicketFragment(ticketFragment, flag), "Break Down")
//        pagerAdapter.add(OnHoldTicketFragment(ticketFragment, flag), "Servicing")
        ticketFragment.viewpager.adapter = pagerAdapter
        ticketFragment.viewpager.offscreenPageLimit = 4

        ticketFragment.tabLayout.setupWithViewPager(ticketFragment.viewpager)
        ticketFragment.viewpager.currentItem = pos

        ticketFragment.addCustomer.shrink()

        if (Prefs.getString(Global.Employee_role).equals("admin", ignoreCase = true) || Prefs.getString(Global.Employee_role).equals("Sr. Executive", ignoreCase = true) ){
            ticketFragment.addCustomer.visibility = View.VISIBLE
        }
        else{
            ticketFragment.addCustomer.visibility = View.GONE
        }

        ticketFragment.addCustomer.setOnClickListener {
            Prefs.putString(Global.TicketFlowFrom, "Ticket")
            val intent = Intent(context, AddTicketActivity::class.java)
            startActivity(intent)

        }

        ticketFragment.kebabMoreFilter.setOnClickListener {  }

        return ticketFragment.root
    }


}
