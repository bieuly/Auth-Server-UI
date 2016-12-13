package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import _root_.util.AuthServerUIConstants.AUTH_SERVER_URL
import services.AuthServerClient
import models.LoginRequest
import models.Customer
import _root_.util.JsonMappers.customerFmt
import _root_.util.JsonMappers.loginRequestFmt
import _root_.util.JsonMappers.tokenClaimReads
import models.TokenClaim
import security.AuthServerUIPermissions.ManageCustomers
import security.AuthServerUIPermissions.ViewCustomers

@Singleton
class HomeController @Inject() (ws: WSClient, authClient: AuthServerClient) extends Controller {
  private val logger = Logger(this.getClass)

  def index = Action { implicit request =>
    Ok(views.html.index()).discardingCookies(DiscardingCookie("user"), DiscardingCookie("token"))
  }

  def login = Action.async(parse.json) { request =>

    request.body.validate[LoginRequest] match {
      case s: JsSuccess[LoginRequest] => {
        val username = s.get.username
        val password = s.get.password

        authClient.authenticate(username, password) map {
          case (200, token) => {
            Ok(toJson(Map("valid" -> true))).withCookies(Cookie("user", username), Cookie("token", token))
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
    val username = request.cookies.get("user")
    val token = request.cookies.get("token")

    val customers = Json.parse(authClient.getCustomers).validate[List[Customer]] match {
      case customers: JsSuccess[List[Customer]] => Some(customers.get)
      case e: JsError => {
        logger.error("Errors: " + JsError.toJson(e).toString())
        None
      }
    }

    Future(username.map {
      user =>
        token.map {
          token =>
            {
              val permissions = Json.parse(authClient.tokenVerify(token.value)).validate[TokenClaim] match {
                case tokenClaim: JsSuccess[TokenClaim] => getAuthServerUIPermissions(tokenClaim.value.permissions)
                case e: JsError => {
                  logger.error("Errors: " + JsError.toJson(e).toString())
                  None
                }
              }

              val customers = permissions match {
                case Some(permissions) => {
                  if (permissions.exists { p => p == ManageCustomers.id || p == ViewCustomers.id }) {
                    Json.parse(authClient.getCustomers).validate[List[Customer]] match {
                      case customers: JsSuccess[List[Customer]] => Some(customers.get)
                      case e: JsError => {
                        logger.error("Errors: " + JsError.toJson(e).toString())
                        None
                      }
                    }
                  } else None

                }
                case None => None
              }

              Ok(views.html.welcome(user.value, token.value, customers, true))
            }
        }.getOrElse(Redirect(routes.HomeController.index()))
    }.getOrElse(Redirect(routes.HomeController.index())))
  }

  def mockAuthServerGetToken = Action {
    Ok("MOCKTOKENMOCKTOKENMOCKTOKEN")
  }

  def getCustomers = Action.async {
    val customersJsonString = authClient.getCustomers
    Future(Ok(customersJsonString))
  }

  def getUsers = Action.async {
    val usersJsonString = authClient.getUsers
    Future(Ok(usersJsonString))
  }

  def getRoles = Action.async {
    val rolesJsonString = authClient.getRoles
    Future(Ok(rolesJsonString))
  }

  def getAuthServerUIPermissions(userPermissions: Map[String, Seq[Int]]) = {
    userPermissions.get("AUI")
  }

}