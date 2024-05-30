package com.ahuja.sons.model

data class CreatePartRequestResponse(
    val `data`: List<PRIDResponse>,
    val message: String,
    val status: Int
)