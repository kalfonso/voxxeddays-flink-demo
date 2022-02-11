package com.demo.flink.model

import com.demo.flink.Payments
import org.apache.flink.api.common.functions.MapFunction

// Simulates enriching the event with location information
class ToCustomerPaymentMapFunction: MapFunction<Payments.PaymentEvent, CustomerPayment> {
  override fun map(event: Payments.PaymentEvent): CustomerPayment {
    return CustomerPayment(
      senderID = event.senderID,
      amount = event.amount,
      createdAt = event.createdAt,
      location = findLocation(event.senderID)
    )
  }

  private fun findLocation(senderID: String): String {
    return "Mars"
  }
}