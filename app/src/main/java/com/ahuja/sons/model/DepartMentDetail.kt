package com.ahuja.sons.model

data class DepartMentDetail(
    val `data`: List<DepartmentData> = emptyList(),
    val message: String,
    val status: Int
)