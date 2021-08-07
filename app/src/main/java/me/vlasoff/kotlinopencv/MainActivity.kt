package me.vlasoff.kotlinopencv

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.vlasoff.kotlinopencv.databinding.ActivityMainBinding
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
        const val TAG = "OPENCV_TAG"
        const val EXTERNAL_STORAGE_PERMISSION_CODE = 23
    }

    val callback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")
                }
                else -> super.onManagerConnected(status)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

        if (!OpenCVLoader.initDebug()) {
            Log.d(
                TAG,
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            );
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, callback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            callback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }

//        // add opencv functionality
        OpenCVLoader.initDebug()
        Toast.makeText(this, "OpenCV loaded successfully!", Toast.LENGTH_SHORT).show()


        val detector = PassportDataRecognition(this)
        val data = detector.getData()
        binding.tvPassportText.text = data

//        PassportMatchingSecondTry.detect(this)


//        val data = PassportDataRecognition(this).getData()
//        binding.tvPassportText.text = data
    }

//    private fun checkForPermissions() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                Log.i(TAG,"Unexpected flow");
//            } else {
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    EXTERNAL_STORAGE_PERMISSION_CODE)
//            }
//        } else {
//            val detector = PassportDataRecognition(this)
//            val data = detector.getData()
//            binding.tvPassportText.text = data
//        }
//    }

    private fun requestPermissions(): Boolean {
        var request = true
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, 102)
            true
        } else {
            false
        }
    }
}