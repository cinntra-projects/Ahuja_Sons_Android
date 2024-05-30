package com.ahuja.sons.newapimodel

data class ResponseTypeTickets(
    val `data`: List<DataTicketType>,
    val message: String,
    val status: Int
)