package com.meTimoffey.hippocampusMobile

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val manager = EncodedFilesManager("Encoded Files")
    private val filename = "test.jpg.vo"
    private val code = "123"

    private fun storageAvalible(): Boolean {
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

    private val loadUri = registerForActivityResult(ActivityResultContracts.GetContent()) { newUri: Uri? ->
        val stream = contentResolver.openInputStream(newUri ?: return@registerForActivityResult)!!

        manager.save(stream, filename, code)
    }

    private fun show() {
        val image = manager.load(filename, code)
        if(image == null) {
            Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
            return
        }
        val selectedImage = BitmapFactory.decodeByteArray(image, 0, image.size)

        findViewById<ImageView>(R.id.main_image).setImageBitmap(selectedImage)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!storageAvalible())
            requestStoragePermission()

        findViewById<Button>(R.id.select_button).setOnClickListener {
            if(!storageAvalible())
                requestStoragePermission()
            else
                loadUri.launch("image/*")
        }
        findViewById<Button>(R.id.show_button).setOnClickListener { show() }
    }
}