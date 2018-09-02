package ru.ginger.account.utils.instances

import ru.ginger.account.model.Account
import ru.ginger.account.protocol.{AccountView, OperationView}
import ru.ginger.account.utils.data.Convertable

private[instances] trait AccountInstances {
  implicit val accountConvertable: Convertable[Account, Seq[OperationView] => AccountView] = {
    x => operations => AccountView(x.name, x.amount, operations)
  }
}