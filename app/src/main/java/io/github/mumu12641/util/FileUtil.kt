package io.github.mumu12641.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import io.github.mumu12641.App.Companion.context
import io.github.mumu12641.R
import io.github.mumu12641.data.local.model.ECGModel
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale


object FileUtil {
    private fun getPictureDirectory() =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    private fun getMp3Directory() = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
    private val TAG = "FileUtil"

    private fun createPictureFile(name: String) = File(
        getPictureDirectory(), name
    )

    private fun createFile(name: String) = File(
        getMp3Directory(), name
    )

    suspend fun saveECG(bitmap: Bitmap, ecgData: List<Int>): ECGModel {
        val time = SimpleDateFormat(
            "MM-dd-HH:mm:ss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val bitmapName = time + "_jpg"
        val txtName = time + "_txt"
        val wavName = time + "_wav"
        val pcmName = time + "_pcm"
        val txtPath = writeECGDataToTxt(ecgData, txtName)
        val jpgPath = writeBitmapToFile(bitmap, bitmapName)
        val pcmPath =
            writeECGDataToPcm(
                DataUtil.quantitativeSampling(ecgData),
                pcmName
            )
        val wavPath = pcmToWav(pcmPath, wavName)
        delay(1000)
        Timber.tag(TAG).d("Save ECG to database")
        return ECGModel(
            0, time, pcmPath, wavPath, jpgPath, txtPath, time, null
        )
    }

    private fun writeECGDataToTxt(ecgData: List<Int>, name: String): String {
        val file = createFile("$name.txt")
        file.bufferedWriter().use { writer ->
            for (ecg in ecgData) {
                writer.write("$ecg\n")
            }
        }
        Timber.tag(TAG).d("Save ECGData to %s", file.absolutePath)
        return file.absolutePath
    }

    private fun writeBitmapToFile(bitmap: Bitmap, name: String): String {
        val file = createPictureFile("$name.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, 100, outputStream
            )
        }
        Timber.tag(TAG).d("Save ECGData to %s", file.absolutePath)
        return file.absolutePath
    }

    private fun writeECGDataToPcm(ecgData: List<Int>, name: String): String {
        val file = createFile("$name.pcm")
        FileOutputStream(file).apply {
            write(DataUtil.intListToByteArray(ecgData))
            close()
        }
        Timber.tag(TAG).d("Save PCM to %s", file.absolutePath)
        return file.absolutePath
    }


    private fun pcmToWav(pcmPath: String, name: String): String {
        val mp3File = createFile("$name.wav")
        val ret = pcmToWavJNI(pcmPath, mp3File.absolutePath)

        if (ret == -1) {
            Timber.tag(TAG).d("return err")
        }
        return mp3File.absolutePath
    }

    fun openFile(path: String) {
        path.runCatching {
            createIntentForFile(this)?.run {
                context.startActivity(this)

            }
                ?: throw Exception()
        }.onFailure {
            Toast.makeText(context, context.getString(R.string.open_file_error), Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun createIntentForFile(path: String?): Intent? {
        if (path == null) return null
        val uri = FileProvider.getUriForFile(context, context.getFileProvider(), File(path))
        return Intent().apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            action = (Intent.ACTION_VIEW)
            data = uri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun Context.getFileProvider() = "$packageName.provider"

    private fun removeFile(path: String) = path.runCatching {
        File(path).delete()
    }

    fun removeECGFile(ecgModel: ECGModel) {
        ecgModel.apply {
            removeFile(this.jpgPath)
            removeFile(this.wavPath)
            removeFile(this.pcmPath)
            removeFile(this.txtPath)
        }
    }

    private external fun pcmToWavJNI(
        pcmPath: String, wavPath: String
    ): Int
}