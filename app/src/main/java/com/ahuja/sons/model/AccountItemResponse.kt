package com.ahuja.sons.model

data class AccountItemResponse(
    val `data`: List<DocumentLine>,
    val message: String,
    val status: Int
)