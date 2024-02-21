package io.github.mumu12641.util

import android.os.Environment
import java.io.File

object FileUtil {
    internal fun getExternalDownloadDirectory() = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Heart"
    ).also { it.mkdir() }
}