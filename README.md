Application *BANK ACCOUNT*

- Application start: `./start.sh`
- Coverage report: `./coverage.sh`

1) `GET localhost:8080/account/1?withOperations=(true|false)` - Get account with or without operations (true by default) for present day
2) `POST localhost:8080/account/1/deposit { "amount": 10000 }` - Deposit money to the account
3) `POST localhost:8080/account/1/withdraw { "amount": 10000 }` - Withdraw money from the account