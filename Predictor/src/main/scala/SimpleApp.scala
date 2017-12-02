/* SimpleApp.scala */

import org.apache.spark.SparkConf

import org.apache.spark.SparkContext

import org.apache.spark.sql.SparkSession

import org.apache.spark.sql.functions._

object SimpleApp {


  def main(args: Array[String]) {


    // Set up Spark running environment
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]")
    new SparkContext(conf)
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()


    import spark.implicits._


    // Open Gold Json file and load data
    val goldJson = spark.read.json("gold.json")


    goldJson.printSchema()



    val goldData = goldJson.select($"dataset.data")

    val goldDF = goldData.withColumn("data", explode($"data"))
      .withColumn("date", $"data" (0))
      .withColumn("open", $"data" (1))
      .withColumn("close", $"data" (2))
      .drop("data")



      goldDF.show(false)


    spark.stop()
  }
}

