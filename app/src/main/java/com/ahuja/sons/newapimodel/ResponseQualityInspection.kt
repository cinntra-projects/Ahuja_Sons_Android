package com.ahuja.sons.newapimodel

data class ResponseQualityInspection(
    val `data`: MutableList<DataQualityInspectionList>,
    val message: String,
    val status: Int
)