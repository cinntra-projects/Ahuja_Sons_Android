package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable


@Serializable
data class SAPDataNew(
    val CompanyDB: String,
    val Password: String,
    val SessionId: String,
    val UserName: String,
)