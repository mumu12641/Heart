package io.github.mumu12641.util

import android.graphics.Bitmap
import android.os.Environment
import io.github.mumu12641.App
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    private fun getDirectory() = App.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val TAG = "FileUtil"

    private fun createFile(name: String) = File(
        getDirectory(), name
    )

    fun writeBitmapToFile(bitmap: Bitmap, time: String): String {
        val file = createFile("$time.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, 100, outputStream
            )
        }
        Timber.tag(TAG).d("Save ECGData to %s", file.absolutePath)
        return file.absolutePath
    }
}