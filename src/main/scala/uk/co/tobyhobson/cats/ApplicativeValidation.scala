package uk.co.tobyhobson.cats

import cats.Applicative
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.instances.list._
import cats.syntax.validated._

/**
  * Illustrates using Applicatives to perform N validation operations independently
  * see https://www.tobyhobson.co.uk/applicatives-vs-monads/
  */
object ApplicativeValidation {

  type ValidationError = List[String]
  // Validated is a data type similar to Either but it accumulates errors unlike Either which can hold only
  // a single value on the left side
  type ErrorsOr[A] = Validated[ValidationError, A]

  def validate(key: String, value: String): ErrorsOr[String] = {
    if (value.nonEmpty)
      value.valid
    else
      List(s"$key is empty").invalid // we could return more than one error here if we like
  }

  def main(args: Array[String]): Unit = {
    val errorOrName = Applicative[ErrorsOr].map2(validate("first name", "john"), validate("last name", "doe"))(_ + " " + _)

    errorOrName match {
      case Invalid(errors) => println("errors: " + errors.mkString(", "))
      case Valid(fullName) => println(fullName)
    }
  }
}
