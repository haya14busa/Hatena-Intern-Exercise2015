package hatena.intern

import hatena.intern.helper._

class Exercise2Spec extends UnitSpec {

  describe("LTSV Parser") {

    describe(".parseLine") {
      it("should parse one line of valid LTSV") {
        val line = "host:127.0.0.1\tuser:frank\tepoch:1372694390\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200\tsize:2326\treferer:http://www.hatena.ne.jp/"
        val expect = Log("127.0.0.1", Some("frank"), 1372694390, "GET /apache_pb.gif HTTP/1.0", 200, 2326, Some("http//www.hatena.ne.jp/"))
        LtsvParser.parseLine(line) shouldBe expect
      }

      it("should parse one line of valid LTSV with empty user and referer") {
        val line = "host:127.0.0.1\tuser:-\tepoch:1372694390\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200\tsize:2326\treferer:-"
        val expect = Log("127.0.0.1", None, 1372694390, "GET /apache_pb.gif HTTP/1.0", 200, 2326, None)
        LtsvParser.parseLine(line) shouldBe expect
      }

      it("should parse a line with random field order") {
        val line = "user:-\treferer:-\tepoch:1372694390\thost:127.0.0.1\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200\tsize:2326"
        val expect = Log("127.0.0.1", None, 1372694390, "GET /apache_pb.gif HTTP/1.0", 200, 2326, None)
        LtsvParser.parseLine(line) shouldBe expect
      }

      it("should raise appropriate error with invalid LTSV") {
        val line = "user:-\treferer:-\tepoch:1372694390\thost:127.0.0.1\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200\t"
        the[LtsvParserException] thrownBy LtsvParser.parseLine(line) should have message "`size` field not found"
      }

    }

    it("LTSVファイルが正しくパースされていること") {
      val filePath = (scalax.file.Path(".").toAbsolute.parent.flatMap(_.parent).get / "sample_data" / "log.ltsv").toURI.getRawPath
      val logs = LtsvParser.parse(filePath)
      logs.size shouldBe 5

      // 以降ファイルが正しくLogクラスにパースされているテストを書いてみてください
      logs foreach (_.host shouldBe "127.0.0.1")

      // LTSV のパースのテストは `LtsvParser.parseLine` で行い，ここでは正しい
      // LTSVファイルへのパスを指定したらパースできる程度のテストに留める
    }

    it("LTSVファイルが正しくパースできない形式の場合") {
      // エラーハンドリングの設計を考えながら、テストを書いてみてください
      // => `.parseLine` のテストで行う...と思ったけどエラーの行番号ほしいな
    }

    it("LTSVファイルが存在しない場合") {
      // エラーハンドリングの設計を考えながら、テストを書いてみてください
    }
  }

}
