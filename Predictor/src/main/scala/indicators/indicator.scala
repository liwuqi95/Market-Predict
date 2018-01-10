package indicators

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.asc

object DataTypes extends Enumeration {
  val openPrice = 1
  val highPrice = 2
  val lowPrice = 3
  val closePrice = 4
}

object ResultTypes extends Enumeration{
  val strongSell = -2
  val sell = -1
  val neutral = 0
  val buy = 1
  val strongBuy = 2
}


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

  /** AROON **/
  //http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:aroon
  //https://www.investopedia.com/articles/trading/06/aroon.asp
  val aroon_indicator = new aroon(25)

  /** Row Class **/
  case class Row(Future_price: Float, SMA: Float, EMA: Float, MACD: Float, RSI: Float, STOCH: Float, CCI: Float, AROON: Float)


  def compute: Unit = {

    val spark = SparkSession.builder().appName("Indicator").getOrCreate()

    import spark.implicits._

    var cci_counter: Float = 0
    var total_counter: Float = 0

    for (iteration <- DF.orderBy(asc("Local time")).filter($"Close" =!= "null" ).collect()){
      print(iteration + "    ")

      /** SMA **/
      var isSMAUp:Int = ResultTypes.neutral
      isSMAUp = sma_indicator.computeSMAResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** EMA **/
      var isEMAUp:Int = ResultTypes.neutral
      isEMAUp = ema_indicator.computeEMAResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** MACD **/
      var isMACDUp:Int = ResultTypes.neutral
      isMACDUp = macd_indicator.computeMACDResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** RSI **/
      var RSIValue:Int = ResultTypes.neutral
      // this value is only being considered when it is bigger than 70/80 and lower than 30/20
      // in between, we'll not consider
      RSIValue = rsi_indicator.computeRSIResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** STOCH **/
      var STOCHValue: Int = ResultTypes.neutral
      // this value is considered overbought when above 80, oversold when below 20
      STOCHValue = stoch_indicator.computeSTOCHResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** CCI **/
      var CCIValue: Int = ResultTypes.neutral
      // this value is considered overbought when above 80, oversold when below 20
      CCIValue = cci_indicator.computeCCIResult(iteration.getString(DataTypes.closePrice).toFloat)

      total_counter += 1
      if (CCIValue >= -100 && CCIValue <= 100)
        cci_counter += 1

      /** AROON **/
      var AROONValue: Int = ResultTypes.neutral
      // up(0) >= 70 && down(1) <= 30, bull
      // up(0) <= 30 &7 down(1) >= 70, bear
      // when 30-70, if up cross above down, bull
      // if down cross above up, bear
      AROONValue = aroon_indicator.computeAROONResult(iteration.getString(DataTypes.closePrice).toFloat)


      println("SMA: " + isSMAUp + "   EMA: " + isEMAUp + "   MACD: " + isMACDUp + "   RSI: " + RSIValue + "   STOCH: " + STOCHValue
        + "   CCI: " + CCIValue + "   AROON: " + AROONValue)


    }
  }
}
