lazy val scala213 = "2.13.2"

ThisBuild / intellijPluginName := "scala-heapview"
ThisBuild / intellijPlatform := IntelliJPlatform.IdeaCommunity
ThisBuild / intellijBuild := "213"

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias(
  "check",
  "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck"
)

lazy val `scala-heapview` =
  project
    .in(file("."))
    .enablePlugins(SbtIdeaPlugin)
    .settings(
      scalaVersion := scala213,
      version := "0.0.1-SNAPSHOT",
      Global / intellijAttachSources := true,
      Compile / javacOptions ++= "--release" :: "11" :: Nil,
      intellijPlugins := Seq(
        "com.intellij.java".toPlugin,
        "org.intellij.scala:2021.3.18".toPlugin
      )
    )
