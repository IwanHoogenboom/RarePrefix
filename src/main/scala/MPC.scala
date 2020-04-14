import scala.io.Source

class MPC(filename: String) {
  def computeMPC(): Unit = {
    var i = 0
    val size = candidates.size

    var currentGroup: List[Candidate] = List()
    var group = ""

    for (rawC <- candidates) {
      i += 1
      if (i % 100000 == 0) {
        val progInt = (i / size.toFloat) * 100
        val prog = f"$progInt%1.2f"
        println(s"Computing MRR for MPC: $prog%")
        println(MRR.result())
      }

      val candidate = Feature.parseCandidate(rawC)

      if (candidate.prefix != group) {
        group = candidate.prefix
        computeMRR(currentGroup.take(8))
        currentGroup = candidate :: Nil
      } else {
        currentGroup = candidate :: currentGroup
      }

    }
  }

  def computeMRR(group: List[Candidate]): Unit = {
    if (group.filter(_.relevant == 1).size >= 1) {

      val index = group.indexWhere(_.relevant == 1)
      MRR.add(1 / (index + 1).toFloat)
    } else {
      MRR.add(0)
    }
  }

  def candidates = Source.fromFile(filename).getLines.drop(0)
}
