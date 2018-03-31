/* SimpleApp.scala */

import indicators._
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

class SimpleApp {

  /** Setup Spark environment */
  // Set up Spark running environment
  val conf = new SparkConf().setAppName("Predictor").setMaster("local[4]")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")

  val spark = SparkSession.builder().appName("Predictor").getOrCreate()

  import spark.implicits._

  var goldModels :List[Double] = List()
  var oilModels :List[Double] = List()
  var currencyModels :List[Double] = List()

  def init(instrument:String) {

    /** Initialize DataLoader */
    val Loader: DataLoader = new DataLoader()

    /** Initilize DataParser */
    val Parser: DataParser = new DataParser()

    /** Initilize DataEvaluater */
    val Evaluater: DataEvaluater = new DataEvaluater()


    val time_periods = Array(1, 3, 7, 15, 30, 60, 90)


    /** Load Indicators */
    val gold_indicators = new indicator(Loader.getDF( instrument + ".csv")).compute()

    for (time_period <- time_periods) {

      println("Predicting " + instrument + " price with time period " + time_period + " days")

      /** Parse Indicators */
      val gold_DF = Parser.parse(gold_indicators, time_period)

      // split train and valid
      val gold_train_DF = gold_DF.filter($"time" < "2017-01-01 00:00:00")

      val gold_valid_DF = gold_DF.filter($"time" >= "2017-01-01 00:00:00")


      /** Initialize  learner */
      val gold_learner = new Learner()

      /** train with linear regression */

      val mlLin_result = gold_learner.predict_LinReg(gold_train_DF, gold_valid_DF)

      Evaluater.evaluatePrice(mlLin_result)

      Evaluater.evaluateDirection(mlLin_result)

      val prediction:Double = mlLin_result.orderBy($"time".desc).first().getDouble(5)


     if(instrument == "gold")
      goldModels = goldModels :+ prediction
     else if (instrument == "oil")
       oilModels = oilModels :+ prediction
     else
       currencyModels = currencyModels :+ prediction

    }
  }


  def predict_Gold(data:Array[Double], period: Int): Double ={
    goldModels(period)
  }





  def stop() {
    spark.stop()
  }
}

