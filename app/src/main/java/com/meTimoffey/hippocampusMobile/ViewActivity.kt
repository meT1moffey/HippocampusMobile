package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi

class ViewActivity : Activity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view: View
        when(intent.extras?.getString("type")) {
            "Text" -> {
                view = TextView(this)
                view.text = intent.extras?.getString("text")
            }
            else -> {
                // Unreachable
                return
            }
        }
        setContentView(view)
    }
}