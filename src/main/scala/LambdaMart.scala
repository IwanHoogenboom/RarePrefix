import java.io.File

import ml.dmlc.xgboost4j.scala.{DMatrix, XGBoost}

import scala.collection.mutable
import scala.io.Source

object LambdaMart {

  def trainModel(trainFile: String,
                 validationFile: String,
                 modelName: String) = {
    val trainM = new DMatrix(trainFile)
    val validateM = new DMatrix(validationFile)

    val watches = new mutable.HashMap[String, DMatrix]
    watches += "validation" -> validateM

    val params = new mutable.HashMap[String, Any]()
    params += "eta" -> 0.1
    params += "gamma" -> 1.0
    params += "min_child_weight" -> 0.1
    params += "max_depth" -> 6
    params += "n_estimators" -> 300
    params += "objective" -> "rank:pairwise"

    val round = 4
    // train a model
    println("Now training the model!")
    val booster = XGBoost.train(trainM, params.toMap, round, watches.toMap)

    // predict
    // save model to model path
    val file = new File("./model")
    if (!file.exists()) {
      file.mkdirs()
    }

    booster.saveModel(file.getAbsolutePath + s"/$modelName")
  }

  def evaluateModel(pathModel: String, pathTest: String) = {
    // reload model and data
    val booster = XGBoost.loadModel(pathModel)
    val testMax = new DMatrix(pathTest)
    val predicts = booster.predict(testMax)

    predicts.foreach(x => println(x.length))
  }

}
