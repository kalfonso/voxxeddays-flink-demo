package com.demo.flink

data class LedgerEventInfo(val bank: String, val amount: Long, val occurredAt: Long)
