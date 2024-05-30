package com.ahuja.sons.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.`interface`.SelectedItemData
import com.ahuja.sons.activity.AddTicketActivity
import kotlin.collections.ArrayList


class SelectItemAdapter() : RecyclerView.Adapter<SelectItemAdapter.Category_Holder>()   {

     var AllitemsList= ArrayList<com.ahuja.sons.newapimodel.DocumentLine>()

    private  lateinit var oncontext: Activity

    private  lateinit var selectedItemData: SelectedItemData

    constructor(
        orderValue1: AddTicketActivity,
        requireContext: Context,

        documentLines: ArrayList<com.ahuja.sons.newapimodel.DocumentLine>
    ) : this(){
        this.AllitemsList.clear()
        this.AllitemsList.addAll(documentLines)
        this.oncontext = requireContext as Activity
        this.selectedItemData = orderValue1
    }


    private  lateinit var context: Context





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


       /* val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        val drawable: TextDrawable = TextDrawable.builder()
            .beginConfig()
            .withBorder(4) *//* thickness in px *//*
            .endConfig()
            .buildRound(
                AllitemsList[position].ItemDescription
                    .uppercase(Locale.getDefault()), color1
            )
        holder.profile_pic.setImageDrawable(drawable)*/
        holder.name.text = AllitemsList[position].ItemDescription
     //   holder.email.text =  AllitemsList[position].ItemCode

        holder.itemView.setOnClickListener {
            Log.e("SELECTEDITEMADAPTER===>>", "onBindViewHolder: "+AllitemsList[position].ItemDescription )
            selectedItemData.selecteditemdata(AllitemsList[position])

            oncontext.onBackPressed()
        }
          }

    override fun getItemCount(): Int {
        return AllitemsList.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profile_pic : ImageView = itemView.findViewById(R.id.profile_pic)
        val name : TextView = itemView.findViewById(R.id.name)
        val email : TextView = itemView.findViewById(R.id.email)



    }





}
