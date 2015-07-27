package hatena.intern

import hatena.intern.helper._
import org.typelevel.scalatest.ValidationMatchers

import org.scalatest.DiagrammedAssertions

import scalaz._

class Exercise2Spec extends UnitSpec with ValidationMatchers with DiagrammedAssertions {

  describe("LTSV Parser") {

    describe(".parseLine") {
      it("should parse one line of valid LTSV") {
        val line = "host:127.0.0.1\tuser:frank\tepoch:1372694390\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200\tsize:2326\treferer:http://www.hatena.ne.jp/"
        val expect = Log("127.0.0.1", Some("frank"), 1372694390, "GET /apache_pb.gif HTTP/1.0", 200, 2326, Some("http//www.hatena.ne.jp/"))
        LtsvParser.parseLine(line) should beSuccess(expect)
      }

      it("should parse one line of valid LTSV with empty user and referer") {
        val line = "host:127.0.0.1\tuser:-\tepoch:1372694390\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200\tsize:2326\treferer:-"
        val expect = Log("127.0.0.1", None, 1372694390, "GET /apache_pb.gif HTTP/1.0", 200, 2326, None)
        LtsvParser.parseLine(line) should beSuccess(expect)
      }

      it("should parse a line with random field order") {
        val line = "user:-\treferer:-\tepoch:1372694390\thost:127.0.0.1\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200\tsize:2326"
        val expect = Log("127.0.0.1", None, 1372694390, "GET /apache_pb.gif HTTP/1.0", 200, 2326, None)
        LtsvParser.parseLine(line) should beSuccess(expect)
      }

      it("should return appropriate error with invalid LTSV") {
        val line = "user:-\treferer:-\tepoch:1372694390\thost:127.0.0.1\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200"
        import scalaz._
        LtsvParser.parseLine(line) match {
          case Failure(es) =>
            assert(es.head.getMessage() == "`size` field not found")
          case _ =>
            fail("it should be faulure")
        }
      }

      it("should return appropriate error with invalid LTSV with line number") {
        val line = "user:-\treferer:-\tepoch:1372694390\thost:127.0.0.1\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200"
        import scalaz._
        LtsvParser.parseLine(line, Some(14)) match {
          case Failure(es) =>
            assert(es.head.getMessage() == "at line 14: `size` field not found")
          case _ =>
            fail("it should be faulure")
        }
      }

      it("should return appropriate error with invalid req parameter") {
        val line = "user:-\treferer:-\tepoch:1372694390\thost:127.0.0.1\treq:/notfound.gif HTTP/1.0\tstatus:200\tsize:2326"
        LtsvParser.parseLine(line) match {
          case Failure(es) =>
            assert(es.map(_.getMessage()).list contains "the `req` parameter is invalid: /notfound.gif HTTP/1.0")
          case _ =>
            fail("it should be faulure")
        }
      }

      it("should return multiple appropriate errors with invalid LTSV") {
        val line = "user:-\tepoch:1372694390\thost:127.0.0.1\treq:GET /apache_pb.gif HTTP/1.0\tstatus:200"
        import scalaz._
        LtsvParser.parseLine(line) match {
          case Failure(es) =>
            assert(es.map(_.getMessage()).list contains "`size` field not found")
            assert(es.map(_.getMessage()).list contains "`referer` field not found")
          case _ =>
            fail("it should be faulure")
        }
      }

    }

    describe(".parse") {

      it("LTSVファイルが正しくパースされていること") {
        val filePath = (scalax.file.Path(".").toAbsolute.parent.flatMap(_.parent).get / "sample_data" / "log.ltsv").toURI.getRawPath
        val logs = LtsvParser.parse(filePath)
        logs.size shouldBe 5

        // 以降ファイルが正しくLogクラスにパースされているテストを書いてみてください
        logs foreach (_.host shouldBe "127.0.0.1")

        // LTSV のパースのテストは `LtsvParser.parseLine` で行い，ここでは正しい
        // LTSVファイルへのパスを指定したらパースできる程度のテストに留める
      }

      it("LTSVファイルが存在しない場合") {
        // エラーハンドリングの設計を考えながら、テストを書いてみてください
        the[LtsvFileNotFoundException] thrownBy LtsvParser.parse("/path/doesn't/exist") should have message "/path/doesn't/exist not found"
      }

      it("LTSVファイルが正しくパースできない形式の場合") {
        // エラーハンドリングの設計を考えながら、テストを書いてみてください
        val filePath = (scalax.file.Path(".").toAbsolute.parent.flatMap(_.parent).get / "sample_data" / "log.error.ltsv").toURI.getRawPath
        the[LtsvParserException] thrownBy LtsvParser.parse(filePath) should have message (
          """at line 0: `user` field not found
            |at line 0: `epoch` is not number
            |at line 2: `size` field not found
            |at line 4: the `req` parameter is invalid: /notfound.gif HTTP/1.0""".stripMargin
        )
      }

    }

    describe(".parseOrError which returns error") {

      it("LTSVファイルが存在しない場合") {
        LtsvParser.parseOrError("/path/doesn't/exist") match {
          case Failure(es) =>
            assert(es.map(_.getMessage()).list contains "/path/doesn't/exist not found")
          case _ =>
            fail("it should be faulure")
        }
      }

      describe("LTSVファイルが正しくパースできない形式の場合") {
        it("It shows error line number") {
          val filePath = (scalax.file.Path(".").toAbsolute.parent.flatMap(_.parent).get / "sample_data" / "log.error.ltsv").toURI.getRawPath
          LtsvParser.parseOrError(filePath) match {
            case Failure(es) =>
              assert(es.map(_.getMessage()).list contains "at line 0: `user` field not found")
              assert(es.map(_.getMessage()).list contains "at line 0: `epoch` is not number")
              assert(es.map(_.getMessage()).list contains "at line 2: `size` field not found")
              assert(es.map(_.getMessage()).list contains "at line 4: the `req` parameter is invalid: /notfound.gif HTTP/1.0")
            case _ =>
              fail("it should be faulure")
          }
        }
      }

    }

  }

}
