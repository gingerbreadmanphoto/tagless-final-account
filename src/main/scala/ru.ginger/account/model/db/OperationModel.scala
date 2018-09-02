package ru.ginger.account.model.db

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime, LocalTime}
import ru.ginger.account.model.{Operation, OperationType}
import ru.ginger.account.model.OperationType.OperationType
import ru.ginger.account.utils.database.DatabaseModel

trait OperationModel extends DatabaseModel {

  import profile.api._

  implicit lazy val operationTypeMapped: BaseColumnType[OperationType] = MappedColumnType.base[OperationType, String](
    _.toString, OperationType.withName
  )

  implicit lazy val localDateMapped: BaseColumnType[LocalDate] = MappedColumnType.base[LocalDate, Timestamp](
    ld => Timestamp.valueOf(LocalDateTime.of(ld, LocalTime.MIDNIGHT)),
    ts => ts.toLocalDateTime.toLocalDate
  )

  class OperationTable(tag: Tag) extends Table[Operation](tag, "operations") {

    def accountId: Rep[Long] = column[Long]("account_id")
    def date: Rep[LocalDate] = column[LocalDate]("date")
    def amount: Rep[BigDecimal] = column[BigDecimal]("amount")
    def `type`: Rep[OperationType] = column[OperationType]("type")

    override def * = (accountId, amount, date, `type`) <> ((Operation.apply _).tupled, Operation.unapply)
  }

 val operations = TableQuery[OperationTable]
}