package com.meTimoffey.hippocampusMobile

import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.experimental.xor

class EncodedFilesManager(directoryName: String) {
    private val dir = File(Environment.getExternalStorageDirectory().absolutePath, directoryName)

    private fun getFile(filename: String): File {
        if (!dir.exists())
            dir.mkdir()

        return File(dir, filename)
    }

    private fun ByteArray.code(key: ByteArray) : ByteArray {
        this.forEachIndexed { idx, value ->
            this[idx] = value xor key[idx % key.size]
        }
        return this
    }

    fun save(stream: InputStream, filename: String, key: String) {
        val file = getFile(filename)
        file.createNewFile()
        val saveStream = FileOutputStream(file)

        saveStream.write(stream.readBytes().code(key.toByteArray()))
        stream.close()
        saveStream.close()
    }

    fun load(filename: String, key: String): ByteArray? {
        val file = getFile(filename)
        return try {
            val fileStream = FileInputStream(file)

            fileStream.readBytes().code(key.toByteArray())
        } catch (e: FileNotFoundException) {
            null
        }
    }
}