package dao

import models.Status
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

trait StatusesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class StatusesTable(tag: Tag)() extends Table[Status](tag, "statuses") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    override def * = (id.?, name) <> (Status.tupled, Status.unapply)
  }
}

@Singleton
class StatusDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends StatusesComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val statuses = TableQuery[StatusesTable]

  def insert(record: Status): Future[Unit] = db.run(statuses += record).map(_ => ())

  def all(): Future[Seq[Status]] = db.run(statuses.result)
}
