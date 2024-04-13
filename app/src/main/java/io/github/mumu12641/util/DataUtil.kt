package io.github.mumu12641.util

object DataUtil {

    private val TAG = "DataUtil"

    private fun linearInterpolation(ecgData: List<Int>, factor: Int = 47): List<Int> {
        val upSampledList = mutableListOf<Int>()
        for (i in 0 until ecgData.size - 1) {
            val startValue = ecgData[i]
            val endValue = ecgData[i + 1]
            val delta = endValue - startValue
            for (j in 0 until factor) {
                val value = startValue + (delta * j.toFloat() / factor).toInt()
                upSampledList.add(value)
            }
        }
        upSampledList.add(ecgData.last())
        return upSampledList
    }

    private fun linearMap(x: Int, inMin: Int, inMax: Int): Int {
        return ((x - inMin) * (32767 + 32768) / (inMax - inMin) - 32768)
    }

    fun quantitativeSampling(ecgData: List<Int>): List<Int> {
        val upSampledList = linearInterpolation(ecgData)
        val filter = upSampledList.filter { it != 0 }
        val meanValue = filter.average()
        val norm = filter.map { it - meanValue }
        val minValue = norm.minOrNull()?.toInt() ?: 0
        val maxValue = norm.maxOrNull()?.toInt() ?: 0
        return norm.map { linearMap(it.toInt(), maxValue, minValue) }
    }

    fun intListToByteArray(list: List<Int>): ByteArray {
        val byteArray = ByteArray(list.size * 2)
        list.forEachIndexed { index, value ->
            val shortValue = value.toShort()
            byteArray[index * 2] = (shortValue.toInt() and 0xFF).toByte()
            byteArray[index * 2 + 1] = ((shortValue.toInt() shr 8) and 0xFF).toByte()
        }
        return byteArray
    }
}

