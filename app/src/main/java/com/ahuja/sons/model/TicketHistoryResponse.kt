package com.ahuja.sons.model

data class TicketHistoryResponse(
    val `data`: List<TicketHistoryData>,
    val message: String,
    val status: Int
)