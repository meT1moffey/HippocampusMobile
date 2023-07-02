package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi

class ExplorerActivity : Activity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.explorer_activiry)

        val path = intent.extras?.getString("path")
        val manager = if (path != null)
            EncodedFilesManager(path)
        else
            EncodedFilesManager()

        findViewById<Button>(R.id.goto_encode).setOnClickListener {
            val encoder = Intent(this, EncodeActivity::class.java)
                .putExtra("path", manager.relativePath())
            startActivity(encoder)
        }
        findViewById<Button>(R.id.new_dir).setOnClickListener {
            val textField = EditText(this)

            AlertDialog.Builder(this)
                .setTitle("New directory")
                .setMessage("Enter directory name")
                .setView(textField)
                .setPositiveButton("Done") { _, _ ->
                    manager.makeDirectory(textField.text.toString())
                    recreate()
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        }
        if(manager.parentDirectory()?.isNotEmpty() == true) {
            findViewById<Button>(R.id.back_button).setOnClickListener {
                intent.putExtra("path", manager.parentDirectory())
                recreate()
            }
        }
        else {
            findViewById<Button>(R.id.back_button).visibility = View.INVISIBLE
        }

        val files = manager.filesList()

        val scroll = findViewById<LinearLayout>(R.id.explorer)
        if(files.isEmpty()) {
            val textView = TextView(this)
            textView.text = "There is no files available"
            scroll.addView(textView)
        }
        else {
            files.forEach { name ->
                val file = Button(this)
                file.text = name
                if(manager.fileExist(name)) file.setOnClickListener {
                    val decoder = Decoder(manager.relativePath(), this)
                    decoder.show(name)
                }
                else file.setOnClickListener {
                    intent.putExtra("path", manager.directoryRelativePath(name))
                    recreate()
                }

                scroll.addView(file)
            }
        }
    }
}