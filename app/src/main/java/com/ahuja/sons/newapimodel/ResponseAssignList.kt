package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable


@Serializable
data class ResponseAssignList(
    val data: List<DataAssignedList>,
    val message: String,
    val status: Int
)