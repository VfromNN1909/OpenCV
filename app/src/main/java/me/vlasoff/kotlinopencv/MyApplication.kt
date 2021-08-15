package me.vlasoff.kotlinopencv

import android.app.Application
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.ui.DocumentScanner

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = DocumentScanner.Configuration().apply {
            imageQuality = 100
            imageSize = 1000000
            imageType = Bitmap.CompressFormat.JPEG
        }
        DocumentScanner.init(this, config)
    }

}