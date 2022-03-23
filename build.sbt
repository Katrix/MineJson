lazy val sharedSettings = Seq(
  organization       := "net.katsstuff",
  version            := "0.3.2",
  scalaVersion       := "2.13.4",
  crossScalaVersions := Seq("2.13.4", "2.12.10"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint"
  ),
  scalacOptions ++= {
    if (scalaVersion.value.startsWith("2.13"))
      Seq(
        "-Wdead-code",
        "-Wunused:imports"
      )
    else
      Seq(
        "-Ywarn-dead-code",
        "-Ywarn-unused-import"
      )
  }
)

lazy val publishSettings = Seq(
  publishMavenStyle       := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/Katrix/MineJson"),
      "scm:git:github.com/Katrix/MineJson",
      Some("scm:git:github.com/Katrix/MineJson")
    )
  ),
  homepage        := Some(url("https://github.com/Katrix/MineJson")),
  developers      := List(Developer("Katrix", "Kathryn Frid", "katrix97@hotmail.com", url("http://katsstuff.net/"))),
  autoAPIMappings := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val noPublishSettings = Seq(publish := {}, publishLocal := {}, publishArtifact := false)

lazy val minejsonText = project.settings(
  sharedSettings,
  publishSettings,
  name                                   := "minejson-text",
  libraryDependencies += "net.katsstuff" %%% "typenbt" % "0.5.1",
  libraryDependencies += "net.katsstuff" %%% "typenbt-mojangson" % "0.5.1",
  libraryDependencies += "io.circe"      %%% "circe-core" % "0.13.0",
  libraryDependencies += "io.circe"      %%% "circe-parser" % "0.13.0",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.3" % Test
)

lazy val minejsonRoot =
  project
    .in(file("."))
    .aggregate(minejsonText)
    .settings(
      sharedSettings,
      noPublishSettings,
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
        else Some("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    )
