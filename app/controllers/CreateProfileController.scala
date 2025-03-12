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

class CreateProfileController @Inject()(
                                    protected val dbConfigProvider: DatabaseConfigProvider,
                                    override val uuidService: UuidService,
                                    cc: ControllerComponents,
                                    implicit val ec: ExecutionContext
                                  ) extends AbstractController(cc)
  with HasDatabaseConfigProvider[JdbcProfile]
  with ValidationRules {

  import profile.api._

  def createProfile(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    // Use for comprehension to combine validation steps and profile insertion
    val result = for {
      // Validate Accept header
      _ <- EitherT.fromEither[Future](validateAcceptHeader)

      // Validate request body and extract profile if valid
      profile <- EitherT.fromEither[Future](validateRequestBody[Profile](fieldsToErrorCode))

      // Insert the profile into the database, while checking for conflicts
      response <- insertProfile(profile)
    } yield response

    // Merge the result, returning either the successful response or an error
    result.merge
  }

  private def insertProfile(profile: Profile): EitherT[Future, Result, Result] = {
    // Prepare the profile to insert
    val profileToInsert = Profile(None, profile.name, profile.email)

    // Check if profile with the same email already exists
    val existingProfileQuery = ProfileTable.profiles.filter(_.email === profile.email).exists.result

    // Execute the actions in sequence
    EitherT(db.run(existingProfileQuery).flatMap {
      case true => // If profile exists, return conflict error
        Future.successful(Left(Conflict(Json.obj("error" -> s"Email '${profile.email}' is already in use"))))

      case false =>
        // If no conflict, proceed with inserting the profile
        val insertAction = ProfileTable.profiles += profileToInsert
        db.run(insertAction).map { _ =>
          Right(Created(Json.toJson(profileToInsert))) // Return the created profile as a success
        }.recover {
          case ex: Exception =>
            Left(InternalServerError(Json.obj("error" -> s"Failed to insert profile: ${ex.getMessage}")))
        }
    })
  }

}

