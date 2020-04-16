import java.io.{BufferedWriter, File, FileWriter}

import com.rklaehn.radixtree.RadixTree

import scala.collection.immutable.HashMap
import scala.collection.mutable

object MRR {
  private var mrr: Float = 0
  private var count: Int = 0

  def reset(): Unit = {
    mrr = 0
    count = 0
  }

  def add(toAdd: Float) = {
    mrr += toAdd
    count += 1
  }

  def result(): Float = mrr / count

  def printResult() = {
    println(mrr)
    println(count)
  }

}

object Synthetic {
  val NO_SYNTHETIC = 0
  val SYNTHETIC_10K = 1
  val SYNTHETIC_100K = 2
}
class CandidateGenerator(dataset: Dataset, backgroundData: BackgroundData) {

  private val candidate_file = dataset.filename
    .replace(".txt", "") + "_candidates.txt"

  private lazy val tree = backgroundData.getRadixTree()
  private lazy val radixTree10k = backgroundData.getNGramsRadixTree10k()
  private lazy val radixTree100k = backgroundData.getNGramsRadixTree100k()
  private val candidateHash: mutable.LinkedHashMap[String, List[String]] =
    mutable.LinkedHashMap.empty
  private val synthethicCandidateHash
    : mutable.LinkedHashMap[String, List[String]] =
    mutable.LinkedHashMap.empty

  def generateCandidates(synthetic: Int = 0): Unit = {
    candidateHash.clear()
    synthethicCandidateHash.clear()

    println(
      s"Now generating candidates for ${dataset.filename}, synthetic: $synthetic: ")
    val candidateFile = new File(
      candidate_file.replace(".txt", s"_${synthetic}.txt"))
    if (!candidateFile.exists()) {
      candidateFile.createNewFile()
    } else {
      println(
        s"Candidates already exists in ${candidateFile.getName}. Exiting.")
      return
    }

    val bufferedWriter: BufferedWriter = new BufferedWriter(
      new FileWriter(candidateFile))

    var i = 0
    val size = dataset.queries.size
    var groupId = 0
    for (query <- dataset.queries) {
      i += 1

      if (i % 10000 == 0) {
        val progInt = (i / size.toFloat) * 100
        val prog = f"$progInt%1.2f"
        if (candidateHash.size >= 4000000) {
          println("Clearing candidate cache.")
          candidateHash.clear()
        }
        if (synthethicCandidateHash.size >= 4000000) {
          println("Clearing synthetic candidate cache.")
          synthethicCandidateHash.clear()
        }
        println(s"Generating candidates: $prog%")
      }

      val querySplit = query.split(" ", 2)
      val firstWord: String = querySplit(0)

      val (fCandidatesH, fCandidatesS) = getCandidates(firstWord, synthetic)
      var candidateList: Set[String] = Set()
      fCandidatesH.distinct.filter(!candidateList.contains(_)).foreach { c =>
        writeCandidate(groupId.toString, query, firstWord, c, bufferedWriter)
      }
      candidateList = candidateList.union(fCandidatesH.toSet)

      fCandidatesS.distinct.filter(!candidateList.contains(_)).foreach { c =>
        writeCandidate(groupId.toString,
                       query,
                       firstWord,
                       c,
                       bufferedWriter,
                       true)
      }

      candidateList = candidateList.union(fCandidatesH.toSet)

      if (fCandidatesH.size == 0 && fCandidatesS.size == 0) {
        writeCandidate(groupId.toString,
                       "no",
                       "candidates",
                       "found",
                       bufferedWriter)
      }
      groupId += 1

      candidateList = candidateList.empty

      if (querySplit.size > 1) {
        var prefix = firstWord + " "
        for (ic <- 0 to (querySplit(1).length - 1)) {
          val can = querySplit(1)(ic)
          prefix += can

          val (candidatesH, candidatesS) =
            getCandidates(prefix, 10, synthetic)

          candidatesH.distinct.filter(!candidateList.contains(_)).foreach { c =>
            writeCandidate(groupId.toString, query, prefix, c, bufferedWriter)
          }

          candidateList = candidateList.union(candidatesH.toSet)

          candidatesS.distinct.filter(!candidateList.contains(_)) foreach { c =>
            writeCandidate(groupId.toString,
                           query,
                           prefix,
                           c,
                           bufferedWriter,
                           true)
          }

          if (candidatesH.size == 0 && candidatesS.size == 0) {
            writeCandidate(groupId.toString,
                           "no",
                           "candidates",
                           "found",
                           bufferedWriter)
          }

          groupId += 1
          candidateList = candidateList.empty
        }
      }
    }

    bufferedWriter.close()
  }

  private def getCandidates(
      prefix: String,
      limit: Int = 10,
      synthetic: Int = 0): (List[String], List[String]) = {
    var candidates: List[String] = List()
    if (candidateHash.contains(prefix)) {
      candidates = candidateHash.get(prefix).get
    } else {
      val ar = tree
        .filterPrefix(prefix)
        .entries
        .toList
        .sortWith((x, y) => y._2 < x._2)
        .map(_._1)
        .take(limit)

      candidateHash(prefix) = ar

      candidates = ar
    }

    synthetic match {
      case 0 => return (candidates, List())
      case 1 => return (candidates, syntheticCandidates(prefix, radixTree10k))
      case 2 => return (candidates, syntheticCandidates(prefix, radixTree100k))
    }

    (candidates, List())
  }

  private def syntheticCandidates(prefix: String,
                                  radixTree: RadixTree[String, Int],
                                  limit: Int = 10): List[String] = {
    val prefixSplit = prefix.split(" ")
    val endTerm =
      if (prefix.endsWith(" ")) prefixSplit.last + " " else prefixSplit.last

    if (synthethicCandidateHash.contains(endTerm)) {
      return synthethicCandidateHash.get(endTerm).get
    }

    val candidates = radixTree
      .filterPrefix(endTerm)
      .entries
      .toList
      .sortWith((x, y) => y._2 < x._2)
      .map(_._1)
      .take(limit)
      .map(prefixSplit.dropRight(1).mkString(" ") + " " + _)

    synthethicCandidateHash(endTerm) = candidates

    candidates
  }

  def writeCandidate(group: String,
                     query: String,
                     prefix: String,
                     candidate: String,
                     writer: BufferedWriter,
                     synthetic: Boolean = false): Unit = {
    if (!synthetic) {
      writer.write("H")
    } else {
      writer.write("S")
    }

    writer.write(",")
    writer.write(group)
    writer.write(",")
    writer.write(prefix)
    writer.write(",")
    writer.write(candidate)
    writer.write(",")

    if (query == candidate) {
      writer.write("1")
    } else {
      writer.write("0")
    }

    writer.write("\n")
  }

}
