package ru.ginger.account.utils.json

import play.api.libs.json._

object JsonUtils {
  def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = new Format[E#Value] {
    override def writes(o: E#Value): JsValue = JsString(o.toString)
    override def reads(json: JsValue): JsResult[E#Value] = {
      json match {
        case JsString(value) => JsSuccess(enum.withName(value))
        case js => JsError(s"Couldn't parse $js to ${classOf[E#Value].getName}")
      }
    }
  }
}