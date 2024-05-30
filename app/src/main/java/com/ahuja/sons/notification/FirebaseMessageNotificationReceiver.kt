package com.ahuja.sons.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ahuja.sons.R
import com.ahuja.sons.activity.LoginActivity

class FirebaseMessageNotificationReceiver : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("Token2...", "------> Token Received => $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("message...", "message Received")
        if(remoteMessage.data.isNotEmpty() && remoteMessage.data == null){ //todo on get notification application crash everytime due to NullPointerException.. solution is getting notification data while getting null also..
            showNotification(remoteMessage.getData().get("title").toString(), remoteMessage.data.get("message")!!);
        }
        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification!!.title.toString(), remoteMessage.notification!!.body!!)
        }

    }

    private fun getCustomDesign(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.icon, R.drawable.wae_logo)
        return remoteViews
    }

/*    fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken: String = FirebaseInstanceId.getInstance().getToken()
        Log.d("TAG", "Refreshed token: $refreshedToken")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }*/


    //todo Method to display the notifications
    fun showNotification(title: String?, message: String?) {
        Log.e("Notification click...", "------> Activity show")
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = it.result //this is the token retrieved
            Log.e("Token1...", "------> Token Received => $token")
        }

        val intent = Intent(this, LoginActivity::class.java)
        val channel_id = "notification_channel"
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT )//PendingIntent.FLAG_ONE_SHOT
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channel_id)
            .setSmallIcon(R.drawable.notification_icon)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
        ) {
            builder = builder.setContent(
                getCustomDesign(title!!, message!!)
            )
        } // If Android Version is lower than Jelly Beans,
        else {
            builder = builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_icon)
        }
        var notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channel_id, "web_app",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }




}