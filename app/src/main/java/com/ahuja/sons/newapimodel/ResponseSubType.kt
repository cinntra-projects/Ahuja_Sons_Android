package com.ahuja.sons.newapimodel

data class ResponseSubType(
    val `data`: List<DataSubType>,
    val message: String,
    val status: Int
)