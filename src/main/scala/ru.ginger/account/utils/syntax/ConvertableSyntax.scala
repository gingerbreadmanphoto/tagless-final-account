package ru.ginger.account.utils.syntax

import ru.ginger.account.utils.data.Convertable

private[syntax] trait ConvertableSyntax {
  implicit class ConvertableOps[X](value: X) {
    def convert[Y](implicit convertable: Convertable[X, Y]): Y = {
      convertable.convert(value)
    }
  }
}