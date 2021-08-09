package me.vlasoff.kotlinopencv

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

class PassportDataRecognition(
    private val context: Context
) {

    companion object {
        const val SUCCESS = "OPENCV_SUCCESS"
        const val ERROR = "OPENCV_ERROR"
        val FILE_PATH =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "TessData" + File.separator + "tessdata" + File.separator

        const val FILE_NAME = "tessdata/eng.traineddata"

    }

    private var readPermissionGranted = false
    private var writePermissionGranted = false
//    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>


    fun getData(): String {
        TessDataManager.initTessTrainedData(context)
        val image = Utils.loadResource(
            context,
            R.drawable.test5,
            CvType.CV_8UC4
        )

        val gray = Mat()
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)

//        val target = Mat()
//        Imgproc.adaptiveThreshold(
//            gray,
//            target,
//            255.0,
//            Imgproc.ADAPTIVE_THRESH_MEAN_C,
//            Imgproc.THRESH_BINARY,
//            15,
//            40.0
//        )
        return try {
            val bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(gray, bitmap)
            val api = TessBaseAPI()
            api.apply {
                init("/data/data/me.vlasoff.kotlinopencv/files/tesseract", "eng")
                pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_ONLY
                setVariable(
                    TessBaseAPI.VAR_CHAR_WHITELIST,
                    ",.0123456789<>ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                )
                setImage(bitmap)
            }
            Log.d(SUCCESS, api.utF8Text)
            api.utF8Text

        } catch (ex: Exception) {
            Log.e(ERROR, ex.message.toString())
            ""
        }

//        val bitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888)
//        Utils.matToBitmap(target, bitmap)

//        return PassportAnalyzer().runTextRecognition(context)
    }


//    private fun trainedDataToBytes(file: File): ByteArray {
//        val stream = context.assets.open("eng.traineddata", AssetManager.ACCESS_STREAMING)
//        val out = FileOutputStream(file)
//        val buff = ByteArray(1024)
//        val len: Int = stream.read(buff)
//        while (len > 0) {
//            out.write(buff, 0, len)
//        }
//        out.close()
//        stream.close()
//        return buff
//    }


    private fun saveTrainedDataToExternal(): String {
        var buf = ByteArray(1024)
        val data = File(context.getExternalFilesDir(FILE_PATH), FILE_NAME)
        try {
            val inStream = context.assets.open("tessdata/eng.traineddata")
            val fos = FileOutputStream(data)
            buf = ByteArray(1024)
            var len: Int
            while (inStream.read(buf).also { len = it } > 0) {
                fos.write(buf, 0, len)
            }
            inStream.close()
            fos.close()
        } catch (ex: Exception) {
            Log.e(ERROR, ex.message.toString())
        }
        return data.absolutePath
    }

}