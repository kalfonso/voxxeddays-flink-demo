package com.demo.flink.serdes

import com.demo.flink.Payments
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord

// DeserializationSchema for Payments.PaymentEvent protobuf.
// The first operator in the Flink pipeline after the source must be able to deserialize this event.
class PaymentEventDeserializationSchema :
  KafkaDeserializationSchema<Payments.PaymentEvent> {
  override fun deserialize(
    record: ConsumerRecord<ByteArray, ByteArray>
  ): Payments.PaymentEvent {
    return Payments.PaymentEvent.parseFrom(record.value())
  }

  override fun getProducedType(): TypeInformation<Payments.PaymentEvent> {
    return TypeInformation.of(Payments.PaymentEvent::class.java)
  }

  override fun isEndOfStream(event: Payments.PaymentEvent): Boolean {
    return false
  }
}
