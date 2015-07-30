package hatena.intern

case class LogCounter(logs: Iterable[Log]) {
  // count HTTP server error
  def countError: Int = (logs filter (log => 500 <= log.status && log.status < 600)).size

  type User = String
  def groupByUser: Map[User, Iterable[Log]] = logs groupBy (_.user getOrElse "guest")
}
