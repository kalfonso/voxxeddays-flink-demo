package com.demo.flink.load

import com.demo.flink.Payments
import org.apache.kafka.common.serialization.Serializer

class PaymentEventSerializer : Serializer<Payments.PaymentEvent> {
  override fun serialize(topic: String, data: Payments.PaymentEvent): ByteArray {
    return data.toByteArray()
  }
}
