
// Latest version numbers were updated on 2026-04-24.
val scala211 = "2.11.12" // up to 2.11.12
val scala212 = "2.12.21" // up to 2.12.21
val scala213 = "2.13.18" // up to 2.13.18
val scala31  = "3.1.3"   // up to 3.1.3
// Only the LTS versions are listed next.
val scala33  = "3.3.7"   // up to 3.3.7
val scala3   = scala31

ThisBuild / crossScalaVersions := Seq(scala212, scala211, scala213, scala3)
ThisBuild / scalaVersion := scala212

scalacOptions ++= {
  val noAdaptedArgsOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 11)) => Some("-Yno-adapted-args")
    case Some((2, 12)) => Some("-Yno-adapted-args")
    case _ => None
  }
  val lintOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => Some("-Xlint")
    case _ => None
  }
  val deadCodeOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => Some("-Ywarn-dead-code")
    case _ => None
  }
  val unusedOpt = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => Some("-Ywarn-unused")
    case _ => None
  }

  Seq(
    "-feature",
    "-unchecked",
    "-deprecation",
    // "-Ywarn-value-discard",
    "-encoding", "utf8"
  ) ++ deadCodeOpt ++ unusedOpt ++ lintOpt ++ noAdaptedArgsOpt
}

libraryDependencies ++= {
  val scalaXmlVersion = CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => "1.3.1"
    // 2.0.1 => scala3-library_3 version 3.0.1
    // 2.1.0 => scala3-library_3 version 3.0.2
    // 2.2.0 => scala3-library_3 version 3.3.0
    // 2.3.0 => scala3-library_3 version 3.3.3
    // 2.4.0 => scala3-library_3 version 3.3.6
    // Can't use this first one because "ai.lum" %% "common" => scala3-library_3 version 3.1.3
    case Some((3, 0)) => "2.1.0"
    case Some((3, 1)) => "2.1.0"
    case Some((3, 2)) => "2.1.0"
    case Some((3, _)) => "2.4.0"
    case _ => ???
  }

  Seq(
    "ai.lum"                 %% "common"                  % "0.2.1",
    "org.scala-lang.modules" %% "scala-xml"               % scalaXmlVersion,
    // 3.2.20 of below depends on scala3-library 3.1.3, so use this one for cross-compilation.
    "org.scalatest"          %% "scalatest"               % "3.2.20" % "test",
    // 2.9.0 of below depends on scala3-library 3.1.3, so use this one for cross-compilation.
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.9.0" % "test"
  )
}

