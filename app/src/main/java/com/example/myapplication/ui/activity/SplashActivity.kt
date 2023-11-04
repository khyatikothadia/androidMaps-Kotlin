package com.example.myapplication.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySplashBinding
import com.example.myapplication.preferences.PreferenceManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        loadAnimation()
        launchHomeScreen()
    }

    /**
     * Method to start and load animation
     */
    private fun loadAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_anim)
        binding.activitySplashImageView.startAnimation(animation)
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