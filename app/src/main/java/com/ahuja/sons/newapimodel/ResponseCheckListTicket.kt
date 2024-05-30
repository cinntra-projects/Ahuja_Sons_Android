package com.ahuja.sons.newapimodel

data class ResponseCheckListTicket(
    val `data`: List<DataCheckList>,
    val message: String,
    val status: Int
)