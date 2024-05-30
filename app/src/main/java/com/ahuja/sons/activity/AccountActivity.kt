package com.ahuja.sons.activity

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.ahuja.sons.adapter.AccountAdapter
import com.ahuja.sons.adapter.ViewPagerAdapter

import com.ahuja.sons.fragment.AccountAllTicketFragment
import com.ahuja.sons.fragment.AccountOnHoldTicketFragment
import com.ahuja.sons.fragment.AccountOpenTicketFragment
import com.ahuja.sons.fragment.AccountOverdueTicketFragment
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.model.TicketDataModel
import com.ahuja.sons.R
import com.ahuja.sons.databinding.FragmentAccoutdetailBinding
import java.util.*
import kotlin.collections.ArrayList

class AccountActivity() : MainBaseActivity() {

    lateinit var adapter: AccountAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var ticketFragment: FragmentAccoutdetailBinding

    var bpwiseticketlist = ArrayList<TicketDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ticketFragment = FragmentAccoutdetailBinding.inflate(layoutInflater)
        setContentView(ticketFragment.root)

        setSupportActionBar(ticketFragment.toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        bpwiseticketlist = intent.getParcelableArrayListExtra<TicketDataModel>(Global.BPWiseticketlist)!!


        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.add(AccountAllTicketFragment(bpwiseticketlist), "All")
        pagerAdapter.add(AccountOpenTicketFragment(bpwiseticketlist.filter { it.TicketStatus == "Rejected" } as ArrayList<TicketDataModel>),
            "Rejected")
        pagerAdapter.add(AccountOverdueTicketFragment(bpwiseticketlist.filter { it.TicketStatus == "Accepted" } as ArrayList<TicketDataModel>),
            "Accepted")
        pagerAdapter.add(AccountOnHoldTicketFragment(bpwiseticketlist.filter { it.TicketStatus == "Pending" } as ArrayList<TicketDataModel>),
            "Pending")
        ticketFragment.viewpager.adapter = pagerAdapter
        ticketFragment.tabLayout.setupWithViewPager(ticketFragment.viewpager)

        ticketFragment.companyName.text = bpwiseticketlist[0].BusinessPartner.CardName

        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if (bpwiseticketlist[0].BusinessPartner.CardName.isNotEmpty()) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    bpwiseticketlist[0].BusinessPartner.CardName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            ticketFragment.nameIcon.setImageDrawable(drawable)
        }

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.account_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}
