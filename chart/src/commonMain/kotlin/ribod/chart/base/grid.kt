package ribod.chart.base

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import ribod.chart.base.model.GridOrientation
import ribod.chart.utils.formatToThousandsMillionsBillions

internal fun DrawScope.grid(
  xAxisDataSize: Int,
  isShowGrid: Boolean,
  gridColor: Color,
  backgroundLineWidth: Float,
  showGridWithSpacer: Boolean,
  spacingY: Dp,
  yAxisRange: Int,
  specialChart: Boolean,
  upperValue: Float,
  textMeasurer: TextMeasurer,
  gridOrientation: GridOrientation,
  xRegionWidth: Dp,
  goal: Int?,
  goalColor: Color
) {
  if (specialChart) {
    return
  }

  val yTextLayoutResult = textMeasurer.measure(
    text = AnnotatedString(upperValue.formatToThousandsMillionsBillions()),
  ).size.width
  val textSpace = yTextLayoutResult - (yTextLayoutResult / 4)

  if (isShowGrid) {
    when (gridOrientation) {
      GridOrientation.HORIZONTAL -> drawHorizontalGrid(
        spacingY = spacingY,
        yAxisRange = yAxisRange,
        gridColor = gridColor,
        backgroundLineWidth = backgroundLineWidth,
        showGridWithSpacer = showGridWithSpacer,
        yTextLayoutResult = textSpace,
        upperValue = upperValue,
        goal = goal,
        goalColor = goalColor
      )

      GridOrientation.VERTICAL -> drawVerticalGrid(
        xAxisDataSize = xAxisDataSize,
        xRegionWidth = xRegionWidth,
        gridColor = gridColor,
        backgroundLineWidth = backgroundLineWidth,
        yTextLayoutResult = textSpace
      )

      else -> {
        drawHorizontalGrid(
          spacingY = spacingY,
          yAxisRange = yAxisRange,
          gridColor = gridColor,
          xEndLength = 38.dp.toPx(),
          backgroundLineWidth = backgroundLineWidth,
          showGridWithSpacer = showGridWithSpacer,
          yTextLayoutResult = textSpace,
          upperValue = upperValue,
          goal = goal,
          goalColor = goalColor
        )

        drawVerticalGrid(
          xAxisDataSize = xAxisDataSize,
          xRegionWidth = xRegionWidth,
          gridColor = gridColor,
          yEndLength = 9f.toDp(),
          backgroundLineWidth = backgroundLineWidth,
          yTextLayoutResult = textSpace
        )
      }
    }
  }
}

private fun DrawScope.drawHorizontalGrid(
  spacingY: Dp,
  yAxisRange: Int,
  gridColor: Color,
  xEndLength: Float = 0f,
  backgroundLineWidth: Float,
  showGridWithSpacer: Boolean,
  upperValue: Float,
  yTextLayoutResult: Int,
  goal: Int?,
  goalColor: Color
) {

  val xAxisMaxValue = size.width

  val textSpace = yTextLayoutResult - (yTextLayoutResult / 4)

  val goalYAlignmentValue = yAxisCalc(spacingY, (goal ?: -10).toFloat(), upperValue.toInt())
  val goalYMinus = yAxisCalc(spacingY, ((goal ?: -10) - 1.5).toFloat(), upperValue.toInt())
  val goalYPlus = yAxisCalc(spacingY, ((goal ?: -10) + 1.5).toFloat(), upperValue.toInt())

  (0..upperValue.toInt() step (upperValue.toInt() + 1) / yAxisRange).forEach { i ->
    val yAlignmentValue = yAxisCalc(spacingY, i.toFloat(), upperValue.toInt())

    if (yAlignmentValue != goalYAlignmentValue && yAlignmentValue !in goalYPlus..goalYMinus && yAlignmentValue !in yAxisCalc(
        spacingY,
        upperValue,
        upperValue.toInt()
      )..yAxisCalc(
        spacingY,
        upperValue - 2,
        upperValue.toInt()
      )
    ) {
      drawGrid(
        gridColor = gridColor,
        xStart = (yTextLayoutResult * 1.5.toFloat().toDp()).toPx(),
        yStart = yAlignmentValue,
        xEnd = xAxisMaxValue - (textSpace / 0.9.toFloat().toDp().toPx()),
        yEnd = yAlignmentValue,
        backgroundLineWidth = backgroundLineWidth,
        bottomY = if (i == 0) yAlignmentValue else null
      )
    }
  }

  // Always draw upper value
  drawGrid(
    gridColor = gridColor,
    xStart = (yTextLayoutResult * 1.5.toFloat().toDp()).toPx(),
    yStart = yAxisCalc(spacingY, upperValue, upperValue.toInt()),
    xEnd = xAxisMaxValue - (textSpace / 0.9.toFloat().toDp().toPx()),
    yEnd = yAxisCalc(spacingY, upperValue, upperValue.toInt()),
    backgroundLineWidth = backgroundLineWidth
  )

  if (goal != null) {
    drawGrid(
      gridColor = goalColor,
      xStart = (yTextLayoutResult * 1.5.toFloat().toDp()).toPx(),
      yStart = goalYAlignmentValue,
      xEnd = xAxisMaxValue - (textSpace / 0.9.toFloat().toDp().toPx()),
      yEnd = goalYAlignmentValue,
      backgroundLineWidth = backgroundLineWidth
    )
  }
}

fun DrawScope.yAxisCalc(spacingY: Dp, i: Float, yAxisRange: Int) = (size.height.toDp()
  .toPx() - (spacingY.toPx()) - i * (size.height.toDp() - spacingY).toPx() / yAxisRange) + 9.dp.toPx()

private fun DrawScope.drawVerticalGrid(
  xAxisDataSize: Int,
  xRegionWidth: Dp,
  gridColor: Color,
  backgroundLineWidth: Float,
  yTextLayoutResult: Int,
  yEndLength: Dp = 3.toFloat().toDp()
) {
  (0..xAxisDataSize).forEach { i ->
    val xLength = (i * xRegionWidth)

    drawGrid(
      gridColor = gridColor,
      xStart = xLength.toPx() + (yTextLayoutResult * 1.5.toFloat().toDp()).toPx(),
      yStart = 10.dp.toPx(),
      xEnd = xLength.toPx() + (yTextLayoutResult * 1.5.toFloat().toDp()).toPx(),
      yEnd = size.height - (size.height.toDp() / yEndLength),
      backgroundLineWidth = backgroundLineWidth
    )
  }
}

private fun DrawScope.drawGrid(
  gridColor: Color,
  xStart: Float,
  yStart: Float,
  xEnd: Float,
  yEnd: Float,
  backgroundLineWidth: Float,
  bottomY: Float? = null
) {
  drawLine(
    color = gridColor,
    start = Offset(xStart, yStart),
    end = Offset(xEnd, yEnd),
    strokeWidth = backgroundLineWidth,
    pathEffect = if (yStart == bottomY) null else PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
  )
}