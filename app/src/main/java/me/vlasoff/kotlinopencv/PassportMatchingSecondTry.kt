package me.vlasoff.kotlinopencv

import android.content.Context
import android.widget.Toast
import org.opencv.android.Utils
import org.opencv.core.CvType


object PassportMatchingSecondTry {
//    private const val TAG = "MATCHING"
//    private const val SUCCESS = "PASSPORT DETECTED"
//    private const val FAIL = "PASSPORT NOT FOUND"
//
//
//    fun detect(context: Context) {
//        Toast.makeText(context, "Detection start!", Toast.LENGTH_SHORT).show()
//        val img1 = Utils.loadResource(context, R.drawable.main_page_template, CvType.CV_8UC4)
//        val img2 = Utils.loadResource(context, R.drawable.cat, CvType.CV_8UC4)
//
//        val hessianThreshold = 400.0
//        val nOctaves = 4
//        val nOctaveLayers = 3
//        val extended = false
//        val upright = false
//        val detector: SURF =
//            SURF.create(hessianThreshold, nOctaves, nOctaveLayers, extended, upright)
//        val keypoints1 = MatOfKeyPoint()
//        val keypoints2 = MatOfKeyPoint()
//        val descriptors1 = Mat()
//        val descriptors2 = Mat()
//        detector.detectAndCompute(img1, Mat(), keypoints1, descriptors1)
//        detector.detectAndCompute(img2, Mat(), keypoints2, descriptors2)
//
//        val matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
//        val knnMatches: List<MatOfDMatch> = ArrayList()
//        matcher.knnMatch(descriptors1, descriptors2, knnMatches, 2)
//        val ratioThresh = 0.7f
//        val listOfGoodMatches: MutableList<DMatch> = ArrayList()
//        for (i in knnMatches.indices) {
//            if (knnMatches[i].rows() > 1) {
//                val matches = knnMatches[i].toArray()
//                if (matches[0].distance < ratioThresh * matches[1].distance) {
//                    listOfGoodMatches.add(matches[0])
//                }
//            }
//        }
//        if (listOfGoodMatches.size >= 10) {
//            Toast.makeText(context, SUCCESS, Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, FAIL, Toast.LENGTH_SHORT).show()
//        }
//
//
//    }

}