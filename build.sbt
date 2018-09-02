name := "bank"

version := "0.1"

lazy val dependencySettings: SettingsDefinition = Seq(
  libraryDependencies ++= Dependencies.dependencies,
  libraryDependencies ++= Dependencies.testDependencies
)

lazy val dockerSettings: SettingsDefinition = Seq(
  dockerAlias := DockerAlias(None, None,"account-bank", Some("interview")),
  dockerExposedPorts := Seq(8080),
  dockerBuildOptions += "--no-cache"
)

lazy val bank = (project in file(".")).settings(
  scalaVersion := "2.12.6",
  dependencySettings,
  dockerSettings
).enablePlugins(JavaAppPackaging, DockerPlugin)
