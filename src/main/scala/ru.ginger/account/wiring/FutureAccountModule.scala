package ru.ginger.account.wiring

import FutureCoreModule._
import FutureOperationModule._
import ru.ginger.account.dao.{AccountDao, AccountDaoImpl}
import ru.ginger.account.service.{AccountControllerService, AccountControllerServiceImpl, AccountService, AccountServiceImpl}
import ru.ginger.account.utils.database.Database.IO
import ru.ginger.account.validation.{AccountValidation, AccountValidationImpl}
import scala.concurrent.Future
import cats.instances.future._
import ru.ginger.account.utils.instances.dbio._

object FutureAccountModule {
  lazy val accountValidation: AccountValidation = new AccountValidationImpl(accountConfiguration)
  lazy val accountDao: AccountDao[IO] = new AccountDaoImpl(database)
  lazy val accountService: AccountService[Future] = new AccountServiceImpl(accountDao, operationDao, accountValidation, databaseRunner)
  lazy val accountControllerService: AccountControllerService[Future] = new AccountControllerServiceImpl(accountService, operationService)
}