package ru.ginger.account.controller

import java.time.LocalDate
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.FlatSpec
import ru.ginger.account.service.AccountControllerService
import utils.SpecBase
import akka.http.scaladsl.model.StatusCodes.OK
import scala.util.{Success, Try}
import ru.ginger.account.utils.instances.`try`._
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => calledWith, _}
import play.api.libs.json.JsValue
import ru.ginger.account.model.OperationType
import ru.ginger.account.protocol.{AccountView, DepositRequest, OperationView, WithdrawalRequest}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import ru.ginger.account.format.JsonFormat._

class AccountControllerSpec extends FlatSpec with SpecBase with ScalatestRouteTest {

  "GET /account/{id}?withOperations=false" should "return the account by id without operations" in new TestModule {
    when(mockAccountControllerService.get(any(), any())).thenReturn(Success(sampleAccountViewWithoutOperations))

    Get(Uri(s"/account/$sampleAccountId?withOperations=false")) ~> controller.route ~> check {
      verify(mockAccountControllerService).get(
        calledWith(sampleAccountId),
        calledWith(false)
      )

      handled shouldBe true
      status shouldBe OK
      val response = responseAs[JsValue]
      (response \ "result").as[AccountView] shouldBe sampleAccountViewWithoutOperations
    }
  }

  it should "return an account by id with operations" in new TestModule {
    when(mockAccountControllerService.get(any(), any())).thenReturn(Success(sampleAccountViewWithOperations))

    Get(Uri(s"/account/$sampleAccountId")) ~> controller.route ~> check {
      verify(mockAccountControllerService).get(
        calledWith(sampleAccountId),
        calledWith(true)
      )

      handled shouldBe true
      status shouldBe OK
      val response = responseAs[JsValue]
      (response \ "result").as[AccountView] shouldBe sampleAccountViewWithOperations
    }
  }

  "POST /account/{id}/deposit" should "deposit money to the account" in new TestModule {
    when(mockAccountControllerService.deposit(any(), any())).thenReturn(Success(()))

    Post(Uri(s"/account/$sampleAccountId/deposit"), sampleDepositRequest) ~> controller.route ~> check {
      verify(mockAccountControllerService).deposit(
        calledWith(sampleAccountId),
        calledWith(sampleDepositRequest)
      )

      handled shouldBe true
      status shouldBe OK
      val response = responseAs[JsValue]
      (response \ "result").as[String] shouldBe "OK"
    }
  }

  "POST /account/{id}/withdraw" should "withdraw money from the account" in new TestModule {
    when(mockAccountControllerService.withdraw(any(), any())).thenReturn(Success(()))

    Post(Uri(s"/account/$sampleAccountId/withdraw"), sampleWithdrawalRequest) ~> controller.route ~> check {
      verify(mockAccountControllerService).withdraw(
        calledWith(sampleAccountId),
        calledWith(sampleWithdrawalRequest)
      )

      handled shouldBe true
      status shouldBe OK
      val response = responseAs[JsValue]
      (response \ "result").as[String] shouldBe "OK"
    }
  }

  private trait TestModule {
    protected val mockAccountControllerService: AccountControllerService[Try] = mock[AccountControllerService[Try]]

    protected val controller: AccountController[Try] = new AccountController[Try](
      mockAccountControllerService
    )
  }

  private val sampleAccountId = 1L
  private val sampleAccountName = "sampleAccountName"
  private val sampleAccountAmount = BigDecimal(1000)
  private val sampleWidrawalAmount = BigDecimal(500)
  private val sampleDepositAmount = BigDecimal(1500)
  private val sampleOperationDate = LocalDate.now()

  private val sampleWithdrawalOperationView: OperationView = OperationView(
    amount = sampleWidrawalAmount,
    date = sampleOperationDate,
    `type` = OperationType.Withdrawal
  )

  private val sampleDepositOperationView: OperationView = OperationView(
    amount = sampleDepositAmount,
    date = sampleOperationDate,
    `type` = OperationType.Deposit
  )

  private val sampleAccountViewWithOperations: AccountView = AccountView(
    name = sampleAccountName,
    amount = sampleAccountAmount,
    presentDayOperation = Seq(
      sampleWithdrawalOperationView,
      sampleDepositOperationView
    )
  )

  private val sampleAccountViewWithoutOperations: AccountView = AccountView(
    name = sampleAccountName,
    amount = sampleAccountAmount,
    presentDayOperation = Seq.empty
  )

  private val sampleWithdrawalRequest = WithdrawalRequest(sampleWidrawalAmount)
  private val sampleDepositRequest = DepositRequest(sampleDepositAmount)
}