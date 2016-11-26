package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Reads
import play.api.libs.json.Json
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSRequest
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.libs.json.JsValue

import util.AuthServerUIConstants.AUTH_SERVER_URL
import services.AuthServerClient
import models.LoginRequest

@Singleton
class HomeController @Inject() (ws: WSClient, authClient: AuthServerClient) extends Controller {
  private val logger = Logger(this.getClass)

  def index = Action { implicit request =>
    logger.error("username is: " + request.cookies.get("user").get.value)
    logger.error("username is: " + request.cookies.get("token").get.value)
    Ok(views.html.index()).discardingCookies(DiscardingCookie("user"), DiscardingCookie("token"))
  }

  def login = Action.async(parse.json) { request =>
    implicit val loginRequest: Reads[LoginRequest] = Json.reads[LoginRequest]

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

    Future(username.map {
      user =>
        token.map {
          token => Ok(views.html.welcome(user.value, token.value))
        }.getOrElse(Redirect(routes.HomeController.index()))
    }.getOrElse(Redirect(routes.HomeController.index())))
  }
}