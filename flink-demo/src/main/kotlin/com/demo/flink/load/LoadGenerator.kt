package com.demo.flink.load

import org.apache.kafka.tools.ThroughputThrottler
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Properties
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.random.nextLong

class LoadGenerator(private val latch: CountDownLatch) {
  private val numRecords: Long = 100
  private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
  private val amountRange = LongRange(1, 300)

  private val throughput: Int = -1

  private val publisher = createPublisher()

  fun run() {
    val startMs = System.currentTimeMillis()
    val throttler = ThroughputThrottler(throughput.toLong(), startMs)

    logger.info("Starting load testing")
    logger.info("Generating $numRecords records with throughput $throughput msg/s")
    try {
      for (i: Long in 1..numRecords) {
        val event = randomTransactionEvent()
        val sendStartMs = System.currentTimeMillis()
        publisher.publish(event)
        logger.info("Published event for customer: ${event.senderToken}, amount: ${event.amount}")
        if (throttler.shouldThrottle(i, sendStartMs)) {
          logger.info("Throttling message $i to achieve throughput $throughput")
          throttler.throttle()
        }
      }
      logger.info("Published $numRecords events")
    } finally {
      publisher.close()
      latch.countDown()
    }
  }

  private fun randomTransactionEvent(): Payments {
    return Payments.TransactionEvent.newBuilder()
      .setSenderToken("S_${randomString(3)}")
      .setReceiverToken("R_${randomString(3)}")
      .setAmount(Random.nextLong(amountRange))
      .setCreatedAt(
        randomDateBetween(
          Instant.now().minus(1, ChronoUnit.MINUTES),
          Instant.now()
        )
      )
      .build()
  }

  private fun randomString(length: Int): String {
    return (1..length)
      .map { Random.nextInt(0, charPool.size) }
      .map(charPool::get)
      .joinToString("")
  }

  private fun randomDateBetween(startInclusive: Instant, endExclusive: Instant): Long {
    val startSeconds: Long = startInclusive.epochSecond
    val endSeconds: Long = endExclusive.epochSecond
    val random: Long = Random.nextLong(startSeconds, endSeconds)
    return Instant.ofEpochSecond(random).toEpochMilli()
  }

  private fun createPublisher(): TestTransactionEventPublisher {
    val properties = Properties()
    properties["bootstrap.servers"] = "localhost:9092"
    return TestTransactionEventPublisher(properties)
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
