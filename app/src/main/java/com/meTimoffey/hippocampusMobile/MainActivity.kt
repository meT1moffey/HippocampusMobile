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
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.experimental.xor


class MainActivity : AppCompatActivity() {
    private val directoryName = "Encoded Files"
    private val filename = "test.jpg.vo"

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

    private fun getFile(filename: String): File? {
        if (!storageAvalible()) {
            requestStoragePermission()
            return null
        }
        else {
            val dir = File(Environment.getExternalStorageDirectory().absolutePath, directoryName)
            if (!dir.exists())
                dir.mkdir()

            return File(dir, filename)
        }
    }

    private fun ByteArray.code(key: ByteArray) : ByteArray {
        this.forEachIndexed { idx, value ->
            this[idx] = value xor key[idx % key.size]
        }
        return this
    }

    private fun save(stream: InputStream, filename: String, key: String) {
        val file = getFile(filename) ?: return
        file.createNewFile()
        val saveStream = FileOutputStream(file)

        saveStream.write(stream.readBytes().code(key.toByteArray()))
        stream.close()
        saveStream.close()
    }

    private fun load(filename: String, key: String): ByteArray? {
        val file = getFile(filename) ?: return null
        return try {
            val fileStream = FileInputStream(file)

            fileStream.readBytes().code(key.toByteArray())
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
            null
        }
    }

    private val loadUri = registerForActivityResult(ActivityResultContracts.GetContent()) { newUri: Uri? ->
        val stream = contentResolver.openInputStream(newUri ?: return@registerForActivityResult)!!

        save(stream, filename, "123")
    }

    private fun show() {
        val image = load(filename, "123") ?: return
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