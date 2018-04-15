import sbtcrossproject.{crossProject, CrossType}

lazy val sharedSettings = Seq(
  organization := "net.katsstuff",
  version := "0.1",
  scalaVersion := "2.12.5",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-unused-import"
  )
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/Katrix-/AckCord"),
      "scm:git:github.com/Katrix-/AckCord",
      Some("scm:git:github.com/Katrix-/AckCord")
    )
  ),
  homepage := Some(url("https://github.com/Katrix-/AckCord")),
  developers := List(Developer("Katrix", "Nikolai Frid", "katrix97@hotmail.com", url("http://katsstuff.net/"))),
  autoAPIMappings := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val noPublishSettings = Seq(publish := {}, publishLocal := {}, publishArtifact := false)

lazy val minejsonBase = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(
    sharedSettings,
    publishSettings,
    name := "minejson",
    libraryDependencies += "net.katsstuff" %%% "typenbt"      % "0.3",
    libraryDependencies += "io.circe"      %%% "circe-core"   % "0.9.3",
    libraryDependencies += "io.circe"      %%% "circe-core"   % "0.9.3",
    libraryDependencies += "io.circe"      %%% "circe-parser" % "0.9.3"
  )

lazy val minejsonBaseJVM = minejsonBase.jvm
lazy val minejsonBaseJS  = minejsonBase.js

lazy val minejsonGenerator = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings, publishSettings, name := "minejson-generator")
  .dependsOn(minejsonBase)

lazy val minejsonGeneratorJVM = minejsonGenerator.jvm
lazy val minejsonGeneratorJS  = minejsonGenerator.js

lazy val minejsonRoot =
  project
    .in(file("."))
    .aggregate(minejsonBaseJVM, minejsonBaseJS, minejsonGeneratorJVM, minejsonGeneratorJS)
    .settings(
      noPublishSettings,
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
        else Some("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    )
