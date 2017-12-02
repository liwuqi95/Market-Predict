package indicators

class ema(val dayParam: Int) {

  var dayNum: Int = dayParam

  var priceData : List[Float] = List()


  def addData(data: Float):Unit ={
    priceData = priceData :+ data
    if (priceData.length > dayNum)
      priceData = priceData.dropRight(dayNum)
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
