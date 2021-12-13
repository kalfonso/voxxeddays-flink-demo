package com.demo.flink.serdes

import com.demo.flink.Payments
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord

class TransactionEventDeserializationSchema :
  KafkaDeserializationSchema<Payments.TransactionEvent> {
  override fun deserialize(
    record: ConsumerRecord<ByteArray, ByteArray>
  ): Payments.TransactionEvent {
    return Payments.TransactionEvent.parseFrom(record.value())
  }

  override fun getProducedType(): TypeInformation<Payments.TransactionEvent> {
    return TypeInformation.of(Payments.TransactionEvent::class.java)
  }

  override fun isEndOfStream(event: Payments.TransactionEvent): Boolean {
    return false
  }
}
