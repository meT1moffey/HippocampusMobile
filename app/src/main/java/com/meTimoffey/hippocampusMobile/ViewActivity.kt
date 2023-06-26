package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.nio.charset.Charset

class ViewActivity : Activity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = intent.extras?.getString("path")!!
        val data = EncodedFilesManager(path).quickLoad()!!
        val view: View
        when(intent.extras?.getString("type")) {
            "Text" -> {
                view = TextView(this)
                view.text = data.toString(Charset.defaultCharset())
            }
            "Image" -> {
                view = ImageView(this)
                val img = BitmapFactory.decodeByteArray(data, 0, data.size)
                if (img == null) {
                    Toast.makeText(this, "Key is incorrect or file is not an image", Toast.LENGTH_LONG).show()
                    finish()
                }
                view.setImageBitmap(img)
            }
            else -> {
                // Unreachable
                return
            }
        }
        setContentView(view)
    }
}