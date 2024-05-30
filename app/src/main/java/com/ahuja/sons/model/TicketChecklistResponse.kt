package com.ahuja.sons.model

data class TicketChecklistResponse(
    val `data`: List<TicketChecklistData>,
    val message: String,
    val status: Int
)