package com.example.home_automation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var appLogo: TextView
    private lateinit var appTitle: TextView
    private lateinit var appSubtitle: TextView
    private val splashTimeOut: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_splash)

        initializeViews()
        startAnimations()
        navigateToMainActivity()
    }

    private fun initializeViews() {
        progressBar = findViewById(R.id.progressBar)
        appLogo = findViewById(R.id.appLogo)
        appTitle = findViewById(R.id.appTitle)
        appSubtitle = findViewById(R.id.appSubtitle)
    }

    private fun startAnimations() {
        // Animate logo with scale and fade
        appLogo.alpha = 0f
        appLogo.scaleX = 0.3f
        appLogo.scaleY = 0.3f
        appLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()

        // Animate title with fade and slide
        appTitle.alpha = 0f
        appTitle.translationY = 50f
        appTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(800)
            .start()

        // Animate subtitle
        appSubtitle.alpha = 0f
        appSubtitle.translationY = 30f
        appSubtitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(1200)
            .start()

        // Animate progress bar
        progressBar.alpha = 0f
        progressBar.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(1600)
            .start()
    }

    private fun navigateToMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // Add smooth transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            finish()
        }, splashTimeOut)
    }

    override fun onBackPressed() {
        // Disable back button on splash screen
        // Do nothing - prevent user from going back
    }
}
