package ru.ginger.account.validation

abstract class ValidationError(val message: String)

object ValidationError {
  case object InsufficientFundsError extends ValidationError("Insufficient funds")
  case object NonPositiveAmountError extends ValidationError("Amount must be a positive number")
  case object MaxAmountLimitPerDayExceededError extends ValidationError("Max amount limit per day has been exceeded")
  case object MaxAmountLimitPerOperationExceededError extends ValidationError("Max amount limit per operation has been exceeded")
  case object AllowableOperationCountExceededError extends ValidationError("Allowable operation's count for a day has been exceeded")
}