package ru.ginger.account.utils.controller

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, Route}
import play.api.libs.json.Writes
import ru.ginger.account.exception.ApplicationException
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.ginger.account.format.JsonFormat.{failureResponseWrites, successResponseWrites}
import scala.util.{Failure, Success, Try}

sealed trait Response
case class SuccessResponse[X](result: X) extends Response
case class FailureResponse(ex: Throwable) extends Response

object Response extends Directives {
  def tryToRoute[X: Writes](f: Try[X]): Route = {
    f match {
      case Success(value) => Response.okRoute(value)
      case Failure(ex: ApplicationException) => Response.badRequestRoute(FailureResponse(ex))
      case Failure(ex) => Response.internalErrorRoute(FailureResponse(ex))
    }
  }

  def okRoute[X: Writes](value: X): Route = complete(createResponse(SuccessResponse(value), StatusCodes.OK))
  def badRequestRoute(value: FailureResponse): Route = complete(createResponse(value, StatusCodes.BadRequest))
  def internalErrorRoute(value: FailureResponse): Route = complete(createResponse(value, StatusCodes.InternalServerError))

  // internal

  private def createResponse[X](value: X, code: StatusCode)(implicit writes: Writes[X]): HttpResponse = {
    HttpResponse(
      status = code,
      entity = HttpEntity(
        contentType = ContentTypes.`application/json`,
        writes.writes(value).toString()
      )
    )
  }
}