package com.ahuja.sons.newapimodel

data class ResponseQualityIssueCategory(
    val `data`: List<DataQualityIssueCategory>,
    val message: String,
    val status: Int
)