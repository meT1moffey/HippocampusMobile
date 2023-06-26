package com.meTimoffey.hippocampusMobile

import android.os.Environment
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.experimental.xor


class EncodedFilesManager(directoryName : String = "Encoded Files") {
    private val dir = File(Environment.getExternalStorageDirectory().absolutePath, directoryName)
    private val cache = "__cache__"
    private val cache_key = "enough_secure_key"

    private fun getFile(filename: String): File {
        if (!dir.exists())
            dir.mkdir()

        return File(dir, filename)
    }

    private fun ByteArray.code(key: ByteArray) : ByteArray {
        if(key.isNotEmpty()) this.forEachIndexed { idx, value ->
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

    fun quick_save(bytes: ByteArray) = save(ByteArrayInputStream(bytes), cache, cache_key)
    fun quick_load() = load(cache, cache_key)

    fun load(filename: String, key: String): ByteArray? {
        val file = getFile(filename)
        return try {
            FileInputStream(file).use { stream ->
                stream.readBytes().code(key.toByteArray())
            }
        } catch (e: FileNotFoundException) {
            null
        }
    }

    fun filesList() : Array<out String>? = dir.list()
}