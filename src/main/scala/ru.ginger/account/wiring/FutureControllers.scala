package ru.ginger.account.wiring

import ru.ginger.account.controller.AccountController
import ru.ginger.account.utils.controller.Controller
import FutureAccountModule._
import scala.concurrent.Future
import ru.ginger.account.utils.instances.future._

object FutureControllers {
  lazy val controllers: Seq[Controller] = Seq(
    new AccountController[Future](accountControllerService)
  )
}