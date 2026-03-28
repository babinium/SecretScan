package com.secretscan.app

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrHelper {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    fun recognizeText(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val result = visionText.text.trim()
                    callback(result.ifEmpty { null })
                }
                .addOnFailureListener { _ ->
                    callback(null)
                }
        } catch (e: Exception) {
            callback(null)
        }
    }
}
