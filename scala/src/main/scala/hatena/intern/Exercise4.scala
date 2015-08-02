package hatena.intern

import com.github.nscala_time.time.Imports._

case class LogGraph(logs: Iterable[Log]) {

  def groupByUserCount(): List[String] = {
    val userWithCnt = LogCounter(logs).groupByUser.mapValues(_.size).toList.sortBy(_._1).reverse
    val unames = userWithCnt map (_._1)
    val values = userWithCnt map (_._2)
    val barGraph = AsciiGraph.makeBarGraph(values)
    val barLeft = AsciiGraph.makeBarLeft(unames)
    (barLeft zip barGraph) map {
      case (uname, value) => s"$uname $value"
    }
  }

}

object AsciiGraph {

  /**
   * DOCTEST
   * {{{
   * scala> "\n" + AsciiGraph.makeBarGraph(List(14, 6, 4, 3, 2)).mkString("\n")
   * res0: String =
   * "
   * 0        10
   * =============* (14)
   * =====* (6)
   * ===* (4)
   * ==* (3)
   * =* (2)"
   * }}}
   */
  def makeBarGraph(xs: List[Int]): List[String] = {
    val maxValue = xs.max
    makeScaleBar(maxValue) :: (xs map (x => makeBar(x, maxValue)))
  }

  /**
   * DOCTEST
   * {{{
   * scala> "\n" + AsciiGraph.makeBarLeft(List("hi", "hoge", "foo")).mkString("\n")
   * res0: String =
   * "
   * ----:
   *   hi:
   * hoge:
   *  foo:"
   * }}}
   */
  def makeBarLeft(keys: List[String]): List[String] = {
    val maxKname: Int = (keys map (_.length)).max
    ("-" * maxKname + ":") :: (keys map (kname => (" " * (maxKname - kname.length)) + s"$kname:"))
  }

  /**
   * DOCTEST
   * {{{
   * scala> AsciiGraph.makeBar(2, 10)
   * res0: String = =* (2)
   * scala> AsciiGraph.makeBar(800, 1000)
   * res0: String = =======* (800)
   * }}}
   */
  def makeBar(cnt: Int, maxCount: Int): String =
    "=" * (cnt.toDouble / calcScale(maxCount) - 1).toInt + s"* ($cnt)"

  /**
   * DOCTEST
   * {{{
   * scala> AsciiGraph.makeScaleBar(14)
   * res0: String = 0        10
   * scala> AsciiGraph.makeScaleBar(1001)
   * res0: String = 0       1000
   * }}}
   */
  def makeScaleBar(maxCount: Int): String = {
    val toCnt: Int = (maxCount.toString.charAt(0).toString.toInt * base(maxCount))
    (0 to toCnt by (calcScale(maxCount) * 10)).zipWithIndex.foldLeft("") {
      case (scaleBar, (scale, i)) =>
        scaleBar + (" " * (10 * i - scaleBar.length - (scale.toString.length / 2))) + scale
    }
  }

  // calculate scale of `=`
  private def calcScale(maxCount: Int): Int = ((base(maxCount) / 10)).toInt

  private def base(x: Int): Int = math.pow(10, x.toString.length - 1).toInt
}
