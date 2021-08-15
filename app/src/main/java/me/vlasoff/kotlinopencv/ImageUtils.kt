package me.vlasoff.kotlinopencv

import org.opencv.core.*
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



fun detectMRZ(img: Mat): Mat? {
    //Mat img = Imgcodecs.imread(photoPath);
    var img = img
    var roi: Mat? = Mat()
    var kernHt = img.height() / 23
    if (kernHt % 2 != 0) kernHt++
    val rectKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(13.0, 5.0))
    val sqKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(21.0, kernHt.toDouble()))
    if (img.width() > 800) // load the image, resize it, and convert it to grayscale
        img = resize(img, 800, 600, Imgproc.INTER_AREA)

    //displayImage(toBufferedImage(img), "orig image resized");
    val gray = Mat()
    Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY)

    //displayImage(toBufferedImage(gray), "image in grayscale");

    //smooth the image using a 3x3 Gaussian, then apply the blackhat
    //morphological operator to find dark regions on a light background
    Imgproc.GaussianBlur(gray, gray, Size(3.0, 3.0), 0.0)

    //displayImage(toBufferedImage(gray), "gaussian blur");
    val blackhat = Mat()
    Imgproc.morphologyEx(gray, blackhat, Imgproc.MORPH_BLACKHAT, rectKernel)

    //displayImage(toBufferedImage(blackhat), "blackhat");

    //compute the Scharr gradient of the blackhat image and scale the
    //result into the range [0, 255]
    val gradX = Mat()
    //gradX = cv2.Sobel(blackhat, ddepth=cv2.CV_32F, dx=1, dy=0, ksize=-1)
    Imgproc.Sobel(blackhat, gradX, CvType.CV_32F, 1, 0, -1, 1.0, 0.0)
    //gradX = Matrix absolute(gradX)

    //displayImage(toBufferedImage(gradX), "sobel");

    //(minVal, maxVal) = (np.min(gradX), np.max(gradX))
    val minMaxVal = Core.minMaxLoc(gradX)

    //gradX = (255 * ((gradX - minVal) / (maxVal - minVal))).astype("uint8")
    gradX.convertTo(
        gradX,
        CvType.CV_8U,
        255.0 / (minMaxVal.maxVal - minMaxVal.minVal),
        -255.0 / minMaxVal.minVal
    )

    //displayImage(toBufferedImage(gradX), "sobel converted to CV_8U");

    //apply a closing operation using the rectangular kernel to close
    //gaps in between letters -- then apply Otsu's thresholding method
    Imgproc.morphologyEx(gradX, gradX, Imgproc.MORPH_CLOSE, rectKernel)

    //displayImage(toBufferedImage(gradX), "closing operation morphology");
    val thresh = Mat()
    Imgproc.threshold(gradX, thresh, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)

    //displayImage(toBufferedImage(thresh), "applied threshold");

    // perform another closing operation, this time using the square
    // kernel to close gaps between lines of the MRZ, then perform a
    // series of erosions to break apart connected components
    Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_CLOSE, sqKernel)

    //displayImage(toBufferedImage(thresh), "another closing operation morphology");
    Imgproc.erode(thresh, thresh, Mat(), Point(-1.0, -1.0), 4)

    //displayImage(toBufferedImage(thresh), "erode");
    // during thresholding, it's possible that border pixels were
    // included in the thresholding, so let's set 5% of the left and
    // right borders to zero
    val pRows = (img.rows() * 0.05).toInt()
    val pCols = (img.cols() * 0.05).toInt()

    //thresh[:, 0:pCols] = 0;
    //thresh.put(thresh.rows(), pCols, 0);
    //thresh[:, image.cols() - pCols] = 0;
    for (i in 0..thresh.rows()) for (j in 0..pCols) thresh.put(i, j, 0.0)

    //thresh[:, image.cols() - pCols] = 0;
    for (i in 0..thresh.rows()) for (j in img.cols() - pCols..img.cols()) thresh.put(i, j, 0.0)

    //displayImage(toBufferedImage(thresh), "");

    // find contours in the thresholded image and sort them by their
    // size
    val cnts: List<MatOfPoint> = ArrayList()
    Imgproc.findContours(
        thresh.clone(), cnts, Mat(), Imgproc.RETR_EXTERNAL,
        Imgproc.CHAIN_APPROX_SIMPLE
    )

    //cnts.sort(Imgproc.contourArea(contour));//, Imgproc.contourArea(cnts, true))

    // loop over the contours
    for (c in cnts) {
        // compute the bounding box of the contour and use the contour to
        // compute the aspect ratio and coverage ratio of the bounding box
        // width to the width of the image
        val bRect: Rect = Imgproc.boundingRect(c)
        var x: Int = bRect.x
        var y: Int = bRect.y
        var w: Int = bRect.width
        var h: Int = bRect.height
        val grWidth = gray.width()
        val ar = w.toFloat() / h.toFloat()
        val crWidth = w.toFloat() / grWidth.toFloat()

        // check to see if the aspect ratio and coverage width are within
        // acceptable criteria
        if (ar > 4 && crWidth > 0.75) {
            // pad the bounding box since we applied erosions and now need
            // to re-grow it
            val pX = ((x + w) * 0.03).toInt() //previously 0.03 expanded to allow for warp
            val pY = ((y + h) * 0.03).toInt()
            x -= pX
            y -= pY
            w += pX * 2
            h += pY * 2

            // extract the ROI from the image and draw a bounding box
            // surrounding the MRZ
            //roi = new Mat(img, bRect);
            roi = Mat(img, Rect(x, y, w, h))
            Imgproc.rectangle(
                img,
                Point(x.toDouble(), y.toDouble()),
                Point((x + w).toDouble(), (y + h).toDouble()),
                Scalar(0.0, 255.0, 0.0),
                2
            )

            //displayImage(toBufferedImage(img), "found mrz?");
            break
        }
    }
    return roi
}

private fun resize(img: Mat, width: Int, height: Int, inter: Int): Mat {
    var inter = inter
    inter = Imgproc.INTER_AREA
    val imgDim = img.size()
    var dim: Size? = null
    var r = 1.0
    if (width <= 0 && height <= 0) return img
    if (height == 0) {
        r = width / imgDim.width
        dim = Size(width.toDouble(), img.height() * r)
    } else if (width == 0) {
        r = height / imgDim.height
        dim = Size(img.width() * r, height.toDouble())
    } else if (width > 0 && height > 0) {
        dim = Size(width.toDouble(), height.toDouble())
    }


    //resize the image
    val resized = Mat()
    Imgproc.resize(img, resized, dim, 0.0, 0.0, inter)
    return resized
}