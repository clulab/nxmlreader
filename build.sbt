import ReleaseTransformations._

name := "nxmlreader"

organization := "ai.lum"

val scala211 = "2.11.12" // up to 2.11.12
val scala212 = "2.12.21" // up to 2.12.21
val scala213 = "2.13.17" // up to 2.13.17
val scala30  = "3.0.2"   // up to 3.0.2 // not possible because commons is 3.1.3
val scala31  = "3.1.3"   // up to 3.1.3, for maximum compatibility
val scala32  = "3.2.2"   // up to 3.2.2
val scala33  = "3.3.7"   // up to 3.3.7 (LTS) // works starting with this
val scala34  = "3.4.3"   // up to 3.4.3
val scala35  = "3.5.2"   // up to 3.5.2
val scala36  = "3.6.4"   // up to 3.6.4
val scala37  = "3.7.4"   // up to 3.7.4
val scala3   = scala33

ThisBuild / crossScalaVersions := Seq(scala212, scala211, scala213, scala31, scala32, scala33, scala34, scala35, scala36, scala37)
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
    // 3.2.11 of below depends on scala3-library 3.0.2, so use this one for cross-compilation.
    "org.scalatest"          %% "scalatest"               % "3.2.11"    % "test",
    // 2.7.0 of below depends on scala3-library 3.0.2, so use this one for cross-compilation.
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.7.0" % "test",
    "ai.lum"                 %% "common"                  % "0.2.0-SNAPSHOT",
    "org.scala-lang.modules" %% "scala-xml"               % scalaXmlVersion
  )
}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("+test"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommandAndRemaining("sonatypeReleaseAll"),
  pushChanges
)

// Publishing settings

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/lum-ai/nxmlreader</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <url>scm:git:github.com/lum-ai/nxmlreader</url>
    <connection>scm:git:git@github.com:lum-ai/nxmlreader.git</connection>
  </scm>
  <developers>
    <developer>
      <id>marcovzla</id>
      <name>Marco Antonio Valenzuela Escárcega</name>
      <url>lum.ai</url>
    </developer>
    <developer>
      <id>ghp</id>
      <name>Gus Hahn-Powell</name>
      <url>lum.ai</url>
    </developer>
  </developers>
