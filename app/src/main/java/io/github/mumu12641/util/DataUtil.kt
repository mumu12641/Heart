package io.github.mumu12641.util

object DataUtil {
    fun quantitativeSampling(ecgData: List<Int>): List<Int> {
        val minValue = ecgData.minOrNull() ?: 0
        val maxValue = ecgData.maxOrNull() ?: 0
        val quantizedArray = ecgData.map { value ->
            ((value - minValue).toDouble() / (maxValue - minValue) * 255).toInt().coerceIn(0, 255)
        }
        return quantizedArray
    }

    fun intListToByteArray(list: List<Int>): ByteArray = list.map { it.toByte() }.toByteArray()

}