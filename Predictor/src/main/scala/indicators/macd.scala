package indicators

import indicators.ema

class macd(val dayParam1: Int, val dayParam2: Int) {
  var firstEma:ema = new ema(dayParam1)
  var secondEma:ema = new ema(dayParam2)
  var dayFirstBigger:Boolean = if (dayParam1 > dayParam2) true else false
  var trendFirstBigger:Boolean = false
  var previousResult:Boolean = false

  def compuateMACDResult(data: Float): Boolean ={
    var firstData = firstEma.addData(data)
    var secondData = secondEma.addData(data)

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

    if (previousResult)
      print("go up    ")
    else
      print("go down   ")
    print(dayParam1 + "-day ema is " + firstData + "   ")
    println(dayParam2 + "-day ema is " + secondData)


    previousResult
  }
}

