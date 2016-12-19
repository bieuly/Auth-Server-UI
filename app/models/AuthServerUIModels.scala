package models


// For Auth UI
case class LoginRequest(username: String, password: String)
case class Customer(_id: String, customerName: String)
case class TokenClaim(userName: String, permissions: Map[String, Seq[Int]], tokenExpiryTime: Long)
case class UserCreationRequest(userName: String, password: String, firstName: String, lastName: String, customerId: String, userStatusType: String, userType: String, roles: List[String])
case class CustomerCreationRequest(customerName: String)