package ru.ginger.account.model.db

import ru.ginger.account.model.Account
import ru.ginger.account.utils.database.DatabaseModel
import slick.lifted.ProvenShape

trait AccountModel extends DatabaseModel {

  import profile.api._

  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey)
    def name: Rep[String] = column[String]("name")
    def amount: Rep[BigDecimal] = column[BigDecimal]("amount")
    def countOperations: Rep[Long] = column[Long]("operation_count")

    override def * : ProvenShape[Account] = (id, name, countOperations, amount) <> ((Account.apply _).tupled, Account.unapply)
  }

  val accounts = TableQuery[AccountTable]
}