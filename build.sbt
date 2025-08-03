import sbtassembly.AssemblyPlugin.autoImport._

val scala3Version = "3.3.5"
val javafxVersion = "21"
val osName = System.getProperty("os.name").toLowerCase()

val osClassifier = if (osName.contains("win")) "win"
else if (osName.contains("mac")) "mac"
else "linux"

enablePlugins(ScalafmtPlugin, ScoverageSbtPlugin, AssemblyPlugin)

assembly / mainClass := Some("Launcher")

assembly / assemblyOutputPath := file(s"./sgp-$osClassifier.jar")

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
      "org.scalafx" %% "scalafx" % "21.0.0-R32",

      // JavaFX dependencies with platform classifier
      "org.openjfx" % "javafx-base" % javafxVersion classifier osClassifier,
      "org.openjfx" % "javafx-controls" % javafxVersion classifier osClassifier,
      "org.openjfx" % "javafx-graphics" % javafxVersion classifier osClassifier,
      "org.openjfx" % "javafx-swing" % javafxVersion classifier osClassifier,

      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.typelevel" %% "cats-kernel" % "2.12.0"
    )
  )

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", "substrate", "config", _*) => MergeStrategy.first
  case PathList("META-INF", "native-image", _*) => MergeStrategy.first
  case "module-info.class" => MergeStrategy.discard
  case PathList("META-INF", "substrate", "config", "resourcebundles") => MergeStrategy.first
  case PathList("META-INF", "substrate", "config", xs @ _*) if xs.exists(_.endsWith(".json")) => MergeStrategy.first
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.defaultMergeStrategy(x)
}
