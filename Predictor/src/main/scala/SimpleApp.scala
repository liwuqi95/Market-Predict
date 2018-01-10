/* SimpleApp.scala */

import indicators._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.classification.LogisticRegression

object SimpleApp {


  def main(args: Array[String]) {


    // Set up Spark running environment
    val conf = new SparkConf().setAppName("Predictor").setMaster("local[4]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val spark = SparkSession.builder().appName("Predictor").getOrCreate()

    import spark.implicits._


    // Open Gold Json file and load data
    val goldJson = spark.read.json("gold.json")
    val goldData = goldJson.select($"dataset.data")
    val goldDF = goldData.withColumn("data", explode($"data"))
      .withColumn("date", $"data" (0))
      .withColumn("price", $"data" (1))
      .drop("data")
  // OPen csv file and load gold data
    val df = spark.read
      .format("csv")
      .option("header", "true") //reading the headers
      .option("mode", "DROPMALFORMED")
      .option("dateFormat", "MM/dd/yyyy ")
      .load("XAUUSD_Candlestick_1_D_BID_01.01.2017-31.12.2017.csv")

   df.show

    val gold_indicators = new indicator(df)

    gold_indicators.compute


    val training = spark.createDataFrame(Seq(
      (1.0, Vectors.dense(0.0, 1.1, 0.1)),
      (0.0, Vectors.dense(2.0, 1.0, -1.0)),
      (0.0, Vectors.dense(2.0, 1.3, 1.0)),
      (1.0, Vectors.dense(0.0, 1.2, -0.5))
    )).toDF("label", "features")

    training.show


    val lr = new LogisticRegression()

    lr.setMaxIter(10)
      .setRegParam(0.01)


    val model = lr.fit(training)


    println("Model was fit using parameters: " + model.parent.extractParamMap)


    spark.stop()
  }
}

