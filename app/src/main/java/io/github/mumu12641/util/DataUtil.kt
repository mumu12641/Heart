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

    //        fun intListToByteArray(list: List<Int>): ByteArray = list.map { it.toShort().toByte() }.toByteArray()
    fun intListToByteArray(list: List<Int>): ByteArray {
        val byteArray = ByteArray(list.size * 2) // 每个元素占两个字节，所以总共占用 list.size * 2 个字节
        list.forEachIndexed { index, value ->
            val shortValue = value.toShort() // 将整数转换为 Short 类型
            byteArray[index * 2] = (shortValue.toInt() and 0xFF).toByte() // 低位字节
            byteArray[index * 2 + 1] = ((shortValue.toInt() shr 8) and 0xFF).toByte() // 高位字节
        }
        return byteArray
    }
}

