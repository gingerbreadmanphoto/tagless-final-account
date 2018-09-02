package ru.ginger.account.configuration

import com.typesafe.config.Config

class LiquibaseConfiguration(config: Config) {
  lazy val databasePassword: String = config.getString(LiquibaseConfiguration.DatabasePasswordKey)
  lazy val databaseUser: String = config.getString(LiquibaseConfiguration.DatabaseUserKey)
  lazy val databaseUrl: String = config.getString(LiquibaseConfiguration.DatabaseUrlKey)

  val migrationPath: String = "migrations/changelog.xml"
}

object LiquibaseConfiguration {
  val DatabasePasswordKey: String = "account.db.properties.password"
  val DatabaseUserKey: String = "account.db.properties.user"
  val DatabaseUrlKey: String = "account.db.properties.url"
}