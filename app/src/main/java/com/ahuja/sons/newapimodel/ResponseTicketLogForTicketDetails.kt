package com.ahuja.sons.newapimodel

data class ResponseTicketLogForTicketDetails(
    val `data`: MutableList<DataTicketLogForTicketDetails>,
    val message: String,
    val status: Int
)