import java.io.File

import ml.dmlc.xgboost4j.scala.{DMatrix, XGBoost}

import scala.collection.mutable
import scala.io.Source

object LambdaMart {

  def trainModel(trainFile: String,
                 validationFile: String,
                 modelName: String) = {
    val trainM = new DMatrix(trainFile + "#train.cache")
    val validateM = new DMatrix(validationFile + "#validation.cache")

    val watches = new mutable.HashMap[String, DMatrix]
    watches += "validation" -> validateM

    val params = new mutable.HashMap[String, Any]()
    params += "eta" -> 0.1
    params += "gamma" -> 1.0
    params += "max_depth" -> 6
    params += "min_child_weight" -> 0.1
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
    val testMax = new DMatrix(
      pathTest + "#" +
        "+test.cache")
    val predicts = booster.predict(testMax)

    booster
      .getFeatureScore(
        Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"))
      .foreach(x => println(s"${x._1} , ${x._2}"))
    println(predicts.length)
    MRR.reset()
    println(predicts(0).length)
    evaluate(pathTest, predicts.map(_(0)))
    println(MRR.result())
  }

  private def evaluate(pathTest: String, ranking: Array[Float]): Unit = {

    var currentI = 0
    var i = 0
    val size = iterator(pathTest).length

    var currentGroup: List[(Float, Int)] = List()
    var group = ""

    for (x <- rankingIterator(ranking).zip(iterator(pathTest))) {
      val rank = x._1
      val prediction = x._2._1
      val groupNew = x._2._2

      i += 1
      if (i % 1000000 == 0) {
        val progInt = (i / size.toFloat) * 100
        val prog = f"$progInt%1.2f"
        println(s"Computing MRR: $prog%")
        println(MRR.result())
      }

      if (groupNew != group) {
        group = groupNew
        computeMRR(
          currentGroup.sortBy(_._1)(Ordering.Float.reverse).take(8).map(_._2))
        currentGroup = (rank, prediction) :: Nil
      } else {
        currentGroup = currentGroup :+ (rank, prediction)
      }

    }

    println(currentI)

  }
  def computeMRR(group: List[Int]): Unit = {
    if (group.filter(_ == 1).size >= 1) {
      val index = group.indexWhere(_ == 1)
      MRR.add(1 / (index + 1).toFloat)
    } else {
      MRR.add(0)
    }
  }

  def rankingIterator(ar: Array[Float]) = ar.iterator

  def iterator(str: String) =
    Source
      .fromFile(str)
      .getLines()
      .map(_.split(" "))
      .map(x => (x(0).toInt, x(1)))

  def groups(path: String) = Source.fromFile(path).getLines().map(_.toInt)

}
