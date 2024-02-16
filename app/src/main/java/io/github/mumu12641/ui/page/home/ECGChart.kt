package io.github.mumu12641.ui.page.home

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

val TAG = "ECGChart"

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewEcgChart() {
    EcgChart(data = List(100) { (Random.nextFloat() - 0.5f) * 160 })
}

@Composable
fun EcgChart(data: List<Float>) {
    val lines = mutableListOf<LineDataSet>()
    AndroidView(factory = { context ->
        LineChart(context).apply {
//                val x = (0..100).map { i -> 0.1f * i }
            val x = (0..100).map { i -> 1f * i }
//                val y = x.map { i -> 5 * sin(7 * i) * sin(0.5f * i) * cos(3.25f * i) }
//                val y = x.map { i -> sin(i) }
//                val y = List(100) { (Random.nextFloat() - 0.5f) * 100 }
            val y = List(100) { 0f }

            val primaryLine = LineDataSet(x.zip(y).map { Entry(it.first, it.second) }, "primary")

            primaryLine.apply {
                setDrawCircles(false)
                lineWidth = 2f
                color = Color.RED
                setDrawValues(false)
                isHighlightEnabled = false
            }
            lines.add(primaryLine)

            val majorX = 10f
            val majorY = 40f
            val minorX = majorX / 10
            val minorY = majorY / 5

            val xMin = floor(x.min() / majorX) * majorX
            val xMax = ceil(x.max() / majorX) * majorX
            val yMin = floor(-80 / majorY) * majorY
            val yMax = ceil(80 / majorY) * majorY

            xGridLines(lines, minorX, yMin, yMax, xMin, xMax, false)
            yGridLines(lines, minorY, yMin, yMax, xMin, xMax, false)

            xGridLines(lines, majorX, yMin, yMax, xMin, xMax, true)
            yGridLines(lines, majorY, yMin, yMax, xMin, xMax, true)


            this.axisRight.isEnabled = false
            val yAx = this.axisLeft
            yAx.setDrawLabels(false)
            yAx.setDrawGridLines(false)
            yAx.setDrawAxisLine(false)
            yAx.axisMinimum = yMin
            yAx.axisMaximum = yMax

            val xAx = this.xAxis
            xAx.setDrawLabels(false)
            xAx.position = XAxis.XAxisPosition.BOTTOM
            xAx.setDrawGridLines(false)
            xAx.setDrawAxisLine(false)
            xAx.axisMinimum = xMin
            xAx.axisMaximum = xMax + 0.01f

            this.data = LineData(lines.toList())
            this.description.isEnabled = false
            this.legend.isEnabled = false
        }
    }, update = {
        val x = (0..100).map { i -> 1f * i }
        val y = data
        val primaryLine = LineDataSet(x.zip(y).map { Entry(it.first, it.second) }, "primary")
        primaryLine.apply {
            setDrawCircles(false)
            lineWidth = 2f
            color = Color.RED
            setDrawValues(false)
            isHighlightEnabled = false
        }
        it.data.dataSets[0] = primaryLine
    }, modifier = Modifier
        .fillMaxWidth()
        .height(400.dp)
        .clip(RoundedCornerShape(32.dp))
    )
}

private fun yGridLines(
    lines: MutableList<LineDataSet>,
    spacing: Float,
    yMin: Float,
    yMax: Float,
    xMin: Float,
    xMax: Float,
    major: Boolean
) {
    val nY = ((yMax - yMin) / spacing).roundToInt()
    for (i in 0..nY) {
        val yl = yMin + i * spacing
        val ep = listOf(Entry(xMin, yl), Entry(xMax, yl))
        lines.add(makeGridLineDataSet(ep, major))
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
    major: Boolean
) {
    val nX = ((xMax - xMin) / spacing).roundToInt()
    for (i in 0..nX) {
        val xl = xMin + i * spacing
        val ep = listOf(Entry(xl, yMin), Entry(xl, yMax))
        lines.add(makeGridLineDataSet(ep, major))
    }
}

private fun makeGridLineDataSet(e: List<Entry>, major: Boolean): LineDataSet {
    val ds = LineDataSet(e, "")
    ds.setDrawCircles(false)
    if (major) {
        ds.color = Color.BLACK
        ds.lineWidth = 1f
    } else {
        ds.color = Color.BLACK
        ds.lineWidth = 0.5f
        ds.enableDashedLine(10f, 10f, 0f)
    }
    ds.setDrawValues(false)
    ds.isHighlightEnabled = false
    return ds
}
