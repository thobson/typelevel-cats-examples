package uk.co.tobyhobson

import cats.Applicative
import cats.instances.future._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Illustrates using Applicatives to perform N operations independently and concurrently
  * see https://www.tobyhobson.co.uk/applicatives-vs-monads/
  */
object ApplicativeFutures {

  def fetchFirstName: Future[String] = {
    println("fetching first name")
    Future.successful("John")
  }

  def fetchLastName: Future[String] = {
    println("fetching last name")
    Future.successful("Doe")
  }

  def main(args: Array[String]): Unit = {
    val eventualFullName = Applicative[Future].map2(fetchFirstName, fetchLastName) (_ + " " + _)
    val fullName = Await.result(eventualFullName, 1.second)
    println(fullName)
  }

}
