package com.ahuja.sons.newapimodel

data class ResponseQualityIssueSubCategory(
    val `data`: MutableList<DataQualityIssueSubCategory>,
    val message: String,
    val status: Int
)