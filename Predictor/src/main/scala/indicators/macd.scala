package indicators

class macd(val dayParam1: Int, val dayParam2: Int) {
  var firstSma:sma = new sma(dayParam1)
  var secondSma:sma = new sma(dayParam2)
  var dayFirstBigger:Boolean = if (dayParam1 > dayParam2) true else false
  var trendFirstBigger:Boolean = false
  var previousResult:Boolean = false

  def computeMACDResult(data: Float): Boolean ={
    val firstData = firstSma.addData(data)
    val secondData = secondSma.addData(data)

    if (dayFirstBigger){
      if (trendFirstBigger && firstData < secondData){
        trendFirstBigger = false
        previousResult = true
      }

      else if (!trendFirstBigger && firstData > secondData){
        trendFirstBigger = true
        previousResult = false
      }
    }
    else{
      if (trendFirstBigger && firstData < secondData ){
        trendFirstBigger = false
        previousResult = false
      }
      else if (!trendFirstBigger && firstData > secondData){
        trendFirstBigger = true
        previousResult = true
      }
    }

//    if (previousResult)
//      print("go up    ")
//    else
//      print("go down   ")
//    print(dayParam1 + "-day sma is " + firstData + "   ")
//    println(dayParam2 + "-day sma is " + secondData)


    previousResult
  }
}

