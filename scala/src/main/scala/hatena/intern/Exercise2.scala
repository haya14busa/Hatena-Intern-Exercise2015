package hatena.intern

import scalax.file.Path

class LtsvParserException(message: String) extends RuntimeException(message)

object LtsvParser {

  /** parse a LTSV file
   * 1. Should I pass File class instead of string path? All this method does
   *    should be parsing LTSV file, I guess...
   * 2. Ideally, signature should be more flexible.
   *    def parse[A](filePath: String): Iterable[A]
   *    def parse[A](filePath: String)(implicit converter: Map[String, String] => A): Iterable[A]
   *                                                           ~~~~~~  ~~~~~~
   *                                                           key     value
   */
  def parse(filePath: String): Iterable[Log] =
    Path.fromString(filePath).lines() map parseLine toIterable

  /**
   * DOCTEST
   * {{{
   * scala> LtsvParser.parseLine(
   *      |   List(
   *      |     "host:127.0.0.1",
   *      |     "user:frank",
   *      |     "epoch:1372694390",
   *      |     "req:GET /apache_pb.gif HTTP/1.0",
   *      |     "status:200",
   *      |     "size:2326",
   *      |     "referer:http://www.hatena.ne.jp/"
   *      |   ).mkString("\t")
   *      | )
   * res0: hatena.intern.Log = Log(127.0.0.1,Some(frank),1372694390,GET /apache_pb.gif HTTP/1.0,200,2326,Some(http//www.hatena.ne.jp/))
   * }}}
   */
  def parseLine: String => Log = (mapToLog _) compose (parseLineToMap _)

  //                                                key   : value
  //                                                ______  ______
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
