package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable

@Serializable
data class SubDepartMentOfEmployeeAtLoginData(
    val Department: String,
    val Name: String,
    val id: String
)