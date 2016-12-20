package services

import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.ws.WSRequest
import play.api.libs.json.Json.toJson
import play.api.libs.json.Json.parse
//import play.api.libs.json.Json
import util.AuthServerUIConstants.AUTH_SERVER_URL
import play.api.libs.ws.WSClient
import com.google.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.libs.json._
import scala.collection.mutable.ArrayBuffer

import security.AuthServerUIPermissions.MANAGE_CUSTOMERS
import security.AuthServerUIPermissions.VIEW_CUSTOMERS
import security.AuthServerUIPermissions.MANAGE_USERS
import security.AuthServerUIPermissions.VIEW_USERS
import security.AuthServerUIPermissions.MANAGE_ROLES
import security.AuthServerUIPermissions.VIEW_ROLES

import _root_.util.JsonMappers.tokenClaimReads
import _root_.util.JsonMappers.customerCreationRequestReads
import _root_.util.JsonMappers.customerCreationRequestWrites

import models.TokenClaim
import models.UserCreationRequest
import models.CustomerCreationRequest

import exceptions.ParseTokenClaimException
import exceptions.TokenVerifyException
import exceptions.RetrieveTokenException
import com.digitalpaytech.ribbon.ClientFactory
import rx.lang.scala.JavaConversions._
import java.nio.charset.Charset
import t2systems.util.discovery.RX._
import io.netty.buffer.ByteBuf
import com.digitalpaytech.client.util.CommunicationException
import exceptions.TokenInvalidatedException

class AuthServerClient @Inject() (ws: WSClient, clientFactory: ClientFactory) {

  private val logger = Logger(this.getClass)

  def authenticate(username: String, password: String) = {

    val body: JsValue = Json.obj("userName" -> username, "password" -> password)

//    val authClient = clientFactory.from(classOf[AuthClient])
    //              toScalaObservable(authClient.tokenRequest(body.toString()).observe()).single.subscribe(
    //                response => {
    //                  logger.info("SUCCESS!")
    //                  response.toString(Charset.defaultCharset())
    //                  
    //                },
    //                error => {
    //                  logger.error("Failed")
    //                })
    
    
//    logger.error("A;LSDFJ; APDHFAHDS;FA;SDFJ;ADHF;HA;FADH;FH;DF;DSHF;AHDFAHSD;F")
//    submit(authClient.tokenRequest(body.toString()))(
//      (decodedToken: ByteBuf) => {
//        logger.error("IT WAS A SUCCESSSSSSSSSSSSSSSSSSSSSSSSSS")
//        Future successful (200, "Success")},
//      (error: Throwable) => {
//        error match {
//          case e: CommunicationException => {
//            logger.error("IT DOESN'T WORK.")
//            Future successful ((e.getResponseStatus, "SOMETHING WRONG HAPPENED"))
//          }
//          case e => {
//            logger.error("ITS THE UNHANDLED CASE!")
//            Future.failed(e)
//          }
//        }
//      })
//  }

        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/token").withHeaders(("Content-type", "application/json"))
        val responseFuture = request.post(body)
  
        responseFuture map { response =>
          {
            logger.error("status is: " + response.status + response.body)
            (response.status, response.body)
          }
        }
  }

  def tokenVerify(token: String) = {
        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/token/decoded.json").withHeaders(("Content-type", "text/plain"))
        val responseFuture = request.post(token)
    
        responseFuture map { response =>
          {
            (response.status, response.body)
          }
        }
//    val authServerUIResponse =
//      """{
//        "userName": "irisuser",
//        "permissions": {
//          "MS": [
//            2,
//            1
//          ],
//          "PS": [
//            7,
//            4,
//            3
//          ],
//          "AUI": [
//            1,
//            2,
//            3,
//            4,
//            5,
//            6
//          ]
//        },
//        "tokenExpiryTime": 1489009052774
//      }
//      """
//
//    Future(200, authServerUIResponse)
  }

  def getCustomers = {
        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/customers").withHeaders(("Content-type", "text/plain"))
        val responseFuture = request.get()
    
        responseFuture map { response =>
        {
          (response.status, response.body)
        }
        }

//    val authServerUIResponse =
//      """
//[
//  {
//    "_id": "dbe9838196a4a02c475cd0f21a262a8779a113d7",
//    "customerName": "authGod"
//  },
//  {
//    "_id": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
//    "customerName": "IrisUser"
//  },
//  {
//    "_id": "944410cac115b71a89816942c1d960acd14e3d8d",
//    "customerName": "IrisUser"
//  }
//]
//    """.stripMargin
//
//    Future(authServerUIResponse)
    //    var result = ArrayBuffer[Human]()
    //    val json = parse(authServerUIResponse)
    //    val elements = (json \ "Humans").children
    //    for (human <- elements) {
    //      val h = human.extract[Human]
    //      result.+=(h)
    //    }
    //    result

  }

