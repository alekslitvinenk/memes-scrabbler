lazy val root = (project in file("."))
  .settings(
    name := "memes-scrabbler",
    version := "0.1",
    scalaVersion := "2.12.8",
    unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/resources" },

    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig" % "0.12.2",
      "com.typesafe.akka" %% "akka-http" % "10.1.8",
      "com.typesafe.akka" %% "akka-stream" % "2.5.26",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.8.0",
      "io.prometheus" % "simpleclient" % "0.6.0",
      "io.prometheus" % "simpleclient_hotspot" % "0.6.0",
      "io.prometheus" % "simpleclient_logback" % "0.6.0",
      "io.prometheus" % "simpleclient_pushgateway" % "0.6.0",
        "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scalamock" %% "scalamock" % "4.1.0" % Test,
    ),
  )

addCommandAlias(
  "build",
  """|;
     |clean;
     |assembly;
  """.stripMargin)