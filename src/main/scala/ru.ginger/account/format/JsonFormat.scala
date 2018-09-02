package ru.ginger.account.format

import play.api.libs.json._
import ru.ginger.account.exception.{ApplicationException, ValidationException}
import ru.ginger.account.model.OperationType
import ru.ginger.account.model.OperationType.OperationType
import ru.ginger.account.protocol.{AccountView, DepositRequest, OperationView, WithdrawalRequest}
import ru.ginger.account.utils.controller.{FailureResponse, SuccessResponse}
import ru.ginger.account.utils.json.JsonUtils

object JsonFormat {
  implicit val operationTypeFormat: Format[OperationType] = JsonUtils.enumFormat(OperationType)
  implicit val operationViewFormat: Format[OperationView] = Json.format[OperationView]
  implicit val accountViewFormat: Format[AccountView] = Json.format[AccountView]
  implicit val withdrawalRequestFormat: Format[WithdrawalRequest] = Json.format[WithdrawalRequest]
  implicit val depositRequestFormat: Format[DepositRequest] = Json.format[DepositRequest]

  implicit val failureResponseWrites: Writes[FailureResponse] = {
    case FailureResponse(ex: ValidationException) =>
      Json.obj(
        "errors" -> ex.errors.map(e => JsString(e.message)).toList,
        "message" -> ex.getMessage
      )
    case FailureResponse(ex: ApplicationException) => Json.obj("message" -> ex.getMessage)
    case FailureResponse(ex) => Json.obj("message" -> ex.getMessage)
  }

  implicit def successResponseWrites[X](implicit writes: Writes[X]): Writes[SuccessResponse[X]] = {
    o => Json.obj("result" -> Json.toJson(o.result))
  }

  implicit val unitWrites: Writes[Unit] = _ => JsString("OK")
}