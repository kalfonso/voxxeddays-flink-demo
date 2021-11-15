package com.demo.flink.serdes

import com.demo.flink.Exemplar.TransactionEvent
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

class TransactionEventSerializationSchema(
  private val topic: String
): KafkaSerializationSchema<TransactionEvent> {
  override fun serialize(
    event: TransactionEvent, timest: Long?
  ): ProducerRecord<ByteArray, ByteArray> {
    return ProducerRecord(
      topic,
      event.senderToken.toByteArray(Charsets.UTF_8),
      event.toByteArray()
    )
  }
}