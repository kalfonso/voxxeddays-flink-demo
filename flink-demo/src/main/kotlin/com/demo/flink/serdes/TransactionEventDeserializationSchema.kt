package com.demo.flink.serdes

import com.demo.flink.Exemplar.TransactionEvent
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord

class TransactionEventDeserializationSchema: KafkaDeserializationSchema<TransactionEvent> {
  override fun deserialize(
    record: ConsumerRecord<ByteArray, ByteArray>
  ): TransactionEvent {
    return TransactionEvent.parseFrom(record.value())
  }

  override fun getProducedType(): TypeInformation<TransactionEvent> {
    return TypeInformation.of(TransactionEvent::class.java)
  }

  override fun isEndOfStream(event: TransactionEvent): Boolean {
    return false
  }
}