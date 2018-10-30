package com.marknjunge.marlin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.marknjunge.marlin.data.local.PreferencesStorage
import com.marknjunge.marlin.ui.login.LoginActivity
import com.marknjunge.marlin.ui.main.MainActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

class SplashActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val prefs: PreferencesStorage by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If the user is null, they are not logged in. Go to LoginActivity
        // If the user is not null, they already logged in. Go to MainActivity
        if (prefs.user == null) {
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
