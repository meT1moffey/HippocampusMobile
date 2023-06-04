package com.meTimoffey.hippocampusMobile

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream


class EncodeActivity : AppCompatActivity() {
    private val manager = EncodedFilesManager("Encoded Files")
    private var uri: Uri? = null

    private fun storageAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        val uri = Uri.parse("package:com.meTimoffey.hippocampusMobile")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning!")
            .setMessage("Please allow app to store files.")
            .setPositiveButton("Ok"
            ) { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            uri
                        )
                    )
                }
                else {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            }
        builder.create().show()
    }

    private val loadUri = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        this.uri = uri ?: return@registerForActivityResult
    }

//    private fun show() {
//        val image = manager.load(filename, code)
//        if(image == null) {
//            Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
//            return
//        }
//        val selectedImage = BitmapFactory.decodeByteArray(image, 0, image.size)
//
//        findViewById<ImageView>(R.id.main_image).setImageBitmap(selectedImage)
//    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encode_activity)

        if (!storageAvailable())
            requestStoragePermission()

        val dropdown = findViewById<Spinner>(R.id.file_type)
        val items = arrayOf("Text", "Image")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter

        findViewById<Button>(R.id.select_button).setOnClickListener {
            if(!storageAvailable())
                requestStoragePermission()
            else {
                when(dropdown.selectedItem.toString()) {
                    "Text" -> {
                        val textField = EditText(this)

                        AlertDialog.Builder(this)
                            .setTitle("Text input")
                            .setMessage("Enter text to encode")
                            .setView(textField)
                            .setPositiveButton(
                                "Done"
                            ) { _, _ ->
                                val cache = File(getExternalFilesDir(null), "__cache__")
                                cache.createNewFile()
                                FileOutputStream(cache).write(textField.text.toString().toByteArray())
                                uri = Uri.fromFile(cache)
                            }
                            .setNegativeButton(
                                "Cancel"
                            ) { _, _ -> }
                            .show()
                    }
                    "Image" -> loadUri.launch("image/*")
                }
            }
        }
        findViewById<Button>(R.id.launch).setOnClickListener {
            val name = findViewById<EditText>(R.id.name_field).text.toString()
            val key = findViewById<EditText>(R.id.key_field).text.toString()

            if(uri == null) {
                Toast.makeText(this, "Select file to encode", Toast.LENGTH_LONG).show()
            }
            else if(name.isEmpty()) {
                Toast.makeText(this, "Enter new file name", Toast.LENGTH_LONG).show()
            }
            else if(key.isEmpty()) {
                Toast.makeText(this, "Enter encode key", Toast.LENGTH_LONG).show()
            }
            else {
                val stream = contentResolver.openInputStream(uri!!)!!
                manager.save(stream, "$name.vo", key)
                Toast.makeText(this, "File encoded successfully", Toast.LENGTH_LONG).show()
            }
        }
        findViewById<Button>(R.id.goto_selection).setOnClickListener {
            startActivity(Intent(this, ExplorerActivity::class.java))
        }
    }
}