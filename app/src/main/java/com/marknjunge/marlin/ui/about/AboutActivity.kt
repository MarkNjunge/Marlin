package com.marknjunge.marlin.ui.about

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.marknjunge.marlin.BuildConfig
import com.marknjunge.marlin.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.title = "About"

        tvVersion.text = "Version ${BuildConfig.VERSION_NAME}"
        tvSource.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MarkNjunge/Marlin")))
        }
    }
}
