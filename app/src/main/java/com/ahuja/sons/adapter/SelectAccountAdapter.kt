package com.ahuja.sons.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.ahuja.sons.R
import com.ahuja.sons.`interface`.SelectBusinessPartneer
import com.ahuja.sons.newapimodel.DataCustomerListForContact
import java.util.*


class SelectAccountAdapter() : RecyclerView.Adapter<SelectAccountAdapter.Category_Holder>() {

    lateinit var AllitemsList: ArrayList<DataCustomerListForContact>
    lateinit var selectpartner: SelectBusinessPartneer
    private lateinit var oncontext: Activity

    constructor(
        requireContext: Context,
        selectprtner: SelectBusinessPartneer,
        allitemsList: ArrayList<DataCustomerListForContact>
    ) : this() {
        this.AllitemsList = allitemsList
        this.selectpartner = selectprtner
        this.oncontext = requireContext as Activity
    }


    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.account_view,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {


        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        val drawable: TextDrawable = TextDrawable.builder()
            .beginConfig()
            .withBorder(4) /* thickness in px */
            .endConfig()
            .buildRound(
                AllitemsList[position].CardName[0].toString()
                    .uppercase(Locale.getDefault()), color1
            )
        holder.profile_pic.setImageDrawable(drawable)
        holder.name.text = AllitemsList[position].CardName
        holder.email.text = AllitemsList[position].CardCode

        holder.itemView.setOnClickListener {
            selectpartner.selectpartner(AllitemsList[position])
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
