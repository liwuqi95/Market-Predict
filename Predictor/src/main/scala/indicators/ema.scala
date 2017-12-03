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
