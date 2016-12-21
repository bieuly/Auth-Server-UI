package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import play.api.mvc.Action
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.mvc.Results.Status
import play.api.mvc.Results.Forbidden
import play.api.mvc.Results.Unauthorized
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import _root_.util.AuthServerUIConstants.AUTH_SERVER_URL
import services.AuthServerClient
import com.typesafe.config.ConfigFactory

import models.LoginRequest
import models.Customer
import models.UserCreationRequest
import models.CustomerCreationRequest
import models.TokenClaim

import _root_.util.JsonMappers.customerFmt
import _root_.util.JsonMappers.loginRequestFmt
import _root_.util.JsonMappers.tokenClaimReads
import _root_.util.JsonMappers.userCreationRequestReads
import _root_.util.JsonMappers.customerCreationRequestReads
import _root_.util.JsonMappers.customerCreationRequestWrites
import _root_.util.JsonMappers.roleCreationRequestReads
import _root_.util.JsonMappers.roleCreationRequestWrites
import _root_.util.Predicates
import _root_.util.Predicates._

import security.AuthServerUIPermissions.MANAGE_CUSTOMERS
import security.AuthServerUIPermissions.VIEW_CUSTOMERS
import security.AuthServerUIPermissions.MANAGE_USERS
import security.AuthServerUIPermissions.VIEW_USERS
import security.AuthServerUIPermissions.MANAGE_ROLES
import security.AuthServerUIPermissions.VIEW_ROLES

import exceptions.RetrieveTokenException
import exceptions.TokenVerifyException
import exceptions.ParseTokenClaimException
import exceptions.UserNotAuthorizedException
import exceptions.TokenInvalidatedException
import models.RoleCreationRequest
import java.util.Date

@Singleton
class HomeController @Inject() (ws: WSClient, authClient: AuthServerClient) extends Controller {
  private val logger = Logger(this.getClass)

  def index = Action { implicit request =>
    Ok(views.html.index()).withNewSession.withHeaders(("Cache-Control", "no-cache, must-revalidate, no-store"))
  }

  def login = Action.async(parse.json) { request =>

    request.body.validate[LoginRequest] match {
      case s: JsSuccess[LoginRequest] => {
        val username = s.get.username
        val password = s.get.password
        logger.error(username + password)
        authClient.authenticate(username, password) map {
          case (200, token) => {
            val time = new Date().getTime.toString()
            Ok(toJson(Map("valid" -> true))).withSession(("user", username), ("token", token), ("timeStamp", time))
          }
          case (statusCode, errorMsg) => {
            logger.error(statusCode + ": " + errorMsg)
            (Ok(toJson(Map("valid" -> false))))
          }
        }
      }
      case _ => Future(Ok(toJson(Map("valid" -> false))))
    }
  }

  def welcome = Action.async { implicit request =>
    val username = request.session.get("user")
    val tokenOpt = request.session.get("token")
    (username, tokenOpt) match {
      case (Some(user), Some(token)) => {
        logger.error(";LAFSDFAJD;LFJAFDS;FHASD;LGFH;LASGH;LASHADHFJ;LDS::::" + token)
        authClient.verifyRequest(tokenOpt, p => p == MANAGE_CUSTOMERS || p == VIEW_CUSTOMERS) flatMap { verifyRequestResponse =>
          val authorized = verifyRequestResponse._1
          val permissions = verifyRequestResponse._2

          if (authorized) {
            authClient.getCustomers map { getCustomersResponse =>
              val status = getCustomersResponse._1
              val responseBody = getCustomersResponse._2
              status match {
                case 200 => {
                  val customers = responseBody
                  Json.parse(customers).validate[List[Customer]] match {
                    case customers: JsSuccess[List[Customer]] => Ok(views.html.welcome(user, token, Some(customers.get), permissions)).withHeaders(("Cache-Control", "no-cache, must-revalidate, no-store"))
                    case _                                    => Ok(views.html.welcome(user, token, None, permissions))
                  }
                }
                case _ => Redirect(routes.HomeController.index())
              }

            } recover {
              case e => Redirect(routes.HomeController.index())
            }
          } else { Future(Ok(views.html.welcome(user, token, None, None))) }
        }
      }
      case (_, _) => {
        Future(Redirect(routes.HomeController.index()))
      }
    }

  }

