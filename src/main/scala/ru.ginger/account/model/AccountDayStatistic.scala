package ru.ginger.account.model

import ru.ginger.account.model.OperationType.OperationType

case class AccountDayStatistic(count: Int, amount: BigDecimal, `type`: OperationType)