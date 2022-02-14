package com.demo.flink.fraudalerts

import com.demo.flink.FraudulentPayments.FraudulentPaymentEvent
import org.apache.kafka.common.serialization.Deserializer

class FraudulentPaymentEventDeserializer : Deserializer<FraudulentPaymentEvent> {
  override fun deserialize(topic: String, payload: ByteArray): FraudulentPaymentEvent {
    return FraudulentPaymentEvent.parseFrom(payload)
  }
}
