import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

class DataParser {

  val spark = SparkSession.builder().appName("Predictor").getOrCreate()

  import spark.implicits._

  def parse(DF: DataFrame, timeScale: Int ): DataFrame = {

    val win = Window.orderBy("time")

    transform(DF).withColumn("label", lead("price", timeScale, 3).over(win))
        .withColumn("d_label", when($"label" >= $"price", 1.0).otherwise(0.0))
      .filter($"label" =!= 3)
      .select($"time", $"features", $"label", $"d_label", $"price")

  }


  def transform(data:DataFrame): DataFrame ={



    val assembler = new VectorAssembler().
      setInputCols(Array("SMA", "EMA", "MACD","RSI","STOCH", "STOCH_RSI", "CCI", "AROON","price")).
      setOutputCol("features")

      assembler.transform(data)

  }

}