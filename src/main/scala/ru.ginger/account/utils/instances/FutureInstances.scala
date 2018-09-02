package ru.ginger.account.utils.instances

import akka.http.scaladsl.server.{Directives, Route}
import play.api.libs.json.{Json, Writes}
import ru.ginger.account.utils.controller.Response
import ru.ginger.account.utils.data.Routable
import Directives._
import scala.concurrent.Future

trait FutureInstances {
  implicit val responseFutureRoutable: Routable[Future] = new Routable[Future] {
    override def route[X: Writes](f: Future[X]): Route = onComplete(f)(Response.tryToRoute)
  }
}