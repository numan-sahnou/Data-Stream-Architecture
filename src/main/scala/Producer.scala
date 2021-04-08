import java.util.Properties
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.kafka.clients.producer._
import scala.collection.JavaConverters._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.apache.spark.sql._

//CLASS DRONE
case class Drone(
                  `DroneID`: Int,                 // Drone ID,
                  `Record`: String,               // Record ID,
                  `Citizen`: String,              // Citizen Name,
                  `Message`: String,              // Message,
                  `PeaceScore`: Int,              // Peace Score,
                  `Country`: String,              // Country,
                  `City`: String,                 // City,
                  `Latitude`: Double,             // Latitude position,
                  `Longitude`: Double,            // Longitude position,
                  `Battery`: Int,                 // Battery,
                  `Alert`: Int                    // Alert,
                )

object Producer {
  def main (args: Array[String]): Unit = {
    //DATA PATH
    val path = "C:/Users/Numan/Desktop/projet-data-engineering/data/drone_event.csv"

    //CREATE SPARK SESSION
    val sparkConfig = new SparkConf()
      .setMaster("local[*]")
      .setAppName("PeaceWatcher Simulator")

    val spark = SparkSession.builder
      .config(sparkConfig)
      .getOrCreate()

    import spark.implicits._
    
    //LOAD DF
    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path)
    
    // PREPROCESSING
    val dronesId = List(1,2,3,4,5,6,7,8) //LIST OF DRONE ID
    val drones: IndexedSeq[Dataset[Row]] = (0 to dronesId.size).map { i => df.where(df("DroneID")===i)} // WE COLLECT EACH SUB DATAFRAME BY DRONES 
    val d : IndexedSeq[Dataset[Drone]] = drones.map { d => d.as[Drone]} // WE CONVERT EACH DRONES RECORDS TO Drone CLASS INSTANCES
    val dronesJson : IndexedSeq[Array[String]] = d.map {e => e.toJSON.collect()} // TO JSON

    dronesJson.foreach(d => KafkaProd(d)) //CREATE A PRODUCER FOR EACH DRONE

    def KafkaProd (drones : Array[String]): Unit = {
      //Definition of the KAFKA Topics
      val STORAGE_TOPIC="peace-watch-records"
      val ALERT_TOPIC="alerts"

      //Properties of the kafka producer (connect to the bootstrap server and define the key and value serializer)
      val props = new Properties()
      props.put("bootstrap.servers", "localhost:9092")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("acks","all")

      val producer = new KafkaProducer[String, String](props)
      
      val thread = new Thread {

          override def run(): Unit = {
            val droneProducer = new KafkaProducer[String, String](props)

            drones.foreach{ row =>
              droneProducer.send(new ProducerRecord(STORAGE_TOPIC, "key", row))
              //IF THE RECORD IS AN ALERT THEN WE SEND THE RECORD THROUGH THE ALERT TOPIC
              if(row.contains("\"Alert\":1")) {
                producer.send(new ProducerRecord(ALERT_TOPIC, "key", Json.parse(row).as[JsObject].toString()))
                Thread.sleep(100)
              }
              println(row)
              Thread.sleep(60000)
            }
            droneProducer.close()
          }
      }
      thread.start()
    }
  }
}