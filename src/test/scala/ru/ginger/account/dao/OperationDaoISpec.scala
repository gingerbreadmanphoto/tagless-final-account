package ru.ginger.account.dao

import java.time.LocalDate

import com.dimafeng.testcontainers.{ForEachTestContainer, PostgreSQLContainer}
import modules.TestDatabaseModule
import org.scalatest.FlatSpec
import ru.ginger.account.model.{Account, AccountDayStatistic, Operation, OperationType}
import ru.ginger.account.utils.database.Database.IO
import utils.SpecBase
import scala.concurrent.ExecutionContext.Implicits.global

class OperationDaoISpec extends FlatSpec with SpecBase with ForEachTestContainer {
  override val container: PostgreSQLContainer = PostgreSQLContainer()

  "insert" should "insert new operation" in new TestModule {
    initSchema()

    whenReady(databaseRunner.run(dao.insert(sampleDepositOperation).zip(list(sampleAccountId)))) { result =>
      val (countInserted, Seq(operation)) = result

      countInserted shouldBe 1
      operation shouldBe sampleDepositOperation
    }
  }

  "list" should "return only present day operations" in new TestModule {
    val today: LocalDate = LocalDate.now()
    initSchema()
    insertOperations(Seq(sampleDepositOperation, sampleWithdrawalOperation.copy(date = today.minusDays(2))))

    whenReady(databaseRunner.run(dao.list(sampleAccountId, today))) { result =>
      result should contain theSameElementsAs Seq(sampleDepositOperation)
    }
  }

  "statistic" should "return statistic for account by deposit operations" in new TestModule {
    val today: LocalDate = LocalDate.now()

    initSchema()
    insertOperations(
      Seq(
        sampleDepositOperation,
        sampleDepositOperation,
        sampleDepositOperation.copy(date = today.minusDays(2)),
        sampleWithdrawalOperation,
        sampleWithdrawalOperation,
        sampleWithdrawalOperation.copy(date = today.minusDays(2))
      )
    )

    whenReady(databaseRunner.run(dao.statistic(sampleAccountId, today, OperationType.Deposit))) { result =>
      result shouldBe AccountDayStatistic(
        2,
        sampleDepositOperation.amount * 2,
        OperationType.Deposit
      )
    }
  }

  "statistic" should "return statistic for account by withdrawal operations" in new TestModule {
    val today: LocalDate = LocalDate.now()

    initSchema()
    insertOperations(
      Seq(
        sampleDepositOperation,
        sampleDepositOperation,
        sampleDepositOperation.copy(date = today.minusDays(2)),
        sampleWithdrawalOperation,
        sampleWithdrawalOperation,
        sampleWithdrawalOperation.copy(date = today.minusDays(2))
      )
    )

    whenReady(databaseRunner.run(dao.statistic(sampleAccountId, today, OperationType.Withdrawal))) { result =>
      result shouldBe AccountDayStatistic(
        2,
        sampleWithdrawalOperation.amount * 2,
        OperationType.Withdrawal
      )
    }
  }

  private trait TestModule extends TestDatabaseModule {

    import profile.api._

    override protected val testContainer: PostgreSQLContainer = container
    protected val dao: OperationDao[IO] = new OperationDaoImpl(database)

    protected def list(accountId: Long): IO[Seq[Operation]] = {
      databaseModel.operations.filter(_.accountId === accountId.bind).result
    }
  }

  private val sampleAccountId = 1L
  private val sampleAccountName = "sampleAccountName"
  private val sampleAccountAmount = BigDecimal(1000)
  private val sampleWithdrawalAmount = BigDecimal(300)
  private val sampleDepositAmount = BigDecimal(900)
  private val sampleOperationDate = LocalDate.now()
  private val sampleDepositOperationCount = 1
  private val sampleWithdrawalOperationCount = 1
  private val sampleAmountDelta = BigDecimal(300)

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
}
