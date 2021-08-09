package me.vlasoff.kotlinopencv

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

fun addGrayscale(image: Mat): Mat {
    val gray = Mat()
    Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)

    val target = Mat()
    Imgproc.adaptiveThreshold(
        gray,
        target,
        255.0,
        Imgproc.ADAPTIVE_THRESH_MEAN_C,
        Imgproc.THRESH_BINARY,
        15,
        40.0
    )
    return target
}