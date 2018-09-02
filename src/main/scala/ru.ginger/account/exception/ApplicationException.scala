package ru.ginger.account.exception

abstract class ApplicationException(val message: String) extends RuntimeException(message)