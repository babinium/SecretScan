package com.secretscan.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.secretscan.app.databinding.ActivityCropBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class CropActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SOURCE_URI = "source_uri"
        const val EXTRA_IS_FROM_CAMERA = "is_from_camera"
    }

    private lateinit var binding: ActivityCropBinding
    private var isFromCamera = false
    private var sourceUriString: String? = null

    private val cropLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val croppedUri = UCrop.getOutput(result.data!!)
            if (croppedUri != null) {
                // Delete original camera photo after successful crop
                if (isFromCamera) {
                    deleteOriginalPhoto()
                }
                launchPreview(croppedUri)
            } else {
                showErrorAndFinish()
            }
        } else {
            // User cancelled or error
            if (isFromCamera) {
                deleteOriginalPhoto()
            }
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sourceUriString = intent.getStringExtra(EXTRA_SOURCE_URI)
        isFromCamera = intent.getBooleanExtra(EXTRA_IS_FROM_CAMERA, false)

        if (sourceUriString == null) {
            finish()
            return
        }

        startCrop(Uri.parse(sourceUriString))
    }

    private fun startCrop(sourceUri: Uri) {
        val destFile = File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        val destUri = Uri.fromFile(destFile)

        val options = UCrop.Options().apply {
            setCompressionQuality(95)
            setToolbarColor(getColor(R.color.background))
            setStatusBarColor(getColor(android.R.color.black))
            setToolbarWidgetColor(getColor(R.color.on_surface))
            setActiveControlsWidgetColor(getColor(R.color.primary))
            setRootViewBackgroundColor(getColor(R.color.background))
            setToolbarTitle(getString(R.string.crop_title))
            setFreeStyleCropEnabled(true)
        }

        val cropIntent = UCrop.of(sourceUri, destUri)
            .withOptions(options)
            .getIntent(this)

        cropLauncher.launch(cropIntent)
    }

    private fun deleteOriginalPhoto() {
        try {
            sourceUriString?.let { uriStr ->
                val uri = Uri.parse(uriStr)
                when (uri.scheme) {
                    "content" -> contentResolver.delete(uri, null, null)
                    "file" -> uri.path?.let { path -> File(path).delete() }
                    else -> { /* no-op */ }
                }
            }
        } catch (_: Exception) {
            // Silently ignore delete failures
        }
    }

    private fun launchPreview(croppedUri: Uri) {
        val intent = Intent(this, PreviewActivity::class.java).apply {
            putExtra(PreviewActivity.EXTRA_IMAGE_URI, croppedUri.toString())
        }
        startActivity(intent)
        finish()
    }

    private fun showErrorAndFinish() {
        Toast.makeText(this, getString(R.string.crop_error), Toast.LENGTH_SHORT).show()
        finish()
    }
}
