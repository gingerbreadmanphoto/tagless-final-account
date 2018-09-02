package ru.ginger.account.exception

import cats.data.NonEmptyList
import ru.ginger.account.validation.ValidationError

class WithdrawalException(override val errors: NonEmptyList[ValidationError])
  extends ApplicationException("Withdrawal error")
    with ValidationException