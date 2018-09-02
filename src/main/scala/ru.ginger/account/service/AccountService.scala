package ru.ginger.account.service

import java.time.LocalDate
import cats.{Monad, MonadError}
import cats.data.Validated.{Invalid, Valid}
import ru.ginger.account.dao.{AccountDao, OperationDao}
import ru.ginger.account.exception.{AccountNotFoundException, DepositException, WithdrawalException}
import ru.ginger.account.model._
import ru.ginger.account.validation.AccountValidation
import cats.syntax.flatMap._
import cats.syntax.functor._
import ru.ginger.account.utils.data.Transactionable
import ru.ginger.account.utils.database.Runner
import ru.ginger.account.utils.syntax.transactionable._
import scala.language.higherKinds

trait AccountService[F[_]] {
  def get(id: Long): F[Account]
  def withdraw(id: Long, requestAmount: BigDecimal): F[Unit]
  def deposit(id: Long, requestAmount: BigDecimal): F[Unit]
}

class AccountServiceImpl[F[_]: Monad, DB[_]: Monad](accountDao: AccountDao[DB],
                                                 operationDao: OperationDao[DB],
                                                 accountValidation: AccountValidation,
                                                 runner: Runner[DB, F])
                                                 (implicit
                                                  fMonadError: MonadError[F, Throwable],
                                                  transactionable: Transactionable[DB]) extends AccountService[F] {

  override def get(id: Long): F[Account] = {
    runner.run(accountDao.find(id)).flatMap {
      case Some(account) => fMonadError.pure(account)
      case None => fMonadError.raiseError(new AccountNotFoundException)
    }
  }

  override def withdraw(id: Long, requestAmount: BigDecimal): F[Unit] = {
    val today = LocalDate.now()

    def updateBalance(account: Account, amount: BigDecimal, statistic: AccountDayStatistic): F[Unit] = {
      runner.run {
        (
          for {
            _ <- accountDao.update(id, AccountUpdateData(account.amount, account.amount - amount, account.countOperations))
            _ <- operationDao.insert(Operation(id, amount, today, OperationType.Withdrawal))
          } yield ()
        ).transaction
      }
    }

    for {
      account <- get(id)
      statistic <- runner.run(operationDao.statistic(id, today, OperationType.Withdrawal))
      validatedAmount = accountValidation.validateWithdrawal(statistic, requestAmount, account.amount)
      _ <- validatedAmount match {
        case Valid(value) => updateBalance(account, value, statistic)
        case Invalid(errors) => fMonadError.raiseError(new WithdrawalException(errors))
      }
    } yield ()
  }

  override def deposit(id: Long, requestAmount: BigDecimal): F[Unit] = {
    val today = LocalDate.now()

    def updateBalance(account: Account, amount: BigDecimal, statistic: AccountDayStatistic): F[Unit] = {
      runner.run {
        (
          for {
            _ <- accountDao.update(id, AccountUpdateData(account.amount, account.amount + amount, account.countOperations))
            _ <- operationDao.insert(Operation(id, amount, today, OperationType.Deposit))
          } yield ()
        ).transaction
      }
    }

    for {
      account <- get(id)
      statistic <- runner.run(operationDao.statistic(id, today, OperationType.Deposit))
      validatedAmount = accountValidation.validateDeposit(statistic, requestAmount)
      _ <- validatedAmount match {
        case Valid(value) => updateBalance(account, value, statistic)
        case Invalid(errors) => fMonadError.raiseError(new DepositException(errors))
      }
    } yield ()
  }
}