package models

import play.api.libs.json.{Json, OFormat}
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

// Define the Profile case class
case class Profile(id: Option[Long], name: String, email: String)

object Profile {
  // Add implicit JSON formatter
  implicit val profileFormat: OFormat[Profile] = Json.format[Profile]
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

