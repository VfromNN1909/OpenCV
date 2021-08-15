package me.vlasoff.kotlinopencv

import android.app.Application
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.ui.DocumentScanner

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DocumentScanner.init(this)
    }

}