package uk.co.tobyhobson.cats

import cats.MonadError
import cats.data.EitherT
import cats.instances.future._
import cats.syntax.either._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Example showing how to use MonadError
  * see https://www.tobyhobson.co.uk/monad-error/
  */
object MonadErrors {

  // Our error hierarchy
  sealed trait ServiceError
  case class UserNotFound(id: Int) extends ServiceError
  case class OrderNotFound(user: User) extends ServiceError
  case class OtherError(message: String) extends ServiceError

  case class User(id: Int)
  case class Order(totalAmount: Int)

  // Try changing this to return an odd number to trigger the failure
  def getUserId: Either[UserNotFound, Int] = 1.asRight

  def fetchUser(id: Int): Future[Either[UserNotFound, User]] =  {
    if (id % 2 == 0)
      Future.successful { User(id).asRight }
    else
      Future.failed(new Exception("BOOM!"))
  }

  def fetchOrder(user: User): Future[Either[OrderNotFound, Order]] = Future.successful(Order(100).asRight)

  def main(args: Array[String]): Unit = {
    val eitherT = for {
      userId <- EitherT.fromEither[Future](getUserId: Either[ServiceError, Int])
      user <- EitherT(fetchUser(userId): Future[Either[ServiceError, User]])
      order <- EitherT(fetchOrder(user): Future[Either[ServiceError, Order]])
    } yield order

    /**
      * MonadError expects a Monad in the shape M[_] but our EitherT is actually M[F[_], A, B] so we need
      * to fix two of the three types to give us M[_]. We could use an explicit type for this i.e.
      *
      * type MyEitherT[A] = EitherT[Future, ServiceError, A]
      * MonadError[MyEitherT, Throwable].recoverWith(eitherT) { ... }
      *
      * But we can also use the kind compiler plugin to create an anonymous type for us. That's what's happening
      * in the EitherT[Future, ServiceError, ?] call below
      */
    val recoveredEitherT = MonadError[EitherT[Future, ServiceError, ?], Throwable].recoverWith(eitherT) {
      case t => EitherT.leftT[Future, Order](OtherError(s"future failed - ${t.getMessage}"): ServiceError)
    }

    val errorOrUserAndOrder = Await.result(recoveredEitherT.value, 1.second)
    errorOrUserAndOrder.foreach(order => println(s"success: $order"))
    errorOrUserAndOrder.left.foreach(error => println(s"error: $error"))

    // Using the implicit class and recoverF
    val recoveredEitherT2 = eitherT.recoverF(t => OtherError(s"future failed - ${t.getMessage}"): ServiceError)
    val errorOrUserAndOrder2 = Await.result(recoveredEitherT2.value, 1.second)
    errorOrUserAndOrder2.foreach(order => println(s"success: $order"))
    errorOrUserAndOrder2.left.foreach(error => println(s"error: $error"))
  }

  implicit class RecoveringEitherT[F[_], A, B](underlying: EitherT[F, A, B])(implicit me: MonadError[F, Throwable]) {
    def recoverF(op: Throwable => A) = MonadError[EitherT[F, A, ?], Throwable].recoverWith(underlying) {
      case t => EitherT.fromEither[F](op(t).asLeft[B])
    }
  }
}
