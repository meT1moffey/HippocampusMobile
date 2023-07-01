package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi

class DecodeActivity : Activity() {
    private val view_activities = mapOf(
        "Text"  to TextViewActivity::class.java,
        "Image" to ImageViewActivity::class.java
    )
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decode_activity)

        val path = intent.extras?.getString("path")!!
        val manager = EncodedFilesManager(path)

        val dropdown = findViewById<Spinner>(R.id.show_type)
        val items = view_activities.keys.toTypedArray()
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

            val view = Intent(this, view_activities[dropdown.selectedItem.toString()])

            view.putExtra("path", path)
            manager.quickSave(file)

            startActivity(view)
        }
    }
}