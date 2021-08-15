package me.vlasoff.kotlinopencv

import android.app.Activity
import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc.*
import org.opencv.objdetect.CascadeClassifier
import org.opencv.objdetect.Objdetect
import java.io.File
import kotlin.math.round
import kotlin.math.roundToInt

object FaceDetection {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private const val faceModel = "haarcascade_frontalface_alt.xml"

    private lateinit var faceCascade: CascadeClassifier

    private var absoluteFaceSize = 0

    fun loadModel(activity: Activity) {
        faceCascade = CascadeClassifier(
            File(activity.filesDir, "das")
                .apply { writeBytes(activity.assets.open(faceModel).readBytes()) }.path
        )
    }

    fun detectFaces(image: Bitmap): Int {

        var size: Int = 0
        scope.launch {
            val frame = Mat()
            Utils.bitmapToMat(image, frame)

            val faces = MatOfRect()
            val grayFrame = Mat()

            cvtColor(frame, grayFrame, COLOR_BGR2GRAY)
            equalizeHist(grayFrame, grayFrame)

            if (absoluteFaceSize == 0) {
                val height = grayFrame.rows()
                if (round(height * 0.2f) > 0) {
                    absoluteFaceSize = (height * 0.2f).roundToInt()
                }
            }
            faceCascade.detectMultiScale(
                grayFrame, faces, 1.1, 3, Objdetect.CASCADE_SCALE_IMAGE,
                Size(absoluteFaceSize.toDouble(), absoluteFaceSize.toDouble()), Size()
            )
            size = faces.toArray().size
        }

        return size
    }

    private fun Mat.toGrayScale(): Mat =
        if (channels() >= 3) Mat()
            .apply { cvtColor(this@toGrayScale, this, COLOR_BGR2GRAY) }
        else this

    private fun Mat.prepare(): Mat {
        val mat = toGrayScale()
        equalizeHist(mat, mat)
        return mat
    }
}