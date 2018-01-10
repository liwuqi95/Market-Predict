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

    // up(0) >= 70 && down(1) <= 30, bull
    // up(0) <= 30 &7 down(1) >= 70, bear
    // when 30-70, if up cross above down, bull
    // if down cross above up, bear

    if (aroonUp >= 70 && aroonDown <= 30)
      ResultTypes.buy
    else if (aroonUp <= 30 && aroonDown >= 70)
      ResultTypes.sell
    else
      ResultTypes.neutral

  }
}
