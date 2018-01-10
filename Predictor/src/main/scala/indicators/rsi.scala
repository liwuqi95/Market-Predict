package indicators

class rsi(val dayParam: Int) extends Serializable {
  val dayNum:Int = dayParam

  var priceData : List[Float] = List()

  var prevRSIValue: Float = 50

  def addData(data: Float): Unit ={
    priceData = priceData :+ data
    if (priceData.length > dayNum+1)
      priceData = priceData.drop(1)
  }

  def getRSIValue(): Float = {
    prevRSIValue
  }

  val computeRSIResult = (data: Float) => {
    addData(data)

    if (priceData.length == dayNum+1){
      var upSum:Float = 0
      var downSum:Float = 0

      for (i <- 1 to dayNum){
        if (priceData(i) > priceData(i-1)){
          upSum += priceData(i) - priceData(i-1)
        }
        else{
          downSum += priceData(i-1) - priceData(i)
        }
      }

      val rs:Float = upSum/downSum
      val rsi_value:Float = 100 - 100/(1+rs)
      prevRSIValue = rsi_value

      if (rsi_value >= 70)
        ResultTypes.strongSell
      else if (rsi_value >= 55 && rsi_value < 70)
        ResultTypes.buy
      else if (rsi_value <= 45 && rsi_value < 30)
        ResultTypes.sell
      else if (rsi_value <= 30)
        ResultTypes.strongBuy
      else
        ResultTypes.neutral
    }
    else{
      ResultTypes.invalid
    }
  }
}
