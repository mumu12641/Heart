package io.github.mumu12641.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.github.mumu12641.BLE.ECG_DATA_SIZE
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt


//val TAG = "ECGChart"

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewEcgChart() {
//    2528
    EcgChart(data = List(ECG_DATA_SIZE) { 2530 }, false) {}
//    EcgChart(data = List(100) { (Random.nextInt(2450, 2550).toFloat()) }, false){}
}

@Composable
fun EcgChart(data: List<Int>, saving: Boolean, saveBitMap: (Bitmap) -> Unit) {
    val lines = mutableListOf<LineDataSet>()
    val lineColor = MaterialTheme.colorScheme.onSecondaryContainer.toArgb()
    val primaryLineColor = MaterialTheme.colorScheme.primary.toArgb()
    Surface(modifier = Modifier
        .clip(RoundedCornerShape(32.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .clickable { }) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    val x = (0..ECG_DATA_SIZE).map { i -> 1f * i }
                    val y = List(ECG_DATA_SIZE) { 0f }
                    val primaryLine =
                        LineDataSet(x.zip(y).map { Entry(it.first, it.second) }, "primary")
                    lines.add(primaryLine)
                    this.ConfigureGrid(x, lines, lineColor)

                }
            },
            update = { it ->
                val x = (0..ECG_DATA_SIZE).map { i -> 1f * i }
                val primaryLine =
                    LineDataSet(x.zip(data).map { Entry(it.first, it.second.toFloat()) }, "primary")
                primaryLine.apply {
                    setDrawCircles(false)
                    lineWidth = 2f
                    color = primaryLineColor
                    setDrawValues(false)
                    isHighlightEnabled = false
                }
                it.apply {
                    this.data.dataSets[0] = primaryLine
                    this.data.notifyDataChanged()
                    notifyDataSetChanged()
                    invalidate()
                }
                if (saving) {
                    saveBitMap(it.chartBitmap)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clip(RoundedCornerShape(32.dp))
        )
    }
}


private fun LineChart.ConfigureGrid(
    x: List<Float>,
    lines: MutableList<LineDataSet>,
    lineColor: Int,
) {
    val majorX = 10f
    val majorY = 50f
    val minorX = majorX / 10
    val minorY = majorY / 5

    val xMin = floor(x.min() / majorX) * majorX
    val xMax = ceil(x.max() / majorX) * majorX

//    val yMin = floor(2420 / majorY) * majorY
//    val yMax = ceil(2580 / majorY) * majorY

//    val yMin = floor(2420 / majorY) * majorY
//    val yMax = ceil(2640 / majorY) * majorY
    val yMin = 2470f
    val yMax = 2610f

    xGridLines(lines, minorX, yMin, yMax, xMin, xMax, false, lineColor)
    yGridLines(lines, minorY, yMin, yMax, xMin, xMax, false, lineColor)

    xGridLines(lines, majorX, yMin, yMax, xMin, xMax, true, lineColor)
    yGridLines(lines, majorY, yMin, yMax, xMin, xMax, true, lineColor)


    this.axisRight.isEnabled = false
    val yAx = this.axisLeft
//    yAx.setDrawLabels(false)
//    yAx.setDrawGridLines(false)
//    yAx.setDrawAxisLine(false)
    yAx.axisMinimum = yMin
    yAx.axisMaximum = yMax

    val xAx = this.xAxis
//    xAx.setDrawLabels(false)
    xAx.position = XAxis.XAxisPosition.BOTTOM
//    xAx.setDrawGridLines(false)
//    xAx.setDrawAxisLine(false)
    xAx.axisMinimum = xMin
    xAx.axisMaximum = xMax + 0.01f

    this.data = LineData(lines.toList())
    this.description.isEnabled = false
    this.legend.isEnabled = false
    isDragEnabled = true
}

private fun yGridLines(
    lines: MutableList<LineDataSet>,
    spacing: Float,
    yMin: Float,
    yMax: Float,
    xMin: Float,
    xMax: Float,
    major: Boolean,
    lineColor: Int,
) {
    val nY = ((yMax - yMin) / spacing).roundToInt()
    for (i in 0..nY) {
        val yl = yMin + i * spacing
        val ep = listOf(Entry(xMin, yl), Entry(xMax, yl))
        lines.add(makeGridLineDataSet(ep, major, lineColor))
    }
}

//xGridLines(lines, minorX, yMin, yMax, xMin, xMax, false)
private fun xGridLines(
    lines: MutableList<LineDataSet>,
    spacing: Float,
    yMin: Float,
    yMax: Float,
    xMin: Float,
    xMax: Float,
    major: Boolean,
    lineColor: Int,
) {
    val nX = ((xMax - xMin) / spacing).roundToInt()
    for (i in 0..nX) {
        val xl = xMin + i * spacing
        val ep = listOf(Entry(xl, yMin), Entry(xl, yMax))
        lines.add(makeGridLineDataSet(ep, major, lineColor))
    }
}

private fun makeGridLineDataSet(e: List<Entry>, major: Boolean, lineColor: Int): LineDataSet {
    val ds = LineDataSet(e, "")
    ds.setDrawCircles(false)
    if (major) {
        ds.color = lineColor
        ds.lineWidth = 1f
    } else {
        ds.color = lineColor
        ds.lineWidth = 0.5f
        ds.enableDashedLine(10f, 10f, 0f)
    }
    ds.setDrawValues(false)
    ds.isHighlightEnabled = false
    return ds
}
