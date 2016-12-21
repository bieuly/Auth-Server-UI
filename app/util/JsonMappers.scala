package util

import play.api.libs.json.{JsPath, Json, Reads}
import models._
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

  implicit val tokenClaimWrites = Json.writes[TokenClaim]
  implicit val tokenClaimReads = Json.reads[TokenClaim]

  implicit val userCreationRequestWrites = Json.writes[UserCreationRequest]
  implicit val userCreationRequestReads = Json.reads[UserCreationRequest]

  implicit val customerCreationRequestWrites = Json.writes[CustomerCreationRequest]
  implicit val customerCreationRequestReads = Json.reads[CustomerCreationRequest]
  
  implicit val roleCreationRequestWrites = Json.writes[RoleCreationRequest]
  implicit val roleCreationRequestReads = Json.reads[RoleCreationRequest]
  
  implicit val changePasswordRequestWrites = Json.writes[ChangePasswordRequest]
  implicit val changePasswordRequestReads = Json.reads[ChangePasswordRequest]
  
  implicit val changePasswordRequestWritesAUI = Json.writes[ChangePasswordRequestAUI]
  implicit val changePasswordRequestReadsAUI = Json.reads[ChangePasswordRequestAUI]
}
