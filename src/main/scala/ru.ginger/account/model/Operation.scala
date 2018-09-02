package ru.ginger.account.model

import java.time.LocalDate
import ru.ginger.account.model.OperationType.OperationType

case class Operation(accountId: Long,
                     amount: BigDecimal,
                     date: LocalDate,
                     `type`: OperationType)

object OperationType extends Enumeration {
  type OperationType = Value

  val Withdrawal: Value = Value("WITHDRAWAL")
  val Deposit: Value = Value("DEPOSIT")
}