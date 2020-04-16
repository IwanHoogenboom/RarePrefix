import Feature.background
import com.rklaehn.radixtree._
import cats.implicits._

import scala.io.Source
object Main {

  def main(args: Array[String]): Unit = {
    // Generate all candidates. Note: this takes quite a while.
//    genAllCandidates(Dataset.TEST)
//    genAllCandidates(Dataset.VALIDATION)
//    genAllCandidates(Dataset.TRAINING)

    // Compute MPC for the test candidates WITHOUT synthetic candidates. We use this as baseline.
//    val mpc = new MPC("test_candidates_0.txt")
//    mpc.computeMPC()

    // This probably takes a bit of time.
//    genAllFeatures(Dataset.TEST)
//    genAllFeatures(Dataset.TRAINING)
//    genAllFeatures(Dataset.VALIDATION)
//
//    val lambda = LambdaMart.trainModel("train_candidates_2_features.txt",
//                                       "validation_candidates_2_features.txt",
//                                       "no_ngrams_2_synthetic_correct")

//    LambdaMart.evaluateModel("./model/no_ngrams_2_synthetic_correct",
//                             "test_candidates_0_features.txt")
    val bgd = new BackgroundData();
    bgd.getNgramsMap()

  }

  def genAllCandidates(file: String): Unit = {
    val candidateGenerator =
      new CandidateGenerator(new Dataset(file), new BackgroundData())
    candidateGenerator.generateCandidates(Synthetic.NO_SYNTHETIC)
    //   candidateGenerator.generateCandidates(Synthetic.SYNTHETIC_10K)
    //candidateGenerator.generateCandidates(Synthetic.SYNTHETIC_100K)
  }

  def genAllFeatures(file: String): Unit = {
    val featureGenerator = new FeatureGenerator(
      s"${file.replace(".txt", "")}_candidates_2.txt")
    featureGenerator.generateAndWriteFeatures()
    featureGenerator.generateGroups()

//    val featureGenerator2 = new FeatureGenerator(s"${file.replace(".txt", "")}_candidates_1.txt")
//    featureGenerator2.generateAndWriteFeatures()
//
//    val featureGenerator3 = new FeatureGenerator(s"${file.replace(".txt", "")}_candidates_2.txt")
//    featureGenerator3.generateAndWriteFeatures()
  }

}
