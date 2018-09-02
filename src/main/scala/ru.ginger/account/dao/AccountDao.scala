package ru.ginger.account.dao

import ru.ginger.account.exception.OperationException
import ru.ginger.account.model.db.AccountModel
import ru.ginger.account.model.{Account, AccountUpdateData}
import ru.ginger.account.utils.database.Database
import ru.ginger.account.utils.database.Database.IO
import slick.dbio.DBIOAction

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait AccountDao[F[_]] {
  def find(id: Long): F[Option[Account]]
  def update(id: Long, data: AccountUpdateData): F[Int]
}

class AccountDaoImpl(database: Database[AccountModel])
                    (implicit ec: ExecutionContext) extends AccountDao[IO] {

  import database.model.profile.api._
  import database.model

  override def find(id: Long): IO[Option[Account]] = {
    model.accounts
      .filter(_.id === id.bind)
      .result
      .map(_.headOption)
  }

  override def update(id: Long, data: AccountUpdateData): IO[Int] = {
    model.accounts
      .filter(_.id === id.bind)
      .filter(_.amount === data.oldAmount.bind)
      .filter(_.countOperations === data.oldCount.bind)
      .map(account => (account.amount, account.countOperations))
      .update(data.newAmount, data.oldCount + 1)
      .flatMap {
        case 0 => IO.failed(new OperationException)
        case count => IO.successful(count)
      }
  }
}