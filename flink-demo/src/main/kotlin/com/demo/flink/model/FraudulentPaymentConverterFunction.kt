package com.demo.flink.model

import com.demo.flink.FraudulentPayments.FraudulentPaymentEvent
import org.apache.flink.api.common.functions.MapFunction

class FraudulentPaymentConverterFunction :
  MapFunction<CustomerPayments, FraudulentPaymentEvent> {
  override fun map(payments: CustomerPayments): FraudulentPaymentEvent {
    return FraudulentPaymentEvent.newBuilder()
      .setCustomerID(payments.id)
      .setAmount(payments.amount)
      .setCount(payments.count)
      .setLocation(payments.location)
      .setStartTime(payments.startTime)
      .setEndTime(payments.endTime)
      .build()
  }
}