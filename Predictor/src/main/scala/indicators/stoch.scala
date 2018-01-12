package indicators

class stoch(val dayParam: Int) {
  var dayNum: Int = dayParam

  var priceData : List[Float] = List()
  var perKData : List[Float] = List()

  def addData(data: Float):Unit = {
    priceData = priceData :+ data
    if (priceData.length > dayNum)
      priceData = priceData.drop(1)
  }

  def getLow(): Float = {
    var min: Float = Float.MaxValue
    for (num <- priceData){
      if (num < min)
        min = num
    }
    min
  }

  def getHigh(): Float = {
    var max: Float = Float.MinValue
    for (num <- priceData){
      if (num > max)
        max = num
    }
    max
  }

  def computeSTOCHResult(data: Float): Int = {
    addData(data)
    var high: Float = getHigh()
    var low: Float = getLow()
    if (high != low){
      var PerK: Float = ((data-low)/(high-low))*100
      perKData = perKData :+ PerK
      if (perKData.length > 3){
        perKData = perKData.drop(1)
      }
      val PerD: Float = perKData.sum/perKData.length

      if (PerD >= 80)
        ResultTypes.strongSell
      else if (PerD >= 55 && PerD < 80)
        ResultTypes.buy
      else if (PerD <= 45 && PerD > 20)
        ResultTypes.sell
      else if (PerD <= 20)
        ResultTypes.strongBuy
      else
        ResultTypes.neutral
    }
    else
      ResultTypes.neutral
  }
}
