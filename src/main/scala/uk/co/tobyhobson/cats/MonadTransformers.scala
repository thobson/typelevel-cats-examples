package uk.co.tobyhobson.cats

import cats.data.{EitherT, OptionT}
import cats.instances.future._
import cats.syntax.either._
import cats.syntax.option._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Examples of Monad Transformers
  * see https://www.tobyhobson.co.uk/what-is-a-monad/ and https://www.tobyhobson.co.uk/scala-monad-transformers/
  */
object MonadTransformers {

  // Our error hierarchy
  sealed trait ServiceError
  case class UserNotFound(id: Int) extends ServiceError
  case class OrderNotFound(user: User) extends ServiceError

  case class User(id: Int)
  case class Order(totalAmount: Int)

  def fetchUser(id: Int): Future[Option[User]] = Future.successful(User(id).some)

  def fetchOrder(user: User): Future[Option[Order]] = Future.successful(Order(100).some)

  def fetchUserOrError(id: Int): Future[Either[UserNotFound, User]] = Future.successful {
    if (id % 2 == 0) User(id).asRight else UserNotFound(id).asLeft
  }

  def fetchOrderOrError(user: User): Future[Either[OrderNotFound, Order]] = Future.successful(Order(100).asRight)

  def main(args: Array[String]): Unit = {
    // OptionT
    val eventualMaybeUserAndOrder = (for {
      user <- OptionT(fetchUser(1))
      order <- OptionT(fetchOrder(user))
    } yield (user, order)).value

    val maybeUserAndOrder = Await.result(eventualMaybeUserAndOrder, 1.second)
    maybeUserAndOrder.foreach(println)

    // EitherT with variance example
    val eventualErrorOrUserAndOrder = (for {
      // Note how we need to tell the compiler to treat the UserNotFound error as a Service error
      // this is because we need fetchUserOrError and fetchOrderOrError to be of the same left type
      // but EitherT is invariant therefore as far as it's concerned UserNotFound and OrderNotFound
      // are totally unrelated types
      user <- EitherT(fetchUserOrError(2): Future[Either[ServiceError, User]])
      order <- EitherT(fetchOrderOrError(user): Future[Either[ServiceError, Order]])
    } yield (user, order)).value

    val errorOrUserAndOrder = Await.result(eventualErrorOrUserAndOrder, 1.second)
    errorOrUserAndOrder.foreach(println)

    // EitherT wrapping a Left
    val eventualLeftEitherT = (for {
      user <- EitherT(fetchUserOrError(1): Future[Either[ServiceError, User]])
      order <- EitherT(fetchOrderOrError(user): Future[Either[ServiceError, Order]])
    } yield (user, order)).value

    val leftEitherT = Await.result(eventualLeftEitherT, 1.second)
    leftEitherT.left.foreach(println)
  }

}
