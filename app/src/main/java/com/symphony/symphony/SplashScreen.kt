package com.symphony.symphony

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val SPLASH_DELAY_TIME = 3000L
        val handler = Handler()

        // Check if there's a stored username, password and userID
        val sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)
        val password = sharedPref.getString("password", null)
        val userID = sharedPref.getString("userID", null)


        handler.postDelayed({


            if (username != null && password != null && userID != null) {
                // If stored credentials exist, go to the main activity

                val intent = Intent(this, TechnicianDashboard::class.java)
                startActivity(intent)
            } else {

                // If no stored credentials exist, go to the login activity

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("id", userID)
                startActivity(intent)
            }

            // Finish the splash screen activity
            finish()
        }, SPLASH_DELAY_TIME)
    }
}
