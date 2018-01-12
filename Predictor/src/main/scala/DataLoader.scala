import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

class DataLoader {

  val spark = SparkSession.builder().appName("Indicator").getOrCreate()

  import spark.implicits._

  def getDataFrames(fileName: String): DataFrame = {
    val df = spark.read
      .format("csv")
      .option("header", "true") //reading the headers
      .option("mode", "DROPMALFORMED")
      .load(fileName)

    val result = df.withColumn("time",to_timestamp($"Local time", "dd.MM.yyyy HH:mm:ss.SSS"))
      .drop($"Local time")
    result
  }

}
