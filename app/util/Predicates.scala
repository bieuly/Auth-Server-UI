package util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by billyhoang on 2016-12-17.
  */
object Predicates {
  def checkAndThrowExceptionIfFails(condition: Boolean)(fail: => Exception): Future[Unit] = {
    if (condition) Future.successful(()) else Future.failed(fail)
  }
}
