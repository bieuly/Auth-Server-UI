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
import _root_.util.JsonMappers.roleCreationRequestReads
import _root_.util.JsonMappers.roleCreationRequestWrites
import _root_.util.JsonMappers.userCreationRequestReads
import _root_.util.JsonMappers.userCreationRequestWrites


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
import models.RoleCreationRequest

class AuthServerClient @Inject() (ws: WSClient, clientFactory: ClientFactory) {

  private val logger = Logger(this.getClass)

  def authenticate(username: String, password: String) = {

    val body: JsValue = Json.obj("userName" -> username, "password" -> password)

    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.tokenRequest(body.toString()))(
    (decodedToken: ByteBuf) => Future successful ((200, decodedToken.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    }
)             
//        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/token").withHeaders(("Content-type", "application/json"))
//        val responseFuture = request.post(body)
//  
//        responseFuture map { response =>
//          {
//            logger.error("status is: " + response.status + response.body)
//            (response.status, response.body)
//          }
//        }
  }

  def tokenVerify(token: String) = {

    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.tokenVerify(token))(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    }
)     
    
    
  
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/token/decoded.json").withHeaders(("Content-type", "text/plain"))
//        val responseFuture = request.post(token)
//    
//        responseFuture map { response =>
//          {
//            (response.status, response.body)
//          }
//        }
  }

  def getCustomers = {
    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.getCustomers)(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    }
)     
    
    
//        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/customers").withHeaders(("Content-type", "text/plain"))
//        val responseFuture = request.get()
//    
//        responseFuture map { response =>
//        {
//          (response.status, response.body)
//        }
//        }
  }

  def getUsers(customerId: String) = {
    
    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.getUsers(customerId))(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    })
//        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/users/" + customerId).withHeaders(("Content-type", "text/plain"))
//        val responseFuture = request.get()
//    
//        responseFuture map { response =>
//        {
//          (response.status, response.body)
//        }
//        }
  }

  def getRoles(customerId: String) = {
    
    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.getRoles(customerId))(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    })
    
//        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/roles/" + customerId).withHeaders(("Content-type", "text/plain"))
//        val responseFuture = request.get()
//    
//        responseFuture map { response =>
//        {
//          (response.status, response.body)
//        }
//        }
  }

  def getPermissions = {
    
    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.getPermissions)(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    })
    
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/permissions").withHeaders(("Content-type", "text/plain"))
//        val responseFuture = request.get()
//    
//        responseFuture map { response =>
//        {
//          (response.status, response.body)
//        }
//        }
  }
  
  def createUser(userCreationRequest: UserCreationRequest) = {
    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.createUser(Json.toJson(userCreationRequest).toString()))(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    })
    
    
//        val request: WSRequest = ws.url(AUTH_SERVER_URL + "/users").withHeaders(("Content-type", "application/json"))
//        val body = Json.toJson(userCreationRequest)         
//        val responseFuture = request.post(body)
//        responseFuture map { response => (response.status, response.body) }
  }
  
  def createRole(roleCreationRequest: RoleCreationRequest) = {
    
    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.createRole(Json.toJson(roleCreationRequest).toString()))(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    })
    
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/roles").withHeaders(("Content-type", "application/json"))
//        val body = Json.toJson(roleCreationRequest)
//        val responseFuture = request.post(body)
//    
//        responseFuture map { response =>
//        {
//          (response.status, response.body)
//        }
//        }
  }

  def createCustomer(customerCreationRequest: CustomerCreationRequest) = {
    val authClient = clientFactory.from(classOf[AuthClient])
    submit(authClient.createCustomer(Json.toJson(customerCreationRequest).toString()))(
    (responseMessage: ByteBuf) => Future successful ((200, responseMessage.toString(Charset.defaultCharset()))),
    (error: Throwable) => error match {
      case ce: CommunicationException => Future successful ((ce.getResponseStatus, ce.getMessage))
      case e => throw new Exception("w.e", e)
    })
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/customers").withHeaders(("Content-type", "application/json"))
//        val body = Json.toJson(customerCreationRequest)
//        val responseFuture = request.post(body)
//    
//        responseFuture map { response =>
//        {
//          (response.status, response.body)
//        }
//        }
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