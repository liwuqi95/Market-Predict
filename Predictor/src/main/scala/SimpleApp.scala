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
    new SparkContext(conf)
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





    /** MACD **/
    val macd_predictor = new macd(10, 100)
    for (iteration <- goldDF.orderBy(asc("date")).collect()){
      //print(iteration + "    ")
      var isUp:Boolean = false
      if (iteration.getString(2) != null)
        isUp = macd_predictor.compuateMACDResult(iteration.getString(2).toFloat)
    }



    /** RSI **/
    val rsi_predictor = new rsi(14)

    for (iteration <- goldDF.collect()){
      //print(iteration + "    ")
      var isUp:Float = 50
      // this value is only being considered when it is bigger than 70/80 and lower than 30/20
      // in between, we'll not consider
      if (iteration.getString(2) != null)
        isUp = rsi_predictor.compuateRSIResult(iteration.getString(2).toFloat)
    }





    def transformRow(row: Row): Row =  Row.fromSeq(row.toSeq ++ Array[Any](-1, 1))

    def transformRows(iter: Iterator[Row]): Iterator[Row] = iter.map(transformRow)


    val newSchema = StructType(goldDF.schema.fields ++ Array(
      StructField("z", IntegerType, false), StructField("v", IntegerType, false)))

    spark.createDataFrame(goldDF.rdd.mapPartitions(transformRows), newSchema).show


    spark.stop()
  }
}

