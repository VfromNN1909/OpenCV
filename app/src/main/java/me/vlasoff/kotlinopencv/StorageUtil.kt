package me.vlasoff.kotlinopencv

import android.R.attr
import android.content.Context
import android.os.Build
import java.io.*


inline fun <T> sdk29AndUp(onSdk29: () -> T): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null


/**
 * Function to copy assets into external storage
 *
 * @param path - full path to trained data in external - "/storage/emulated/0//tessdata/eng.traineddata"
 *
 * @param name - file name - eng.traineddata
 *
 * @return path to created file
 */
fun assetsToExternal(context: Context, path: String, name: String): String {

    val f = File(path)
    if (f.exists())
        f.delete()

    if (!f.exists()) {
        val p = File(f.parent as String)
        if (!p.exists()) {
            p.createNewFile()
        }
        try {
            f.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    var inputStream: InputStream? = null
    var os: OutputStream? = null
    try {
        inputStream = context.assets.open(name)
        val file: File = File(path)
        os = FileOutputStream(file)
        val bytes = ByteArray(2048)
        var len = 0
        while (inputStream.read(bytes).also { len = it } != -1) {
            os.write(bytes, 0, len)
        }
        os.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return f.absolutePath
}
