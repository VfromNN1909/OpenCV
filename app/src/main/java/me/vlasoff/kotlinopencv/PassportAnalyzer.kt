package me.vlasoff.kotlinopencv

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions


class PassportAnalyzer {

    fun runTextRecognition(context: Context): String {
        val imageBitmap = AppCompatResources.getDrawable(context, R.drawable.main_page_test_1)?.toBitmap() as Bitmap
        var result: String = ""
        val image = InputImage.fromBitmap(imageBitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener {
                result = textRecognitionResult(it)
            }
            .addOnFailureListener {
                result = it.message as String
            }
        return result
    }

    private fun textRecognitionResult(texts: Text) = texts.text

}