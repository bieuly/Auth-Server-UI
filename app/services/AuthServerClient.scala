package services

import play.api.libs.json.JsValue
import play.api.libs.ws.WSRequest
import play.api.libs.json.Json
import util.AuthServerUIConstants.AUTH_SERVER_URL
import play.api.libs.ws.WSClient
import com.google.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.libs.json._
import scala.collection.mutable.ArrayBuffer

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
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "/token/decoded.json").withHeaders(("Content-type", "text/plain"))
//    val responseFuture = request.post(token)
//
//    responseFuture map { response =>
//      {
//        (response.status, response.body)
//      }
//    }
    val authServerUIResponse = 
      """{
        "userName": "irisuser",
        "permissions": {
          "MS": [
            2,
            1
          ],
          "PS": [
            7,
            4,
            3
          ]
        },
        "tokenExpiryTime": 1489009052774
      }
      """
    
    authServerUIResponse
  }

  def getCustomers = {
    // TODO: Implement an endpoint an authserver to get all Customers
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "<PATH FOR GET CUSTOMERS>").withHeaders(("Content-type", "text/plain"))
//    val responseFuture = request.post(token)
//
//    responseFuture map { response =>
//    {
//      (response.status, response.body)
//    }
//    }

  val authServerUIResponse =
    """
      |[{
      | "_id": "0",
      |	"firstName": "billy",
      |	"lastName": "Hoang"
      |}, {
      | "_id": "1",
      |	"firstName": "jason",
      |	"lastName": "hoang"
      |}]
    """.stripMargin

    authServerUIResponse
//    var result = ArrayBuffer[Human]()
//    val json = parse(authServerUIResponse)
//    val elements = (json \ "Humans").children
//    for (human <- elements) {
//      val h = human.extract[Human]
//      result.+=(h)
//    }
//    result

  }

  def getUsers = {
//    // TODO: Implement an endpoint an authserver to get all Users
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "<PATH FOR GET USERS>").withHeaders(("Content-type", "text/plain"))
//    val responseFuture = request.post(token)
//
//    responseFuture map { response =>
//    {
//      (response.status, response.body)
//    }
//    }
  }

  def getRoles = {
//    // TODO: Implement an endpoint an authserver to get all Permissions
//    val request: WSRequest = ws.url(AUTH_SERVER_URL + "<PATH FOR GET PERMISSIONS>").withHeaders(("Content-type", "text/plain"))
//    val responseFuture = request.post(token)
//
//    responseFuture map { response =>
//    {
//      (response.status, response.body)
//    }
//    }
  }

}