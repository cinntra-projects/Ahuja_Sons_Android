package com.ahuja.sons.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.LogInResponse
import com.ahuja.sons.model.NewLoginData
import com.ahuja.sons.receiver.DataEmployeeAllData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SelectEmployeeDialogAdapter() : RecyclerView.Adapter<SelectEmployeeDialogAdapter.Category_Holder>() {

    lateinit var AllitemsList: ArrayList<DataEmployeeAllData>
    private lateinit var oncontext: DialogFragment

    lateinit var TicketID: String

    constructor(requireContext: DialogFragment, ticketID: String?, contactlist: ArrayList<DataEmployeeAllData>) : this() {
        if (ticketID != null) {
            TicketID = ticketID
        }
        oncontext = requireContext
        AllitemsList = contactlist
    }


    private lateinit var context: Context


    override fun getItemViewType(position: Int): Int {
        return position
    }


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


        /*     val generator: ColorGenerator = ColorGenerator.MATERIAL
             val color1: Int = generator.randomColor
             val drawable: TextDrawable = TextDrawable.builder()
                 .beginConfig()
                 .withBorder(4) */
        /* thickness in px */
        /*
            .endConfig()
            .buildRound(
                AllitemsList[position].FirstName[0].toString()
                    .uppercase(Locale.getDefault()), color1
            )
        holder.profile_pic.setImageDrawable(drawable)*/
        holder.name.text = AllitemsList[position].SalesEmployeeName
        //  holder.email.text =  AllitemsList[position].MobilePhone

        holder.name.setOnClickListener {
            val assignticket = NewLoginData()
            assignticket.setEmployeeId(AllitemsList[position].SalesEmployeeCode.toString())
            assignticket.setTicketid(TicketID)
            callAssignedTicketAPI(assignticket)
        }
    }

    private fun callAssignedTicketAPI(employeeassign: NewLoginData) {
        Log.e("msz", employeeassign.toString())

        val call: Call<LogInResponse> = ApiClient().service.assignticketemployee(employeeassign)
        call.enqueue(object : Callback<LogInResponse?> {
            override fun onResponse(
                call: Call<LogInResponse?>,
                response: Response<LogInResponse?>
            ) {
                if (response.body()!!.getStatus() == 200) {
                    Log.e("msz", response.body().toString())
                    oncontext.context?.let {
                        Global.successmessagetoast(it, "Assigned successfully, Kindly refresh the list")
                    }
                    oncontext.dismiss()
                }

            }

            override fun onFailure(call: Call<LogInResponse?>, t: Throwable) {

                Global.errormessagetoast(context, t.message.toString())
            }
        })


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
