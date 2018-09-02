package ru.ginger.account

import cats.data.ValidatedNel
import cats.syntax.validated._

import scala.math.ScalaNumber

package object validation {
  type ValidResult[X] = ValidatedNel[ValidationError, X]

  def ensure[X](value: X)(cond: X => Boolean)(error: => ValidationError): ValidResult[X] = {
    if (cond(value)) {
      value.validNel
    } else {
      error.invalidNel
    }
  }

  def valueShouldBeLessOrEq[X: Ordering](value: X)(compareValue: X)(error: => ValidationError): ValidResult[X] = {
    val ord = implicitly[Ordering[X]]
    ensure(value)(ord.lteq(_, compareValue))(error)
  }

  def valueShouldBePositive(value: BigDecimal)(error: ValidationError): ValidResult[BigDecimal] = {
    ensure(value)(_ > 0)(error)
  }
}