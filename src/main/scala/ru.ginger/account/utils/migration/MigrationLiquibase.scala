package ru.ginger.account.utils.migration

import java.sql.{Connection, DriverManager}

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import ru.ginger.account.configuration.LiquibaseConfiguration

class MigrationLiquibase(config: LiquibaseConfiguration) {
  Class.forName("org.postgresql.Driver")

  def createLiquibase(dbConnection: Connection, diffFilePath: String): Liquibase = {
    val database = DatabaseFactory.getInstance.findCorrectDatabaseImplementation(new JdbcConnection(dbConnection))
    val classLoader = classOf[MigrationLiquibase].getClassLoader
    val resourceAccessor = new ClassLoaderResourceAccessor(classLoader)

    new Liquibase(diffFilePath, resourceAccessor, database)
  }

  def updateDb(diffFilePath: String): Unit = {
    val dbConnection = DriverManager.getConnection(config.databaseUrl,config.databaseUser, config.databasePassword)
    val liquibase = createLiquibase(dbConnection, diffFilePath)
    try {
      val contexts: String = null
      liquibase.update(contexts)
    } catch {
      case e: Throwable => throw e
    } finally {
      liquibase.forceReleaseLocks()
      dbConnection.rollback()
      dbConnection.close()
    }
  }

  def run(): Unit = {
    println(s"SQL Migrations is running")
    updateDb(config.migrationPath)
  }
}
