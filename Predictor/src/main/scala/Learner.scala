import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._


class Learner() {

  var DF: DataFrame = null

  var lrModel: LinearRegressionModel = null

  val linear_regression = new LinearRegression()
    .setMaxIter(10)
    .setRegParam(0.3)
    .setElasticNetParam(0.8)


  def Initialize(dataframe: DataFrame):Unit = {

    val assembler = new VectorAssembler().
      setInputCols(Array("SMA", "EMA", "MACD","RSI","STOCH", "STOCH_RSI", "CCI", "AROON","price")).
      setOutputCol("features")

    DF = assembler.transform(dataframe)

    DF = DF.drop("Close").drop("SMA").drop("EMA").drop("MACD")
      .drop("RSI").drop("STOCH").drop("STOCH_RSI").drop("CCI").drop("AROON").drop("price")

   // DF.show
  }


  def train(): Unit ={

    lrModel = linear_regression.fit(DF)

    // Print the coefficients and intercept for linear regression
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

  }

  def analyze(): Unit = {
    val spark = SparkSession.builder().appName("Indicator").getOrCreate()

    import spark.implicits._

    val average = lrModel.transform(DF).withColumn("Error", $"label" - $"prediction").select(avg(abs($"Error"))).show()
   // print(average.toString())
  }



}
