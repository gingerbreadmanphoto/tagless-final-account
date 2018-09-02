package ru.ginger.account.utils.database

import slick.dbio.Effect.All
import slick.dbio.{DBIOAction, NoStream}

trait Database[+M <: DatabaseModel] {
  val model: M
}

object Database {
  type IO[+R] = DBIOAction[R, NoStream, All]
  val IO = DBIOAction
}