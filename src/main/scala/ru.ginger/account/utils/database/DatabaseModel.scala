package ru.ginger.account.utils.database

import slick.jdbc.JdbcProfile

trait DatabaseModel {
  val profile: JdbcProfile
}