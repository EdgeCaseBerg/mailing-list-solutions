name := "default-json-examples"

version := "0.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"

scalacOptions in Compile ++= Seq("-feature", "-deprecation")