/* SimpleApp.scala */

import indicators._

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




    // Set spark Session
    val spark = SparkSession.builder().appName("Predictor").getOrCreate()


    // Initialize data loader
    val goldLoader: DataLoader = new DataLoader()

    // Initialize indicators
    val gold_indicators = new indicator(goldLoader.getDF("XAUUSD_Candlestick_1_D_BID_01.01.2017-31.12.2017.csv"))

    gold_indicators.compute

    // Initialize learner


    val gold_learner = new Learner()

    gold_learner.Initialize(gold_indicators.getDF())

    gold_learner.train()

    
    spark.stop()
  }
}

