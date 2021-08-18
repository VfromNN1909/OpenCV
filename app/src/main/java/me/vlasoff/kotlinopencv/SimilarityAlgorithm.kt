package me.vlasoff.kotlinopencv

import android.content.Context
import android.graphics.*
import android.graphics.Canvas
import android.util.Log
import org.opencv.android.Utils

import org.opencv.core.*

import org.opencv.imgproc.Imgproc
import java.lang.Math.abs
import kotlin.math.roundToInt


class SimilarityAlgorithm(private val context: Context) {

    fun detectEdge(bmpimg1: Bitmap?, faces: Int): Double {
        var _bmpimg1 = bmpimg1
        var bmpimg2 = when (faces) {
            0 -> {
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.registration_page_template
                )
            }
            1 -> {
                BitmapFactory.decodeResource(context.resources, R.drawable.main_page_template)
            }
            else -> {
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.registration_page_template
                )
            }
        }
        _bmpimg1 = Bitmap.createScaledBitmap(_bmpimg1!!, 300, 300, true)
        bmpimg2 = Bitmap.createScaledBitmap(bmpimg2!!, 300, 300, true)
        val img1 = Mat()
        Utils.bitmapToMat(_bmpimg1, img1)
        val img2 = Mat()
        Utils.bitmapToMat(bmpimg2, img2)
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_RGBA2GRAY)
        img1.convertTo(img1, CvType.CV_32F)
        img2.convertTo(img2, CvType.CV_32F)
        //Log.d("ImageComparator", "img1:"+img1.rows()+"x"+img1.cols()+" img2:"+img2.rows()+"x"+img2.cols());
        val hist1 = Mat()
        val hist2 = Mat()
        val histSize = MatOfInt(180)
        val channels = MatOfInt(0)
        val bgr_planes1 = ArrayList<Mat>()
        val bgr_planes2 = ArrayList<Mat>()
        Core.split(img1, bgr_planes1)
        Core.split(img2, bgr_planes2)
        val histRanges = MatOfFloat(0f, 180f)
        val accumulate = false
        Imgproc.calcHist(bgr_planes1, channels, Mat(), hist1, histSize, histRanges, accumulate)
        Core.normalize(hist1, hist1, 0.0, hist1.rows().toDouble(), Core.NORM_MINMAX, -1, Mat())
        Imgproc.calcHist(bgr_planes2, channels, Mat(), hist2, histSize, histRanges, accumulate)
        Core.normalize(hist2, hist2, 0.0, hist2.rows().toDouble(), Core.NORM_MINMAX, -1, Mat())
        img1.convertTo(img1, CvType.CV_32F)
        img2.convertTo(img2, CvType.CV_32F)
        hist1.convertTo(hist1, CvType.CV_32F)
        hist2.convertTo(hist2, CvType.CV_32F)
        val compare = Imgproc.compareHist(hist1, hist2, 0)
        val size = hist1.height()
        Log.d("the compare is ", compare.toString())
        val simi = abs(compare * 10000).roundToInt().toDouble()
        Log.d("HIST_SIM", "" + simi)
        return simi

//        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
//
//        Mat img1 = new Mat();
//        Utils.bitmapToMat(bmp, img1);
//        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGBA2GRAY);
//        img1.convertTo(img1, CvType.CV_32F);


//        Mat rgba = new Mat();
//        Utils.bitmapToMat(b2, rgba);
//
//        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
//
//        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
//        Imgproc.Canny(edges, edges, 50, 50);


//        return edges;
//        Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(edges, resultBitmap);
//        return resultBitmap;

