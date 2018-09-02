package ru.ginger.account

import ru.ginger.account.wiring.{AccountHttp, LiquibaseModule}

object Application extends App {
  LiquibaseModule.migrationsRunner.run()
  AccountHttp.run()
}