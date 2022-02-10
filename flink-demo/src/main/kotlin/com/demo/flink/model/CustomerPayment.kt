package com.demo.flink.model

// Defines an internal view of a payment inside the Flink pipeline.
// This class needs to comply with Flink's serialization rules.
// https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/dev/datastream/fault-tolerance/serialization/types_serialization/
// https://stackoverflow.com/questions/54260401/kotlin-classes-not-identified-as-flink-valid-pojos
data class CustomerPayment @JvmOverloads constructor(
  var id: String,
  var amount: Long,
  var createdAt: Long,
  var location: String
)