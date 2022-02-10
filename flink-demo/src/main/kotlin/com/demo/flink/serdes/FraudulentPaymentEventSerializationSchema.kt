package com.demo.flink.serdes

import com.demo.flink.FraudulentPayments
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

class FraudulentPaymentEventSerializationSchema(
  private val topic: String
) : KafkaSerializationSchema<FraudulentPayments.FraudulentPaymentEvent> {
  override fun serialize(
    event: FraudulentPayments.FraudulentPaymentEvent,
    timest: Long?
  ): ProducerRecord<ByteArray, ByteArray> {
    return ProducerRecord(
      topic,
      event.customerID.toByteArray(Charsets.UTF_8),
      event.toByteArray()
    )
  }
}
