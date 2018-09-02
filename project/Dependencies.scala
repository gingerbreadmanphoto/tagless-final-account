import sbt._
import sbt.librarymanagement._

object Dependencies extends Versions {
  override def dependencies: Seq[ModuleID] = Seq(
    "de.heikoseeberger" %% "akka-http-play-json" % playJsonSupportVersion,
    "com.typesafe.play" %% "play-json" % playJsonVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.slick" %% "slick" % slickVersion,
    "org.typelevel" %% "cats-core" % catsVersion,
    "com.typesafe" % "config" % typeSafeConfigVersion,
    "org.postgresql" % "postgresql" % postgresVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "org.liquibase" % "liquibase-core" % liquibaseCoreVersion
  )

  override def testDependencies: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "org.mockito" % "mockito-all" % mockitoVersion % Test,
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "com.dimafeng" %% "testcontainers-scala" % testContainersScalaVersion % Test,
    "org.testcontainers" % "postgresql" % postgresTestContainersVersion
  )
}