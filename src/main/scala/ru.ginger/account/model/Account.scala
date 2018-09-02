package ru.ginger.account.model

case class Account(id: Long,
                   name: String,
                   countOperations: Long,
                   amount: BigDecimal)

case class AccountUpdateData(oldAmount: BigDecimal, newAmount: BigDecimal, oldCount: Long)