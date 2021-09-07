import sbt._

object Dependencies {

  object Version {
    val Avro = "1.9.2"
    val Test = "3.2.0"
    val AlpakkaFile = "2.0.2"
    val AlpakkaElasticsearch = "2.0.2"
    val Flink = "1.13.0"
  }

  object Avro {
    val avro = "org.apache.avro" % "avro" % Version.Avro
  }

  object Testing {
    val scalaTest = "org.scalatest" %% "scalatest" % Version.Test
  }

  object Flink {
    //val s = "org.apache.flink"%"flink-azure-fs-hadoop"%cloudflow.sbt.CloudflowFlinkPlugin.FlinkVersion
    val s = "org.apache.flink"%"flink-azure-fs-hadoop"%cloudflow.sbt.CloudflowFlinkPlugin.label
  }

}
