package ru.ginger.account.validation

import cats.Applicative
import ru.ginger.account.configuration.AccountConfiguration
import ru.ginger.account.model.AccountDayStatistic
import ru.ginger.account.validation.ValidationError._

trait AccountValidation {
  def validateWithdrawal(statistic: AccountDayStatistic,
                         requestAmount: BigDecimal,
                         accountAmount: BigDecimal): ValidResult[BigDecimal]

  def validateDeposit(statistic: AccountDayStatistic,
                      requestAmount: BigDecimal): ValidResult[BigDecimal]
}

class AccountValidationImpl(accountConfiguration: AccountConfiguration) extends AccountValidation {

  import accountConfiguration._

  override def validateWithdrawal(statistic: AccountDayStatistic,
                                  requestAmount: BigDecimal,
                                  accountAmount: BigDecimal): ValidResult[BigDecimal] = {

    val validAccountAmount: ValidResult[BigDecimal] = valueShouldBeLessOrEq(requestAmount)(accountAmount)(InsufficientFundsError)
    val validRequestAmount: ValidResult[BigDecimal] =
      valueShouldBePositive(requestAmount)(NonPositiveAmountError).andThen(positiveAmount =>
        valueShouldBeLessOrEq(positiveAmount)(withdrawalMaxAmountLimitPerOperation)(MaxAmountLimitPerOperationExceededError).andThen( amount =>
          valueShouldBeLessOrEq(statistic.amount + amount)(withdrawalMaxAmountLimitPerDay)(MaxAmountLimitPerDayExceededError)
        )
      )
    val validCount: ValidResult[Int] = valueShouldBeLessOrEq(statistic.count + 1)(withdrawalAllowableOperationCount)(AllowableOperationCountExceededError)

    Applicative[ValidResult].map3(
      validAccountAmount,
      validRequestAmount,
      validCount
    )((_, _, _) => requestAmount)
  }

  override def validateDeposit(statistic: AccountDayStatistic,
                               requestAmount: BigDecimal): ValidResult[BigDecimal] = {

    val validRequestAmount: ValidResult[BigDecimal] =
      valueShouldBePositive(requestAmount)(NonPositiveAmountError).andThen( positiveAmount =>
        valueShouldBeLessOrEq(positiveAmount)(depositMaxAmountLimitPerOperation)(MaxAmountLimitPerOperationExceededError).andThen( amount =>
          valueShouldBeLessOrEq(statistic.amount + amount)(depositMaxAmountLimitPerDay)(MaxAmountLimitPerDayExceededError)
        )
      )
    val validCount: ValidResult[Int] = valueShouldBeLessOrEq(statistic.count + 1)(depositAllowableOperationCount)(AllowableOperationCountExceededError)

    Applicative[ValidResult].map2(
      validRequestAmount,
      validCount
    )((_, _) => requestAmount)
  }
}