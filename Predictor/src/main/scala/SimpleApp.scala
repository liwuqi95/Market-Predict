/* SimpleApp.scala */

import indicators._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object SimpleApp {


  def main(args: Array[String]) {


    // Set up Spark running environment
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
 


    import spark.implicits._


    // Open Gold Json file and load data
    val goldJson = spark.read.json("gold.json")



    val goldData = goldJson.select($"dataset.data")

    val goldDF = goldData.withColumn("data", explode($"data"))
      .withColumn("date", $"data" (0))
      .withColumn("open", $"data" (1))
      .withColumn("close", $"data" (2))
      .drop("data")

    goldDF.show

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
    var cci_counter: Float = 0
    var total_counter: Float = 0

    /** AROON **/
    val aroon_indicator = new aroon(25)

    for (iteration <- goldDF.orderBy(asc("date")).collect()){
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
      // in between, we'll not consider, consider indecision
      if (iteration.getString(2) != null)
        RSIValue = rsi_indicator.computeRSIResult(iteration.getString(2).toFloat)

      /** STOCH **/
      var STOCHValue: Float = 50
      // this value is considered overbought when above 80, oversold when below 20
      if (iteration.getString(2) != null)
        STOCHValue = stoch_indicator.computeSTOCHResult(iteration.getString(2).toFloat)

      /** CCI **/
      var CCIValue: Float = 0
      // surges above 100 means a start of uptrend
      // plunges below -100 means a start of downtrend
      // 0-100 favors bull
      // -100-0 favors bear
      if (iteration.getString(2) != null)
        CCIValue = cci_indicator.computeCCIResult(iteration.getString(2).toFloat)

      total_counter += 1
      if (CCIValue >= -100 && CCIValue <= 100)
        cci_counter += 1

      /** AROON **/
      var AROONValue: (Float, Float) = (50, 50)
      // up(0) >= 70 && down(1) <= 30, bull
      // up(0) <= 30 &7 down(1) >= 70, bear
      if (iteration.getString(2) != null)
        AROONValue = aroon_indicator.computeAROONResult(iteration.getString(2).toFloat)



      println("SMA: " + isSMAUp + "   EMA: " + isEMAUp + "   MACD: " + isMACDUp + "   RSI: " + RSIValue + "   STOCH: " + STOCHValue
        + "   CCI: " + CCIValue + "   AROON: " + AROONValue)
    }

    //println(cci_counter/total_counter)





    def transformRow(row: Row): Row =  Row.fromSeq(row.toSeq ++ Array[Any](-1, 1))

    def transformRows(iter: Iterator[Row]): Iterator[Row] = iter.map(transformRow)


    val newSchema = StructType(goldDF.schema.fields ++ Array(
      StructField("z", IntegerType, false), StructField("v", IntegerType, false)))

    spark.createDataFrame(goldDF.rdd.mapPartitions(transformRows), newSchema).show


    spark.stop()
  }
}

