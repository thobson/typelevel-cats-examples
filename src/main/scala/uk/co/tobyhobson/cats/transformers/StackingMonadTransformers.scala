package uk.co.tobyhobson.cats.transformers

import cats.data.{EitherT, WriterT}
import cats.implicits._
import monix.eval.Task

import scala.concurrent.Await
import scala.concurrent.duration._

object StackingMonadTransformers {
  
  // Our error hierarchy
  sealed trait ServiceError
  case class UserNotFound(id: Int) extends ServiceError
  case class OrderNotFound(user: User) extends ServiceError
  
  case class User(id: Int)
  case class Order(totalAmount: Int)
  
  // Our custom monad stack
  type OurStack[A] = WriterT[EitherT[Task, ServiceError, *], Vector[String], A]
  
  def fetchUser(id: Int): OurStack[User] = {
    val eitherT = EitherT.rightT[Task, ServiceError](User(id))
    // we can use WriterT's apply method to build a WriterT from an F[(L, V)]
    // i.e. a tuple, where L is the log and V is the value
    val withLogs = eitherT.map(user => Vector("getting user") -> user)
    WriterT(withLogs)
  }
  
  def fetchOrder(user: User): OurStack[Order] = {
    val eitherT = EitherT.rightT[Task, ServiceError](Order(100))
    val withLogs = eitherT.map(user => Vector("getting order") -> user)
    WriterT(withLogs)
  }
  
  def main(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global
    
    val program = for {
      user <- fetchUser(1)
      order <- fetchOrder(user)
    } yield order
  
    val eventualLogs = program.written.value.runToFuture
    val logs = Await.result(eventualLogs, 1.second)
    
    val eventualResult = program.value.value.runToFuture
    val result = Await.result(eventualResult, 1.second)
    
    println(logs)
    println(result)
  }
  
}
