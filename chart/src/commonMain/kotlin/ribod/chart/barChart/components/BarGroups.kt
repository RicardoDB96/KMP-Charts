package ribod.chart.barChart.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import ribod.chart.barChart.model.BarParameters

internal fun DrawScope.drawBarGroups(
  barsParameters: List<BarParameters>,
  upperValue: Double,
  barWidth: Dp,
  xRegionWidth: Dp,
  spaceBetweenBars: Dp,
  maxWidth: Dp,
  height: Dp,
  animatedProgress: Animatable<Float, AnimationVector1D>,
  barCornerRadius: Dp,
  textMeasure: TextMeasurer,
  showBarValue: Boolean
) {

  barsParameters.forEachIndexed { barIndex, bar ->

    bar.data.forEachIndexed { index, data ->
      val ratio = (data.toFloat()) / (upperValue.toFloat())
      val barLength = ((height / 1.02.toFloat().dp).toDp() * animatedProgress.value) * ratio

      val xAxisLength = (index * xRegionWidth).coerceAtLeast(0.dp)
      val lengthWithRatio = (xAxisLength + (barIndex * (barWidth + spaceBetweenBars))).coerceAtLeast(0.dp)

      drawRoundRect(
        brush = Brush.verticalGradient(listOf(bar.barColor, bar.barColor)),
        topLeft = Offset(
          lengthWithRatio.coerceAtMost(maxWidth).toPx(),
          height.value - barLength.toPx()
        ),
        size = Size(
          width = barWidth.toPx(),
          height = barLength.toPx()
        ),
        cornerRadius = CornerRadius(barCornerRadius.toPx())
      )

      if (showBarValue && data.toInt() != 0) {
        val textLayoutResult = textMeasure.measure(
          text = data.toInt().toString(),
          style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
          )
        )

        drawText(
          textLayoutResult = textLayoutResult,
          topLeft = Offset(
            x = lengthWithRatio.coerceAtMost(maxWidth).toPx() + (barWidth.toPx() - textLayoutResult.size.width) / 2,
            y = height.value - barLength.toPx()
          )
        )
      }
    }
  }
}
