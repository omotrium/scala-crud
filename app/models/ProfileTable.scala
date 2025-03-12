package models

import controllers.action.ValidationRules.Reads.lengthBetween
import play.api.libs.json.{JsPath, OWrites, Reads}
import play.api.libs.functional.syntax.toFunctionalBuilderOps

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.Function.unlift

// Define the Profile case class
case class Profile(id: Option[Long], name: String, email: String)

object Profile {

  implicit val reads: Reads[Profile] =
    ((JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read(lengthBetween(2, 50))and
      (JsPath \ "email").read[String](Reads.email)
  )(Profile.apply _)

  implicit lazy val writes: OWrites[Profile] =
    ((JsPath \ "id").writeNullable[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "email").write[String])(unlift(Profile.unapply))
}

// Define the ProfileTable class
class ProfileTable(tag: Tag) extends Table[Profile](tag, "PROFILES") {
  def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("NAME")
  def email: Rep[String] = column[String]("EMAIL", O.Unique)

  // Correct the mapping to support Option[Long]
  def * : ProvenShape[Profile] = (id.?, name, email) <> ((Profile.apply _).tupled, Profile.unapply)
}

// Define the ProfileTable object in the same file
object ProfileTable {
  val profiles = TableQuery[ProfileTable]
}

