import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.DataFrame

class Learner() {

  var DF: DataFrame = null


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

    val lrModel = linear_regression.fit(DF)

    // Print the coefficients and intercept for linear regression
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")
  }



}
