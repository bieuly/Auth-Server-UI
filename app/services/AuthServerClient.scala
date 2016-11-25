package services

import play.api.libs.json.JsValue
import play.api.libs.ws.WSRequest
import play.api.libs.json.Json
import util.AuthServerUIConstants.AUTH_SERVER_URL
import play.api.libs.ws.WSClient
import com.google.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class AuthServerClient @Inject() (ws: WSClient){
  
    def authenticate(username: String, password: String) = {
    val body: JsValue = Json.obj("userName" -> username, "password" -> password)
    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/token").withHeaders(("Content-type", "application/json"))
    val responseFuture = request.post(body)

    responseFuture map { response =>
      {
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
  }
}