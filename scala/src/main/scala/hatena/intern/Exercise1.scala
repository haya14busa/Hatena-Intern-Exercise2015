package hatena.intern

import com.github.nscala_time.time.Imports._

class LogInitializeException(message: String) extends RuntimeException(message)

case class Log(host: String, user: Option[String], epoch: Int, req: String, status: Int, size: Int, referer: Option[String]) {
  private val reqs = this.req.split(" ")
  if (reqs.length != 3) {
    // NOTE: should I return Either instead of throwing exception even if it's
    // weired?
    throw new LogInitializeException(s"the `req` parameter is invalid: $req")
  }
  // xxx Value. Just `val` definitions without `V` suffix suttisfy the
  // requirements but defining them with `def` is more readable I guess
  private val Array(methodV, pathV, protocolV) = this.req.split(" ")

  def method: String = methodV
  def path: String = pathV
  def protocol: String = protocolV

  def uri: String = s"http://$host$path"

  def time: String =
    DateTimeFormat
      .forPattern("yyyy-MM-dd'T'HH:mm:ss")
      .print(new DateTime(this.epoch.toLong * 1000)
        .withZone(DateTimeZone.UTC))
}
