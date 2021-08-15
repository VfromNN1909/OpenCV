package me.vlasoff.kotlinopencv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.zynksoftware.documentscanner.ScanActivity
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.ScannerResults

class AppScanActivity : ScanActivity() {

    companion object {
        const val TRANSFORMED_IMAGE_PATH = "t_image_path"
        const val CROPPED_IMAGE_PATH = "c_image_path"
        const val ORIGINAL_IMAGE_PATH = "o_image_path"
        const val IMAGE_TYPE = "image_type"
        const val IS_RECOGNIZED = "is_recognized"
    }

    override fun onClose() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_scan_acticity)
        addFragmentContentLayout()
    }

    override fun onError(error: DocumentScannerErrorModel) {
        Log.e("SCANNER_ERROR", error.errorMessage?.error.toString())
    }

    override fun onSuccess(scannerResults: ScannerResults) {
        val transformedImagePath = scannerResults.transformedImageFile?.path ?: ""
        val croppedImagePath = scannerResults.croppedImageFile?.path ?: ""
        val intent = Intent(this, MainActivity::class.java).apply {
            if(transformedImagePath == "")
                putExtra(IMAGE_TYPE, croppedImagePath)
            else
                putExtra(IMAGE_TYPE, transformedImagePath)
        }
        startActivity(intent)
    }
}