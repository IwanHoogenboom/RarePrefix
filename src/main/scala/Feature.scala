import java.io.BufferedWriter

case class FeatureVec(freq: Int,
                      prefix: String,
                      lenPrefix: Int,
                      lenWordsPrefix: Int,
                      lenSuffix: Int,
                      lenWordsSuffix: Int,
                      lenFull: Int,
                      lenWordsFull: Int,
                      endsWithSpace: Int,
                      relevant: Int)

case class Candidate(prefix: String,
                     suffix: String,
                     full: String,
                     relevant: Int)

object Feature {

  private lazy val background = new BackgroundData()
  private var currentGroup = ""
  private var currentGroupId = 0
  private var currentGroupSize = 0
  var groupWriter: BufferedWriter = null

  def computeFeatureVec(candidate: Candidate): FeatureVec = {
    val freq =
      if (background.countHashMap.contains(candidate.full))
        background.countHashMap(candidate.full)
      else 0

    val prefixLen = candidate.prefix.length
    val lenWordsPrefix = candidate.prefix.split(" ").length

    val suffixLen = candidate.suffix.length
    val lenWordsSuffix = candidate.suffix.split(" ").length

    val fullLen = candidate.full.length
    val lenWordsFull = candidate.full.split(" ").length

    val endsWithSpace = if (candidate.prefix.endsWith(" ")) 1 else 0

    FeatureVec(freq,
               candidate.prefix,
               prefixLen,
               lenWordsPrefix,
               suffixLen,
               lenWordsSuffix,
               fullLen,
               lenWordsFull,
               endsWithSpace,
               candidate.relevant)
  }

  def writeFeature(feature: FeatureVec): String = {
    if (currentGroup != feature.prefix) {
      if (currentGroupSize != 0) {
        groupWriter.write(s"$currentGroupSize\n")
      }
      currentGroup = feature.prefix
      currentGroupId += 1
      currentGroupSize = 0
    }
    val builder = new StringBuilder()
    builder.append(feature.relevant + " ")
    builder.append(s"qid:${currentGroupId} ")
    builder.append(s"1:${feature.freq} ")
    builder.append(s"2:${feature.lenPrefix} ")
    builder.append(s"3:${feature.lenWordsPrefix} ")
    builder.append(s"4:${feature.lenSuffix} ")
    builder.append(s"5:${feature.lenWordsSuffix} ")
    builder.append(s"6:${feature.lenFull} ")
    builder.append(s"7:${feature.lenWordsFull} ")
    builder.append(s"8:${feature.endsWithSpace}")
    builder.append("\n")
    builder.toString()
  }

  def parseCandidate(candidate: String): Candidate = {
    val cSplit = candidate.split("\t")
    val prefix = cSplit(1)
    val full = cSplit(2)
    val suffix = full.replace(prefix, "")
    val relevant = cSplit(3).toInt

    Candidate(prefix, suffix, full, relevant)
  }

  def candidate2FeatureVec(candidate: String) =
    writeFeature(computeFeatureVec(parseCandidate(candidate)))
}
