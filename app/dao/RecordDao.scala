package dao

import models.Record
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RecordDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends StatusesComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private class RecordTable(tag: Tag) extends Table[Record](tag, "records") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def todoItem = column[String]("todo_item")

    def statusId = column[Long]("status_id")

    override def * = (id.?, todoItem, statusId) <> (Record.tupled, Record.unapply)
  }

  private val records = TableQuery[RecordTable]
  private val statuses = TableQuery[StatusesTable]

  def insert(record: Record): Future[Unit] = db.run(records += record).map(_ => ())

  def all(): Future[Seq[(Record, Long)]] = {
    val query = for {
      (record, status) <- records join statuses on (_.statusId === _.id)
    } yield (record, status.id)

    db.run(query.sortBy(_._1.id.desc.desc).result)
  }

  def updateStatus(recordId: Long, statusId: Long): Future[Unit] = {
    val query = records.filter(_.id === recordId).map(r => r.statusId).update(statusId);
    db.run(query).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(records.filter(_.id === id).delete).map(_ => ())
}
