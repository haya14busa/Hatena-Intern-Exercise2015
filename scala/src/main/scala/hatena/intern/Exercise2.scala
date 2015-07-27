package hatena.intern

import scala.util.control.Exception._

import scalax.file.Path
import scalax.file.Path.AccessModes

import scalaz._
import Scalaz._

class LtsvParserException(message: String) extends RuntimeException(message)
class LtsvFileNotFoundException(message: String) extends LtsvParserException(message)

object LtsvParser {
  type ValidOrLtsvParserException[X] = Validation[NonEmptyList[LtsvParserException], X]

  /**
   * parse a LTSV file. Return Exception or parsed Logs
   * 1. Should I pass File class instead of string path? All this method does
   *    should be parsing LTSV file, I guess...
   * 2. Ideally, signature should be more flexible.
   *    def parse[A](filePath: String): Iterable[A]
   *    def parse[A](filePath: String)(implicit converter: Map[String, String] => A): Iterable[A]
   *                                                           ~~~~~~  ~~~~~~
   *                                                           key     value
   */
  def parseOrError(filePath: String): ValidOrLtsvParserException[Iterable[Log]] = {
    val path = Path.fromString(filePath)
    if (path.checkAccess(AccessModes.Read))
      parseReadableFile(path).toList.sequenceU
    else
      (new LtsvFileNotFoundException(s"${filePath} not found")).failureNel
  }

  /** parse LTSV file. It may throws Exception */
  def parse(filePath: String): Iterable[Log] = {
    val path = Path.fromString(filePath)
    if (path.checkAccess(AccessModes.Read))
      parseOrError(filePath) match {
        case Success(a) => a
        case Failure(es) =>
          throw new LtsvParserException(es.map(_.getMessage()).list.mkString("\n"))
      }
    else
      throw new LtsvFileNotFoundException(s"${filePath} not found")
  }

  private def parseReadableFile(readableFile: Path): Iterable[ValidOrLtsvParserException[Log]] =
    readableFile.lines().zipWithIndex map {
      case (line, index) =>
        parseLine(line, Some(index))
    } toIterable

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
   * res0: hatena.intern.LtsvParser.ValidOrLtsvParserException[hatena.intern.Log] = Log(127.0.0.1,Some(frank),1372694390,GET /apache_pb.gif HTTP/1.0,200,2326,Some(http//www.hatena.ne.jp/))
   * }}}
   */
  def parseLine(line: String, lineNumber: Option[Int] = None): ValidOrLtsvParserException[Log] =
    ((mapToLog _) compose (parseLineToMap _))(line) leftMap (validations =>
      validations map (msg => new LtsvParserException(
        lineNumber.fold(msg)(lnum => s"at line $lnum: $msg")
      ))
    )

  //                                                key   : value
  //                                                ______  ______
  private def parseLineToMap(ltsvLine: String): Map[String, String] =
    ltsvLine.split("\t").map(x => splitFirst(x, ":")).foldLeft(Map[String, String]()) {
      case (m, (field, value)) => m + (field -> value)
    }

  private def mapToLog(logMap: Map[String, String]): Validation[NonEmptyList[String], Log] = {
    val host = getOrError(logMap, "host")
    val user = getOrError(logMap, "user") map hyphenToNone
    val epoch = getIntOrError(logMap, "epoch")
    val req = getOrError(logMap, "req")
    val status = getIntOrError(logMap, "status")
    val size = getIntOrError(logMap, "size")
    val referer = getOrError(logMap, "referer") map hyphenToNone

    val logE = catching(classOf[LogInitializeException]) either (host |@| user |@| epoch |@| req |@| status |@| size |@| referer)(Log)
    if (logE.isRight) logE.right.get
    else (logE.left.get.getMessage()).failureNel

  }

  private def getOrError(logMap: Map[String, String], key: String): Validation[NonEmptyList[String], String] = {
    val r = logMap.get(key)
    if (r.isEmpty) (s"`$key` field not found").failureNel
    else (r.get).successNel
  }

  private def getIntOrError(logMap: Map[String, String], key: String): Validation[NonEmptyList[String], Int] = {
    val r = logMap.get(key)
    if (r.isEmpty) (s"`$key` field not found").failureNel
    else
      allCatch opt (r.get.toInt).successNel getOrElse (s"`$key` is not number").failureNel
  }

  private def hyphenToNone(value: String): Option[String] =
    if (value == "-") None else Some(value)

  /** @return (left, right) right will be empty string if there are no splitter */
  private def splitFirst(string: String, splitter: String): (String, String) = {
    val xs = string.split(splitter)
    (xs.head, xs.tail.mkString(""))
  }

}
