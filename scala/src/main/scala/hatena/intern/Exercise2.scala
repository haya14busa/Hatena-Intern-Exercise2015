package hatena.intern

import scalax.file.Path

class LtsvParserException(message: String) extends RuntimeException(message)

object LtsvParser {
  def parse(filePath: String): Iterable[Log] =
    Path.fromString(filePath).lines() map parseLine toIterable

  def parseLine: String => Log = (mapToLog _) compose (parseLineToMap _)

  private def parseLineToMap(ltsvLine: String): Map[String, String] =
    ltsvLine.split("\t").map(x => splitFirst(x, ":")).foldLeft(Map[String, String]()) {
      case (m, (field, value)) => m + (field -> value)
    }

  // TODO: error handling
  // [better]: collecting all errors instead of stopping with first error
  private def mapToLog(logMap: Map[String, String]): Log = {
    Log(
      host = getOrError(logMap, "host"),
      user = hyphenToNone(getOrError(logMap, "user")),
      epoch = getOrError(logMap, "epoch").toInt,
      req = getOrError(logMap, "req"),
      status = getOrError(logMap, "status").toInt,
      size = getOrError(logMap, "size").toInt,
      referer = hyphenToNone(getOrError(logMap, "referer"))
    )
  }

  private def getOrError(logMap: Map[String, String], key: String): String = {
    val r = logMap.get(key)
    if (r.isEmpty) {
      throw new LtsvParserException(s"`$key` field not found")
    }
    r.get
  }

  private def hyphenToNone(value: String): Option[String] =
    if (value == "-") None else Some(value)

  /** @return (left, right) right will be empty string if there are no splitter */
  private def splitFirst(string: String, splitter: String): (String, String) = {
    val xs = string.split(splitter)
    (xs.head, xs.tail.mkString(""))
  }

}
