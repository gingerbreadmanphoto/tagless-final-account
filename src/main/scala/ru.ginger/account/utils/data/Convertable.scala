package ru.ginger.account.utils.data

trait Convertable[X, Y] {
  def convert(x: X): Y
}