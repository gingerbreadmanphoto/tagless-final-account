package modules

import com.dimafeng.testcontainers.PostgreSQLContainer
import ru.ginger.account.model.{Account, Operation}
import ru.ginger.account.model.db.{AccountModel, OperationModel}
import ru.ginger.account.utils.database.Database.IO
import ru.ginger.account.utils.database.{Database, Runner}
import slick.jdbc.PostgresProfile
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait TestDatabaseModule {

  protected val testContainer: PostgreSQLContainer

  protected lazy val jdbcUrl: String = s"${testContainer.jdbcUrl}?user=${testContainer.username}&password=${testContainer.password}"

  protected lazy val databaseModel = new AccountModel with OperationModel {
    override val profile = PostgresProfile
  }

  protected lazy val profile: slick.jdbc.JdbcProfile = PostgresProfile
  protected lazy val databaseRunner: Runner[IO, Future] = new TestFutureIORunner(jdbcUrl)

  protected lazy val database: Database[databaseModel.type] = new Database[databaseModel.type] {
    override val model: databaseModel.type = databaseModel
  }

  import profile.api._

  protected def initSchema(): Unit  = {
    runSync((databaseModel.operations.schema ++ databaseModel.accounts.schema).create)
  }

  protected def insertAccount(account: Account): Unit = {
    runSync(databaseModel.accounts += account)
  }

  protected def insertOperations(operations: Seq[Operation]): Unit  = {
    runSync(databaseModel.operations ++= operations)
  }

  // internal

  private def runSync[X](io: IO[X]): X = {
    Await.result(databaseRunner.run(io), 30.seconds)
  }

  private[this] class TestFutureIORunner(url: String) extends Runner[IO, Future] {
    override def run[X](f: IO[X]): Future[X] = db.run(f)

    // internal
    private lazy val db: profile.backend.DatabaseDef = profile.backend.Database.forURL(url)
  }
}