package hatena.intern

import hatena.intern.helper._

import org.scalatest.DiagrammedAssertions

class Exercise3Spec extends UnitSpec with DiagrammedAssertions {
  describe("LTSV Counter") {

    val logs = LtsvParser.parse(SpecHelper.pathToDataFile("log.ltsv"))

    it("エラー数が正しくカウントされていること") {
      LogCounter(logs).countError shouldBe 2
    }

    it("ユーザごとにログがグループ化されていること") {
      val groupdLogs = LogCounter(logs).groupByUser
      val franksLogs = groupdLogs.get("frank").get

      groupdLogs.get("john").size shouldBe 1
      groupdLogs.get("guest").size shouldBe 1 // <- これOptionなので絶対に1になってる

      franksLogs.size shouldBe 3
      // ただしくグルーピングされているかどうかを検査するテストの続きを書いてみてください

      // there are 3 users in log
      assert(groupdLogs.size == 3)

      // there are 5 lines in log file
      assert(groupdLogs.values.flatten.size == 5)

      // all username of groupdLogs (guest if None) should be equal to grouped username key
      groupdLogs foreach {
        case (username, logs) =>
          logs foreach (log => assert((log.user getOrElse ("guest")) == username))
      }
    }
  }

}
