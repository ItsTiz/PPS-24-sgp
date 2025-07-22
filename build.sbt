import sbtassembly.AssemblyPlugin.autoImport._

val scala3Version = "3.3.5"

enablePlugins(ScalafmtPlugin, ScoverageSbtPlugin, AssemblyPlugin)

assembly / assemblyOutputPath := file("./sgp.jar")

lazy val root = (project in file("."))
  .settings(
    name := "PPS-24-sgp",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    javacOptions ++= Seq("-source", "17", "-target", "17"),
    scalafmtOnCompile := true,
    coverageEnabled := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "com.github.vlsi.mxgraph" % "jgraphx" % "4.2.2",
      "org.scalaz" %% "scalaz-core" % "7.3.8",
      "org.scalafx" %% "scalafx" % "20.0.0-R31"
    )
  )