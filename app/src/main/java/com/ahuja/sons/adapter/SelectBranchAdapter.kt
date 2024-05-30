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
import com.ahuja.sons.`interface`.SelectBranchItem
import com.ahuja.sons.newapimodel.BranchAllListResponseModel
import java.util.ArrayList

class SelectBranchAdapter() : RecyclerView.Adapter<SelectBranchAdapter.Category_Holder>() {

    lateinit var AllitemsList: ArrayList<BranchAllListResponseModel.DataXXX>
    private lateinit var oncontext: Activity
    lateinit var selectBranch: SelectBranchItem
    lateinit var edtSelectBranch: SelectBranchItem
    lateinit var branchnameVal: EditText
    lateinit var Flag : String

    constructor(requireContext: Context, branchnameValue: EditText, contactlist: ArrayList<BranchAllListResponseModel.DataXXX>, selectBranch: AddTicketActivity, editTicketActivity: EditTicketActivity, flag : String) : this() {
        this.oncontext = requireContext as Activity
        this.AllitemsList = contactlist
        this.branchnameVal = branchnameValue
        this.selectBranch = selectBranch
        this.edtSelectBranch = editTicketActivity
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
        holder.name.text = AllitemsList[position].AddressName
        //  holder.email.text =  AllitemsList[position].MobilePhone

        holder.itemView.setOnClickListener {
            branchnameVal.setText(AllitemsList[position].AddressName)
            if (Flag.equals("AddTicketContext")) {
                selectBranch.selectBranch(AllitemsList[position])
            }else if (Flag.equals("EditTicketConext")){
                edtSelectBranch.selectBranch(AllitemsList[position])
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
