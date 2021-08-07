package me.vlasoff.kotlinopencv

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc

object PassportMatching {

    private const val TAG = "MATCHING"
    private const val SUCCESS = "PASSPORT DETECTED"
    private const val FAIL = "PASSPORT NOT FOUND"


    fun matchPassport(
        context: Context,
        inFile: String,
        tempFile: String,
        matchMethod: Int
    ) {
        Log.i(TAG, "Running passport matching")

        val img = Utils.loadResource(context, R.drawable.main_page_template, CvType.CV_8UC4)
        val temp = Utils.loadResource(context, R.drawable.main_page_test_1, CvType.CV_8UC4)

        // result matrix
        val resCols = img.cols() - temp.cols() + 1
        val resRows = img.rows() - temp.rows() + 1
        val result = Mat(resRows, resCols, CvType.CV_32FC1)

        // matching and normalize
        Imgproc.matchTemplate(img, temp, result, matchMethod)
        Core.normalize(result, result, 0.0, 1.0, Core.NORM_MINMAX, -1, Mat())

        while (true) {
            // localizing the best match with minMaxLoc
            val mmr = Core.minMaxLoc(result)

            val matchLoc: Point =
                if (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
                    mmr.minLoc
                } else {
                    mmr.maxLoc
                }

            if (mmr.minVal >= 0.97) {
                // visualize detection, but we don't need this
//                Imgproc.rectangle(
//                    img, matchLoc,
//                    Point(matchLoc.x + temp.cols(), matchLoc.y + temp.rows()),
//                    Scalar(0.0, 255.0, 0.0), 2
//                )
//                Imgproc.rectangle(
//                    result, matchLoc,
//                    Point(matchLoc.x + temp.cols(), matchLoc.y + temp.rows()),
//                    Scalar(0.0, 255.0, 0.0), -1
//                )
                Toast.makeText(context, SUCCESS, Toast.LENGTH_SHORT).show()
                Log.i(SUCCESS, "Passport detected!")
                break

            } else {
                // No more results within tolerance, break search
                Toast.makeText(context, FAIL, Toast.LENGTH_SHORT).show()
                Log.i(FAIL, "Passport not detected!")
                break
            }
        }
    }

}