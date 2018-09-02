package ru.ginger.account.utils.instances

import cats.Monad
import ru.ginger.account.utils.data.Transactionable
import ru.ginger.account.utils.database.Database.IO
import scala.concurrent.ExecutionContext

trait DBIOInstances {
  implicit def dbioTransactionable(implicit profile: slick.jdbc.JdbcProfile): Transactionable[IO] = new Transactionable[IO] {
    import profile.api._
    override def transaction[X](f: IO[X]): IO[X] = f.transactionally
  }

  implicit def dbioMonad(implicit ec: ExecutionContext): Monad[IO] = new Monad[IO] {
    override def pure[A](x: A): IO[A] = IO.successful(x)

    override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B]]): IO[B] = {
      f(a).flatMap {
        case Right(value) => IO.successful(value)
        case Left(x) => tailRecM(x)(f)
      }
    }
  }
}