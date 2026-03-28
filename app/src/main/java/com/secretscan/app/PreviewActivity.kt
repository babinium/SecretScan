package com.secretscan.app

import android.content.ContentValues
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.secretscan.app.databinding.ActivityPreviewBinding
import java.io.File
import java.io.FileOutputStream

class PreviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "image_uri"
        private val CHARS = ('a'..'z').toList()
    }

    private lateinit var binding: ActivityPreviewBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriStr = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (uriStr == null) {
            finish()
            return
        }
        imageUri = Uri.parse(uriStr)

        // Show scanning state
        binding.scrollView.visibility = View.GONE
        binding.buttonBar.visibility = View.GONE
        binding.previewTitle.text = getString(R.string.scanning)

        // Run OCR
        OcrHelper.recognizeText(this, imageUri!!) { text ->
            runOnUiThread {
                onOcrComplete(text)
            }
        }
    }

    private fun onOcrComplete(text: String?) {
        // Clean up cropped image
        cleanupTempImage()

        if (text.isNullOrBlank()) {
            binding.previewTitle.text = getString(R.string.no_text_found)
            binding.buttonBar.visibility = View.VISIBLE
            binding.btnCopy.visibility = View.GONE
            binding.btnSave.visibility = View.GONE
        } else {
            binding.previewTitle.text = "Texto Detectado"
            binding.editTextResult.setText(text)
            binding.scrollView.visibility = View.VISIBLE
            binding.buttonBar.visibility = View.VISIBLE
        }

        binding.btnCopy.setOnClickListener {
            val currentText = binding.editTextResult.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("scanned_text", currentText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
        }

        binding.btnSave.setOnClickListener {
            val currentText = binding.editTextResult.text.toString()
            saveToDownloads(currentText)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun generateFileName(): String {
        return (1..5).map { CHARS.random() }.joinToString("") + ".txt"
    }

    private fun saveToDownloads(text: String) {
        val fileName = generateFileName()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ use MediaStore
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(text.toByteArray())
                    }
                    Toast.makeText(
                        this,
                        getString(R.string.saved_format, fileName),
                        Toast.LENGTH_LONG
                    ).show()
                    println("$fileName guardado en descargas")
                } else {
                    Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Older Android versions
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { out ->
                    out.write(text.toByteArray())
                }
                Toast.makeText(
                    this,
                    getString(R.string.saved_format, fileName),
                    Toast.LENGTH_LONG
                ).show()
                println("$fileName guardado en descargas")
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun cleanupTempImage() {
        try {
            imageUri?.let { uri ->
                if (uri.scheme == "file") {
                    uri.path?.let { File(it).delete() }
                }
            }
        } catch (_: Exception) {
            // Silently ignore
        }
    }
}
