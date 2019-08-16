package uk.co.tobyhobson.cats

import cats.kernel.Monoid

object Monoids {

  def combineInts(collection: Seq[Int]): Int = {
    import cats.instances.int._
    Semigroups.combineCollection(Monoid[Int].empty, Seq(1, 2, 3))
  }

  def combineWithCustomMonoid(collection: Seq[String]): String = {

    implicit val customMonoid = new Monoid[String] {
      override def empty: String = "A collection of Strings:"
      override def combine(x: String, y: String): String = s"$x $y"
    }

    collection.foldLeft(customMonoid.empty)(customMonoid.combine)
  }

  def combineAnything[A](collection: Seq[A])(implicit ev: Monoid[A]): A = {
    val instance = Monoid[A]
    collection.foldLeft(instance.empty)(instance.combine)
  }

  def mapReduce[A, B](collection: Seq[A])(op: A => B)(implicit ev: Monoid[B]): B = {
    val instance = Monoid[B]
    collection.map(op).foldLeft(instance.empty)(instance.combine)
  }

  def main(args: Array[String]): Unit = {
    val combinedInts = combineInts(Seq(1, 2, 3))
    println(s"Combined ints: $combinedInts")

    val combinedWithCustomMonoid = combineWithCustomMonoid(Seq("john", "doe"))
    println(combinedWithCustomMonoid)

    import cats.instances.long._
    val combinedLongs = combineAnything(Seq(1l, 2l))
    println(s"combined longs: $combinedLongs")

    val mappedAndReduced = mapReduce(Seq(1l, 2l, 3l)) { _ * 2 }
    println(s"map reduce output: $mappedAndReduced")
  }

}
