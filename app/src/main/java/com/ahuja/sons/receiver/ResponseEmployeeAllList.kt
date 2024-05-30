package com.ahuja.sons.receiver

data class ResponseEmployeeAllList(
    val `data`: ArrayList<DataEmployeeAllData>,
    val message: String,
    val status: Int
)