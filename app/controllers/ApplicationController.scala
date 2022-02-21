package controllers

import dao.{RecordDao, StatusDao}
import models.Record
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ApplicationController @Inject()(recordDao: RecordDao,
                                      statusDao: StatusDao,
                                      val controllerComponents: ControllerComponents)
                                     (implicit executionContext: ExecutionContext) extends BaseController {

  case class RecordWithStatus(recordId: Long, statusId: Long)

  val recordForm: Form[Record] = Form(
    mapping(
      "id" -> optional(longNumber),
      "todoItem" -> nonEmptyText,
      "statusId" -> ignored(1L)
    )(Record.apply)(Record.unapply)
  )

  val statusForm: Form[RecordWithStatus] = Form(
    mapping(
      "recordId" -> longNumber,
      "statusId" -> longNumber
    )(RecordWithStatus.apply)(RecordWithStatus.unapply)
  )

  def insertRecord(): Action[AnyContent] = Action.async { implicit request =>
    val record: Record = recordForm.bindFromRequest().get
    recordDao.insert(record).map(_ => Redirect(routes.ApplicationController.index()))
  }

  def deleteRecord(id: Long): Action[AnyContent] = Action.async { implicit request =>
    recordDao.delete(id).map(_ => Redirect(routes.ApplicationController.index()))
  }

  def updateRecord(): Action[AnyContent] = Action.async { implicit request =>
    val recordWithStatus: RecordWithStatus = statusForm.bindFromRequest().get
    recordDao.updateStatus(recordWithStatus.recordId, recordWithStatus.statusId)
      .map(_ => Redirect(routes.ApplicationController.index()))
  }

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    recordDao.all().zip(statusDao.all()).map {
      case (recordsWithStatus, statuses) => Ok(views.html.index(recordsWithStatus, recordForm, statuses))
    }
  }
}
