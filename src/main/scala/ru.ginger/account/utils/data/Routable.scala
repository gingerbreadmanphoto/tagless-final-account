package ru.ginger.account.utils.data

import akka.http.scaladsl.server.Route
import play.api.libs.json.Writes
import scala.language.higherKinds

trait Routable[F[_]] {
  def route[X: Writes](f: F[X]): Route
}