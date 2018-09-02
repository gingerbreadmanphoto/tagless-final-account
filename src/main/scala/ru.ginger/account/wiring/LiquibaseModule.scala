package ru.ginger.account.wiring

import com.typesafe.config.ConfigFactory
import ru.ginger.account.configuration.LiquibaseConfiguration
import ru.ginger.account.utils.migration.MigrationLiquibase

object LiquibaseModule {
  lazy val liquibaseConfiguration: LiquibaseConfiguration = new LiquibaseConfiguration(ConfigFactory.load())
  lazy val migrationsRunner: MigrationLiquibase = new MigrationLiquibase(liquibaseConfiguration)
}