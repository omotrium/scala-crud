
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

class DeleteProfileController @Inject() (
                                    protected val dbConfigProvider: DatabaseConfigProvider,
                                    override val uuidService: UuidService,
                                    cc: ControllerComponents,
                                    implicit val ec: ExecutionContext
                                  ) extends AbstractController(cc)
  with HasDatabaseConfigProvider[JdbcProfile]
  with ValidationRules {

  import profile.api._

  def deleteProfile(id: Long): Action[AnyContent] = Action.async {
    val deleteAction = ProfileTable.profiles.filter(_.id === id).delete
    val actionResult = dbConfig.db.run(deleteAction)
    actionResult.map {
      case 0 => NotFound
      case _ => NoContent
    }
  }


}


