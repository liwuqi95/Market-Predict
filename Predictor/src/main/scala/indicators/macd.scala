package indicators

class ema(val dayParam: Int) {

  var dayNum: Int = dayParam

  var priceData : List[Float] = List()


  def addData(data: Float):Float ={
    priceData = priceData :+ data
    if (priceData.length > dayNum)
      priceData = priceData.drop(1)

    getEma()
  }

  def getEma(): Float ={
    if(priceData.length == dayNum) {
      priceData.sum/dayNum
    }else{
      0
    }
  }

  def print(): Unit ={
    println("EMA data is " + priceData)
  }


}

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

//    if (previousResult)
//      print("go up    ")
//    else
//      print("go down   ")
//    print(dayParam1 + "-day ema is " + firstData + "   ")
//    println(dayParam2 + "-day ema is " + secondData)


    previousResult
  }
}

