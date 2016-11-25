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

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

case class LoginRequest(username: String, password: String)

@Singleton
class HomeController @Inject() (ws: WSClient, authClient: AuthServerClient) extends Controller {
  private val logger = Logger(this.getClass)
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index()).withNewSession
  }

  def login = Action.async(parse.json) { request =>
    implicit val loginRequest: Reads[LoginRequest] = Json.reads[LoginRequest]

    request.body.validate[LoginRequest] match {
      case s: JsSuccess[LoginRequest] => {
        val username = s.get.username
        val password = s.get.password

        authClient.authenticate(username, password) map {
          case (200, token) => {
            Ok(toJson(Map("valid" -> true))).withSession(("user", username), ("token", token))
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
    val token = request.session.get("token")

    Future(username.map {
      user =>
        token.map {
          token => Ok(views.html.welcome(user, token))
        }.getOrElse(Redirect(routes.HomeController.index())).withNewSession
    }.getOrElse(Redirect(routes.HomeController.index())).withNewSession)
  }
}