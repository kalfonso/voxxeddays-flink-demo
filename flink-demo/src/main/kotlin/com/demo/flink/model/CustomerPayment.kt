package com.demo.flink.model

// Defines an internal view of a payment inside the Flink pipeline.
// This class needs to comply with Flink's serialization rules.
data class CustomerPayment(
  val senderID: String,
  val amount: Long,
  val createdAt: Long,
  val location: String
)
