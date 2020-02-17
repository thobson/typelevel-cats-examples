package uk.co.tobyhobson.cats

import cats.data.WriterT
import cats.instances.vector._
import ch.qos.logback.classic.Logger
import monix.eval.Task
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

import scala.concurrent.duration._
import scala.concurrent.Await

object WriterMonad {
  
  private val Logger = LoggerFactory.getLogger(this.getClass)
  
  case class User(id: Int)
  case class Order(totalAmount: Int)
  
  case class LogEntry(level: Level, message: String)
  
  def getUser(id: Int): WriterT[Task, Vector[LogEntry], User] = {
    val logs = Task(Vector(LogEntry(Level.DEBUG, "getting user")))
    val withResult = logs.map(logs => logs -> User(id))
    // The apply method is a simple way of constructing a writer with a log and result, suspended in F
    WriterT(withResult)
  }
  
  def getOrder(user: User): WriterT[Task, Vector[LogEntry], Order] = {
    val logs = Task(Vector(LogEntry(Level.DEBUG, "getting order")))
    val withResult = logs.map(logs => logs -> Order(100))
    WriterT(withResult)
  }
  
  def writeLogs(logs: Vector[LogEntry]): Task[Unit] = Task {
    logs.foreach {
      case LogEntry(Level.DEBUG, message) => Logger.debug(message)
      case LogEntry(Level.INFO, message) => Logger.info(message)
      // don't do this in production!
      case LogEntry(_, message) => Logger.debug(message)
    }
  }
  
  def main(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global
    
    val program = for {
      user <- getUser(1)
      order <- getOrder(user)
    } yield order
    
    val logged = for {
      logs <- program.written
      result <- program.value
      _ <- writeLogs(logs)
    } yield result
    
    val eventualResult = logged.runToFuture
    val result = Await.result(eventualResult, 1.second)
   
    Logger.info(result.toString)
  }
  
}
