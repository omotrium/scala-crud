import sbt.Keys.libraryDependencies

name := """my-play-crud-project"""

version := "1.0-SNAPSHOT"
scalaVersion := "2.13.16"
lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  PlayKeys.playDefaultPort := 10911,
  libraryDependencies ++= Seq(
    guice,
    //jdbc,
    ws,
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
//    "com.typesafe.slick" %% "slick" % "3.3.3",
    "com.typesafe.play" %% "play-slick" % "5.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
    "com.h2database" % "h2" % "2.2.224",

),
  dependencyOverrides += "org.scala-lang.modules" % "scala-xml_2.13" % "2.2.0"

)

//Compile / unmanagedResourceDirectories += baseDirectory.value / "conf"



// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
