package io.github.mumu12641.util

import timber.log.Timber

object DataUtil {

    private val TAG = "DataUtil"
    fun quantitativeSampling(ecgData: List<Int>): List<Int> {
        val filter = ecgData.filter { it != 0 }
        val minValue = filter.minOrNull() ?: 0
        val maxValue = filter.maxOrNull() ?: 0

        Timber.tag(TAG)
            .d("minValue%s", minValue)
        Timber.tag(TAG)
            .d("maxValue%s", maxValue)
        Timber.tag(TAG)
            .d("ecgData%s", filter)

        val quantizedArray = filter.map { value ->
            ((value - minValue).toDouble() / (maxValue - minValue) * 255).toInt().coerceIn(0, 255)
        }
        Timber.tag(TAG)
            .d("quantizedArray%s", quantizedArray)
        return quantizedArray
    }

    fun intListToByteArray(list: List<Int>): ByteArray = list.map { it.toByte() }.toByteArray()
}