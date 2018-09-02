package ru.ginger.account.utils.instances

import ru.ginger.account.model.Operation
import ru.ginger.account.protocol.OperationView
import ru.ginger.account.utils.data.Convertable

trait OperationInstances {
  implicit val operationViewConvertable: Convertable[Operation, OperationView] = {
    x => OperationView(x.amount, x.date, x.`type`)
  }
}