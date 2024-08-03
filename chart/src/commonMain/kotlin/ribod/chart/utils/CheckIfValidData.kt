package ribod.chart.utils

import ribod.chart.barChart.model.BarParameters

internal fun checkIfDataValid(
  xAxisData: List<String>,
  barParameters: List<BarParameters> = emptyList()
) {
  val data = barParameters.map { it.data }
  data.forEach {
    if (it.size != xAxisData.size) {
      throw Exception("The data size of bar must be equal to the x-axis data size.")
    }
    checkIfDataIsNegative(it)
  }
}

internal fun checkIfDataIsNegative(data: List<Double>) {
  data.forEach {
    if (it < 0.0) {
      throw Exception("The data can't contains negative values.")
    }
  }
}