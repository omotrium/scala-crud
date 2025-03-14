package controllers

import cats.data.EitherT
import controllers.action.ValidationRules
import controllers.action.ValidationRules.fieldsToErrorCode
import service.UuidService
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import models._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CreateProfileController @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    override val uuidService: UuidService,
    cc: ControllerComponents,
    implicit val ec: ExecutionContext
) extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile]
    with ValidationRules {

  import profile.api._

  def createProfile(): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      val result = for {
        _ <- EitherT.fromEither[Future](validateAcceptHeader)
        profile <- EitherT.fromEither[Future](
          validateRequestBody[Profile](fieldsToErrorCode)
        )
        response <- insertProfile(profile)
      } yield response
      result.merge
  }

  private def insertProfile(
      profile: Profile
  ): EitherT[Future, Result, Result] = {
    val profileToInsert = Profile(None, profile.name, profile.email)
    val existingProfileQuery =
      ProfileTable.profiles.filter(_.email === profile.email).exists.result
    EitherT(db.run(existingProfileQuery).flatMap {
      case true =>
        Future.successful(
          Left(
            Conflict(
              Json.obj("error" -> s"Email '${profile.email}' is already in use")
            )
          )
        )
      case false =>
        val insertAction = ProfileTable.profiles += profileToInsert
        db.run(insertAction)
          .map { _ =>
            Right(Created(Json.toJson(profileToInsert)))
          }
          .recover { case ex: Exception =>
            Left(
              InternalServerError(
                Json
                  .obj("error" -> s"Failed to insert profile: ${ex.getMessage}")
              )
            )
          }
    })
  }

}
