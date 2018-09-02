package ru.ginger.account.controller

import akka.http.scaladsl.server.Route
import ru.ginger.account.service.AccountControllerService
import ru.ginger.account.utils.controller.Controller
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.ginger.account.format.JsonFormat._
import ru.ginger.account.protocol.{DepositRequest, WithdrawalRequest}
import ru.ginger.account.utils.data.Routable
import scala.language.higherKinds

class AccountController[F[_]](accountControllerService: AccountControllerService[F])
                             (implicit routable: Routable[F]) extends Controller {

  override def route: Route = {
    pathPrefix("account") {
      pathPrefix(LongNumber) { accountId =>
        pathEnd {
          getAccount(accountId)
        } ~
        path("deposit") {
          depositRequest(accountId)
        } ~
        path("withdraw") {
          withdrawalRequest(accountId)
        }
      }
    }
  }

  protected def getAccount(id: Long): Route = {
    (get & parameter("withOperations".as[Boolean] ? true)) { withOperations =>
      routable.route(accountControllerService.get(id, withOperations))
    }
  }

  protected def depositRequest(id: Long): Route = {
    (post & entity(as[DepositRequest])) { request =>
      routable.route(accountControllerService.deposit(id, request))
    }
  }

  protected def withdrawalRequest(id: Long): Route = {
    (post & entity(as[WithdrawalRequest])) { request =>
      routable.route(accountControllerService.withdraw(id, request))
    }
  }
}