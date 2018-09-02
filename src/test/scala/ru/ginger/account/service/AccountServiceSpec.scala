package ru.ginger.account.service

import java.time.LocalDate

import cats.data.Validated.Valid
import modules.TryCoreModule._
import org.scalatest.FlatSpec
import ru.ginger.account.dao.{AccountDao, OperationDao}
import ru.ginger.account.validation.AccountValidation
import utils.SpecBase
import cats.syntax.validated._

import scala.util.{Failure, Success, Try}
import cats.instances.try_._
import ru.ginger.account.model._
import ru.ginger.account.utils.instances.`try`._
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => calledWith, _}
import ru.ginger.account.exception.{AccountNotFoundException, DepositException, WithdrawalException}
import ru.ginger.account.validation.ValidationError.MaxAmountLimitPerOperationExceededError

class AccountServiceSpec extends FlatSpec with SpecBase {

  "get" should "return an account by id" in new TestModule {
    when(mockAccountDao.find(any())).thenReturn(Success(Some(sampleAccount)))

    service.get(sampleAccountId) shouldBe Success(sampleAccount)

    verify(mockAccountDao).find(calledWith(sampleAccountId))
  }

  it should "return a wrapped error if account has not been found by id" in new TestModule {
    when(mockAccountDao.find(any())).thenReturn(Success(None))

    val Failure(ex) = service.get(sampleAccountId)

    ex shouldBe a [AccountNotFoundException]

    verify(mockAccountDao).find(calledWith(sampleAccountId))
  }

  it should "return a wrapped error if dao returned error" in new TestModule {
    when(mockAccountDao.find(any())).thenReturn(Failure(new RuntimeException))

    val Failure(ex) = service.get(sampleAccountId)

    ex shouldBe a [RuntimeException]

    verify(mockAccountDao).find(calledWith(sampleAccountId))
  }

  "deposit" should "deposit money to the account" in new TestModule {
    when(mockAccountDao.find(any())).thenReturn(Success(Some(sampleAccount)))
    when(mockOperationDao.statistic(any(), any(), any())).thenReturn(Success(sampleAccountDayDepositStatistic))
    when(mockAccountValidation.validateDeposit(any(), any())).thenReturn(Valid(sampleDepositAmount))
    when(mockAccountDao.update(any(), any())).thenReturn(Success(1))
    when(mockOperationDao.insert(any())).thenReturn(Success(1))

    service.deposit(sampleAccountId, sampleDepositAmount) shouldBe Success(())

    verify(mockAccountDao).find(calledWith(sampleAccountId))
    verify(mockOperationDao).statistic(calledWith(sampleAccountId), calledWith(sampleOperationDate), calledWith(OperationType.Deposit))
    verify(mockAccountValidation).validateDeposit(calledWith(sampleAccountDayDepositStatistic), calledWith(sampleDepositAmount))
    verify(mockAccountDao).update(calledWith(sampleAccountId), calledWith(sampleAccountDepositUpdateDate))
    verify(mockOperationDao).insert(calledWith(sampleDepositOperation))
  }

  it should "return a wrapped error if validation has failed" in new TestModule {
    when(mockAccountDao.find(any())).thenReturn(Success(Some(sampleAccount)))
    when(mockOperationDao.statistic(any(), any(), any())).thenReturn(Success(sampleAccountDayDepositStatistic))
    when(mockAccountValidation.validateDeposit(any(), any())).thenReturn(MaxAmountLimitPerOperationExceededError.invalidNel)

    val Failure(ex) = service.deposit(sampleAccountId, sampleDepositAmount)
    ex shouldBe a [DepositException]

    verify(mockAccountDao).find(calledWith(sampleAccountId))
    verify(mockOperationDao).statistic(calledWith(sampleAccountId), calledWith(sampleOperationDate), calledWith(OperationType.Deposit))
    verify(mockAccountValidation).validateDeposit(calledWith(sampleAccountDayDepositStatistic), calledWith(sampleDepositAmount))
    verify(mockAccountDao, never()).update(any(), any())
    verify(mockOperationDao, never()).insert(any())
  }

