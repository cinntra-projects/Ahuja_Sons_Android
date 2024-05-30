package com.ahuja.sons.newapimodel

data class ResponseParticularCustomerOrder(
    val data: List<DataParticularCustomerOrder>,
    val message: String,
    val status: Int
)