//        Bitmap bmp = null;
//        Mat tmp = new Mat (bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
//        try {
//            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);        Imgproc.cvtColor(footGrayMat, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
//            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(tmp, bmp);
//        }
//        catch (Exception e){
//            Log.d("Exception",e.getMessage());}
//        return bmp;
    }

    fun distance(bmpimg1: Bitmap, faces: Int): Double {
        var _bmpimg1 = bmpimg1
        var bmpimg2: Bitmap = when (faces) {
            0 -> {
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.registration_page_template
                )
            }
            1 -> {
                BitmapFactory.decodeResource(context.resources, R.drawable.main_page_template)
            }
            else -> {
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.registration_page_template
                )
            }
        }
        _bmpimg1 = Bitmap.createScaledBitmap(_bmpimg1, 50, 50, true)
        bmpimg2 = Bitmap.createScaledBitmap(bmpimg2, 50, 50, true)
        val resultBitmap1 = toGrayscale(_bmpimg1)
        val resultBitmap2 = toGrayscale(bmpimg2)
        val average1 = getAveragePixel(resultBitmap1)
        val average2 = getAveragePixel(resultBitmap2)
        Log.d("average1 is ", average1.toString())
        Log.d("average2 is ", average2.toString())
        val pixel1 = IntArray(2500)
        val pixel2 = IntArray(2500)
        //        for(int i = 0; i<50; i++){
//            for(int j = 0; j<50; j++){
//
//            }
//        }


//        Bitmap resultBitmap1 = Bitmap.createBitmap(edge1.cols(), edge1.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(edge1, resultBitmap1);
//
//        Bitmap resultBitmap2 = Bitmap.createBitmap(edge2.cols(), edge2.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(edge2, resultBitmap2);
//        Log.d("the bitmap2 heoght", String.valueOf(resultBitmap2.getHeight()));
//        Log.d("the bitmap1 heoght", String.valueOf(resultBitmap1.getHeight()));
        val diff = 0.0
        val count = 0
        Log.d("the height is ", _bmpimg1.height.toString())
        for (i in 0 until resultBitmap1.width) for (j in 0 until resultBitmap1.height) {
            val p1: Int = abs(resultBitmap1.getPixel(i, j))
            if (average1 > p1) {
                pixel1[i * j] = 0
            } else {
                pixel1[i * j] = 1
            }
            //                long num1 = abs(resultBitmap1.getPixel(i, j));
//                long num2 = abs(resultBitmap2.getPixel(i, j));
//                Log.d("origin pixel", String.valueOf(resultBitmap2.getPixel(i, j)));
//                if(num1==num2){
//                    count++;
//                    Log.d("count is ", String.valueOf(count));
//                }
//
//                // Log.d("the pixel is ", String.valueOf(resultBitmap1.getPixel(i, j)));
//                diff += abs(num1 - num2)/max(num1, num2);

//                Log.d("diff is +++", String.valueOf(diff));
        }
        for (i in 0 until resultBitmap2.width) for (j in 0 until resultBitmap2.height) {
            val p2: Int = abs(resultBitmap2.getPixel(i, j))
            Log.d("p2 is ", "+++++$p2")
            if (average2 > p2) {
                pixel2[i * j] = 0
                Log.d("p2 distance<<<<<<: ", "p2 < average")
            } else {
                pixel2[i * j] = 1
                Log.d("p2 distance>>>>>>: ", "p2 > average")
            }
        }
        var count1 = 0
        for (i in 0..2499) {
            if (pixel1[i] == pixel2[i]) {
                count1++
            }
        }


//
//        double errorL2 = norm( edge1);
//         double errorL3 = norm(edge2);
//        System.out.println("The erro is " + errorL2);

//        int totalDis=0;
//        for(int i = 0; i < 50; i++){
//            for(int j=0; j<50; j++){
//                 totalDis += abs(bmp1.getPixel(i, j) - bmp2.getPixel(i, j));
//                System.out.println("the pixels are :" + bmp1.getPixel(i, j));
//            }
//        }
//        Log.d("the distance is ", String.valueOf(totalDis));
        return ((count1 * 100).toFloat().roundToInt() / 100).toDouble()
    }

    //    public double calSimilarity(int totalDis){
    //        int length = 50*50;
    //        double similarity = (length - totalDis) / (double) length;
    //
    //        // 使用指数曲线调整相似度结果
    //        similarity = java.lang.Math.pow(similarity, 2);
    //        return similarity;
    //    }
    fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
        val height: Int = bmpOriginal.height
        val width: Int = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0.0f, 0.0f, paint)
        return bmpGrayscale
    }

    fun getAveragePixel(bmpimg1: Bitmap): Int {
        val width = bmpimg1.width
        val height = bmpimg1.height
        var total = 0
        for (i in 0 until width) {
            for (j in 0 until height) {
                total += abs(bmpimg1.getPixel(i, j))
            }
        }
        return total / (width * height)
    }
}