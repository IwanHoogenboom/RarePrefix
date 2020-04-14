import scala.io.Source
class Dataset(val filename: String) {
  def queries = Source.fromFile(filename).getLines.drop(0)
}
