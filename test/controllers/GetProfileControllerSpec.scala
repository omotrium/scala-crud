
package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.{Profile, ProfileTable}
import org.apache.pekko.stream
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class GetProfileControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with ScalaFutures {

  implicit lazy val materializer: stream.Materializer = app.materializer
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  val db = Database.forConfig("test")

  val profileController = inject[GetProfileController]

  "GetProfileController" should {

//    "create a profile successfully" in {
//      val jsonBody = Json.obj(
//        "name" -> "John Doe",
//        "email" -> "john.doe@example.com"
//      )
//
//      val request = FakeRequest(POST, "/api/profiles")
//        .withHeaders("Content-Type" -> "application/json")
//        .withJsonBody(jsonBody)
//
//      val response = call(profileController.createProfile(), request)
//
//      status(response) mustBe CREATED
//      contentType(response) mustBe Some("application/json")
//      (contentAsJson(response) \ "name").as[String] mustBe "John Doe"
//    }
//
//    "fail to create a profile with missing fields" in {
//      val jsonBody = Json.obj(
//        "name" -> "John Doe"
//        // Missing email field
//      )
//
//      val request = FakeRequest(POST, "/api/profiles")
//        .withHeaders("Content-Type" -> "application/json")
//        .withJsonBody(jsonBody)
//
//      val response = call(profileController.createProfile(), request)
//
//      status(response) mustBe BAD_REQUEST
//    }

    "retrieve an existing profile" in {
      val profile = Profile(Some(1), "Jane Doe", "jane.doe@example.com")
      val insertAction = ProfileTable.profiles += profile
      db.run(insertAction).futureValue

      val request = FakeRequest(GET, s"/api/profiles/1")
      val response = call(profileController.getProfile(1), request)

      status(response) mustBe OK
      (contentAsJson(response) \ "name").as[String] mustBe "Jane Doe"
    }

    "return 404 when retrieving a non-existing profile" in {
      val request = FakeRequest(GET, "/api/profiles/999")
      val response = call(profileController.getProfile(999), request)

      status(response) mustBe NOT_FOUND
    }

//    "update a profile successfully" in {
//      val profile = Profile(Some(2), "John Doe", "john.doe@example.com")
//      db.run(ProfileTable.profiles += profile).futureValue
//
//      val updatedJson = Json.obj(
//        "name" -> "John Updated",
//        "email" -> "john.updated@example.com"
//      )
//
//      val request = FakeRequest(PUT, "/api/profiles/2")
//        .withHeaders("Content-Type" -> "application/json")
//        .withJsonBody(updatedJson)
//
//      val response = call(profileController.updateProfile(2), request)
//
//      status(response) mustBe OK
//      (contentAsJson(response) \ "name").as[String] mustBe "John Updated"
//    }
//
//    "return 404 when updating a non-existing profile" in {
//      val updatedJson = Json.obj(
//        "name" -> "Not Found",
//        "email" -> "notfound@example.com"
//      )
//
//      val request = FakeRequest(PUT, "/api/profiles/999")
//        .withHeaders("Content-Type" -> "application/json")
//        .withJsonBody(updatedJson)
//
//      val response = call(profileController.updateProfile(999), request)
//
//      status(response) mustBe NOT_FOUND
//    }
//
//    "delete an existing profile" in {
//      val profile = Profile(Some(3), "Jane Doe", "jane.doe@example.com")
//      db.run(ProfileTable.profiles += profile).futureValue
//
//      val request = FakeRequest(DELETE, "/api/profiles/3")
//      val response = call(profileController.deleteProfile(3), request)
//
//      status(response) mustBe OK
//    }
//
//    "return 404 when deleting a non-existing profile" in {
//      val request = FakeRequest(DELETE, "/api/profiles/999")
//      val response = call(profileController.deleteProfile(999), request)
//
//      status(response) mustBe NOT_FOUND
//    }
  }
}
