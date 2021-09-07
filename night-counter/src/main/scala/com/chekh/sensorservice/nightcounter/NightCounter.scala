package com.chekh.sensorservice.nightcounter

import cloudflow.flink.{FlinkStreamlet, FlinkStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import com.chekh.sensorservice.datamodel.SensorData
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}

import java.time.ZoneOffset

class NightCounter extends FlinkStreamlet {
  val inlet: AvroInlet[SensorData] = AvroInlet[SensorData]("in")
  def shape = StreamletShape.withInlets(inlet)

  override def createLogic = new FlinkStreamletLogic() {
    override def buildExecutionGraph(): Unit = {
      val env = StreamExecutionEnvironment.getExecutionEnvironment
      val stream = readStream(inlet)
      stream.map{x=>(x.ts.toString, x.ts.atZone(ZoneOffset.UTC).getHour)}.print()
    }
  }
}
