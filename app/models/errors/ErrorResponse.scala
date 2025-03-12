
package models.errors

import play.api.libs.json.{Json, OFormat}
import utils.ApplicationConstants.{InvalidRequestParameters, UnexpectedErrorCode}

case class ErrorResponse(
  correlationId: String,
  code: String,
  message: String,
  errors: Option[Seq[Error]] = None
)

case class Error(code: String, message: String, errorNumber: Int)

object ErrorResponse {
  implicit val format: OFormat[ErrorResponse] = Json.format[ErrorResponse]
}

object Error {
  implicit val format: OFormat[Error] = Json.format[Error]

  def invalidRequestParameterError(message: String, errorNumber: Int): Error =
    Error(InvalidRequestParameters, message, errorNumber)

  def unexpectedError(message: String, errorNumber: Int): Error =
    Error(UnexpectedErrorCode, message, errorNumber)
}

case class ErrorType(httpStatus: Int, errorMessage: String, errorResponse: String)

