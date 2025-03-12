
package controllers.action

import controllers.action.ValidationRules.{BadRequestErrorResponse, extractSimplePaths}
import org.apache.commons.validator.routines.EmailValidator
import play.api.libs.functional.syntax.toApplicativeOps
import play.api.libs.json.Json.toJson
import play.api.libs.json.Reads.{maxLength, minLength, verifying}
import play.api.libs.json._
import play.api.mvc.Results.BadRequest
import play.api.mvc.{BaseController, Request, Result}
import service.UuidService
import utils.HeaderNames

import scala.concurrent.ExecutionContext
import models.errors.{Error, ErrorResponse}
import utils.ApplicationConstants._

trait ValidationRules {
  this: BaseController =>

  def uuidService: UuidService

  implicit def ec: ExecutionContext


  protected def validateAcceptHeader(implicit request: Request[_]): Either[Result, String] = {
    val pattern = """^application/vnd[.]{1}hmrc[.]{1}1{1}[.]0[+]{1}json$""".r
    request.headers
      .get(HeaderNames.Accept)
      .filter(pattern.matches(_))
      .toRight(
        BadRequestErrorResponse(
          uuidService.uuid,
          Seq(Error(InvalidHeader, InvalidOrMissingAccept, 4))
        ).asPresentation
      )
  }



  protected def validateRequestBody[A: Reads](
    fieldToErrorCodeTable: Map[String, (String, String)]
  )(implicit request: Request[JsValue]): Either[Result, A] =
    request.body
      .validate[A]
      .asEither
      .left
      .map(x =>
        BadRequestErrorResponse(
          uuidService.uuid,
          convertError(x, fieldToErrorCodeTable)
        ).asPresentation
      )





  private def convertError(
    errors: scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])],
    fieldToErrorCodeTable: Map[String, (String, String)]
  ): Seq[Error] =
    extractSimplePaths(errors)
      .map(key => fieldToErrorCodeTable.get(key).map(res => Error.invalidRequestParameterError(res._2, res._1.toInt)))
      .toSeq
      .flatten
}

object ValidationRules {


  private val emailValidator: EmailValidator = EmailValidator.getInstance(true)

  object Reads {
    def lengthBetween(min: Int, max: Int): Reads[String] =
      minLength[String](min).keepAnd(maxLength[String](max))


    val validEmailAddress: Reads[String] = verifying(isValidEmailAddress)

  }

  def isValidEmailAddress(emailAddress: String): Boolean = emailValidator.isValid(emailAddress)

  private def extractSimplePaths(
    errors: scala.collection.Seq[(JsPath, collection.Seq[JsonValidationError])]
  ): collection.Seq[String] =
    errors
      .map(_._1)
      .map(_.path.filter(_.isInstanceOf[KeyPathNode]))
      .map(_.mkString)

  val fieldsToErrorCode: Map[String, (String, String)] = Map(
    "/name"                                       -> (InvalidOrMissingNameCode, InvalidOrMissingName)
  )

  case class BadRequestErrorResponse(correlationId: String, errors: Seq[Error]) {
    def asPresentation: Result =
      BadRequest(
        toJson(
          ErrorResponse(
            correlationId,
            BadRequestCode,
            BadRequestMessage,
            Some(errors)
          )
        )
      )
  }

  object BadRequestErrorResponse {
    implicit val format: OFormat[BadRequestErrorResponse] = Json.format[BadRequestErrorResponse]
  }
}
