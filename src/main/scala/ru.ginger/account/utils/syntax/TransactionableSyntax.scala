package ru.ginger.account.utils.syntax

import ru.ginger.account.utils.data.Transactionable
import scala.language.higherKinds

private[syntax] trait TransactionableSyntax {
  implicit class TransactionableOps[F[_], X](value: F[X]) {
    def transaction(implicit transactionable: Transactionable[F]): F[X] = {
      transactionable.transaction(value)
    }
  }
}