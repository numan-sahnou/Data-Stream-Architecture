name := "Project Data Engineering"

version := "0.1"

scalaVersion := "2.11.8"

val sparkVersion = "2.3.0"


dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.mongodb.spark" %% "mongo-spark-connector" % sparkVersion,
  "org.apache.kafka" % "kafka-clients" % "2.7.0",
  "com.typesafe.play" %% "play-json" % "2.4.2",
  "org.apache.commons" % "commons-email" % "1.5",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.8.0",
  "org.mongodb.scala" %% "mongo-scala-bson" % "2.8.0",
  "org.mongodb" % "bson" % "3.12.0",
  "org.mongodb" % "mongodb-driver-core" % "3.12.0",
  "org.mongodb" % "mongodb-driver-async" % "3.12.0",
  "org.mongodb" %% "casbah-core" % "3.1.1"
)