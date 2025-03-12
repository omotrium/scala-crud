
package models.errors

import play.api.libs.json.{Json, OFormat}

case class RouterError(status: Int, errorResponse: ErrorResponse)

object RouterError {
  implicit val format: OFormat[RouterError] = Json.format[RouterError]
}
