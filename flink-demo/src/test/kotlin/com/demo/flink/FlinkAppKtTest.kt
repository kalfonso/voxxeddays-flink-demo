package com.demo.flink

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.extracting
import org.apache.flink.api.java.tuple.Tuple2
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

data class TestEvent(
  val offset: Long,
  val sponsoringBank: String,
  val amount: Long,
  val occurredAt: Long
)

class MyFlinkAppTest {
  @Test
  fun testAggregationMultipleEventTimeWindows() {
    val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    // Setup events spread out in two hourly windows.
    val events: List<TestEvent> = listOf(
      TestEvent(1L, BANK1, 100L, clock.millis()),
      TestEvent(2L, BANK1, 200L, clock.millis() + 1000),
      TestEvent(3L, BANK1, 300L, clock.millis() + 2000),
      TestEvent(
        4L, BANK1, 400L,
        clock.millis() + Duration.ofHours(1).toMillis()
      ),
      TestEvent(
        5L, BANK1, 500L,
        clock.millis() + Duration.ofHours(1).toMillis() + 1000
      ),
      TestEvent(
        6L, BANK1, 600L,
        clock.millis() + Duration.ofHours(1).toMillis() + 2000
      )
    )

    val eventsSource = createEventsSource(events)
    val source = ParallelEventSource.of(*eventsSource)
    val sink = CollectSink.create<TestBankSettlementEvent>()
    val app = FlinkExemplarApp(source, sink)
    app.execute()

    assertThat(sink.values())
      .extracting { it.data }
      .containsExactly(
        TestBankSettlementEvent.Builder()
          .amount(600)
          .bank("BANK1")
          .startTime(clock.millis())
          .endTime(clock.millis() + 2000)
          .build(),
        TestBankSettlementEvent.Builder()
          .amount(1500)
          .bank("BANK2")
          .startTime(clock.millis() + Duration.ofHours(1).toMillis())
          .endTime(clock.millis() + Duration.ofHours(1).toMillis() + 2000)
          .build()
      )
  }

  private fun createEventsSource(
    events: List<TestEvent>
  ): Array<Tuple2<Long, LedgerEvent>> {
    
    }
    return eventsSource.toTypedArray()
  }
}
