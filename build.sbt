import sbt.Keys.name

version := "0.1"

scalaVersion := scVersion

name := "sensor-service"

val appName = "sensor-service"
val pipelineName = "sensor-service-pipeline"

val localConfigPath = "src/main/resources/local.conf"
val localLog4jPath = "src/main/resources/log4j.xml"

val scVersion = "2.12.11"

lazy val root = Project(id = appName, base = file("."))
  .settings(
    name := appName,
    scalaVersion := scVersion,
    skip in publish := true,
  )
  .withId(appName)
  .aggregate(
    pipeline,
    dataLoader,
    dataProcessor,
    dataModel,
    dayCounter,
    nightCounter
  )

lazy val pipeline = (project in file("pipeline"))
  .enablePlugins(CloudflowApplicationPlugin)
  .withId(pipelineName)
  .settings(
    scalaVersion := scVersion,
    blueprint := Some(s"blueprint.conf"),
    name := pipelineName,
    runLocalConfigFile := Some((baseDirectory.value / localConfigPath).getAbsolutePath),
    runLocalLog4jConfigFile := Some((baseDirectory.value / localLog4jPath).getAbsolutePath)
  )

lazy val dataLoader = generateModule("data-loader")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(commonSettings)
  .settings(
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.scalatest"  %% "scalatest"      % scalatestVersion % "test"
    )
  ).dependsOn(commons, dataModel)

lazy val dataProcessor = generateModule("data-processor")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(commonSettings)
  .settings(
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.scalatest"  %% "scalatest"      % scalatestVersion % "test"
    )
  ).dependsOn(commons, dataModel)

lazy val dayCounter = generateModule("day-counter")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(commonSettings)
  .settings(
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.scalatest"  %% "scalatest"      % scalatestVersion % "test"
    )
  ).dependsOn(commons, dataModel)

lazy val nightCounter = generateModule("night-counter")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(commonSettings)
  .settings(
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.scalatest"  %% "scalatest"      % scalatestVersion % "test"
    )
  ).dependsOn(commons, dataModel)

lazy val dataModel = generateModule("data-model")
  .enablePlugins(CloudflowLibraryPlugin)
  .settings(
    scalaVersion := scVersion,
    schemaCodeGenerator := SchemaCodeGenerator.Scala,
    schemaPaths := Map(
      SchemaFormat.Avro -> "src/main/avro",
    )
  )

lazy val commons = generateModule("commons")

def generateModule(appName: String): Project = {
  Project(id = appName, base = file(appName))
    .settings(name := appName, scalaVersion := scVersion)
    .withId(appName)
}

lazy val commonSettings = Seq(
  organization := "com.lightbend.cloudflow",
  headerLicense := Some(HeaderLicense.ALv2("(C) 2016-2020", "Lightbend Inc. <https://www.lightbend.com>")),
  scalaVersion := "2.12.11",
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8",
    "-Xlog-reflective-calls",
    "-Xlint",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-deprecation",
    "-feature",
    "-language:_",
    "-unchecked"
  ),
  scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
)

val logbackVersion   = "1.2.3"
val scalatestVersion = "3.0.8"
