package ru.ginger.account.utils.database

import ru.ginger.account.utils.database.Database.IO
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import scala.language.higherKinds

trait Runner[F[_], M[_]] { // FunctionK
  def run[X](f: F[X]): M[X]
}

class FutureIORunner(name: String, val profile: JdbcProfile) extends Runner[IO, Future] {
  override def run[X](f: IO[X]): Future[X] = db.run(f)

  // internal
  private lazy val db: profile.backend.DatabaseDef = profile.backend.Database.forConfig(name)
}