package ru.ginger.account.exception

import cats.data.NonEmptyList
import ru.ginger.account.validation.ValidationError

trait ValidationException {
  def errors: NonEmptyList[ValidationError]
}