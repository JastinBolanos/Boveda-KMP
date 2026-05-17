package com.jastin.boveda.presentation.model

enum class TxUiStatus { COMPLETED, PENDING }

data class TimelineEventUi(val status: String, val time: String, val done: Boolean)

data class TransactionUiModel(
    val id: String,
    val title: String,
    val amount: Double,
    val status: TxUiStatus,
    val date: String,
    val time: String,
    val method: String,
    val recipient: String,
    val reference: String,
    val timeline: List<TimelineEventUi>
)