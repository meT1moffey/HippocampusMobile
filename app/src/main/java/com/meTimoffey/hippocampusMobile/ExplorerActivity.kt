package com.meTimoffey.hippocampusMobile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ExplorerActivity : AppCompatActivity() {
    private lateinit var manager: EncodedFilesManager

    private fun goBack() {
        if(manager.parentDirectory() != null) {
            intent.putExtra("path", manager.parentDirectory())
            recreate()
        }
        else {
            Toast.makeText(this, "This is root directory", Toast.LENGTH_LONG).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.explorer_activiry)
        setSupportActionBar(findViewById(R.id.explorer_menu))

        val path = intent.extras?.getString("path")
        manager = if (path != null)
            EncodedFilesManager(path)
        else
            EncodedFilesManager()
    }

    override fun onResume() {
        super.onResume()
        val files = manager.filesList()

        val scroll = findViewById<LinearLayout>(R.id.explorer)
        scroll.removeAllViews()

        if (files.isEmpty()) {
            val textView = TextView(this)
            textView.text = "There is no files available"
            scroll.addView(textView)
        }
        else {
            files.forEach { name ->
                val file = Button(this)
                file.text = name
                if (manager.fileExist(name)) file.setOnClickListener {
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.explorer_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.add_file -> {
            val encoder = Intent(this, EncodeActivity::class.java)
                .putExtra("path", manager.relativePath())
            startActivity(encoder)
            true
        }
        R.id.back_button -> {
            goBack()
            true
        }
        R.id.make_dir -> {
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
            true
        }
        else -> {
            // Unreachable
            super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() = goBack()
}