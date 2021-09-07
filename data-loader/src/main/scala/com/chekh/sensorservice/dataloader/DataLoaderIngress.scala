package com.chekh.sensorservice.dataloader

import java.time.{Instant, ZoneOffset}
import cloudflow.flink.{FlinkStreamlet, FlinkStreamletLogic}
import cloudflow.streamlets.avro._
import cloudflow.streamlets.StreamletShape
import com.chekh.sensorservice.datamodel.SensorData
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

class DataLoaderIngress extends FlinkStreamlet {
  @transient private val out: AvroOutlet[SensorData] = AvroOutlet[SensorData]("out")
  override def shape: StreamletShape = StreamletShape.withOutlets(out)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val env = StreamExecutionEnvironment.getExecutionEnvironment

      val lines: DataStream[String] = env.readTextFile("iot_telemetry_data.csv")
      //lines.print()

      def lineParse(res: Array[String]): SensorData = {
        SensorData(Instant.ofEpochSecond(res(0).toDouble.toLong), res(1), res(2).toDouble, res(3).toDouble, res(4).toBoolean,
          res(5).toDouble, res(6).toBoolean, res(7).toDouble, res(8).toDouble)
      }

      val stream: DataStream[SensorData] = lines
        .map(_.split(",").map(_.trim).map(_.drop(1).dropRight(1)))
        .map(x => lineParse(x))
      stream.map{x=>(x.ts.toString, x.ts.atZone(ZoneOffset.UTC).getHour)}.print()
      writeStream(out, stream)
      env.execute()
    }
  }
}