  "withdraw" should "withdraw money from the account" in new TestModule {
    when(mockAccountDao.find(any())).thenReturn(Success(Some(sampleAccount)))
    when(mockOperationDao.statistic(any(), any(), any())).thenReturn(Success(sampleAccountDayWithdrawalStatistic))
    when(mockAccountValidation.validateWithdrawal(any(), any(), any())).thenReturn(Valid(sampleWithdrawalAmount))
    when(mockAccountDao.update(any(), any())).thenReturn(Success(1))
    when(mockOperationDao.insert(any())).thenReturn(Success(1))

    service.withdraw(sampleAccountId, sampleWithdrawalAmount) shouldBe Success(())

    verify(mockAccountDao).find(calledWith(sampleAccountId))
    verify(mockOperationDao).statistic(calledWith(sampleAccountId), calledWith(sampleOperationDate), calledWith(OperationType.Withdrawal))
    verify(mockAccountValidation).validateWithdrawal(calledWith(sampleAccountDayWithdrawalStatistic), calledWith(sampleWithdrawalAmount), calledWith(sampleAccountAmount))
    verify(mockAccountDao).update(calledWith(sampleAccountId), calledWith(sampleAccountWithdrawalUpdateDate))
    verify(mockOperationDao).insert(calledWith(sampleWithdrawalOperation))
  }

  it should "return a wrapped error if validation has failed" in new TestModule {
    when(mockAccountDao.find(any())).thenReturn(Success(Some(sampleAccount)))
    when(mockOperationDao.statistic(any(), any(), any())).thenReturn(Success(sampleAccountDayWithdrawalStatistic))
    when(mockAccountValidation.validateWithdrawal(any(), any(), any())).thenReturn(MaxAmountLimitPerOperationExceededError.invalidNel)
    when(mockAccountDao.update(any(), any())).thenReturn(Success(1))
    when(mockOperationDao.insert(any())).thenReturn(Success(1))

    val Failure(ex) = service.withdraw(sampleAccountId, sampleWithdrawalAmount)
    ex shouldBe a [WithdrawalException]

    verify(mockAccountDao).find(calledWith(sampleAccountId))
    verify(mockOperationDao).statistic(calledWith(sampleAccountId), calledWith(sampleOperationDate), calledWith(OperationType.Withdrawal))
    verify(mockAccountValidation).validateWithdrawal(calledWith(sampleAccountDayWithdrawalStatistic), calledWith(sampleWithdrawalAmount), calledWith(sampleAccountAmount))
    verify(mockAccountDao, never()).update(any(), any())
    verify(mockOperationDao, never()).insert(any())
  }

  private trait TestModule {
    protected val mockAccountDao: AccountDao[Try] = mock[AccountDao[Try]]
    protected val mockOperationDao: OperationDao[Try] = mock[OperationDao[Try]]
    protected val mockAccountValidation: AccountValidation = mock[AccountValidation]

    protected val service: AccountService[Try] = new AccountServiceImpl[Try, Try](
      mockAccountDao,
      mockOperationDao,
      mockAccountValidation,
      databaseRunner
    )
  }

  private val sampleAccountId = 1L
  private val sampleAccountName = "sampleAccountName"
  private val sampleAccountAmount = BigDecimal(1000)
  private val sampleWithdrawalAmount = BigDecimal(300)
  private val sampleDepositAmount = BigDecimal(900)
  private val sampleOperationDate = LocalDate.now()
  private val sampleDepositOperationCount = 1
  private val sampleWithdrawalOperationCount = 1

  private val sampleAccount: Account = Account(
    id = sampleAccountId,
    name = sampleAccountName,
    countOperations = sampleDepositOperationCount + sampleWithdrawalOperationCount,
    amount = sampleAccountAmount
  )

  private val sampleWithdrawalOperation: Operation = Operation(
    accountId = sampleAccountId,
    amount = sampleWithdrawalAmount,
    date = sampleOperationDate,
    `type` = OperationType.Withdrawal
  )

  private val sampleDepositOperation: Operation = Operation(
    accountId = sampleAccountId,
    amount = sampleDepositAmount,
    date = sampleOperationDate,
    `type` = OperationType.Deposit
  )

  private val sampleAccountWithdrawalUpdateDate = AccountUpdateData(sampleAccountAmount, sampleAccountAmount - sampleWithdrawalAmount, sampleDepositOperationCount + sampleWithdrawalOperationCount)
  private val sampleAccountDepositUpdateDate = AccountUpdateData(sampleAccountAmount, sampleAccountAmount + sampleDepositAmount, sampleDepositOperationCount + sampleWithdrawalOperationCount)

  private val sampleAccountDayDepositStatistic = AccountDayStatistic(sampleDepositOperationCount, sampleAccountAmount, OperationType.Deposit )
  private val sampleAccountDayWithdrawalStatistic = AccountDayStatistic(sampleWithdrawalOperationCount, sampleAccountAmount, OperationType.Withdrawal)
}