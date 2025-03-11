package controllers

import play.api.mvc._
import play.api.db._
import play.api.db.evolutions._
import javax.inject._

@Singleton
class ApplicationEvolutions @Inject()(cc: ControllerComponents, dbApi: DBApi, evolutionsApi: EvolutionsApi) extends AbstractController(cc) {

  def showEvolutions() = Action {
    val db = dbApi.database("default")

    // Check if evolutions are available
    try {
      // Attempt to apply the evolutions for the database
      Evolutions.applyEvolutions(db)
      Ok("Evolutions applied successfully.")
    } catch {
      case e: Exception =>
        NotFound(s"Error applying evolutions: ${e.getMessage}")
    }
  }
}
