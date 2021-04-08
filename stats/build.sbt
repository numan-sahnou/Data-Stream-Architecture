name := "Project Data Engineering"

version := "0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.mongodb.spark" %% "mongo-spark-connector" % "2.3.0",
  "org.apache.spark" %% "spark-core" % "2.3.0",
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "io.github.cquiroz" %% "scala-java-time" % "2.0.0-RC3"
)