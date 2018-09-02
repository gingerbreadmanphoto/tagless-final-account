package ru.ginger.account.dao

import java.time.LocalDate

import com.dimafeng.testcontainers.{Container, ForEachTestContainer, PostgreSQLContainer}
import modules.TestDatabaseModule
import org.scalatest.FlatSpec
import ru.ginger.account.exception.OperationException
import ru.ginger.account.model.{Account, AccountUpdateData, Operation, OperationType}
import ru.ginger.account.utils.database.Database.IO
import utils.SpecBase

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class AccountDaoISpec extends FlatSpec with SpecBase with ForEachTestContainer {

  override val container: PostgreSQLContainer = PostgreSQLContainer()

  "find" should "return account by id" in new TestModule {
    initSchema()
    insertAccount(sampleAccount)

    whenReady(databaseRunner.run(dao.find(sampleAccountId))) { result =>
      result shouldBe Some(sampleAccount)
    }
  }

  it should "return None if account not found" in new TestModule {
    initSchema()
    insertAccount(sampleAccount)

    whenReady(databaseRunner.run(dao.find(sampleAccountId + 1))) { result =>
      result shouldBe None
    }
  }

  "update" should "update account" in new TestModule {
    initSchema()
    insertAccount(sampleAccount)

    whenReady {
      databaseRunner.run(dao.update(sampleAccountId, sampleAccountUpdateData).zip(find(sampleAccountId)))
    } { result =>

      val (countUpdated, Some(account)) = result

      countUpdated shouldBe 1
      account shouldBe sampleUpdatedAccount
    }
  }

  it should "throw an exception if account amount has been changed by other request" in new TestModule {
    initSchema()
    insertAccount(sampleAccount)

    whenReady {
      databaseRunner.run(dao.update(sampleAccountId, sampleAccountUpdateDataFailedByAmount)).failed
    } { result =>
      result shouldBe a [OperationException]
    }
  }

  it should "throw an exception if account count operations has been changed by other request" in new TestModule {
    initSchema()
    insertAccount(sampleAccount)

    whenReady {
      databaseRunner.run(dao.update(sampleAccountId, sampleAccountUpdateDataFailedByCount)).failed
    } { result =>
      result shouldBe a [OperationException]
    }
  }

  private trait TestModule extends TestDatabaseModule {

    import profile.api._

    override protected val testContainer: PostgreSQLContainer = container
    protected val dao: AccountDao[IO] = new AccountDaoImpl(database)

    protected def find(id: Long): IO[Option[Account]] = databaseModel.accounts.filter(_.id === id.bind).result.map(_.headOption)
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

  private val sampleAccountUpdateData: AccountUpdateData = AccountUpdateData(
    sampleAccountAmount,
    sampleAccountAmount + sampleAmountDelta,
    sampleDepositOperationCount + sampleWithdrawalOperationCount
  )

  private val sampleAccountUpdateDataFailedByAmount: AccountUpdateData = AccountUpdateData(
    sampleAccountAmount + 1,
    sampleAccountAmount + sampleAmountDelta,
    sampleDepositOperationCount + sampleWithdrawalOperationCount
  )

  private val sampleAccountUpdateDataFailedByCount: AccountUpdateData = AccountUpdateData(
    sampleAccountAmount,
    sampleAccountAmount + sampleAmountDelta,
    sampleDepositOperationCount + sampleWithdrawalOperationCount + 1
  )

  private val sampleUpdatedAccount = sampleAccount.copy(
    amount = sampleAccountAmount + sampleAmountDelta,
    countOperations = sampleDepositOperationCount + sampleWithdrawalOperationCount + 1
  )
}