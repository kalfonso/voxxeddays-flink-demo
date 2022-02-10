package com.demo.flink.load

import com.demo.flink.Payments
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

class PaymentEventPublisher(props: Properties) {
  private val topic = "payment_events"

  private val serializer = PaymentEventSerializer()

  private val producer = KafkaProducer(
    props,
    StringSerializer(),
    serializer
  )

  fun publish(event: Payments.PaymentEvent) {
    val record = ProducerRecord(
      topic,
      event.senderID,
      event
    )

    producer.send(record)
  }

  fun close() {
    producer.flush()
    producer.close()
  }
}
