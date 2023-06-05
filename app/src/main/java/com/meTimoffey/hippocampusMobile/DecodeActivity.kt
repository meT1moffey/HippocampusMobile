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
import android.widget.Toast
import androidx.annotation.RequiresApi

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

        findViewById<Button>(R.id.show).setOnClickListener {
            val key = findViewById<EditText>(R.id.decode_key_field).text.toString()

            if (key.isEmpty()) {
                Toast.makeText(this, "Enter decode key", Toast.LENGTH_LONG).show()
            }
            else {
                val file = manager.load(intent.extras?.getString("filename")!!, key)
                if(file == null) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val view = Intent(this, DecodeActivity::class.java)
                when(dropdown.selectedItem.toString()) {
                    "Text" -> view.putExtra("text", file.toString())
                    "Image" ->
                        try {
                            view.putExtra("image", BitmapFactory.decodeByteArray(file, 0, file.size)!!)
                        }
                        catch(e: NullPointerException) {
                            Toast.makeText(this, "Key is incorrect or file is not an image", Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                }

                //startActivity(view)
            }
        }
        findViewById<Button>(R.id.back).setOnClickListener {
            startActivity(Intent(this, ExplorerActivity::class.java))
        }
    }
}