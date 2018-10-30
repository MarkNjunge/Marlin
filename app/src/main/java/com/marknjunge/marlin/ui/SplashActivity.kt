package com.marknjunge.marlin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.marknjunge.marlin.App
import com.marknjunge.marlin.ui.login.LoginActivity
import com.marknjunge.marlin.ui.main.MainActivity
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If the user is null, they are not logged in. Go to LoginActivity
        // If the user is not null, they already logged in. Go to MainActivity
        if (App.preferencesStorage.user == null) {
            Timber.d("User is not logged in")
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        } else {
            Timber.d("User already logged in")
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

    }

}
