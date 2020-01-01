lazy val root = (project in file("."))
  .settings(
    name := "memes-scrabbler",
    version := "0.1",
    scalaVersion := "2.12.8",
    unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/resources" },

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scalamock" %% "scalamock" % "4.1.0" % Test,
      "com.github.pureconfig" %% "pureconfig" % "0.12.2",
    ),
  )

addCommandAlias(
  "build",
  """|;
     |clean;
     |assembly;
  """.stripMargin)