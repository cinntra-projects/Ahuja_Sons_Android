package com.ahuja.sons.model

data class AccountBPResponse(
    val `data`: ArrayList<AccountBpData>,
    val message: String,
    val status: Int
)