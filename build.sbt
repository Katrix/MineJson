import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val sharedSettings = Seq(
  organization := "net.katsstuff",
  version      := "0.2",
  scalaVersion := "2.12.8",
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
  developers      := List(Developer("Katrix", "Nikolai Frid", "katrix97@hotmail.com", url("http://katsstuff.net/"))),
  autoAPIMappings := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val noPublishSettings = Seq(publish := {}, publishLocal := {}, publishArtifact := false)

lazy val minejsonText = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(
    sharedSettings,
    publishSettings,
    name                                   := "minejson-text",
    libraryDependencies += "net.katsstuff" %%% "typenbt" % "0.5.0",
    libraryDependencies += "net.katsstuff" %%% "typenbt-mojangson" % "0.5.0",
    libraryDependencies += "io.circe"      %%% "circe-core" % "0.11.1",
    libraryDependencies += "io.circe"      %%% "circe-parser" % "0.11.1",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test
  )

lazy val minejsonTextJVM = minejsonText.jvm
lazy val minejsonTextJS  = minejsonText.js

lazy val minejsonBase = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings, publishSettings, name := "minejson-base")
  .dependsOn(minejsonText)

lazy val minejsonBaseJVM = minejsonBase.jvm
lazy val minejsonBaseJS  = minejsonBase.js

lazy val minejsonAdvancement = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings, publishSettings, name := "minejson-advancement")
  .dependsOn(minejsonLootTable, minejsonRecipe)

lazy val minejsonAdvancementJVM = minejsonAdvancement.jvm
lazy val minejsonAdvancementJS  = minejsonAdvancement.js

lazy val minejsonLootTable = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings, publishSettings, name := "minejson-loottable")
  .dependsOn(minejsonBase)

lazy val minejsonLootTableJVM = minejsonLootTable.jvm
lazy val minejsonLootTableJS  = minejsonLootTable.js

lazy val minejsonRecipe = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings, publishSettings, name := "minejson-recipe")
  .dependsOn(minejsonBase)

lazy val minejsonRecipeJVM = minejsonRecipe.jvm
lazy val minejsonRecipeJS  = minejsonRecipe.js

lazy val minejsonGenerator = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings, publishSettings, name := "minejson-generator")
  .dependsOn(minejsonAdvancement, minejsonLootTable, minejsonRecipe)

lazy val minejsonGeneratorJVM = minejsonGenerator.jvm
lazy val minejsonGeneratorJS  = minejsonGenerator.js

lazy val minejsonRoot =
  project
    .in(file("."))
    .aggregate(
      minejsonTextJVM,
      minejsonTextJS,
      minejsonBaseJVM,
      minejsonBaseJS,
      minejsonAdvancementJVM,
      minejsonAdvancementJS,
      minejsonLootTableJVM,
      minejsonLootTableJS,
      minejsonRecipeJVM,
      minejsonRecipeJS,
      minejsonGeneratorJVM,
      minejsonGeneratorJS
    )
    .settings(
      noPublishSettings,
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
        else Some("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    )
