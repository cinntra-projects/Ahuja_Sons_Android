package com.ahuja.sons.newapimodel

data class ResponseAllPart(
    val `data`: List<DataAllPartRequest>,
    val message: String,
    val status: Int
)