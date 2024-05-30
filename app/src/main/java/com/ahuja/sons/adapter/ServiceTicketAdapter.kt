package com.ahuja.sons.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ahuja.sons.R
import com.ahuja.sons.`interface`.RecallApi
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.fragment.ServiceTicketFragment
import com.ahuja.sons.globals.Global
import com.ahuja.sons.model.TicketChecklistData
import com.ahuja.sons.model.TicketChecklistResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ServiceTicketAdapter(
    val ticketstatus: String,
    val tickethistorydata: ArrayList<TicketChecklistData>,
    val historyTicketFragment: ServiceTicketFragment
) : RecyclerView.Adapter<ServiceTicketAdapter.Category_Holder>() {

    private lateinit var context: Context
    private lateinit var recallApi: RecallApi


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Category_Holder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.servce_ticket_adapter,
            parent,
            false
        )

        context = parent.context
        recallApi = historyTicketFragment
        return Category_Holder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Category_Holder, position: Int) {

        holder.threedot.setOnClickListener {

            when (ticketstatus) {
                "Accepted" -> {
                    if (tickethistorydata[position].Status == "False") {
                        if (Global.TicketStartDate.isNotEmpty()) {
                            openCommentDialog(tickethistorydata[position])

                        } else {
                            Global.warningdialogbox(context, "Start timer to working on it")
                        }
                    }
                }
                "Pending" -> {
                    Global.warningdialogbox(
                        context,
                        "Your ticket is in pending state,Kindly accept it"
                    )
                }
                "Rejected" -> {
                    Global.warningdialogbox(context, "Your ticket will be rejected")
                }
            }
        }

        holder.clickview.setOnClickListener {


            when (ticketstatus) {
                "Accepted" -> {
                    if (tickethistorydata[position].Status == "False") {
                        if (Global.TicketStartDate.isNotEmpty()) {
                            openCommentDialog(tickethistorydata[position])

                        } else {
                            Global.warningdialogbox(context, "Start timer to working on it")
                        }
                    }
                }
                "Pending" -> {
                    Global.warningdialogbox(
                        context,
                        "Your ticket is in pending state,Kindly accept it"
                    )
                }
                "Rejected" -> {
                    Global.warningdialogbox(context, "Your ticket will be rejected")
                }

            }
        }



        holder.title.text = tickethistorydata[position].TaskName
        holder.message.text = tickethistorydata[position].Comment

        if (tickethistorydata[position].Status == "False") {
            holder.tick_green.background =
                context.resources.getDrawable(R.drawable.tick_square_grey)
            holder.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
        } else {
            holder.tick_green.background =
                context.resources.getDrawable(R.drawable.tick_square_green)
            holder.divider.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        }


    }

    private fun openCommentDialog(ticketChecklistData: TicketChecklistData) {
        val dialog = Dialog(context, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.comment_dialog)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        val try_again: Button = dialog.findViewById(R.id.try_again)
        val done: Button = dialog.findViewById(R.id.done)
        val edttext: EditText = dialog.findViewById(R.id.edttext)

        try_again.setOnClickListener {
            dialog.dismiss()

        }

        done.setOnClickListener {
            if (Global.TicketAuthentication) {
                if (edttext.text.toString().isNotEmpty()) {
                    val checkupdate = TicketChecklistData(
                        TicketId = ticketChecklistData.TicketId,
                        id = ticketChecklistData.id,
                        Status = "True",
                        Comment = edttext.text.toString(),
                        Datetime = ticketChecklistData.Datetime,
                        TaskName = ticketChecklistData.TaskName,
                        Duration = ticketChecklistData.Duration
                    )

                    callupdatechecklistApi(checkupdate, dialog)
                } else {
                    Global.warningmessagetoast(context, "Enter Comments")
                }
            } else {
                Global.warningdialogbox(context, "You have not authorization to work on ticket")
            }
        }






        dialog.window!!.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun callupdatechecklistApi(checkupdate: TicketChecklistData, dialog: Dialog) {
        val call: Call<TicketChecklistResponse> = ApiClient().service.updatechecklist(checkupdate)
        call.enqueue(object : Callback<TicketChecklistResponse?> {
            override fun onResponse(
                call: Call<TicketChecklistResponse?>,
                response: Response<TicketChecklistResponse?>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    recallApi.recallApi()
                    dialog.dismiss()

                } else {
                    Global.warningmessagetoast(context, response.errorBody().toString());

                }
            }

            override fun onFailure(call: Call<TicketChecklistResponse?>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return tickethistorydata.size
    }

    inner class Category_Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val threedot = itemView.findViewById<LinearLayout>(R.id.threedot)
        val clickview = itemView.findViewById<LinearLayout>(R.id.clickview)
        val title = itemView.findViewById<TextView>(R.id.title)
        val message = itemView.findViewById<TextView>(R.id.message)
        val tick_green = itemView.findViewById<ImageView>(R.id.tick_green)
        val divider = itemView.findViewById<View>(R.id.divider)


    }


}
