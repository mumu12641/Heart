package io.github.mumu12641.util

import android.graphics.Bitmap
import android.os.Environment
import io.github.mumu12641.App
import io.github.mumu12641.data.local.model.ECGModel
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object FileUtil {
    private fun getPictureDirectory() =
        App.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    private fun getMp3Directory() = App.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
    private val TAG = "FileUtil"

    private fun createPictureFile(name: String) = File(
        getPictureDirectory(), name
    )

    private fun createMp3File(name: String) = File(
        getMp3Directory(), name
    )

    suspend fun saveECG(bitmap: Bitmap, ecgData: List<Int>): ECGModel {
        val time = SimpleDateFormat(
            "MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val bitmapName = time + "_jpg"
        val mp3Name = time + "_mp3"
        val pcmName = time + "_pcm"
        val jpgPath = writeBitmapToFile(bitmap, bitmapName)
        val pcmPath =
            writeECGDataToPcm(
                DataUtil.quantitativeSampling(ecgData),
                pcmName
            )
        val mp3Path = pcmToMp3(pcmPath, mp3Name)
        delay(1000)
        Timber.tag(TAG).d("Save ECG to database")
        return ECGModel(
            0, time, pcmPath, mp3Path, jpgPath, time, null
        )
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
        val file = createMp3File("$name.pcm")
        FileOutputStream(file).apply {
            write(DataUtil.intListToByteArray(ecgData))
            close()
        }
        Timber.tag(TAG).d("Save PCM to %s", file.absolutePath)
        return file.absolutePath
    }

    private fun pcmToMp3(pcmPath: String, name: String): String {
        val mp3File = createMp3File("$name.mp3")
        val sampleRate = 44100
        val channel = 2
        val bitRate = 64000
        val ret = pcmToMp3JNI(pcmPath, mp3File.absolutePath, sampleRate, channel, bitRate)
        if (ret == -1) {
            Timber.tag(TAG).d("return err")
        }
        return mp3File.absolutePath
    }

    private external fun pcmToMp3JNI(
        pcmPath: String, mp3Path: String,
        sampleRate: Int, channel: Int, bitRate: Int
    ): Int
}