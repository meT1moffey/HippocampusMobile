package com.meTimoffey.hippocampusMobile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi

class ExplorerActivity : Activity() {
    @SuppressLint("SetTextI18n", "WrongViewCast")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.explorer_activiry)

        findViewById<Button>(R.id.goto_encode).setOnClickListener {
            startActivity(Intent(this, EncodeActivity::class.java))
        }

        val files = EncodedFilesManager().filesList()

        val scroll = findViewById<LinearLayout>(R.id.explorer)
        if(files == null) {
            val textView = TextView(this)
            textView.text = "There is no files available"
            scroll.addView(textView)
        }
        else {
            files.forEach { name ->
                val file = Button(this)
                file.text = name
                file.setOnClickListener {
                    val decoder = Intent(this, DecodeActivity::class.java)
                    decoder.putExtra("filename", name)
                    startActivity(decoder)
                }

                scroll.addView(file)
            }
        }
    }
}