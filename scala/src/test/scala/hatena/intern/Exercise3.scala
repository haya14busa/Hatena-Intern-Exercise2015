package hatena.intern

import hatena.intern.helper._

class Exercise3Spec extends UnitSpec {
  describe("LTSV Counter") {

    val logs = LtsvParser.parse(SpecHelper.pathToDataFile("log.ltsv"))

    it("エラー数が正しくカウントされていること") {
      LogCounter(logs).countError shouldBe 2
    }

    it("ユーザごとにログがグループ化されていること") {
      val groupdLogs = LogCounter(logs).groupByUser
      val franksLogs = groupdLogs.get("frank")

      groupdLogs.get("john").size shouldBe 1
      groupdLogs.get("guest").size shouldBe 1

      franksLogs.size shouldBe 3
      // ただしくグルーピングされているかどうかを検査するテストの続きを書いてみてください
    }
  }

}
