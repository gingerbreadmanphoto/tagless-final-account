package ru.ginger.account.wiring

import FutureCoreModule._
import ru.ginger.account.dao.{OperationDao, OperationDaoImpl}
import ru.ginger.account.service.{OperationService, OperationServiceImpl}
import ru.ginger.account.utils.database.Database.IO
import scala.concurrent.Future
import cats.instances.future._
import ru.ginger.account.utils.instances.dbio._

object FutureOperationModule {
  lazy val operationDao: OperationDao[IO] = new OperationDaoImpl(database)
  lazy val operationService: OperationService[Future] = new OperationServiceImpl(operationDao, databaseRunner)
}