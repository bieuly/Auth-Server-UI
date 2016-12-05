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
      case e : JsError => {
        logger.error("Errors: " + JsError.toJson(e).toString())
        None
      }
    }

    Future(username.map {
      user =>
        token.map {
          token => Ok(views.html.welcome(user.value, token.value, List(1,2), customers))
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
    Future(Ok("user data"))
  }

  def getRoles = Action.async {
    Future(Ok("role data"))
  }

}