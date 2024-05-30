package com.ahuja.sons.newapimodel

data class ResponseOrderOne(
    val data: List<DataOrderOne>,
    val message: String,
    val status: Int
)