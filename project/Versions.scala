import sbt.librarymanagement.ModuleID

trait Versions {
  protected val slickVersion: String = "3.2.3"
  protected val akkaHttpVersion: String = "10.1.4"
  protected val playJsonVersion: String = "2.6.10"
  protected val playJsonSupportVersion: String = "1.21.0"
  protected val catsVersion: String = "1.2.0"
  protected val typeSafeConfigVersion: String = "1.3.3"
  protected val postgresVersion: String = "42.2.4"
  protected val liquibaseCoreVersion: String = "3.6.2"

  protected val mockitoVersion: String = "1.9.5"
  protected val scalaTestVersion: String = "3.0.5"
  protected val testContainersScalaVersion: String = "0.20.0"
  protected val postgresTestContainersVersion: String = "1.8.3"

  def dependencies: Seq[ModuleID]
  def testDependencies: Seq[ModuleID]
}