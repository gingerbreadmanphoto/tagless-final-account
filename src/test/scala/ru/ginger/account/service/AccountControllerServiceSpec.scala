package ru.ginger.account.service

import java.time.LocalDate
import org.scalatest.FlatSpec
import utils.SpecBase
import scala.util.{Success, Try}
import cats.instances.try_._
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => calledWith, _}
import ru.ginger.account.model.{Account, Operation, OperationType}
import ru.ginger.account.protocol.{AccountView, DepositRequest, OperationView, WithdrawalRequest}

class AccountControllerServiceSpec extends FlatSpec with SpecBase {

  "get" should "return account with operations by id" in new TestModule {
    when(mockAccountService.get(any())).thenReturn(Success(sampleAccount))
    when(mockOperationService.list(any(), any())).thenReturn(Success(Seq(sampleWithdrawalOperation, sampleDepositOperation)))

    service.get(sampleAccountId, withOperations = true) shouldBe Success(sampleAccountViewWithOperations)

    verify(mockAccountService).get(calledWith(sampleAccountId))
    verify(mockOperationService).list(calledWith(sampleAccountId), calledWith(LocalDate.now()))
  }

  it should "return account without operations by id" in new TestModule {
    when(mockAccountService.get(any())).thenReturn(Success(sampleAccount))

    service.get(sampleAccountId, withOperations = false) shouldBe Success(sampleAccountViewWithoutOperations)

    verify(mockAccountService).get(calledWith(sampleAccountId))
    verify(mockOperationService, never()).list(any(), any())
  }

  "deposit" should "deposit money to the account" in new TestModule {
    when(mockAccountService.deposit(any(), any())).thenReturn(Success(()))

    service.deposit(sampleAccountId, sampleDepositRequest) shouldBe Success(())

    verify(mockAccountService).deposit(calledWith(sampleAccountId), calledWith(sampleDepositAmount))
  }

  "withdraw" should "withdraw money from the account" in new TestModule {
    when(mockAccountService.withdraw(any(), any())).thenReturn(Success(()))

    service.withdraw(sampleAccountId, sampleWithdrawalRequest) shouldBe Success(())

    verify(mockAccountService).withdraw(calledWith(sampleAccountId), calledWith(sampleWidrawalAmount))
  }

  private trait TestModule {
    protected val mockAccountService: AccountService[Try] = mock[AccountService[Try]]
    protected val mockOperationService: OperationService[Try] = mock[OperationService[Try]]

    protected val service: AccountControllerService[Try] = new AccountControllerServiceImpl[Try](
      mockAccountService,
      mockOperationService
    )
  }

  private val sampleAccountId = 1L
  private val sampleAccountName = "sampleAccountName"
  private val sampleAccountAmount = BigDecimal(1000)
  private val sampleWidrawalAmount = BigDecimal(500)
  private val sampleDepositAmount = BigDecimal(1500)
  private val sampleOperationDate = LocalDate.now()
  private val sampleCountOperations = 2

  private val sampleAccount: Account = Account(
    id = sampleAccountId,
    name = sampleAccountName,
    countOperations = sampleCountOperations,
    amount = sampleAccountAmount
  )

  private val sampleWithdrawalOperation: Operation = Operation(
    accountId = sampleAccountId,
    amount = sampleWidrawalAmount,
    date = sampleOperationDate,
    `type` = OperationType.Withdrawal
  )

  private val sampleDepositOperation: Operation = Operation(
    accountId = sampleAccountId,
    amount = sampleDepositAmount,
    date = sampleOperationDate,
    `type` = OperationType.Deposit
  )

  private val sampleWithdrawalOperationView: OperationView = OperationView(
    amount = sampleWidrawalAmount,
    date = sampleOperationDate,
    `type` = OperationType.Withdrawal
  )

  private val sampleDepositOperationView: OperationView = OperationView(
    amount = sampleDepositAmount,
    date = sampleOperationDate,
    `type` = OperationType.Deposit
  )

  private val sampleAccountViewWithOperations: AccountView = AccountView(
    name = sampleAccountName,
    amount = sampleAccountAmount,
    presentDayOperation = Seq(
      sampleWithdrawalOperationView,
      sampleDepositOperationView
    )
  )

  private val sampleAccountViewWithoutOperations: AccountView = AccountView(
    name = sampleAccountName,
    amount = sampleAccountAmount,
    presentDayOperation = Seq.empty
  )

  private val sampleWithdrawalRequest = WithdrawalRequest(sampleWidrawalAmount)
  private val sampleDepositRequest = DepositRequest(sampleDepositAmount)
}