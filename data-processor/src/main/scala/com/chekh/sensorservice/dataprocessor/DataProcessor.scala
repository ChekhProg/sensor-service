package com.chekh.sensorservice.dataprocessor

import cloudflow.flink.{FlinkStreamlet, FlinkStreamletLogic}
import cloudflow.streamlets._
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import com.chekh.sensorservice.datamodel.SensorData
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}

import java.time.ZoneOffset

class DataProcessor extends FlinkStreamlet {
  val inlet: AvroInlet[SensorData] = AvroInlet[SensorData]("in")
  val day: AvroOutlet[SensorData] = AvroOutlet[SensorData]("day").withPartitioner(RoundRobinPartitioner)
  val night: AvroOutlet[SensorData] = AvroOutlet[SensorData]("night").withPartitioner(RoundRobinPartitioner)

  override def shape(): StreamletShape = StreamletShape.withInlets(inlet).withOutlets(day, night)

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val env = StreamExecutionEnvironment.getExecutionEnvironment
      val stream = readStream(inlet)
      def isNight(data: SensorData) = {
        val hours = data.ts.atZone(ZoneOffset.UTC).getHour
        hours < 6 || hours >= 22
      }
      val dayStream = stream.filter(!isNight(_))
      val nightStream = stream.filter(isNight(_))
      writeStream(day, dayStream)
      writeStream(night, nightStream)
    }
  }
}
