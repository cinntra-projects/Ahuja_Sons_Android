package com.ahuja.sons.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.ahuja.sons.R
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.globals.Global
import com.ahuja.sons.`interface`.FragmentRefresher
import com.ahuja.sons.newapimodel.NotificationListResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationAdapter(private val context: Context, private val notificationList: ArrayList<NotificationListResponseModel.DataXXX>, private val fresh : FragmentRefresher) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var notificationData = notificationList[position]
        holder.title.setText(notificationData.notification.Type)
        holder.description.setText(notificationData.notification.Description)

        if (notificationData.notification.Read.equals("1", ignoreCase = true)) {
            holder.profile_pic.visibility = View.GONE
        } else {
            holder.profile_pic.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val profile_pic: ImageView = itemView.findViewById(R.id.profile_pic)

        init {
            itemView.setOnClickListener {
                callReadNotificationApi(notificationList[adapterPosition].notification.id)
            }
        }

    }


    private fun callReadNotificationApi(id: Int) {
        var jsonObject = JsonObject()
        jsonObject.addProperty("id", id.toString())
        val call: Call<NotificationListResponseModel> = ApiClient().service.readNotificationAPi(jsonObject)
        call.enqueue(object : Callback<NotificationListResponseModel> {
            override fun onResponse(call: Call<NotificationListResponseModel>, response: Response<NotificationListResponseModel>) {
                if (response.body()?.status == 200) {
                    try {
                        fresh.onRefresh()
                    }catch (e: Exception){
                        e.printStackTrace()
                        Log.e("TAG===>", "onResponse: "+e.message )
                    }

                } else {
                    Global.warningmessagetoast(context, response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<NotificationListResponseModel>, t: Throwable) {
                context?.let { t.message?.let { it1 -> Global.errormessagetoast(it, it1) } }
            }
        })

    }

}
