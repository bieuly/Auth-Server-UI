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
          ],
          "AUI": [
            1,
            2,
            3,
            4,
            5,
            6
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
[
  {
    "_id": "dbe9838196a4a02c475cd0f21a262a8779a113d7",
    "customerName": "authGod"
  },
  {
    "_id": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
    "customerName": "IrisUser"
  },
  {
    "_id": "944410cac115b71a89816942c1d960acd14e3d8d",
    "customerName": "IrisUser"
  }
]
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
    """
      [
  {
    "_id": "58094b2dd2fe343101f151b1",
    "userName": "irisuser",
    "firstName": "IrisUser",
    "lastName": "IrisUser",
    "tokenSignature": "_ueYzqXPePG411uHWX0nIBlmAYsidRzYkioFz_du-f4",
    "password": "$2a$12$R7ZdZo2piYUFDILLuXpWnuntzqPL/oKcJ6Htht8eoJ8p1qarcpK/.",
    "isTemporaryPassword": false,
    "isLocked": false,
    "numberOfFailures": 0,
    "lockExpiryTime": 1481563299615,
    "customerId": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
    "userStatusType": "Enabled",
    "userType": "User",
    "rolesId": {
      "MessagingServer_messagingServerRole": 1,
      "PS_paymentServerRole": 3
    }
  }
]
      """
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
    """
      [
  {
    "_id": {
      "$oid": "58094a9dd2fe342e01f151b0"
    },
    "name": "MessagingServer_messagingServerRole",
    "displayName": "MessagingServerRole",
    "version": 1,
    "customerId": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
    "permissions": [
      {
        "_id": {
          "$oid": "580949f6bef7a0ef1a0a708a"
        },
        "permissionId": 1,
        "parentPermissionType": "",
        "name": "Send SMS",
        "tokenExpiryTime": 7689600000,
        "serviceOwner": "MessagingServer"
      },
      {
        "_id": {
          "$oid": "58094a39bef7a0ef1a0a708b"
        },
        "permissionId": 2,
        "parentPermissionType": "",
        "name": "Terminate Conversation",
        "tokenExpiryTime": 7689600000,
        "serviceOwner": "MessagingServer"
      }
    ],
    "serviceOwner": "MS"
  },
  {
    "_id": {
      "$oid": "580e68efd2fe34dd001098be"
    },
    "name": "PS_paymentServerRole",
    "displayName": "paymentServerRole",
    "version": 3,
    "customerId": "653923c31379b360484c29ff5809f7aaf6f7ecf7",
    "permissions": [
      {
        "_id": {
          "$oid": "580e5d18bef7a0ef1a0a708c"
        },
        "permissionId": 3,
        "parentPermissionType": "",
        "name": "Read Terminal Configurations",
        "tokenExpiryTime": 7689600000,
        "serviceOwner": "PS"
      },
      {
        "_id": {
          "$oid": "580e5d33bef7a0ef1a0a708d"
        },
        "permissionId": 4,
        "parentPermissionType": "",
        "name": "Write Terminal Configurations",
        "tokenExpiryTime": 7689600000,
        "serviceOwner": "PS"
      },
      {
        "_id": {
          "$oid": "5818cafa090f9661f17c3d89"
        },
        "permissionId": 7,
        "parentPermissionType": "",
        "name": "Process EMV transaction",
        "tokenExpiryTime": 7689600000,
        "serviceOwner": "PS"
      }
    ],
    "serviceOwner": "PS"
  }
]
      """
  }

}