package indicators

class cci(val dayParam: Int) extends Serializable {
  var dayNum: Int = dayParam

  var normalPriceData : List[Float] = List()
  var typicalPriceData: List[Float] = List()

  def addData(data: Float, priceData: List[Float]):List[Float] = {
    var newList: List[Float] = priceData
    newList = newList :+ data
    if (newList.length > dayNum)
      newList = newList.drop(1)
    newList
  }

  def getLow(priceData: List[Float]): Float = {
    var min: Float = Float.MaxValue
    for (num <- priceData){
      if (num < min)
        min = num
    }
    min
  }

  def getHigh(priceData: List[Float]): Float = {
    var max: Float = Float.MinValue
    for (num <- priceData){
      if (num > max)
        max = num
    }
    max
  }

  def getMeanDerivation(): Float ={
    var sum: Float = 0
    val smaValue = typicalPriceData.sum/typicalPriceData.length
    for (price <- typicalPriceData){
      sum += Math.abs(price-smaValue)
    }
    sum/typicalPriceData.length
  }

  val computeCCIResult = (data: Float) => {
    normalPriceData = addData(data, normalPriceData)
    val normalHigh: Float = getHigh(normalPriceData)
    val normalLow: Float = getLow(normalPriceData)
    val typicalPrice: Float = (normalHigh+normalLow+data)/3
    typicalPriceData = addData(typicalPrice, typicalPriceData)

    if (typicalPriceData.length == dayNum){
      val smaValue: Float = typicalPriceData.sum/typicalPriceData.length
      val meanDerivation: Float = getMeanDerivation()
      val cciValue = (typicalPrice-smaValue)/(0.015*meanDerivation)

      if (cciValue >= 200)
        ResultTypes.strongSell
      else if (cciValue <= -200)
        ResultTypes.strongBuy
      else if (cciValue < -50 && cciValue > -200)
        ResultTypes.sell
      else if (cciValue > 50 && cciValue < 200)
        ResultTypes.buy
      else
        ResultTypes.neutral
    }
    else
      ResultTypes.invalid
  }
}
