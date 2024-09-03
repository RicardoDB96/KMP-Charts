package ribod.chart.barChart.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ribod.chart.barChart.model.BarParameters
import ribod.chart.base.baseChartContainer
import ribod.chart.base.xAxisDrawing
import ribod.chart.utils.ChartDefaultValues.specialChart
import ribod.chart.utils.checkIfDataValid
import ribod.chart.utils.formatToThousandsMillionsBillions

@Composable
internal fun BarChartContent(
  barsParameters: List<BarParameters>,
  gridColor: Color,
  xAxisData: List<String>,
  isShowGrid: Boolean,
  animateChart: Boolean,
  showGridWithSpacer: Boolean,
  yAxisStyle: TextStyle,
  xAxisStyle: TextStyle,
  backgroundLineWidth: Float,
  yAxisRange: Int,
  showXAxis: Boolean,
  showYAxis: Boolean,
  barWidth: Dp,
  spaceBetweenBars: Dp,
  spaceBetweenGroups: Dp,
  topValue: Float,
  modifier: Modifier = Modifier,
  barCornerRadius: Dp,
  goal: Int?,
  showBarValue: Boolean,
  goalColor: Color,
  isColorAdaptive: Boolean,
  barPercentage: Int
) {

  val textMeasure = rememberTextMeasurer()

  val animatedProgress = remember(barsParameters) {
    if (animateChart) Animatable(0f) else Animatable(1f)
  }
  var upperValue by rememberSaveable {
    mutableStateOf(if (barsParameters.getUpperValue() > topValue) barsParameters.getUpperValue() else topValue.toDouble())
  }

  var lowerValue by rememberSaveable {
    mutableStateOf(barsParameters.getLowerValue())
  }
  var maxWidth by remember { mutableStateOf(0.dp) }
  var yTextLayoutResult by remember { mutableStateOf(0.dp) }
  var maxHeight by remember { mutableStateOf(0f) }
  var xRegionWidthWithoutSpacing by remember { mutableStateOf(0.dp) }
  var xRegionWidth by remember { mutableStateOf(0.dp) }

  //initial height set at 0.dp
  var boxWidth by remember { mutableStateOf(0.dp) }
  var boxHeight by remember { mutableStateOf(0.dp) }

  // get local density from composable
  val density = LocalDensity.current

  checkIfDataValid(xAxisData = xAxisData, barParameters = barsParameters)
  Box(modifier = modifier.fillMaxSize().onGloballyPositioned {
    boxWidth = with(density) {
      it.size.width.toDp()
    }
    boxHeight = with(density) {
      it.size.height.toDp()
    }
  }
  ) {
    Canvas(
      modifier = Modifier.fillMaxSize()
    ) {

      val spacingY = (boxHeight / 10)
      xRegionWidth = ((barWidth + spaceBetweenBars) * barsParameters.size) + spaceBetweenGroups
      xRegionWidthWithoutSpacing = xRegionWidth - spaceBetweenGroups
      maxWidth = (xRegionWidth * xAxisData.size) - spaceBetweenGroups
      maxHeight = boxHeight.toPx() - spacingY.toPx() + 10.dp.toPx()

      baseChartContainer(
        xAxisData = xAxisData,
        textMeasure = textMeasure,
        upperValue = upperValue.toFloat(),
        lowerValue = lowerValue.toFloat(),
        isShowGrid = isShowGrid,
        backgroundLineWidth = backgroundLineWidth,
        gridColor = gridColor,
        showGridWithSpacer = showGridWithSpacer,
        spacingY = spacingY,
        yAxisStyle = yAxisStyle,
        xAxisStyle = xAxisStyle,
        yAxisRange = yAxisRange,
        showXAxis = showXAxis,
        showYAxis = showYAxis,
        isFromBarChart = true,
        xRegionWidth = xRegionWidth,
        goal = goal,
        goalColor = goalColor
      )
    }

    Box(
      modifier = Modifier.fillMaxSize().padding(start = yTextLayoutResult + (yTextLayoutResult / 2))
        .horizontalScroll(rememberScrollState())
    ) {

      Canvas(
        Modifier.width(maxWidth).fillMaxHeight()

      ) {
        yTextLayoutResult = textMeasure.measure(
          text = AnnotatedString(upperValue.toFloat().formatToThousandsMillionsBillions()),
        ).size.width.toDp()

        drawBarGroups(
          barsParameters = barsParameters,
          upperValue = upperValue,
          barWidth = barWidth,
          xRegionWidth = xRegionWidth,
          spaceBetweenBars = spaceBetweenBars,
          maxWidth = maxWidth,
          height = maxHeight.dp,
          animatedProgress = animatedProgress,
          barCornerRadius = barCornerRadius,
          textMeasure = textMeasure,
          showBarValue = showBarValue,
          goal = goal,
          goalColor = goalColor,
          isColorAdaptive = isColorAdaptive,
          barPercentage = barPercentage
        )

        xAxisDrawing(
          xAxisData = xAxisData,
          textMeasure = textMeasure,
          xAxisStyle = xAxisStyle,
          specialChart = specialChart,
          xRegionWidth = xRegionWidth,
          xRegionWidthWithoutSpacing = xRegionWidthWithoutSpacing,
          height = maxHeight.dp,
        )
      }
    }
  }


  LaunchedEffect(barsParameters, animateChart) {
    if (animateChart) {
      collectToSnapShotFlow(barsParameters) {
        upperValue = if (it.getUpperValue() > topValue) it.getUpperValue() else topValue.toDouble()
        lowerValue = it.getLowerValue()
      }

      delay(400)
      animatedProgress.animateTo(
        targetValue = 1f, animationSpec = tween(durationMillis = 1000, delayMillis = 0)
      )
    }
  }
}

private fun List<BarParameters>.getUpperValue(): Double {
  return this.flatMap { item -> item.data }.maxOrNull() ?: 0.0
}

private fun List<BarParameters>.getLowerValue(): Double {
  return this.flatMap { item -> item.data }.minOrNull() ?: 0.0
}

private fun CoroutineScope.collectToSnapShotFlow(
  linesParameters: List<BarParameters>, makeUpdateData: (List<BarParameters>) -> Unit
) {
  this.launch {
    snapshotFlow {
      linesParameters
    }.collect {
      makeUpdateData(it)
    }
  }
}