package me.vlasoff.kotlinopencv

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import kotlinx.coroutines.*
import org.opencv.android.Utils
import org.opencv.calib3d.Calib3d
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import java.util.*


object PassportRecognition {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    fun detect(context: Context, imageToRecognize: Bitmap): Int {

        var score: Int = 0

        val orb = ORB.create()
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2)


        // firstImage
        var img1 =
            Utils.loadResource(context, R.drawable.main_page_template, CvType.CV_8UC4)
        scope.launch {
            img1 = addGrayscale(img1)
            val descriptors1 = Mat()
            val keyPoints1 = MatOfKeyPoint()
            orb.detect(img1, keyPoints1)
            orb.compute(img1, keyPoints1, descriptors1)

            // second image
            var img2 = Mat()
            val bmp32 = imageToRecognize.copy(Bitmap.Config.ARGB_8888, true)
            Utils.bitmapToMat(bmp32, img2)

            img2 = addGrayscale(img2)
            val descriptors2 = Mat()
            val keyPoints2 = MatOfKeyPoint()
            orb.detect(img2, keyPoints2)
            orb.compute(img2, keyPoints2, descriptors2)

            val matches = mutableListOf<MatOfDMatch>()
            matcher.knnMatch(descriptors1, descriptors2, matches, 5)

            val goodMatches = LinkedList<DMatch>()
            val iterator: Iterator<MatOfDMatch> = matches.iterator()
            while (iterator.hasNext()) {
                val matOfDMatch = iterator.next()
                if (matOfDMatch.toArray()[0].distance < 0.7f * matOfDMatch.toArray()[1].distance) {
                    goodMatches.add(matOfDMatch.toArray()[0])
                }
            }

//        val dist = Core.norm(img1, img2)

//        val pts1 = mutableListOf<Point>()
//        val pts2 = mutableListOf<Point>()
//        for (i in 0 until goodMatches.size) {
//            pts1.add(keyPoints1.toList()[goodMatches[i].queryIdx].pt)
//            pts2.add(keyPoints2.toList()[goodMatches[i].trainIdx].pt)
//        }
//
//        val outputMask = Mat()
//        val pts1Mat = MatOfPoint2f()
//        pts1Mat.fromList(pts1)
//        val pts2Mat = MatOfPoint2f()
//        pts2Mat.fromList(pts2)
//
//        val homog =
//            Calib3d.findHomography(pts1Mat, pts2Mat, Calib3d.RANSAC, 15.0, outputMask, 2000, 0.995)
//
//        val betterMatches = LinkedList<DMatch>()
//        for (i in 0 until goodMatches.size) {
//            if (outputMask[i, 0][0] != 0.0) {
//                betterMatches.add(goodMatches[i])
//            }
//        }


//        Toast.makeText(context, goodMatches.size.toString(), Toast.LENGTH_SHORT).show()
//        Toast.makeText(context, dist.toString(), Toast.LENGTH_LONG).show()
            score = goodMatches.size
        }

        return score
    }
}