package com.ahuja.sons.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.activity.AddTicketActivity
import com.ahuja.sons.activity.EditTicketActivity
import com.ahuja.sons.`interface`.ContactItemSelect
import com.ahuja.sons.newapimodel.DataXX
import java.util.*


class SelectContactAdapter() : RecyclerView.Adapter<SelectContactAdapter.Category_Holder>() {

    lateinit var AllitemsList: ArrayList<DataXX>
    private lateinit var oncontext: Activity
    lateinit var contactval: EditText
    lateinit var contactItemSelect: ContactItemSelect
    lateinit var editItemSelect: ContactItemSelect
    lateinit var Flag : String

    constructor(requireContext: Context, contacnameValue: EditText,
        contactlist: ArrayList<DataXX>,//com.wae.servicesupportportal.newapimodel.ContactPersonCode
        selectBranch: AddTicketActivity, editActivity : EditTicketActivity, flag : String) : this() {
        oncontext = requireContext as Activity
        AllitemsList = contactlist
        contactval = contacnameValue
        this.contactItemSelect = selectBranch
        this.editItemSelect = editActivity
        this.Flag = flag
    }


    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.listing_view,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        holder.name.text = AllitemsList[position].FirstName

        holder.itemView.setOnClickListener {
            contactval.setText(AllitemsList[position].FirstName)

            if (Flag.equals("AddTicketContext")) {
                contactItemSelect.selectContactItem(AllitemsList[position])
            }
            else if (Flag.equals("EditTicketConext")){
                editItemSelect.selectContactItem(AllitemsList[position])
            }
            oncontext.onBackPressed()
        }
    }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile_pic: ImageView = itemView.findViewById(R.id.profile_pic)
        val name: TextView = itemView.findViewById(R.id.name)
        val email: TextView = itemView.findViewById(R.id.email)

    }


}
