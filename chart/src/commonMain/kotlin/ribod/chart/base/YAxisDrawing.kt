package ribod.chart.base

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import ribod.chart.utils.formatToThousandsMillionsBillions
import kotlin.math.roundToInt

internal fun DrawScope.yAxisDrawing(
  upperValue: Float,
  lowerValue: Float,
  textMeasure: TextMeasurer,
  spacing: Dp,
  yAxisStyle: TextStyle,
  yAxisRange: Int,
  specialChart: Boolean,
  isFromBarChart: Boolean,
  goal: Int?,
  goalColor: Color
) {
  if (specialChart) {
    return
  }
  val dataRange = if (isFromBarChart) upperValue else upperValue - lowerValue
  val dataStep = dataRange / upperValue

  (0..upperValue.toInt() step (upperValue.toInt() + 1) / yAxisRange).forEach { i ->
    val range = goal ?: -10

    if (i == 0) {
      // Paint goal
      var y = (size.height.toDp() - spacing - range * (size.height.toDp() - spacing) / upperValue)
      drawText(
        textMeasurer = textMeasure,
        text = (dataRange / upperValue * range).roundToInt().toString(),
        style = yAxisStyle.copy(color = goalColor),
        topLeft = Offset(0f, y.toPx())
      )
      // Paint top
      y = (size.height.toDp() - spacing - upperValue * (size.height.toDp() - spacing) / upperValue)
      drawText(
        textMeasurer = textMeasure,
        text = (dataRange / upperValue * upperValue).roundToInt().toString(),
        style = yAxisStyle,
        topLeft = Offset(0f, y.toPx())
      )
    }

    val yValue = if (isFromBarChart) {
      dataStep * i
    } else {
      lowerValue + dataStep * i
    }

    drawContext.canvas.nativeCanvas.apply {
      if (yValue !in (dataStep * upperValue - 2)..(dataStep * upperValue)) {
        val y = (size.height.toDp() - spacing - i * (size.height.toDp() - spacing) / upperValue)
        drawText(
          textMeasurer = textMeasure,
          text = yValue.roundToInt().toString(),
          style = yAxisStyle,
          topLeft = Offset(0f, y.toPx())
        )
      }
    }
  }
}