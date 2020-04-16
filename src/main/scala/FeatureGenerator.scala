import java.io.{BufferedWriter, File, FileWriter}

import scala.io.Source

class FeatureGenerator(filename: String) {

  private val writeFilename = filename.replace(".txt", "_features.txt")
  private val writeGroupname = filename.replace(".txt", "_features.txt.group")

  def generateAndWriteFeatures(): Unit = {
    val featureFile = new File(writeFilename)

    println("Now generating features.")
    if (!featureFile.exists()) {
      featureFile.createNewFile()
    } else {
      println(s"Features already exist for ${featureFile.getName}.")
      return
    }

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

  def generateGroups(): Unit = {
    val groupFile = new File(writeGroupname)

    if (!groupFile.exists()) {
      groupFile.createNewFile()
    } else {
      println(s"Groups already exist for ${groupFile.getName}.")
      return
    }

    val groupWriter: FileWriter = new FileWriter(writeGroupname)

    var i = 0
    var size = features.size

    var currentGroup = ""
    var firstEntry = true
    var firstPrint = true
    var currentGroupSize = 0
    for (feat <- features) {
      i += 1
      if (i % 100000 == 0) {
        val progInt = (i / size.toFloat) * 100
        val prog = f"$progInt%1.2f"
        println(s"Generating feature groups: $prog%")
      }

      val group = feat.split(" ")(1)

      if (currentGroup != group && firstEntry) {
        currentGroup = group
        currentGroupSize = 0
        firstEntry = false
      }

      if (currentGroup != group) {
        if (firstPrint) {
          groupWriter.write(currentGroupSize.toString)
          firstPrint = false
        } else {
          groupWriter.write("\n")
          groupWriter.write(currentGroupSize.toString)
        }
        currentGroup = group
        currentGroupSize = 0
      }

      currentGroupSize += 1
    }

    groupWriter.write("\n")
    groupWriter.write(currentGroupSize.toString)

    groupWriter.flush()
    groupWriter.close()

  }

  def candidates = Source.fromFile(filename).getLines
  def features = Source.fromFile(writeFilename).getLines

}
