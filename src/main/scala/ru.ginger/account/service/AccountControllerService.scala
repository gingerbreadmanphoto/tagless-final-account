package ru.ginger.account.service

import java.time.LocalDate
import cats.Monad
import ru.ginger.account.model.Account
import ru.ginger.account.protocol.{AccountView, DepositRequest, WithdrawalRequest}
import ru.ginger.account.utils.syntax.convertable._
import ru.ginger.account.utils.instances.account._
import ru.ginger.account.utils.instances.operation._
import cats.syntax.flatMap._
import cats.syntax.functor._
import scala.language.higherKinds

trait AccountControllerService[F[_]] {
  def get(id: Long, withOperations: Boolean): F[AccountView]
  def withdraw(id: Long, request: WithdrawalRequest): F[Unit]
  def deposit(id: Long, request: DepositRequest): F[Unit]
}

class AccountControllerServiceImpl[F[_]](accountService: AccountService[F],
                                         operationService: OperationService[F])
                                         (implicit fMonad: Monad[F])extends AccountControllerService[F] {

  override def get(id: Long, withOperations: Boolean): F[AccountView] = {
    def enrichWithOperations(account: Account): F[AccountView] = {
      operationService.list(id, LocalDate.now()).map(operations =>
        account.convert.apply(operations.map(_.convert))
      )
    }

    accountService.get(id).flatMap {
      case account if withOperations => enrichWithOperations(account)
      case account => fMonad.pure(account.convert.apply(Seq.empty))
    }
  }

  override def withdraw(id: Long,
                        request: WithdrawalRequest): F[Unit] = {
    accountService.withdraw(id, request.amount)
  }

  override def deposit(id: Long,
                       request: DepositRequest): F[Unit] = {
    accountService.deposit(id, request.amount)
  }
}