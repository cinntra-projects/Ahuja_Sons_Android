package com.ahuja.sons.model

data class OrderDataResponse(
    val `data`: List<OrderDataModel>,
    val message: String,
    val status: Int
)