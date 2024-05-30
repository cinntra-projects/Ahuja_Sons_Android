package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable


@Serializable
data class ResponseTicket(
    val data: ArrayList<TicketData>,
    val message: String,
    val status: Int
)