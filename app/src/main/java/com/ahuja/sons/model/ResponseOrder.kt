package com.ahuja.sons.model

data class ResponseOrder(
    val `data`: List<Data>,
    val message: String,
    val status: Int
)