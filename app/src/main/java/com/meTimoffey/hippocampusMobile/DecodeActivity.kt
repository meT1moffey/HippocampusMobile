package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.nio.charset.Charset

class DecodeActivity : Activity() {
    private val manager = EncodedFilesManager()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decode_activity)

        val dropdown = findViewById<Spinner>(R.id.show_type)
        val items = arrayOf("Text", "Image")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter

        val filename = intent.getStringExtra("filename")!!
        findViewById<TextView>(R.id.file_name).text = filename

        findViewById<Button>(R.id.show).setOnClickListener {
            val key = findViewById<EditText>(R.id.decode_key_field).text.toString()

            val file = manager.load(filename, key)
            if(file == null) {
                Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val view = Intent(this, ViewActivity::class.java)

            view.putExtra("type", dropdown.selectedItem.toString())
            view.putExtra("data", file)
            when(dropdown.selectedItem.toString()) {
                "Image" ->
                    if(BitmapFactory.decodeByteArray(file, 0, file.size) == null) {
                        Toast.makeText(this, "Key is incorrect or file is not an image", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
            }

            startActivity(view)
        }
    }
}