  def getUsers(customerId: String) = {
        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/users/" + customerId).withHeaders(("Content-type", "text/plain"))
        val responseFuture = request.get()
    
        responseFuture map { response =>
        {
          (response.status, response.body)
        }
        }
//    """
//      [
//  {
//    "_id": "58094b2dd2fe343101f151b1",
//    "userName": "irisuser",
//    "firstName": "IrisUser",
//    "lastName": "IrisUser",
//    "tokenSignature": "_ueYzqXPePG411uHWX0nIBlmAYsidRzYkioFz_du-f4",
//    "password": "$2a$12$R7ZdZo2piYUFDILLuXpWnuntzqPL/oKcJ6Htht8eoJ8p1qarcpK/.",
//    "isTemporaryPassword": false,
//    "isLocked": false,
//    "numberOfFailures": 0,
//    "lockExpiryTime": 1481563299615,
//    "customerId": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
//    "userStatusType": "Enabled",
//    "userType": "User",
//    "rolesId": {
//      "MessagingServer_messagingServerRole": 1,
//      "PS_paymentServerRole": 3
//    }
//  }
//]
//      """
  }

  def getRoles(customerId: String) = {
        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/roles/" + customerId).withHeaders(("Content-type", "text/plain"))
        val responseFuture = request.get()
    
        responseFuture map { response =>
        {
          (response.status, response.body)
        }
        }
//    """
//      [
//  {
//    "_id": {
//      "$oid": "58094a9dd2fe342e01f151b0"
//    },
//    "name": "MessagingServer_messagingServerRole",
//    "displayName": "MessagingServerRole",
//    "version": 1,
//    "customerId": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
//    "permissions": [
//      {
//        "_id": {
//          "$oid": "580949f6bef7a0ef1a0a708a"
//        },
//        "permissionId": 1,
//        "parentPermissionType": "",
//        "name": "Send SMS",
//        "tokenExpiryTime": 7689600000,
//        "serviceOwner": "MessagingServer"
//      },
//      {
//        "_id": {
//          "$oid": "58094a39bef7a0ef1a0a708b"
//        },
//        "permissionId": 2,
//        "parentPermissionType": "",
//        "name": "Terminate Conversation",
//        "tokenExpiryTime": 7689600000,
//        "serviceOwner": "MessagingServer"
//      }
//    ],
//    "serviceOwner": "MS"
//  },
//  {
//    "_id": {
//      "$oid": "580e68efd2fe34dd001098be"
//    },
//    "name": "PS_paymentServerRole",
//    "displayName": "paymentServerRole",
//    "version": 3,
//    "customerId": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
//    "permissions": [
//      {
//        "_id": {
//          "$oid": "580e5d18bef7a0ef1a0a708c"
//        },
//        "permissionId": 3,
//        "parentPermissionType": "",
//        "name": "Read Terminal Configurations",
//        "tokenExpiryTime": 7689600000,
//        "serviceOwner": "PS"
//      },
//      {
//        "_id": {
//          "$oid": "580e5d33bef7a0ef1a0a708d"
//        },
//        "permissionId": 4,
//        "parentPermissionType": "",
//        "name": "Write Terminal Configurations",
//        "tokenExpiryTime": 7689600000,
//        "serviceOwner": "PS"
//      },
//      {
//        "_id": {
//          "$oid": "5818cafa090f9661f17c3d89"
//        },
//        "permissionId": 7,
//        "parentPermissionType": "",
//        "name": "Process EMV transaction",
//        "tokenExpiryTime": 7689600000,
//        "serviceOwner": "PS"
//      }
//    ],
//    "serviceOwner": "PS"
//  }
//]
//      """
  }

  def createUser(request: UserCreationRequest) = {
    //    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/users").withHeaders(("Content-type", "application/json")).withBody(Json.toJson(request))
    //    val responseFuture = request.post()
    //    responseFuture map { response => (response.status, response.body) }
    Future(201, "EEEEER Created Role")
  }

  def createCustomer(customerCreationRequest: CustomerCreationRequest) = {
    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/customers").withHeaders(("Content-type", "application/json"))
        val body = Json.toJson(customerCreationRequest)
        val responseFuture = request.post(body)
    
        responseFuture map { response =>
        {
          (response.status, response.body)
        }
        }
  }

  def verifyRequest(tokenOpt: Option[String], p: Int => Boolean) = {
    tokenOpt match {
      case Some(token) => {
        tokenVerify(token).map(tokenVerifyResponse => {
          tokenVerifyResponse match {
            case (200, responseBody) => {
              Json.parse(responseBody).validate[TokenClaim] match {
                case s: JsSuccess[TokenClaim] => {
                  val permissions = s.get.permissions.get("AUI")
                  permissions match {
                    case Some(permissions) => {
                      (permissions.exists(p), Some(permissions))
                    }
                    case None => (false, None)
                  }
                }
                case _ => throw new ParseTokenClaimException
              }
            }
            case (401, responseBody) => throw new TokenInvalidatedException(responseBody)
            case (_, responseBody) => throw new TokenVerifyException(responseBody)
          }
        })
      }
      case None => throw new RetrieveTokenException
    }

  }

}