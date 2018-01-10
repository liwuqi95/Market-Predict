package indicators

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._


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
  val invalid = 3
}

// Define candle stick
sealed trait CandleColour

case object Red extends CandleColour

case object Green extends CandleColour

sealed trait CandleTrait {
  def open: Double

  def high: Double

  def low: Double

  def close: Double

  def volume: Long

  def highWick(): Double = Math.abs(high - Math.max(open, close))

  def lowWick(): Double = Math.abs(low - Math.min(open, close))

  def spread(): Double = Math.abs(close - open)

  def spreadRatio(): Double = spread / spreadWithWicks

  def spreadWithWicks(): Double = high - low

  def colour: CandleColour = if (open > close) Red else Green
}
// each candle contains the following data and it also matches the csv file I uploaded
case class Candle(open: Double,
                  high: Double,
                  low: Double,
                  close: Double,
                  volume: Long) extends CandleTrait

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

  /** STOCHRSI **/
  //http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:stochrsi
  val stochrsi_indicator = new stochrsi(14)

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


    val UDF_sma = udf(sma_indicator.computeSMAResult)

    val UDF_ema = udf(ema_indicator.computeEMAResult)

    val UDF_macd = udf(macd_indicator.computeMACDResult)

    val UDF_rsi = udf(rsi_indicator.computeRSIResult)

    val UDF_stoch = udf(stoch_indicator.computeSTOCHResult)

    //val UDF_stochrsi = udf(stochrsi_indicator.computeSTOCHRSIResult)

    val UDF_cci = udf(cci_indicator.computeCCIResult)

    val UDF_aroon = udf(aroon_indicator.computeAROONResult)

     DF.withColumn("SMA", UDF_sma($"Close"))
       .withColumn("EMA", UDF_ema($"Close"))
       .withColumn("MACD", UDF_macd($"Close"))
       .withColumn("RSI", UDF_rsi($"Close"))
       .withColumn("STOCH", UDF_stoch($"Close"))
       .withColumn("STOCH_RSI", UDF_stoch($"RSI"))
       .withColumn("CCI", UDF_cci($"Close"))
       .withColumn("AROON", UDF_aroon($"Close"))
       .filter($"SMA" =!= 3)
       .filter($"EMA" =!= 3)
       .filter($"MACD" =!= 3)
       .filter($"RSI" =!= 3)
       .filter($"STOCH" =!= 3)
       .filter($"STOCH_RSI" =!= 3)
       .filter($"CCI" =!= 3)
       .filter($"AROON" =!= 3)
       .drop("High")
       .drop("Open")
       .drop("Low")
       .drop("Volume")
       .show(100)










    var cci_counter: Float = 0
    var total_counter: Float = 0

    val pattern = "dd-MMM-yy hh.mm.ss.S a"
    //
    val newDF = DF.orderBy(unix_timestamp(DF("Local time"), pattern).cast("timestamp"))

    for (iteration <- DF.filter($"Close" =!= "null" ).collect()){
      print(iteration + "    ")

      /** SMA **/
      var isSMAUp:Int = ResultTypes.invalid
      isSMAUp = sma_indicator.computeSMAResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** EMA **/
      var isEMAUp:Int = ResultTypes.invalid
      isEMAUp = ema_indicator.computeEMAResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** MACD **/
      var isMACDUp:Int = ResultTypes.invalid
      isMACDUp = macd_indicator.computeMACDResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** RSI **/
      var RSIValue:Int = ResultTypes.invalid
      // this value is only being considered when it is bigger than 70/80 and lower than 30/20
      // in between, we'll not consider
      RSIValue = rsi_indicator.computeRSIResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** STOCH **/
      var STOCHValue: Int = ResultTypes.invalid
      // this value is considered overbought when above 80, oversold when below 20
      STOCHValue = stoch_indicator.computeSTOCHResult(iteration.getString(DataTypes.closePrice).toFloat)

      /** STOCHRSI **/
      var STOCHRSIValue:Int = ResultTypes.invalid
      // this value is only being considered when it is bigger than 70/80 and lower than 30/20
      // in between, we'll not consider
      STOCHRSIValue = stochrsi_indicator.computeSTOCHRSIResult(rsi_indicator.getRSIValue())

      /** CCI **/
      var CCIValue: Int = ResultTypes.invalid
      // this value is considered overbought when above 80, oversold when below 20
      CCIValue = cci_indicator.computeCCIResult(iteration.getString(DataTypes.closePrice).toFloat)

      total_counter += 1
      if (CCIValue >= -100 && CCIValue <= 100)
        cci_counter += 1

      /** AROON **/
      var AROONValue: Int = ResultTypes.invalid
      // up(0) >= 70 && down(1) <= 30, bull
      // up(0) <= 30 &7 down(1) >= 70, bear
      // when 30-70, if up cross above down, bull
      // if down cross above up, bear
      AROONValue = aroon_indicator.computeAROONResult(iteration.getString(DataTypes.closePrice).toFloat)



     // println("SMA: " + isSMAUp + "   EMA: " + isEMAUp + "   MACD: " + isMACDUp + "   RSI: " + RSIValue + "   STOCH: " + STOCHValue
      //  + "   STOCHRSI: " + STOCHRSIValue + "   CCI: " + CCIValue + "   AROON: " + AROONValue)


    }
  }
}
