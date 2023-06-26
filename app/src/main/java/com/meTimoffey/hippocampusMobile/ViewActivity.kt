package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.nio.charset.Charset

class ViewActivity : Activity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.getByteArrayExtra("data")!!
        val view: View
        when(intent.extras?.getString("type")) {
            "Text" -> {
                view = TextView(this)
                view.text = data.toString(Charset.defaultCharset())
            }
            "Image" -> {
                view = ImageView(this)
                view.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.size))
            }
            else -> {
                // Unreachable
                return
            }
        }
        setContentView(view)
    }
}