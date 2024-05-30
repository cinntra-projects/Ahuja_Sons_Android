package com.ahuja.sons.newapimodel

data class ResponseFollowUp(
    val `data`: List<DataFollowUpList>,
    val message: String,
    val status: Int
)