  def createCustomer = Action.async(parse.json) { implicit request =>
    request.body.validate[CustomerCreationRequest].fold(
      error => Future.successful(InternalServerError("Unable to process request")),
      customerCreationRequest => {
        val timeStamp = request.session.get("timeStamp")
        val token = request.session.get("token")
        
        if(!isSessionExpired(timeStamp)){
            val responseFut = for {
            (authorized, permissions) <- authClient.verifyRequest(token, p => p == MANAGE_CUSTOMERS)
            _ <- Predicates.checkAndThrowExceptionIfFails(authorized)(new UserNotAuthorizedException)
            createCustomerResponse <- authClient.createCustomer(customerCreationRequest)
            } yield createCustomerResponse
  
            responseFut map {
              case (status, responseBody) =>
                Status(status)(responseBody)
            } recover {
              case e @ (_: UserNotAuthorizedException | _: TokenInvalidatedException) => {
                logger.error("User is not authorized")
                Unauthorized(e.getMessage)
              }
              case e: TokenVerifyException => {
                logger.error("Unexpected error occured while trying to verify token")
                InternalServerError(e.getMessage)
              }
            }
        } else {
            logger.error("Session timeout")
            Future(Unauthorized("Session timeout"))
        }
        
        
      })
  }

  def createUser = Action.async(parse.json) { implicit request =>
    request.body.validate[UserCreationRequest].fold(
      error => Future.successful(InternalServerError("Unable to process request")),
      userCreationRequest => {
        val token = request.session.get("token")
        val responseFut = for {
          (authorized, permissions) <- authClient.verifyRequest(token, p => p == MANAGE_USERS)
          _ <- Predicates.checkAndThrowExceptionIfFails(authorized)(new UserNotAuthorizedException)
          createUserResponse <- authClient.createUser(userCreationRequest)
        } yield createUserResponse

        responseFut map {
          case (status, responseBody) =>
            Status(status)(responseBody)
        }
      })
  }
  
  def createRole = Action.async(parse.json) { implicit request =>
    request.body.validate[RoleCreationRequest].fold(
      error => Future.successful(InternalServerError("Unable to process request")),
      roleCreationRequest => {
        val token = request.session.get("token")
        val responseFut = for {
          (authorized, permissions) <- authClient.verifyRequest(token, p => p == MANAGE_ROLES)
          _ <- Predicates.checkAndThrowExceptionIfFails(authorized)(new UserNotAuthorizedException)
          roleCreationRequest <- authClient.createRole(roleCreationRequest)
        } yield roleCreationRequest

        responseFut map {
          case (status, responseBody) =>
            Status(status)(responseBody)
        }
      })
  }
  
  

  def mockAuthServerGetToken = Action {
    Ok("MOCKTOKENMOCKTOKENMOCKTOKEN")
  }

  def getCustomers = Action.async {
    authClient.getCustomers map { getRolesResponse =>
      val status = getRolesResponse._1
      val responseBody = getRolesResponse._2
      Status(status)(responseBody)
    }
  }

  def getUsers(customerId: String) = Action.async {
    authClient.getUsers(customerId) map { getRolesResponse =>
      val status = getRolesResponse._1
      val responseBody = getRolesResponse._2
      Status(status)(responseBody)
    }
  }

  def getRoles(customerId: String) = Action.async {
    authClient.getRoles(customerId) map { getRolesResponse =>
      val status = getRolesResponse._1
      val responseBody = getRolesResponse._2
      Status(status)(responseBody)
    }

  }

  def getPermissions = Action.async {
    authClient.getPermissions map { getPermissionsResponse =>
      val status = getPermissionsResponse._1
      val responseBody = getPermissionsResponse._2
      Status(status)(responseBody)
    }
  }
  
  def getAuthServerUIPermissions(userPermissions: Map[String, Seq[Int]]) = {
    logger.info("AUI PERMISSIONS: " + userPermissions.get("AUI"))
    userPermissions.get("AUI")
  }
  
  def isSessionExpired(timeStamp: Option[String]) = {
    timeStamp match {
      case Some(time) => {
        val previousTime = time.toLong
        val currentTime = new Date().getTime
        val timeout = (ConfigFactory.load().getInt("sessionTimeout") * 1000 * 60).toLong
        if((currentTime - previousTime) > timeout) true else false
      }
      case None => true
    }
  }
  

}