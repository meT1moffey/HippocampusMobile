package com.meTimoffey.hippocampusMobile

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.RequiresApi
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

abstract class ViewActivity : Activity() {
    protected abstract fun makeView(data: ByteArray) : View?

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = intent.extras?.getString("path")!!
        val data = EncodedFilesManager(path).quickLoad()!!
        val view = makeView(data)

        if (view == null) {
            Toast.makeText(this, "Key is incorrect or has been chosen incorrect file type", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        setContentView(view)
    }
}

class TextViewActivity : ViewActivity() {
    override fun makeView(data: ByteArray): View {
        val view = TextView(this)
        view.text = data.toString(Charset.defaultCharset())
        return view
    }
}

class ImageViewActivity : ViewActivity() {
    override fun makeView(data: ByteArray): View? {
        val view = ImageView(this)
        val img = BitmapFactory.decodeByteArray(data, 0, data.size) ?: return null
        view.setImageBitmap(img)
        return view
    }
}

class VideoViewActivity : ViewActivity() {
    override fun makeView(data: ByteArray): View {
        val view = VideoView(this)
        val manager = EncodedFilesManager()
        manager.save(ByteArrayInputStream(data), "__cache__", "")
        view.setVideoPath(manager.absolutePath() + "/__cache__")
        view.offsetLeftAndRight(0)

        val controller = MediaController(this)
        controller.setMediaPlayer(view)
        view.setMediaController(controller)

        view.start()
        return view
    }
}