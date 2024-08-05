import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import ribod.chart.barChart.BarChart
import ribod.chart.barChart.model.BarParameters
import ribod.chart.base.model.LegendPosition
import kotlin.random.Random

@Composable
@Preview
fun App() {
  MaterialTheme {
    Column {
      // Remember para mantener el estado de la lista y visibilidad observable.
      val data = remember {
        mutableStateListOf(
          10.0,
          5.0,
          7.0,
          6.0,
          2.0,
          3.0,
          8.0,
          10.0,
          5.0,
          7.0,
          6.0,
          11.0,
          3.0,
          8.0,
          16.0,
          12.0,
          7.0,
          14.0
        )
      }

      var isVisible by remember { mutableStateOf(true) }

      var showLegend by remember { mutableStateOf(false) }

      var showBarValue by remember { mutableStateOf(false) }

      val testBarParameters: List<BarParameters> = listOf(
        BarParameters(
          dataName = "1",
          data = data,
          barColor = Color.Blue,
        )
      )

      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = {
          // Modificar un elemento aleatorio de la lista
          val randomIndex = Random.nextInt(data.size)
          data[randomIndex] += 1.0
        }) {
          Text("Add 1")
        }
        Button(onClick = { isVisible = !isVisible }) {
          Text("Show/Hide chart")
        }
        Button(onClick = { showLegend = !showLegend }) {
          Text("Show/Hide legend")
        }
        Button(onClick = { showBarValue = !showBarValue }) {
          Text("Show/Hide bar value")
        }
      }

      if (isVisible) {
        Box(Modifier.fillMaxSize().padding(24.dp)) {
          BarChart(
            chartParameters = testBarParameters,
            gridColor = Color.DarkGray,
            xAxisData = listOf(
              "1",
              "2",
              "3",
              "4",
              "5",
              "6",
              "7",
              "8",
              "9",
              "10",
              "11",
              "12",
              "13",
              "14",
              "15",
              "16",
              "17",
              "18"
            ),
            isShowGrid = true,
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = TextStyle(
              fontSize = 14.sp,
              color = Color.DarkGray,
            ),
            xAxisStyle = TextStyle(
              fontSize = 14.sp,
              color = Color.DarkGray,
              fontWeight = FontWeight.W400
            ),
            yAxisRange = 3,
            barWidth = 30.dp,
            legendPosition = if (showLegend) LegendPosition.TOP else LegendPosition.DISAPPEAR,
            barCornerRadius = 4.dp,
            topValue = if (data.max() < 15f) 15f else data.max().toFloat(),
            goal = 10,
            showBarValue = showBarValue
          )
        }
      }
    }
  }
}