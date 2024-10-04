package com.ahuja.sons.ahujaSonsClasses.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.ahuja.sons.R
import com.ahuja.sons.activity.LoginActivity
import com.ahuja.sons.databinding.ActivitySplashBinding
import com.pixplicity.easyprefs.library.Prefs
import java.util.concurrent.Executor

class SplashActivity : AppCompatActivity() {
    lateinit var bindind : ActivitySplashBinding
    private var isFirstAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bindind.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Hold animation for the ImageView
        val hold = AnimationUtils.loadAnimation(this, R.anim.hold)

        // Translate and scale animation for the ImageView
        val translateScale = AnimationUtils.loadAnimation(this, R.anim.translate_scale)

        val imageView: ImageView = findViewById(R.id.header_icon)

        // Animation listener for translateScale animation
        translateScale.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (!isFirstAnimation) {
                    imageView.clearAnimation()
                    login()
                }
                isFirstAnimation = true
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Animation listener for hold animation
        hold.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                imageView.clearAnimation()
                imageView.startAnimation(translateScale)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        // Start the hold animation
        imageView.startAnimation(hold)
    }

    private fun login() {
        if (Prefs.getBoolean(com.ahuja.sons.globals.Global.AutoLogIn, false)) {
            com.ahuja.sons.globals.Global.APILog = "Not"

            var session = 30L * 60 * 1000
            Prefs.putLong(com.ahuja.sons.globals.Global.SESSION_TIMEOUT, session)
            Prefs.putLong(com.ahuja.sons.globals.Global.SESSION_REMAIN_TIME, 0)

            val intent = Intent(this@SplashActivity, AhujaSonsMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}