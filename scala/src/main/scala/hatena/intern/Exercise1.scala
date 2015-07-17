package hatena.intern

case class Log(host: String, user: String, epoch: Int, req: String, status: Int, size: Int, referer: String) {
  // xxx Value. Just `val` definitions without `V` suffix suttisfy the
  // requirements but defining them with `def` is more readable I guess
  private val Array(methodV, pathV, protocolV) = this.req.split(" ")

  def method: String = methodV
  def path: String = pathV
  def protocol: String = protocolV

  def uri: String = s"http://$host$path"
  def time: String = ???
}
