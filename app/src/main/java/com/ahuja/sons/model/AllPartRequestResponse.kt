package com.ahuja.sons.model

data class AllPartRequestResponse(
    val `data`: List<AllPartRequestItemData> = emptyList(),
    val message: String,
    val status: Int
)