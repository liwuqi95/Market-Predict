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
    val sma_predictor = new sma(50)

    /** EMA **/
    //https://www.investopedia.com/terms/e/ema.asp
    val ema_predictor = new ema(12)

    /** MACD **/
    val macd_predictor = new macd(5, 14)

    /** RSI **/
    val rsi_predictor = new rsi(14)

    for (iteration <- goldDF.orderBy(asc("date")).collect()){
      print(iteration + "    ")


      /** SMA **/
      var isSMAUp:Boolean = false
      if (iteration.getString(2) != null)
        isSMAUp = sma_predictor.compuateSMAResult(iteration.getString(2).toFloat)

      /** EMA **/
      var isEMAUp:Boolean = false
      if (iteration.getString(2) != null)
        isEMAUp = ema_predictor.compuateEMAResult(iteration.getString(2).toFloat)

      /** MACD **/
      var isMACDUp:Boolean = false
      if (iteration.getString(2) != null)
        isMACDUp = macd_predictor.compuateMACDResult(iteration.getString(2).toFloat)

      /** RSI **/
      var RSIValue:Float = 50
      // this value is only being considered when it is bigger than 70/80 and lower than 30/20
      // in between, we'll not consider
      if (iteration.getString(2) != null)
        RSIValue = rsi_predictor.compuateRSIResult(iteration.getString(2).toFloat)


      println("SMA: " + isSMAUp + "   EMA: " + isEMAUp + "   MACD: " + isMACDUp + "   RSI: " + RSIValue)
    }





    def transformRow(row: Row): Row =  Row.fromSeq(row.toSeq ++ Array[Any](-1, 1))

    def transformRows(iter: Iterator[Row]): Iterator[Row] = iter.map(transformRow)


    val newSchema = StructType(goldDF.schema.fields ++ Array(
      StructField("z", IntegerType, false), StructField("v", IntegerType, false)))

    spark.createDataFrame(goldDF.rdd.mapPartitions(transformRows), newSchema).show


    spark.stop()
  }
}

