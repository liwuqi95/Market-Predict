package indicators

class sma(val dayParam: Int) extends Serializable {
  var dayNum: Int = dayParam

  var priceData : List[Float] = List()

  var averageBigger:Boolean = false
  var previousResult:Boolean = false


  def addData(data: Float):Float ={
    priceData = priceData :+ data
    if (priceData.length > dayNum)
      priceData = priceData.drop(1)

    getSma()
  }

  def getSma(): Float ={
    if(priceData.length == dayNum) {
      priceData.sum/dayNum
    }else{
      0
    }
  }

  val computeSMAResult = (data: Float) => {
    val average = addData(data)


    if (priceData.length == dayNum) {
      if (averageBigger && data > average) {
        previousResult = true
        averageBigger = false
      }
      else if (!averageBigger && data < average) {
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
