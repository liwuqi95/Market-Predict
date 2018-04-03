import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.ml.linalg.{DenseVector, SparseVector, Vectors}
import vegas._
import vegas.render.WindowRenderer._
import vegas.sparkExt._
import org.apache.spark.sql.functions._


class DataEvaluater {

  val spark = SparkSession.builder().appName("Predictor").getOrCreate()

  import spark.implicits._


  def evaluatePrice(DF: DataFrame): Unit = {

    val v_evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("rmse")


    val rmse = v_evaluator.evaluate(DF)
    println("Root Mean Squared Error (RMSE) on test data = " + rmse)

  }

  def evaluateDirection(DF: DataFrame): Unit = {
    val correct_count: Double = DF.filter($"d_label" === $"d_prediction").count()

    val total_count: Double = DF.count()

    println("Mean Directional Accuracy (MDA) on test data = " + correct_count / total_count)
  }


  def evaluateAverageAccuracy(DF: DataFrame): Unit = {


    val data = DF.rdd

    var c = 0

    var t = 0

    val total_count: Double = DF.count()

    for (d <- data) {

      val count = d.getAs[SparseVector](1).toArray.count(_ > 0)

      if (count > 5 && d.getDouble(3) == 1) {
        c += 1
      }

      if (count < 4 && d.getDouble(3) == 0) {
        c += 1
      }

      t = t + 1

      if (t >= total_count)

        println("Root Mean Directional Accuracy (MDA) on test data For average algorithm = " + c / total_count)
    }


  }






  def getResult(current :Double, predict :Double): String ={

    var result = Array("Strong Sell", "Sell", "Neutral", "Buy", "Strong Buy")


    if(predict < current / 1.02)
      return result(0)

    if(predict < current / 1.005)
      return result(1)

    if(predict > current / 0.98)
      return result(4)

    if(predict > current / 0.995)
      return result(3)


    return result(2)

  }


  def plotResult(dataFrame: DataFrame, period: Int): Unit ={

        val df1 = dataFrame.select($"time", $"label".alias("data")).withColumn("type", lit("Actual value"))

        val df2 = dataFrame.select($"time", $"prediction".alias("data")).withColumn("type", lit("Prediction"))

        val data = df1.union(df2)


        Vegas("Sample Multi Series Line Chart", width=400.0, height=300.0)
          .withDataFrame(data)
          .mark(Line)
          .encodeX("time", Temp)
          .encodeY("data", Quantitative)
          .encodeColor(
            field="type",
            dataType=Nominal,
            legend=Legend(orient="left", title="Prices"))

          .encodeDetailFields(Field(field="type", dataType=Nominal))
          .show
  }

}