package uk.co.tobyhobson

import cats.Functor
import cats.data.Nested
import cats.instances.future._
import cats.instances.list._
import cats.instances.option._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.languageFeature.higherKinds._

/**
  * Examples illustrating the use of Functors from the Cats library.
  * see https://www.tobyhobson.co.uk/scala-cats-functor/
  */
object Functors {

  case class LineItem(price: Double)

  /**
    * Base method which operates on LineItems
    *
    * @param lineItem
    * @return
    */
  def applyVat(lineItem: LineItem): LineItem = lineItem.copy(price = lineItem.price * 1.2)

  /**
    * Use a Functor to map over "something", applying op to each item
    *
    * @param item A List, Option or any other "type constructor" (Wrapper type)
    * @param op The operation to perform
    * @param ev A Functor implementation for type F
    * @tparam A
    * @tparam B
    * @tparam F A type constructor e.g. List, Option, Future etc
    * @return
    */
  def withFunctor[A, B, F[_]](item: F[A], op: A => B)(implicit ev: Functor[F]): F[_] = Functor[F].map(item)(op)

  /**
    * An implicit class which just allows us to call .nestedMap on any nested functor e.g. a List[Option[LineItem]
    *
    * @param underlying "something" that consists of two nested "wrapper types" e.g. List[Option]
    * @param ev$1 Functor implementation for the outer wrapper
    * @param ev$2 Functor implementation for the inner wrapper
    * @tparam A The type of the item itself
    * @tparam F Outer wrapper
    * @tparam G Inner wrapper
    */
  implicit class RichFunctor[A, F[_]: Functor, G[_]: Functor](underlying: F[G[A]]) {
    def nestedMap[B](op: A => B): F[G[B]] = Functor[F].compose[G].map(underlying)(op)
  }

  def main(args: Array[String]): Unit = {
    // Pass List to withVat
    val lineItemsList = List(LineItem(10.0), LineItem(20.0))
    withFunctor(lineItemsList, applyVat).foreach(println)

    // Pass an Option
    val maybeLineItem: Option[LineItem] = Some(LineItem(10))
    withFunctor(maybeLineItem, applyVat).foreach(println)

    // Compose Future & Option
    val eventualLineItem: Future[Option[LineItem]] = Future.successful { Some(LineItem(10)) }
    val eventualResult = Functor[Future].compose[Option].map(eventualLineItem)(applyVat)
    Await.result(eventualResult, 1.second).foreach(println)

    // Using the nestedMap function
    val eventualResultNestedMap = eventualLineItem.nestedMap(applyVat)
    Await.result(eventualResultNestedMap, 1.second).foreach(println)

    // WARNING - Advanced stuff here!
    advancedOps()
  }

  /**
    * More advanced usage which requires some hacks to get things in the correct
    * shape for the compiler. This is a downside to Scala's strong type safety,
    * sometimes it actually works against us :(
    */
  def advancedOps(): Unit = {
    val eventualLineItem: Future[Option[LineItem]] = Future.successful { Some(LineItem(10)) }

    // We can still use our withFunctor method with nested functors but it's a little messy
    // as we need to define a custom type and Functor implementation
    // withFunctor takes type F[_] i.e. Option[LineItem] but we're passing F[G[_]] i.e. Future[Option[LineItem]]
    // so we need to "flatten" the type back to F[_] to get it in the right "shape".
    // we do this by fixing one of the types (option in this case) and leaving the remaining type free
    // i.e. F[G[_]] => F[_]
    type FutureOption[A] = Future[Option[A]]

    // Cats includes Functors for Future and Option but we need to compose them
    // together to create a Functor capable of handling Future[Option[A]]
    implicit val futureOptionFunctor = Functor[Future].compose[Option]
    withFunctor[LineItem, LineItem, FutureOption](eventualLineItem, applyVat)

    // Alternatively we can use Cats' Nested type
    val nested = Nested(eventualLineItem)

    // Similar to the FutureOption compiler hack
    // withFunctor expects to receive something of type F[_] (one "hole")
    // but our nested type is actually [F[_], G[_], A] so we fix the first two types
    // i.e. Nested[F[_], G[_], A] => Nested[_]
    type NestedFutureOption[A] = Nested[Future, Option, A]

    val eventualResultNested = withFunctor[LineItem, LineItem, NestedFutureOption](nested, applyVat).value
    Await.result(eventualResultNested, 1.second).foreach(println)

    // Alternative implementation which uses the kind compiler plugin to define a
    // custom type on the fly instead of needing the NestedFutureOption type
    val eventualResultNested2 = withFunctor[LineItem, LineItem, Nested[Future, Option, ?]](nested, applyVat).value
    Await.result(eventualResultNested2, 1.second).foreach(println)
  }

}
