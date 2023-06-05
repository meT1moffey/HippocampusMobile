package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi

class ViewActivity : Activity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(intent.extras?.getString("type")) {
            "Text" -> {
                setContentView(R.layout.text_view_activity)
                Log.d("log", intent.extras?.getString("text")!!)
                findViewById<TextView>(R.id.text_show).text = intent.extras?.getString("text")
            }
            else -> {
                // Unreachable
                return
            }
        }
    }
}