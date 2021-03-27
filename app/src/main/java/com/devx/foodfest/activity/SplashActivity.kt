package com.devx.foodfest.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.devx.foodfest.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        postDelay()
    }


    fun postDelay() {
        startSound()
        Handler().postDelayed({
            val sharedPreferences = this.getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )

            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)


            if (isLoggedIn) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val startAct = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(startAct)
                finish()
            }
        }, 2000)

        val animSlide = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out)
        splashImageView.startAnimation(animSlide)
    }

    private fun startSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.horn);
        }
        mediaPlayer?.start()
    }
}