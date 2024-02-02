package io.github.mumu12641.ui.page.welcome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import io.github.mumu12641.R
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(request: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.welcome))
    Scaffold(topBar = { LargeTopAppBar(title = { Text(text = stringResource(id = R.string.welcome)) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(bottom = 30.dp),
                text = { Text(text = stringResource(id = R.string.agree_continue)) },
                icon = { Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null) },
                onClick = { request() })
        }
    ) {
//        TestChart(modifier = Modifier.padding(it))
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .aspectRatio(1.38f)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    LottieAnimation(
                        composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .weight(1f)
                            .size(400.dp),
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.welcome_tip),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomePrev() {
    WelcomeScreen {
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TestChart() {
    val lines = mutableListOf<LineDataSet>()
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                val x = (0..100).map { i -> 0.01f * i }
                val y = x.map { i -> 5 * sin(7 * i) * sin(0.5f * i) * cos(3.25f * i) }
                val primaryLine =
                    LineDataSet(x.zip(y).map { Entry(it.first, it.second) }, "primary")

                primaryLine.setDrawCircles(false)
                primaryLine.lineWidth = 3f
                primaryLine.color = android.graphics.Color.RED
                primaryLine.setDrawValues(false)
                primaryLine.isHighlightEnabled = false

                val majorX = 1f
                val majorY = 4f
                val minorX = majorX / 10
                val minorY = majorY / 5

                val xMin = floor(x.min() / majorX) * majorX
                val xMax = ceil(x.max() / majorX) * majorX
                val yMin = floor(y.min() / majorY) * majorY
                val yMax = ceil(y.max() / majorY) * majorY

                xGridLines(lines, minorX, yMin, yMax, xMin, xMax, false)
                yGridLines(lines, minorY, yMin, yMax, xMin, xMax, false)

                xGridLines(lines, majorX, yMin, yMax, xMin, xMax, true)
                yGridLines(lines, majorY, yMin, yMax, xMin, xMax, true)

                lines.add(primaryLine)

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
        },
        update = {
        },
        modifier = Modifier.fillMaxWidth().height(200.dp)
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
        ds.color = android.graphics.Color.BLACK
        ds.lineWidth = 1f
    } else {
        ds.color = android.graphics.Color.BLACK
        ds.lineWidth = 0.5f
        ds.enableDashedLine(10f, 10f, 0f)
    }
    ds.setDrawValues(false)
    ds.isHighlightEnabled = false
    return ds
}
