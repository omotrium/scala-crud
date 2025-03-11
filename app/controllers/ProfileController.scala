package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.H2Profile.api._
import models._
import slick.jdbc.{H2Profile, JdbcProfile}

import scala.concurrent.{ExecutionContext, Future}

class ProfileController @Inject() (
                                    protected val dbConfigProvider: DatabaseConfigProvider,
                                    cc: ControllerComponents
                                  )(implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def createProfile() = Action.async(parse.json) { request =>
    request.body.validate[Profile].map { profile =>
      val profileToInsert = Profile(None, profile.name, profile.email)
      val insertAction = ProfileTable.profiles += profileToInsert
      db.run(insertAction).map { _ =>
        Created(Json.toJson(profileToInsert))
      }
    }.getOrElse(Future.successful(BadRequest("Invalid JSON")))
  }

//  def createProfile2() = Action.async(parse.json) { request =>
//    request.body.validate[Profile].map { profile =>
//      val profileToInsert = Profile(None, profile.name, profile.email)
//      val insertAction = ProfileTable.profiles += profileToInsert
//      val actionResult = dbConfig.db.run(insertAction)
//      actionResult.map { _ =>
//        Created(Json.toJson(profileToInsert))
//      }
//    }.getOrElse(Future.successful(BadRequest("Invalid JSON")))
//  }

  def getAllProfiles() = Action.async {
    val action = ProfileTable.profiles.result
    val profilesFuture = dbConfig.db.run(action)
    profilesFuture.map { profiles =>
      Ok(Json.toJson(profiles))
    }
  }

  def getProfile(id: Long) = Action.async {
    val query = ProfileTable.profiles.filter(_.id === id).result.headOption
    val profileFuture = dbConfig.db.run(query)
    profileFuture.map {
      case Some(profile) => Ok(Json.toJson(profile))
      case None => NotFound
    }
  }

  def updateProfile(id: Long) = Action.async(parse.json) { request =>
    request.body.validate[Profile].map { profile =>
      val updateAction = ProfileTable.profiles.filter(_.id === id).map(p => (p.name, p.email))
        .update(profile.name, profile.email)
      val actionResult = dbConfig.db.run(updateAction)
      actionResult.map {
        case 0 => NotFound
        case _ => NoContent
      }
    }.getOrElse(Future.successful(BadRequest("Invalid JSON")))
  }

  def deleteProfile(id: Long) = Action.async {
    val deleteAction = ProfileTable.profiles.filter(_.id === id).delete
    val actionResult = dbConfig.db.run(deleteAction)
    actionResult.map {
      case 0 => NotFound
      case _ => NoContent
    }
  }


}

