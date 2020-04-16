import scala.io.Source
import scala.util.Random

class MPC(filename: String) {
  def computeMPC(): Unit = {
    var i = 0
    val size = candidates.size

    var currentGroup: List[Candidate] = List()
    var group = ""
    var amountOfGroups = 0

    for (rawC <- candidates) {
      i += 1
      if (i % 1000000 == 0) {
        val progInt = (i / size.toFloat) * 100
        val prog = f"$progInt%1.2f"
        println(s"Computing MRR for MPC: $prog%")
        println(MRR.result())
      }

      val candidate = Feature.parseCandidate(rawC)

      if (candidate.group != group) {
        group = candidate.group

        computeMRR(currentGroup.take(8))
        currentGroup = candidate :: Nil
        amountOfGroups += 1
      } else {
        currentGroup = currentGroup :+ candidate
      }

    }

    println(s"${amountOfGroups} <-- amount of groups")
  }

  def computeMRR(group: List[Candidate]): Unit = {
    if (group.filter(_.relevant == 1).size >= 1) {
      val index = group.indexWhere(_.relevant == 1)
      MRR.add(1 / (index + 1).toFloat)
    } else {
      MRR.add(0)
    }
  }

  def candidates = Source.fromFile(filename).getLines
}
