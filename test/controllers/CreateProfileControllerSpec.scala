package controllers

import controllers.action.ValidationRules
import models.errors.{Error, ErrorResponse}
import models.ProfileTable
import org.apache.pekko.stream.Materializer
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.db.Database
import play.api.db.slick.DatabaseConfigProvider
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.test.{FakeRequest, _}
import play.api.test.Helpers._
import service.UuidService
import slick.jdbc.H2Profile.api._
import utils.ApplicationConstants.{BadRequestCode, BadRequestMessage}
import utils.HeaderNames

import java.util.UUID
import scala.concurrent.ExecutionContext

class CreateProfileControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with ScalaFutures
    with BeforeAndAfterEach {

  implicit lazy val materializer: Materializer = app.materializer
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  lazy val db: Database = app.injector.instanceOf[Database]
  //lazy val profileController: CreateProfileController = app.injector.instanceOf[CreateProfileController]

  val mockDbConfigProvider = mock[DatabaseConfigProvider]

  val profileController = new CreateProfileController(
    mockDbConfigProvider,
    mockUuidService,
    stubControllerComponents(),
    ec
  )

  val slickDb = Database.forConfig("slick.dbs.default.db")
  val mockUuidService: UuidService = mock[UuidService]

  def awaitDbSetup(): Unit = {
    val setup = slickDb.run(ProfileTable.profiles.schema.createIfNotExists)
    setup.futureValue
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    awaitDbSetup()
  }

  def validHeaders: Seq[(String, String)] = Seq(
    HeaderNames.Accept -> "application/json",
    HeaderNames.ContentType -> MimeTypes.JSON
  )
  "CreateProfileController" should {

    "create a profile successfully" in {
      val jsonBody = Json.obj(
        "name" -> "John",
        "email" -> "john.teec@example.com"
      )

      val request = FakeRequest(POST, "/api/profiles")
        .withHeaders(validHeaders: _*)
        .withJsonBody(jsonBody)

      val response = route(app, request).get // ✅ Better Play integration

      status(response) mustBe CREATED
      contentType(response) mustBe Some("application/json")
      (contentAsJson(response) \ "name").as[String] mustBe "John"
    }

    "return a BadRequest error when required fields are missing" in {
      val errorResponse = ErrorResponse(
        "8ebb6b04-6ab0-4fe2-ad62-e6389a8a204f",
        BadRequestCode,
        BadRequestMessage,
        Some(
          Seq(
            Error(
              "INVALID_REQUEST_PARAMETER",
              "Mandatory field email was missing from body or is in the wrong format",
              7
            ),
            Error(
              "INVALID_REQUEST_PARAMETER",
              "Mandatory field name was missing from body or is in the wrong format",
              6
            )
          )
        )
      )
      when(mockUuidService.uuid).thenReturn(
        "8ebb6b04-6ab0-4fe2-ad62-e6389a8a204f"
      )

      val jsonBody = Json.obj(
        "name" -> "",
        "email" -> ""
      )
      val result =
        profileController.createProfile()(
          FakeRequest(POST, "/api/profiles")
            .withHeaders(validHeaders: _*)
            .withJsonBody(jsonBody)
        )

      status(result) mustBe BAD_REQUEST
     // contentAsJson(result) mustBe Json.toJson(errorResponse)

//      val request = FakeRequest(POST, "/api/profiles")
//        .withHeaders(validHeaders: _*)
//        .withJsonBody(jsonBody)
//
//      val response = route(app, request).get
//
//      status(response) mustBe BAD_REQUEST
//      contentAsJson(response) mustBe Json.toJson(errorResponse)
    }

    "return a 400 when the Accept header is missing" in {
      val jsonBody = Json.obj(
        "name" -> "John",
        "email" -> "john.teec@example.com"
      )

      val request = FakeRequest(POST, "/api/profiles")
        .withHeaders()
        .withJsonBody(jsonBody)

      val response = route(app, request).get // ✅ Better Play integration

      status(response) mustBe BAD_REQUEST
    }

  }

}
