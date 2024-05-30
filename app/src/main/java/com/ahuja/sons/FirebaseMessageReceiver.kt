package com.ahuja.sons

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import android.content.Intent

import android.app.PendingIntent
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ahuja.sons.activity.LoginActivity
import com.ahuja.sons.globals.MyApp
import com.ahuja.sons.model.NewLoginData

class FirebaseMessageReceiver : FirebaseMessagingService() {
    var hashMap = HashMap<String, Any>()
    val currentActivity = MyApp.currentApp
    val ticketdata = NewLoginData()
    var typo = ""


   // val dialog = Dialog(MyApp.currentActivity!!)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("MESSAGE", "onMessageReceived: ")

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data==null) {


            Log.e("TAG", "onMessageReceived: ${remoteMessage.data.toString()}")
            Log.e(TAG, "onMessageReceivedRemOTE==>: ${remoteMessage.notification!!.body.toString()}", )
            hashMap["img"] = remoteMessage.data["img"]!!
            hashMap["Type"] = remoteMessage.data["Type"]!!
            hashMap["SourceID"] = remoteMessage.data["SourceID"]!!
            hashMap["SalesEmployeeCode"] = remoteMessage.data["SalesEmployeeCode"]!!
            sendNotification(remoteMessage.notification?.title,remoteMessage.notification?.body)

      //      showCustomDialog()
//

        }
    }


//    private fun showCustomDialog() {
//        Log.e(TAG, "showCustomDialog: ")
//        val handler = Handler(Looper.getMainLooper())
//        handler.post(object : Runnable {
//            override fun run() {
//                Log.e(TAG, "run: $currentActivity.")
//                Log.e(TAG, "run====>: ${MyApp.currentActivity}.")
//                dialog.setContentView(R.layout.dialog_man_trap_rescue)
//                // dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                // var tvAnnounce:TextView= dialog.findViewById<TextView>(R.id.tvAnnouncement)
//                var bellIcon: ImageView = dialog.findViewById<ImageView>(R.id.imageBellIcon)
//                var acceptButton: Button = dialog.findViewById<Button>(R.id.btnAccept)
//                var rejectButton: Button = dialog.findViewById<Button>(R.id.btnReject)
//                Glide.with(MyApp.currentActivity!!).asGif().load(R.raw.alertsos).into(bellIcon)
//                var mediaPlayer: MediaPlayer? = null
//                mediaPlayer = MediaPlayer.create(MyApp.currentActivity!!, R.raw.sos_tone)
//                mediaPlayer?.isLooping = true
//                mediaPlayer?.start()
//
//
//
//                dialog.show()
//
//                dialog.setOnDismissListener {
//                    mediaPlayer?.stop()
//
//                    mediaPlayer?.release()
//                    mediaPlayer = null
//                }
//                acceptButton.setOnClickListener {
//                    ticketdata.setTicketid(hashMap["SourceID"]!!.toString())
//                    ticketdata.setEmployeeId(hashMap["SalesEmployeeCode"]!!.toString())
//                    ticketdata.setTicketStatus("Accepted")
//                    typo = "Accepted"
//                    callAcceptrejectApi(ticketdata)
//                }
//                rejectButton.setOnClickListener {
//                    ticketdata.setTicketid(hashMap["SourceID"]!!.toString())
//                    ticketdata.setEmployeeId(hashMap["SalesEmployeeCode"]!!.toString())
//                    ticketdata.setTicketStatus("Rejected")
//                    typo = "Rejected"
//                    callAcceptrejectApi(ticketdata)
//                }
//
//
//            }
//        })
//
//
//    }



    companion object {
        fun getToken(context: Context): String? {
            return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty")
        }

        private const val TAG = "FirebaseMessageReceiver"
    }


    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_massaed_logo_final)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)

            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel (for Android Oreo and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}