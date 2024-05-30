package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable


@Serializable
data class ResponseDashBoardCounter(
    val data: DashBoardCounterData,
    val message: String,
    val status: Int
)