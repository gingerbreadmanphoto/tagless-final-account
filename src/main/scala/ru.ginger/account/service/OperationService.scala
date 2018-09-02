package ru.ginger.account.service

import java.time.LocalDate
import cats.Monad
import ru.ginger.account.dao.OperationDao
import ru.ginger.account.model.Operation
import ru.ginger.account.utils.database.Runner
import scala.language.higherKinds

trait OperationService[F[_]] {
  def list(accountId: Long, date: LocalDate): F[Seq[Operation]]
}

class OperationServiceImpl[F[_]: Monad, DB[_]: Monad](operationDao: OperationDao[DB],
                                                      runner: Runner[DB, F]) extends OperationService[F] {

  override def list(accountId: Long, date: LocalDate): F[Seq[Operation]] = {
    runner.run(operationDao.list(accountId, date))
  }
}