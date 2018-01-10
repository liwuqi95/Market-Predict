package indicators

class ema(val dayParam: Int) extends Serializable {
  var dayNum: Int = dayParam

  var priceData : List[Float] = List()

  var averageBigger:Boolean = false
  var previousResult:Boolean = false
  var previousEMA:Float = 0


  def addData(data: Float):Unit ={
    priceData = priceData :+ data
    if (priceData.length > dayNum)
      priceData = priceData.drop(1)
  }

  def getSumAverage(): Float ={
    if(priceData.length == dayNum) {
      priceData.sum/dayNum
    }else{
      0
    }
  }

  val computeEMAResult = (data: Float) => {
    addData(data)

    if (priceData.length == dayNum) {
      val simpleAverage = getSumAverage()
      val multiplier = 2 / (dayNum + 1)
      val exponentialAverage = (data - previousEMA) * multiplier + previousEMA

      if (averageBigger && data > exponentialAverage) {
        previousResult = true
        averageBigger = false
      }
      else if (!averageBigger && data < exponentialAverage) {
        previousResult = false
        averageBigger = true
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