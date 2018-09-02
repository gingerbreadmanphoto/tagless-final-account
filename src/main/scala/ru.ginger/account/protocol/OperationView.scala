package ru.ginger.account.protocol

import java.time.LocalDate
import ru.ginger.account.model.OperationType.OperationType

case class OperationView(amount: BigDecimal,
                         date: LocalDate,
                         `type`: OperationType)