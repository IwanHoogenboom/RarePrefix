import com.rklaehn.radixtree._
import cats.implicits._
object Main {

  def main(args: Array[String]): Unit = {
//    val candidateGenerator =
//      new CandidateGenerator(new Dataset("validation.txt"),
//                             new BackgroundData())
//    candidateGenerator.generateCandidates(Synthetic.NO_SYNTHETIC)
//
//    val mpc = new MPC("validation_candidates_0.txt")
//    mpc.computeMPC()
//
//    val featureGenerator = new FeatureGenerator("validation_candidates_0.txt")
//    featureGenerator.generateAndWriteFeatures()
//    val lambda = LambdaMart.trainModel("train_candidates_0_features.txt",
//                                       "validation_candidates_0_features.txt",
//                                       "model.txt")

    LambdaMart.evaluateModel("./model/xgb.model",
                             "test_candidates_0_features.txt")

  }

}
