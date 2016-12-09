package models

case class LoginRequest(username: String, password: String)
case class Customer(_id: String, firstName: String, lastName: String)
case class TokenClaim(userName: String, permissions: Map[String, Seq[Int]], tokenExpiryTime: Long)