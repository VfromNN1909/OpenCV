package me.vlasoff.kotlinopencv

import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.features2d.AKAZE
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.SIFT
import org.opencv.core.DMatch
import org.opencv.core.Scalar

import org.opencv.core.Core

import org.opencv.core.Mat

import org.opencv.core.CvType

import org.opencv.core.KeyPoint
import kotlin.math.pow
import org.opencv.core.MatOfDMatch

import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.ORB
import kotlin.math.roundToInt
import kotlin.math.sqrt


object PassportRecognition {

    fun detect(context: Context, imageToRecognize: Bitmap, faces: Int): Int {

        var score: Int = 0

//        val akaze = AKAZE.create()

        val sift = ORB.create()
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
        // firstImage
        var img1 = when (faces) {
            0 -> Utils.loadResource(context, R.drawable.reg_page, CvType.CV_8UC4)
            1 -> Utils.loadResource(context, R.drawable.main_page_template, CvType.CV_8UC4)
            else -> Utils.loadResource(context, R.drawable.registration_single_page, CvType.CV_8UC4)
        }
//        img1 = addGrayscale(img1)
        val descriptors1 = Mat()
        val keyPoints1 = MatOfKeyPoint()
        sift.detect(img1, keyPoints1)
        sift.compute(img1, keyPoints1, descriptors1)

        // second image
        var img2 = Mat()
        val bmp32 = imageToRecognize.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, img2)
//        img2 = addGrayscale(img2)
        val descriptors2 = Mat()
        val keyPoints2 = MatOfKeyPoint()
        sift.detect(img2, keyPoints2)
        sift.compute(img2, keyPoints2, descriptors2)

        val matches = mutableListOf<MatOfDMatch>()
        matcher.knnMatch(descriptors1, descriptors2, matches, 2)

        val nndrRatio = 0.8f

        val goodMatches = mutableListOf<DMatch>()
        for (i in 0 until matches.size) {
            val matOfDMatch = matches[i]
            val arr = matOfDMatch.toArray()
            val m1 = arr[0]
            val m2 = arr[1]
            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatches.add(m1)
            }
        }
        score = goodMatches.size / matches[0].toArray().size

        return score
    }

    fun akazeDetect(context: Context, imageToRecognize: Bitmap, faces: Int): Double {

        var score: Int = 0

        val akaze = AKAZE.create()
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
        // firstImage
        var img1 = when (faces) {
            0 -> Utils.loadResource(context, R.drawable.reg_page, CvType.CV_8UC4)
            1 -> Utils.loadResource(context, R.drawable.main_page_template, CvType.CV_8UC4)
            else -> Utils.loadResource(context, R.drawable.registration_single_page, CvType.CV_8UC4)
        }
        var img2 = Mat()
        val bmp32 = imageToRecognize.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, img2)

        val homography = Mat(3, 3, CvType.CV_64F)
//        val homoData = doubleArrayOf(
//            7.6285898e-01, -2.9922929e-01, 2.2567123e+02,
//            3.3443473e-01, 1.0143901e+00, -7.6999973e+01,
//            3.4663091e-04, -1.4364524e-05, 1.0000000e+00
//        )

        homography.put(
            0, 0, 7.6285898e-01, -2.9922929e-01, 2.2567123e+02,
            3.3443473e-01, 1.0143901e+00, -7.6999973e+01,
            3.4663091e-04, -1.4364524e-05, 1.0000000e+00
        )

        val kpts1 = MatOfKeyPoint()
        val kpts2 = MatOfKeyPoint()
        val desc1 = Mat()
        val desc2 = Mat()

        akaze.detectAndCompute(img1, Mat(), kpts1, desc1)
        akaze.detectAndCompute(img2, Mat(), kpts2, desc2)

        val matches = mutableListOf<MatOfDMatch>()
        matcher.knnMatch(desc1, desc2, matches, 2)

        val ratioThreshold = 0.8f
        val listOfMatched1 = mutableListOf<KeyPoint>()
        val listOfMatched2 = mutableListOf<KeyPoint>()
        val listOfKeypoints1 = kpts1.toList()
        val listOfKeypoints2 = kpts1.toList()
        for (i in 0 until matches.size) {
            val m = matches[i].toArray()
            val dist1 = m[0].distance
            val dist2 = m[1].distance
            val q = m[0].queryIdx
            val t = m[0].trainIdx
            if (dist1 < ratioThreshold * dist2 &&
                q < listOfKeypoints1.size &&
                t < listOfKeypoints2.size
            ) {
                listOfMatched1.add(listOfKeypoints1[q])
                listOfMatched2.add(listOfKeypoints2[t])
            }
        }

        val inlierThreshold = 2.5 // Distance threshold to identify inliers with homography check

        val listOfInliers1: MutableList<KeyPoint> = ArrayList()
        val listOfInliers2: MutableList<KeyPoint> = ArrayList()
        val listOfGoodMatches: MutableList<DMatch> = ArrayList()
        for (i in listOfMatched1.indices) {
            val col = Mat(3, 1, CvType.CV_64F)
            val colData = DoubleArray((col.total() * col.channels()).toInt())
            colData[0] = listOfMatched1[i].pt.x
            colData[1] = listOfMatched1[i].pt.y
            colData[2] = 1.0
            col.put(0, 0, *colData)
            val colRes = Mat()
            Core.gemm(homography, col, 1.0, Mat(), 0.0, colRes)
            colRes[0, 0, colData]
            Core.multiply(colRes, Scalar(1.0 / colData[2]), col)
            col[0, 0, colData]
            val dist = sqrt(
                (colData[0] - listOfMatched2[i].pt.x).pow(2.0) +
                        (colData[1] - listOfMatched2[i].pt.y).pow(2.0)
            )
            if (dist < inlierThreshold) {
                listOfGoodMatches.add(DMatch(listOfInliers1.size, listOfInliers2.size, 0F))
                listOfInliers1.add(listOfMatched1[i])
                listOfInliers2.add(listOfMatched2[i])
            }
        }

        val res = Mat()
        val inliers1 = MatOfKeyPoint(*listOfInliers1.toTypedArray())
        val inliers2 = MatOfKeyPoint(*listOfInliers2.toTypedArray())
        val goodMatches = MatOfDMatch(*listOfGoodMatches.toTypedArray())

        return listOfInliers1.size / listOfMatched1.size.toDouble()
    }
}