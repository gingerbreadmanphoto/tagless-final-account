package ru.ginger.account.utils.instances

import akka.http.scaladsl.server.Route
import play.api.libs.json.Writes
import ru.ginger.account.utils.controller.Response
import ru.ginger.account.utils.data.{Routable, Transactionable}

import scala.util.Try

trait TryInstances {
  implicit val responseTryRoutable: Routable[Try] = new Routable[Try] {
    override def route[X: Writes](f: Try[X]): Route = Response.tryToRoute(f)
  }

  implicit val tryTransactionalable: Transactionable[Try] = new Transactionable[Try] {
    override def transaction[X](f: Try[X]): Try[X] = f
  }
}