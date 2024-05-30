package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable


@Serializable
data class DashBoardCounterData(
    val All: String,
    val Assigned: String,
    val In_Progress: String,
    val Pending: String,
    val Resolved: String
)