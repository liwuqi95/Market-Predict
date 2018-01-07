package indicators

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.asc


class indicator(val dataframe: DataFrame) {

  val DF: DataFrame = dataframe

  /** SMA **/
  //https://www.investopedia.com/articles/technical/052201.asp
  val sma_indicator = new sma(50)

  /** EMA **/
  //https://www.investopedia.com/terms/e/ema.asp
  val ema_indicator = new ema(12)

  /** MACD **/
  //https://www.investopedia.com/articles/forex/05/macddiverge.asp
  val macd_indicator = new macd(5, 14)

  /** RSI **/
  //https://www.investopedia.com/articles/technical/071601.asp
  val rsi_indicator = new rsi(14)

  /** STOCH **/
  //https://www.investopedia.com/university/indicator_oscillator/ind_osc8.asp
  val stoch_indicator = new stoch(14)

  /** CCI **/
  //http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:commodity_channel_index_cci
  val cci_indicator = new cci(20)


  def compute: Unit = {

    val spark = SparkSession.builder().appName("Indicator").getOrCreate()

    import spark.implicits._

    var cci_counter: Float = 0
    var total_counter: Float = 0

    for (iteration <- DF.orderBy(asc("date")).filter($"price" =!= "null" ).collect()){
      print(iteration + "    ")


      /** SMA **/
      var isSMAUp:Boolean = false
      if (iteration.getString(2) != null)
        isSMAUp = sma_indicator.computeSMAResult(iteration.getString(2).toFloat)

      /** EMA **/
      var isEMAUp:Boolean = false
      if (iteration.getString(2) != null)
        isEMAUp = ema_indicator.computeEMAResult(iteration.getString(2).toFloat)

      /** MACD **/
      var isMACDUp:Boolean = false
      if (iteration.getString(2) != null)
        isMACDUp = macd_indicator.computeMACDResult(iteration.getString(2).toFloat)

      /** RSI **/
      var RSIValue:Float = 50
      // this value is only being considered when it is bigger than 70/80 and lower than 30/20
      // in between, we'll not consider
      if (iteration.getString(2) != null)
        RSIValue = rsi_indicator.computeRSIResult(iteration.getString(2).toFloat)

      /** STOCH **/
      var STOCHValue: Float = 50
      // this value is considered overbought when above 80, oversold when below 20
      if (iteration.getString(2) != null)
        STOCHValue = stoch_indicator.computeSTOCHResult(iteration.getString(2).toFloat)

      /** CCI **/
      var CCIValue: Float = 0
      // this value is considered overbought when above 80, oversold when below 20
      if (iteration.getString(2) != null)
        CCIValue = cci_indicator.computeCCIResult(iteration.getString(2).toFloat)

      total_counter += 1
      if (CCIValue >= -100 && CCIValue <= 100)
        cci_counter += 1


      println("SMA: " + isSMAUp + "   EMA: " + isEMAUp + "   MACD: " + isMACDUp + "   RSI: " + RSIValue + "   STOCH: " + STOCHValue
        + "   CCI: " + CCIValue)

      spark.stop()
    }
  }
}
