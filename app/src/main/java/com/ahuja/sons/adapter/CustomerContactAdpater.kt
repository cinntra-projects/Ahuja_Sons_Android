package com.ahuja.sons.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.ContactEmployee
import java.util.*
import kotlin.collections.ArrayList


class CustomerContactAdpater(val contactEmployees: ArrayList<ContactEmployee>) : RecyclerView.Adapter<CustomerContactAdpater.Category_Holder>()   {

    private  lateinit var context: Context





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.customercontact_view,
            parent,
            false
        )

        context = parent.context
        return Category_Holder(view)
    }

    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        holder.name_value.text = contactEmployees[position].FirstName
        holder.role_val.text = contactEmployees[position].MobilePhone
        val generator: ColorGenerator = ColorGenerator.MATERIAL
        val color1: Int = generator.randomColor
        if(contactEmployees[position].FirstName.isNotEmpty()) {
            val drawable: TextDrawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRound(
                    contactEmployees[position].FirstName[0].toString()
                        .uppercase(Locale.getDefault()), color1
                )
            holder.profile_pic.setImageDrawable(drawable)
        }


        holder.call_view.setOnClickListener {
//            checkMobilePermission(position)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactEmployees[position].MobilePhone))
            context.startActivity(intent)

        }

        holder.chat_view.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // Specifies the "mailto" scheme
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(contactEmployees[position].E_Mail)) // Sets the recipient email address
            // it.putExtra(Intent.EXTRA_SUBJECT, subject) // Sets the email subject
            // it.putExtra(Intent.EXTRA_TEXT, body) // Sets the email body

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Global.infomessagetoast(context,"No App Found")
                // Handle case when Gmail app is not installed
                // For example, you can open a web-based email service
            }


//            context.startActivity(
//            Intent(
//                Intent.ACTION_VIEW,
//                Uri.fromParts("sms", contactEmployees[position].MobilePhone, null)
//            )
//        )

        }
    }


    override fun getItemCount(): Int {
        return contactEmployees.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name_value : TextView = itemView.findViewById(R.id.name_value)
        val role_val : TextView = itemView.findViewById(R.id.role_val)
        val profile_pic : ImageView = itemView.findViewById(R.id.profile_pic)
        val chat_view : LinearLayout = itemView.findViewById(R.id.chat_view)
        val call_view : LinearLayout = itemView.findViewById(R.id.call_view)




    }





}
