package com.ahuja.sons.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.ahuja.sons.R
import com.ahuja.sons.activity.AccountDetailActivity
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.AccountBpData
import java.util.*
import kotlin.collections.ArrayList


class AccountAdapter(val AllitemsList: ArrayList<AccountBpData>) : RecyclerView.Adapter<AccountAdapter.Category_Holder>() {

    private lateinit var context: Context
    var tempList = ArrayList<AccountBpData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_view, parent, false)
        tempList.clear()
        tempList.addAll(AllitemsList)
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
            .buildRound(AllitemsList[position].CardName[0].toString().uppercase(Locale.getDefault()), color1)
        holder.profile_pic.setImageDrawable(drawable)
        holder.name.text = AllitemsList[position].CardName
        holder.email.text = AllitemsList[position].CardCode

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Global.AccountData, AllitemsList[position])
            val intent = Intent(context, AccountDetailActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
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

    fun AllData(tmp: ArrayList<AccountBpData>?) {
        tempList.clear()
        tempList.addAll(tmp!!)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(newText: String) {
        Log.e("AllSiz", AllitemsList.size.toString())
        Log.e("tmpSiz", tempList.size.toString())
        newText.lowercase()
        //newText = newText.lowercase(Locale.getDefault())
        AllitemsList.clear()
        if (newText.length == 0) {
            AllitemsList.addAll(tempList)
        } else {
            for (st in tempList) {
                if (st.CardName != null && !st.CardName.isEmpty()) {
                    if (st.CardName.toLowerCase(Locale.getDefault())
                            .contains(newText.lowercase().toString())
                    ) {
                        AllitemsList.add(st)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }


}
