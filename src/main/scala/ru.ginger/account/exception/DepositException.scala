package ru.ginger.account.exception

import cats.data.NonEmptyList
import ru.ginger.account.validation.ValidationError

class DepositException(override val errors: NonEmptyList[ValidationError])
  extends ApplicationException("Deposit error")
    with ValidationException