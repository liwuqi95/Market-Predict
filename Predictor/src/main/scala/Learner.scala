import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel, MultilayerPerceptronClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.regression.{GeneralizedLinearRegression, LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.regression.{RandomForestRegressionModel, RandomForestRegressor}
import vegas._
import vegas.render.WindowRenderer._
import vegas.sparkExt._

class Learner() extends Serializable {

  val spark = SparkSession.builder().appName("Indicator").getOrCreate()
  import spark.implicits._

  var linRegModel: LinearRegressionModel = null

  var logRegModel: LogisticRegressionModel = null

  var rfRegModel : RandomForestRegressionModel = null


  var logReg = new LogisticRegression()
    .setMaxIter(10)
    .setRegParam(0.3)
    .setElasticNetParam(0.8)

  val linReg = new LinearRegression()
    .setMaxIter(100)
    .setRegParam(0.6)
    .setElasticNetParam(0.8)



  def trainAndPredict(trainData:DataFrame, validData:DataFrame): Unit ={
    linRegModel = linReg.fit(trainData)
    logRegModel = logReg.fit(trainData)
  }


  def predict_LinReg(trainData:DataFrame, validData:DataFrame): DataFrame ={
    linRegModel = linReg.fit(trainData)
    linRegModel.transform(validData).withColumn("d_prediction", when($"prediction" >= $"price", 1.0).otherwise(0.0))
  }

  def predict_LogReg(trainData:DataFrame, validData:DataFrame): DataFrame = {
//    trainData.show()
    val train = trainData.drop("label").withColumn("label", $"d_label").select($"label", $"features")

    train.show(false)

    logRegModel = logReg.fit(train)

    val trainingSummary = logRegModel.summary

    // Obtain the objective per iteration
    val objectiveHistory = trainingSummary.objectiveHistory
    println("objectiveHistory:")
    objectiveHistory.foreach(println)

    logRegModel.transform(validData.drop("label").withColumn("label", $"d_label").select($"label", $"features")).withColumn("d_prediction", $"prediction")
  }

  def predict_rfReg(trainData:DataFrame, validData:DataFrame): DataFrame ={

    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(trainData)



    val rf = new RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")


    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, rf))


    val rfRegModel = pipeline.fit(trainData)

    linRegModel.transform(validData).withColumn("d_prediction", when($"prediction" >= $"price", 1.0).otherwise(0.0))
  }

  def predict_mpCls(trainData:DataFrame, validData:DataFrame): DataFrame ={





    val layers = Array[Int](4, 5)

    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(128)
      .setSeed(1234L)
      .setMaxIter(100)

    val model = trainer.fit(trainData)


println("Ddd")

    trainData.show(false)
    val result = model.transform(validData)
//    val predictionAndLabels = result.drop($"label").withColumn("label", $"d_label").select("prediction", "label")
//    val evaluator = new MulticlassClassificationEvaluator()
//      .setMetricName("accuracy")



    result.drop($"label").withColumn("label", $"d_label").withColumn("d_prediction", $"prediction")

  }











//   predictResult.select($"time",$"features",$"prediction").show()
//
//
//    val win = Window.orderBy("time")
//
//    predictResult = predictResult.withColumn("actual", lead("price", timeScale, 3)
//      .over(win)).filter($"actual" =!= 3).select($"time", $"price", $"prediction",$"actual")
//        predictResult.show
//
//    evaluateAccuracy(predictResult)
//
//    val df1 = predictResult.select($"time", ($"actual" - 1000).alias("data")).withColumn("type", lit("Actual value"))
//
//    val df2 = predictResult.select($"time", ($"prediction" - 1000).alias("data")).withColumn("type", lit("Prediction"))
//
//
//    predictResult = df1.union(df2)
//
//    Vegas("Sample Multi Series Line Chart", width=400.0, height=300.0)
//      .withDataFrame(predictResult)
//      .mark(Line)
//      .encodeX("time", Temp)
//      .encodeY("data", Quantitative)
//
//      .encodeColor(
//        field="type",
//        dataType=Nominal,
//        legend=Legend(orient="left", title="Stock Symbol"))
//
//      .encodeDetailFields(Field(field="type", dataType=Nominal))
//      .show


//
//  def evaluateAccuracy(data:DataFrame): Unit ={
//    val spark = SparkSession.builder().appName("Predictor").getOrCreate()
//
//    import spark.implicits._
//
//    val win = Window.orderBy("time")
//
//    val mda = (a1:Float, a2:Float, f1:Float, f2:Float) =>  {
//      if(a2 == 3 || f2 ==3){
//        3
//      }
//      else if (sign(a1,a2) == sign(f1, f2)){
//        1
//      }
//      else{
//        0
//      }
//    }
//
//    val UDF_mda = udf(mda)
//
//    var data2 =  data.withColumn("accuracy", UDF_mda($"prediction", $"price", $"actual", $"price"))
//       .filter($"accuracy" =!= 3)
//      data2.show()
//       data2.select(avg($"accuracy").alias("Mean of accuracy result")).show()
//  }
//
//  def sign(data1:Float,data2:Float):Int ={
//  if(data1 - data2 > 0)
//    1
//  else if (data1 - data2 < 0)
//    -1
//  else 0
//  }



}
