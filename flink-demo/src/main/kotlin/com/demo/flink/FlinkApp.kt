package com.demo.flink

import com.demo.flink.serdes.TransactionEventDeserializationSchema
import com.demo.flink.serdes.TransactionEventSerializationSchema
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import java.util.Properties

fun main() {
  val props = Properties()
  props["bootstrap.servers"] = "localhost:9092"
  props["group.id"] = "flink-voxxeddays-demo"

  val source = FlinkKafkaConsumer(
    "transaction-events",
    TransactionEventDeserializationSchema(),
    props
  )
  source.setStartFromEarliest()

  val sinkTopic = "fraudulent-transaction-events"
  val sink = FlinkKafkaProducer(
    sinkTopic,
    TransactionEventSerializationSchema(sinkTopic),
    props,
    FlinkKafkaProducer.Semantic.EXACTLY_ONCE
  )
  val app = FlinkExemplarApp(source, sink)
  app.execute()
}

class FlinkExemplarApp(
  private val source: SourceFunction<Payments.TransactionEvent>,
  private val sink: SinkFunction<Payments.TransactionEvent>
) {
  fun execute() {
    val env = StreamExecutionEnvironment.getExecutionEnvironment()

    env.addSource(source)
      .uid("transaction_events_source")
      .addSink(sink)
      .uid("fraudulent_events_sink")

    env.execute("Fraud Detection App")
  }
}
