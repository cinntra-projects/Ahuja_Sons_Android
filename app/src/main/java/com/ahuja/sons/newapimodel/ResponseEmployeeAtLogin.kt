package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable


@Serializable
data class ResponseEmployeeAtLogin(
    val SAP: SAPDataNew,
    val data: EmployeeAtLoginData,
    val message: String,
    val status: Int
)