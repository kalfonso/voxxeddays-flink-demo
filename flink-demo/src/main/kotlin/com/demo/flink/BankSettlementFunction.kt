package com.demo.flink

import com.demo.flink.TestBankSettlementEvent
import org.apache.flink.api.common.functions.AggregateFunction
import kotlin.math.max
import kotlin.math.min

class BankSettlementFunction :
  AggregateFunction<LedgerEventInfo, TestBankSettlementEvent, TestBankSettlementEvent> {
  override fun createAccumulator(): TestBankSettlementEvent {
    return TestBankSettlementEvent("", 0, -1L, -1L)
  }

  override fun add(
    ledgerEventInfo: LedgerEventInfo,
    bankSettlementEvent: TestBankSettlementEvent
  ): TestBankSettlementEvent {
    if (bankSettlementEvent.bank != "" && bankSettlementEvent.bank != ledgerEventInfo.bank.name) {
      throw RuntimeException(
        "Bank settlements are calculated on the same bank, " +
          "bank settlement for: ${bankSettlementEvent.bank}, got: ${ledgerEventInfo.bank.name}"
      )
    }
    val bank = ledgerEventInfo.bank.name
    val amount = bankSettlementEvent.amount + ledgerEventInfo.amount
    val startTime = if (bankSettlementEvent.startTime == -1L) {
      ledgerEventInfo.occurredAt
    } else {
      min(bankSettlementEvent.startTime, ledgerEventInfo.occurredAt)
    }
    val endTime = if (bankSettlementEvent.endTime == -1L) {
      ledgerEventInfo.occurredAt
    } else {
      max(bankSettlementEvent.endTime, ledgerEventInfo.occurredAt)
    }
    return TestBankSettlementEvent(bank, amount, startTime, endTime)
  }

  override fun getResult(p0: TestBankSettlementEvent): TestBankSettlementEvent {
    return p0
  }

  override fun merge(
    p0: TestBankSettlementEvent,
    p1: TestBankSettlementEvent
  ): TestBankSettlementEvent {
    val amount = p0.amount + p1.amount
    val startTime = min(p0.startTime, p1.startTime)
    val endtime = max(p0.endTime, p1.endTime)
    return TestBankSettlementEvent(p0.bank, amount, startTime, endtime)
  }
}
