package hatena.intern.helper

object SpecHelper {
  def pathToDataFile(filename: String) =
    (scalax.file.Path(".").toAbsolute.parent.flatMap(_.parent).get / "sample_data" / filename).toURI.getRawPath
}
