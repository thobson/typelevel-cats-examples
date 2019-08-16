package uk.co.tobyhobson.cats

import cats.kernel.Semigroup

object Semigroups {

  val combineWithASpace = Semigroup.instance[String]((a, b) =>
    if (a.nonEmpty) s"$a $b" else b
  )

  /**
    * Combine two Strings using the String Semigroup provided by Cats (concatenation)
    * @return
    */
  def defaultSemigroup: String = {
    // use the default Semigroup type class for String
    import cats.instances.string._
    Semigroup[String].combine("john", "doe")
  }

  /**
    * We can also write our own instance which is pretty simple
    * @return
    */
  def customSemigroup: String = {
    // in this case we want a space between the two strings
    implicit val x = combineWithASpace
    Semigroup[String].combine("john", "doe")
  }

  /**
    * Semigroup provides combine(a, b) but of course we can build a recursive function to
    * combine n entries in a collection
    *
    * @param collection The collection to concatenate together
    * @param acc The accumulated value, we need an empty "starter" value in case the collection is empty
    * @return
    */
  def combineColectionOfStrings(collection: Seq[String], acc: String = ""): String = {
    implicit val x = combineWithASpace

    // This could also be implemented nicely using Seq.fold (see below)
    collection.headOption match {
      case Some(head) =>
        val updatedAcc = Semigroup[String].combine(acc, head)
        combineColectionOfStrings(collection.tail, updatedAcc)
      case None =>
        acc
    }
  }

  /**
    * Semigroup provides combine(a, b) but of course we can build a recursive function to
    * combine n entries in a collection
    *
    * @param collection The collection to concatenate together
    * @return
    */
  def combineColectionOfStringsImproved(collection: Seq[String]): String = {
    implicit val x = combineWithASpace

    // foldLeft needs a function of type (String, String) => String
    // and combine has a signature of (String, String) => String ... perfect!
    collection.foldLeft("")(Semigroup[String].combine)
  }

  /**
    * Generic function capable of combining anything given:
    * 1. an empty value of type A
    * 2. a semigroup instance for type A
    *
    * @param empty exactly what "empty" means depends on the context, probably "" for Strings and 0 for Ints
    * @param collection
    * @param ev the Semigroup instance for A
    * @tparam A
    * @return
    */
    def combineCollection[A](empty: A, collection: Seq[A])(implicit ev: Semigroup[A]): A = {
      collection.foldLeft(empty)(Semigroup[A].combine)
    }

  def main(args: Array[String]): Unit = {
    // Combine using the default semigroup instance
    println(s"Using default Semigroup for String: $defaultSemigroup")

    // Combine using a custom semigroup instance
    println(s"Using custom Semigroup for String: $customSemigroup")

    // Combine a collection of String using a String Semigroup
    val collection = combineColectionOfStrings(Seq("john", "doe"))
    println(s"Using a collection: $collection")

    // Combine a collection of Strings using a String Semigroup
    val improved = combineColectionOfStringsImproved(Seq("john", "doe"))
    println(s"Using a collection: $improved")

    // use our generic function to combine the Strings
    // note how the caller now needs to supply the empty value and the semigroup instance for type A
    val genericStrings = combineCollection("", Seq("john", "doe"))(combineWithASpace)
    println(s"Using the generic combine method: $genericStrings")

    // let's try this with an Int using cats default instance and see what happens
    // note how we pull in the Semigroup instance for int
    import cats.instances.int._
    val combinedInts = combineCollection(0, Seq(1, 2, 3))
    println(s"Combined ints: $combinedInts")

  }

}
