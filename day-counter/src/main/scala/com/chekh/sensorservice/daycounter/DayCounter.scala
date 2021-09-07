package com.chekh.sensorservice.daycounter

import cloudflow.flink.{FlinkStreamlet, FlinkStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import com.chekh.sensorservice.datamodel.SensorData
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.slf4j.LoggerFactory

import java.time.ZoneOffset

class DayCounter extends FlinkStreamlet {
  val inlet: AvroInlet[SensorData] = AvroInlet[SensorData]("in")
  def shape: StreamletShape = StreamletShape.withInlets(inlet)

  override def createLogic: FlinkStreamletLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val stream = readStream(inlet)
      stream.map{x=>(x.ts.toString, x.ts.atZone(ZoneOffset.UTC).getHour)}.print()
    }
  }
}
