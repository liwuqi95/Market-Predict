package indicators

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.column
import org.apache.spark.sql.expressions.Window
import org.apache.spark.ml.feature.VectorAssembler

object DataTypes extends Enumeration {
  val openPrice = 1
  val highPrice = 2
  val lowPrice = 3
  val closePrice = 4
}

object ResultTypes extends Enumeration {
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

  var DF: DataFrame = dataframe

  /** SMA **/
  //https://www.investopedia.com/articles/technical/052201.asp
  val sma_indicator = new sma(5)

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

    val t1 = System.currentTimeMillis

    val spark = SparkSession.builder().appName("Predictor").getOrCreate()

    import spark.implicits._

    val win = Window.orderBy("time")

    val UDF_sma = udf(sma_indicator.computeSMAResult)

    val UDF_ema = udf(ema_indicator.computeEMAResult)

    val UDF_macd = udf(macd_indicator.computeMACDResult)

    val UDF_rsi = udf(rsi_indicator.computeRSIResult)

    val UDF_stoch = udf(stoch_indicator.computeSTOCHResult)

    val UDF_stochrsi = udf(stochrsi_indicator.computeSTOCHRSIResult)

    val UDF_cci = udf(cci_indicator.computeCCIResult)

    val UDF_aroon = udf(aroon_indicator.computeAROONResult)


    DF = DF
      .withColumn("price", $"Close".cast("float"))
      .withColumn("SMA", UDF_sma($"price")).cache()
      .withColumn("EMA", UDF_ema($"price")).cache()
      .withColumn("MACD", UDF_macd($"price")).cache()
      .withColumn("RSI", UDF_rsi($"price")).cache()
      .withColumn("STOCH", UDF_stoch($"price")).cache()
      .withColumn("STOCH_RSI", UDF_stochrsi($"RSI")).cache()
      .withColumn("CCI", UDF_cci($"price")).cache()
      .withColumn("AROON", UDF_aroon($"price")).cache()
      .withColumn("label", lead("price", 1, 3).over(win))

      .filter($"SMA" =!= 3)
      .filter($"EMA" =!= 3)
      .filter($"MACD" =!= 3)
      .filter($"RSI" =!= 3)
      .filter($"STOCH" =!= 3)
      .filter($"STOCH_RSI" =!= 3)
      .filter($"CCI" =!= 3)
      .filter($"AROON" =!= 3)
      .filter($"label" =!= 3)

      .drop("High")
      .drop("Open")
      .drop("Low")
      .drop("Volume")
      .drop("time")
      .drop("Close")


    val t2 = System.currentTimeMillis

    println("Computing indicators uses " + (t2 - t1) + " millisecond")

  }

  def getDF(): DataFrame ={
    DF
  }
}
