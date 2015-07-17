package hatena.intern

import scalax.file.Path

object LtsvParser {
  def parse(filePath: String): Iterable[Log] = ???

  def parseLine: String => Log = (mapToLog _) compose (parseLineToMap _)

  private def parseLineToMap(ltsvLine: String): Map[String, String] =
    ltsvLine.split("\t").map(x => splitFirst(x, ":")).foldLeft(Map[String, String]()) {
      case (m, (field, value)) => m + (field -> value)
    }

  // TODO: error handling
  private def mapToLog(logMap: Map[String, String]): Log = {
    Log(
      host = logMap.get("host").get,
      user = logMap.get("user") flatMap hyphenToOption,
      epoch = logMap.get("epoch").get.toInt,
      req = logMap.get("req").get,
      status = logMap.get("status").get.toInt,
      size = logMap.get("size").get.toInt,
      referer = logMap.get("referer") flatMap hyphenToOption
    )
  }

  private def hyphenToOption(value: String): Option[String] =
    if (value == "-") None else Some(value)

  /** @return (left, right) right will be empty string if there are no splitter */
  private def splitFirst(string: String, splitter: String): (String, String) = {
    val xs = string.split(splitter)
    (xs.head, xs.tail.mkString(""))
  }

}
