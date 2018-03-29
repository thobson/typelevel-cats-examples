package uk.co.tobyhobson

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.syntax.validated._
import cats.{Applicative, Semigroupal}

/**
  * Examples illustrating the use of the Validated and ValidatedNel data types along with Applicative from the Cats library.
  * see https://www.tobyhobson.co.uk/cats-validated/
  */
object ValidatedNels {

  // Define our error type, here I've just used a type alias but it could be a sealed trait hierarchy
  type Error = String

  /**
    * Another type alias. I've used this for two reasons:
    *
    * 1. It makes the code a little more readable
    * 2. When we summon an instance of Applicative (see below) we need a type of the form F[_],
    *    but ValidatedNel (like either) is actually F[_, _] ( F[Errors, Result] )
    *    the type alias allows us to fix the first type (the error) so we get F[Result] == F[_]
    */
  type ErrorsOr[A] = ValidatedNel[Error, A]

  def validate(key: String, value: String): ErrorsOr[String] = {
    if (value.nonEmpty)
      // Conceptually similar to Right(value). We could also use cats.data.validated.Valid(value) here
      value.validNel
    else
      s"$key is empty".invalidNel
  }

  def main(args: Array[String]): Unit = {
    // Semigroupal/Cartesian is a simplified version of Applicative which includes the product() call
    // product() takes ErrorsOr[String] (first name) + ErrorsOr[String] (last name) to produce ErrorsOr[(String, String)] (first, last)
    val _: ErrorsOr[(String, String)] = Semigroupal[ErrorsOr].product(validate("first name", "john"), validate("last name", "doe"))

    // we can use the familiar Applicative.map2() call
    val errorOrName = Applicative[ErrorsOr].map2(validate("first name", "john"), validate("last name", "doe"))(_ + " " + _)

    errorOrName match {
      case Invalid(errors) => println("errors: " + errors.toList.mkString(", "))
      case Valid(fullName) => println(fullName)
    }
  }

}
