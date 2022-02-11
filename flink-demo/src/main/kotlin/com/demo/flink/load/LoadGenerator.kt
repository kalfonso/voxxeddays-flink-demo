package com.demo.flink.load

import com.demo.flink.Payments
import org.apache.kafka.tools.ThroughputThrottler
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Properties
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.random.nextLong

// Publishes random payments to Kafka for testing purposes.
class LoadGenerator(private val latch: CountDownLatch) {
  private val numPayments: Long = 100000
  private val numCustomers: Long = 100
  private val amountRange = LongRange(1, 4000)

  private val throughput: Int = -1

  private val publisher = createPublisher()

  fun run() {
    val startMs = System.currentTimeMillis()
    val throttler = ThroughputThrottler(throughput.toLong(), startMs)

    logger.info("Starting load testing")
    logger.info("Generating $numPayments records with throughput $throughput msg/s")
    try {
      for (i: Long in 1..numPayments) {
        val event = randomPaymentEvent()
        val sendStartMs = System.currentTimeMillis()
        publisher.publish(event)
        logger.info("Published event for customer: ${event.senderID}, amount: ${event.amount}")
        if (throttler.shouldThrottle(i, sendStartMs)) {
          logger.info("Throttling message $i to achieve throughput $throughput")
          throttler.throttle()
        }
      }
      logger.info("Published $numPayments events")
    } finally {
      publisher.close()
      latch.countDown()
    }
  }

  private fun randomPaymentEvent(): Payments.PaymentEvent {
    return Payments.PaymentEvent.newBuilder()
      .setSenderID("C_${Random.nextLong(0, numCustomers)}")
      .setReceiverID("C_${Random.nextLong(0, numCustomers)}")
      .setAmount(Random.nextLong(amountRange))
      .setCreatedAt(randomDateBetween(
        Instant.now().minus(1, ChronoUnit.HOURS),
        Instant.now()
      ))
      .build()
  }

  private fun randomDateBetween(startInclusive: Instant, endExclusive: Instant): Long {
    val startMillis: Long = startInclusive.toEpochMilli()
    val endMillis: Long = endExclusive.toEpochMilli()
    return Random.nextLong(startMillis, endMillis)
  }

  private fun createPublisher(): PaymentEventPublisher {
    val properties = Properties()
    properties["bootstrap.servers"] = "localhost:9092"
    properties["batch.size"] = "0"
    properties["request.required.acks"] = "1"
    return PaymentEventPublisher(properties)
  }

  fun close() {
    publisher.close()
  }
}

private val logger = LoggerFactory.getLogger(LoadGenerator::class.java)

fun main(args: Array<String>) {
  val latch = CountDownLatch(1)
  val loadGenerator = LoadGenerator(latch)

  Runtime.getRuntime().addShutdownHook(
    Thread {
      loadGenerator.close()
      latch.countDown()
    }
  )

  thread(start = true) {
    logger.info("Start running")
    loadGenerator.run()
  }
  latch.await()
}
