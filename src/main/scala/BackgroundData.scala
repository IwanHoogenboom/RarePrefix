import java.io._
import java.nio.file.{Files, Paths}

import cats.Hash
import com.rklaehn.radixtree.RadixTree

import scala.collection.immutable.HashMap
import scala.io.Source

class BackgroundData() {

  private val background_data = "background.txt"
  private val background_count_map = "background_count.txt"
  private val background_ngram_map = "background_ngram_map.txt"
  lazy val countHashMap: Map[String, Int] = this.getCountMap().toMap
  lazy val ngramCountHashMap: Map[String, Int] = this.getNgramsMap().toMap

  def getCountMap(): Iterator[(String, Int)] = {
    if (!new File(background_count_map).exists()) {
      createAndStoreCountMap()
    } else {
      Source.fromFile(background_count_map).getLines().map { x =>
        val t = x.split(",")
        (t(0), t(1).toInt)
      }
    }
  }

  def getNgramsMap(): Iterator[(String, Int)] = {
    if (!new File(background_ngram_map).exists()) {
      createAndStoreNGramMap()
    } else {
      Source.fromFile(background_ngram_map).getLines().map { x =>
        val t = x.split(",")
        (t(0), t(1).toInt)
      }
    }
  }

  def getRadixTree(): RadixTree[String, Int] = {
    val pairs = this.getCountMap().toArray.map(x => x._1 -> x._2)

    RadixTree(pairs: _*)
  }

  def getNGramsRadixTree10k(): RadixTree[String, Int] = {
    val ngrams10k =
      this.getNgramsMap().take(10000).toArray.map(x => x._1 -> x._2)
    RadixTree(ngrams10k: _*)
  }

  def getNGramsRadixTree100k(): RadixTree[String, Int] = {
    val ngrams10k =
      this.getNgramsMap().take(100000).toArray.map(x => x._1 -> x._2)
    RadixTree(ngrams10k: _*)
  }

  private def createAndStoreCountMap(): Iterator[(String, Int)] = {
    var i = 0
    val countMap = scala.collection.mutable.HashMap
      .empty[String, Int] withDefaultValue 0
    for (line <- queries) {
      i += 1
      if (i % 100000 == 0) {
        println(s"${(i / queries.size.toFloat) * 100}%")
      }

      countMap(line) += 1
    }

    val file = new File(background_count_map)
    file.createNewFile()

    val writer = new FileWriter(file)
    for ((key, value) <- countMap) {
      writer.append(s"$key,$value\n")
    }
    writer.flush()
    writer.close()

    return countMap.toIterator
  }

  private def createAndStoreNGramMap(): Iterator[(String, Int)] = {
    var i = 0
    val countMap = scala.collection.mutable.HashMap
      .empty[String, Int] withDefaultValue 0
    for (line <- queries) {
      i += 1
      if (i % 100000 == 0) {
        println(s"${(i / queries.size.toFloat) * 100}%")
      }
      val ngrams = getNGrams(line)

      ngrams.foreach {
        countMap(_) += 1
      }
    }

    val sortedCountMap = countMap.toList
    countMap.clear()

    val file = new File(background_ngram_map)
    file.createNewFile()

    val writer = new FileWriter(file)
    for ((key, value) <- sortedCountMap.sortWith((x, y) => y._2 < x._2)) {
      writer.append(s"$key,$value\n")
    }
    writer.flush()
    writer.close()

    return countMap.toIterator
  }

  private def getEndGrams(query: String): List[String] = {
    val split = query.split(" ")
    val endGrams = for (i <- split.length to 1 by -1)
      yield split.takeRight(i).mkString(" ")

    endGrams.toList
  }

  def getNGrams(query: String): List[String] = {
    val split = query.split(" ")
    var NgramList = List[String]()
    for ( n <- 1 to 6) {
      split.sliding(n).foreach( p => {
        val str = p.mkString(" ")
        NgramList = str :: NgramList
      })
    }
    NgramList
  }

  def queries = Source.fromFile(background_data).getLines.drop(0)
}
