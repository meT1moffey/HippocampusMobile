package com.meTimoffey.hippocampusMobile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

class Decoder(path: String, private val context: Context) {
    companion object {
        enum class Filetype { Text, Image }
        val typeStrings = mapOf(
            "Text" to Filetype.Text,
            "Image" to Filetype.Image
        )
        val fileSuffixes = mapOf(
            Filetype.Text to ".txt",
            Filetype.Image to ".jpg"
        )
    }

    private val viewActivities = mapOf(
        Filetype.Text to TextViewActivity::class.java,
        Filetype.Image to ImageViewActivity::class.java
    )

    private val manager = EncodedFilesManager(path)

    private lateinit var filename: String

    private fun isEncoded() = filename.length >= 3 && filename.takeLast(3) == ".vo"
    private fun unencodedFilename() = if(isEncoded()) filename.dropLast(3) else filename
    private fun filetype() = fileSuffixes.toList().firstOrNull {
        it.second == "." + unencodedFilename().split(".").last()}?.first

    private fun launch(key: String = "") {
        val file = manager.load(filename, key)
        if(file == null) {
            Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
            return
        }

        val view = Intent(context, viewActivities[filetype()])

        view.putExtra("path", manager.relativePath())
        manager.quickSave(file)

        context.startActivity(view)
    }

    private fun selectFiletype() {
        val dropdown = Spinner(context)
        dropdown.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, viewActivities.keys.toTypedArray())

        AlertDialog.Builder(context)
            .setTitle("Select file type")
            .setMessage("File type has not been recognised")
            .setView(dropdown)
            .setPositiveButton("Done") { _, _ ->
                val newFiletype = typeStrings[dropdown.selectedItem.toString()]

                val newName = unencodedFilename() + newFiletype + if(isEncoded()) ".vo" else ""

                manager.renameFile(filename, newName)
                show(newName)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }


    fun show(filename: String) {
        this.filename = filename

        if(filetype() == null) {
            selectFiletype()
            return
        }

        if(!isEncoded())
            launch()
        else {
            val keyField = EditText(context)
            keyField.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

            AlertDialog.Builder(context)
                .setTitle(filename)
                .setMessage("Enter file key")
                .setView(keyField)
                .setPositiveButton("Done") { _, _ ->
                    launch(keyField.text.toString())
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        }
    }
}