import scala.io.Source
object Dataset {
  val TEST = "test.txt"
  val BACKGROUND = "background.txt"
  val VALIDATION = "validation.txt"
  val TRAINING = "train.txt"
}
class Dataset(val filename: String) {
  def queries = Source.fromFile(filename).getLines
}
