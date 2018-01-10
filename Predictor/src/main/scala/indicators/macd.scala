package indicators

class macd(val dayParam1: Int, val dayParam2: Int) {
  var firstSma:sma = new sma(dayParam1)
  var secondSma:sma = new sma(dayParam2)
  var dayCounter: Int = 0
  var dayFirstBigger:Boolean = if (dayParam1 > dayParam2) true else false
  var trendFirstBigger:Boolean = false
  var previousResult:Boolean = false

  def computeMACDResult(data: Float): Int ={
    val firstData = firstSma.addData(data)
    val secondData = secondSma.addData(data)
    dayCounter += 1
    if (dayCounter >= Math.max(dayParam1, dayParam2)) {
      if (dayFirstBigger) {
        if (trendFirstBigger && firstData < secondData) {
          trendFirstBigger = false
          previousResult = true
        }

        else if (!trendFirstBigger && firstData > secondData) {
          trendFirstBigger = true
          previousResult = false
        }
      }
      else {
        if (trendFirstBigger && firstData < secondData) {
          trendFirstBigger = false
          previousResult = false
        }
        else if (!trendFirstBigger && firstData > secondData) {
          trendFirstBigger = true
          previousResult = true
        }
      }


      if (previousResult)
        ResultTypes.buy
      else
        ResultTypes.sell
    }
    else
      ResultTypes.invalid
  }
}

