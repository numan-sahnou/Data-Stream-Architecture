import org.mongodb.scala._
import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Properties

import scala.collection.JavaConverters._

object ConsumerSaver {
  def main(args: Array[String]): Unit = {
    consumeFromKafka("peace-watch-records")
  }

  def consumeFromKafka(topic: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092, localhost:9093, localhost:9094")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "key")
    props.put("auto.offset.reset", "latest")
    props.put("auto.commit.interval.ms", "1000")
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)

    consumer.subscribe(util.Collections.singletonList(topic))

    val client: MongoClient = MongoClient("mongodb://127.0.0.1:27017") //IP MONGO SERVER
    val db: MongoDatabase = client.getDatabase("PeaceData") //DATABASE
    val collection: MongoCollection[Document] = db.getCollection("records") //COLLECTION

    // I know you won't be happy about this While infinite loop but after a lot of research
    // We came to the point that it's okay because it's not a busy loop. During each poll if data is not available, the call waits for the given period of time
    // Moreover, Consumers needs to continuously poll Kafka for more data so it needs an infinite loop because that's how Kafka was designed (it's not a queue)
    while (true) {
      val record = consumer.poll(1000).asScala      
      record.foreach{ rec =>
        val data = rec.value()
        //We add the timestamp of the data produced
        //Records need to be parsed into Mongo Document to be pulled into the Mongo collection  
        val document: Document = Document(data.replace("}", ","+"\"Timestamp\""+":"+rec.timestamp+"}"))
        val observable: Observable[Completed] = collection.insertOne(document)
        //Explicitly subscribe:
        observable.subscribe(new Observer[Completed] {

          override def onNext(result: Completed): Unit = println("Inserted")

          override def onError(e: Throwable): Unit = println("Failed")

          override def onComplete(): Unit = println("Completed")
        })

        //collection.replaceOne(Filters.eq("Ts", document.getInt32()), new Document("Ts" -> new BsonInt32(rec.timestamp))).subscribe((updateResult: UpdateResult) => println(updateResult))
        Thread.sleep(1000)
        }
    }
  }
}