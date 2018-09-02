package ru.ginger.account.wiring

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import ru.ginger.account.configuration.AccountConfiguration
import ru.ginger.account.model.db.{AccountModel, OperationModel}
import ru.ginger.account.utils.database.Database.IO
import ru.ginger.account.utils.database.{Database, FutureIORunner, Runner}
import slick.jdbc.PostgresProfile
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object FutureCoreModule {
  lazy val accountConfiguration: AccountConfiguration = new AccountConfiguration(ConfigFactory.load())

  implicit lazy val actorSystem: ActorSystem = ActorSystem("Accounts")
  implicit lazy val ec: ExecutionContext = actorSystem.dispatcher
  implicit lazy val materializer: Materializer = ActorMaterializer()

  sys.addShutdownHook {
    Await.result(actorSystem.terminate(), Duration.Inf)
    println("ActorSystem has been terminated")
  }

  lazy val databaseModel = new AccountModel with OperationModel {
    override val profile = PostgresProfile
  }

  implicit lazy val profile: slick.jdbc.JdbcProfile = PostgresProfile
  lazy val databaseRunner: Runner[IO, Future] = new FutureIORunner("account.db", PostgresProfile)

  lazy val database: Database[databaseModel.type] = new Database[databaseModel.type] {
    override val model: databaseModel.type = databaseModel
  }
}