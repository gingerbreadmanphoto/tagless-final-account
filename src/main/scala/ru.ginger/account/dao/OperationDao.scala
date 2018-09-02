package ru.ginger.account.dao

import java.time.LocalDate

import ru.ginger.account.model.OperationType.OperationType
import ru.ginger.account.model.{AccountDayStatistic, Operation, OperationType}
import ru.ginger.account.model.db.OperationModel
import ru.ginger.account.utils.database.Database
import ru.ginger.account.utils.database.Database.IO

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait OperationDao[F[_]] {
  def list(accountId: Long, date: LocalDate): F[Seq[Operation]]
  def statistic(accountId: Long, date: LocalDate, `type`: OperationType): F[AccountDayStatistic]
  def insert(o: Operation): F[Int]
}

class OperationDaoImpl(database: Database[OperationModel])
                      (implicit ec: ExecutionContext) extends OperationDao[IO] {

  import database.model.profile.api._
  import database.model
  import database.model._

  override def list(accountId: Long, date: LocalDate): IO[Seq[Operation]] = {
    model.operations
      .filter(_.accountId === accountId.bind)
      .filter(_.date === date.bind)
      .result
  }

  override def statistic(accountId: Long, date: LocalDate, `type`: OperationType): IO[AccountDayStatistic] = {

    val query = model.operations
      .filter(_.accountId === accountId.bind)
      .filter(_.date === date.bind)
      .filter(_.`type` === `type`.bind)

    val countQuery = query.size.result
    val amountQuery = query.map(_.amount).sum.getOrElse(BigDecimal(0)).result

    countQuery.zip(amountQuery).map { case (count, amount) => AccountDayStatistic(count, amount, `type`) }
  }

  override def insert(o: Operation): IO[Int] = {
    model.operations += o
  }
}