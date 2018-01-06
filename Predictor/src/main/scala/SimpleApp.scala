/* SimpleApp.scala */

import indicators.ema
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


    val fiveDay = new ema(5)
    val twentyoneDay = new ema(21)


    for (iteration <- goldDF.collect()){
      print(iteration)
      print("5-day ema is " + fiveDay.addData(iteration.getString(2).toFloat) + "   ")
      println("021-day ema is " + twentyoneDay.addData(iteration.getString(2).toFloat))
    }



    def transformRow(row: Row): Row =  Row.fromSeq(row.toSeq ++ Array[Any](-1, 1))

    def transformRows(iter: Iterator[Row]): Iterator[Row] = iter.map(transformRow)


    val newSchema = StructType(goldDF.schema.fields ++ Array(
      StructField("z", IntegerType, false), StructField("v", IntegerType, false)))

    spark.createDataFrame(goldDF.rdd.mapPartitions(transformRows), newSchema).show


    spark.stop()
  }
}

