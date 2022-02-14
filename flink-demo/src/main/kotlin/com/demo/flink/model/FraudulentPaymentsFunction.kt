package com.demo.flink.model

import org.apache.flink.api.common.functions.AggregateFunction
import kotlin.math.max
import kotlin.math.min

class FraudulentPaymentsFunction(val maxCount: Long, val maxAmount: Long) :
  AggregateFunction<CustomerPayment, CustomerPayments, CustomerPayments> {
  override fun createAccumulator(): CustomerPayments {
    return CustomerPayments("", 0, 0, "", 0, 0, false)
  }

  override fun add(payment: CustomerPayment, accumulator: CustomerPayments): CustomerPayments {
    val updatedAmount = accumulator.amount + payment.amount
    val updatedCount = accumulator.count + 1
    val fraudulent = (updatedCount > maxCount) || (updatedAmount > maxAmount)
    val startTime = if (accumulator.startTime == 0L) {
      payment.createdAt
    } else {
      min(payment.createdAt, accumulator.startTime)
    }
    val endTime = if (accumulator.endTime == 0L) {
      payment.createdAt
    } else {
      max(payment.createdAt, accumulator.endTime)
    }
    return CustomerPayments(
      id = payment.senderID,
      amount = updatedAmount,
      count = updatedCount,
      location = payment.location,
      startTime = startTime,
      endTime = endTime,
      fraudulent = fraudulent
    )
  }

  override fun getResult(accumulator: CustomerPayments): CustomerPayments {
    return accumulator
  }

  override fun merge(
    payments1: CustomerPayments,
    payments2: CustomerPayments
  ): CustomerPayments {
    val id = payments1.id
    val updatedAmount = payments1.amount + payments2.amount
    val updatedCount = payments1.count + payments2.count
    val fraudulent = (updatedCount > maxCount) || (updatedAmount > maxAmount)
    val startTime = min(payments1.startTime, payments2.startTime)
    val endTime = max(payments1.endTime, payments2.endTime)
    return CustomerPayments(
      id = id,
      amount = updatedAmount,
      count = updatedCount,
      location = payments1.location,
      startTime = startTime,
      endTime = endTime,
      fraudulent = fraudulent
    )
  }
}
