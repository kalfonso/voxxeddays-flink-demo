package com.demo.flink.load

import com.demo.flink.Payments
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

class TestTransactionEventPublisher(props: Properties) {
  private val topic = "test_transaction_events"

  private val publisherRecordSerializer = TransactionEventSerializer()

  private val producer = KafkaProducer(
    props,
    StringSerializer(),
    publisherRecordSerializer
  )

  fun publish(event: Payments.TransactionEvent) {
    val record = ProducerRecord(
      topic,
      event.senderToken,
      event
    )

    producer.send(record)
  }

  fun close() {
    producer.flush()
    producer.close()
  }
}
