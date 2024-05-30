package com.ahuja.sons.service

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ahuja.sons.R
import com.ahuja.sons.globals.Global
import com.pixplicity.easyprefs.library.Prefs


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("newToken", token)
        //  getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply()
        Prefs.putString(Global.FCM, token)

    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.e(TAG, "onMessageReceivedMYFIREEE===>: ", )

        if (message.notification != null) {

            Log.e("TAG", "onMessageReceived: ", )
            showCustomDialog(applicationContext)

        }
    }

    companion object {
        fun getToken(context: Context): String {
            return Prefs.getString(Global.FCM,"")
        }
        private const val TAG = "MyFirebaseMessagingServ"
    }


    private fun showCustomDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_man_trap_rescue)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // var tvAnnounce:TextView= dialog.findViewById<TextView>(R.id.tvAnnouncement)
        var bellIcon: ImageView = dialog.findViewById<ImageView>(R.id.imageBellIcon)
        Glide.with(context).asGif().load(R.raw.alertsos).into(bellIcon)
        var mediaPlayer: MediaPlayer? = null
        mediaPlayer = MediaPlayer.create(this, R.raw.sos_tone)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

//        tvAnnounce.setOnClickListener {
//            Log.e(TAG, "showCustomDialog: clickkkkkkk", )
//        }

        // Customize the dialog as needed, e.g., set text, button listeners, etc.

        dialog.show()

        dialog.setOnDismissListener {
            mediaPlayer?.stop()

            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}