package ru.ginger.account.validation

import java.time.LocalDate
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.FlatSpec
import utils.SpecBase
import modules.TryCoreModule._
import ru.ginger.account.model.{Account, AccountDayStatistic, OperationType}
import ru.ginger.account.validation.ValidationError.{AllowableOperationCountExceededError, _}

class AccountValidationSpec extends FlatSpec with SpecBase {

  "validateDeposit" should "return error if max count of operations has been exceeded" in new TestModule {
    val Invalid(errors) = service.validateDeposit(
      nonExceededDepositStatistic.copy(count = exceededDepositOperationCount),
      delta / 2
    )

    errors.toList should contain theSameElementsAs Seq(AllowableOperationCountExceededError)
  }

  it should "return error if max amount per day has been exceeded" in new TestModule {
    val Invalid(errors) = service.validateDeposit(
      nonExceededDepositStatistic.copy(amount = accountConfiguration.depositMaxAmountLimitPerDay),
      1
    )

    errors.toList should contain theSameElementsAs Seq(MaxAmountLimitPerDayExceededError)
  }

  it should "return error if max amount per operation has been exceeded" in new TestModule {
    val Invalid(errors) = service.validateDeposit(
      nonExceededDepositStatistic.copy(amount = 0),
      exceededDepositAmountPerOperation
    )

    errors.toList should contain theSameElementsAs Seq(MaxAmountLimitPerOperationExceededError)
  }

  it should "return error if amount is not a positive number" in new TestModule {
    val Invalid(errors) = service.validateDeposit(
      nonExceededDepositStatistic.copy(amount = 0),
      -1
    )

    errors.toList should contain theSameElementsAs Seq(NonPositiveAmountError)
  }

  it should "return the valid deposit amount" in new TestModule {
    val Valid(amount) = service.validateDeposit(
      nonExceededDepositStatistic,
      delta / 2
    )

    amount shouldBe delta / 2
  }

  "validateWithdrawal" should "return error if max count of operations has been exceeded" in new TestModule {
    val Invalid(errors) = service.validateWithdrawal(
      nonExceededWithdrawalStatistic.copy(count = exceededWithdrawalOperationCount),
      delta / 2,
      accountConfiguration.withdrawalMaxAmountLimitPerDay
    )

    errors.toList should contain theSameElementsAs Seq(AllowableOperationCountExceededError)
  }

  it should "return error if max amount per day has been exceeded" in new TestModule {
    val Invalid(errors) = service.validateWithdrawal(
      nonExceededWithdrawalStatistic.copy(amount = accountConfiguration.withdrawalMaxAmountLimitPerDay),
      delta / 2,
      accountConfiguration.withdrawalMaxAmountLimitPerDay
    )

    errors.toList should contain theSameElementsAs Seq(MaxAmountLimitPerDayExceededError)
  }

  it should "return error if max amount per operation has been exceeded" in new TestModule {
    val Invalid(errors) = service.validateWithdrawal(
      nonExceededWithdrawalStatistic.copy(amount = 0),
      exceededWithdrawalAmountPerOperation,
      accountConfiguration.withdrawalMaxAmountLimitPerDay
    )

    errors.toList should contain theSameElementsAs Seq(MaxAmountLimitPerOperationExceededError)
  }

  it should "return error there is not enough money on the account" in new TestModule {
    val Invalid(errors) = service.validateWithdrawal(
      nonExceededWithdrawalStatistic.copy(amount = 0),
      1,
      0
    )

    errors.toList should contain theSameElementsAs Seq(InsufficientFundsError)
  }

  it should "return error if amount is not a positive number" in new TestModule {
    val Invalid(errors) = service.validateWithdrawal(
      nonExceededWithdrawalStatistic.copy(amount = 0),
      -1,
      accountConfiguration.withdrawalMaxAmountLimitPerDay
    )

    errors.toList should contain theSameElementsAs Seq(NonPositiveAmountError)
  }

  it should "return the valid deposit amount" in new TestModule {
    val Valid(amount) = service.validateWithdrawal(
      nonExceededWithdrawalStatistic,
      delta / 2,
      accountConfiguration.withdrawalMaxAmountLimitPerDay
    )

    amount shouldBe delta / 2
  }

  private trait TestModule {
    protected val service: AccountValidation = new AccountValidationImpl(accountConfiguration) // assume we always rely on reference.conf limits
  }

  private val sampleAccountId = 1L
  private val sampleAccountName = "sampleAccountName"
  private val sampleAccountAmount = BigDecimal(1000)
  private val sampleWithdrawalAmount = BigDecimal(500)
  private val sampleDepositAmount = BigDecimal(1500)
  private val sampleOperationDate = LocalDate.now()
  private val sampleDepositOperationCount = 1
  private val sampleWithdrawalOperationCount = 1

  private val sampleAccount: Account = Account(
    id = sampleAccountId,
    name = sampleAccountName,
    countOperations = sampleDepositOperationCount + sampleWithdrawalOperationCount,
    amount = sampleAccountAmount
  )

  private val exceededDepositOperationCount = accountConfiguration.depositAllowableOperationCount + 1
  private val exceededWithdrawalOperationCount = accountConfiguration.withdrawalAllowableOperationCount + 1

  private val exceededDepositAmountPerOperation = accountConfiguration.depositMaxAmountLimitPerOperation + 1
  private val exceededWithdrawalAmountPerOperation = accountConfiguration.withdrawalMaxAmountLimitPerOperation + 1

  private val delta = 1000

  private val nonExceededDepositStatistic = AccountDayStatistic(
    accountConfiguration.depositAllowableOperationCount - 1,
    accountConfiguration.depositMaxAmountLimitPerDay - delta,
    OperationType.Deposit
  )

  private val nonExceededWithdrawalStatistic = AccountDayStatistic(
    accountConfiguration.withdrawalAllowableOperationCount - 1,
    accountConfiguration.withdrawalMaxAmountLimitPerDay - delta,
    OperationType.Withdrawal
  )
}