package me.vlasoff.kotlinopencv

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.vlasoff.kotlinopencv.databinding.ActivityMainBinding
import me.vlasoff.kotlinopencv.face_recognition.FaceDetect
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
        const val TAG = "OPENCV_TAG"
        const val EXTERNAL_STORAGE_PERMISSION_CODE = 23
        const val CALLBACKS = "dbg_callbacks"
        const val REQUEST_CODE = 99
    }

    private val callback = object : BaseLoaderCallback(this) {
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
        setContentView(binding.root)
        Log.i(CALLBACKS, "onCreate: ")

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


//        detectFaces(BitmapFactory.decodeResource(this.resources, R.drawable.selfie2))

//        PassportRecognition.detect(this)

//        // add opencv functionality
        OpenCVLoader.initDebug()

        val imagePath = intent.getStringExtra("image_type")
        if (imagePath != null) {
            val bitmap = getBitmap(imagePath)
            bitmap?.let { recognize(it) }
        } else
            Toast.makeText(this, "Image was not found", Toast.LENGTH_SHORT).show()


        binding.ivPhoto.setOnClickListener {
            Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AppScanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveCheckboxState(type: PassportImageType, state: Boolean) =
        PassportUtils(this).save(type.page, state)

    override fun onRestart() {
        super.onRestart()
        Log.i(CALLBACKS, "onRestart: ")
    }

//    private fun recognize() = lifecycleScope.launch(Dispatchers.IO) {
//        val drawable = binding.ivPhoto.drawable
//        val faces =
//            async(Dispatchers.IO) { FaceDetection.detectFaces(image) }.await()
//        Log.d("faces", "F: $faces")
//    }

    private fun getBitmap(path: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            binding.ivPhoto.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun recognize(image: Bitmap) = lifecycleScope.launch(Dispatchers.IO) {
        FaceDetection.loadModel(this@MainActivity)
        val faces = FaceDetect.detectFaces(image).size
        val keypoints = PassportRecognition.detect(this@MainActivity, image, faces)
//        Toast.makeText(this@MainActivity, "$faces", Toast.LENGTH_SHORT).show()
        Log.i("keypoints", "Points: $keypoints, Faces: $faces")

        when (faces) {
            0 -> {
                if (keypoints >= 25) {
                    runOnUiThread { binding.checkBoxRegistrationPage.isChecked = true }
                }

            }
            1 -> {
                if (keypoints >= 25) {
                    runOnUiThread { binding.checkBoxMainPage.isChecked = true }
                }
            }
            else -> {
            }
        }
    }

}