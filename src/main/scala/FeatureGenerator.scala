import java.io.{BufferedWriter, File, FileWriter}

import scala.io.Source

class FeatureGenerator(filename: String) {

  private val writeFilename = filename.replace(".txt", "_features.txt")

  def generateAndWriteFeatures(): Unit = {
    val featureFile = new File(writeFilename)

    if (!featureFile.exists()) featureFile.createNewFile()

    var i = 0
    val size = candidates.size

    val bufferedWriter: BufferedWriter = new BufferedWriter(
      new FileWriter(writeFilename))

    for (rawC <- candidates) {
      i += 1
      if (i % 100000 == 0) {
        val progInt = (i / size.toFloat) * 100
        val prog = f"$progInt%1.2f"
        println(s"Generating feature vectors: $prog%")
      }

      bufferedWriter.write(Feature.candidate2FeatureVec(rawC))
    }

    bufferedWriter.flush()
    bufferedWriter.close()

  }

  def candidates = Source.fromFile(filename).getLines.drop(0)

}
