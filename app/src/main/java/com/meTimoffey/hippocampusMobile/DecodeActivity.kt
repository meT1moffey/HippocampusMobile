package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi

class DecodeActivity : Activity() {
    private val viewActivities = mapOf(
        "Text"  to TextViewActivity::class.java,
        "Image" to ImageViewActivity::class.java
    )
    private val fileSuffixes = arrayOf(
        "Text" to "txt",
        "Image" to "jpg"
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decode_activity)

        val path = intent.extras?.getString("path")!!
        val manager = EncodedFilesManager(path)

        val filename = intent.getStringExtra("filename")!!
        findViewById<TextView>(R.id.file_name).text = filename

        val nameParts = filename.split('.')
        val isEncoded = nameParts.last() == "vo" && nameParts.size > 1 // file named "vo" is not a *.vo file
        val filetype = (fileSuffixes.firstOrNull {
                it.second == if (isEncoded) nameParts.dropLast(1).last() else nameParts.last()})?.first

        if(filetype == null) {
            val dropdown = Spinner(this)
            val items = viewActivities.keys.toTypedArray()
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
            dropdown.adapter = adapter

            AlertDialog.Builder(this)
                .setTitle("Select file type")
                .setMessage("File type has not been recognised")
                .setView(dropdown)
                .setPositiveButton(
                    "Done"
                ) { _, _ ->
                    val newFiletype = "." + (fileSuffixes.firstOrNull {
                    it.first == dropdown.selectedItem.toString()})?.second

                    val newName = if(isEncoded) nameParts.dropLast(1).joinToString(".") + newFiletype +  ".vo"
                                  else filename + newFiletype
                    manager.renameFile(filename, newName)
                    intent.putExtra("filename", newName)
                    recreate()
                }
                .setNegativeButton(
                    "Cancel"
                ) { _, _ -> finish()}
                .show()
        }

        findViewById<Button>(R.id.show).setOnClickListener {
            val key = findViewById<EditText>(R.id.decode_key_field).text.toString()
            val file = manager.load(filename, key)
            if(file == null) {
                Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val view = Intent(this, viewActivities[filetype])

            view.putExtra("path", path)
            manager.quickSave(file)

            startActivity(view)
        }
    }
}