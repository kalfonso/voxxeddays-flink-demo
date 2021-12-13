package com.demo.flink.load

import com.demo.flink.Payments.TransactionEvent
import org.apache.kafka.common.serialization.Serializer

class TransactionEventSerializer : Serializer<TransactionEvent> {
  override fun serialize(topic: String, data: TransactionEvent): ByteArray {
    return data.toByteArray()
  }
}
