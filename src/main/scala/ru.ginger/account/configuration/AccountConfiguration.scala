package ru.ginger.account.configuration

import com.typesafe.config.Config

class AccountConfiguration(config: Config) {

  lazy val httpPort: Int = config.getInt(AccountConfiguration.httpPortKey)
  lazy val httpHost: String = config.getString(AccountConfiguration.httpHostKey)

  lazy val withdrawalMaxAmountLimitPerDay: Int = config.getInt(AccountConfiguration.WithdrawalMaxAmountLimitPerDayKey)
  lazy val withdrawalMaxAmountLimitPerOperation: Int = config.getInt(AccountConfiguration.WithdrawalMaxAmountLimitPerOperationKey)
  lazy val withdrawalAllowableOperationCount: Int = config.getInt(AccountConfiguration.WithdrawalAllowableOperationCountKey)

  lazy val depositMaxAmountLimitPerDay: Int = config.getInt(AccountConfiguration.DepositMaxAmountLimitPerDayKey)
  lazy val depositMaxAmountLimitPerOperation: Int = config.getInt(AccountConfiguration.DepositMaxAmountLimitPerOperationKey)
  lazy val depositAllowableOperationCount: Int = config.getInt(AccountConfiguration.DepositAllowableOperationCountKey)
}

object AccountConfiguration {
  val httpPortKey = "account.http.port"
  val httpHostKey = "account.http.host"

  val WithdrawalMaxAmountLimitPerDayKey = "account.withdrawal.max-amount-per-day"
  val WithdrawalMaxAmountLimitPerOperationKey = "account.withdrawal.max-amount-per-operation"
  val WithdrawalAllowableOperationCountKey = "account.withdrawal.count-per-day"

  val DepositMaxAmountLimitPerDayKey = "account.deposit.max-amount-per-day"
  val DepositMaxAmountLimitPerOperationKey = "account.deposit.max-amount-per-operation"
  val DepositAllowableOperationCountKey = "account.deposit.count-per-day"
}