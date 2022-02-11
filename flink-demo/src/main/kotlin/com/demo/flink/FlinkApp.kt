package com.demo.flink

import com.demo.flink.FraudulentPayments.FraudulentPaymentEvent
import com.demo.flink.Payments.PaymentEvent
import com.demo.flink.model.CustomerPayments
import com.demo.flink.model.FraudulentPaymentConverterFunction
import com.demo.flink.model.FraudulentPaymentsFunction
import com.demo.flink.model.ToCustomerPaymentMapFunction
import com.demo.flink.serdes.FraudulentPaymentEventSerializationSchema
import com.demo.flink.serdes.PaymentEventDeserializationSchema
import com.twitter.chill.protobuf.ProtobufSerializer
import org.apache.flink.api.common.eventtime.WatermarkStrategy.forBoundedOutOfOrderness
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import java.time.Duration
import java.util.Properties

fun main() {
  val props = Properties()
  props["bootstrap.servers"] = "localhost:9092"
  props["group.id"] = "flink-voxxeddays-demo"

  val source = FlinkKafkaConsumer(
    "payment_events",
    PaymentEventDeserializationSchema(),
    props
  )
  source.setStartFromEarliest()
  source.assignTimestampsAndWatermarks(
    forBoundedOutOfOrderness<PaymentEvent>(Duration.ofMinutes(30))
      .withTimestampAssigner { e, _ -> e.createdAt }
  )

  val sinkTopic = "fraudulent_payment_events"
  val sink = FlinkKafkaProducer(
    sinkTopic,
    FraudulentPaymentEventSerializationSchema(sinkTopic),
    props,
    FlinkKafkaProducer.Semantic.EXACTLY_ONCE
  )
  val app = FlinkExemplarApp(source, sink)
  app.execute()
}

class FlinkExemplarApp(
  private val source: SourceFunction<PaymentEvent>,
  private val sink: SinkFunction<FraudulentPaymentEvent>
) {
  fun execute() {
    val env = StreamExecutionEnvironment.getExecutionEnvironment()
    env.config.registerTypeWithKryoSerializer(
      PaymentEvent::class.java,
      ProtobufSerializer::class.java
    )
    env.config.registerTypeWithKryoSerializer(
      FraudulentPaymentEvent::class.java,
      ProtobufSerializer::class.java
    )

    env.addSource(source)
      .uid("payment_events_source")
      .map(ToCustomerPaymentMapFunction())
      .keyBy { it.senderID }
      .window(TumblingEventTimeWindows.of(Time.minutes(30)))
      .allowedLateness(Time.hours(1))
      .aggregate(FraudulentPaymentsFunction(maxCount = 3, maxAmount = 3000))
      .uid("fraudulent_payments")
      .filter(CustomerPayments::fraudulent)
      .map(FraudulentPaymentConverterFunction())
      .uid("fraudulent_payments_events")
      .addSink(sink)
      .uid("fraudulent_events_sink")

    env.execute("Fraud Detection App")
  }
}
