package com.ahuja.sons.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.R
import com.ahuja.sons.activity.AddContactPerson
import com.ahuja.sons.adapter.CustomerContactAdpater
import com.ahuja.sons.databinding.CustomercontactBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBpData
import com.ahuja.sons.model.ContactEmployee
import com.google.android.material.appbar.AppBarLayout
import com.ahuja.sons.activity.AccountDetailActivity
import kotlin.math.abs

class CustomerContactFragment(val customerdata: AccountBpData) : Fragment() , AccountDetailActivity.MyFragmentCustomerListener{

    private lateinit var ticketbiding: CustomercontactBinding
    lateinit var adapter: CustomerContactAdpater
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ticketbiding = CustomercontactBinding.inflate(layoutInflater)

        ticketbiding.loadingView.start()

        if (customerdata != null ){
            linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = CustomerContactAdpater(customerdata.ContactEmployees as ArrayList<ContactEmployee>)
            ticketbiding.productRecyclerView.layoutManager = linearLayoutManager
            ticketbiding.productRecyclerView.adapter = adapter
            ticketbiding.loadingView.stop()
            ticketbiding.loadingback.visibility = View.GONE
        }else{
            ticketbiding.loadingView.stop()
            ticketbiding.loadingback.visibility = View.GONE
            ticketbiding.nodatafound.visibility = View.VISIBLE
        }



        ticketbiding.addContact.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Global.AccountData, customerdata)
            val intent = Intent(context, AddContactPerson::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        }


        val appbar = activity?.findViewById<AppBarLayout>(R.id.appbar)


        appbar?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->


            if (abs(verticalOffset) > 30) {

                /*           ticketbiding.addcomment.margin(left = 0F)
                           ticketbiding.addcomment.margin(right = 30F)
                           ticketbiding.addcomment.margin(top = 0F)
                           ticketbiding.addcomment.margin(bottom = 90F)*/


            } else {
                ticketbiding.addContact.margin(left = 0F)
                ticketbiding.addContact.margin(right = 30F)
                ticketbiding.addContact.margin(top = 0F)
                ticketbiding.addContact.margin(bottom = 150F)


            }
        }
        return ticketbiding.root
    }

    fun View.margin(
        left: Float? = null,
        top: Float? = null,
        right: Float? = null,
        bottom: Float? = null
    ) {
        layoutParams<ViewGroup.MarginLayoutParams> {
            left?.run { leftMargin = dpToPx(this) }
            top?.run { topMargin = dpToPx(this) }
            right?.run { rightMargin = dpToPx(this) }
            bottom?.run { bottomMargin = dpToPx(this) }
        }
    }

    inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
    }

    fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    fun Context.dpToPx(dp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

    /*override fun onDataPassedCustomer(data: BranchAllListResponseModel.DataXXX) {

    }*/

    override fun onDataPassedCustomer(startDate: String?, endDate: String?, pos: Int?) {
        Log.e(TAG, "onDataPassedCustomer: ", )
    }


    companion object{
        private const val TAG = "CustomerContactFragment"
    }
}
