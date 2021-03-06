import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbt.Keys._
import sbt._

import scalariform.formatter.preferences._
import com.github.tkawachi.doctest.DoctestPlugin.doctestSettings

object hatenaInternExerciseBuild extends Build {
  val appName = "hatena-intern-exercise"
  val appVersion  = "0.0.1"
  val appScalaVersion = "2.11.6"

  val main = Project(
    appName,
    base = file("."),
    settings = Seq(
      version := appVersion,
      scalaVersion := appScalaVersion,
      libraryDependencies ++= Seq(
        "com.github.nscala-time" %% "nscala-time" % "2.0.0",
        "org.scalatest" %% "scalatest" % "2.2.4" % "test",
        "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3",
        "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
        "org.scalaz" %% "scalaz-core" % "7.1.3",
        "org.typelevel" %% "scalaz-scalatest" % "0.2.2" % "test"
        ),
      resolvers ++= Seq(
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
      ),
      scalacOptions ++= Seq(
        "-Xlint",
        "-Ywarn-unused",
        "-Ywarn-unused-import",
        "-unchecked", "-deprecation", "-feature",
        "-language:postfixOps",
        "-language:reflectiveCalls",
        "-encoding", "utf8"
      ),
      fork in Test := true,
      scalacOptions in Test ++= Seq("-Yrangepos")
    ) ++  formatSettings
  ).settings(SbtScalariform.scalariformSettings: _*)
   .settings(doctestSettings: _*)

  lazy val formatSettings = Seq(
    ScalariformKeys.preferences := FormattingPreferences()
    .setPreference(IndentWithTabs, true)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveDanglingCloseParenthesis, true)
  )
}
