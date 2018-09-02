package ru.ginger.account.utils.data

import scala.language.higherKinds

trait Transactionable[F[_]] {
  def transaction[X](f: F[X]): F[X]
}