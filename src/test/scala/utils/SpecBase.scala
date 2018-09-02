package utils

import org.scalatest.Matchers
import org.scalatest.concurrent.{AbstractPatienceConfiguration, ScalaFutures}
import org.scalatest.mockito.MockitoSugar

trait SpecBase extends MockitoSugar with AbstractPatienceConfiguration with Matchers with ScalaFutures {
  override implicit val patienceConfig: PatienceConfig = PatienceConfig()
}