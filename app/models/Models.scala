package models

case class Status(id: Option[Long], name: String)

case class Record(id: Option[Long], todoItem: String, statusId: Long)
