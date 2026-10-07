package com.example.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

object CameraXManager {
    private const val TAG = "CameraXManager"
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

    fun createOutputPhotoFile(context: Context): File {
        val outputDirectory = File(context.filesDir, "AjustaPhotos").apply { mkdirs() }
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        return File(outputDirectory, "AJUSTA_${timeStamp}.jpg")
    }

    fun takePhoto(
        context: Context,
        imageCapture: ImageCapture,
        executor: Executor = ContextCompat.getMainExecutor(context),
        onImageCaptured: (Uri, Bitmap) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val photoFile = createOutputPhotoFile(context)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    try {
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        onImageCaptured(savedUri, bitmap)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error decoding saved photo", e)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    onError(exception)
                }
            }
        )
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
