package ru.ginger.account.wiring

import FutureCoreModule._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Route, RouteConcatenation}

object AccountHttp {

  def run(): Unit = {
    Http().bindAndHandle(
      RouteConcatenation.concat(routes: _*),
      accountConfiguration.httpHost,
      accountConfiguration.httpPort
    ).map { _ =>
      println(s"Application has been started on ${accountConfiguration.httpHost}:${accountConfiguration.httpPort}")
    }
  }

  // internal

  private lazy val routes: Seq[Route] = FutureControllers.controllers.map(_.route)
}