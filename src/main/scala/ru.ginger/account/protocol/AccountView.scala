package ru.ginger.account.protocol

case class AccountView(name: String,
                       amount: BigDecimal,
                       presentDayOperation: Seq[OperationView])