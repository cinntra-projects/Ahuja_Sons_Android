package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable

@Serializable
data class DepartMentOfEmployeeAtLoginData(
    val Name: String,
    val id: String
)