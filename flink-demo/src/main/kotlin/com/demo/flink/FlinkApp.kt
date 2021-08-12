package com.demo.flink

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.nio.charset.Charset
import java.util.Properties

fun main() {

  val props = Properties()
  props["bootstrap.servers"] = "localhost:9092"
  props["group.id"] = "flink-voxxeddays-demo"

  val source = FlinkKafkaConsumer(
    "input-topic",
    SimpleStringSchema(),
    props
  )

  val sink = FlinkKafkaProducer<String>(
    "test-topic",
    { value, timestamp ->
      ProducerRecord(
        "test-topic",
        "myKey".toByteArray(Charset.defaultCharset()),
        value.toByteArray(Charset.defaultCharset())
      )
    },
    props,
    FlinkKafkaProducer.Semantic.EXACTLY_ONCE
  )
  val app = FlinkExemplarApp(source, sink)
  app.execute()
}

class FlinkExemplarApp(
  private val source: SourceFunction<String>,
  private val sink: SinkFunction<String>
) {
  fun execute() {
    val env = StreamExecutionEnvironment.getExecutionEnvironment()

    env.addSource(source)
      .uid("ledger_events_source")
      .addSink(sink)

    env.execute("Funds Settlement App")
  }
}
