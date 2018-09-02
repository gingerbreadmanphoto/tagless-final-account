package modules

import com.typesafe.config.ConfigFactory
import ru.ginger.account.configuration.AccountConfiguration
import ru.ginger.account.utils.database.Runner
import scala.util.Try

object TryCoreModule {
  lazy val accountConfiguration: AccountConfiguration = new AccountConfiguration(ConfigFactory.load())

  lazy val databaseRunner: Runner[Try, Try] = new Runner[Try, Try] {
    override def run[X](f: Try[X]): Try[X] = f
  }
}