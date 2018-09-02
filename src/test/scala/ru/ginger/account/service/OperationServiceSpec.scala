package ru.ginger.account.service

import java.time.LocalDate
import org.scalatest.FlatSpec
import ru.ginger.account.dao.OperationDao
import utils.SpecBase
import modules.TryCoreModule._
import scala.util.{Success, Try}
import cats.instances.try_._
import ru.ginger.account.model.{Operation, OperationType}
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => calledWith, _}

class OperationServiceSpec extends FlatSpec with SpecBase {

  "list" should "return list of operations by accountId" in new TestModule {
    when(mockOperationDao.list(any(), any())).thenReturn(Success(Seq(sampleWithdrawalOperation, sampleDepositOperation)))

    service.list(sampleAccountId, sampleOperationDate) shouldBe Success(Seq(sampleWithdrawalOperation, sampleDepositOperation))

    verify(mockOperationDao).list(
      calledWith(sampleAccountId),
      calledWith(sampleOperationDate)
    )
  }

  private trait TestModule {
    protected val mockOperationDao: OperationDao[Try] = mock[OperationDao[Try]]

    protected val service: OperationService[Try] = new OperationServiceImpl[Try, Try](
      mockOperationDao,
      databaseRunner
    )
  }

  private val sampleAccountId = 1L
  private val sampleWidrawalAmount = BigDecimal(500)
  private val sampleDepositAmount = BigDecimal(1500)
  private val sampleOperationDate = LocalDate.now()

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
}