package indicators

class stochrsi(val dayParam: Int) extends Serializable {
  var dayNum: Int = dayParam

  var rsiData : List[Float] = List()

  def addData(data: Float):Unit = {
    rsiData = rsiData :+ data
    if (rsiData.length > dayNum)
      rsiData = rsiData.drop(1)
  }

  def getLow(): Float = {
    var min: Float = Float.MaxValue
    for (num <- rsiData){
      if (num < min)
        min = num
    }
    min
  }

  def getHigh(): Float = {
    var max: Float = Float.MinValue
    for (num <- rsiData){
      if (num > max)
        max = num
    }
    max
  }

  val computeSTOCHRSIResult = (data: Float) => {
    addData(data)
    if (rsiData.length != dayNum){
      ResultTypes.invalid
    }
    else{
      val high = getHigh()
      val low = getLow()
      val stochRsiValue = ((data - low) / (high - low))*100

      if (stochRsiValue <= 20)
        ResultTypes.strongBuy
      else if (stochRsiValue > 20 && stochRsiValue < 45)
        ResultTypes.sell
      else if (stochRsiValue > 55 && stochRsiValue < 80)
        ResultTypes.buy
      else if (stochRsiValue >= 80)
        ResultTypes.strongSell
      else
        ResultTypes.neutral
    }
  }
}
