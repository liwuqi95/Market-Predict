package indicators

class aroon(val dayParam: Int) {
  var dayNum: Int = dayParam
  var priceData : List[Float] = List()

  def addData(data: Float):Unit = {
    priceData = priceData :+ data
    if (priceData.length > dayNum)
      priceData = priceData.drop(1)
  }

  def getLowIndex(): Int = {
    var min: Float = Float.MaxValue
    var index: Int = 0
    for (num <- priceData) {
      if (num < min){
        min = num
        index = priceData.indexOf(num)
      }
    }
    index
  }

  def getHighIndex(): Int = {
    var max: Float = Float.MinValue
    var index: Int = 0
    for (num <- priceData){
      if (num > max){
        max = num
        index = priceData.indexOf(num)
      }
    }
    index
  }

  def computeAROONResult(data: Float): Int = {
    addData(data)
    val lowIndex = getLowIndex()
    val highIndex = getHighIndex()
    val aroonUp = ((highIndex.toFloat+1)/25)*100
    val aroonDown = ((lowIndex.toFloat+1)/25)*100

    if (aroonUp == 100 && aroonDown <= 30)
      ResultTypes.strongBuy
    else if (aroonUp <= 30 && aroonDown == 100)
      ResultTypes.strongSell
    else if (aroonUp >= 50 && aroonDown <= 50)
      ResultTypes.buy
    else if (aroonUp <= 50 && aroonDown >= 50)
      ResultTypes.sell
    else
      ResultTypes.neutral

  }
}
