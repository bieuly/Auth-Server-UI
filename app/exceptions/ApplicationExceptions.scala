package exceptions

/**
  * Created by billyhoang on 2016-12-17.
  */

class UserNotAuthorizedException(message: String = "User is not authorized", cause: Throwable = null) extends Exception(message, cause)
class ParseTokenClaimException(message: String = "Unable to parse token claim", cause: Throwable = null) extends Exception(message, cause)
class TokenVerifyException(message: String = "Unable to verify token", cause: Throwable = null) extends Exception(message, cause)
class RetrieveTokenException(message: String = "Unable to retrieve token from request", cause: Throwable = null) extends Exception(message, cause)
