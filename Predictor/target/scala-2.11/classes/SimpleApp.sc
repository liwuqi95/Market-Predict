/* SimpleApp.scala */

import indicators.ema
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.ml.classification.LogisticRegression

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



    val training = spark.createDataFrame(Seq(
      (1.0, Vectors.dense(0.0, 1.1, 0.1)),
      (0.0, Vectors.dense(2.0, 1.0, -1.0)),
      (0.0, Vectors.dense(2.0, 1.3, 1.0)),
      (1.0, Vectors.dense(0.0, 1.2, -0.5))
    )).toDF("label", "features")


    training.show


    val lr = new LogisticRegression()
    // Print out the parameters, documentation, and any default values.
    //println("LogisticRegression parameters:\n" + lr.explainParams() + "\n")

    lr.setMaxIter(10)
      .setRegParam(0.01)



    val model1 = lr.fit(training)



    println("Model 1 was fit using parameters: " + model1.parent.extractParamMap)

    spark.stop()
  }
}

