package hatena.intern

import hatena.intern.helper._

import org.scalatest.DiagrammedAssertions

class Exercise4Spec extends UnitSpec with DiagrammedAssertions {

  describe("LogGraph") {
    val logs = LtsvParser.parse(SpecHelper.pathToDataFile("large_log.ltsv"))

    it(".groupByUserCount") {

      assert(LogGraph(logs).groupByUserCount.mkString("\n") == List(
        "-----: 0        100       200",
        " john: ========* (96)",
        "guest: ========* (96)",
        "frank: ===========================* (288)"
      ).mkString("\n"))
    }
  }

}

