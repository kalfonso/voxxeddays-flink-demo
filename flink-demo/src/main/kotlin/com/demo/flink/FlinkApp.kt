package com.demo.flink

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import java.time.Duration

fun main() {
  val source = KafkaSource.create<LedgerEvent>("ledger_events")
  source.setStartFromEarliest()
  source.assignTimestampsAndWatermarks(
    WatermarkStrategy
      .forBoundedOutOfOrderness<ReceiverRecord<LedgerEvent>>(Duration.ofHours(2))
      .withTimestampAssigner { x, _ -> x.data.occurred_at }
  )
  val sink = KafkaSink.create<TestBankSettlementEvent>("bank_settlement_events")
  val app = FlinkExemplarApp(source, sink)
  app.execute()
}

class FlinkExemplarApp(
  private val source: SourceFunction<ReceiverRecord<LedgerEvent>>,
  private val sink: SinkFunction<PublisherRecord<TestBankSettlementEvent>>
) {
  fun execute() {
    val env = StreamExecutionEnvironment.getExecutionEnvironment()

    env.addSource(source)
      .uid("ledger_events_source")
      .addSink(sink)

    env.execute("Funds Settlement App")
  }
}
