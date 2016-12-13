package models

case class LoginRequest(username: String, password: String)
case class Customer(_id: String, customerName: String)
case class TokenClaim(userName: String, permissions: Map[String, Seq[Int]], tokenExpiryTime: Long)