package com.demo.flink.serdes

import com.demo.flink.Payments
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

class TransactionEventSerializationSchema(
  private val topic: String
) : KafkaSerializationSchema<Payments.TransactionEvent> {
  override fun serialize(
    event: Payments.TransactionEvent,
    timest: Long?
  ): ProducerRecord<ByteArray, ByteArray> {
    return ProducerRecord(
      topic,
      event.senderToken.toByteArray(Charsets.UTF_8),
      event.toByteArray()
    )
  }
}
