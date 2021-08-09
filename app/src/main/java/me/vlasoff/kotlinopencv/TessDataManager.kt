package me.vlasoff.kotlinopencv

import android.content.Context
import android.util.Log
import java.io.*


object TessDataManager {

    val TAG = "DBG_" + TessDataManager.javaClass.name

    const val tessDir = "tesseract"
    const val subDir = "tessdata"
    const val fileName = "eng.traineddata"

    private var trainedDataPath: String? = null

    private var tesseractFolder: String? = null

    private var initiated = false


    fun getTesseractFolder(): String? {
        return tesseractFolder
    }

    fun getTrainedDataPath(): String? = if (initiated)
        trainedDataPath
    else
        null


    fun initTessTrainedData(context: Context) {
        if (initiated) {
            return
        }
        val appFolder: File = context.filesDir
        val folder = File(appFolder, tessDir)
        if (!folder.exists()) {
            folder.mkdir()
        }
        tesseractFolder = folder.absolutePath
        val subfolder = File(folder, subDir)
        if (!subfolder.exists()) {
            subfolder.mkdir()
        }
        val file = File(subfolder, fileName)
        trainedDataPath = file.absolutePath
        Log.d(TAG, "Trained data filepath: $trainedDataPath")
        if (!file.exists()) {
            try {
                val bytes: ByteArray = readRawTrainingData(context) ?: return
                val fileOutputStream: FileOutputStream = FileOutputStream(file)
                fileOutputStream.write(bytes)
                fileOutputStream.close()
                initiated = true
                Log.d(TAG, "Prepared training data file")
            } catch (e: FileNotFoundException) {
                Log.e(
                    TAG, """
     Error opening training data file
     ${e.message}
     """.trimIndent()
                )
            } catch (e: IOException) {
                Log.e(
                    TAG, """
     Error opening training data file
     ${e.message}
     """.trimIndent()
                )
            }
        } else {
            initiated = true
        }
    }

    private fun readRawTrainingData(context: Context): ByteArray? {
        try {
            val fileInputStream: InputStream = context.resources
                .openRawResource(R.raw.eng_traineddata)
            val bos = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var bytesRead: Int
            while (fileInputStream.read(b).also { bytesRead = it } != -1) {
                bos.write(b, 0, bytesRead)
            }
            fileInputStream.close()
            return bos.toByteArray()
        } catch (e: FileNotFoundException) {
            Log.e(
                TAG, """
     Error reading raw training data file
     ${e.message}
     """.trimIndent()
            )
            return null
        } catch (e: IOException) {
            Log.e(
                TAG, """
     Error reading raw training data file
     ${e.message}
     """.trimIndent()
            )
        }
        return null
    }

}