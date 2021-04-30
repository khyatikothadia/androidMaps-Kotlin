package com.example.myapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.preferences.PreferenceManager
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        loadAnimation()
        launchHomeScreen()
    }

    /**
     * Method to start and load animation
     */
    private fun loadAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_anim)
        activitySplashImageView.startAnimation(animation)
    }

    /**
     * Method to launch login or maps screen based on auth token
     */
    private fun launchHomeScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (TextUtils.isEmpty(PreferenceManager.getInstance(this).getAuthToken())) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, MapsActivity::class.java))
                finish()
            }
        }, 3000)
    }
}