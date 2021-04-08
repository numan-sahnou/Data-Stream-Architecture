import com.mongodb._
import com.mongodb.spark._
import com.mongodb.spark.config._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark._

import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId

import java.io._

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
                  `Alert`: Int,                   // Alert,
				  `Timestamp`: Long               // Timestamp
                )

object Statistics {
  def main(args: Array[String]): Unit = {
   
   val spark = SparkSession.builder()
      .master("local")
      .appName("MongoSparkConnectorIntro")
      .config("spark.mongodb.input.uri", "mongodb://127.0.0.1:27017/PeaceData.records")
      .getOrCreate()

	import spark.implicits._

	val df= MongoSpark.load(spark)
	val myRdd : RDD[Drone] = df.as[Drone].rdd
    
	question1(myRdd)
	question2(myRdd)
	question3(myRdd)
	question4(myRdd)
	question5(myRdd)
  }
  def timestampToDate(ts: Long): LocalDateTime = {
		val tmp = Instant.ofEpochSecond(ts)
		LocalDateTime.ofInstant(tmp, ZoneId.systemDefault())
  }
	 
  def alertPeople(result: RDD[Drone]): RDD[Drone] = {
	  //we filter all the people with no alerts
	  result.filter(x => x.Alert==1)
  }

  def question1(result: RDD[Drone]): Unit = {
		//look for the part of the day and we sort by size (either we have more people alerted on the morning or evening)
		val partDay = alertPeople(result).groupBy(x => {
			val hour = timestampToDate(x.Timestamp).getHour()
			if(hour >= 0 && hour < 12) "morning" else "evening"
		}).sortBy(_._2.size, ascending = false) //Descending order
			
		val angryHour = partDay.first() //pick the first element
		
		val pw = new PrintWriter(new File("question1.txt" ))
		pw.write(s"Question 1:\nThe most riots occures during the ${angryHour._1} with a total of ${angryHour._2.size} riots")
		pw.close
	}

	def question2(result: RDD[Drone]): Unit = {

		val country = alertPeople(result).groupBy(_.Country).top(5)(Ordering[Int].on(_._2.size))	
		val pw = new PrintWriter(new File("question2.txt" ))
		pw.write("Question 2:\nTop 5 coutries with the most alerted of people:")
		country.foreach(x => pw.write(s"\n${x._1} : ${x._2.size}"))
		pw.close
	}

	def question3(result: RDD[Drone]): Unit = {
		val avgBattery = result.map(_.Battery).sum / result.count()
		val avgPeace = result.map(_.PeaceScore).sum / result.count()

		val pw = new PrintWriter(new File("question3.txt" ))
		pw.write(s"Question 3:\nAverage battery : $avgBattery")
		pw.write(s"\nAverage Peace Score : $avgPeace")
		pw.close
	}

	def question4(result: RDD[Drone]): Unit = {
		val weekDays = alertPeople(result).groupBy(x => { 
			val days = timestampToDate(x.Timestamp).getDayOfWeek() 
			if(days=="SATURDAY" || days=="SUNDAY") "week-end" else "work-days" 
		}).sortBy(_._2.size, ascending = false)

		val riotDays = weekDays.first()
		val pw = new PrintWriter(new File("question4.txt" ))
		pw.write(s"Question 4:\nThere is more riot during ${riotDays._1} with a total of ${riotDays._2.size} riots compared to ")
		pw.close
	}

	def question5(result: RDD[Drone]) {
		val percent = result.filter(_.Battery < 21).count() * 100 / result.count()

		val pw = new PrintWriter(new File("question5.txt" ))
		pw.write(s"Question 5:\nThere is $percent% of drones which have less than 20% of battery")
		pw.close
	}
}