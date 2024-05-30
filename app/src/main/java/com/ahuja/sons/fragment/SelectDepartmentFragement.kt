package com.ahuja.sons.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.activity.EditTicketActivity
import com.ahuja.sons.adapter.SelectContactAdapter
import com.ahuja.sons.databinding.SelectDepartmentBinding
import com.ahuja.sons.newapimodel.DataXX

class SelectDepartmentFragement(val contacnameValue: EditText, val contactlist: ArrayList<DataXX>, val contactAddTicketActivity: AddTicketActivity, val fromEditActivity: EditTicketActivity, val flag: String) : Fragment() {

    var adapter : SelectContactAdapter? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var ticketFragment: SelectDepartmentBinding
    lateinit var addTicketActivity: AddTicketActivity
    lateinit var editTicketActivity: EditTicketActivity

    /* constructor(addTicketActivity: AddTicketActivity) : this() {
         this.selectBusinessPartner = addTicketActivity
     }

     constructor(addcp: EditTicketActivity) : this() {
         this.selectBusinessPartner = addcp
     }
 */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ticketFragment = SelectDepartmentBinding.inflate(layoutInflater)

        addTicketActivity = contactAddTicketActivity

        editTicketActivity = fromEditActivity

        ticketFragment.toolbarview.heading.text = "Select Contact Person"

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SelectContactAdapter(
            requireContext(),
            contacnameValue,
            contactlist,
            contactAddTicketActivity,
            fromEditActivity,
            flag
        )
        ticketFragment.recyclerview.layoutManager = linearLayoutManager
        ticketFragment.recyclerview.adapter = adapter
        ticketFragment.nodatafound.isVisible = adapter?.itemCount == 0


        ticketFragment.toolbarview.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        return ticketFragment.root
    }


}
