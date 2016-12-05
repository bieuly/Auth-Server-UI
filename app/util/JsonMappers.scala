package util

import play.api.libs.json.{JsPath, Json, Reads}
import models.{Customer, LoginRequest}
import play.api.libs.functional.syntax.toFunctionalBuilderOps

/**
  * Created by billyhoang on 2016-12-03.
  */
object JsonMappers {

  implicit val customerFmt = Json.format[Customer]
//  implicit val customerFmt : Reads[Customer] = (
//    (JsPath \ "_id").read[String] and
//    (JsPath \ "firstName").read[String] and
//      (JsPath \ "lastName").read[String]
//    )(Customer)

  implicit val loginRequestFmt: Reads[LoginRequest] = Json.reads[LoginRequest]


}
